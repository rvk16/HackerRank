package com.amdocs.aia.il.configuration.mapper;

import com.amdocs.aia.common.model.repo.ChangeStatus;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.configuration.tables.AbstractPublisherConfigurationModel;
import com.amdocs.aia.il.configuration.dto.ChangeStatusDTO;
import com.amdocs.aia.il.configuration.dto.CommonModelDTO;

public abstract class EntityModelMapper<M extends AbstractPublisherConfigurationModel, D extends CommonModelDTO> implements ModelDtoMapper<M, D> {

    @Override
    public M toModel(final String projectKey, final D dto) {
        final M model = createConfiguration();
        model.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        model.setProjectKey(projectKey);
        model.setName(dto.getDisplayName());
        model.setDescription(dto.getDescription());
        model.setPublisherName(dto.getStoreName());
        model.setKey(dto.getStoreName());
        return model;
    }

    @Override
    public D toDTO(final M model) {
        final D dto = createDTO();
        dto.projectKey(model.getProjectKey())
                .displayName(model.getName())
                .description(model.getDescription())
                .storeName(model.getPublisherName())
                .originProcess(model.getOriginProcess().toString())
                .status(toDTO(model.getStatus()));
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