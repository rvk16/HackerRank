package com.amdocs.aia.il.common.stores;

public interface QueryProvider {

    String getSelectQuery();
    String getSelectWithBulkProcessedFlagQuery();
    String getSelectQueryPredicate();

    String getRelationalDataInsertQuery();
    String getRelationalDataInsertQueryPredicate();
    String getRelationalDataInsertQueryPredicateWithTrue(); //

    String getUpsertSelectQuery();
    String getUpsertSelectQueryPredicate();
    String getUpsertSelectQueryRefPredicate();
    String getUpsertSelectBulkQueryPredicate();

    String getMainDataInsertQuery();
    String getMainDataInsertQueryPredicate();

    String getPartialTrxSelectQuery();
    String getPartialTrxInsertQuery();
    String getPartialTrxDeleteQuery();

    String getRetryInsertQuery();
    String getRetryDeleteSingleQuery();
    String getRetryDeleteQuery();
    String getRetrySelectQuery();
    String getRetrySelectAllQuery();
    String getRetrySelectAllQueryWithFilters();

    String getRIExistsQuery();
    String getRIPersistsQuery();
    String getTTLQuery();
    String getWRITETIMEQuery();

    String getRefTableSelectQuery();
    String getSelectTimestampQuery();
    String getSelectTimestampQueryPredicate();

}
