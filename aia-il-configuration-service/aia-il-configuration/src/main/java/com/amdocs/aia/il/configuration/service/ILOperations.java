package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.service.cache.CacheReferenceService;
import com.amdocs.aia.il.configuration.service.external.ExternalEntityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ILOperations {

    private final TransformationConfigurationService transformationConfigurationService;
    private final ContextConfigurationService contextConfigurationService;
    private final TransformationAttributeConfigurationService<TransformationAttributeDTO> transformationAttributeConfigurationService;
    private final EntityReferentialIntegrityService entityReferentialIntegrityService;
    private final CacheReferenceService cacheReferenceService;
    private final ExternalEntityService externalEntityService;

    public ILOperations(final TransformationConfigurationService transformationConfigurationService,
                        final ContextConfigurationService contextConfigurationService,
                        final TransformationAttributeConfigurationService<TransformationAttributeDTO> transformationAttributeConfigurationService,
                        final EntityReferentialIntegrityService entityReferentialIntegrityService,
                        final CacheReferenceService cacheReferenceService,
                        final ExternalEntityService externalEntityService) {
        this.transformationConfigurationService = transformationConfigurationService;
        this.contextConfigurationService = contextConfigurationService;
        this.transformationAttributeConfigurationService = transformationAttributeConfigurationService;
        this.entityReferentialIntegrityService = entityReferentialIntegrityService;
        this.cacheReferenceService = cacheReferenceService;
        this.externalEntityService = externalEntityService;
    }

    public List<TransformationDTO> listTransformations(final String projectKey) {
        return transformationConfigurationService.list(projectKey);
    }

    public List<TransformationDTO> getTransformationsByLogicalKey(final String projectKey, String schemaStore, String entityStore) {
        return transformationConfigurationService.findByLogicalEntity(projectKey,schemaStore,entityStore);
    }


    public void updateTransformation(final String projectKey, final String key, TransformationDTO transformation) {
        transformationConfigurationService.update(projectKey, key, transformation);
    }

    public void saveTransformation(final String projectKey, TransformationDTO transformation) {
        transformationConfigurationService.save(projectKey, transformation);
    }

    public void deleteTransformation(final String projectKey, final String key) {
        transformationConfigurationService.delete(projectKey, key);
    }

    public void deleteEntityTransformations(String projectKey, String targetSchemaStoreKey, String targetEntityStoreKey) {
        transformationConfigurationService.deleteEntityTransformations(projectKey, targetSchemaStoreKey, targetEntityStoreKey);
    }

    public List<ContextDTO> listContexts(final String projectKey) {
        return contextConfigurationService.list(projectKey);
    }

    public ContextDTO getContextsByStoreName(final String projectKey, final String storeName) {
        return contextConfigurationService.findPublisherContextByPublisherName(projectKey,storeName);
    }

    public void updateContext(final String projectKey, final String contextKey, ContextDTO context) {
        contextConfigurationService.update(projectKey, contextKey, context);
    }

    public void saveContext(final String projectKey, ContextDTO context) {
        contextConfigurationService.save(projectKey, context);
    }

    public List<TransformationAttributeDTO> listTransformationAttributes(final String projectKey,
                                                                         final String logicalSchemaKey,
                                                                         final String logicalEntityKey) {
        return transformationAttributeConfigurationService.list(projectKey, logicalSchemaKey, logicalEntityKey);
    }

    public void updateEntityReferentialIntegrity(final String projectKey, EntityReferentialIntegrity entityReferentialIntegrity) {
        entityReferentialIntegrity.setProjectKey(projectKey);
        entityReferentialIntegrityService.createOrUpdate(entityReferentialIntegrity);
    }

    public EntityReferentialIntegrity getEntityReferentialIntegrity(final String projectKey, final String schemaStoreKey, final String entityStoreKey){
        return entityReferentialIntegrityService.get(projectKey,schemaStoreKey,entityStoreKey).orElse(null);
    }

    public CacheReferenceEntityDTO getCacheEntity(final String projectKey, final String cacheEntity) {
        return cacheReferenceService.get(projectKey, cacheEntity);
    }

    public CacheReferenceEntityDTO saveCacheEntity(final String projectKey, CacheReferenceEntityDTO cacheReferenceEntity) {
        return cacheReferenceService.save(projectKey, cacheReferenceEntity);
    }

    public CacheReferenceEntityDTO updateCacheEntity(final String projectKey, final String cacheReferenceEntityKey, CacheReferenceEntityDTO cacheReferenceEntity) {
        return cacheReferenceService.update(projectKey, cacheReferenceEntityKey, cacheReferenceEntity);
    }

    public void deleteCacheEntity(final String projectKey, final String cacheReferenceEntityKey) {
        cacheReferenceService.delete(projectKey, cacheReferenceEntityKey);
    }

    public ExternalEntityDTO getExternalEntity(String projectKey, String schemaKey, String entityKey){
        return externalEntityService.get(projectKey, schemaKey, entityKey);
    }
}
