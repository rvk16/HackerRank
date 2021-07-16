package com.amdocs.aia.il.configuration.mapper;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.common.model.transformation.TransformationContextEntity;
import com.amdocs.aia.il.common.model.configuration.transformation.LeadKey;
import com.amdocs.aia.il.common.model.configuration.transformation.Transformation;
import com.amdocs.aia.il.common.model.configuration.transformation.TransformationImplementationType;
import com.amdocs.aia.il.common.model.configuration.transformation.TransformationSourceType;
import com.amdocs.aia.il.configuration.dto.CommonModelDTO;
import com.amdocs.aia.il.configuration.dto.LeadKeyDTO;
import com.amdocs.aia.il.configuration.dto.TransformationContextEntityDTO;
import com.amdocs.aia.il.configuration.dto.TransformationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransformationMapper extends EntityModelMapper<Transformation, TransformationDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformationMapper.class);

    @Override
    public Transformation toModel(final String projectKey, final TransformationDTO dto) {
        Transformation transformation = super.toModel(projectKey, dto);

        transformation.setContextKey(dto.getContextKey());
        transformation.setSourceType(TransformationDTO.SourceTypeEnum.CONTEXT.equals(dto.getSourceType()) ?
                TransformationSourceType.CONTEXT : TransformationSourceType.REFERENCE);
        transformation.setTargetEntityStoreKey(dto.getTargetEntityStoreKey());
        transformation.setTargetSchemaStoreKey(dto.getTargetSchemaStoreKey());
        transformation.setCustomScript(dto.getCustomScript());
        transformation.setCustomScriptForDeletionKeys(dto.getCustomScriptForDeletionKeys());
        transformation.setCustomGroovyScript(dto.getCustomGroovyScript());
        transformation.setCustomGroovyScriptForDeletionKeys(dto.getCustomGroovyScriptForDeletionKeys());
        transformation.setReferenceSourceEntities(dto.getReferenceSourceEntities() != null ? dto.getReferenceSourceEntities().stream().map(TransformationMapper::toModel).collect(Collectors.toList()) : Collections.emptyList());
        transformation.setImplementationType(TransformationDTO.ImplementationTypeEnum.SQL.equals(dto.getImplementationType()) ? TransformationImplementationType.CUSTOM_SQL : TransformationImplementationType.CUSTOM_GROOVY);
        transformation.setName(StringUtils.hasText(dto.getDisplayName())? dto.getDisplayName(): genrateDefaultDisplayName(dto));
        transformation.setPublished(dto.isIsPublished());
        transformation.setTargetSchemaName(dto.getTargetSchemaName());
        transformation.setReferenceAttributes(dto.getReferenceAttributes());
        transformation.setLeadKeys(dto.getLeadkeys()!=null ?dto.getLeadkeys().stream().map(TransformationMapper::toModel).collect(Collectors.toList()) : Collections.emptyList() );
        setOriginValues(dto.getOriginProcess(), transformation);
        return transformation;
    }

    private static LeadKey toModel(LeadKeyDTO leadKeyDTO) {
        LeadKey leadKey=new LeadKey();
        leadKey.setTargetAttribute(leadKeyDTO.getTargetAttribute());
        leadKey.setSourceAttribute(leadKeyDTO.getSourceAttribute());
        return  leadKey;
    }

    @Override
    public List<Transformation> toModel(String projectKey, List<TransformationDTO> dtos) {
        return dtos.stream()
                .peek(dto->  LOGGER.info("Transformation toModel: {} {}", dto.getContextKey(),dto.getStoreName() ))
                .map(dto -> toModel(projectKey,dto))
                .collect(Collectors.toList());

    }

    private String genrateDefaultDisplayName(TransformationDTO dto) {
        return String.format("%s -> %s",dto.getContextKey(),dto.getTargetEntityStoreKey() );
    }

    private static TransformationContextEntity toModel(TransformationContextEntityDTO transformationContextEntityDTO) {
        final TransformationContextEntity transformationContextEntity = new TransformationContextEntity();
        transformationContextEntity.setEntityStoreKey(transformationContextEntityDTO.getEntityStoreKey());
        transformationContextEntity.setSchemaStoreKey(transformationContextEntityDTO.getSchemaStoreKey());
        transformationContextEntity.setFilterDescription(transformationContextEntityDTO.getFilterDescription());
        return transformationContextEntity;
    }

    private static void setOriginValues(String originProcesStr, Transformation transformation) {
        OriginProcess originProcess = valueOfLabel(originProcesStr);
        if (originProcess != null) {
            transformation.setOriginProcess(originProcess);
        } else {
            transformation.setOriginProcess(OriginProcess.CUSTOM);
            transformation.setOrigin(originProcesStr);
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
    public TransformationDTO toDTO(final Transformation model) {
        TransformationDTO publisherTransformationDTO = super.toDTO(model)
                .id(model.getKey())
                .contextKey(model.getContextKey())
                .targetEntityStoreKey(model.getTargetEntityStoreKey())
                .targetSchemaStoreKey(model.getTargetSchemaStoreKey())
                .customScript(model.getCustomScript())
                .customScriptForDeletionKeys(model.getCustomScriptForDeletionKeys())
                .customGroovyScript(model.getCustomGroovyScript())
                .customGroovyScriptForDeletionKeys(model.getCustomGroovyScriptForDeletionKeys())
                .sourceType(TransformationSourceType.CONTEXT.equals(model.getSourceType()) ? TransformationDTO.SourceTypeEnum.CONTEXT : TransformationDTO.SourceTypeEnum.REFERENCE)
                .implementationType(TransformationImplementationType.CUSTOM_SQL.equals(model.getImplementationType()) ?
                        TransformationDTO.ImplementationTypeEnum.SQL : TransformationDTO.ImplementationTypeEnum.GROOVY)
                .referenceSourceEntities(model.getReferenceSourceEntities().stream().map(TransformationMapper::toDTO).collect(Collectors.toList()))
                .isPublished(model.isPublished())
                .targetSchemaName(model.getTargetSchemaName())
                .referenceAttributes(model.getReferenceAttributes())
                .leadkeys(model.getLeadKeys()!=null? model.getLeadKeys().stream().map(TransformationMapper::toDTO).collect(Collectors.toList()): Collections.emptyList() );
        publisherTransformationDTO.modelType(CommonModelDTO.ModelTypeEnum.TRANSFORMATION);
        return publisherTransformationDTO;
    }

    private static LeadKeyDTO  toDTO(LeadKey leadKey) {
        LeadKeyDTO leadKeyDTO=new LeadKeyDTO();
        leadKeyDTO.setTargetAttribute(leadKey.getTargetAttribute());
        leadKeyDTO.setSourceAttribute(leadKey.getSourceAttribute());
        return leadKeyDTO;

    }

    private static TransformationContextEntityDTO toDTO(TransformationContextEntity transformationContextEntity) {
        final TransformationContextEntityDTO transformationContextEntityDTO = new TransformationContextEntityDTO();
        transformationContextEntityDTO.setEntityStoreKey(transformationContextEntity.getEntityStoreKey());
        transformationContextEntityDTO.setSchemaStoreKey(transformationContextEntity.getSchemaStoreKey());
        transformationContextEntityDTO.setFilterDescription(transformationContextEntity.getFilterDescription());
        return transformationContextEntityDTO;
    }

    @Override
    protected TransformationDTO createDTO() {
        return new TransformationDTO();
    }

    @Override
    protected Transformation createConfiguration() {
        return new Transformation();
    }
}