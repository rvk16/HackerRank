package com.amdocs.aia.il.configuration.discovery.sql;

import java.util.List;

public class TableInfo {
    private String tableName;
    private List<ColumnInfo> columns;
    private PrimaryKeyInfo primaryKeyInfo;
    private List<IndexInfo> indexes;

    public TableInfo() {
    }

    public TableInfo(String tableName, List<ColumnInfo> columns, PrimaryKeyInfo primaryKeyInfo, List<IndexInfo> indexes) {
        this.tableName = tableName;
        this.columns = columns;
        this.primaryKeyInfo = primaryKeyInfo;
        this.indexes = indexes;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnInfo> columns) {
        this.columns = columns;
    }

    public List<IndexInfo> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<IndexInfo> indexes) {
        this.indexes = indexes;
    }

    public PrimaryKeyInfo getPrimaryKeyInfo() {
        return primaryKeyInfo;
    }

    public void setPrimaryKeyInfo(PrimaryKeyInfo primaryKeyInfo) {
        this.primaryKeyInfo = primaryKeyInfo;
    }
}
