package com.amdocs.aia.il.configuration.discovery.sql;

import java.util.List;

public class IndexInfo {
    private String indexName;
    private List<String> columnNames;
    private boolean isUnique;

    public IndexInfo() {
    }

    public IndexInfo(String indexName, List<String> columnNames, boolean isUnique) {
        this.indexName = indexName;
        this.columnNames = columnNames;
        this.isUnique = isUnique;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }
}
