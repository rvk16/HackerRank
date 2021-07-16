package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.logical.LogicalEntity;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.SaveElementsResponse;
import com.amdocs.aia.common.model.repo.ValidationIssueDTO;
import com.amdocs.aia.common.model.repo.ValidationResponseDTO;
import com.amdocs.aia.common.model.repo.ValidationStatus;
import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;
import com.amdocs.aia.il.configuration.dependencies.AbstractEntityReferentialIntegrityDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.EntityReferentialIntegrityDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.mapper.ModelDtoMapper;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.EntityReferentialIntegrityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntityReferentialIntegrityServiceImpl implements EntityReferentialIntegrityService{
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityReferentialIntegrityServiceImpl.class);
    private final EntityReferentialIntegrityRepository repository;
    private final AbstractEntityReferentialIntegrityDependencyAnalyzer dependencyAnalyzer;
    protected final MessageHelper messageHelper;
    private ModelDtoMapper<EntityReferentialIntegrity, EntityReferentialIntegrityDTO> mapper;

    @Autowired
    public EntityReferentialIntegrityServiceImpl(EntityReferentialIntegrityRepository repository, AbstractEntityReferentialIntegrityDependencyAnalyzer dependencyAnalyzer,
                                                 MessageHelper messageHelper,ModelDtoMapper<EntityReferentialIntegrity, EntityReferentialIntegrityDTO> mapper) {
        this.repository = repository;
        this.dependencyAnalyzer = dependencyAnalyzer;
        this.messageHelper = messageHelper;
        this.mapper = mapper;
    }

    @Override
    public Optional<EntityReferentialIntegrity> get(String projectKey, String logicalSchemaKey, String logicalEntityKey) {
        if (StringUtils.isEmpty(logicalSchemaKey)) {
            throw messageHelper.createIDNotSetException(EntityReferentialIntegrity.class.getSimpleName(),logicalSchemaKey,"EntityReferentialIntegrity Logical Schema key doesn't exist");
        }
        if (StringUtils.isEmpty(logicalEntityKey)) {
            throw messageHelper.createIDNotSetException(EntityReferentialIntegrity.class.getSimpleName(),logicalEntityKey,"EntityReferentialIntegrity Logical Entity key doesn't exist");
        }
        return Optional.ofNullable(repository.findByProjectKeyAndLogicalSchemaKeyAndLogicalEntityKey(projectKey,
            logicalSchemaKey, logicalEntityKey));
    }

    @Override
    public EntityReferentialIntegrity createOrUpdate(EntityReferentialIntegrity model) {
        final EntityReferentialIntegrity existingModel = repository.findByProjectKeyAndLogicalSchemaKeyAndLogicalEntityKey(model.getProjectKey(), model.getLogicalSchemaKey(), model.getLogicalEntityKey());
        if (existingModel != null && existingModel.getId() != null) {
            return doUpdate(model, existingModel);
        }
        if(model.getRelations() == null || model.getRelations().isEmpty()){
            return model;
        }
        return doSave(model);
    }

    @Override
    public EntityReferentialIntegrityDTO save(String projectKey, EntityReferentialIntegrityDTO entityReferentialIntegrityDTO) {

        return mapper.toDTO(createOrUpdate(mapper.toModel(projectKey,entityReferentialIntegrityDTO)));
    }

    @Override
    public SaveElementsResponseDTO bulkSave(String projectKey, List<EntityReferentialIntegrityDTO> entitiesReferentialIntegrity) {
        LOGGER.debug("Saving Entity Referential Integrity Context");
        final List<EntityReferentialIntegrity> existingEntities = repository.findByProjectKey(projectKey);

        List<EntityReferentialIntegrity> entitiesModelsToUpdate = new ArrayList<>();
        entitiesReferentialIntegrity.stream()
                .filter(entity -> existingEntities.stream().anyMatch(exitingEntity -> entity.getSchemaStoreKey().equals(exitingEntity.getLogicalSchemaKey())
                        && entity.getEntityStoreKey().equals(exitingEntity.getLogicalEntityKey())))
                .map(entityDTO -> mapper.toModel(projectKey, entityDTO))
                .forEach(entity -> {
                    if (entity.getRelations() == null || entity.getRelations().isEmpty()) {
                        entity.clearDependencies();
                        repository.delete(entity.getId());//TODO: add bulk delete to repo
                    } else {
                        entitiesModelsToUpdate.add(entity);
                    }

                });


        List<EntityReferentialIntegrityDTO> entitiesToCreate = entitiesReferentialIntegrity.stream()
                .filter(entity -> existingEntities.stream().noneMatch(exitingEntity -> entity.getSchemaStoreKey().equals(exitingEntity.getLogicalSchemaKey())
                        && entity.getEntityStoreKey().equals(exitingEntity.getLogicalEntityKey()))).collect(Collectors.toList());


        List<EntityReferentialIntegrity> models = mapper.toModel(projectKey, entitiesToCreate);
        models.addAll(entitiesModelsToUpdate);

        models.forEach(model-> model.setDependencies(dependencyAnalyzer.getDependencies(model)));

       // completeModelBeforeSave(projectKey,models); add bulk

        SaveElementsResponse ret = repository.saveElements(models);

        SaveElementsResponseDTO dto = new SaveElementsResponseDTO();
        dto.setSavedElementsCount(ret.getSavedElementsCount());
        return dto;


    }


    private EntityReferentialIntegrity doUpdate( final EntityReferentialIntegrity newModel, final EntityReferentialIntegrity oldModel) {
       if(newModel.getRelations() == null || newModel.getRelations().isEmpty()){
           oldModel.clearDependencies();
           repository.delete(oldModel.getId());
           return oldModel;
       }
       return doSave(newModel);
    }

    private EntityReferentialIntegrity doSave(final EntityReferentialIntegrity model) {
        model.setDependencies(dependencyAnalyzer.getDependencies(model));
        validateBeforeCreateOrUpdate(model);
        final EntityReferentialIntegrity savedModel = repository.save(model);
        return savedModel;
    }

    private void validateBeforeCreateOrUpdate(final EntityReferentialIntegrity model) {
        final ValidationResponseDTO validationResponse = repository.validateBeforeSave(model);
        checkValidationResponseBeforeSave(model, validationResponse);
    }

    private static void checkValidationResponseBeforeSave(final EntityReferentialIntegrity entityReferentialIntegrity,
                                                                     final ValidationResponseDTO validationResponse) {
        if (validationResponse.getStatus().equals(ValidationStatus.ERROR)) {
            final List<ValidationIssueDTO> relevantIssues = AbstractConfigurationService.extractRelevantIssues(validationResponse);
            if (!relevantIssues.isEmpty()) {
                throw new ApiException(
                        AiaApiException.AiaApiHttpCodes.CONFLICT,
                        AiaApiMessages.GENERAL.CANNOT_SAVE_ELEMENT,
                        entityReferentialIntegrity.getName(),
                        formatValidationIssues(relevantIssues));
            }
        }
    }
    protected static String formatValidationIssues(final List<ValidationIssueDTO> issues) {
        List<String> messages = issues.stream()
                .map(ValidationIssueDTO::getUserMessage)
                .collect(Collectors.toList());
        return String.join("\n", messages);
    }
}