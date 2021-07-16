package com.amdocs.aia.il.configuration.mapper.external;

import com.amdocs.aia.common.core.web.AiaInternalServerException;
import com.amdocs.aia.il.common.model.external.ExternalAttributeIncrementalAttribute;
import com.amdocs.aia.il.common.model.external.ExternalEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.ExternalEntityFilter;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.configuration.dto.ExternalEntityCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityFilterDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityIncrementalAttributeDTO;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.stream.Collectors;

public abstract class AbstractExternalEntityCollectionRulesInfoMapper<M extends ExternalEntityCollectionRules, D extends ExternalEntityCollectionRulesDTO> implements ExternalEntityCollectionRulesMapper<M, D> {

    private Class<M> modelClass;
    private Class<D> dtoClass;

    protected void lazyInit() {
        final Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        modelClass = (Class<M>) actualTypeArguments[0];
        dtoClass = (Class<D>) actualTypeArguments[1];
    }

    private Class<M> getModelClass() {
        if (modelClass == null) {
            lazyInit();
        }
        return modelClass;
    }

    public Class<D> getDtoClass() {
        if (dtoClass == null) {
            lazyInit();
        }
        return dtoClass;
    }

    @Override
    public M toModel(D dto, ExternalSchema schema) {
        try {
            final M model = getModelClass().getConstructor().newInstance();
            model.setDefaultFilter(toModel(dto.getDefaultFilter()));
            model.setFilters(dto.getFilters()!=null ? dto.getFilters().stream().map(this::toModel).collect(Collectors.toList()) : Collections.emptyList());
            model.setIncrementalAttribute(toModel(dto.getIncrementalAttribute()));
            return model;
        } catch (Exception e) {
            throw new AiaInternalServerException(e);
        }
    }

    private ExternalEntityFilter toModel(ExternalEntityFilterDTO filterDTO) {
        if (filterDTO == null) {
            return null;
        }
        ExternalEntityFilter filter = new ExternalEntityFilter();
        filter.setFilterKey(filterDTO.getFilterKey());
        filter.setFilterLogic(filterDTO.getFilterLogic());
        return filter;
    }

    private ExternalAttributeIncrementalAttribute toModel(ExternalEntityIncrementalAttributeDTO dto) {
        if (dto == null) {
            return null;
        }
        final ExternalAttributeIncrementalAttribute attribute = new ExternalAttributeIncrementalAttribute();
        attribute.setKey(dto.getKey());
        attribute.setType(ExternalAttributeIncrementalAttribute.Type.valueOf(dto.getType().name()));
        return attribute;
    }

    @Override
    public D toDTO(M model) {
        try {
            if (model == null) return null;
            D dto = getDtoClass().getConstructor().newInstance();
            dto.setDefaultFilter(toDTO(model.getDefaultFilter()));
            dto.setFilters(model.getFilters().stream().map(this::toDTO).collect(Collectors.toList()));
            dto.setIncrementalAttribute(toDTO(dto.getIncrementalAttribute()));
            return dto;
        } catch (Exception e) {
            throw new AiaInternalServerException(e);
        }
    }

    private ExternalEntityFilterDTO toDTO(ExternalEntityFilter model) {
        if (model == null) {
            return null;
        }
        ExternalEntityFilterDTO dto = new ExternalEntityFilterDTO();
        dto.setFilterKey(model.getFilterKey());
        dto.setFilterLogic(model.getFilterLogic());
        return dto;
    }

    private ExternalEntityIncrementalAttributeDTO toDTO(ExternalEntityIncrementalAttributeDTO model) {
        if (model == null) {
            return null;
        }
        final ExternalEntityIncrementalAttributeDTO dto = new ExternalEntityIncrementalAttributeDTO();
        dto.setKey(model.getKey());
        dto.setType(ExternalEntityIncrementalAttributeDTO.TypeEnum.valueOf(model.getType().name()));
        return dto;
    }

}
