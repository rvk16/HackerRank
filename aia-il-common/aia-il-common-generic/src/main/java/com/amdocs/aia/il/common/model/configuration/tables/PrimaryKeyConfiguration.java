package com.amdocs.aia.il.common.model.configuration.tables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SWARNIMJ
 */
public class PrimaryKeyConfiguration implements Serializable {
    private static final long serialVersionUID = 980643932882352488L;

    private List<String> columnNames = new ArrayList<>();

    public PrimaryKeyConfiguration(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public PrimaryKeyConfiguration() {
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }
}
