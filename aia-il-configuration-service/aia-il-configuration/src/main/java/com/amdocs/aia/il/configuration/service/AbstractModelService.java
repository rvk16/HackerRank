package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.repo.ValidationIssueDTO;
import com.amdocs.aia.common.model.repo.ValidationResponseDTO;
import com.amdocs.aia.common.model.repo.ValidationStatus;
import com.amdocs.aia.il.common.model.configuration.ConfigurationUtils;
import com.amdocs.aia.il.common.model.external.AbstractConfigurationModel;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.repo.client.AiaProjectElementRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractModelService<M extends AbstractConfigurationModel,
        R extends AiaProjectElementRepository<M>> {

    private final R repository;
    private static final String ELEMENT_ID = "elementId";

    public AbstractModelService(R repository) {
        this.repository = repository;
    }

    protected void validateBeforeSave(final M model) {
        final ValidationResponseDTO validationResponse = getRepository().validateBeforeSave(model);
        checkValidationResponseBeforeSave(model, validationResponse);
    }


    protected void validateBeforeDelete(final M model) {
        final ValidationResponseDTO validationResponse = getRepository().validateBeforeDelete(model.getId());
        checkValidationResponseBeforeDelete(model, validationResponse);
    }

    protected void validateBeforeDelete(final AbstractConfigurationModel model,Set<String> elementsToDelete) {
        List<ValidationIssueDTO> relevantIssues = elementsToDelete.stream()
                .map(getRepository()::validateBeforeDelete)
                .flatMap(validationResponseDTO -> validationResponseDTO.getIssues().stream())
                .filter(validationIssueDTO -> validationIssueDTO.getStatus().equals(ValidationStatus.ERROR))
                .filter(issue -> !ConfigurationUtils.nullSafeContains(elementsToDelete,extractDependentElementId(issue)))
                .collect(Collectors.toList());
        if (!relevantIssues.isEmpty()){
            throwValidationBeforeDeleteException(model, relevantIssues);
        }

    }

    private String extractDependentElementId(ValidationIssueDTO issue) {
        String res = null;
        if (issue.getParams() != null) {
            Object requiredElementObject = issue.getParams().get("dependentElement");
            if (requiredElementObject instanceof Map && ((Map) requiredElementObject).containsKey(ELEMENT_ID)) {
                res = ((Map) requiredElementObject).get(ELEMENT_ID).toString();
            }
        }
        return res;
    }



    protected static void checkValidationResponseBeforeSave(final AbstractConfigurationModel abstractConfigurationModel,
                                                            final ValidationResponseDTO validationResponse) {
        if (validationResponse.getStatus().equals(ValidationStatus.ERROR)) {
            final List<ValidationIssueDTO> relevantIssues = AbstractConfigurationService.extractRelevantIssues(validationResponse);
            if (!relevantIssues.isEmpty()) {
                throw new ApiException(
                        AiaApiException.AiaApiHttpCodes.CONFLICT,
                        AiaApiMessages.GENERAL.CANNOT_SAVE_ELEMENT,
                        abstractConfigurationModel.getName(),
                        formatValidationIssues(relevantIssues));
            }
        }
    }

    protected static void checkValidationResponseBeforeDelete(final AbstractConfigurationModel abstractConfigurationModel,
                                                              final ValidationResponseDTO validationResponse) {
        if (validationResponse.getStatus().equals(ValidationStatus.ERROR)) {
            final List<ValidationIssueDTO> relevantIssues = AbstractConfigurationService.extractRelevantIssues(validationResponse);
            if (!relevantIssues.isEmpty()) {
                throwValidationBeforeDeleteException(abstractConfigurationModel, relevantIssues);
                return;
            }
        }
    }

    private static void throwValidationBeforeDeleteException(AbstractConfigurationModel abstractConfigurationModel, List<ValidationIssueDTO> issues) {
        throw new ApiException(
                AiaApiException.AiaApiHttpCodes.CONFLICT,
                AiaApiMessages.GENERAL.CANNOT_DELETE_ELEMENT,
                abstractConfigurationModel.getName(),
                formatValidationIssues(issues));
    }

    protected static String formatValidationIssues(final List<ValidationIssueDTO> issues) {
        List<String> messages = issues.stream()
                .map(ValidationIssueDTO::getUserMessage)
                .collect(Collectors.toList());
        return String.join("\n", messages);
    }


    public R getRepository() {
        return repository;
    }


}
