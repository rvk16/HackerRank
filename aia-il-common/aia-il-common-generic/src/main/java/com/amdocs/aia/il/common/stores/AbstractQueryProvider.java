package com.amdocs.aia.il.common.stores;

import java.io.Serializable;

public class AbstractQueryProvider implements QueryProvider, Serializable {

    private static final long serialVersionUID = 6565922676312765683L;

    public static final String SELECT_QUERY = "SELECT relationkey, ttl(tablenamecopy) as ttl_value, writetime(tablenamecopy), tsversion FROM ";
    public static final String SELECT_QUERY_PREDICATES = " where tablename = ? AND tablepk = ? AND contextname = ? AND relationtype = ? AND relationtable =? ;";

    public static final String SELECT_QUERY_WITH_BULKPROCESSEDFLAG = "SELECT relationkey,bulkprocessed,tsversion  FROM ";

    public static final String RELATIONAL_DATA_INSERT_QUERY = "INSERT INTO ";
    public static final String RELATIONAL_DATA_INSERT_QUERY_PREDICATES = " (tablename,tablepk,contextname,relationtype,relationtable,relationkey,tsversion,tablenamecopy, bulkprocessed) VALUES (?,?,?,?,?,?,?,?,false);";
    public static final String RELATIONAL_DATA_INSERT_QUERY_PREDICATES_TRUE = " (tablename,tablepk,contextname,relationtype,relationtable,relationkey,tsversion,tablenamecopy, bulkprocessed) VALUES (?,?,?,?,?,?,?,?,true);";

    public static final String UPSERT_SELECT_QUERY = "SELECT maintabledata,tsversion,ttl(tablenamecopy),writetime(tablenamecopy) FROM ";
    public static final String UPSERT_SELECT_QUERY_PREDICATES = " where tablename = ? AND tablepk = ? ;";
    public static final String UPSERT_SELECT_QUERY_BULK_PREDICATES = " where tablename = ? AND tablepk = ? LIMIT 1 ;";
    public static final String UPSERT_SELECT_QUERY_REF_PREDICATES = " where tablename = ? AND tablepk = ? LIMIT 1;";

    public static final String MAIN_DATA_INSERT_QUERY = "INSERT INTO ";
    public static final String MAIN_DATA_INSERT_QUERY_PREDICATES = " (tablename,tablepk,tsversion,maintabledata,tablenamecopy) VALUES (?,?,?,?,?);";

    public static final String PARTIAL_TXN_SELECT_QUERY = "SELECT MAINTABLEDATA FROM RT_PUBLISHER_TRANSACTIONS where dataChannelName = ? and partitionId = ? ;";
    public static final String PARTIAL_TXN_INSERT_QUERY = "INSERT INTO RT_PUBLISHER_TRANSACTIONS(dataChannelName,partitionId,maintabledata) values(?,?,?) ;";
    public static final String PARTIAL_TXN_DELETE_QUERY = "DELETE FROM RT_PUBLISHER_TRANSACTIONS where datachannelname = ? and partitionid = ?;";

    public static final String RETRY_INSERT_QUERY = "INSERT INTO RETRY(leading_key,context_name,ttl,next_retry_time,interval,leading_table_data) values(?,?,?,?,?,?) ;";
    public static final String RETRY_DELETE_SINGLE_QUERY = "DELETE FROM RETRY WHERE context_name = ? AND leading_key = ? AND ttl = ? AND next_retry_time = ?;";
    public static final String RETRY_DELETE_QUERY = "DELETE FROM RETRY WHERE context_name = ? AND leading_key IN ? ;";
    public static final String RETRY_SELECT_QUERY = "SELECT leading_key, context_name, ttl, next_retry_time, interval, leading_table_data FROM RETRY WHERE  leading_key = ? AND context_name = ? ;";
    public static final String RETRY_SELECT_ALL_QUERY = "SELECT leading_key, context_name, ttl, next_retry_time, interval, leading_table_data FROM RETRY ;";
    public static final String RETRY_SELECT_ALL_QUERY_WITH_FILTER = "SELECT leading_key, context_name, ttl, next_retry_time, interval, leading_table_data FROM RETRY WHERE ttl > ? AND next_retry_time > ? ;";

    public static final String REF_TABLE_SELECT = "SELECT maintabledata FROM ref WHERE tableName = ? ;";
    public static final String SEPARATOR = "_";
    public static final String RELATION_QUALIFIER = "RELATION";

