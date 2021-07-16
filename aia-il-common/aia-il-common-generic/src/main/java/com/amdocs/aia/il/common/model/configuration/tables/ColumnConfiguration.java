package com.amdocs.aia.il.common.model.configuration.tables;

import java.io.Serializable;

/**
 * Created by SWARNIMJ
 */
public class ColumnConfiguration implements Serializable {

    private static final long serialVersionUID = 3611220620914338103L;
    private String columnName;
    private ColumnDatatype datatype;
    private boolean isLogicalTime;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ColumnDatatype getDatatype() {
        return datatype;
    }

    public void setDatatype(ColumnDatatype datatype) {
        this.datatype = datatype;
    }

    public boolean isLogicalTime() {
        return isLogicalTime;
    }

    public void setLogicalTime(boolean logicalTime) {
        isLogicalTime = logicalTime;
    }

}
