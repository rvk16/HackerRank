package com.amdocs.aia.il.common.targetEntity;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.stores.KeyColumn;
import com.amdocs.aia.il.common.stores.scylla.ScyllaConnection;
import com.amdocs.aia.il.common.stores.scylla.ScyllaQueryGeneratorHolder;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class ReferentialIntegrityTargetEntityTable implements Serializable {
    private static final long serialVersionUID = -5038186211795145156L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferentialIntegrityTargetEntityTable.class);

    private static final String DUMMY_QUALIFIER_VALUE = "D";
    private static final String REAL_QUALIFIER_VALUE = "R";
    private static final String KEY_SEPARATOR = ":::";

    private final TargetEntity targetEntity;

    public ReferentialIntegrityTargetEntityTable(TargetEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    /**
     * Check if key is created in table
     *
     * @param key
     * @return true if key exists in table, else false
     */
    public boolean isKeyExists(KeyColumn key) {
        ScyllaQueryGeneratorHolder holder = ScyllaQueryGeneratorHolder.getInstance();
        CqlSession session = ScyllaConnection.getSession();
        try {
            BoundStatement riExistsStatement = holder.getRiExistsStatement().bind(key.toString(), targetEntity.getName(), DUMMY_QUALIFIER_VALUE);
            ResultSet resultSet = session.execute(riExistsStatement);
            return resultSet.one() != null;
        } catch (Exception ex) {
            LOGGER.error("Error while checking is key exists in table", ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Persist messages to table.
     *
     * @param messages - messages to persist
     */
    public void persistMessages(final List<RepeatedMessage> messages) {
        final ScyllaQueryGeneratorHolder holder = ScyllaQueryGeneratorHolder.getInstance();
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        try {
            messages.forEach(message -> {
                final String key = buildKey(targetEntity.getTargetTable().getPrimaryKeys().stream().map(pk -> message.getValue(pk).toString()).collect(Collectors.toList()));
                final String toReal = Boolean.parseBoolean(message.getValue("dummyMessage").toString()) ? DUMMY_QUALIFIER_VALUE : REAL_QUALIFIER_VALUE;
                final CompletionStage<AsyncResultSet> asyncResult = completionStage.thenCompose(s -> s.executeAsync(holder.getRiPersistStatement().bind(key, targetEntity.getName(), toReal)));
                resultSets.add(asyncResult);
            });
            for (final CompletionStage<AsyncResultSet> resultSet : resultSets) {
                resultSet.toCompletableFuture().get();
            }
        } catch (final Exception e) {
            LOGGER.error("Error while persisting to ri table", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static String buildKey(List<String> keValues) {
        return StringUtils.join(keValues, KEY_SEPARATOR);
    }
}