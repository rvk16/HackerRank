package com.amdocs.aia.il.common.model.physical.sql;

import java.io.Serializable;

/**
 * Incremental column.
 *
 * @author ALEXKRA
 */
public class IncrementalColumn implements Serializable {
    private static final long serialVersionUID = -5057603608520909765L;

    public enum Type {SEQUENCE, TIMESTAMP}

    private String key;
    private Type type;

    public IncrementalColumn() {
        // default constructor
    }

    public IncrementalColumn(final String key) {
        this.key = key;
    }

    public IncrementalColumn(final String key, final Type type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "IncrementalColumn{key='" + key + "', type=" + type + '}';
    }
}