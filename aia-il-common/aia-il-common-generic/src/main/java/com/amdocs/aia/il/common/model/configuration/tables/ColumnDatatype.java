package com.amdocs.aia.il.common.model.configuration.tables;

import java.io.Serializable;

/**
 * Created by SWARNIMJ
 */
public class ColumnDatatype implements Serializable {

    private static final long serialVersionUID = 9124334738720144689L;

    private String sqlType;

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }
}
