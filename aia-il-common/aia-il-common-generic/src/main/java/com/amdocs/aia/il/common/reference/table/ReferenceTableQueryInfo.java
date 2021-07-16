package com.amdocs.aia.il.common.reference.table;


import com.amdocs.aia.il.common.model.configuration.RTPEntityType;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.publisher.AbstractMessageCreator;
import com.amdocs.aia.il.common.stores.RandomAccessTable;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;

import java.util.Map;

/**
 * Holds information about reference table
 * Reference table contains sql to run on this tables
 * Reference table can be published
 */
public class ReferenceTableQueryInfo extends AbstractReferenceTableInfo {

    private static final long serialVersionUID = -2418557610101064931L;
    private final String sql;
    private AbstractMessageCreator messageCreator;

    public ReferenceTableQueryInfo(String name, Map<String, RandomAccessTable> tableAccessInfos, Map<String, ConfigurationRow> tableInfos,
                                   String sql, TargetEntity targetEntity, boolean isPublished) {
        super(name, tableAccessInfos, tableInfos, targetEntity, isPublished);
        this.sql = sql;
    }


    public String getSql() {
        return sql;
    }

    @Override
    public RTPEntityType getRTPEntityType() {
        return RTPEntityType.REFERENCE_TABLE_QUERY;
    }

    @Override
    public String toString() {
        return "ReferenceTableQueryInfo{" +
                "sql='" + sql + '\'' +
                ", queryEntityMessageCreator=" + messageCreator +
                "} " + super.toString();
    }
}
