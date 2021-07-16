package com.amdocs.aia.il.configuration.mapper.external;

import com.amdocs.aia.common.model.extensions.typesystems.TypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.TypeSystemFactory;
import com.amdocs.aia.il.common.model.configuration.ConfigurationUtils;
import com.amdocs.aia.il.common.model.external.ExternalAttribute;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.ExternalEntityReplicationPolicy;
import com.amdocs.aia.il.common.model.external.ExternalEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.configuration.dto.ExternalAttributeDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityStoreInfoDTO;
import com.amdocs.aia.il.configuration.mapper.MapperUtils;
import com.amdocs.aia.il.configuration.mapper.ModelDtoMapper;
import com.amdocs.aia.il.configuration.repository.external.ExternalSchemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.common.model.configuration.ConfigurationUtils.nullSafeBoolean;

@Component
public class ExternalEntityMapper implements ModelDtoMapper<ExternalEntity, ExternalEntityDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalEntityMapper.class);
    private final ExternalSchemaRepository externalSchemaRepository; // TODO allow request-level caching of the schema
    private final TypeSystemFactory typeSystemFactory;
    private final ExternalAttributeMapper attributeMapper;
    private final ExternalModelTypeSpecificMapperLookup<ExternalEntityStoreInfoMapper> storeInfoMapperLookup;
    private final ExternalModelTypeSpecificMapperLookup<ExternalEntityCollectionRulesMapper> collectionRulesMapperLookup;

    public ExternalEntityMapper(ExternalSchemaRepository externalSchemaRepository, TypeSystemFactory typeSystemFactory, ExternalAttributeMapper attributeMapper, ExternalModelTypeSpecificMapperLookup<ExternalEntityStoreInfoMapper> storeInfoMapperLookup, ExternalModelTypeSpecificMapperLookup<ExternalEntityCollectionRulesMapper> collectionRulesMapperLookup) {
        this.externalSchemaRepository = externalSchemaRepository;
        this.typeSystemFactory = typeSystemFactory;
        this.attributeMapper = attributeMapper;
        this.storeInfoMapperLookup = storeInfoMapperLookup;
        this.collectionRulesMapperLookup = collectionRulesMapperLookup;
    }



    public ExternalEntity toModel(String projectKey, ExternalEntityDTO dto) {
        final ExternalSchema schema = externalSchemaRepository.getByKey(projectKey, dto.getSchemaKey());
        return setModel(projectKey,dto,schema);
    }

    private ExternalEntity setModel(String projectKey, ExternalEntityDTO dto, ExternalSchema schema){
        final ExternalEntity model = new ExternalEntity();
        model.setSchemaKey(dto.getSchemaKey());
        model.setEntityKey(dto.getEntityKey());
        model.setName(dto.getEntityName());
        model.setDescription(dto.getDescription());
        model.setSerializationId(dto.getSerializationId());
        model.setReplicationPolicy(getReplicationPolicy(dto.isIsTransient()));
        model.setIsTransaction(nullSafeBoolean(dto.isIsTransaction()));
        model.setSchemaType(schema.getSchemaType());
        model.setTypeSystem(schema.getTypeSystem());
        model.setAttributes(getAttributes(dto.getAttributes(), typeSystemFactory.getTypeSystemForKey(schema.getTypeSystem())));
        model.setProjectKey(projectKey);
        model.setStoreInfo(toModel(dto.getStoreInfo(),schema));
        model.setCollectionRules(toModel(dto.getCollectionRules(),schema));
        model.setOriginProcess(ConfigurationUtils.getOriginProcess(dto.getOriginProcess()));
        model.setIsActive(nullSafeBoolean(dto.isIsActive()));
        model.setAvailability(schema.getAvailability());
        model.setSubjectAreaKey(schema.getSubjectAreaKey());
        return model;
    }

    @Override
    public List<ExternalEntity> toModel(String projectKey, List<ExternalEntityDTO> dtos) {

        final List<ExternalSchema> schemas = externalSchemaRepository.findByProjectKey(projectKey);

        return dtos.stream()
                .peek(dto->    LOGGER.info("ExternalEntity toModel: {}", dto.getEntityKey() ))
                .map(dto -> {

            Optional<ExternalSchema> externalSchema = schemas.stream().filter(schema -> schema.getSchemaKey().equals(dto.getSchemaKey())).findAny();
            return setModel(projectKey,dto,  externalSchema.get());
        }).collect(Collectors.toList());
    }

    private ExternalEntityReplicationPolicy getReplicationPolicy(Boolean isTransient) {
        return isTransient != null && isTransient.booleanValue() ? ExternalEntityReplicationPolicy.NO_REPLICATION : ExternalEntityReplicationPolicy.REPLICATE;
    }

    public ExternalEntityDTO toDTO(ExternalEntity model) {
        ExternalEntityDTO dto = new ExternalEntityDTO();
        dto.setSchemaKey(model.getSchemaKey());
        dto.setEntityKey(model.getEntityKey());
        dto.setEntityName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setSerializationId(model.getSerializationId());
        dto.setIsTransient(ExternalEntityReplicationPolicy.NO_REPLICATION.equals(model.getReplicationPolicy()));
        dto.setIsTransaction(model.getIsTransaction());
        dto.setStoreInfo(toDTO(model.getStoreInfo()));
        dto.setCollectionRules(toDTO(model.getCollectionRules()));
        dto.setAttributes(getAttributesDtos(model.getAttributes()));
        dto.setIsActive(model.getIsActive());
        dto.setStatus(MapperUtils.toDTO(model.getChangeStatus()));
        dto.setCreatedBy(model.getCreatedBy());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setOriginProcess(model.getOriginProcess().name());
        return dto;
    }


    private ExternalEntityCollectionRules toModel(ExternalEntityCollectionRulesDTO dto, ExternalSchema schema) {
        return dto != null ? collectionRulesMapperLookup.getByDtoDiscriminator(dto.getStoreType()).toModel(dto,schema) : null;
    }

    private ExternalEntityStoreInfo toModel(ExternalEntityStoreInfoDTO dto,ExternalSchema schema) {
        return dto != null ? storeInfoMapperLookup.getByDtoDiscriminator(dto.getStoreType()).toModel(dto,schema) : null;
    }

    private List<ExternalAttributeDTO> getAttributesDtos(List<ExternalAttribute> attributes) {
        if (attributes == null) {
            return Collections.emptyList();
        }
        final Set<ExternalAttributeDTO> attributesDtos = new HashSet<>(attributes.size());
        for (final ExternalAttribute externalAttribute : attributes) {
            attributesDtos.add(attributeMapper.toDTO(externalAttribute));
        }
        return attributesDtos.stream().collect(Collectors.toList());
    }

    private List<ExternalAttribute> getAttributes(final List<ExternalAttributeDTO> externalAttributeDTOList, TypeSystem typeSystem) {
        if (externalAttributeDTOList == null) {
            return Collections.emptyList();
        }
        final Set<ExternalAttribute> attributes = new HashSet<>(externalAttributeDTOList.size());
        for (final ExternalAttributeDTO externalAttributeDTO : externalAttributeDTOList) {
            attributes.add(attributeMapper.toModel(externalAttributeDTO, typeSystem));
        }
        return attributes.stream().collect(Collectors.toList());
    }

    private ExternalEntityCollectionRulesDTO toDTO(ExternalEntityCollectionRules model) {
        return model != null ? collectionRulesMapperLookup.getBySchemaType(model.getType()).toDTO(model) : null;
    }

    private ExternalEntityStoreInfoDTO toDTO(ExternalEntityStoreInfo model) {
        return model != null ? storeInfoMapperLookup.getBySchemaType(model.getType()).toDTO(model) : null;
    }


}
