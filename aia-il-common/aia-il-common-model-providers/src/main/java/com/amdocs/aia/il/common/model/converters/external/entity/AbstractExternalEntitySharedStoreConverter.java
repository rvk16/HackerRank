package com.amdocs.aia.il.common.model.converters.external.entity;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.store.AttributeStore;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.il.common.model.converters.ConverterUtils;
import com.amdocs.aia.il.common.model.external.ExternalAttribute;
import com.amdocs.aia.il.common.model.external.ExternalEntity;

import java.util.stream.Collectors;

public abstract class AbstractExternalEntitySharedStoreConverter extends AbstractExternalEntityConverter<EntityStore> {


    @Override
    public String getTargetProductKey() {
        return ModelConstants.SHARED_PRODUCT_KEY;
    }

    @Override
    public String getTargetElementType() {
        return EntityStore.ELEMENT_TYPE_CODE;
    }

    @Override
    public Class<EntityStore> getTargetClass() {
        return EntityStore.class;
    }

    @Override
    public EntityStore convert(ExternalEntity source) {
        EntityStore entityStore = super.convert(source);
        entityStore.setEntityStoreKey(source.getEntityKey());
        entityStore.setNumericKey(source.getSerializationId() != null ? source.getSerializationId() : 0);
        entityStore.setLogicalEntityKey(null);
        entityStore.setAttributeStores(source.getAttributes().stream().map(this::convertAttribute).collect(Collectors.toList()));
        return entityStore;
    }

    private AttributeStore convertAttribute(ExternalAttribute externalAttribute) {
        AttributeStore attributeStore = ConverterUtils.createAndCopyProperties(AttributeStore.class, externalAttribute);
        attributeStore.setLogicalAttributeKey(externalAttribute.getAttributeKey());
        attributeStore.setType(getTargetAttributeType(externalAttribute));
        attributeStore.setNumericKey(externalAttribute.getSerializationId());
        attributeStore.setKeyPosition(externalAttribute.getKeyPosition());
        attributeStore.setIsLogicalTime(externalAttribute.isLogicalTime());
        attributeStore.setIsUpdateTime(externalAttribute.isUpdateTime());
        attributeStore.setIsRequired(externalAttribute.isRequired());
        return attributeStore;
    }

    protected abstract String getTargetAttributeType(ExternalAttribute externalAttribute);

}
