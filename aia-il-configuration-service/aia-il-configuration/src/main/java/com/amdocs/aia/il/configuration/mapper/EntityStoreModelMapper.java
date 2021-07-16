package com.amdocs.aia.il.configuration.mapper;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.common.model.properties.PropertyData;
import com.amdocs.aia.common.model.repo.ChangeStatus;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerEntityStoreModel;
import com.amdocs.aia.il.common.model.IntegrationLayerAttributeStore;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;
import com.amdocs.aia.il.configuration.dto.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class EntityStoreModelMapper<M extends AbstractIntegrationLayerEntityStoreModel, D extends EntityStoreModelDTO> implements ModelDtoMapper<M, D> {

    @Override
    public M toModel(final String projectKey, final D dto) {
        final M model = createConfiguration();
        model.setProjectKey(dto.getProjectKey());
        model.setEntityName(dto.getEntityName());
        model.setSchemaStoreKey(dto.getSchemaStoreKey());
        model.setLogicalSchemaKey(dto.getLogicalSchemaKey());
        model.setEntityStoreKey(dto.getEntityStoreKey());
        model.setLogicalEntityKey(dto.getLogicalEntityKey());
        model.setStoreType(StoreTypeCategory.valueOf(dto.getStoreType().toString()));
        model.setSerializationId(dto.getSerializationId());
        model.setAssignedAttributeNumericKey(new LinkedHashMap<>());
        setOriginValues(dto.getOriginProcess(), model);
        model.setAttributeStores(dto.getAttributeStores() != null ? dto.getAttributeStores().stream().map(EntityStoreModelMapper::toModel).collect(Collectors.toList()) : Collections.emptyList());
        return model;
    }

    private static void setOriginValues(final String originProcessStr, final AbstractIntegrationLayerEntityStoreModel model) {
        final OriginProcess originProcess = valueOfLabel(originProcessStr);
        if (originProcess != null) {
            model.setOriginProcess(originProcess);
        } else {
            model.setOriginProcess(OriginProcess.CUSTOM);
            model.setOrigin(originProcessStr);
        }
    }

    public static OriginProcess valueOfLabel(final String originProcessStr) {
        for (final OriginProcess originProcess : OriginProcess.values()) {
            if (originProcess.name().equals(originProcessStr)) {
                return originProcess;
            }
        }
        return null;
    }

    @Override
    public D toDTO(final M model) {
        final D dto = createDTO();
        dto.projectKey(model.getProjectKey())
                .schemaStoreKey(model.getSchemaStoreKey())
                .entityStoreKey(model.getEntityStoreKey())
                .entityName(model.getEntityName())
                .logicalSchemaKey(model.getLogicalSchemaKey())
                .logicalEntityKey(model.getLogicalEntityKey())
                .storeType(StoreTypeDTO.valueOf(model.getStoreType().toString()))
                .assignedAttributeNumericKey(model.getAssignedAttributeNumericKey())
                .serializationId(model.getSerializationId())
                .attributeStores(model.getAttributeStores() == null ? Collections.emptyList() : model.getAttributeStores().stream().map(EntityStoreModelMapper::toDTO).collect(Collectors.toList())  )
                .status(toDTO(model.getElementChangeStatus()));
        return dto;
    }

    private static IntegrationLayerAttributeStore toModel(final AttributeStoreDTO attributeStoreDTO) {
        final IntegrationLayerAttributeStore model = new IntegrationLayerAttributeStore();
        model.setSchemaStoreKey(attributeStoreDTO.getSchemaStoreKey());
        model.setEntityStoreKey(attributeStoreDTO.getEntityStoreKey());
        model.setAttributeStoreKey(attributeStoreDTO.getAttributeStoreKey());
        model.setType(attributeStoreDTO.getType());
        model.setKeyPosition(attributeStoreDTO.getKeyPosition());
        model.setLogicalTime(attributeStoreDTO.isIsLogicalTime());
        model.setUpdateTime(attributeStoreDTO.isIsUpdateTime());
        model.setRequired(attributeStoreDTO.isIsRequired());
        model.setSerializationId(attributeStoreDTO.getSerializationId());
        model.setDoImplementAttribute(attributeStoreDTO.isDoImplementAttribute());
        model.setProperties(attributeStoreDTO.getDynamicProperties() == null ? Collections.emptyMap() :
                attributeStoreDTO.getDynamicProperties().stream().collect( Collectors.toMap(AttributeStorePropertyDTO::getPropertyKey,attribute -> new PropertyData( attribute.getPropertyValue())))) ;

        return model;
    }

    private static AttributeStoreDTO toDTO(final IntegrationLayerAttributeStore model) {
        final AttributeStoreDTO dto = new AttributeStoreDTO();
        dto.schemaStoreKey(model.getSchemaStoreKey())
                .entityStoreKey(model.getEntityStoreKey())
                .attributeStoreKey(model.getAttributeStoreKey())
                .type(model.getType())
                .keyPosition(model.getKeyPosition())
                .isLogicalTime(model.isLogicalTime())
                .isUpdateTime(model.isUpdateTime())
                .doImplementAttribute(model.isDoImplementAttribute())
                .isRequired(model.isRequired())
                .dynamicProperties((model.getProperties() == null) ? Collections.emptyList() :
                        model.getProperties().entrySet().stream().map(e -> new AttributeStorePropertyDTO().propertyKey(e.getKey()).propertyValue(e.getValue())).collect(Collectors.toList()));
        return dto;
    }

    public static ChangeStatusDTO toDTO(final ChangeStatus changeStatus) {
        if (changeStatus == null) {
            return ChangeStatusDTO.NOT_EXIST;
        }
        switch (changeStatus) {
            case DRAFT:
                return ChangeStatusDTO.DRAFT;
            case MODIFIED:
                return ChangeStatusDTO.MODIFIED;
            case PUBLISHED:
                return ChangeStatusDTO.PUBLISHED;
            default:
                throw new IllegalArgumentException("Unknown status type: " + changeStatus);
        }
    }

    protected abstract D createDTO();

    protected abstract M createConfiguration();
}