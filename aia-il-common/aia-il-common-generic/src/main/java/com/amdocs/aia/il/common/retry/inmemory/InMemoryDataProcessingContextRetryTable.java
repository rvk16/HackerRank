package com.amdocs.aia.il.common.retry.inmemory;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.model.configuration.properties.RealtimeTransformerConfiguration;
import com.amdocs.aia.il.common.retry.AbstractDataProcessingContextRetryTable;
import com.amdocs.aia.il.common.retry.DataProcessingContextRetryInfo;
import com.amdocs.aia.il.common.stores.AbstractQueryProvider;
import com.amdocs.aia.il.common.stores.inmemory.InMemoryDBConnection;
import com.amdocs.aia.il.common.utils.DbUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class InMemoryDataProcessingContextRetryTable extends AbstractDataProcessingContextRetryTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDataProcessingContextRetryTable.class);

    public InMemoryDataProcessingContextRetryTable(String name, RealtimeTransformerConfiguration config) {
        super(name, config);
    }

    @Override
    public void persistNew(String leadingKey, RepeatedMessage message) {
        PreparedStatement psQuery = null;
        try {
            psQuery = InMemoryDBConnection.getConnection().prepareStatement(AbstractQueryProvider.RETRY_INSERT_QUERY);
            setParameterValueForPersistNew(psQuery, leadingKey, this.name, getNextTTL(null), getNextRetryScheduleTime(null), interval, DbUtils.serialize(message));
            psQuery.executeQuery();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            if (psQuery != null) {
                try {
                    psQuery.close();
                } catch (SQLException ex) {
                    LOGGER.error("Error while closing the statement in new persist", ex);
                }
            }
        }
    }

    @Override
    protected void persistUpdate(String leadingKey, DataProcessingContextRetryInfo lastRetryInfoFromTable) {
        PreparedStatement psQuery = null;
        try {
            // Delete existing first
            psQuery = InMemoryDBConnection.getConnection().prepareStatement(AbstractQueryProvider.RETRY_DELETE_SINGLE_QUERY);
            setParameterValueForDeleteSingle(psQuery, lastRetryInfoFromTable.getContextName(), lastRetryInfoFromTable.getLeadingKey(), lastRetryInfoFromTable.getTtl(), lastRetryInfoFromTable.getNextRetryTime());
            psQuery.executeQuery();
            // Insert new record
            psQuery = InMemoryDBConnection.getConnection().prepareStatement(AbstractQueryProvider.RETRY_INSERT_QUERY);
            setParameterValueForPersistNew(psQuery, leadingKey, this.name, getNextTTL(lastRetryInfoFromTable.getTtl()), getNextRetryScheduleTime(lastRetryInfoFromTable.getNextRetryTime()),
                    interval, lastRetryInfoFromTable.getLeadingTableData());
            psQuery.executeQuery();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            if (psQuery != null) {
                try {
                    psQuery.close();
                } catch (SQLException ex) {
                    LOGGER.error("Error while closing the statement in update persist", ex);
                }
            }
        }
    }

    @Override
    public void delete(List<String> leadingKeys) {
        PreparedStatement psQuery = null;
        try {
            psQuery = InMemoryDBConnection.getConnection().prepareStatement(AbstractQueryProvider.RETRY_DELETE_QUERY);
            psQuery.setString(1, name);
            psQuery.setString(2, "(" + StringUtils.join(Collections.singletonList(leadingKeys), ",") + ")");
            psQuery.executeQuery();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            if (psQuery != null) {
                try {
                    psQuery.close();
                } catch (SQLException ex) {
                    LOGGER.error("Error while closing the statement in delete", ex);
                }
            }
        }
    }

    @Override
    public void persist(String leadingKey, RepeatedMessage message) {
        LOGGER.debug("Persisting the keys to retry table");
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
    public DataProcessingContextRetryInfo load(String leadingKey) {
        PreparedStatement psQuery = null;
        ResultSet result = null;
        try {
            psQuery = InMemoryDBConnection.getConnection().prepareStatement(AbstractQueryProvider.RETRY_SELECT_QUERY);
            psQuery.setString(1, leadingKey);
            psQuery.setString(2, name);
            result = psQuery.executeQuery();
            result.next();
            return extractQueryResult(result);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            if (psQuery != null) {
                try {
                    psQuery.close();
                } catch (SQLException ex) {
                    LOGGER.error("Error while closing the statement in load", ex);
                }
            }
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    LOGGER.error("Error while closing the resultset in load", ex);
                }
            }
        }
    }

    @Override
    public Collection<DataProcessingContextRetryInfo> loadAll(boolean filterMessagesThatShouldBeProcessed) {
        PreparedStatement psQuery = null;
        ResultSet result = null;
        try {
            Collection<DataProcessingContextRetryInfo> dataProcessingContextRetryInfos = new ArrayList<>();


            if (filterMessagesThatShouldBeProcessed) {
                //add TTL > 0 && time > current time filters
                psQuery = InMemoryDBConnection.getConnection().prepareStatement(AbstractQueryProvider.RETRY_SELECT_ALL_QUERY_WITH_FILTER);
                psQuery.setInt(1, 0);
                psQuery.setLong(2, System.currentTimeMillis());
                result = psQuery.executeQuery();
            } else {
                psQuery = InMemoryDBConnection.getConnection().prepareStatement(AbstractQueryProvider.RETRY_SELECT_ALL_QUERY);
                result = psQuery.executeQuery();
            }

            //execute query on entire table
            while (result.next()) {
                dataProcessingContextRetryInfos.add(extractQueryResult(result));
            }
            return dataProcessingContextRetryInfos;

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            if (psQuery != null) {
                try {
                    psQuery.close();
                } catch (SQLException ex) {
                    LOGGER.error("Error while closing the statement in load all", ex);
                }
            }
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    LOGGER.error("Error while closing the resultset in persist", ex);
                }
            }
        }
    }

    public static void setParameterValueForPersistNew(PreparedStatement ps, String leadingKey, String name, int nextTtl, long nextRetryScheduleTime, int interval, byte[] message) {
        try {
            ps.setString(1, leadingKey);
            ps.setString(2, name);
            ps.setInt(3, nextTtl);
            ps.setLong(4, nextRetryScheduleTime);
            ps.setInt(5, interval);
            ps.setBytes(6, message);
        } catch (SQLException e) {
            LOGGER.error("SQL error", e);
        }
    }

    private static void setParameterValueForDeleteSingle(PreparedStatement ps, String contextName, String leadingKey, int ttl, long nextRetryTime) {
        try {
            ps.setString(1, contextName);
            ps.setString(2, leadingKey);
            ps.setInt(3, ttl);
            ps.setLong(4, nextRetryTime);
        } catch (SQLException e) {
            LOGGER.error("SQL error", e);
        }
    }

    /**
     * Create {@link DataProcessingContextRetryInfo} from HBase result
     *
     * @param result
     * @return {@link DataProcessingContextRetryInfo}
     */
    private static DataProcessingContextRetryInfo extractQueryResult(ResultSet result) {
        if (result == null) {
            return null;
        }
        try {
            return new DataProcessingContextRetryInfo(result.getString(1), result.getString(2), result.getInt(3), result.getLong(4), result.getInt(5), result.getBytes(6));
        } catch (SQLException e) {
            LOGGER.error("SQL error", e);
        }
        return null;
    }
}