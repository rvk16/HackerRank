package com.amdocs.aia.il.common.model.external.kafka;

import com.amdocs.aia.il.common.model.external.AbstractExternalEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;

public class ExternalKafkaEntityCollectionRules extends AbstractExternalEntityCollectionRules {
    private static final long serialVersionUID = -4533819294069924465L;


    public ExternalKafkaEntityCollectionRules() {
        super(ExternalSchemaStoreTypes.KAFKA);
    }
}
