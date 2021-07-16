package com.amdocs.aia.il.common.stores;

import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KeyColumnDescriptor implements Serializable {
    private static final long serialVersionUID = -4208051074156374395L;

    private final List<ColumnConfiguration> keys;
    private final String name;

    public KeyColumnDescriptor(final List<ColumnConfiguration> keys) {
        this.keys = keys;
        final List<String> names = new ArrayList<>(keys.size());
        keys.forEach(fk -> names.add(fk.getColumnName()));
        this.name = String.join("_", names);
    }

    public List<ColumnConfiguration> getKeys() {
        return keys;
    }

    public String getName() {
        return name;
    }
}