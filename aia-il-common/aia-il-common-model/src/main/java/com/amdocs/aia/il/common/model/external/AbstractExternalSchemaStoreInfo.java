package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractExternalSchemaStoreInfo implements ExternalSchemaStoreInfo {
    private static final long serialVersionUID = 6259184949375735166L;

    private final String type;

    public AbstractExternalSchemaStoreInfo(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    @JsonIgnore
    public final PhysicalStoreType getPhysicalStoreType() {
        return ExternalSchemaStoreTypes.toPhysicalStoreType(type);
    }
}
