package com.amdocs.aia.il.configuration.service.external;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.OracleTypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.PostgreSqlTypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.ProtoTypeSystem;
import com.amdocs.aia.common.model.logical.LogicalSchema;
import com.amdocs.aia.common.model.logical.LogicalSchemaCategory;
import com.amdocs.aia.common.model.repo.ElementMetadata;
import com.amdocs.aia.common.model.repo.SaveElementsResponse;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.il.common.model.EntityConfigurationUtils;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.bulk.BulkGroup;
import com.amdocs.aia.il.configuration.dependencies.ExternalSchemaDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.CollectionChannelTypeInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaTypeInfoDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.dto.TypeSystemInfoDTO;
import com.amdocs.aia.il.configuration.mapper.ModelDtoMapper;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.external.ExternalSchemaRepository;
import com.amdocs.aia.il.configuration.service.AbstractModelService;
import com.amdocs.aia.il.configuration.shared.AiaSharedProxy;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.PRODUCT_KEY;

@Service
public class ExternalSchemaServiceImpl extends AbstractModelService<ExternalSchema, ExternalSchemaRepository> implements ExternalSchemaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalSchemaServiceImpl.class);

    private static final String EXTERNAL_SCHEMA = "ExternalSchema";
    private static final String EXTERNAL_SCHEMA_TYPE = "ExternalSchemaType";

    private final ExternalSchemaDependencyAnalyzer externalSchemaDependencyAnalyzer;
    private final ModelDtoMapper<ExternalSchema, ExternalSchemaDTO> mapper;
    private final AiaRepositoryOperations aiaRepositoryOperations;
    private final MessageHelper messageHelper;
    private final AiaSharedProxy aiaSharedProxy;
    private final DataChannelStoreType dataChannelStoreType;

    @Autowired
    public ExternalSchemaServiceImpl(ExternalSchemaRepository externalSchemaRepository,
                                     MessageHelper messageHelper, ModelDtoMapper<ExternalSchema, ExternalSchemaDTO> mapper,
                                     AiaRepositoryOperations aiaRepositoryOperations,
                                     ExternalSchemaDependencyAnalyzer externalSchemaDependencyAnalyzer,
                                     AiaSharedProxy aiaSharedProxy,
                                     DataChannelStoreType dataChannelStoreType) {
        super(externalSchemaRepository);
        this.externalSchemaDependencyAnalyzer = externalSchemaDependencyAnalyzer;
        this.messageHelper = messageHelper;
        this.mapper = mapper;
        this.aiaRepositoryOperations = aiaRepositoryOperations;
        this.aiaSharedProxy = aiaSharedProxy;
        this.dataChannelStoreType = dataChannelStoreType;
    }

    @Override
    public ExternalSchemaDTO get(String projectKey, String schemaKey) {
        if (StringUtils.isEmpty(schemaKey)) {
            throw messageHelper.createIDNotSetException(ExternalSchema.class.getSimpleName(), schemaKey, " Schema key can't be empty");
        }
        final ExternalSchema returnedExternalSchema;
        try {
            returnedExternalSchema = getRepository().getByKey(projectKey, schemaKey);
        } catch (final AiaApiException e) {
            throw messageHelper.createUnexpectedOperationErrorException("get External Schema", e);
        }
        if (returnedExternalSchema == null) {
            throw messageHelper.createObjectDoesNotExistException("get External Schema", EXTERNAL_SCHEMA, schemaKey);
        }
        return mapper.toDTO(returnedExternalSchema);
    }

    @Override
    public List<ExternalSchemaDTO> list(String projectKey) {
        return getRepository().findByProjectKey(projectKey).stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ExternalSchemaTypeInfoDTO> listExternalSchemaTypes(String projectKey) {
        return Arrays.stream(ExternalSchemaType.values())
                .filter(ExternalSchemaType::isAuthoringSupported)
                .map(this::createExternalSchemaTypeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExternalSchemaTypeInfoDTO getExternalSchemaType(String projectKey, String externalSchemaType) {
        try {
            return createExternalSchemaTypeDTO(ExternalSchemaType.valueOf(externalSchemaType));
        } catch (IllegalArgumentException e) {
            throw messageHelper.createElementNotFoundException(EXTERNAL_SCHEMA_TYPE, externalSchemaType);
        }
    }

    @Override
    public List<CollectionChannelTypeInfoDTO> listCollectionChannelTypes(String projectKey) {
        return getCollectorTypesInfo(Arrays.asList(CollectorChannelType.values()));
    }

    @Override
    public List<TypeSystemInfoDTO> listTypeSystems(String projectKey) {
        return getTypeSystemsInfo(Arrays.asList(
                LogicalTypeSystem.NAME,
                OracleTypeSystem.NAME,
                PostgreSqlTypeSystem.NAME,
                ProtoTypeSystem.NAME
        ));
    }

    private ExternalSchemaTypeInfoDTO createExternalSchemaTypeDTO(ExternalSchemaType schemaType) {
        return new ExternalSchemaTypeInfoDTO()
                .type(schemaType.name())
                .displayName(messageHelper.format("external.schema.type." + schemaType.name()))
                .nativeDatatype(schemaType.getNativeDatatype())
                .supportedTypeSystems(getTypeSystemsInfo(schemaType.getSupportedTypeSystems()))
                .supportedOngoingChannels(getCollectorTypesInfo(schemaType.getSupportedOngoingChannels()))
                .supportedInitialLoadChannels(getCollectorTypesInfo(schemaType.getSupportedInitialLoadChannels()))
                .supportedReplayChannels(getCollectorTypesInfo(schemaType.getSupportedReplayChannels()))
                .storeType(ExternalSchemaTypeInfoDTO.StoreTypeEnum.fromValue(schemaType.getStoreType().toUpperCase()));
    }

    private List<CollectionChannelTypeInfoDTO> getCollectorTypesInfo(List<CollectorChannelType> channelTypes) {
        return channelTypes.stream()
                .map(channelType -> new CollectionChannelTypeInfoDTO()
                        .channelType(channelType.name())
                        .displayName(messageHelper.format("collector.channel.type." + channelType)))
                .collect(Collectors.toList());

    }

    private List<TypeSystemInfoDTO> getTypeSystemsInfo(List<String> typeSystems) {
        return typeSystems.stream()
                .map(typeSystem -> new TypeSystemInfoDTO()
                        .typeSystem(typeSystem)
                        .displayName(messageHelper.format("type.system." + typeSystem)))
                .collect(Collectors.toList());
    }

    @Override
    public ExternalSchemaDTO update(String projectKey, String key, ExternalSchemaDTO externalSchemaDTO) {
        final ExternalSchema existingExternalSchema = getRepository().getByKey(projectKey, externalSchemaDTO.getSchemaKey());
        if (existingExternalSchema == null) {
            throw messageHelper.createElementNotFoundException(EXTERNAL_SCHEMA, externalSchemaDTO.getSchemaKey());
        }
        final ExternalSchema updatedModel = mapper.toModel(projectKey, externalSchemaDTO);
        final ExternalSchema savedModel = saveModel(updatedModel, true);
        return mapper.toDTO(savedModel);
    }

    @Override
    public void delete(String projectKey, String externalSchemaKey) {
        ExternalSchema externalSchema = getRepository().getByKey(projectKey, externalSchemaKey);
        String query = String.format("%s:%s AND %s:%s AND (%s:%s OR %s:%s OR %s:%s ) AND schemaKey:%s",
                ElementMetadata.FIELD_PROJECT_KEY, projectKey,
                ElementMetadata.FIELD_ELEMENT_PRODUCT_KEY, PRODUCT_KEY,
                ElementMetadata.FIELD_ELEMENT_TYPE, ExternalSchema.class.getSimpleName(),
                ElementMetadata.FIELD_ELEMENT_TYPE, ExternalEntity.class.getSimpleName(),
                ElementMetadata.FIELD_ELEMENT_TYPE, BulkGroup.class.getSimpleName(),
                externalSchemaKey);

        if (externalSchema == null) {
            throw messageHelper.createElementNotFoundException(EXTERNAL_SCHEMA, externalSchemaKey);
        }
        Set<String> elementsIds = aiaRepositoryOperations.findElementsMetadataByQuery(query).stream().map(ElementMetadata::getElementId).collect(Collectors.toSet());
        validateBeforeDelete(externalSchema, elementsIds);
        aiaRepositoryOperations.deleteByQuery(query);
    }

    @Override
    public SaveElementsResponseDTO bulkSave(String projectKey, List<ExternalSchemaDTO> externalSchemaDTOs) {
        LOGGER.debug("Saving Bulk External Schemas");
        final List<String> existingExternalSchemasKeys = getRepository().findByProjectKey(projectKey).stream().map(ExternalSchema::getSchemaKey).collect(Collectors.toList());

        externalSchemaDTOs.stream()
                .map(ExternalSchemaDTO::getSchemaKey)
                .filter(existingExternalSchemasKeys::contains)
                .findAny()
                .ifPresent(key -> {throw messageHelper.createObjectAlreadyExistException(EXTERNAL_SCHEMA, key);});

        final List<ExternalSchema> models = mapper.toModel(projectKey, externalSchemaDTOs);
        completeModelBeforeSave(models);
        SaveElementsResponse ret = super.getRepository().saveElements(models);

        SaveElementsResponseDTO dto = new SaveElementsResponseDTO();
        dto.setSavedElementsCount(ret.getSavedElementsCount());
        return dto;


    }

    @Override
    public ExternalSchemaDTO save(String projectKey, ExternalSchemaDTO externalSchemaDTO) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Check if exists external Schema By Key: projectKey {} SchemaKey {}  repository {}", projectKey, externalSchemaDTO.getSchemaKey(), getRepository() != null);
        }
        final ExternalSchema existingExternalSchema = getRepository().getByKey(projectKey, externalSchemaDTO.getSchemaKey());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Existing External schema model before save: {}", existingExternalSchema);
        }
        doSave(existingExternalSchema);
        final ExternalSchema model = mapper.toModel(projectKey, externalSchemaDTO);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Complete Model Before Save: {}", model);
        }

        final ExternalSchema savedModel = saveModel(model, true);
        return mapper.toDTO(savedModel);
    }

    public ExternalSchema saveModel(ExternalSchema model, boolean validate) {
        completeModelBeforeSave(model);
        if (validate)
        {
            validateBeforeSave(model);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Save model: {}", model);
        }
        return getRepository().save(model);
    }

    private void completeModelBeforeSave(ExternalSchema model) {
        if(Availability.SHARED.equals(model.getAvailability())){
            if (StringUtils.isEmpty(model.getSubjectAreaKey())) {
                throw messageHelper.createSubjectAreaKeyNotSetException(ExternalSchema.class.getSimpleName(), model.getSubjectAreaKey(), " Subject Area Key can't be empty");
            }

            final String schemaStoreKey = dataChannelStoreType.generateSchemaStoreKeyForLogical(model.getSubjectAreaKey());
            Optional<SchemaStore> schemaStore = aiaSharedProxy.searchSchemaStoreBySchemaKey(model.getProjectKey(), schemaStoreKey);

            if (!schemaStore.isPresent()){
                //create logical schema and schema store
                LogicalSchema logicalSchema = createLogicalSchema(model);
                SchemaStore schemaStore1 = dataChannelStoreType.createSchemaStoreFromLogical(logicalSchema, logicalSchema.getSchemaKey());

                Optional<LogicalSchema> logicalSchemaOptional = aiaSharedProxy.searchLogicalSchemaBySchemaKey(logicalSchema.getProjectKey(), logicalSchema.getSchemaKey());
                if(!logicalSchemaOptional.isPresent()) {
                    aiaSharedProxy.reportAdd(logicalSchema);
                }
                aiaSharedProxy.reportAdd(schemaStore1);
            }
        }

        model.setDependencies(externalSchemaDependencyAnalyzer.getDependencies(model));
        model.setPublicFeatures(externalSchemaDependencyAnalyzer.getPublicFeatures(model));
        model.setCreatedBy(aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy());
        model.setCreatedAt(System.currentTimeMillis());
        model.setId(EntityConfigurationUtils.getElementId(model));
    }

    private void completeModelBeforeSave(List<ExternalSchema> models) {
        final String user = aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy();
        final long currentTime = System.currentTimeMillis();
        models.forEach(model -> {
            model.setCreatedBy(user);
            model.setCreatedAt(currentTime);
            model.setId(EntityConfigurationUtils.getElementId(model));
        });
    }


    protected void doSave(ExternalSchema existingExternalSchema) {
        if (existingExternalSchema != null) {
            throw messageHelper.createObjectAlreadyExistException(EXTERNAL_SCHEMA, existingExternalSchema.getSchemaKey());
        }
    }

    public void getByKey(String projectKey, String externalSchemaKey) {
        ExternalSchema externalSchema = getRepository().getByKey(projectKey, externalSchemaKey);
        if (externalSchema != null) {
            throw messageHelper.createObjectAlreadyExistException(externalSchema.getElementType(), externalSchema.getSchemaKey());
        }
    }

    //shared
    private LogicalSchema createLogicalSchema(ExternalSchema externalSchema) {
        LogicalSchema logicalSchema = new LogicalSchema();
        logicalSchema.setSchemaKey(externalSchema.getSubjectAreaKey());
        logicalSchema.setName(externalSchema.getSubjectAreaName());
        logicalSchema.setDescription(externalSchema.getDescription());
        logicalSchema.setProjectKey(externalSchema.getProjectKey());
        logicalSchema.setProductKey(ModelConstants.SHARED_PRODUCT_KEY);
        logicalSchema.setCategory(LogicalSchemaCategory.SHARED);

        return logicalSchema;
    }
}