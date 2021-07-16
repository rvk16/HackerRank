package com.amdocs.aia.il.common.model.configuration.tables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TableInfoConfiguration implements Serializable {

    private static final long serialVersionUID = 1306522425501232639L;
    private String tableName;
    private List<ColumnConfiguration> columns = new ArrayList<>();
    private PrimaryKeyConfiguration primaryKey = new PrimaryKeyConfiguration();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnConfiguration> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnConfiguration> columns) {
        this.columns = columns;
    }

    public PrimaryKeyConfiguration getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(PrimaryKeyConfiguration primaryKey) {
        this.primaryKey = primaryKey;
    }
}
