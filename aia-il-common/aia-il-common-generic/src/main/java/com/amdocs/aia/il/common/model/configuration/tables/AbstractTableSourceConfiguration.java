package com.amdocs.aia.il.common.model.configuration.tables;

/**
 * Created by SWARNIMJ
 */
public abstract class AbstractTableSourceConfiguration implements TableSourceConfiguration {
    private static final long serialVersionUID = 4963460775202388732L;

    private String type;

    public AbstractTableSourceConfiguration(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}