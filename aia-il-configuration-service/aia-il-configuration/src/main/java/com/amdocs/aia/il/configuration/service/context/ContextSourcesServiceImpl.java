package com.amdocs.aia.il.configuration.service.context;

import com.amdocs.aia.common.model.repo.SearchPropertiesQueryRequest;
import com.amdocs.aia.common.model.store.AttributeStore;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.service.ILOperations;
import com.amdocs.aia.il.configuration.shared.AiaSharedProxy;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.CACHE;
import static com.amdocs.aia.il.common.model.ConfigurationConstants.DATA_CHANNEL;

@Service
public class ContextSourcesServiceImpl implements ContextSourcesService {
    public static final String ENTITY_KEY = "entityKey";
    public static final String NAME = "name";
    public static final String SCHEMA_KEY = "schemaKey";
    public static final String SCHEMA_STORE_KEY = "schemaStoreKey";
    public static final String ENTITY_STORE_KEY = "entityStoreKey";
    public static final String EXTERNAL = "EXTERNAL";
    public static final String REFERENCE = "REFERENCE";
    public static final String LOGICAL_SCHEMA_KEY = "logicalSchemaKey";

    private final AiaRepositoryOperations aiaRepositoryOperations;
    private final ILOperations ilOperations;
    private final AiaSharedProxy aiaSharedProxy;
    private final MessageHelper messageHelper;

    ContextSourcesServiceImpl(AiaRepositoryOperations aiaRepositoryOperations,
                              ILOperations ilOperations, AiaSharedProxy aiaSharedProxy, MessageHelper messageHelper) {

        this.aiaRepositoryOperations = aiaRepositoryOperations;
        this.ilOperations = ilOperations;
        this.aiaSharedProxy = aiaSharedProxy;
        this.messageHelper = messageHelper;
    }

    @Override
    public List<ContextSourceDTO> getContextSources(String projectKey, String schemaType) {
        List<ContextSourceDTO> contextSources = new ArrayList<>();

        if(schemaType != null) {
            switch (schemaType) {
                case CACHE:
                    contextSources.add(contextSourcesForCache());
                    break;
                case EXTERNAL:
                    contextSources.addAll(contextSourcesForExternal());
                    break;
                case REFERENCE:
                    contextSources.addAll(contextSourcesForReference());
                    break;
                default:
                    break;
            }
        }
        else{
            contextSources.add(contextSourcesForCache());
            contextSources.addAll(contextSourcesForExternal());
            contextSources.addAll(contextSourcesForReference());
        }

        return contextSources;
    }

    @Override
    public List<BaseEntityDTO> searchContextEntitiesMetadata(String projectKey, EntitiesDTO entities) {
        List<BaseEntityDTO> baseEntityDTOs = new ArrayList<>();

        entities.getContextEntities().forEach(contextEntityRefDTO -> {
             if (ContextEntityRefDTO.TypeEnum.CACHE.name().equals(contextEntityRefDTO.getType().name())) {
                 baseEntityDTOs.add(loadCacheEntityAttributes(projectKey, contextEntityRefDTO));

             } else if (ContextEntityRefDTO.TypeEnum.EXTERNAL.name().equals(contextEntityRefDTO.getType().name())) {
                 baseEntityDTOs.add(loadExternalEntityAttributes(projectKey, contextEntityRefDTO));

             } else {
                 baseEntityDTOs.add(loadReferenceEntityAttributes(projectKey, contextEntityRefDTO));
             }
         });

        return baseEntityDTOs;
    }

    private BaseEntityDTO loadCacheEntityAttributes(String projectKey, ContextEntityRefDTO contextEntityRefDTO) {
        CacheReferenceEntityDTO cacheReferenceEntity = ilOperations.getCacheEntity(projectKey, contextEntityRefDTO.getEntityKey());
        return new BaseEntityDTO()
                .entityKey(cacheReferenceEntity.getCacheReferenceEntityKey())
                .entityName(cacheReferenceEntity.getCacheReferenceEntityName())
                .schemaKey(CACHE)
                .attributes(convertCacheAttributeToBaseAttribute(cacheReferenceEntity.getCacheReferenceAttributes()));
    }

