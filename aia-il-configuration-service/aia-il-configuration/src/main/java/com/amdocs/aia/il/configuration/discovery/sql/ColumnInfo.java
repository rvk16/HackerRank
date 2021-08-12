package com.amdocs.aia.il.configuration.discovery.sql;

public class ColumnInfo {
    private String name;
    // from java.sql.Types
    private int datatype;
    private int columnSize;
    private int decimalDigits;

    public ColumnInfo() {
    }

    public ColumnInfo(String name, int datatype, int columnSize,int decimalDigits) {
        this.name = name;
        this.datatype = datatype;
        this.columnSize = columnSize;
        this.decimalDigits = decimalDigits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDatatype() {
        return datatype;
    }

    public void setDatatype(int datatype) {
        this.datatype = datatype;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }
}
