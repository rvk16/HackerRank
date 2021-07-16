package com.amdocs.aia.il.common.model.external.sql;

import com.amdocs.aia.il.common.model.external.AbstractExternalSchemaStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExternalSqlSchemaStoreInfo extends AbstractExternalSchemaStoreInfo {

    private static final long serialVersionUID = -1420040887227115353L;

    private String databaseType;

    public ExternalSqlSchemaStoreInfo() {
        super(ExternalSchemaStoreTypes.SQL);
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }
}