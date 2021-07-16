package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractExternalEntityStoreInfo implements ExternalEntityStoreInfo {
    private static final long serialVersionUID = -523211693454835221L;

    private final String type;

    protected AbstractExternalEntityStoreInfo(String type) {
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
