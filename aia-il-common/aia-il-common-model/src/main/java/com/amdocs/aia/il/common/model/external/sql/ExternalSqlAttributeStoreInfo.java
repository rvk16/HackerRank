package com.amdocs.aia.il.common.model.external.sql;

import com.amdocs.aia.il.common.model.external.AbstractExternalAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExternalSqlAttributeStoreInfo extends AbstractExternalAttributeStoreInfo {

    private static final long serialVersionUID = 7319766697452244064L;

    public ExternalSqlAttributeStoreInfo() {
        super(ExternalSchemaStoreTypes.SQL);
    }
}
