package com.amdocs.aia.il.configuration.service.cache;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.repo.SaveElementsResponse;
import com.amdocs.aia.il.common.model.EntityConfigurationUtils;
import com.amdocs.aia.il.common.model.cache.CacheAttribute;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import com.amdocs.aia.il.configuration.dependencies.CacheEntityDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.CacheReferenceEntityDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.mapper.ModelDtoMapper;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.cache.CacheReferenceRepository;
import com.amdocs.aia.il.configuration.service.AbstractModelService;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.CACHE_ENTITY;

@Service
public class CacheReferenceServiceImpl extends AbstractModelService<CacheEntity, CacheReferenceRepository> implements CacheReferenceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheReferenceServiceImpl.class);

    private final CacheEntityDependencyAnalyzer dependencyAnalyzer;
    private ModelDtoMapper<CacheEntity, CacheReferenceEntityDTO> mapper;
    private final AiaRepositoryOperations aiaRepositoryOperations;
    private final MessageHelper messageHelper;

    @Autowired
    public CacheReferenceServiceImpl(CacheReferenceRepository cacheReferenceRepository,
                                     MessageHelper messageHelper, ModelDtoMapper<CacheEntity, CacheReferenceEntityDTO> mapper,
                                     AiaRepositoryOperations aiaRepositoryOperations,
                                     CacheEntityDependencyAnalyzer dependencyAnalyzer) {
        super(cacheReferenceRepository);
        this.messageHelper = messageHelper;
        this.mapper = mapper;
        this.aiaRepositoryOperations = aiaRepositoryOperations;
        this.dependencyAnalyzer = dependencyAnalyzer;
    }

    @Override
    public CacheReferenceEntityDTO get(String projectKey, String cacheReferenceEntityKey) {
        if (StringUtils.isEmpty(cacheReferenceEntityKey)) {
            throw messageHelper.createIDNotSetException(CacheEntity.class.getSimpleName(), cacheReferenceEntityKey, "Cache Entity key can't be empty");
        }
        final CacheEntity returnedCacheEntity;
        try {
            returnedCacheEntity = super.getRepository().getByKey(projectKey, cacheReferenceEntityKey);
        } catch (final AiaApiException e) {
            throw messageHelper.createUnexpectedOperationErrorException("get Cache Entity", e);
        }
        if (returnedCacheEntity == null) {
            throw messageHelper.createObjectDoesNotExistException("get Cache Entity", CACHE_ENTITY, cacheReferenceEntityKey);
        }
        return mapper.toDTO(returnedCacheEntity);
    }

    @Override
    public List<CacheReferenceEntityDTO> list(String projectKey) {
        return super.getRepository().findByProjectKey(projectKey).stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public CacheReferenceEntityDTO update(String projectKey, String key, CacheReferenceEntityDTO cacheReferenceEntityDTO) {
        final CacheEntity existingCacheEntity = super.getRepository().getByKey(projectKey, cacheReferenceEntityDTO.getCacheReferenceEntityKey());
        doUpdate(key, existingCacheEntity);
        final CacheEntity updatedModel = mapper.toModel(projectKey, cacheReferenceEntityDTO);
        completeModelBeforeSave(updatedModel);
        validateBeforeSave(updatedModel);
        final CacheEntity savedModel = super.getRepository().save(updatedModel);
        return mapper.toDTO(savedModel);
    }

    private void doUpdate(String entity, CacheEntity model) {
        if (model == null) {
            throw messageHelper.createElementNotFoundException(CACHE_ENTITY, entity);
        }
    }

    @Override
    public void delete(String projectKey, String cacheReferenceEntityKey) {
        CacheEntity cacheEntity;
        try {
            cacheEntity = super.getRepository().getByKey(projectKey, cacheReferenceEntityKey);
        } catch (final AiaApiException e) {
            throw messageHelper.createUnexpectedOperationErrorException("get Cache Entity", e);
        }
        if (cacheEntity == null) {
            throw messageHelper.createElementNotFoundException(CACHE_ENTITY, cacheReferenceEntityKey);
        }
        validateBeforeDelete(cacheEntity);
        aiaRepositoryOperations.delete(cacheEntity.getId());
    }

    @Override
    public CacheReferenceEntityDTO save(String projectKey, CacheReferenceEntityDTO cacheReferenceEntityDTO) {
        LOGGER.debug("Check if exists cache entity By Key: projectKey {} Entity {}  repository {}", projectKey, cacheReferenceEntityDTO.getCacheReferenceEntityKey(), super.getRepository() != null);
        final CacheEntity existingCacheEntity = super.getRepository().getByKey(projectKey, cacheReferenceEntityDTO.getCacheReferenceEntityKey());
        LOGGER.debug("Existing Cache entity model before save: {}", existingCacheEntity);
        doSave(existingCacheEntity);
        final CacheEntity model = mapper.toModel(projectKey, cacheReferenceEntityDTO);
        LOGGER.debug("Complete Model Before Save: {}", model);
        completeModelBeforeSave(model);
        validateBeforeSave(model);
        LOGGER.debug("Save model: {}", model);
        final CacheEntity savedModel = super.getRepository().save(model);
        return mapper.toDTO(savedModel);
    }

    @Override
    protected void validateBeforeSave(CacheEntity model) {
        Map<String, List<CacheAttribute>> attributesByKey = model.getAttributes().stream().collect(Collectors.groupingBy(CacheAttribute::getAttributeKey));
        String duplicateAttKeys = attributesByKey.entrySet().stream().filter(stringEntry -> stringEntry.getValue().size() > 1).map(Map.Entry::getKey).collect(Collectors.joining(","));
        //check if there duplicate attribute keys
        if (duplicateAttKeys.length() > 0) {
            throw new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.DUPLICATE_ATTRIBUTE, duplicateAttKeys);
        }

        super.validateBeforeSave(model);
    }

    private void completeModelBeforeSave(CacheEntity model) {
        model.setPublicFeatures(dependencyAnalyzer.getPublicFeatures(model));
        model.setDependencies(dependencyAnalyzer.getDependencies(model));
        model.setCreatedBy(aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy());
        model.setCreatedAt(System.currentTimeMillis());
        model.setId(EntityConfigurationUtils.getElementId(model));
    }

    protected void doSave(CacheEntity existingCacheEntity) {
        if (existingCacheEntity != null) {
            throw messageHelper.createObjectAlreadyExistException(CACHE_ENTITY, existingCacheEntity.getEntityKey());
        }
    }

    public ModelDtoMapper<CacheEntity, CacheReferenceEntityDTO> getMapper() {
        return mapper;
    }

    public void setMapper(ModelDtoMapper<CacheEntity, CacheReferenceEntityDTO> mapper) {
        this.mapper = mapper;
    }


    private void completeModelBeforeSave(List<CacheEntity> models) {
        final String user = aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy();
        final long currentTime = System.currentTimeMillis();
        models.forEach(model -> {
            model.setPublicFeatures(dependencyAnalyzer.getPublicFeatures(model));
            model.setDependencies(dependencyAnalyzer.getDependencies(model));
            model.setCreatedBy(user);
            model.setCreatedAt(currentTime);
            model.setId(EntityConfigurationUtils.getElementId(model));
        });
    }

    @Override
    public SaveElementsResponseDTO bulkSave(String projectKey, List<CacheReferenceEntityDTO> cacheReferenceEntityDTOs) {

        LOGGER.debug("Saving Bulk Cache Reference Context");
        final List<String> existingCacheEntitiesKeys =  super.getRepository().findByProjectKey(projectKey).stream()
                .map(CacheEntity::getEntityKey)
                .collect(Collectors.toList());

        cacheReferenceEntityDTOs.stream()
                .map(CacheReferenceEntityDTO::getCacheReferenceEntityKey)
                .filter(existingCacheEntitiesKeys::contains)
                .findAny()
                .ifPresent(key -> {throw messageHelper.createObjectAlreadyExistException(CACHE_ENTITY, key);});

        List<CacheEntity> models = mapper.toModel(projectKey, cacheReferenceEntityDTOs);

        completeModelBeforeSave(models);

        SaveElementsResponse ret = super.getRepository().saveElements(models);

        SaveElementsResponseDTO dto = new SaveElementsResponseDTO();
        dto.setSavedElementsCount(ret.getSavedElementsCount());
        return dto;

    }

}