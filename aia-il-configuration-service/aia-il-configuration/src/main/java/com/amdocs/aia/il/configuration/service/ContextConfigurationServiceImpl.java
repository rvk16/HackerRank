package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.utils.ValidityStatus;
import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.BaseElement;
import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.il.common.model.configuration.tables.AbstractPublisherConfigurationModel;
import com.amdocs.aia.il.common.model.configuration.transformation.Context;
import com.amdocs.aia.il.common.model.external.Availability;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.configuration.dependencies.ContextDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.ContextDTO;
import com.amdocs.aia.il.configuration.dto.ContextEntityDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.mapper.ContextMapper;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.ContextRepository;
import com.amdocs.aia.il.configuration.repository.external.ExternalSchemaRepository;
import com.amdocs.aia.il.configuration.shared.AiaSharedProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.CACHE;
import static com.amdocs.aia.il.configuration.dto.ContextEntityDTO.RelationTypeEnum.REF;

@Service
public class ContextConfigurationServiceImpl extends AbstractConfigurationService
        <Context, ContextDTO, ContextRepository, ContextDependencyAnalyzer> implements ContextConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextConfigurationServiceImpl.class);

    private static final String CONTEXT = "Context";
    private static final String SCHEMA = "Schema";
    private final ExternalSchemaRepository externalSchemaRepository;

    @Autowired
    ContextConfigurationServiceImpl(final ContextRepository repository,
                                    final ContextDependencyAnalyzer dependencyAnalyzer,
                                    final ContextMapper mapper, final MessageHelper messageHelper,
                                    final AiaSharedProxy aiaSharedProxy, ExternalSchemaRepository externalSchemaRepository) {
        super(repository, dependencyAnalyzer, mapper, messageHelper, aiaSharedProxy);
        this.externalSchemaRepository = externalSchemaRepository;
    }

    @Override
    public List<ContextDTO> list(String projectKey) {
        //   return super.list(projectKey);
        final List<Context> contextList = getRepository().findAllPublisherContextsByProjectKey(projectKey);
        return contextList == null || contextList.isEmpty() ? new ArrayList<>() : contextList.stream().map(getMapper()::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<String> relationTypesList() {
        return Stream.of(ContextEntityDTO.RelationTypeEnum.values()).map(ContextEntityDTO.RelationTypeEnum::name)
                .collect(Collectors.toList());
    }

    @Override
    public ContextDTO findPublisherContextByPublisherName(String projectKey, String publisherName) {
        final Context returnedContext = getRepository().findPublisherContextByPublisherName(projectKey, publisherName);
        if (returnedContext == null) {
            throw getMessageHelper().createObjectDoesNotExistException("get Context", CONTEXT, publisherName);
        }
        return getMapper().toDTO(returnedContext);
    }

    @Override
    protected void doSave(String projectKey, ContextDTO s, @Nullable Context existingModel) {
        if (existingModel != null) {
            if (existingModel.getId() != null) {
                throw getMessageHelper().createObjectAlreadyExistException(CONTEXT, existingModel.getId());
            }
            if (existingModel.getOriginProcess() != OriginProcess.MAPPING_SHEETS_MIGRATION && isPublisherContextExist(existingModel)) {
                throw getMessageHelper().createObjectAlreadyExistException(CONTEXT, existingModel.getId());
            }
        }
        Optional<ContextEntityDTO> optionalContextEntityLead = s.getContextEntities().stream().filter(contextEntityDTO -> ContextEntityDTO.RelationTypeEnum.LEAD == contextEntityDTO.getRelationType()).findFirst();
        if (!optionalContextEntityLead.isPresent()) {
            throw getMessageHelper().createPublisherContextDoesNotExistException(s.getContextKey(), CONTEXT, s.getStoreName());
        } else {
            if (CACHE.equals(optionalContextEntityLead.get().getSchemaStoreKey())) {
                throw getMessageHelper().invalidLeadContextEntityException(optionalContextEntityLead.get().getSourceAlias(), s.getStoreName());
            }
        }

        s.getContextEntities().forEach(contextEntityDTO -> {
            if (contextEntityDTO.getRelationType() != REF) {
                ExternalSchema externalSchema = externalSchemaRepository.getByKey(projectKey, contextEntityDTO.getSchemaStoreKey());
                if (externalSchema != null && externalSchema.getAvailability() == (Availability.SHARED)) {
                    throw getMessageHelper().invalidExternalEntity(CONTEXT, s.getContextKey());
                }
            }
        });
    }

    @Override
    protected void doSave(String projectKey, List<ContextDTO> contextDTOS) {
        final List<Context> existingContexts = getRepository().findByProjectKey(projectKey);

        final List<ExternalSchema> externalSchemas = externalSchemaRepository.findByProjectKey(projectKey);

        contextDTOS.forEach(context -> {
            LOGGER.debug("doSave Context Configuration {}", context.getContextKey());
            context.getContextEntities().stream()
                    .filter(contextEntityDTO -> REF != contextEntityDTO.getRelationType())
                    .forEach(contextEntityDTO -> {

                        Optional<ExternalSchema> externalSchema = externalSchemas.stream()
                                .filter(schema -> schema.getSchemaKey().equals(contextEntityDTO.getSchemaStoreKey()))
                                .findAny();

                        if (externalSchema.isPresent()) {

                            if (externalSchema.get().getAvailability() == (Availability.SHARED)) {
                                throw getMessageHelper().invalidExternalEntity(CONTEXT, context.getContextKey());
                            }
                        } else {
                            throw getMessageHelper().missingSchema(SCHEMA, contextEntityDTO.getSchemaStoreKey());
                        }
                    });


            existingContexts.stream()
                    .filter(existing -> existing.getKey().equals(context.getContextKey()))
                    .findAny()
                    .ifPresent(existing -> {
                        if (existing.getId() != null) {
                            throw getMessageHelper().createObjectAlreadyExistException(CONTEXT, existing.getId());
                        }

                        boolean existsByID = existingContexts.stream().anyMatch(model -> Objects.equals(existing.getId(), model.getId()));
                        if (existing.getOriginProcess() != OriginProcess.MAPPING_SHEETS_MIGRATION && existsByID) {
                            throw getMessageHelper().createObjectAlreadyExistException(CONTEXT, existing.getId());
                        }
                    });
        });
    }

    @Override
    public ContextDTO save(String projectKey, ContextDTO s) {
        return super.save(projectKey, s);
    }

    @Override
    public SaveElementsResponseDTO bulkSave(String projectKey, List<ContextDTO> s) {
        LOGGER.debug("Saving Bulk Transformation Context");
        return super.bulkSave(projectKey, s);
    }

    @Override
    public ContextDTO get(String projectKey, String id) {
        final Context returnedContext;
        if (StringUtils.isEmpty(id)) {
            getMessageHelper().createIDNotSetException(CONTEXT, id, "project key doesn't exist");
        }
        try {
            returnedContext = getRepository().getByKey(projectKey, id);
        } catch (final AiaApiException e) {
            throw getMessageHelper().createUnexpectedOperationErrorException("get Context", e);
        }
        if (returnedContext == null) {
            throw getMessageHelper().createObjectDoesNotExistException("get Context", CONTEXT, id);
        }
        return getMapper().toDTO(returnedContext);
    }

    private boolean isPublisherContextExist(final Context transformationContext) {
        boolean isExist = getPublisherContextFromRepositoryById(transformationContext.getId()) != null;
        isExist |= getPublisherContextFromRepositoryByKeys(transformationContext.getProjectKey(), transformationContext.getContextKey()) != null;
        return isExist;
    }

    private Context getPublisherContextFromRepositoryByKeys(final String projectKey, final String contextKey) {
        return getRepository().getByKey(projectKey, contextKey);
    }

    private Context getPublisherContextFromRepositoryById(final String id) {
        return getRepository().findById(id);
    }

    public boolean isPublisherContextExist(final String projectKey, final String contextKey) {
        final Context rtContext = getRepository().getByKey(projectKey, contextKey);
        return rtContext != null;
    }

    @Override
    protected void reportAddEntity(Context savedModel) {
        // TODO - need to implement in case stores need to be exposed .
        LOGGER.debug("Report Add Entity Method called");
    }

    @Override
    protected void doUpdate(String projectKey, String key, ContextDTO dto, Context model) {
        if (model == null) {
            throw getMessageHelper().createElementNotFoundException(Context.ELEMENT_TYPE, key);
        }
        final ValidityStatus status = isPublisherContextValidForUpdate(model);
        if (!status.isValid()) {
            throw getMessageHelper().createValidationException(status, "update Context");
        }
        // model.setLastUpdateTime(Instant.now().toEpochMilli());
        getRepository().save(model);
    }

    private ValidityStatus isPublisherContextValidForUpdate(final Context transformationContext) {
        final ValidityStatus status = checkIfIdIsEmpty(transformationContext, CONTEXT);
        if (status.isValid() && !isPublisherContextExist(transformationContext)) {
            status.setValid(false);
            throw getMessageHelper().createObjectDoesNotExistException("update Context", CONTEXT, transformationContext.getId());
        }
        return status;
    }

    private ValidityStatus checkIfIdIsEmpty(final BaseElement baseElement, final String objectType) {
        final ValidityStatus status = new ValidityStatus(true);
        if (StringUtils.isEmpty(baseElement.getId())) {
            status.setValid(false);
            throw getMessageHelper().createIDNotSetException(CONTEXT, objectType, "Context Id not set");
        }
        return status;
    }

    @Override
    public void delete(String projectKey, String key) {
        final Context context;
        context = getRepository().getByKey(projectKey, key);
        if (context == null) {
            throw getMessageHelper().createObjectDoesNotExistException("delete Context", CONTEXT, key);
        }
        getRepository().delete(context.getKey());
    }

    @Override
    protected Class<? extends AbstractPublisherConfigurationModel> getModelClass() {
        return Context.class;
    }
}