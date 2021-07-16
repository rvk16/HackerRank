package com.amdocs.aia.il.common.model.converters.external.entity;

import com.amdocs.aia.il.common.model.IntegrationLayerAttributeStore;
import com.amdocs.aia.il.common.model.external.ExternalAttribute;
import com.amdocs.aia.il.common.model.external.ExternalAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaEntityStoreInfo;
import com.amdocs.aia.il.common.model.physical.kafka.KafkaEntityStore;

public class ExternalEntityToKafkaStoreConverter extends AbstractExternalEntityPhysicalStoreConverter<KafkaEntityStore, ExternalKafkaEntityStoreInfo, ExternalKafkaEntityCollectionRules> {
    @Override
    public String getTargetElementType() {
        return KafkaEntityStore.ELEMENT_TYPE;
    }

    @Override
    public Class<KafkaEntityStore> getTargetClass() {
        return KafkaEntityStore.class;
    }

    @Override
    protected Class<ExternalKafkaEntityStoreInfo> getStoreInfoClass() {
        return ExternalKafkaEntityStoreInfo.class;
    }

    @Override
    protected void populatePhysicalStoreInfo(KafkaEntityStore target, ExternalKafkaEntityStoreInfo storeInfo) {
        target.setJsonTypePath(storeInfo.getJsonTypePath());
        target.setJsonTypeValue(storeInfo.getJsonTypeValue());
        target.setRelativePaths(storeInfo.getRelativePaths());
        target.setMergedNodes(storeInfo.getMergedNodes());
    }

    @Override
    protected void populateCollectionRules(KafkaEntityStore target, ExternalEntity externalEntity, ExternalKafkaEntityCollectionRules collectionRules) {

    }

    @Override
    protected void populateAttributeStoreDynamicProperties(IntegrationLayerAttributeStore targetAttributeStore, ExternalEntity externalEntity, ExternalAttribute externalAttribute) {
        super.populateAttributeStoreDynamicProperties(targetAttributeStore, externalEntity, externalAttribute);
        final ExternalAttributeStoreInfo storeInfo = externalAttribute.getStoreInfo();
        if (storeInfo != null) {
            if (!(storeInfo instanceof ExternalKafkaAttributeStoreInfo)) {
                throw new IllegalArgumentException("Cannot convert external attribute with store info of type " + storeInfo.getClass() + " into a kafka attribute store");
            }
            ExternalKafkaAttributeStoreInfo kafkaStoreInfo = (ExternalKafkaAttributeStoreInfo)storeInfo;
            targetAttributeStore.setPropertyValue(KafkaEntityStore.NAME, externalAttribute.getAttributeKey());
            targetAttributeStore.setPropertyValue(KafkaEntityStore.IS_MANDATORY, externalAttribute.isRequired());
            populateDynamicPropertyIfNotEmpty(targetAttributeStore, KafkaEntityStore.DATE_FORMAT, kafkaStoreInfo.getDateFormat());
            populateDynamicPropertyIfNotEmpty(targetAttributeStore, KafkaEntityStore.JSON_PATH, kafkaStoreInfo.getJsonPath());
        }
    }
}
