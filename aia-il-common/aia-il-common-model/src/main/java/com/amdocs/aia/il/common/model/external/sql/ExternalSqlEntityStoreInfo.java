package com.amdocs.aia.il.common.model.external.sql;

import com.amdocs.aia.il.common.model.external.AbstractExternalEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExternalSqlEntityStoreInfo extends AbstractExternalEntityStoreInfo {
    private static final long serialVersionUID = 6834733003323307152L;

    public ExternalSqlEntityStoreInfo() {
        super(ExternalSchemaStoreTypes.SQL);
    }
}