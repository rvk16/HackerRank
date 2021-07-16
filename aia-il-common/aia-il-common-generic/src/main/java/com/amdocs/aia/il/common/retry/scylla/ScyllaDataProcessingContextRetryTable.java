package com.amdocs.aia.il.common.retry.scylla;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.model.configuration.properties.RealtimeTransformerConfiguration;
import com.amdocs.aia.il.common.retry.AbstractDataProcessingContextRetryTable;
import com.amdocs.aia.il.common.retry.DataProcessingContextRetryInfo;
import com.amdocs.aia.il.common.stores.scylla.ScyllaConnection;
import com.amdocs.aia.il.common.stores.scylla.ScyllaQueryGeneratorHolder;
import com.amdocs.aia.il.common.utils.DbUtils;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class ScyllaDataProcessingContextRetryTable extends AbstractDataProcessingContextRetryTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScyllaDataProcessingContextRetryTable.class);

    public ScyllaDataProcessingContextRetryTable(String name, RealtimeTransformerConfiguration config) {
        super(name, config);
    }

    @Override
    public void persistNew(String leadingKey, RepeatedMessage message) {
        try {
            CqlSession session = ScyllaConnection.getSession();
            ScyllaQueryGeneratorHolder holder = ScyllaQueryGeneratorHolder.getInstance();
            session.execute(holder.getRetryInsertStatement().bind(leadingKey, this.name, getNextTTL(null), getNextRetryScheduleTime(null), interval + "", ByteBuffer.wrap(DbUtils.serialize(message))));
        } catch (Exception ex) {
            LOGGER.error("Error while new persisting to retry table", ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    protected void persistUpdate(String leadingKey, DataProcessingContextRetryInfo lastRetryInfoFromTable) {
        try {
            CqlSession session = ScyllaConnection.getSession();
            ScyllaQueryGeneratorHolder holder = ScyllaQueryGeneratorHolder.getInstance();
            session.execute(holder.getRetryDeleteStatementSingle().bind(lastRetryInfoFromTable.getContextName(), lastRetryInfoFromTable.getLeadingKey(),
                    lastRetryInfoFromTable.getTtl(), lastRetryInfoFromTable.getNextRetryTime()));
            session.execute(holder.getRetryInsertStatement().bind(leadingKey, this.name, getNextTTL(lastRetryInfoFromTable.getTtl()),
                    getNextRetryScheduleTime(lastRetryInfoFromTable.getNextRetryTime()), interval, lastRetryInfoFromTable.getLeadingTableData()));
        } catch (Exception ex) {
            LOGGER.error("Error while update persisting to retry table", ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public void delete(List<String> leadingKeys) {
        try {
            CqlSession session = ScyllaConnection.getSession();
            ScyllaQueryGeneratorHolder holder = ScyllaQueryGeneratorHolder.getInstance();
            session.execute(holder.getRetryDeleteStatement().bind(name, leadingKeys));
        } catch (Exception ex) {
            LOGGER.error("Error while deleting from retry table", ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public void persist(String leadingKey, RepeatedMessage message) {
        LOGGER.info("Persisting the keys to retry table");
        //load the last status of the leading key from table
        DataProcessingContextRetryInfo lastRetryInfo = this.load(leadingKey);
        if (lastRetryInfo == null) {
            //key was not found in table, persist as new
            this.persistNew(leadingKey, message);
        } else {
            if (getNextTTL(lastRetryInfo.getTtl()) == 0) {
                //TODO report error when last retry fail using metrix
            }
            //key was found in table, update TTL & schedule time
            this.persistUpdate(leadingKey, lastRetryInfo);
        }
    }

    @Override
    public Collection<DataProcessingContextRetryInfo> loadAll(boolean filterMessagesThatShouldBeProcessed) {
        final Collection<DataProcessingContextRetryInfo> dataProcessingContextRetryInfos = new ArrayList<>();
        final ScyllaQueryGeneratorHolder holder = ScyllaQueryGeneratorHolder.getInstance();
        final CqlSession session = ScyllaConnection.getSession();
        try {
            final CompletionStage<AsyncResultSet> completionStage;
            if (filterMessagesThatShouldBeProcessed) {
                //add TTL > 0 && time > current time filters
                completionStage = session.executeAsync(holder.getRetrySelectAllStatementFiltered().bind(0, System.currentTimeMillis()));
            } else {
                completionStage = session.executeAsync(holder.getRetrySelectAllStatement().bind());
            }

            // execute query on entire table
            processRows(completionStage.toCompletableFuture().join(), dataProcessingContextRetryInfos);
            return dataProcessingContextRetryInfos;
        } catch (Exception e) {
            LOGGER.error("Error while loading all records from retry table", e);
            throw new RuntimeException(e);
        }
    }

    private static void processRows(final AsyncResultSet rs, final Collection<DataProcessingContextRetryInfo> dataProcessingContextRetryInfos) {
        for (final Row row : rs.currentPage()) {
            dataProcessingContextRetryInfos.add(extractQueryResult(row));
        }
        if (rs.hasMorePages()) {
            processRows(rs.fetchNextPage().toCompletableFuture().join(), dataProcessingContextRetryInfos);
        }
    }

    @Override
    public DataProcessingContextRetryInfo load(String leadingKey) {
        try {
            CqlSession session = ScyllaConnection.getSession();
            ScyllaQueryGeneratorHolder holder = ScyllaQueryGeneratorHolder.getInstance();
            ResultSet result = session.execute(holder.getRetrySelectStatement().bind(leadingKey, name));
            return extractQueryResult(result.one());
        } catch (Exception ex) {
            LOGGER.error("Error while loading from retry table", ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Create {@link DataProcessingContextRetryInfo}
     *
     * @param row
     * @return {@link DataProcessingContextRetryInfo}
     */
    private static DataProcessingContextRetryInfo extractQueryResult(Row row) {
        if (row == null) {
            return null;
        }
        return new DataProcessingContextRetryInfo(row.getString(0), row.getString(1), row.getInt(2), row.getLong(3), row.getInt(4), toByteArray(row.getByteBuffer(5)));
    }

    private static byte[] toByteArray(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes, 0, bytes.length);
        return bytes;
    }
}