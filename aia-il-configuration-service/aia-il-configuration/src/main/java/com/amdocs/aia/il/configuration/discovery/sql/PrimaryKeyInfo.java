package com.amdocs.aia.il.configuration.discovery.sql;

import java.util.List;

public class PrimaryKeyInfo {
    private List<String> columnNames;

    public PrimaryKeyInfo(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public PrimaryKeyInfo() {
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }
}
