package com.amdocs.aia.il.configuration.service.external;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.logical.*;
import com.amdocs.aia.common.model.repo.SaveElementsResponse;
import com.amdocs.aia.common.model.store.AttributeStore;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.il.common.model.EntityConfigurationUtils;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.configuration.dependencies.ExternalEntityDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.ExternalEntityDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.mapper.ModelDtoMapper;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.external.ExternalEntityRepository;
import com.amdocs.aia.il.configuration.repository.external.ExternalSchemaRepository;
import com.amdocs.aia.il.configuration.service.AbstractModelService;
import com.amdocs.aia.il.configuration.shared.AiaSharedProxy;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.DATA_CHANNEL;

@Service
public class ExternalEntityServiceImpl extends AbstractModelService<ExternalEntity, ExternalEntityRepository> implements ExternalEntityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalEntityServiceImpl.class);

    private static final String EXTERNAL_ENTITY = "ExternalEntity";
    private static final String EXTERNAL_SCHEMA = "ExternalSchema";

    private final ExternalSchemaRepository externalSchemaRepository;
    private final ModelDtoMapper<ExternalEntity, ExternalEntityDTO> mapper;
    private final AiaRepositoryOperations aiaRepositoryOperations;
    private final ExternalEntityDependencyAnalyzer externalEntityDependencyAnalyzer;
    private final MessageHelper messageHelper;
    private final SerializationIDAssigner serializationIDAssigner;
    private final AiaSharedProxy aiaSharedProxy;
    private final DataChannelStoreType dataChannelStoreType;

    @Autowired
    public ExternalEntityServiceImpl(ExternalEntityRepository externalEntityRepository, ExternalSchemaRepository externalSchemaRepository, MessageHelper messageHelper, ModelDtoMapper<ExternalEntity, ExternalEntityDTO> mapper, AiaRepositoryOperations aiaRepositoryOperations, ExternalEntityDependencyAnalyzer externalEntityDependencyAnalyzer, SerializationIDAssigner serializationIDAssigner, AiaSharedProxy aiaSharedProxy, DataChannelStoreType dataChannelStoreType) {
        super(externalEntityRepository);
        this.externalSchemaRepository = externalSchemaRepository;
        this.messageHelper = messageHelper;
        this.mapper = mapper;
        this.aiaRepositoryOperations = aiaRepositoryOperations;
        this.externalEntityDependencyAnalyzer = externalEntityDependencyAnalyzer;
        this.serializationIDAssigner = serializationIDAssigner;
        this.aiaSharedProxy = aiaSharedProxy;
        this.dataChannelStoreType = dataChannelStoreType;
    }

    @Override
    public ExternalEntityDTO get(String projectKey, String key) {
        return new ExternalEntityDTO();
    }

    @Override
    public List<ExternalEntityDTO> list(String projectKey) {
        return super.getRepository().findByProjectKey(projectKey).stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ExternalEntityDTO save(String projectKey, String externalSchemaKey, ExternalEntityDTO externalEntityDTO) {
        LOGGER.debug("Check if exists external Schema By Key: projectKey {} SchemaKey {}  repository {}", projectKey, externalSchemaKey, externalSchemaRepository != null);
        final ExternalSchema existingExternalSchema = externalSchemaRepository.getByKey(projectKey, externalSchemaKey);
        if (existingExternalSchema == null) {
            throw messageHelper.createElementNotFoundException(EXTERNAL_SCHEMA, externalEntityDTO.getSchemaKey());
        }
        LOGGER.debug("Check if exists external Entity By Key: projectKey {} SchemaKey {}  repository {}", projectKey, externalEntityDTO.getSchemaKey(), super.getRepository() != null);
        final ExternalEntity existingExternalEntity = super.getRepository().getByKey(projectKey, externalSchemaKey, externalEntityDTO.getEntityKey());
        LOGGER.debug("Existing External Entity before save: {}", existingExternalEntity);
        if (existingExternalEntity != null) {
            throw messageHelper.createObjectAlreadyExistException(EXTERNAL_ENTITY, existingExternalEntity.getEntityKey());
        }
        final ExternalEntity model = mapper.toModel(projectKey, externalEntityDTO);
        this.serializationIDAssigner.autoAssignAttributesSerializationIDs(model);
        if (this.serializationIDAssigner.autoAssignSerializationID(existingExternalSchema, model)) {
            externalSchemaRepository.save(existingExternalSchema);
        }
        LOGGER.debug("Complete Model Before Save: {}", model);
        model.setCreatedBy(aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy());
        model.setCreatedAt(System.currentTimeMillis());
        final ExternalEntity savedModel = saveModel(model, true);
        return mapper.toDTO(savedModel);
    }

    public ExternalEntity saveModel(ExternalEntity model, boolean validate) {
        completeModelBeforeSave(model);
        if (validate)
        {
            validateBeforeSave(model);
        }

        LOGGER.debug("Save model: {}", model);
        return getRepository().save(model);
    }

    @Override
    protected void validateBeforeSave(ExternalEntity model) {
        Map<String, List<ExternalAttribute>> attributesByKey = model.getAttributes().stream().collect(Collectors.groupingBy(ExternalAttribute::getAttributeKey));
        String duplicateAttKeys = attributesByKey.entrySet().stream().filter(stringEntry -> stringEntry.getValue().size() > 1).map(Map.Entry::getKey).collect(Collectors.joining(","));
        //check if there duplicate attribute keys
        if (duplicateAttKeys.length() > 0) {
            throw new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.DUPLICATE_ATTRIBUTE, duplicateAttKeys);

        }
        Map<String, List<ExternalEntityFilter>> filtersByKey = model.getCollectionRules().getFilters().stream().collect(Collectors.groupingBy(ExternalEntityFilter::getFilterKey));
        //check if there duplicate filter keys
        String duplicatefilterKeys = filtersByKey.entrySet().stream().filter(stringEntry -> stringEntry.getValue().size() > 1).map(Map.Entry::getKey).collect(Collectors.joining(","));
        if (duplicatefilterKeys.length() > 0) {
            throw new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.DUPLICATE_ATTRIBUTE, duplicatefilterKeys);
        }
        //check if there duplicate keys between attribute keys and filters keys
        String sameKeyAtFilterAndAttKey = attributesByKey.entrySet().stream().filter(attributeEntry -> filtersByKey.containsKey(attributeEntry.getKey())).map(Map.Entry::getKey).collect(Collectors.joining(","));
        if (sameKeyAtFilterAndAttKey.length() > 0) {
            throw new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.SAME_FILTER_KEY_AND_ATTRIBUTE_KEY, sameKeyAtFilterAndAttKey);
        }
        super.validateBeforeSave(model);
    }

    private void completeModelBeforeSave(ExternalEntity model) {
        if(Availability.SHARED.equals(model.getAvailability())){
            final List<EntityStore> entityStores = aiaSharedProxy.searchEntityStores(model.getProjectKey(), String.format("logicalEntityKey:%s AND storeType:" + DATA_CHANNEL, model.getEntityKey()));
            Optional<LogicalEntity> logicalEntityOptional = aiaSharedProxy.searchLogicalEntityByEntityKeyAndSchemaKey(model.getProjectKey(), model.getEntityKey(), model.getSubjectAreaKey());

            if(!entityStores.isEmpty() && logicalEntityOptional.isPresent() ){
                updateLogicalEntity(model, logicalEntityOptional.get());
                updateEntityStore(model, entityStores);
            }

            else{
                ExternalSchema externalSchema = externalSchemaRepository.getByKey(model.getProjectKey(), model.getSchemaKey());
                //create logical entity and entity store
                LogicalEntity logicalEntity = createLogicalEntityFromExternalEntity(model,externalSchema.getSubjectAreaKey());
                final String schemaStoreKey = dataChannelStoreType.generateSchemaStoreKeyForLogical(externalSchema.getSubjectAreaKey());
                Optional<SchemaStore> schemaStore = aiaSharedProxy.searchSchemaStoreBySchemaKey(model.getProjectKey(), schemaStoreKey);
                if(!schemaStore.isPresent()){
                    throw messageHelper.createElementNotFoundException(SchemaStore.ELEMENT_TYPE_CODE, externalSchema.getSubjectAreaKey());
                }

                EntityStore entityStore = dataChannelStoreType.createEntityStoreFromLogical(logicalEntity, schemaStore.get());
                aiaSharedProxy.reportAdd(logicalEntity);
                aiaSharedProxy.reportAdd(entityStore);
            }
        }

        model.setId(EntityConfigurationUtils.getElementId(model));
        model.setDependencies(externalEntityDependencyAnalyzer.getDependencies(model));
        model.setPublicFeatures(externalEntityDependencyAnalyzer.getPublicFeatures(model));
    }

    private void updateLogicalEntity(ExternalEntity model, LogicalEntity logicalEntity) {
        Map<String, LogicalAttribute> logicalAttributeByKey = logicalEntity.getAttributes().stream().collect(Collectors.toMap(LogicalAttribute::getAttributeKey, Function.identity()));
        model.getAttributes().stream().filter(externalAttribute -> !logicalAttributeByKey.containsKey(externalAttribute.getAttributeKey())).forEach(
                externalAttribute -> {
                    LogicalAttribute logicalAttribute = convertExternalAttributeToLogicalAttribute(externalAttribute);
                    logicalEntity.getAttributes().add(logicalAttribute);
                    aiaSharedProxy.reportUpdate(logicalEntity);
                }
        );
    }

    private void updateEntityStore(ExternalEntity model, List<EntityStore> entityStores) {
        EntityStore entityStore = entityStores.get(0);
        Map<String, AttributeStore> entityStoreAttributeByKey = entityStore.getAttributeStores().stream().collect(Collectors.toMap(AttributeStore::getLogicalAttributeKey, Function.identity()));
        model.getAttributes().stream().filter(externalAttribute -> !entityStoreAttributeByKey.containsKey(externalAttribute.getAttributeKey())).forEach(
                externalAttribute -> {
                    AttributeStore attributeStore = convertExternalAttributeToAttributeStore(externalAttribute);
                    entityStore.getAttributeStores().add(attributeStore);
                    aiaSharedProxy.reportUpdate(entityStore);
                }
        );
    }

    private void completeModelBeforeSave(List<ExternalEntity> models, List<ExternalSchema> existingExternalSchemas) {
        final String user = aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy();
        final long currentTime = System.currentTimeMillis();
        final List<ExternalSchema> modifiedSchemas = new ArrayList<>();
        models.forEach(model -> {
            model.setCreatedBy(user);
            model.setCreatedAt(currentTime);
            model.setId(EntityConfigurationUtils.getElementId(model));
            model.setDependencies(externalEntityDependencyAnalyzer.getDependencies(model));
            model.setPublicFeatures(externalEntityDependencyAnalyzer.getPublicFeatures(model));

            Optional<ExternalSchema> existingExternalSchema = existingExternalSchemas.stream().filter(schema -> schema.getSchemaKey().equals(model.getSchemaKey())).findAny();
            if (existingExternalSchema.isPresent()) {
                this.serializationIDAssigner.autoAssignAttributesSerializationIDs(model);
                if (this.serializationIDAssigner.autoAssignSerializationID(existingExternalSchema.get(), model)) {
                    modifiedSchemas.add(existingExternalSchema.get());
                }
            }
        });

        externalSchemaRepository.saveElements(modifiedSchemas);
    }

    @Override
    public void delete(String projectKey, String externalSchemaKey, String externalEntityKey) {
        ExternalEntity externalEntity;
        try {
            externalEntity = super.getRepository().getByKey(projectKey, externalSchemaKey, externalEntityKey);
        } catch (final AiaApiException e) {
            throw messageHelper.createUnexpectedOperationErrorException("get External Entity", e);
        }
        if (externalEntity == null) {
            throw messageHelper.createElementNotFoundException(EXTERNAL_ENTITY, externalEntityKey);
        }
        validateBeforeDelete(externalEntity);
        aiaRepositoryOperations.delete(externalEntity.getId());

    }

    @Override
    public SaveElementsResponseDTO bulkSave(String projectKey, List<ExternalEntityDTO> externalEntitiesDTOs) {
        LOGGER.info("Saving bulk external entities");
        final List<ExternalSchema> existingExternalSchemas = externalSchemaRepository.findByProjectKey(projectKey);


        externalEntitiesDTOs.forEach( entity-> {
            boolean notExists = existingExternalSchemas.stream().noneMatch(externalSchema -> externalSchema.getSchemaKey().equals(entity.getSchemaKey()));
            if (notExists) {
                throw messageHelper.createElementNotFoundException(EXTERNAL_SCHEMA, entity.getSchemaKey());
            }
        });



        final List<String> existingExternalEntitiesKeys = getRepository().findByProjectKey(projectKey).stream().map(ExternalEntity::getEntityKey).collect(Collectors.toList());

        externalEntitiesDTOs.stream()
                .map(ExternalEntityDTO::getSchemaKey)
                .filter(existingExternalEntitiesKeys::contains)
                .findAny()
                .ifPresent(key -> {throw messageHelper.createObjectAlreadyExistException(EXTERNAL_ENTITY, key);});

        final List<ExternalEntity> models = mapper.toModel(projectKey, externalEntitiesDTOs);
        completeModelBeforeSave(models,existingExternalSchemas);


        SaveElementsResponse ret = super.getRepository().saveElements(models);

        SaveElementsResponseDTO dto = new SaveElementsResponseDTO();
        dto.setSavedElementsCount(ret.getSavedElementsCount());
        return dto;

    }

    @Override
    public ExternalEntityDTO update(String projectKey, String externalSchemaKey, ExternalEntityDTO externalEntityDTO) {
        final ExternalEntity existingExternalEntity = super.getRepository().getByKey(projectKey, externalSchemaKey, externalEntityDTO.getEntityKey());
        if (existingExternalEntity == null) {
            throw messageHelper.createElementNotFoundException(EXTERNAL_ENTITY, externalEntityDTO.getEntityKey());
        }
        final ExternalSchema existingExternalSchema = externalSchemaRepository.getByKey(projectKey, externalSchemaKey);
        if (existingExternalSchema == null) {
            throw messageHelper.createElementNotFoundException(EXTERNAL_SCHEMA, externalEntityDTO.getSchemaKey());
        }
        final ExternalEntity updatedModel = mapper.toModel(projectKey, externalEntityDTO);
        updatedModel.setAssignedAttributeSerializationIDs(existingExternalEntity.getAssignedAttributeSerializationIDs());
        this.serializationIDAssigner.autoAssignAttributesSerializationIDs(updatedModel);
        if (this.serializationIDAssigner.autoAssignSerializationID(existingExternalSchema, updatedModel)) {
            externalSchemaRepository.save(existingExternalSchema);
        }

        final ExternalEntity savedModel = saveModel(updatedModel, true);
        return mapper.toDTO(savedModel);
    }

    @Override
    public void delete(String projectKey, String key) {
    }

    @Override
    public List<ExternalEntityDTO> list(String projectKey, String externalSchemaKey) {
        List<ExternalEntity> externalEntityList = super.getRepository().findByExternalSchemaKey(projectKey, externalSchemaKey);
        return externalEntityList == null || externalEntityList.isEmpty() ? new ArrayList<>() : externalEntityList.stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ExternalEntityDTO save(String projectKey, ExternalEntityDTO s) {
        return save(projectKey, s.getSchemaKey(), s);
    }

    @Override
    public ExternalEntityDTO get(String projectKey, String schemaKey, String entityKey) {
        if (StringUtils.isEmpty(entityKey)) {
            throw messageHelper.createIDNotSetException(ExternalEntity.class.getSimpleName(), entityKey, " Entity key can't be empty");
        }
        if (StringUtils.isEmpty(schemaKey)) {
            throw messageHelper.createIDNotSetException(ExternalEntity.class.getSimpleName(), schemaKey, " Schema Key can't be empty");
        }
        ExternalEntity returnedExternalEntity;
        try {
            returnedExternalEntity = super.getRepository().getByKey(projectKey, schemaKey, entityKey);
        } catch (final AiaApiException e) {
            throw messageHelper.createUnexpectedOperationErrorException("get External Entity", e);
        }
        if (returnedExternalEntity == null) {
            throw messageHelper.createObjectDoesNotExistException("get External Entity", EXTERNAL_ENTITY, entityKey);
        }
        return mapper.toDTO(returnedExternalEntity);
    }

    public void getByKey(String projectKey, String externalSchemaKey,  String entityKey) {
        ExternalEntity externalEntity = super.getRepository().getByKey(projectKey, externalSchemaKey, entityKey);
        if (externalEntity != null) {
            throw messageHelper.createObjectAlreadyExistException(externalEntity.getElementType(), externalEntity.getEntityKey());
        }
    }

    //shared
    private AttributeStore convertExternalAttributeToAttributeStore(ExternalAttribute externalAttribute) {
        AttributeStore attributeStore = new AttributeStore();

        attributeStore.setLogicalAttributeKey(externalAttribute.getAttributeKey());
        attributeStore.setName(externalAttribute.getName());
        attributeStore.setType(externalAttribute.getDatatype());
        attributeStore.setIsLogicalTime(externalAttribute.isLogicalTime());
        attributeStore.setIsUpdateTime(externalAttribute.isUpdateTime());
        attributeStore.setIsRequired(externalAttribute.isRequired());
        attributeStore.setKeyPosition(externalAttribute.getKeyPosition());

        return attributeStore;
    }

    private LogicalAttribute convertExternalAttributeToLogicalAttribute(ExternalAttribute externalAttribute) {
        LogicalAttribute logicalAttribute = new LogicalAttribute();

        logicalAttribute.setAttributeKey(externalAttribute.getAttributeKey());
        logicalAttribute.setName(externalAttribute.getName());
        logicalAttribute.setDescription(externalAttribute.getDescription());
        logicalAttribute.setOrigin(externalAttribute.getOrigin());
        logicalAttribute.setIsLogicalTime(externalAttribute.isLogicalTime());
        logicalAttribute.setKeyPosition(externalAttribute.getKeyPosition());
        logicalAttribute.setDatatype(Datatype.forPrimitive(PrimitiveDatatype.valueOf(externalAttribute.getDatatype()), Collections.emptyMap()));

        return logicalAttribute;
    }

    public LogicalEntity createLogicalEntityFromExternalEntity(ExternalEntity externalEntity, String subjectAreaKey) {
        LogicalEntity logicalEntityForExternalEntity = new LogicalEntity();
        logicalEntityForExternalEntity.setSchemaKey(subjectAreaKey);
        logicalEntityForExternalEntity.setProjectKey(externalEntity.getProjectKey());
        logicalEntityForExternalEntity.setAttributes(getAttributes(externalEntity.getAttributes()));
        logicalEntityForExternalEntity.setName(externalEntity.getName());
        logicalEntityForExternalEntity.setEntityKey(externalEntity.getEntityKey());
        logicalEntityForExternalEntity.setDescription(externalEntity.getDescription());
        logicalEntityForExternalEntity.setProductKey(ModelConstants.SHARED_PRODUCT_KEY);
        logicalEntityForExternalEntity.setSourceElementId(null);
        logicalEntityForExternalEntity.setEntityType(externalEntity.getIsTransaction() ? LogicalEntityType.TRANSACTION.getKey()
                : LogicalEntityType.MASTER.getKey());
        logicalEntityForExternalEntity.setOrigin(externalEntity.getOrigin());
        logicalEntityForExternalEntity.setOriginProcess(externalEntity.getOriginProcess());
        return logicalEntityForExternalEntity;
    }

    private List<LogicalAttribute> getAttributes(List<ExternalAttribute> attributes) {
        if (attributes == null) {
            return Collections.emptyList();
        }
        final Set<LogicalAttribute> logicalAttributes = new HashSet<>(attributes.size());
        for (final ExternalAttribute externalAttribute : attributes) {
            LogicalAttribute logicalAttribute = new LogicalAttribute();
            logicalAttribute.setAttributeKey(externalAttribute.getAttributeKey());
            logicalAttribute.setName(externalAttribute.getName());
            logicalAttribute.setDescription(externalAttribute.getDescription());
            logicalAttribute.setOrigin(externalAttribute.getOrigin());
            logicalAttribute.setIsLogicalTime(externalAttribute.isLogicalTime());
            logicalAttribute.setKeyPosition(externalAttribute.getKeyPosition());
            logicalAttribute.setDatatype(Datatype.forPrimitive(PrimitiveDatatype.valueOf(externalAttribute.getDatatype()), Collections.emptyMap()));

            logicalAttributes.add(logicalAttribute);
        }
        return logicalAttributes.stream().collect(Collectors.toList());
    }

}