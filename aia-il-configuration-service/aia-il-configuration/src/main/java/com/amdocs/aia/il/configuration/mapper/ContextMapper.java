package com.amdocs.aia.il.configuration.mapper;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.il.common.model.configuration.transformation.Context;
import com.amdocs.aia.il.common.model.configuration.transformation.ContextEntity;
import com.amdocs.aia.il.common.model.configuration.transformation.ContextEntityRelationType;
import com.amdocs.aia.il.common.model.configuration.transformation.NoReferentAction;
import com.amdocs.aia.il.configuration.dto.CommonModelDTO;
import com.amdocs.aia.il.configuration.dto.ContextDTO;
import com.amdocs.aia.il.configuration.dto.ContextEntityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class ContextMapper extends EntityModelMapper<Context, ContextDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextMapper.class);
    @Override
    public Context toModel(String projectKey, ContextDTO dto) {
        Context context = super.toModel(projectKey, dto);
        context.setContextKey(dto.getContextKey());
        context.setName(StringUtils.hasText(dto.getDisplayName())? dto.getDisplayName(): dto.getStoreName());
        context.setContextEntities(dto.getContextEntities() == null ? Collections.emptyList() :
                dto.getContextEntities().stream().map(ContextMapper::toModel).collect(Collectors.toList()));
        setOriginValues(dto.getOriginProcess(), context);
        return context;
    }

    @Override
    public List<Context> toModel(String projectKey, List<ContextDTO> dtos) {

        return dtos.stream()
                .peek(dto->  LOGGER.info("Context toModel: {} {}", dto.getContextKey() ,dto.getStoreName() ))
                .map( group -> toModel(projectKey,group))
                .collect(Collectors.toList());

    }

    private static ContextEntity toModel(ContextEntityDTO contextEntityDTO) {
        final ContextEntity contextEntity = new ContextEntity();
        contextEntity.setEntityStoreKey(contextEntityDTO.getEntityStoreKey());
        contextEntity.setSchemaStoreKey(contextEntityDTO.getSchemaStoreKey());
        contextEntity.setParentContextEntityKey(contextEntityDTO.getParentContextEntityKey());
        contextEntity.setForeignKeys(contextEntityDTO.getForeignKeys());
        contextEntity.setSourceAlias(contextEntityDTO.getSourceAlias());
        contextEntity.setAliasedSourceEntityKey(contextEntityDTO.getAliasedSourceEntityKey()==null? contextEntityDTO.getSourceAlias()+"-"+contextEntityDTO.getEntityStoreKey():contextEntityDTO.getAliasedSourceEntityKey());
        contextEntity.setRelationType(ContextEntityRelationType.fromValue(contextEntityDTO.getRelationType().toString()));
        contextEntity.setDoPropagation(contextEntityDTO.isDoPropagation());
        setReferentAction(contextEntityDTO.getNoReferentAction(), contextEntity);
        return contextEntity;
    }

    private static void setOriginValues(String originProcessStr, Context context) {
        OriginProcess originProcess = valueOfLabel(originProcessStr);
        if (originProcess != null) {
            context.setOriginProcess(originProcess);
        } else {
            context.setOriginProcess(OriginProcess.CUSTOM);
            context.setOrigin(originProcessStr);
        }
    }

    private static void setReferentAction(ContextEntityDTO.NoReferentActionEnum noReferentAction, ContextEntity publisherContext) {
        if (noReferentAction != null) {
            publisherContext.setNoReferentAction(NoReferentAction.valueOf(noReferentAction.toString()));
        } else {
            publisherContext.setNoReferentAction(null);
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
    public ContextDTO toDTO(Context model) {
        ContextDTO publisherContextDTO = super.toDTO(model)
                .contextKey(model.getContextKey())
                .contextEntities(model.getContextEntities().stream().map(ContextMapper::toDTO).collect(Collectors.toList()));
        publisherContextDTO.modelType(CommonModelDTO.ModelTypeEnum.CONTEXT);
        return publisherContextDTO;
    }

    private static ContextEntityDTO toDTO(ContextEntity contextEntity) {
        final ContextEntityDTO contextEntityDTO = new ContextEntityDTO();
        contextEntityDTO.setEntityStoreKey(contextEntity.getEntityStoreKey());
        contextEntityDTO.setSchemaStoreKey(contextEntity.getSchemaStoreKey());
        contextEntityDTO.setParentContextEntityKey(contextEntity.getParentContextEntityKey());
        contextEntityDTO.setDoPropagation(contextEntity.isDoPropagation());
        contextEntityDTO.setAliasedSourceEntityKey(contextEntity.getAliasedSourceEntityKey());
        contextEntityDTO.setForeignKeys(contextEntity.getForeignKeys());
        contextEntityDTO.setSourceAlias(contextEntity.getSourceAlias());
        contextEntityDTO.setRelationType(ContextEntityDTO.RelationTypeEnum.fromValue(contextEntity.getRelationType().toString()));
        setReferentActionDTO(contextEntity.getNoReferentAction(), contextEntityDTO);
        return contextEntityDTO;
    }

    private static void setReferentActionDTO(NoReferentAction noReferentAction, ContextEntityDTO contextEntityDTO) {
        if (noReferentAction == null) {
            contextEntityDTO.setNoReferentAction(null);
        } else {
            contextEntityDTO.setNoReferentAction(ContextEntityDTO.NoReferentActionEnum.valueOf(noReferentAction.toString()));
        }
    }

    @Override
    protected ContextDTO createDTO() {
        return new ContextDTO();
    }

    @Override
    protected Context createConfiguration() {
        return new Context();
    }
}