    private List<BaseAttributeDTO> convertCacheAttributeToBaseAttribute(List<CacheReferenceAttributeDTO> cacheAttributes) {
        if (cacheAttributes == null) {
            return Collections.emptyList();
        }
        final Set<BaseAttributeDTO> baseAttributeDTOs = new HashSet<>(cacheAttributes.size());
        for (final CacheReferenceAttributeDTO cacheAttribute : cacheAttributes) {
            BaseAttributeDTO baseAttributeDTO = new BaseAttributeDTO();
            baseAttributeDTO.setAttributeKey(cacheAttribute.getAttributeKey());
            baseAttributeDTO.setAttributeName(cacheAttribute.getAttributeName());
            baseAttributeDTO.setDatatype(cacheAttribute.getType());
            baseAttributeDTO.setKeyPosition(cacheAttribute.getKeyPosition());

            baseAttributeDTOs.add(baseAttributeDTO);
        }
        return baseAttributeDTOs.stream().collect(Collectors.toList());
    }

    private BaseEntityDTO loadExternalEntityAttributes(String projectKey, ContextEntityRefDTO contextEntityRefDTO) {
        ExternalEntityDTO externalEntity = ilOperations.getExternalEntity(projectKey, contextEntityRefDTO.getSchemaKey(), contextEntityRefDTO.getEntityKey());
        return new BaseEntityDTO()
                .entityKey(externalEntity.getEntityKey())
                .entityName(externalEntity.getEntityName())
                .schemaKey(externalEntity.getSchemaKey())
                .attributes(convertExternalEntityAttributeToBaseAttribute(externalEntity.getAttributes()));
    }

    private List<BaseAttributeDTO> convertExternalEntityAttributeToBaseAttribute(List<ExternalAttributeDTO> externalAttributes) {
        if (externalAttributes == null) {
            return Collections.emptyList();
        }
        final Set<BaseAttributeDTO> baseAttributeDTOs = new HashSet<>(externalAttributes.size());
        for (final ExternalAttributeDTO externalAttribute : externalAttributes) {
            BaseAttributeDTO baseAttributeDTO = new BaseAttributeDTO();
            baseAttributeDTO.setAttributeKey(externalAttribute.getAttributeKey());
            baseAttributeDTO.setAttributeName(externalAttribute.getAttributeName());
            baseAttributeDTO.setDatatype(externalAttribute.getLogicalDatatype());
            baseAttributeDTO.setKeyPosition(externalAttribute.getKeyPosition());

            baseAttributeDTOs.add(baseAttributeDTO);
        }
        return baseAttributeDTOs.stream().collect(Collectors.toList());
    }

    private BaseEntityDTO loadReferenceEntityAttributes(String projectKey, ContextEntityRefDTO contextEntityRefDTO) {
        final List<EntityStore> entityStores = aiaSharedProxy.searchEntityStores(projectKey, String.format("logicalSchemaKey:%s AND logicalEntityKey:%s", contextEntityRefDTO.getSchemaKey(), contextEntityRefDTO.getEntityKey()));
        if(entityStores.isEmpty()){
            throw messageHelper.createElementNotFoundException(EntityStore.ELEMENT_TYPE_CODE, contextEntityRefDTO.getEntityKey());
        }
        EntityStore entityStore = entityStores.get(0);

        return new BaseEntityDTO()
                .entityKey(entityStore.getLogicalEntityKey())
                .entityName(entityStore.getName())
                .schemaKey(entityStore.getLogicalSchemaKey())
                .attributes(convertEntityStoreAttributeToBaseAttribute(entityStore.getAttributeStores()));
    }

    private List<BaseAttributeDTO> convertEntityStoreAttributeToBaseAttribute(List<AttributeStore> attributeStores) {
        if (attributeStores == null) {
            return Collections.emptyList();
        }
        final Set<BaseAttributeDTO> baseAttributeDTOs = new HashSet<>(attributeStores.size());
        for (final AttributeStore attributeStore : attributeStores) {
            BaseAttributeDTO baseAttributeDTO = new BaseAttributeDTO();
            baseAttributeDTO.setAttributeKey(attributeStore.getLogicalAttributeKey());
            baseAttributeDTO.setAttributeName(attributeStore.getName());
            baseAttributeDTO.setDatatype(attributeStore.getType());
            baseAttributeDTO.setKeyPosition(attributeStore.getKeyPosition());

            baseAttributeDTOs.add(baseAttributeDTO);
        }
        return baseAttributeDTOs.stream().collect(Collectors.toList());
    }

    private ContextSourceDTO contextSourcesForCache() {
        final List<Map<String, Object>> responseForCache = aiaRepositoryOperations.findElementsPropertiesByQuery(
                new SearchPropertiesQueryRequest(
                        "elementType:CacheEntity",
                        ImmutableSet.of(ENTITY_KEY, NAME)));
        return createContextSourceDTOForCache(responseForCache);
    }

