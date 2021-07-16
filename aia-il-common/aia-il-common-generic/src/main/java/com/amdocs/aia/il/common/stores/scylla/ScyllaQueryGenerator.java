package com.amdocs.aia.il.common.stores.scylla;

import com.amdocs.aia.il.common.stores.QueryGenerator;
import com.amdocs.aia.il.common.stores.QueryProvider;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

public class ScyllaQueryGenerator extends QueryGenerator {

    private static final long serialVersionUID = -6271426241002990532L;
    private final String keySpace;

    private final transient PreparedStatement psForInsert;
    private final transient PreparedStatement psForInsertRealtion;
    private final transient PreparedStatement psForSelect;
    private final transient PreparedStatement psForSelectWithBulkProcessedFlag;
    private final transient PreparedStatement psForSelectUpsert;
    private final transient PreparedStatement psForSelectBulkUpsert;
    private final transient PreparedStatement psForInsertWithTTL;
    private final transient PreparedStatement psForInsertWithTTLWriteTime;
    private final transient PreparedStatement psForInsertRelationWithTTL;
    private final transient PreparedStatement psForInsertRelationWithTTLWriteTime;
    private final transient PreparedStatement psForInsertRelationBulkProcessedTrue;
    private final transient PreparedStatement psForSelectTimestamp;

    public ScyllaQueryGenerator(String schemaName, String keySpace, Integer timeToLive, QueryProvider queryProvider, Boolean isReference) {
        super(schemaName, queryProvider, isReference);
        this.keySpace = keySpace;
        CqlSession session = ScyllaConnection.getSession();
        this.psForSelect = !super.isRefSchema() ? session.prepare(super.getPsForSelectQueryString()) : null;
        this.psForSelectWithBulkProcessedFlag = !super.isRefSchema() ? session.prepare(super.getPsForSelectQueryWithBulkProcessedFlagString()) : null;
        this.psForInsert = session.prepare(super.getPsForInsertQueryString().replace(";", "") + "USING " + queryProvider.getWRITETIMEQuery() + ";");
        this.psForInsertRealtion = !super.isRefSchema() ? session.prepare(super.getPsForInsertRelationQueryString().replace(";", "") + "USING " + queryProvider.getWRITETIMEQuery() + ";") : null;
        this.psForSelectUpsert = session.prepare(super.getPsForUpsertSelectQueryString());
        this.psForSelectBulkUpsert = session.prepare(super.getPsForUpsertSelectBULKQueryString());
        this.psForInsertWithTTL = session.prepare(super.getPsForInsertQueryString().replace(";", "") + queryProvider.getTTLQuery() + timeToLive + ";");
        this.psForInsertWithTTLWriteTime = session.prepare(super.getPsForInsertQueryString().replace(";", "") + queryProvider.getTTLQuery() + timeToLive + " and " + queryProvider.getWRITETIMEQuery() + ";");
        this.psForInsertRelationWithTTL = !super.isRefSchema() ? session.prepare(super.getPsForInsertRelationQueryString().replace(";", "") + queryProvider.getTTLQuery() + timeToLive + ";") : null;
        this.psForInsertRelationWithTTLWriteTime = !super.isRefSchema() ? session.prepare(super.getPsForInsertRelationQueryString().replace(";", "") + queryProvider.getTTLQuery() + timeToLive + " and " + queryProvider.getWRITETIMEQuery() + ";") : null;
        this.psForInsertRelationBulkProcessedTrue = !super.isRefSchema() ? session.prepare(super.getPsForInsertRelationQueryStringBulkProcessedTrue()) : null;
        this.psForSelectTimestamp = !super.isRefSchema() ? session.prepare(super.getPsForSelectTimestampQueryString()) : null;
    }

    @Override
    protected String prepareStatement(String schemaName, String head, String tail) {
        return head + keySpace + '.' + schemaName + tail;
    }

    public String getKeySpace() {
        return keySpace;
    }

    public PreparedStatement getPsForInsert() {
        return psForInsert;
    }

    public PreparedStatement getPsForInsertRelation() {
        return psForInsertRealtion;
    }

    public PreparedStatement getPsForSelect() {
        return psForSelect;
    }

    public PreparedStatement getPsForSelectWithBulkProcessedFlag() {
        return psForSelectWithBulkProcessedFlag;
    }

    public PreparedStatement getPsForSelectUpsert() {
        return psForSelectUpsert;
    }

    public PreparedStatement getPsForSelectBulkUpsert() {
        return psForSelectBulkUpsert;
    }

    public PreparedStatement getPsForInsertWithTTL() {
        return psForInsertWithTTL;
    }

    public PreparedStatement getPsForInsertRelationWithTTL() {
        return psForInsertRelationWithTTL;
    }

    public PreparedStatement getPsForInsertRelationWithBulkProcessedTrue() {
        return psForInsertRelationBulkProcessedTrue;
    }

    public PreparedStatement getPsForSelectTimestamp() {
        return psForSelectTimestamp;
    }

    public PreparedStatement getPsForInsertWithTTLWriteTime() {
        return psForInsertWithTTLWriteTime;
    }

    public PreparedStatement getPsForInsertRelationWithTTLWriteTime() {
        return psForInsertRelationWithTTLWriteTime;
    }
}
