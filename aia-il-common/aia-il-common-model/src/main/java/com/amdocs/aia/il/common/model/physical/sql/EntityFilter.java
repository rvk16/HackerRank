package com.amdocs.aia.il.common.model.physical.sql;

import com.amdocs.aia.common.model.ProjectElement;

public class EntityFilter extends ProjectElement {
    private static final long serialVersionUID = 6104608693351471752L;

    private String schemaKey;
    private String entityKey;
    private String entityFilterKey;
    private String query;

    public String getSchemaKey() {
        return schemaKey;
    }

    public void setSchemaKey(final String schemaKey) {
        this.schemaKey = schemaKey;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(final String entityKey) {
        this.entityKey = entityKey;
    }

    public String getEntityFilterKey() {
        return entityFilterKey;
    }

    public void setEntityFilterKey(final String entityFilterKey) {
        this.entityFilterKey = entityFilterKey;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }
}