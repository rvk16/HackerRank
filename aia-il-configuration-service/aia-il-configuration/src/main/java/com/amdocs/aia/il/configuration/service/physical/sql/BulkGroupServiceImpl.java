package com.amdocs.aia.il.configuration.service.physical.sql;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.repo.SaveElementsResponse;
import com.amdocs.aia.common.model.repo.ValidationIssueDTO;
import com.amdocs.aia.common.model.repo.ValidationResponseDTO;
import com.amdocs.aia.common.model.repo.ValidationStatus;
import com.amdocs.aia.il.common.model.EntityConfigurationUtils;
import com.amdocs.aia.il.common.model.configuration.transformation.Context;
import com.amdocs.aia.il.common.model.bulk.BulkGroup;
import com.amdocs.aia.il.configuration.dependencies.BulkGroupDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.BulkGroupDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.dto.SetFiltersRequestDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.mapper.ModelDtoMapper;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.BulkGroupRepository;
import com.amdocs.aia.il.configuration.service.AbstractModelService;
import com.amdocs.aia.il.configuration.service.external.ExternalEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BulkGroupServiceImpl extends AbstractModelService<BulkGroup, BulkGroupRepository> implements BulkGroupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkGroupServiceImpl.class);

    private static final String BULK_GROUP_SCHEMA_STORE = "BulkGroup";

    private final BulkGroupDependencyAnalyzer dependencyAnalyzer;
    protected final MessageHelper messageHelper;
    private ModelDtoMapper<BulkGroup, BulkGroupDTO> mapper;
    private final ExternalEntityService externalEntityService;
    @Autowired
    public BulkGroupServiceImpl(BulkGroupRepository repository, BulkGroupDependencyAnalyzer dependencyAnalyzer,
                                MessageHelper messageHelper, ModelDtoMapper<BulkGroup, BulkGroupDTO> bulkGroupMapper, ExternalEntityService externalEntityService) {
        super(repository);
        this.dependencyAnalyzer = dependencyAnalyzer;
        this.messageHelper = messageHelper;
        this.mapper = bulkGroupMapper;
        this.externalEntityService = externalEntityService;
    }

    @Override
    public BulkGroupDTO get(String projectKey, String key) {
        return new BulkGroupDTO();
    }

    @Override
    public List<BulkGroupDTO> list(String projectKey) {
        return super.getRepository().findByProjectKey(projectKey).stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public BulkGroupDTO save(String projectKey, BulkGroupDTO s) {
        LOGGER.info("Check if exists bulk group By Key: projectKey {} SchemaKey {} BulkGroupKey {} repository {}", projectKey, s.getSchemaKey(), s.getBulkGroupKey(), super.getRepository() != null);
        final BulkGroup existingModel = super.getRepository().getByKey(projectKey, s.getSchemaKey(), s.getBulkGroupKey());
        LOGGER.info("Existing bulk group model before save: {}", existingModel);
        doSave(s, existingModel);
        final BulkGroup model = mapper.toModel(projectKey, s);
        LOGGER.info("Complete Model Before Save: {}", model);
        completeModelBeforeSave(model);
        //validateBeforeSave(model);
        LOGGER.info("Save model: {}", model);
        final BulkGroup savedModel = super.getRepository().save(model);
        reportAddEntity(savedModel);
        return mapper.toDTO(savedModel);

    }

    @Override
    public SaveElementsResponseDTO bulkSave(String projectKey, List<BulkGroupDTO> bulkGroupDTOs) {
        LOGGER.debug("Saving Bulk Group Context");
        final List<BulkGroup> existingBulkGroup = getRepository().findByProjectKey(projectKey);
        bulkGroupDTOs.stream()
                .filter(group-> existingBulkGroup.stream().anyMatch(exitingGroup-> group.getBulkGroupKey().equals(exitingGroup.getKey())
                                                                               &&  group.getSchemaKey().equals(exitingGroup.getSchemaKey()) ))
                .findAny()
                .ifPresent(group -> {throw messageHelper.createObjectAlreadyExistException(BULK_GROUP_SCHEMA_STORE, group.getBulkGroupKey());});


       final List<BulkGroup> models = mapper.toModel(projectKey, bulkGroupDTOs);

        completeModelBeforeSave(models);

        SaveElementsResponse ret = super.getRepository().saveElements(models);

        SaveElementsResponseDTO dto = new SaveElementsResponseDTO();
        dto.setSavedElementsCount(ret.getSavedElementsCount());
        return dto;
    }

    @Override
    public SetFiltersRequestDTO updateFilters(String projectKey, String schemaStoreKey, String bulkGroupKey, SetFiltersRequestDTO setFiltersRequest) {

        SetFiltersRequestDTO rsp = new SetFiltersRequestDTO();
        List<ExternalEntityDTO> externalEntitiesDTOList = new ArrayList<>();
        setFiltersRequest.getEntities().stream().forEach( entity -> {
            externalEntitiesDTOList.add(externalEntityService.update(projectKey, schemaStoreKey, entity));
        });

        LOGGER.info("Updated successfully {} entities with filters",externalEntitiesDTOList.size());
        BulkGroupDTO bulkGroupDTO = setFiltersRequest.getBulkGroup();
        LOGGER.info("Check if exists bulk group By Key: projectKey {} SchemaKey {} BulkGroupKey {} repository {}",
                projectKey, bulkGroupDTO.getSchemaKey(), bulkGroupDTO.getBulkGroupKey(), super.getRepository() != null);
        final BulkGroup existingModel = super.getRepository().getByKey(projectKey, bulkGroupDTO.getSchemaKey(), bulkGroupDTO.getBulkGroupKey());
        LOGGER.info("Existing bulk group model before save: {}", existingModel);

        BulkGroupDTO updatedBulkGroup = existingModel != null? update(projectKey,schemaStoreKey,setFiltersRequest.getBulkGroup()): save(projectKey,setFiltersRequest.getBulkGroup());

        rsp.setEntities(externalEntitiesDTOList);
        rsp.setBulkGroup(updatedBulkGroup);
        return rsp;
    }


    protected void reportAddEntity(final BulkGroup savedModel) {
        // will be implemented later
    }

    public BulkGroupDTO update(String projectKey, String schemaStoreKey, BulkGroupDTO s) {
        final BulkGroup existingModel = super.getRepository().getByKey(projectKey, schemaStoreKey, s.getBulkGroupKey());
        doUpdate(projectKey, schemaStoreKey, s, existingModel);
        final BulkGroup updatedModel = mapper.toModel(projectKey, s);
        completeModelBeforeSave(updatedModel);
        //validateBeforeUpdate(updatedModel);
        final BulkGroup savedModel = super.getRepository().save(updatedModel);
        return mapper.toDTO(savedModel);
    }

    private void doUpdate(String projectKey, String schemaStoreKey, BulkGroupDTO model, BulkGroup existingModel) {
        if (model == null) {
            throw messageHelper.createElementNotFoundException(Context.ELEMENT_TYPE, schemaStoreKey);
        }
    }

    @Override
    public void delete(String projectKey, String key) {
        // will be implemented later
    }


    @Override
    public void delete(String projectKey, String schemaStoreKey, String bulkGroupKey) {
        BulkGroup model = super.getRepository().getByKey(projectKey, schemaStoreKey, bulkGroupKey);
        if (model != null) {
            validateOnDelete(model);
            doDelete(model);
        }
    }

    @Override
    public List<BulkGroupDTO> list(String projectKey, String schemaStoreKey) {
        final List<BulkGroup> bulkGroupsList = super.getRepository().findByProjectKeyAndSchemaStoreKey(projectKey, schemaStoreKey);
        return bulkGroupsList == null || bulkGroupsList.isEmpty() ? new ArrayList<>() : bulkGroupsList.stream().map(mapper::toDTO).collect(Collectors.toList());

    }

    @Override
    public BulkGroupDTO get(String projectKey, String schemaStoreKey, String bulkGroupKey) {

        if (StringUtils.isEmpty(schemaStoreKey)) {
            throw messageHelper.createIDNotSetException(BulkGroup.class.getSimpleName(), schemaStoreKey, "bulkGroup Logical Schema key doesn't exist");
        }
        if (StringUtils.isEmpty(bulkGroupKey)) {
            throw messageHelper.createIDNotSetException(BulkGroup.class.getSimpleName(), bulkGroupKey, "bulkGroup Logical Entity key doesn't exist");
        }

        BulkGroup bulkGroup;
        try {
            bulkGroup = super.getRepository().getByKey(projectKey, schemaStoreKey, bulkGroupKey);
        } catch (final AiaApiException e) {
            throw getMessageHelper().createUnexpectedOperationErrorException("get BulkGroup", e);
        }
        if (bulkGroup == null) {
            throw getMessageHelper().createObjectDoesNotExistException("get BulkGroup", BULK_GROUP_SCHEMA_STORE, bulkGroupKey);
        }

        return mapper.toDTO(bulkGroup);
    }

    protected BulkGroup doGet(final String projectKey, final String schemaKey, final String bulkGroupKey, final String operation) {
        final BulkGroup saved = super.getRepository().getByKey(projectKey, schemaKey, bulkGroupKey);
        if (saved == null) {
            throw messageHelper.createElementsNotFoundException(BulkGroup.class.getSimpleName(), bulkGroupKey);
        }

        return saved;
    }

    public void doSave(BulkGroupDTO s, BulkGroup existingModel) {
        if (existingModel != null) {
            if (existingModel.getKey() != null) {
                throw messageHelper.createObjectAlreadyExistException(BULK_GROUP_SCHEMA_STORE, existingModel.getKey());
            }
            if (isBulkGroupKeyExistInSchema(existingModel)) {
                throw messageHelper.createObjectAlreadyExistException(BULK_GROUP_SCHEMA_STORE, existingModel.getKey());
            }
        }
    }

    protected static String formatValidationIssues(final List<ValidationIssueDTO> issues) {
        List<String> messages = issues.stream()
                .map(ValidationIssueDTO::getUserMessage)
                .collect(Collectors.toList());
        return String.join("\n", messages);
    }

    protected MessageHelper getMessageHelper() {
        return messageHelper;
    }

    public boolean isBulkGroupKeyExistInSchema(BulkGroup bulkGroup) {
        return super.getRepository().getByKey(bulkGroup.getProjectKey(), bulkGroup.getSchemaKey(), bulkGroup.getKey()) != null;
    }

    protected void completeModelBeforeSave(final BulkGroup model) {
        model.setId(EntityConfigurationUtils.getElementId(model));
        model.setDependencies(dependencyAnalyzer.getDependencies(model));
        model.setPublicFeatures(dependencyAnalyzer.getPublicFeatures(model));
    }

    protected void completeModelBeforeSave(final List<BulkGroup> models) {
        models.forEach(this::completeModelBeforeSave);
    }

    protected void validateOnDelete(final BulkGroup m) {
        ValidationResponseDTO validationResponse = super.getRepository().validateBeforeDelete(m.getId());
        if (validationResponse.getStatus() == ValidationStatus.ERROR) {
            throw new ApiException(
                    AiaApiException.AiaApiHttpCodes.CONFLICT,
                    AiaApiMessages.GENERAL.CANNOT_DELETE_ELEMENT,
                    m.getName(),
                    formatValidationIssues(validationResponse.getIssues()));
        }
    }

    protected void doDelete(BulkGroup model) {
        super.getRepository().delete(model.getId());
    }

    public void setMapper(ModelDtoMapper<BulkGroup, BulkGroupDTO> mapper) {
        this.mapper = mapper;
    }
}