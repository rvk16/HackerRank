package com.amdocs.aia.il.common.model.converters.shared.entity;

import com.amdocs.aia.common.model.store.AttributeStore;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerEntityStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.IntegrationLayerAttributeStore;
import com.amdocs.aia.il.common.model.converters.ConverterUtils;


import java.util.stream.Collectors;

public abstract class AbstractTargetEntityPrivateStoreConverter<T extends AbstractIntegrationLayerEntityStoreModel> extends AbstractTargetEntityConverter<T> {

    @Override
    public String getTargetProductKey() {
        return ConfigurationConstants.PRODUCT_KEY;
    }

    @Override
    public T convert(EntityStore entityStore) {
        T target = super.convert(entityStore);
        target.setSchemaStoreKey(getSchemaStoreKey(entityStore.getSchemaStoreKey()));
        target.setEntityStoreKey(entityStore.getEntityStoreKey());
        target.setEntityName(entityStore.getName());
        target.setSerializationId(entityStore.getNumericKey());
        target.setEntityStoreKey(entityStore.getEntityStoreKey());
        target.setEntityName(entityStore.getEntityStoreKey());
        target.setId(ModelUtils.generateGlobalUniqueId(target, target.getEntityStoreKey()));
        target.setAttributeStores(entityStore.getAttributeStores().stream().map(attribute -> convertAttribute(attribute)).collect(Collectors.toList()));
        return target;
    }

    private IntegrationLayerAttributeStore convertAttribute( AttributeStore attributeStore) {
        IntegrationLayerAttributeStore dataChannelAttributeStore = ConverterUtils.createAndCopyProperties(IntegrationLayerAttributeStore.class, attributeStore);
        dataChannelAttributeStore.setAttributeStoreKey(attributeStore.getLogicalAttributeKey());
        dataChannelAttributeStore.setType(attributeStore.getType());
        dataChannelAttributeStore.setSerializationId(attributeStore.getNumericKey());
        dataChannelAttributeStore.setKeyPosition(attributeStore.getKeyPosition());
        dataChannelAttributeStore.setLogicalTime(attributeStore.getIsLogicalTime());
        dataChannelAttributeStore.setUpdateTime(attributeStore.getIsUpdateTime());
        dataChannelAttributeStore.setRequired(attributeStore.getIsRequired());
        return dataChannelAttributeStore;
    }

    protected abstract String getTargetTypeSystem(EntityStore entityStore);

    protected abstract String getSchemaStoreKey(String schemaKey);
}
