package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.repo.SaveElementsResponse;
import com.amdocs.aia.common.model.repo.ValidationIssueDTO;
import com.amdocs.aia.common.model.repo.ValidationResponseDTO;
import com.amdocs.aia.common.model.repo.ValidationStatus;
import com.amdocs.aia.il.common.model.configuration.ConfigurationUtils;
import com.amdocs.aia.il.common.model.configuration.tables.AbstractPublisherConfigurationModel;
import com.amdocs.aia.il.configuration.dependencies.AbstractConfigurationModelDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.CommonModelDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.mapper.ModelDtoMapper;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.AbstractConfigurationRepository;
import com.amdocs.aia.il.configuration.shared.AiaSharedProxy;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractConfigurationService<M extends AbstractPublisherConfigurationModel,
        D extends CommonModelDTO,
        R extends AbstractConfigurationRepository<M>,
        A extends AbstractConfigurationModelDependencyAnalyzer<M>>
        implements ConfigurationService<D> {

    private final R repository;
    private final A dependencyAnalyzer;
    private ModelDtoMapper<M, D> mapper;
    protected final MessageHelper messageHelper;
    protected AiaSharedProxy aiaSharedProxy;

    public AbstractConfigurationService(R repository, A dependencyAnalyzer, ModelDtoMapper<M, D> mapper, MessageHelper messageHelper, AiaSharedProxy aiaSharedProxy) {
        this.repository = repository;
        this.dependencyAnalyzer = dependencyAnalyzer;
        this.mapper = mapper;
        this.messageHelper = messageHelper;
        this.aiaSharedProxy = aiaSharedProxy;
    }

    @Override
    public D save(String projectKey, D s) {
        final M existingModel = getRepository().getByKey(projectKey, s.getStoreName());
        doSave(projectKey, s, existingModel);
        final M model = getMapper().toModel(projectKey, s);
        completeModelBeforeSave(model);
        validateBeforeSave(model);
        final M savedModel = getRepository().save(model);
        reportAddEntity(savedModel);
        return getMapper().toDTO(savedModel);
    }

    public SaveElementsResponseDTO bulkSave(String projectKey, List<D> s) {
        doSave(projectKey,s);
        final List<M> models = getMapper().toModel(projectKey, s);
        completeModelBeforeSave(models);

        SaveElementsResponse ret = getRepository().saveElements(models);

        SaveElementsResponseDTO dto = new SaveElementsResponseDTO();
        dto.setSavedElementsCount(ret.getSavedElementsCount());
        return dto;

    }


   /* public D saveBulk(String projectKey, List<D> s) {
        final List<M> existingModels = getRepository().findByProjectKey(projectKey);
        s.forEach(model->{

            Optional<M> existingModel = existingModels.stream().filter(existingModel -> existingModel.getKey().equals(model.getStoreName())).findAny();

        doSave(projectKey, model, existingModel.orElse(null));
        final M model = getMapper().toModel(projectKey, model);
        completeModelBeforeSave(model);
        validateBeforeSave(model);
        final M savedModel = getRepository().save(model);
        reportAddEntity(savedModel);
        return getMapper().toDTO(savedModel);

        });
    }
*/

    @Override
    public D update(String projectKey, String key, D s) {
        final M existingModel = getRepository().getByKey(projectKey, key);
        doUpdate(projectKey, key, s, existingModel);
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
        model.setId(ConfigurationUtils.getElementId(model));
        model.setDependencies(getDependencyAnalyzer().getDependencies(model));
        model.setPublicFeatures(getDependencyAnalyzer().getPublicFeatures(model));
    }

    protected void completeModelBeforeSave(final List<M> models) {
        models.forEach(model -> {
        model.setId(ConfigurationUtils.getElementId(model));
        model.setDependencies(getDependencyAnalyzer().getDependencies(model));
        model.setPublicFeatures(getDependencyAnalyzer().getPublicFeatures(model));
        });
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


    protected static void checkValidationResponseBeforeSave(final AbstractPublisherConfigurationModel publisherConfigurationModel,
                                                            final ValidationResponseDTO validationResponse) {
        if (validationResponse.getStatus().equals(ValidationStatus.ERROR)) {
            final List<ValidationIssueDTO> relevantIssues = AbstractConfigurationService.extractRelevantIssues(validationResponse);
            if (!relevantIssues.isEmpty()) {
                throw new ApiException(
                        AiaApiException.AiaApiHttpCodes.CONFLICT,
                        AiaApiMessages.GENERAL.CANNOT_SAVE_ELEMENT,
                        publisherConfigurationModel.getName(),
                        formatValidationIssues(relevantIssues));
            }
        }
    }


    protected abstract void doSave(String projectKey, D s, M existingModel);
    protected abstract void doSave(String projectKey, List<D> s);

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

    protected abstract Class<? extends AbstractPublisherConfigurationModel> getModelClass();

}
