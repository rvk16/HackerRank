package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractExternalAttributeStoreInfo implements ExternalAttributeStoreInfo {

    private static final long serialVersionUID = -4084097936474304177L;
    private final String type;

    protected AbstractExternalAttributeStoreInfo(String type) {
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