    private List<ContextSourceDTO> contextSourcesForExternal() {
        final Map<String, List<Map<String, Object>>> responseForExternalEntities = aiaRepositoryOperations.findElementsPropertiesByQuery(
                new SearchPropertiesQueryRequest(
                        "elementType:ExternalEntity AND NOT availability:SHARED",
                        ImmutableSet.of(SCHEMA_KEY, ENTITY_KEY, NAME))).stream()
                .collect(Collectors.groupingBy(properties -> properties.get(SCHEMA_KEY).toString()));

        final List<ContextSourceDTO> responseForExternalSchema = aiaRepositoryOperations.findElementsPropertiesByQuery(
                new SearchPropertiesQueryRequest(
                        "elementType:ExternalSchema AND NOT availability:SHARED",
                        ImmutableSet.of(SCHEMA_KEY, NAME))).stream().map(properties -> createContextSourceDTOForExternal(properties, responseForExternalEntities.get(properties.get(SCHEMA_KEY).toString())))
                .collect(Collectors.toList());

        return responseForExternalSchema.stream().filter(contextSourceDTO -> contextSourceDTO.getContextSourceEntities() != null).collect(Collectors.toList());
    }

    private List<ContextSourceDTO> contextSourcesForReference() {
        final Map<String, List<Map<String, Object>>> responseForReferenceEntities = aiaRepositoryOperations.findElementsPropertiesByQuery(
                new SearchPropertiesQueryRequest(
                        "elementType:ENS AND messageCategory:Reference AND storeType:" + DATA_CHANNEL,
                        ImmutableSet.of(SCHEMA_STORE_KEY, ENTITY_STORE_KEY, NAME))).stream()
                .collect(Collectors.groupingBy(properties -> properties.get(SCHEMA_STORE_KEY).toString()));

        final List<ContextSourceDTO> responseForReference = aiaRepositoryOperations.findElementsPropertiesByQuery(
                new SearchPropertiesQueryRequest(
                        "elementType:SCS AND storeType:" + DATA_CHANNEL,
                        ImmutableSet.of(SCHEMA_STORE_KEY, NAME, LOGICAL_SCHEMA_KEY))).stream().map(properties -> createContextSourceDTOForReference(properties, responseForReferenceEntities.get(properties.get(SCHEMA_STORE_KEY).toString())))
                .collect(Collectors.toList());

        return responseForReference.stream().filter(contextSourceDTO -> contextSourceDTO.getContextSourceEntities() != null).collect(Collectors.toList());
    }

    private ContextSourceDTO createContextSourceDTOForExternal(Map<String, Object> schemaProperties, List<Map<String, Object>> entityProperties) {
        return new ContextSourceDTO()
                .schemaKey(schemaProperties.get(SCHEMA_KEY).toString())
                .schemaName(schemaProperties.getOrDefault(NAME, schemaProperties.get(SCHEMA_KEY)).toString())
                .schemaType(ContextSourceDTO.SchemaTypeEnum.EXTERNAL)
                .contextSourceEntities(entityProperties != null ? entityProperties.stream().map(this::createContextSourceEntityDTO).collect(Collectors.toList()) : null);
    }

    private ContextSourceDTO createContextSourceDTOForCache(List<Map<String, Object>> entityProperties) {
        return new ContextSourceDTO()
                .schemaKey(CACHE)
                .schemaName("Cached Entity")
                .schemaType(ContextSourceDTO.SchemaTypeEnum.CACHE)
                .contextSourceEntities(entityProperties != null ? entityProperties.stream().map(this::createContextSourceEntityDTO).collect(Collectors.toList()) : null);
    }

    private ContextSourceEntityDTO createContextSourceEntityDTO(Map<String, Object> entityProperties) {
        return new ContextSourceEntityDTO()
                .entityKey(entityProperties.get(ENTITY_KEY).toString())
                .entityName(entityProperties.get(NAME).toString());
    }

    private ContextSourceDTO createContextSourceDTOForReference(Map<String, Object> schemaProperties, List<Map<String, Object>> entityProperties) {
        return new ContextSourceDTO()
                .schemaKey(schemaProperties.get(LOGICAL_SCHEMA_KEY).toString())
                .schemaName(schemaProperties.get(NAME).toString())
                .schemaType(ContextSourceDTO.SchemaTypeEnum.REFERENCE)
                .contextSourceEntities(entityProperties != null ? entityProperties.stream().map(this::createContextSourceEntityDTOForReference).collect(Collectors.toList()) : null);
    }

    private ContextSourceEntityDTO createContextSourceEntityDTOForReference(Map<String, Object> entityProperties) {
        return new ContextSourceEntityDTO()
                .entityKey(entityProperties.get(ENTITY_STORE_KEY).toString())
                .entityName(entityProperties.get(NAME).toString());
    }
}
