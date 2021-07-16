package com.amdocs.aia.il.common.model.converters.external.entity;

import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerEntityStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.IntegrationLayerAttributeStore;
import com.amdocs.aia.il.common.model.converters.ConverterUtils;
import com.amdocs.aia.il.common.model.external.ExternalAttribute;
import com.amdocs.aia.il.common.model.external.ExternalEntity;

import java.util.stream.Collectors;

public abstract class AbstractExternalEntityPrivateStoreConverter<T extends AbstractIntegrationLayerEntityStoreModel> extends AbstractExternalEntityConverter<T> {

    @Override
    public String getTargetProductKey() {
        return ConfigurationConstants.PRODUCT_KEY;
    }

    @Override
    public T convert(ExternalEntity source) {
        T target = super.convert(source);
        target.setSchemaStoreKey(getSchemaStoreKey(source.getSchemaKey()));
        target.setEntityStoreKey(source.getEntityKey());
        target.setEntityName(source.getEntityKey());
        target.setLogicalEntityKey(null);
        target.setSerializationId(source.getSerializationId());
        target.setEntityStoreKey(source.getEntityKey());
        target.setId(ModelUtils.generateGlobalUniqueId(target, target.getEntityStoreKey()));
        target.setAttributeStores(source.getAttributes().stream().map(attribute -> convertAttribute(source, attribute)).collect(Collectors.toList()));
        return target;
    }

    private IntegrationLayerAttributeStore convertAttribute(ExternalEntity externalEntity, ExternalAttribute externalAttribute) {
        IntegrationLayerAttributeStore attributeStore = ConverterUtils.createAndCopyProperties(IntegrationLayerAttributeStore.class, externalAttribute);
        attributeStore.setAttributeStoreKey(externalAttribute.getAttributeKey());
        final String targetTypeSystem = getTargetTypeSystem(externalEntity);
        if (targetTypeSystem.equals(externalEntity.getTypeSystem())) {
            attributeStore.setType(externalAttribute.getDatatype());
        } else {
            attributeStore.setType(convertDatatypeFromLogical(externalAttribute.getLogicalDatatype(), targetTypeSystem));
        }
        attributeStore.setSerializationId(externalAttribute.getSerializationId());
        attributeStore.setKeyPosition(externalAttribute.getKeyPosition());
        attributeStore.setLogicalTime(externalAttribute.isLogicalTime());
        attributeStore.setUpdateTime(externalAttribute.isUpdateTime());
        attributeStore.setRequired(externalAttribute.isRequired());
        populateAttributeStoreDynamicProperties(attributeStore, externalEntity, externalAttribute);
        return attributeStore;
    }

    protected void populateAttributeStoreDynamicProperties(IntegrationLayerAttributeStore targetAttributeStore, ExternalEntity externalEntity, ExternalAttribute externalAttribute) {
        populateDynamicPropertyIfNotEmpty(targetAttributeStore, ConfigurationConstants.ATTRIBUTE_DEFAULT_VALUE, externalAttribute.getDefaultValue());
        populateDynamicPropertyIfNotEmpty(targetAttributeStore, ConfigurationConstants.ATTRIBUTE_VALIDATE_REGEX, externalAttribute.getValidationRegex());
    }

    protected abstract String getTargetTypeSystem(ExternalEntity externalEntity);

    protected abstract String getSchemaStoreKey(String schemaKey);
}
