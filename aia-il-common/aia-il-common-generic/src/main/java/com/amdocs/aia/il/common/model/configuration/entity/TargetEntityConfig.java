package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.common.model.logical.LogicalEntity;

public class TargetEntityConfig {

    private final String name;
    private final String namespace;

    private LogicalEntity entity;

    public TargetEntityConfig( String name, String namespace) {
        this.name = name;
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public LogicalEntity getEntity() {
        return entity;
    }

    public void setEntity(LogicalEntity entity) {
        this.entity = entity;
    }

    public String getNamespace() {
        return namespace;
    }

}
