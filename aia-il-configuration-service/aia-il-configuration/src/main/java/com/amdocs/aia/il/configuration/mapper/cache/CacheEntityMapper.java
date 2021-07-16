package com.amdocs.aia.il.configuration.mapper.cache;

import com.amdocs.aia.il.common.model.cache.CacheAttribute;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import com.amdocs.aia.il.common.model.configuration.ConfigurationUtils;
import com.amdocs.aia.il.configuration.dto.CacheReferenceAttributeDTO;
import com.amdocs.aia.il.configuration.dto.CacheReferenceEntityDTO;
import com.amdocs.aia.il.configuration.mapper.MapperUtils;
import com.amdocs.aia.il.configuration.mapper.ModelDtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CacheEntityMapper implements ModelDtoMapper<CacheEntity, CacheReferenceEntityDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheEntityMapper.class);
    private final CacheAttributeMapper cacheAttributeMapper;

    public CacheEntityMapper(CacheAttributeMapper cacheAttributeMapper){
        this.cacheAttributeMapper = cacheAttributeMapper;
    }

    @Override
    public CacheEntity toModel(String projectKey, CacheReferenceEntityDTO dto) {

        final CacheEntity model = new CacheEntity();
        model.setProjectKey(projectKey);
        model.setEntityKey(dto.getCacheReferenceEntityKey());
        model.setName(dto.getCacheReferenceEntityName());
        model.setDescription(dto.getDescription());
        model.setAttributes(getAttributes(dto.getCacheReferenceAttributes()));
        model.setOriginProcess(ConfigurationUtils.getOriginProcess(dto.getOriginProcess()));
        return model;
    }

    @Override
    public List<CacheEntity> toModel(String projectKey, List<CacheReferenceEntityDTO> dtos) {
        return dtos.stream()
                .peek(dto->  LOGGER.info("CacheEntity toModel: {}", dto.getCacheReferenceEntityKey() ))
                .map( group -> toModel(projectKey,group))
                .collect(Collectors.toList());
    }

    @Override
    public CacheReferenceEntityDTO toDTO(CacheEntity model) {

        CacheReferenceEntityDTO dto = new CacheReferenceEntityDTO();
        dto.setCacheReferenceEntityKey(model.getEntityKey());
        dto.setCacheReferenceEntityName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setCacheReferenceAttributes(getAttributesDtos(model.getAttributes()));
        dto.setStatus(MapperUtils.toDTO(model.getChangeStatus()));
        dto.setCreatedBy(model.getCreatedBy());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setOriginProcess(model.getOriginProcess().name());
        return dto;
    }

    private List<CacheAttribute> getAttributes(List<CacheReferenceAttributeDTO> cacheReferenceAttributes) {
        if (cacheReferenceAttributes == null) {
            return Collections.emptyList();
        }
        final Set<CacheAttribute> attributes = new HashSet<>(cacheReferenceAttributes.size());
        for (final CacheReferenceAttributeDTO cacheReferenceAttributeDTO : cacheReferenceAttributes) {
            attributes.add(cacheAttributeMapper.toModel(cacheReferenceAttributeDTO));
        }
        return attributes.stream().collect(Collectors.toList());
    }

    private List<CacheReferenceAttributeDTO> getAttributesDtos(List<CacheAttribute> cacheAttributes){
            if (cacheAttributes == null) {
                return Collections.emptyList();
            }
            final Set<CacheReferenceAttributeDTO> attributesDtos = new HashSet<>(cacheAttributes.size());
            for (final CacheAttribute cacheAttribute : cacheAttributes) {
                attributesDtos.add(cacheAttributeMapper.toDTO(cacheAttribute));
            }
            return attributesDtos.stream().collect(Collectors.toList());
    }

}
