package com.amdocs.aia.il.common.stores;

import java.io.Serializable;

public abstract class QueryGenerator implements Serializable {

    private static final String REF_SCHEMA = "ref";
    private static final String SEPARATOR = "_";
    public static final String RELATION_QUALIFIER = "RELATION";

    private final String schemaName;
    private final boolean isRefSchema;
    private final QueryProvider queryProvider;

    public QueryGenerator(String schemaName, QueryProvider queryProvider,boolean isReference) {
        this.schemaName = schemaName;
        
        this.queryProvider = queryProvider;
        this.isRefSchema=isReference;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public boolean isRefSchema() {
        return isRefSchema;
    }

    protected abstract String prepareStatement(String schemaName, String head, String tail);

    public String getPsForSelectQueryString() {
        return isRefSchema() ?
                null :
                prepareStatement(relationalSchemaName(), queryProvider.getSelectQuery(), queryProvider.getSelectQueryPredicate());
    }

    public String getPsForSelectQueryWithBulkProcessedFlagString() {
        return isRefSchema() ?
                null :
                prepareStatement(relationalSchemaName(), queryProvider.getSelectWithBulkProcessedFlagQuery(), queryProvider.getSelectQueryPredicate());
    }

    public String getPsForInsertQueryString() {
        return isRefSchema ? prepareStatement(REF_SCHEMA, queryProvider.getMainDataInsertQuery(), queryProvider.getMainDataInsertQueryPredicate()):
                prepareStatement(schemaName, queryProvider.getMainDataInsertQuery(), queryProvider.getMainDataInsertQueryPredicate());
    }

    public String getPsForInsertRelationQueryString() {
        return prepareStatement(relationalSchemaName(), queryProvider.getRelationalDataInsertQuery(), queryProvider.getRelationalDataInsertQueryPredicate());
    }

    public String getPsForInsertRelationQueryStringBulkProcessedTrue() {
        return prepareStatement(relationalSchemaName(), queryProvider.getRelationalDataInsertQuery(), queryProvider.getRelationalDataInsertQueryPredicateWithTrue());
    }

    private String relationalSchemaName() {
        return schemaName + SEPARATOR + RELATION_QUALIFIER;
    }

    public String getPsForUpsertSelectQueryString() {
        return isRefSchema ?
                prepareStatement(REF_SCHEMA, queryProvider.getUpsertSelectQuery(), queryProvider.getUpsertSelectQueryRefPredicate()) :
                prepareStatement(schemaName, queryProvider.getUpsertSelectQuery(), queryProvider.getUpsertSelectQueryPredicate());
    }

    public String getPsForUpsertSelectBULKQueryString() {
        return isRefSchema ?
                prepareStatement(REF_SCHEMA, queryProvider.getUpsertSelectQuery(), queryProvider.getUpsertSelectQueryRefPredicate()) :
                prepareStatement(schemaName, queryProvider.getUpsertSelectQuery(), queryProvider.getUpsertSelectBulkQueryPredicate());
    }

    public String getPsForSelectTimestampQueryString() {
        return isRefSchema ?
                null :
                prepareStatement(relationalSchemaName(), queryProvider.getSelectTimestampQuery(), queryProvider.getSelectTimestampQueryPredicate());
    }
}
