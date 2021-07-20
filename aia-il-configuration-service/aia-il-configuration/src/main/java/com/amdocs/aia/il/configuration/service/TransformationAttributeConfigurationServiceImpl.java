package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.utils.ValidityStatus;
import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.logical.AttributeRelation;
import com.amdocs.aia.common.model.logical.LogicalAttribute;
import com.amdocs.aia.common.model.logical.LogicalEntity;
import com.amdocs.aia.common.model.logical.LogicalSchema;
import com.amdocs.aia.common.model.store.AttributeStore;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.common.model.store.SharedStores;
import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;
import com.amdocs.aia.il.common.model.Relation;
import com.amdocs.aia.il.configuration.dto.TransformationAttributeDTO;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.shared.AiaSharedProxy;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.DATA_CHANNEL;

@Component
@ComponentScan(basePackages = {"com.amdocs.aia.shared.client, com.amdocs.aia.common.model"})
public class TransformationAttributeConfigurationServiceImpl<L extends TransformationAttributeDTO>
    implements TransformationAttributeConfigurationService<L> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransformationAttributeConfigurationServiceImpl.class);

    private static final String SEARCH_SCHEMA_STORES_QUERY = "logicalSchemaKey:%s AND storeType:%s";
    private final AiaSharedProxy aiaSharedProxy;
    private final MessageHelper messageHelper;
    private final EntityReferentialIntegrityService entityReferentialIntegrityService;
    private final DataChannelStoreType dataChannelStoreType;

    @Autowired
    public TransformationAttributeConfigurationServiceImpl(AiaSharedProxy aiaSharedProxy, MessageHelper messageHelper,
                                                           EntityReferentialIntegrityService entityReferentialIntegrityService,
                                                           DataChannelStoreType dataChannelStoreType) {
        this.aiaSharedProxy = aiaSharedProxy;
        this.messageHelper = messageHelper;
        this.entityReferentialIntegrityService = entityReferentialIntegrityService;
        this.dataChannelStoreType = dataChannelStoreType;
    }

    @Override
    public List<L> getAvailableAttributes(String projectKey, String logicalSchemaKey, String logicalEntityKey) {
        LogicalEntity logicalEntity = getLogicalEntityByEntityKeyAndSchemaKey(projectKey, logicalEntityKey, logicalSchemaKey);

        LogicalSchema logicalSchema = getLogicalSchemaBySchemaKey(projectKey, logicalSchemaKey);
        if(logicalSchema == null){
            LOGGER.error("Logical schema related to schema key {} not found", logicalSchemaKey);
            return Collections.emptyList();
        }

        SchemaStore schemaStore = dataChannelStoreType.createSchemaStoreFromLogical(logicalSchema, logicalSchema.getSchemaKey());
        EntityStore entityStore = dataChannelStoreType.createEntityStoreFromLogical(logicalEntity, schemaStore);

        List<TransformationAttributeDTO> result = getTransformationAttributeDtoListEntityStoreAndLogicalEntity(projectKey, logicalSchemaKey,
                logicalEntityKey, entityStore, schemaStore);
        return (List<L>) result;
    }

    @Override
    public List<L> list(String projectKey, String logicalSchemaKey, String logicalEntityKey) {
        EntityStore entityStore = getEntityStoreByLogicalEntityKey(projectKey, logicalEntityKey);
        List<TransformationAttributeDTO> result = getTransformationAttributeDtoListEntityStoreAndLogicalEntity(projectKey, logicalSchemaKey,
            logicalEntityKey, entityStore, null);
        return (List<L>) result;
    }

    private List<TransformationAttributeDTO> getTransformationAttributeDtoListEntityStoreAndLogicalEntity(String projectKey,
        String logicalSchemaKey, String logicalEntityKey, EntityStore entityStore, SchemaStore dummySchemaStore) {
        List<TransformationAttributeDTO> result = new ArrayList<>();
        LogicalSchema logicalSchema = getLogicalSchemaBySchemaKey(projectKey, logicalSchemaKey);
        if (logicalSchema == null) {
            throw messageHelper.createElementNotFoundException(LogicalSchema.ELEMENT_TYPE_CODE, logicalSchemaKey);
        }
        SchemaStore schemaStore = getDataChannelSchemaStore(logicalSchema);
        if (schemaStore == null){
            schemaStore = dummySchemaStore;
        }
        if (schemaStore == null || entityStore == null) {
            return result;
        }
        LogicalEntity logicalEntity = getLogicalEntityByEntityKeyAndSchemaKey(projectKey, logicalEntityKey, logicalSchemaKey);
        if (logicalEntity == null) {
            throw messageHelper.createElementNotFoundException(LogicalEntity.ELEMENT_TYPE_CODE, logicalEntityKey);
        }

        Map<String,Relation> relationMap = new HashMap<>();

        Optional<EntityReferentialIntegrity> entityReferentialIntegrity = entityReferentialIntegrityService.get(projectKey, schemaStore.getLogicalSchemaKey(),
            entityStore.getLogicalEntityKey());
        entityReferentialIntegrity.ifPresent(eri -> eri.getRelations().stream().forEach( relation -> {
            relationMap.put(relation.getAttributeKey(),relation);
        }));

        logicalEntity.getAttributes()
            .forEach(logicalAttribute -> entityStore.getAttributeStores()
                .forEach(attributeStore -> {
                    if (logicalAttribute.getAttributeKey().equals(attributeStore.getLogicalAttributeKey())) {
                        result.add(createTransformationAttributeDtoFromLogicalAttribute(logicalAttribute, attributeStore,
                                relationMap.get(logicalAttribute.getAttributeKey())));
                    }
                })
            );
        return result;
    }

    private LogicalSchema getLogicalSchemaBySchemaKey(String projectKey, String logicalSchemaKey) {
        return aiaSharedProxy.searchLogicalSchemaBySchemaKey(projectKey, logicalSchemaKey).orElse(null);
    }

    private LogicalEntity getLogicalEntityByEntityKeyAndSchemaKey(String projectKey, String logicalEntityKey, String logicalSchemaKey) {
        return aiaSharedProxy.searchLogicalEntityByEntityKeyAndSchemaKey(projectKey, logicalEntityKey, logicalSchemaKey).orElse(null);
    }

    private EntityStore getEntityStoreByLogicalEntityKey(String projectKey, String logicalEntityKey) {
        final List<EntityStore> entityStores = aiaSharedProxy.searchEntityStores(projectKey, String.format("logicalEntityKey:%s AND storeType:" + DATA_CHANNEL, logicalEntityKey));
        if(entityStores.isEmpty()){
            return null;
        }
        return entityStores.get(0);
    }

    private SchemaStore getDataChannelSchemaStore(LogicalSchema logicalSchema) {
        List<SchemaStore> schemaStores = aiaSharedProxy.searchSchemaStores(logicalSchema.getProjectKey(),
            String.format(SEARCH_SCHEMA_STORES_QUERY, logicalSchema.getSchemaKey(), SharedStores.DataChannel.STORE_TYPE));
        if (schemaStores.isEmpty()) {
            return null;
        } else {
            return schemaStores.get(0);
        }
    }

    private TransformationAttributeDTO createTransformationAttributeDtoFromLogicalAttribute(final LogicalAttribute logicalAttribute,
        final AttributeStore attributeStore, Relation relation) {

        TransformationAttributeDTO dto = new TransformationAttributeDTO()
            .attributeKey(attributeStore.getLogicalAttributeKey())
            .attributeName(attributeStore.getName())
            .description(attributeStore.getDescription())

            .type(LogicalTypeSystem.format(logicalAttribute.getDatatype()))

            .origin(attributeStore.getOrigin());
        if (logicalAttribute.getProperties() != null && logicalAttribute.getProperties().containsKey("SourceMapping")) {
            dto.sourceMapping(logicalAttribute.getProperties().get("SourceMapping").getValue().toString());
        }
        dto.keyPosition(attributeStore.getKeyPosition() != null)
            .sortOrder(logicalAttribute.getSortOrder())
            .isLogicalTime(attributeStore.getIsLogicalTime())
            .isUpdateTime(attributeStore.getIsUpdateTime())
            .isRequired(attributeStore.getIsRequired());

        dto.doReferencialIntegrity(relation != null);
        if(Boolean.TRUE.equals(dto.isDoReferencialIntegrity()) && relation != null){
            dto.parentSchemaKey(relation.getParentSchemaKey())
               .parentEntityKey(relation.getParentEntityKey())
               .parentAttributeKey(relation.getParentAttributeKey());
        }
        else if (logicalAttribute.getAttributeRelation() != null) {
            AttributeRelation attributeRelation = logicalAttribute.getAttributeRelation();
            dto.parentSchemaKey(attributeRelation.getParentSchemaKey())
                .parentEntityKey(attributeRelation.getParentEntityKey())
                .parentAttributeKey(attributeRelation.getParentAttributeKey());

        }

        validate(dto);

        return dto;
    }

    private void validate(final TransformationAttributeDTO dto) {
        String errorMessage = "";

        if (checkEmptyOrNull(dto.getAttributeKey()) || checkEmptyOrNull(dto.getAttributeName()) || checkEmptyOrNull(dto.getType())) {
            errorMessage = String.format("Attribute key %s, Attribute name %s, Attribute type %s, is empty or null", dto.getAttributeKey(),
                dto.getAttributeName(), dto.getType());
            throw messageHelper.createAttributesValidationException(new ValidityStatus(),
                AiaApiMessages.GENERAL.VALIDATION_ERROR_MISSING_REQUIRED_ATTRIBUTES, errorMessage);
        }

        final boolean withoutRelation =
            checkEmptyOrNull(dto.getParentSchemaKey()) && checkEmptyOrNull(dto.getParentEntityKey()) && checkEmptyOrNull(
                dto.getParentAttributeKey());
        final boolean withRelation = (!checkEmptyOrNull(dto.getParentSchemaKey()) && !checkEmptyOrNull(dto.getParentEntityKey())
            && !checkEmptyOrNull(dto.getParentAttributeKey()));

        if (withoutRelation == withRelation) {
            errorMessage = "relations parameters is not valid";
            throw messageHelper.createAttributesValidationException(new ValidityStatus(),
                AiaApiMessages.GENERAL.VALIDATION_ERROR_MISSING_RELATION_ATTRIBUTES, errorMessage);
        }
    }

    private boolean checkEmptyOrNull(final String attribute) {
        return attribute == null || attribute.isEmpty();
    }
}