    public static final String RI_EXISTS = "SELECT dummy_or_real_indicator FROM referential_integrity WHERE target_entity_key=? AND target_entity_table_name=? AND dummy_or_real_indicator=? ;";
    public static final String RI_PERSIST = "INSERT INTO referential_integrity(target_entity_key, target_entity_table_name, dummy_or_real_indicator) values(?,?,?) ;";
    public static final String TTL_QUERY = " USING TTL ";
    public static final String WRITE_TIME_QUERY = "TIMESTAMP ?";

    public static final String SELECT_TIMESTAMP_QUERY = "SELECT tsversion,relationKey FROM ";
    public static final String SELECT_TIMESTAMP_QUERY_PREDICATES = " where tablename = ? AND tablepk = ? AND contextname = ? AND relationtype = ? AND relationtable =? AND tsversion <= ? LIMIT 1;";

    @Override
    public String getSelectQuery() {
        return SELECT_QUERY;
    }

    @Override
    public String getSelectWithBulkProcessedFlagQuery() {
        return SELECT_QUERY_WITH_BULKPROCESSEDFLAG;
    }

    @Override
    public String getSelectQueryPredicate() {
        return SELECT_QUERY_PREDICATES;
    }

    @Override
    public String getRelationalDataInsertQuery() {
        return RELATIONAL_DATA_INSERT_QUERY;
    }

    @Override
    public String getRelationalDataInsertQueryPredicate() {
        return RELATIONAL_DATA_INSERT_QUERY_PREDICATES;
    }

    @Override
    public String getRelationalDataInsertQueryPredicateWithTrue() {
        return RELATIONAL_DATA_INSERT_QUERY_PREDICATES_TRUE;
    }

    @Override
    public String getUpsertSelectQuery() {
        return UPSERT_SELECT_QUERY;
    }

    @Override
    public String getUpsertSelectQueryPredicate() {
        return UPSERT_SELECT_QUERY_PREDICATES;
    }

    @Override
    public String getUpsertSelectQueryRefPredicate() {
        return UPSERT_SELECT_QUERY_REF_PREDICATES;
    }

    @Override
    public String getUpsertSelectBulkQueryPredicate() {
        return UPSERT_SELECT_QUERY_BULK_PREDICATES;
    }


    @Override
    public String getMainDataInsertQuery() {
        return MAIN_DATA_INSERT_QUERY;
    }

    @Override
    public String getMainDataInsertQueryPredicate() {
        return MAIN_DATA_INSERT_QUERY_PREDICATES;
    }

    @Override
    public String getPartialTrxSelectQuery() {
        return PARTIAL_TXN_SELECT_QUERY;
    }

    @Override
    public String getPartialTrxInsertQuery() {
        return PARTIAL_TXN_INSERT_QUERY;
    }

    @Override
    public String getPartialTrxDeleteQuery() {
        return PARTIAL_TXN_DELETE_QUERY;
    }

    @Override
    public String getRetryInsertQuery() {
        return RETRY_INSERT_QUERY;
    }

    @Override
    public String getRetryDeleteSingleQuery() {
        return RETRY_DELETE_SINGLE_QUERY;
    }

    @Override
    public String getRetryDeleteQuery() {
        return RETRY_DELETE_QUERY;
    }

    @Override
    public String getRetrySelectQuery() {
        return RETRY_SELECT_QUERY;
    }

    @Override
    public String getRetrySelectAllQuery() {
        return RETRY_SELECT_ALL_QUERY;
    }

    @Override
    public String getRetrySelectAllQueryWithFilters() {
        return RETRY_SELECT_ALL_QUERY_WITH_FILTER;
    }

    @Override
    public String getRIExistsQuery() {
        return RI_EXISTS;
    }

    @Override
    public String getRIPersistsQuery() {
        return RI_PERSIST;
    }

    @Override
    public String getTTLQuery() {
        return TTL_QUERY;
    }

    @Override
    public String getWRITETIMEQuery() {
        return WRITE_TIME_QUERY;
    }



    @Override
    public String getRefTableSelectQuery() {
        return REF_TABLE_SELECT;
    }

    @Override
    public String getSelectTimestampQuery() {
        return SELECT_TIMESTAMP_QUERY;
    }

    @Override
    public String getSelectTimestampQueryPredicate() {
        return SELECT_TIMESTAMP_QUERY_PREDICATES;
    }
}
