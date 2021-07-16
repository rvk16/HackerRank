package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.logical.*;
import com.amdocs.aia.common.model.properties.PropertyData;
import com.amdocs.aia.common.model.store.AttributeStore;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.shared.client.AiaSharedOperations;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class AbstractEntityTransformationService<E extends EntityTransformationGridElementDTO>
        implements EntityTransformationService<E> {

    private static final String SOURCE_MAPPING = "SourceMapping";

    protected final DataChannelStoreType dataChannelStoreType;
    protected final AiaSharedOperations aiaSharedOperations;
    protected final ILOperations ilOperations;

    protected final MessageHelper messageHelper;

    AbstractEntityTransformationService(final DataChannelStoreType dataChannelStoreType,
                                        final AiaSharedOperations aiaSharedOperations,
                                        final ILOperations ilOperations,
                                        final MessageHelper messageHelper) {
        this.dataChannelStoreType = dataChannelStoreType;
        this.aiaSharedOperations = aiaSharedOperations;
        this.ilOperations = ilOperations;
        this.messageHelper = messageHelper;
    }

    /**
     * Finds and updates the shared {@link LogicalEntity logical entity} according to the attribute list.
     *
     * @param projectKey       The project key
     * @param logicalEntityKey LGE key
     * @param logicalSchemaKey LS key
     * @param attributes       List of transformation attributes
     * @return Updated logical entity
     * @throws ApiException When logical entity does not exist
     */
    protected LogicalEntity updateSharedLogicalEntity(final String projectKey, final String logicalEntityKey, final String logicalSchemaKey,
                                                      List<TransformationAttributeDTO> attributes) {
        LogicalEntity logicalEntity = aiaSharedOperations.searchLogicalEntityByEntityKeyAndSchemaKey(projectKey, logicalEntityKey, logicalSchemaKey)
                .orElseThrow(() -> messageHelper.createElementNotFoundException(LogicalEntity.ELEMENT_TYPE_CODE, logicalEntityKey));
        Set<String> existingAttributes = attributes.stream().map(TransformationAttributeDTO::getAttributeKey).collect(Collectors.toSet());
        logicalEntity.setAttributes(logicalEntity.getAttributes().stream().filter(attribute -> existingAttributes.contains(attribute.getAttributeKey())).collect(Collectors.toList()) );
        return logicalEntity;
    }


    /**
     * Finds and updates the shared {@link EntityStore entity store} according to the logical entity.
     *
     * @param projectKey     The project key
     * @param logicalEntity  The logical entity
     * @param createIfAbsent Whether to create the entity store if it's missing, or not
     * @return Updated entity store
     * @throws ApiException When 'createIfAbsent' is false and the physical model does not exist
     */
    protected EntityStore updateSharedEntityStore(final String projectKey, LogicalEntity logicalEntity,
                                                  List<TransformationAttributeDTO> transformationAttributes, boolean createIfAbsent) {
        final String logicalSchemaKey = logicalEntity.getSchemaKey();
        LogicalSchema logicalSchema = aiaSharedOperations.searchLogicalSchemaBySchemaKey(projectKey, logicalSchemaKey)
                .orElseThrow(() -> messageHelper.createElementNotFoundException(LogicalSchema.ELEMENT_TYPE_CODE, logicalSchemaKey));

        SchemaStore schemaStore = getSchemaStore(projectKey, logicalSchema, createIfAbsent);
        EntityStore entityStore = getEntityStore(projectKey, logicalEntity, schemaStore, createIfAbsent);
        entityStore.setAttributeStores(extractAttributeStoresFromEntityTransformation(transformationAttributes, AttributeStore.class));
        aiaSharedOperations.reportUpdate(entityStore);
        return entityStore;
    }



    private <A> List<A> extractAttributeStoresFromEntityTransformation(List<TransformationAttributeDTO> transformationAttributes, Class<A> attributeStoreClass) {
        AtomicInteger pkCount = new AtomicInteger();
        return transformationAttributes.stream()
                .sorted(Comparator.comparingInt(TransformationAttributeDTO::getSortOrder))
                .map(transformationAttribute -> {
                    Integer keyPosition = transformationAttribute.isKeyPosition() ? pkCount.incrementAndGet() : null;
                    return (A) (attributeStoreClass.equals(AttributeStore.class)
                            ? convertTransformationAttributeToAttributeStore(transformationAttribute, keyPosition)
                            : convertTransformationAttributeToAttributeStoreDTO(transformationAttribute, keyPosition));
                }).collect(Collectors.toList());
    }

    private AttributeStore convertTransformationAttributeToAttributeStore(TransformationAttributeDTO transformationAttribute, Integer keyPosition) {
        AttributeStore attributeStore = new AttributeStore();
        attributeStore.setLogicalAttributeKey(transformationAttribute.getAttributeKey());
        attributeStore.setType(dataChannelStoreType.getDefaultTypeSystem()
                .fromLogicalDatatype(parseLogicalDatatype(transformationAttribute.getType())));
        attributeStore.setIsLogicalTime(transformationAttribute.isIsLogicalTime());
        attributeStore.setIsUpdateTime(transformationAttribute.isIsUpdateTime());
        attributeStore.setIsRequired(transformationAttribute.isIsRequired());
        attributeStore.setKeyPosition(keyPosition);
        attributeStore.setName(transformationAttribute.getAttributeName());
        return attributeStore;
    }

    private static Datatype parseLogicalDatatype(String type) {
        try {
            return LogicalTypeSystem.parse(type);
        } catch (ParseException e) {
            throw new IllegalArgumentException(type);
        }
    }

    private AttributeStoreDTO convertTransformationAttributeToAttributeStoreDTO(TransformationAttributeDTO transformationAttribute, Integer keyPosition) {
        return new AttributeStoreDTO()
                .attributeStoreKey(transformationAttribute.getAttributeKey())
                .type(dataChannelStoreType.getDefaultTypeSystem()
                .fromLogicalDatatype(parseLogicalDatatype(transformationAttribute.getType())))
                .isLogicalTime(transformationAttribute.isIsLogicalTime())
                .isUpdateTime(transformationAttribute.isIsUpdateTime())
                .isRequired(transformationAttribute.isIsRequired())
                .keyPosition(keyPosition);
    }

    /**
     * Gets (or creates) the schema store corresponding to the logical schema.
     *
     * @param projectKey     The project key
     * @param logicalSchema  The logical schema
     * @param createIfAbsent Whether to create the schema store or not
     * @return The schema store
     * @throws ApiException When 'createIfAbsent' is false and the schema store does not exist
     */
    private SchemaStore getSchemaStore(final String projectKey, LogicalSchema logicalSchema, boolean createIfAbsent) {
        final String schemaStoreKey = dataChannelStoreType.generateSchemaStoreKeyForLogical(logicalSchema.getSchemaKey());
        return aiaSharedOperations.searchSchemaStoreBySchemaKey(projectKey, schemaStoreKey)
                .orElseGet(() -> {
                    if (createIfAbsent) {
                        return aiaSharedOperations.addSchemaStore(dataChannelStoreType.createSchemaStoreFromLogical(logicalSchema, logicalSchema.getSchemaKey()));
                    } else {
                        throw messageHelper.createElementNotFoundException(SchemaStore.ELEMENT_TYPE_CODE, schemaStoreKey);
                    }
                });
    }

    /**
     * Gets (or creates) the entity store corresponding to the logical entity.
     *
     * @param projectKey     The project key
     * @param logicalEntity  The logical entity
     * @param schemaStore    The target schema store
     * @param createIfAbsent Whether to create the entity store or not
     * @return The entity store
     * @throws ApiException When 'createIfAbsent' is false and the entity store does not exist
     */
    private EntityStore getEntityStore(final String projectKey, LogicalEntity logicalEntity, SchemaStore schemaStore, boolean createIfAbsent) {
        final String entityStoreKey = dataChannelStoreType.generateEntityStoreKeyForLogical(logicalEntity.getEntityKey());
        return aiaSharedOperations.searchEntityStoreByEntityKeyAndSchemaStore(projectKey, entityStoreKey, schemaStore.getSchemaStoreKey())
                .orElseGet(() -> {
                    if (createIfAbsent) {
                        return aiaSharedOperations.addEntityStore(dataChannelStoreType.createEntityStoreFromLogical(logicalEntity, schemaStore));
                    } else {
                        throw messageHelper.createElementNotFoundException(EntityStore.ELEMENT_TYPE_CODE, entityStoreKey);
                    }
                });
    }

    /**
     * Generates a {@link LogicalAttribute Logical Attribute} from a {@link TransformationAttributeDTO} Transformation Attribute DTO.
     *
     * @param transformationAttribute The DTO
     * @return The logical attribute represented by the DTO.
     */
    private static LogicalAttribute convertTransformationAttributeToLogicalAttribute(TransformationAttributeDTO transformationAttribute) {
        LogicalAttribute logicalAttribute = new LogicalAttribute();
        logicalAttribute.setAttributeKey(transformationAttribute.getAttributeKey());
        logicalAttribute.setName(transformationAttribute.getAttributeName());
        logicalAttribute.setDescription(transformationAttribute.getDescription());
        logicalAttribute.setSortOrder(transformationAttribute.getSortOrder());
        logicalAttribute.setOrigin(transformationAttribute.getOrigin());
        PropertyData sourceMapping = new PropertyData();
        sourceMapping.setValue(transformationAttribute.getSourceMapping());
        logicalAttribute.setProperty(SOURCE_MAPPING, sourceMapping);
        logicalAttribute.setDatatype(parseLogicalDatatype(transformationAttribute.getType()));
        if (transformationAttribute.getParentAttributeKey() != null) {
            AttributeRelation attributeRelation = new AttributeRelation();
            attributeRelation.setRelationKey(transformationAttribute.getAttributeKey());
            attributeRelation.setParentSchemaKey(transformationAttribute.getParentSchemaKey());
            attributeRelation.setParentEntityKey(transformationAttribute.getParentEntityKey());
            attributeRelation.setParentAttributeKey(transformationAttribute.getParentAttributeKey());
            logicalAttribute.setAttributeRelation(attributeRelation);
        }
        return logicalAttribute;
    }

    public static CacheReferenceAttributeDTO convertTransformationAttributeToCacheReferenceAttributes(TransformationAttributeDTO transformationAttribute){
        CacheReferenceAttributeDTO cacheReferenceAttributeDTO = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO.setAttributeKey(transformationAttribute.getAttributeKey());
        cacheReferenceAttributeDTO.setAttributeName(transformationAttribute.getAttributeName());
        cacheReferenceAttributeDTO.setDescription(transformationAttribute.getDescription());
        cacheReferenceAttributeDTO.setKeyPosition(transformationAttribute.isKeyPosition() ? 1 : null);
        cacheReferenceAttributeDTO.setType(transformationAttribute.getType());

        return cacheReferenceAttributeDTO;
    }

    public static TransformationAttributeDTO createTransformationAttributeDtoFromCacheReferenceAttributeDTO(CacheReferenceAttributeDTO cacheReferenceAttributeDTO){
        TransformationAttributeDTO transformationAttributeDTO = new TransformationAttributeDTO();
        transformationAttributeDTO.setAttributeKey(cacheReferenceAttributeDTO.getAttributeKey());
        transformationAttributeDTO.setAttributeName(cacheReferenceAttributeDTO.getAttributeName());
        transformationAttributeDTO.setDescription(cacheReferenceAttributeDTO.getDescription());
        transformationAttributeDTO.setKeyPosition(cacheReferenceAttributeDTO.getKeyPosition() != null);
        transformationAttributeDTO.setType(cacheReferenceAttributeDTO.getType());

        return transformationAttributeDTO;
    }
}