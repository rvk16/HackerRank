package com.amdocs.aia.il.common.model.external.kafka;

import com.amdocs.aia.il.common.model.external.AbstractExternalAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExternalKafkaAttributeStoreInfo extends AbstractExternalAttributeStoreInfo {

    private static final long serialVersionUID = 9136950144282489713L;

    private String jsonPath = null;

    private String dateFormat = null;

    public ExternalKafkaAttributeStoreInfo() {
        super(ExternalSchemaStoreTypes.KAFKA);
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
