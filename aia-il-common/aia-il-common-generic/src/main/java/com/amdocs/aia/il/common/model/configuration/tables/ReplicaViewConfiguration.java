package com.amdocs.aia.il.common.model.configuration.tables;

public class ReplicaViewConfiguration extends AbstractPublisherConfigurationModel {
    private static final long serialVersionUID = -6302565855991079177L;

    public static final String ELEMENT_TYPE = getElementTypeFor(ReplicaViewConfiguration.class);

    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}