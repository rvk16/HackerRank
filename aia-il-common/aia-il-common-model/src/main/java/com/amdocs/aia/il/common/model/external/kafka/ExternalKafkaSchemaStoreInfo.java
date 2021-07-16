package com.amdocs.aia.il.common.model.external.kafka;

import com.amdocs.aia.il.common.model.external.AbstractExternalSchemaStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;

public class ExternalKafkaSchemaStoreInfo extends AbstractExternalSchemaStoreInfo {

    private static final long serialVersionUID = -8964625811109686865L;

    private String defaultDateFormat;

    public ExternalKafkaSchemaStoreInfo() {
        super(ExternalSchemaStoreTypes.KAFKA);
    }

    public String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    public void setDefaultDateFormat(String defaultDateFormat) {
        this.defaultDateFormat = defaultDateFormat;
    }
}
