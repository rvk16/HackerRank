package com.amdocs.aia.il.configuration.mapper.cache;

import com.amdocs.aia.il.common.model.cache.CacheAttribute;
import com.amdocs.aia.il.configuration.dto.CacheReferenceAttributeDTO;
import org.springframework.stereotype.Component;

@Component
public class CacheAttributeMapper{

    public CacheAttribute toModel(CacheReferenceAttributeDTO dto) {
        CacheAttribute model = new CacheAttribute();
        model.setAttributeKey(dto.getAttributeKey());
        model.setName(dto.getAttributeName());
        model.setDescription(dto.getDescription());
        model.setDatatype(dto.getType());
        model.setKeyPosition(dto.getKeyPosition());
        return model;
    }

    public CacheReferenceAttributeDTO toDTO(CacheAttribute model) {
        CacheReferenceAttributeDTO dto = new CacheReferenceAttributeDTO();
        dto.setAttributeKey(model.getAttributeKey());
        dto.setAttributeName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setType(model.getDatatype());
        dto.setKeyPosition(model.getKeyPosition());
        return dto;
    }
}
