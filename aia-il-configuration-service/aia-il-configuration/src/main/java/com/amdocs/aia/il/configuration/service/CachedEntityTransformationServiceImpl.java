package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import com.amdocs.aia.il.common.model.configuration.transformation.Transformation;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.shared.client.AiaSharedOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.CACHE;
import static com.amdocs.aia.il.common.model.ConfigurationConstants.CACHE_ENTITY;

@Service
public class CachedEntityTransformationServiceImpl
        extends AbstractEntityTransformationService<CachedEntityTransformationGridElementDTO>
        implements CachedEntityTransformationService {

    CachedEntityTransformationServiceImpl(final DataChannelStoreType dataChannelStoreType,
                                          final AiaSharedOperations aiaSharedOperations,
                                          final ILOperations ilOperations,
                                          final MessageHelper messageHelper) {
        super(dataChannelStoreType, aiaSharedOperations, ilOperations, messageHelper);
    }

    @Override
    public List<CachedEntityTransformationGridElementDTO> list(String projectKey) {
        return Collections.emptyList();
    }

    @Override
    public void delete(String projectKey, String logicalEntityKey) {
        final String targetSchemaStoreKey = CACHE;
        final String targetEntityStoreKey = logicalEntityKey;
        ilOperations.deleteCacheEntity(projectKey, logicalEntityKey);
        ilOperations.deleteEntityTransformations(projectKey, targetSchemaStoreKey, targetEntityStoreKey);
    }

    @Override
    public EntityTransformationDTO get(String projectKey, String cacheEntityKey) {
        EntityTransformationDTO entityTransformationDTO = new EntityTransformationDTO();
        entityTransformationDTO.setLogicalEntityKey(cacheEntityKey);
        String logicalSchemaKey = CACHE;
        entityTransformationDTO.setLogicalSchemaKey(logicalSchemaKey);

        CacheReferenceEntityDTO cacheReferenceEntityDTO = ilOperations.getCacheEntity(projectKey, cacheEntityKey);
        if(cacheReferenceEntityDTO == null){
            throw messageHelper.createElementNotFoundException(CacheEntity.ELEMENT_TYPE, cacheEntityKey);
       }

        entityTransformationDTO.setEntityName(cacheReferenceEntityDTO.getCacheReferenceEntityName());
        entityTransformationDTO.setDescription(cacheReferenceEntityDTO.getDescription());
        entityTransformationDTO.setAttributes(getTransformationAttributes(cacheReferenceEntityDTO.getCacheReferenceAttributes()));
        entityTransformationDTO.setTransformations(ilOperations.listTransformations(projectKey)
                .stream().filter(transformationDTO ->
                        transformationDTO.getTargetSchemaName().equals(logicalSchemaKey) &&
                                transformationDTO.getTargetEntityStoreKey().equals(cacheEntityKey))
                .collect(Collectors.toList()));

        return entityTransformationDTO;
    }

    @Override
    public EntityTransformationDTO update(String projectKey, EntityTransformationDTO dto) {
        /**
         * input is EntityTransformationDTO
         * update Cache Entity
         * update/delete IL Transformation
         */
        final String cacheEntityKey = dto.getLogicalEntityKey();
        final String cacheEntityName = dto.getEntityName();
        validate(cacheEntityKey, dto.getTransformations());
        updateCacheEntity(projectKey, cacheEntityKey, cacheEntityName,  dto.getDescription(), dto.getAttributes());
        updateTransformationService(projectKey, dto);

        return dto;
    }

    @Override
    public EntityTransformationDTO create(String projectKey, EntityTransformationDTO dto) {
        /**
         * input is EntityTransformationDTO
         * create Cache Entity
         * create/update IL Transformation
         */
        final String cacheEntityKey = dto.getLogicalEntityKey();
        final String cacheEntityName = dto.getEntityName();
        dto.setLogicalSchemaKey(CACHE);
        validate(cacheEntityKey, dto.getTransformations());
        createCacheEntity(projectKey, cacheEntityKey, cacheEntityName, dto.getDescription(), dto.getAttributes());

        for (TransformationDTO transformationDTO : dto.getTransformations()) {
            transformationDTO.setStoreName(CACHE + "-" + cacheEntityKey + "-" + transformationDTO.getContextKey());
            transformationDTO.setId(transformationDTO.getStoreName());
        }
        updateTransformationService(projectKey, dto);

        return dto;
    }

    private CacheReferenceEntityDTO createCacheEntity(final String projectKey, final String cacheEntityKey,
                                                      final String cacheEntityName, final String entityDescription,
                                                      List<TransformationAttributeDTO> attributes) {
        CacheReferenceEntityDTO cacheEntity = new CacheReferenceEntityDTO();
        cacheEntity
                .cacheReferenceEntityKey(cacheEntityKey)
                .cacheReferenceEntityName(cacheEntityName)
                .description(entityDescription)
                .cacheReferenceAttributes(addTransformationAttributesToCacheEntity(attributes));
        ilOperations.saveCacheEntity(projectKey, cacheEntity);

        return cacheEntity;
    }

    private CacheReferenceEntityDTO updateCacheEntity(final String projectKey, final String cacheEntityKey,
                                                      final String cacheEntityName, final String entityDescription,
                                                      List<TransformationAttributeDTO> attributes) {
        CacheReferenceEntityDTO existingCacheEntity = ilOperations.getCacheEntity(projectKey, cacheEntityKey);
        if(existingCacheEntity == null){
            throw messageHelper.createElementNotFoundException(CacheEntity.ELEMENT_TYPE, cacheEntityKey);
        }
        existingCacheEntity.setDescription(entityDescription);
        existingCacheEntity.setCacheReferenceEntityName(cacheEntityName);
        existingCacheEntity.setCacheReferenceAttributes(addTransformationAttributesToCacheEntity(attributes));
        existingCacheEntity = ilOperations.updateCacheEntity(projectKey, cacheEntityKey, existingCacheEntity);

        return existingCacheEntity;
    }

    private List<CacheReferenceAttributeDTO> addTransformationAttributesToCacheEntity(List<TransformationAttributeDTO> transformationAttributes){
        if (transformationAttributes == null) {
            return Collections.emptyList();
        }
        final Set<CacheReferenceAttributeDTO> attributesDtos = new HashSet<>(transformationAttributes.size());
        for (final TransformationAttributeDTO transformationAttribute : transformationAttributes) {
            attributesDtos.add(convertTransformationAttributeToCacheReferenceAttributes(transformationAttribute));
        }
        return attributesDtos.stream().collect(Collectors.toList());
    }

    private List<TransformationAttributeDTO> getTransformationAttributes(List<CacheReferenceAttributeDTO> cacheReferenceAttributes){
        if (cacheReferenceAttributes == null) {
            return Collections.emptyList();
        }
        final Set<TransformationAttributeDTO> attributesDtos = new HashSet<>(cacheReferenceAttributes.size());
        for (final CacheReferenceAttributeDTO cacheReferenceAttribute : cacheReferenceAttributes) {
            attributesDtos.add(createTransformationAttributeDtoFromCacheReferenceAttributeDTO(cacheReferenceAttribute));
        }
        return attributesDtos.stream().collect(Collectors.toList());
    }

    /**
     * m
     * Updates the {@link TransformationConfigurationService IL transaction service}
     * according to the {@link EntityTransformationDTO entity transformation} (create/update/delete).
     *
     * @param projectKey           The project key
     * @param entityTransformation The entity transformation
     */
    private void updateTransformationService(final String projectKey, EntityTransformationDTO entityTransformation) {
        Map<String, TransformationDTO> repositoryTransformations = ilOperations.listTransformations(projectKey).stream()
                .filter(tx -> tx.getTargetEntityStoreKey().equals(entityTransformation.getLogicalEntityKey()))
                .collect(Collectors.toMap(TransformationDTO::getStoreName, Function.identity()));
        Map<String, TransformationDTO> dtoTransformations = entityTransformation.getTransformations().stream()
                .collect(Collectors.toMap(TransformationDTO::getId, Function.identity()));

        // Transactions present in both maps -> update
        dtoTransformations.entrySet().removeIf(e -> {
            if (repositoryTransformations.containsKey(e.getKey())) {
                repositoryTransformations.remove(e.getKey());
                ilOperations.updateTransformation(projectKey, e.getKey(), e.getValue());
                return true;
            }
            return false;
        });

        // Transactions only in dto -> create
        dtoTransformations.values().forEach(tx -> ilOperations.saveTransformation(projectKey, tx));

        // Transactions only in repo -> delete
        repositoryTransformations.keySet().forEach(txId -> ilOperations.deleteTransformation(projectKey, txId));
    }

    private void validate(String cacheEntityKey, List<TransformationDTO> transformations) {
        //check if cache entity key is invalid
        if (!cacheEntityKey.equals(ModelUtils.toAllowedLocalKey(cacheEntityKey))) {
            throw messageHelper.invalidPrimaryKeyException(CACHE_ENTITY, cacheEntityKey);
        }

        //check if cache entity includes not just one transformation
        if(transformations.size() != 1){
            throw messageHelper.invalidIndexException(Transformation.ELEMENT_TYPE, cacheEntityKey);
        }

        //check if there duplicate attribute keys for sources
        Map<String, List<TransformationContextEntityDTO>> attributesByKey = transformations.get(0).getReferenceSourceEntities().stream().collect(Collectors.groupingBy(TransformationContextEntityDTO::getEntityStoreKey));
        String duplicateAttKeys = attributesByKey.entrySet().stream().filter(stringEntry -> stringEntry.getValue().size() > 1).map(Map.Entry::getKey).collect(Collectors.joining(","));
        if (duplicateAttKeys.length() > 0) {
            throw new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.DUPLICATE_ATTRIBUTE, duplicateAttKeys);
        }
    }
}
