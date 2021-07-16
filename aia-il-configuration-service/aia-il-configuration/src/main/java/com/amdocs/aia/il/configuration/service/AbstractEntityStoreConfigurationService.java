package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.repo.ValidationIssueDTO;
import com.amdocs.aia.common.model.repo.ValidationResponseDTO;
import com.amdocs.aia.common.model.repo.ValidationStatus;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerEntityStoreModel;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel;
import com.amdocs.aia.il.common.model.EntityConfigurationUtils;
import com.amdocs.aia.il.common.model.stores.NumericKeyAssignmentPolicy;
import com.amdocs.aia.il.configuration.dependencies.AbstractEntityStoreModelDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.EntityStoreModelDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.mapper.ModelDtoMapper;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.AbstractEntityStoreRepository;
import com.amdocs.aia.il.configuration.repository.AbstractSchemaStoreRepository;
import com.amdocs.aia.il.configuration.shared.AiaSharedProxy;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractEntityStoreConfigurationService<M extends AbstractIntegrationLayerEntityStoreModel,
        S extends AbstractIntegrationLayerSchemaStoreModel,
        D extends EntityStoreModelDTO,
        R extends AbstractEntityStoreRepository<M>, T extends AbstractSchemaStoreRepository<S>,
        A extends AbstractEntityStoreModelDependencyAnalyzer<M>> implements ConfigurationService<D> {

    private final R repository;
    private final A dependencyAnalyzer;
    private ModelDtoMapper<M, D> mapper;
    protected final MessageHelper messageHelper;
    protected AiaSharedProxy aiaSharedProxy;
    private final T sourceRepository;

    public AbstractEntityStoreConfigurationService(R repository, A dependencyAnalyzer, ModelDtoMapper<M, D> mapper, MessageHelper messageHelper, AiaSharedProxy aiaSharedProxy, T sourceRepository) {
        this.repository = repository;
        this.dependencyAnalyzer = dependencyAnalyzer;
        this.mapper = mapper;
        this.messageHelper = messageHelper;
        this.aiaSharedProxy = aiaSharedProxy;
        this.sourceRepository = sourceRepository;
    }

    @Override
    public D save(String projectKey, D s) {
        final M existingModel = getRepository().getByKey(projectKey, s.getEntityStoreKey(), s.getSchemaStoreKey());
        doSave(projectKey, s, existingModel);
        final M model = getMapper().toModel(projectKey, s);
        completeModelBeforeSave(model);
        validateBeforeSave(model);
        completeNumericKeyMap(model);
        final M savedModel = getRepository().save(model);
        reportAddEntity(savedModel);
        return getMapper().toDTO(savedModel);
    }

    @Override
    public D update(String projectKey, String entityStoreKey, D s) {
        final M existingModel = getRepository().getByKey(projectKey, entityStoreKey, s.getSchemaStoreKey());
        doUpdate(projectKey, entityStoreKey, s, existingModel);
        final M updatedModel = getMapper().toModel(projectKey, s);
        completeModelBeforeSave(updatedModel);
        validateBeforeUpdate(updatedModel);
        final M savedModel = getRepository().save(updatedModel);
        return getMapper().toDTO(savedModel);
    }

    protected void validateBeforeUpdate(final M model) {
        final ValidationResponseDTO validationResponse = getRepository().validateBeforeSave(model);
        checkValidationResponseBeforeSave(model, validationResponse);
    }

    protected abstract void doUpdate(String projectKey, String key, D dto, M model);

    @Override
    public void delete(String projectKey, String key) {
    }

    protected abstract void reportAddEntity(M savedModel);

    @Override
    public List<D> list(String projectKey) {
        return getRepository()
                .findByProjectKey(projectKey)
                .stream()
                .map(getMapper()::toDTO)
                .collect(Collectors.toList());
    }

    protected void completeModelBeforeSave(final M model) {
        model.setId(EntityConfigurationUtils.getElementId(model));
        model.setDependencies(getDependencyAnalyzer().getDependencies(model));
        model.setPublicFeatures(getDependencyAnalyzer().getPublicFeatures(model));
    }

    protected void validateBeforeSave(final M model) {
        ValidationResponseDTO validationResponse = getRepository().validateBeforeSave(model);
        checkValidationResponseBeforeSave(model, validationResponse);
    }

    protected static List<ValidationIssueDTO> extractRelevantIssues(final ValidationResponseDTO validationResponse) {
        // by default, all validation issues are relevant
        return validationResponse.getIssues();
    }

    protected static String formatValidationIssues(final List<ValidationIssueDTO> issues) {
        List<String> messages = issues.stream()
                .map(ValidationIssueDTO::getUserMessage)
                .collect(Collectors.toList());
        return String.join("\n", messages);
    }

    protected static void checkValidationResponseBeforeSave(final AbstractIntegrationLayerEntityStoreModel entityStoreModel,
                                                            final ValidationResponseDTO validationResponse) {
        if (validationResponse.getStatus().equals(ValidationStatus.ERROR)) {
            final List<ValidationIssueDTO> relevantIssues = AbstractConfigurationService.extractRelevantIssues(validationResponse);
            if (!relevantIssues.isEmpty()) {
                throw new ApiException(
                        AiaApiException.AiaApiHttpCodes.CONFLICT,
                        AiaApiMessages.GENERAL.CANNOT_SAVE_ELEMENT,
                        entityStoreModel.getName(),
                        formatValidationIssues(relevantIssues));
            }
        }
    }

    private void completeNumericKeyMap(final M model) {
        S sourceSchema = sourceRepository.getByKey(model.getProjectKey(), model.getSchemaStoreKey());
        if (sourceSchema != null) {
            if (NumericKeyAssignmentPolicy.AUTOMATIC.equals(sourceSchema.getNumericKeyAssignmentPolicy())) {
                model.autoAssignNumericKeys();
            }
        } else {
            throw getMessageHelper().createObjectDoesNotExistException("Save entity store", getModelClass().getSimpleName(), model.getSchemaStoreKey());
        }
    }

    protected abstract void doSave(String projectKey, D s, M existingModel);

    public R getRepository() {
        return repository;
    }

    public A getDependencyAnalyzer() {
        return dependencyAnalyzer;
    }

    public ModelDtoMapper<M, D> getMapper() {
        return mapper;
    }

    protected MessageHelper getMessageHelper() {
        return messageHelper;
    }

    public AiaSharedProxy getAiaSharedProxy() {
        return aiaSharedProxy;
    }

    public void setMapper(ModelDtoMapper<M, D> mapper) {
        this.mapper = mapper;
    }

    protected abstract Class<? extends AbstractIntegrationLayerSchemaStoreModel> getModelClass();

    public T getSourceRepository() {
        return sourceRepository;
    }
}