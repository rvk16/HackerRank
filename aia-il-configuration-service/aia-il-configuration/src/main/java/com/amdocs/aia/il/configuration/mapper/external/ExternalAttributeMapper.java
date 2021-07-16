package com.amdocs.aia.il.configuration.mapper.external;

import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.TypeSystem;
import com.amdocs.aia.il.common.model.external.ExternalAttribute;
import com.amdocs.aia.il.common.model.external.ExternalAttributeStoreInfo;
import com.amdocs.aia.il.configuration.dto.ExternalAttributeDTO;
import com.amdocs.aia.il.configuration.dto.ExternalAttributeStoreInfoDTO;
import org.springframework.stereotype.Component;

import static com.amdocs.aia.il.common.model.configuration.ConfigurationUtils.nullSafeBoolean;

@Component
public class ExternalAttributeMapper {
    private final ExternalModelTypeSpecificMapperLookup<ExternalAttributeStoreInfoMapper> storeInfoMapperLookup;

    public ExternalAttributeMapper(ExternalModelTypeSpecificMapperLookup<ExternalAttributeStoreInfoMapper> storeInfoMapperLookup) {
        this.storeInfoMapperLookup = storeInfoMapperLookup;
    }

    public ExternalAttribute toModel(ExternalAttributeDTO dto, TypeSystem typeSystem) {
        ExternalAttribute model = new ExternalAttribute();
        model.setAttributeKey(dto.getAttributeKey());
        model.setDatatype(dto.getDatatype());
        model.setKeyPosition(dto.getKeyPosition());
        model.setName(dto.getAttributeName());
        model.setLogicalDatatype(LogicalTypeSystem.format(typeSystem.toLogicalDatatype(dto.getDatatype())));
        model.setSerializationId(dto.getSerializationId() != null ? dto.getSerializationId() : -1);
        model.setDescription(dto.getDescription());
        model.setLogicalTime(nullSafeBoolean(dto.isIsLogicalTime()));
        model.setUpdateTime(nullSafeBoolean(dto.isIsUpdateTime()));
        model.setRequired(nullSafeBoolean(dto.isIsRequired()));
        model.setDefaultValue(dto.getDefaultValue());
        model.setValidationRegex(dto.getValidationRegex());
        model.setStoreInfo(toModel(dto.getStoreInfo()));
        return model;
    }

    private ExternalAttributeStoreInfo toModel(ExternalAttributeStoreInfoDTO dto) {
        return dto != null ? storeInfoMapperLookup.getByDtoDiscriminator(dto.getStoreType()).toModel(dto) : null;
    }

    public ExternalAttributeDTO toDTO(ExternalAttribute model) {
        ExternalAttributeDTO dto = new ExternalAttributeDTO();
        dto.setAttributeKey(model.getAttributeKey());
        dto.setAttributeName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setDatatype(model.getDatatype());
        dto.setLogicalDatatype(model.getLogicalDatatype());
        dto.setSerializationId(model.getSerializationId());
        dto.setKeyPosition(model.getKeyPosition());
        dto.setIsLogicalTime(model.isLogicalTime());
        dto.setIsUpdateTime(model.isUpdateTime());
        dto.setIsRequired(model.isRequired());
        dto.setDefaultValue(model.getDefaultValue());
        dto.setValidationRegex(model.getValidationRegex());
        dto.setStoreInfo(toDTO(model.getStoreInfo()));
        return dto;
    }

    private ExternalAttributeStoreInfoDTO toDTO(ExternalAttributeStoreInfo model) {
        return model != null ? storeInfoMapperLookup.getBySchemaType(model.getType()).toDTO(model) : null;
    }

}
