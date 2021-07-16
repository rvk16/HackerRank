package com.amdocs.aia.il.configuration.mapper;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.common.model.repo.ChangeStatus;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel;
import com.amdocs.aia.il.common.model.stores.NumericKeyAssignmentPolicy;
import com.amdocs.aia.il.common.model.stores.SchemaStoreCategory;
import com.amdocs.aia.il.common.model.stores.SourceTargetType;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;
import com.amdocs.aia.il.configuration.dto.*;

public abstract class SchemaStoreModelMapper<M extends AbstractIntegrationLayerSchemaStoreModel, D extends SchemaStoreModelDTO> implements ModelDtoMapper<M, D> {

    @Override
    public M toModel(String projectKey, D dto) {
        M model = createConfiguration();
        model.setProjectKey(projectKey);
        model.setSchemaName(dto.getSchemaName());
        model.setSchemaStoreKey(dto.getSchemaStoreKey());
        model.setLogicalSchemaKey(dto.getLogicalSchemaKey());
        model.setSourceTarget(SourceTargetType.valueOf(dto.getSourceTarget().toString()));
        model.setStoreType(StoreTypeCategory.valueOf(dto.getStoreType().toString()));
        model.setDataChannel(dto.getDataChannel());
        model.setTypeSystem(dto.getTypeSystem());
        model.setReference(dto.isIsReference());
        model.setCategory(SchemaStoreCategory.valueOf(dto.getCategory().toString()));
        setOriginValues(dto.getOriginProcess(), model);
        model.setNumericKeyAssignmentPolicy(NumericKeyAssignmentPolicy.valueOf(dto.getNumericKeyAssignmentPolicy().toString()));
        model.setAssignedEntityNumericKey(dto.getAssignedEntityNumericKey());
        return model;
    }

    private static void setOriginValues(String originProcesStr, AbstractIntegrationLayerSchemaStoreModel model) {
        OriginProcess originProcess = valueOfLabel(originProcesStr);
        if (originProcess != null) {
            model.setOriginProcess(originProcess);
        } else {
            model.setOriginProcess(OriginProcess.CUSTOM);
            model.setOrigin(originProcesStr);
        }
    }

    public static OriginProcess valueOfLabel(String originProcessStr) {
        for (OriginProcess originProcess : OriginProcess.values()) {
            if (originProcess.name().equals(originProcessStr)) {
                return originProcess;
            }
        }
        return null;
    }

    @Override
    public D toDTO(M model) {
        D dto = createDTO();
        dto.projectKey(model.getProjectKey())
                .schemaName(model.getSchemaName())
                .description(model.getDescription())
                .schemaStoreKey(model.getSchemaStoreKey())
                .logicalSchemaKey(model.getLogicalSchemaKey())
                .sourceTarget(SourceTargetTypeDTO.valueOf(model.getSourceTarget().toString()))
                .storeType(StoreTypeDTO.valueOf(model.getStoreType().toString()))
                .dataChannel(model.getDataChannel())
                .typeSystem(model.getTypeSystem())
                .isReference(model.getReference())
                .category(StoreCategoryDTO.valueOf(model.getCategory().toString()))
                .numericKeyAssignmentPolicy(NumericKeyAssignmentPolicyDTO.valueOf(model.getNumericKeyAssignmentPolicy().toString()))
                .assignedEntityNumericKey(model.getAssignedEntityNumericKey())
                .status(toDTO(model.getElementChangeStatus()));

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