package com.amdocs.aia.il.common.model.external.csv;

import com.amdocs.aia.il.common.model.external.AbstractExternalAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExternalCsvAttributeStoreInfo extends AbstractExternalAttributeStoreInfo {

    private static final long serialVersionUID = 1541550695250988128L;

    private String dateFormat = null;

    public ExternalCsvAttributeStoreInfo() {
        super(ExternalSchemaStoreTypes.CSV);
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

}
