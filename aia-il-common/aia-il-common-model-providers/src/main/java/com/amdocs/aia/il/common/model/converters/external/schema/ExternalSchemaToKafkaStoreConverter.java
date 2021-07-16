package com.amdocs.aia.il.common.model.converters.external.schema;

import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaStoreInfo;
import com.amdocs.aia.il.common.model.physical.kafka.KafkaSchemaStore;

public class ExternalSchemaToKafkaStoreConverter extends AbstractExternalSchemaPhysicalStoreConverter<KafkaSchemaStore, ExternalKafkaSchemaStoreInfo, ExternalKafkaSchemaCollectionRules> {
    @Override
    public String getTargetElementType() {
        return KafkaSchemaStore.ELEMENT_TYPE;
    }

    @Override
    public Class<KafkaSchemaStore> getTargetClass() {
        return KafkaSchemaStore.class;
    }

    @Override
    protected Class<ExternalKafkaSchemaStoreInfo> getStoreInfoClass() {
        return ExternalKafkaSchemaStoreInfo.class;
    }

    @Override
    protected void populatePhysicalStoreInfo(KafkaSchemaStore target, ExternalKafkaSchemaStoreInfo storeInfo) {
    }

    @Override
    protected void populateCollectionRules(KafkaSchemaStore target, ExternalKafkaSchemaCollectionRules collectionRules) {
        target.setInputDataChannel(collectionRules.getInputDataChannel());
        target.setSkipNodeFromParsing(collectionRules.getSkipNodeFromParsing());
        target.setDeleteEventJsonPath(collectionRules.getDeleteEventJsonPath());
        target.setDeleteEventOperation(collectionRules.getDeleteEventOperation());
        target.setImplicitHandlerPreviousNode(collectionRules.getImplicitHandlerPreviousNode());
        target.setImplicitHandlerCurrentNode(collectionRules.getImplicitHandlerCurrentNode());
    }
}
