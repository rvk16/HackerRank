package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.utils.ValidityStatus;
import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.BaseElement;
import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.il.common.model.configuration.tables.AbstractPublisherConfigurationModel;
import com.amdocs.aia.il.common.model.configuration.transformation.Transformation;
import com.amdocs.aia.il.configuration.dependencies.TransformationDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.dto.TransformationDTO;
import com.amdocs.aia.il.configuration.mapper.TransformationMapper;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.TransformationRepository;
import com.amdocs.aia.il.configuration.shared.AiaSharedProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TransformationConfigurationServiceImpl extends AbstractConfigurationService
        <Transformation, TransformationDTO, TransformationRepository, TransformationDependencyAnalyzer> implements TransformationConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformationConfigurationService.class);

    private static final String TRANSFORMATION = "Transformation";

    @Autowired
    TransformationConfigurationServiceImpl(final TransformationRepository repository,
                                           final TransformationDependencyAnalyzer dependencyAnalyzer,
                                           final TransformationMapper mapper, final MessageHelper messageHelper,
                                           final AiaSharedProxy aiaSharedProxy) {
        super(repository, dependencyAnalyzer, mapper, messageHelper, aiaSharedProxy);
    }

    @Override
    public TransformationDTO update(String projectKey, String key, TransformationDTO entityTransformation) {
        return super.update(projectKey, key, entityTransformation);
    }

    @Override
    protected void doUpdate(String projectKey, String key, TransformationDTO dto, Transformation model) {
        if (model == null) {
            throw getMessageHelper().createElementNotFoundException(Transformation.ELEMENT_TYPE, key);
        }
        final ValidityStatus status = isPublisherTransformationValidForUpdate(model);
        if (!status.isValid()) {
            throw getMessageHelper().createValidationException(status, "update PublisherTransformation");
        }
        // model.setLastUpdateTime(Instant.now().toEpochMilli());
        getRepository().save(model);
    }

    private ValidityStatus isPublisherTransformationValidForUpdate(final Transformation transformationContext) {
        final ValidityStatus status = checkIfIdIsEmpty(transformationContext, TRANSFORMATION);
        if (status.isValid() && !isPublisherTransformationExist(transformationContext)) {
            status.setValid(false);
            throw getMessageHelper().createObjectDoesNotExistException("update Transformation", TRANSFORMATION, transformationContext.getId());

        }
        return status;
    }

    private ValidityStatus checkIfIdIsEmpty(final BaseElement baseElement, final String objectType) {
        final ValidityStatus status = new ValidityStatus(true);
        if (StringUtils.isEmpty(baseElement.getId())) {
            status.setValid(false);
            throw getMessageHelper().createIDNotSetException(TRANSFORMATION, objectType, "Transformation ID not set");
        }
        return status;
    }

    @Override
    public TransformationDTO get(String projectKey, String id) throws AiaApiException {
        final Transformation returnedTransformation;
        if (StringUtils.isEmpty(id)) {
            throw getMessageHelper().createIDNotSetException(TRANSFORMATION, id, "Transformation ID not set");
        }
        try {
            returnedTransformation = getRepository().getByKey(projectKey, id);
        } catch (final Exception e) {
            throw getMessageHelper().createUnexpectedOperationErrorException("get Transformation", e);
        }
        if (returnedTransformation == null) {
            throw getMessageHelper().createObjectDoesNotExistException("get Transformation", TRANSFORMATION, id);
        }

        return getMapper().toDTO(returnedTransformation);
    }

    @Override
    public TransformationDTO save(String projectKey, TransformationDTO t) {
        if (t.getContextKey() == null || t.getContextKey().isEmpty()) {
            throw getMessageHelper().createIDNotSetException(TRANSFORMATION, t.getContextKey(), "contextKey doesn't exist");
        }
        if (t.getTargetEntityStoreKey() == null || t.getTargetEntityStoreKey().isEmpty()) {
            throw getMessageHelper().createIDNotSetException(TRANSFORMATION, t.getTargetEntityStoreKey(), "EntityStoreKey doesn't exist");
        }
        if (t.getTargetSchemaStoreKey() == null || t.getTargetSchemaStoreKey().isEmpty()) {
            throw getMessageHelper().createIDNotSetException(TRANSFORMATION, t.getTargetSchemaStoreKey(), "SchemaStoreKey doesn't exist");
        }
        return super.save(projectKey, t);
    }

    @Override
    public SaveElementsResponseDTO bulkSave(String projectKey, List<TransformationDTO> s) {
        LOGGER.debug("Saving Bulk TTransformation Configurations");
        s.forEach(t -> {

            if (t.getContextKey() == null || t.getContextKey().isEmpty()) {
                throw getMessageHelper().createIDNotSetException(TRANSFORMATION, t.getContextKey(), "contextKey doesn't exist");
            }
            if (t.getTargetEntityStoreKey() == null || t.getTargetEntityStoreKey().isEmpty()) {
                throw getMessageHelper().createIDNotSetException(TRANSFORMATION, t.getTargetEntityStoreKey(), "EntityStoreKey doesn't exist");
            }
            if (t.getTargetSchemaStoreKey() == null || t.getTargetSchemaStoreKey().isEmpty()) {
                throw getMessageHelper().createIDNotSetException(TRANSFORMATION, t.getTargetSchemaStoreKey(), "SchemaStoreKey doesn't exist");
            }

        });

        return super.bulkSave(projectKey, s);
    }

    @Override
    protected void doSave(String projectKey, TransformationDTO s, Transformation transformation) {
        if (transformation != null) {
            if (transformation.getId() != null) {
                throw getMessageHelper().createElementAlreadyExistsException(TRANSFORMATION, transformation.getId());
            }
            if (!transformation.getOriginProcess().equals(OriginProcess.MAPPING_SHEETS_MIGRATION) && isPublisherTransformationExist(transformation)) {
                throw getMessageHelper().createElementAlreadyExistsException(TRANSFORMATION, transformation.getId());
            }
        }

    }

    @Override
    protected void doSave(String projectKey, List<TransformationDTO> s) {
        final List<Transformation> existingTransformations = getRepository().findByProjectKey(projectKey);
        s.forEach(transformation -> {
            LOGGER.debug("doSave Transformation Configuration {}", transformation.getContextKey());
            existingTransformations.stream()
                    .filter(existing -> existing.getKey().equals(transformation.getContextKey()))
                    .findAny()
                    .ifPresent(existing -> {
                        if (existing.getId() != null) {
                            throw getMessageHelper().createElementAlreadyExistsException(TRANSFORMATION, transformation.getId());
                        }

                        boolean existsByID = existingTransformations.stream().anyMatch(model -> Objects.equals(existing.getId(), model.getId()));
                        if (!existing.getOriginProcess().equals(OriginProcess.MAPPING_SHEETS_MIGRATION) && existsByID) {
                            throw getMessageHelper().createObjectAlreadyExistException(TRANSFORMATION, existing.getId());
                        }

                    });
        });
    }


    @Override
    public List<TransformationDTO> list(final String projectKey) {
        final List<Transformation> entityTransformationList = getRepository().findAllPublisherTransformation(projectKey);
        return entityTransformationList == null || entityTransformationList.isEmpty() ? new ArrayList<>() : entityTransformationList.stream().map(getMapper()::toDTO).collect(Collectors.toList());
    }

    @Override
    public void delete(String projectKey, String key) {
        final Transformation transformation;

        transformation = getRepository().getByKey(projectKey, key);
        if (transformation == null) {
            throw getMessageHelper().createObjectDoesNotExistException("delete Transformation", TRANSFORMATION, key);
        }
        getRepository().delete(transformation.getId());
//        getAiaSharedProxy().reportDelete(publisherTransformation.toSharedElement());
    }

    public void deleteEntityTransformations(String projectKey, String targetSchemaStoreKey, String targetEntityStoreKey) {
        getRepository().deleteEntityTransformations(projectKey, targetSchemaStoreKey, targetEntityStoreKey);
    }

    private boolean isPublisherTransformationExist(final Transformation entityTransformation) {
        boolean isExist = getPublisherTransformationFromRepositoryById(entityTransformation.getId()) != null;
        isExist |= getPublisherTransformationFromRepositoryByKey(entityTransformation.getProjectKey(), entityTransformation.getTargetSchemaStoreKey(), entityTransformation.getTargetEntityStoreKey(), entityTransformation.getContextKey()) != null;
        return isExist;
    }

    private Transformation getPublisherTransformationFromRepositoryById(String id) {
        return getRepository().findById(id);
    }

    private Transformation getPublisherTransformationFromRepositoryByKey(final String projectKey,
                                                                         final String targetSchemaStoreKey,
                                                                         final String targetEntityStoreKey,
                                                                         final String contextKey) {
        return getRepository().getByKey(projectKey, contextKey);
        //return getRepository().findPublisherTransformationByKeys(projectKey, targetSchemaStoreKey, targetEntityStoreKey, contextKey);
    }

    @Override
    protected void reportAddEntity(Transformation transformation) {
        // TODO - need to implement in case stores need to be exposed .
        LOGGER.debug("Report Add Entity Method called");
        //getAiaSharedProxy().reportAdd(publisherTransformation.toSharedElement());
    }


    @Override
    protected Class<? extends AbstractPublisherConfigurationModel> getModelClass() {
        return Transformation.class;
    }


    @Override
    public TransformationDTO findByTargetSchemaStoreAndTargetEntityStore(String projectKey, String targetSchemaStoreKey, String targetEntityStoreKey, String key) {
        try {
            return getMapper().toDTO(getRepository().findPublisherTransformationByKeys(projectKey, targetSchemaStoreKey, targetEntityStoreKey, key));
        } catch (Exception e) {
            throw getMessageHelper().createObjectDoesNotExistException(TRANSFORMATION, projectKey, "Key: " + targetSchemaStoreKey + " " + targetEntityStoreKey);

        }
    }


    @Override
    public List<TransformationDTO> findByLogicalEntity(String projectKey, String schemaStore, String entityStore) {
        try {
            final List<Transformation> entityTransformationList = getRepository().findPublisherTransformationByLogicalEntity(projectKey, schemaStore, entityStore);
            return entityTransformationList == null || entityTransformationList.isEmpty() ? new ArrayList<>() : entityTransformationList.stream().map(getMapper()::toDTO).collect(Collectors.toList());

        } catch (Exception e) {
            throw getMessageHelper().createObjectDoesNotExistException(TRANSFORMATION, projectKey, "from schemaStore: " + schemaStore + " and entityStore:" + entityStore);

        }
    }
}