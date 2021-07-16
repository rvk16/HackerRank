package com.amdocs.aia.il.common.stores.scylla;

import com.amdocs.aia.il.common.stores.QueryGenerator;
import com.amdocs.aia.il.common.stores.QueryProvider;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

import java.util.HashMap;
import java.util.Map;

public class ScyllaQueryGeneratorHolder {

    private static ScyllaQueryGeneratorHolder instance;

    private final Map<String, QueryGenerator> schemaToQueryGeneratorMap;

    // Retry Table statements
    private final PreparedStatement retryInsertStatement;
    private final PreparedStatement retryDeleteStatementSingle;
    private final PreparedStatement retryDeleteStatement;
    private final PreparedStatement retrySelectStatement;
    private final PreparedStatement retrySelectAllStatement;
    private final PreparedStatement retrySelectAllStatementFiltered;
    private final PreparedStatement partialTxnSelectStatement;
    private final PreparedStatement partialTxnInsertStatement;
    private final PreparedStatement partialTxnDeleteStatement;

    // Reference table statements
    private final PreparedStatement refSelectAll;

    // Referential Integrity Table Statements
    private final PreparedStatement riExistsStatement;
    private final PreparedStatement riPersistStatement;

    private ScyllaQueryGeneratorHolder(QueryProvider queryProvider) {
        this.schemaToQueryGeneratorMap = new HashMap<>(0);
        final CqlSession session = ScyllaConnection.getSession();
        // Retry Related Initialization
        this.retryInsertStatement = session.prepare(queryProvider.getRetryInsertQuery());
        this.retryDeleteStatementSingle = session.prepare(queryProvider.getRetryDeleteSingleQuery());
        this.retryDeleteStatement = session.prepare(queryProvider.getRetryDeleteQuery());
        this.retrySelectStatement = session.prepare(queryProvider.getRetrySelectQuery());
        this.retrySelectAllStatement = session.prepare(queryProvider.getRetrySelectAllQuery());
        this.retrySelectAllStatementFiltered = session.prepare(queryProvider.getRetrySelectAllQuery());
        this.refSelectAll = session.prepare(queryProvider.getRefTableSelectQuery());
        // RI related initialization
        this.riExistsStatement = session.prepare(queryProvider.getRIExistsQuery());
        this.riPersistStatement = session.prepare(queryProvider.getRIPersistsQuery());
        this.partialTxnSelectStatement = session.prepare(queryProvider.getPartialTrxSelectQuery());
        this.partialTxnInsertStatement = session.prepare(queryProvider.getPartialTrxInsertQuery());
        this.partialTxnDeleteStatement = session.prepare(queryProvider.getPartialTrxDeleteQuery());
    }

    public static ScyllaQueryGeneratorHolder getInstance() {
        return instance;
    }

    public static synchronized void initInstance(QueryProvider queryProvider) {
        if (instance == null) {
            instance = new ScyllaQueryGeneratorHolder(queryProvider);
        }
    }

    /**
     * Use this method only when you want to recreate QueryGeneratorHolder for Spark executors.
     * This is to avoid using Prepared Statement which was created in driver.
     *
     * @param queryProvider
     */
    public static void recreateInstanceForSparkExecutor(QueryProvider queryProvider) {
        instance = new ScyllaQueryGeneratorHolder(queryProvider);
    }

    public void populateQueryGenerators(String schemaStoreKey, QueryGenerator qGen) {
        this.schemaToQueryGeneratorMap.computeIfAbsent(schemaStoreKey, t -> qGen);
    }

    public QueryGenerator getQueryGenerator(String schemaName) {
        return schemaToQueryGeneratorMap.get(schemaName);
    }

    public int size() {
        return this.schemaToQueryGeneratorMap.size();
    }

    public PreparedStatement getRetryInsertStatement() {
        return retryInsertStatement;
    }

    public PreparedStatement getRetryDeleteStatementSingle() {
        return retryDeleteStatementSingle;
    }

    public PreparedStatement getRetryDeleteStatement() {
        return retryDeleteStatement;
    }

    public PreparedStatement getRetrySelectStatement() {
        return retrySelectStatement;
    }

    public PreparedStatement getRetrySelectAllStatement() {
        return retrySelectAllStatement;
    }

    public PreparedStatement getRetrySelectAllStatementFiltered() {
        return retrySelectAllStatementFiltered;
    }

    public PreparedStatement getRefSelectAllStatement() {
        return refSelectAll;
    }

    public PreparedStatement getRiExistsStatement() {
        return riExistsStatement;
    }

    public PreparedStatement getRiPersistStatement() {
        return riPersistStatement;
    }

    public PreparedStatement getPartialTxnSelectStatement() {
        return partialTxnSelectStatement;
    }

    public PreparedStatement getPartialTxnInsertStatement() {
        return partialTxnInsertStatement;
    }

    public PreparedStatement getPartialTxnDeleteStatement() {
        return partialTxnDeleteStatement;
    }
}