package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.logical.*;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;
import com.amdocs.aia.il.common.model.Relation;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.shared.client.AiaSharedOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.CACHE;
import static com.amdocs.aia.il.common.model.ConfigurationConstants.DATA_CHANNEL;

@Service
public class SharedEntityTransformationServiceImpl
        extends AbstractEntityTransformationService<SharedEntityTransformationGridElementDTO>
        implements SharedEntityTransformationService {


     SharedEntityTransformationServiceImpl(final DataChannelStoreType dataChannelStoreType,
                                          final AiaSharedOperations aiaSharedOperations,
                                          final ILOperations ilOperations,
                                          final MessageHelper messageHelper) {
        super(dataChannelStoreType, aiaSharedOperations, ilOperations, messageHelper);
    }

    @Override
    public List<SharedEntityTransformationGridElementDTO> list(String projectKey) {
        Set<SharedEntityTransformationGridElementDTO> sharedEntityTransformationGridElementDTOSet = new HashSet<>();
        Map<String, List<TransformationDTO>> transformationMap =  ilOperations.listTransformations(projectKey).stream().filter(transformationDTO -> !CACHE.equals(transformationDTO.getTargetSchemaStoreKey())).collect(Collectors.groupingBy(transformationDTO -> transformationDTO.getTargetEntityStoreKey()));
        for (final Map.Entry<String, List<TransformationDTO>> sharedEntity : transformationMap.entrySet()) {

            String targetSchemaName = sharedEntity.getValue().get(0).getTargetSchemaName();
            String targetEntityStoreKey = sharedEntity.getKey();
            LogicalEntity logicalEntity = getLogicalEntityByEntityKeyAndSchemaKey(projectKey,targetSchemaName,targetEntityStoreKey);

            sharedEntityTransformationGridElementDTOSet.add(toSharedEntityTransformationGridElementDTOList(logicalEntity, sharedEntity.getValue()));
       };
       List<SharedEntityTransformationGridElementDTO> sharedEntityTransformationGridElementDTOList = new ArrayList<>(sharedEntityTransformationGridElementDTOSet);
       return sharedEntityTransformationGridElementDTOList;
    }

    private SharedEntityTransformationGridElementDTO toSharedEntityTransformationGridElementDTOList( LogicalEntity logicalEntity, List<TransformationDTO> transformationDTOList) {
        SharedEntityTransformationGridElementDTO sharedEntityTransformationGridElementDTO = null;
         if (logicalEntity.getEntityType().equalsIgnoreCase(LogicalEntityType.MASTER.name()) ||
                logicalEntity.getEntityType().equalsIgnoreCase(LogicalEntityType.TRANSACTION.name()) ||
                logicalEntity.getEntityType().equalsIgnoreCase(LogicalEntityType.REFERENCE.name())) {

            sharedEntityTransformationGridElementDTO = (SharedEntityTransformationGridElementDTO) new SharedEntityTransformationGridElementDTO()
                    .entityTransformationType(EntityTransformationGridElementDTO.EntityTransformationTypeEnum.SHARED);
            sharedEntityTransformationGridElementDTO.setEntityKey(logicalEntity.getEntityKey());
            sharedEntityTransformationGridElementDTO.setEntityName(logicalEntity.getName());
            sharedEntityTransformationGridElementDTO.setEntityType(logicalEntity.getEntityType());

            if(transformationDTOList.stream().anyMatch(transformationDTO -> transformationDTO.getStatus().equals(ChangeStatusDTO.DRAFT))){
                sharedEntityTransformationGridElementDTO.setStatus(ChangeStatusDTO.DRAFT);
            }
            else if(transformationDTOList.stream().anyMatch(transformationDTO -> transformationDTO.getStatus().equals(ChangeStatusDTO.MODIFIED))){
                sharedEntityTransformationGridElementDTO.setStatus(ChangeStatusDTO.MODIFIED);
            }
            else
                sharedEntityTransformationGridElementDTO.setStatus(ChangeStatusDTO.PUBLISHED);

            sharedEntityTransformationGridElementDTO.setSubjectArea(logicalEntity.getSchemaKey());

        }
        return sharedEntityTransformationGridElementDTO;
    }

    @Override
    public EntityTransformationDTO get(String projectKey, String logicalEntityKey) {
        EntityTransformationDTO entityTransformationDTO = new EntityTransformationDTO();
        entityTransformationDTO.setLogicalEntityKey(logicalEntityKey);

        LogicalEntity logicalEntity = getLogicalEntity(projectKey, logicalEntityKey);

        String logicalSchemaKey = logicalEntity.getSchemaKey();

        entityTransformationDTO.setLogicalSchemaKey(logicalSchemaKey);

        List<TransformationAttributeDTO> transformationAttributeDTOList = ilOperations.listTransformationAttributes(projectKey, logicalSchemaKey, logicalEntityKey);
        if (!transformationAttributeDTOList.isEmpty()) {
            entityTransformationDTO.setAttributes(transformationAttributeDTOList);
        }
        EntityStore entityStore=null;
        try{
            entityStore = getEntityStore(projectKey,logicalEntityKey);
        }catch (ApiException apiException){
            return entityTransformationDTO;
        }
        entityTransformationDTO.setTransformations(ilOperations.getTransformationsByLogicalKey(projectKey ,entityStore.getSchemaStoreKey() ,entityStore.getEntityStoreKey()));
        List<ContextDTO> contexts = new ArrayList<>();
        if (!entityTransformationDTO.getTransformations().isEmpty()) {
            entityTransformationDTO.getTransformations().stream().filter(transformationDTO -> TransformationDTO.SourceTypeEnum.CONTEXT == transformationDTO.getSourceType()).forEach(transformationDTO ->{
                contexts.add(ilOperations.getContextsByStoreName(projectKey,transformationDTO.getContextKey()));
        });}
        entityTransformationDTO.setContexts(contexts);
        return entityTransformationDTO;
    }

    private LogicalEntity getLogicalEntity(String projectKey, String logicalEntityKey) {
        final List<LogicalEntity> logicalEntities = aiaSharedOperations.searchLogicalEntities(projectKey, String.format("entityKey:%s", logicalEntityKey));
        if (logicalEntities.isEmpty()) {
            throw messageHelper.createElementNotFoundException(LogicalEntity.ELEMENT_TYPE_CODE, logicalEntityKey);
        }
        return logicalEntities.get(0);
    }

    private LogicalEntity getLogicalEntityByEntityKeyAndSchemaKey (String projectKey, String logicalSchemaKey, String logicalEntityKey) {
        final Optional<LogicalEntity> logicalEntities = aiaSharedOperations.searchLogicalEntityByEntityKeyAndSchemaKey(projectKey,logicalEntityKey,logicalSchemaKey);
        if (!logicalEntities.isPresent()) {
            throw messageHelper.createElementNotFoundException(LogicalEntity.ELEMENT_TYPE_CODE, logicalEntityKey);
        }
        return logicalEntities.get();
    }

    @Override
    public void delete(String projectKey, String logicalEntityKey) {
        EntityStore entityStore = getEntityStore(projectKey, logicalEntityKey);
        ilOperations.deleteEntityTransformations(projectKey,  entityStore.getSchemaStoreKey(), entityStore.getEntityStoreKey());
    }

    private EntityStore getEntityStore(String projectKey, String logicalEntityKey) {
        final List<EntityStore> entityStores = aiaSharedOperations.searchEntityStores(projectKey, String.format("logicalEntityKey:%s AND storeType:" + DATA_CHANNEL, logicalEntityKey));
        if(entityStores.isEmpty()){
            throw messageHelper.createElementNotFoundException(EntityStore.ELEMENT_TYPE_CODE, logicalEntityKey);
        }
        return entityStores.get(0);
    }

    /**
     * Creates an entity transformation, as follows:
     * <ul>
     *     <li>Updates the {@link LogicalEntity SHARED Logical Entity}</li>
     *     <li>Creates/updates the {@link EntityStore SHARED Data Channel Entity Store}</li>
     *     <li>Creates/updates the IL {@link com.amdocs.aia.il.common.model.DataChannelEntityStore Data Channel Entity Store} and {@link com.amdocs.aia.il.common.model.DataChannelSchemaStore Data Channel Schema Store}</li>
     *     <li>Creates/updates the {@link com.amdocs.aia.il.common.model.EntityReferentialIntegrity Entity Referential Integrity}</li>
     *     <li>Creates/updates the IL {@link com.amdocs.aia.il.common.model.configuration.transformation.Context Contexts}</li>
     *     <li>Creates/updates the IL {@link com.amdocs.aia.il.common.model.configuration.transformation.Transformation Transformations}</li>
     * </ul>
     *
     * @param projectKey The project key
     * @param dto        The entity transformation
     * @return Same as DTO input
     */
    @Override
    public EntityTransformationDTO create(String projectKey, EntityTransformationDTO dto) {
        final String logicalEntityKey = dto.getLogicalEntityKey();
        final String logicalSchemaKey = dto.getLogicalSchemaKey();
        LogicalEntity logicalEntity = updateSharedLogicalEntity(projectKey, logicalEntityKey, logicalSchemaKey, dto.getAttributes());
        EntityStore entityStore = updateSharedEntityStore(projectKey, logicalEntity, dto.getAttributes(), true);
        updateEntityReferentialIntegrityService(projectKey, dto);
        updateContextService(projectKey, dto);
        updateTransformationService(projectKey, dto, entityStore);

        return dto;
    }

    /**
     * Updates an existing entity transformation, as follows:
     * <ul>
     *     <li>Updates the {@link LogicalEntity SHARED Logical Entity}</li>
     *     <li>Updates the {@link EntityStore SHARED Data Channel Entity Store}</li>
     *     <li>Updates the IL {@link com.amdocs.aia.il.common.model.DataChannelEntityStore Data Channel Entity Store} and {@link com.amdocs.aia.il.common.model.DataChannelSchemaStore Data Channel Schema Store}</li>
     *     <li>Updates the {@link com.amdocs.aia.il.common.model.EntityReferentialIntegrity Entity Referential Integrity}</li>
     *     <li>Updates/deletes the IL {@link com.amdocs.aia.il.common.model.configuration.transformation.Transformation Transformations} associated with this logical entity</li>
     *     <li>Creates/updates the IL {@link com.amdocs.aia.il.common.model.configuration.transformation.Context Contexts} associated with this logical entity</li>
     * </ul>
     *
     * @param projectKey The project key
     * @param dto        The entity transformation
     * @return Same as DTO input
     */
    @Override
    public EntityTransformationDTO update(final String projectKey, EntityTransformationDTO dto) {
        final String logicalEntityKey = dto.getLogicalEntityKey();
        final String logicalSchemaKey = dto.getLogicalSchemaKey();

        LogicalEntity logicalEntity = updateSharedLogicalEntity(projectKey, logicalEntityKey, logicalSchemaKey, dto.getAttributes());
        EntityStore entityStore = updateSharedEntityStore(projectKey, logicalEntity, dto.getAttributes(), false);
        updateEntityReferentialIntegrityService(projectKey, dto);
        updateContextService(projectKey, dto);
        updateTransformationService(projectKey, dto, entityStore);

        return dto;
    }

    /**
     * m
     * Updates the {@link TransformationConfigurationService IL transaction service}
     * according to the {@link EntityTransformationDTO entity transformation} (create/update/delete).
     *
     * @param projectKey           The project key
     * @param entityTransformation The entity transformation
     */
    private void updateTransformationService(final String projectKey, EntityTransformationDTO entityTransformation, EntityStore entityStore) {

        generateKeys(entityTransformation.getTransformations().stream().filter(transformationDTO -> transformationDTO.getId() == null).collect(Collectors.toList()), entityStore);

        Map<String, TransformationDTO> repositoryTransformations = ilOperations.listTransformations(projectKey).stream()
                .filter(tx -> tx.getTargetEntityStoreKey().equals(entityTransformation.getLogicalEntityKey()))
                .collect(Collectors.toMap(TransformationDTO::getStoreName, Function.identity()));
        Map<String, TransformationDTO> dtoTransformations = entityTransformation.getTransformations().stream()
                .collect(Collectors.toMap(TransformationDTO::getId, Function.identity()));

        updateReferentialIntegrityIfNeeded(projectKey, entityTransformation, dtoTransformations.values());
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
        dtoTransformations.values().forEach(transformationDTO -> ilOperations.saveTransformation(projectKey, transformationDTO));

        // Transactions only in repo -> delete
        repositoryTransformations.keySet().forEach(txId -> ilOperations.deleteTransformation(projectKey, txId));
    }

    private void generateKeys(List<TransformationDTO> transformationDTOList, EntityStore entityStore) {

        transformationDTOList.forEach(transformationDTO -> {
            transformationDTO.setTargetSchemaStoreKey(entityStore.getSchemaStoreKey());
            transformationDTO.setTargetEntityStoreKey(entityStore.getEntityStoreKey());
            transformationDTO.setStoreName(entityStore.getSchemaStoreKey() + "-" + entityStore.getEntityStoreKey() + "-" + transformationDTO.getContextKey());
            transformationDTO.setId(transformationDTO.getStoreName());

        });

    }

    private void updateReferentialIntegrityIfNeeded(String projectKey, EntityTransformationDTO entityTransformation, Collection<TransformationDTO> dtoTransformations) {
        final EntityReferentialIntegrity entityReferentialIntegrity = ilOperations.getEntityReferentialIntegrity(projectKey,entityTransformation.getLogicalSchemaKey(),entityTransformation.getLogicalEntityKey());


        if(entityReferentialIntegrity != null) {
            List <Relation> relations = entityReferentialIntegrity.getRelations();
            Optional<LogicalEntity> logicalEntity = aiaSharedOperations.searchLogicalEntityByEntityKeyAndSchemaKey(projectKey, entityTransformation.getLogicalSchemaKey(), entityTransformation.getLogicalEntityKey());
            if (logicalEntity.isPresent()) {
                List<LogicalAttribute> logicalAttributesWithRelation = logicalEntity.get().getAttributes().stream().filter(logicalAttribute -> logicalAttribute.getAttributeRelation() != null).collect(Collectors.toList());
                final Map<String, AttributeRelation> logicalRelationByAttKey = logicalAttributesWithRelation.stream().collect(Collectors.toMap(LogicalAttribute::getAttributeKey, logicalAttribute -> logicalAttribute.getAttributeRelation()));
                if (!relations.isEmpty() && !logicalRelationByAttKey.isEmpty()) {
                    relations.stream().filter(relation -> logicalRelationByAttKey.containsKey(relation.getAttributeKey())).forEach(relation -> {
                        if (compareRelation(relation, logicalRelationByAttKey.get(relation.getAttributeKey()))) {
                            dtoTransformations.forEach(tx -> tx.addReferenceAttributesItem(relation.getAttributeKey()));
                        }
                    });
                }
            }
        }
    }

    private boolean compareRelation(Relation relation, AttributeRelation logicalRelation) {
        return (relation.getParentAttributeKey().equals(logicalRelation.getParentAttributeKey()) &&
                relation.getParentEntityKey().equals(logicalRelation.getParentEntityKey()) &&
                relation.getParentSchemaKey().equals(logicalRelation.getParentSchemaKey()));

    }

    /**
     * Updates the {@link ContextConfigurationService IL context service}
     * according to the {@link EntityTransformationDTO entity transformation} (create/update).
     *
     * @param projectKey           The project key
     * @param entityTransformation The entity transformation
     */
    private void updateContextService(final String projectKey, EntityTransformationDTO entityTransformation) {
        Map<String, ContextDTO> repositoryContexts = ilOperations.listContexts(projectKey).stream()
                .collect(Collectors.toMap(ContextDTO::getStoreName, Function.identity()));

        entityTransformation.getContexts()
                .forEach(ctx -> {
                    if (repositoryContexts.containsKey(ctx.getStoreName())) {
                        ilOperations.updateContext(projectKey, ctx.getStoreName(), ctx);
                    } else {
                        ilOperations.saveContext(projectKey, ctx);
                    }
                });
    }

    private void updateEntityReferentialIntegrityService(final String projectKey, EntityTransformationDTO entityTransformation) {
        ilOperations.updateEntityReferentialIntegrity(projectKey, extractERIFromEntityTransformation(entityTransformation));
    }

    /**
     * Generates an {@link EntityReferentialIntegrity EntityReferentialIntegrity} from the {@link EntityTransformationDTO Entity Transformation}.
     *
     * @param entityTransformation  The entity transformation
     * @return The generated EntityReferentialIntegrity object
     */
    private static EntityReferentialIntegrity extractERIFromEntityTransformation(EntityTransformationDTO entityTransformation) {
        return new EntityReferentialIntegrity()
                .setLogicalSchemaKey(entityTransformation.getLogicalSchemaKey())
                .setLogicalEntityKey(entityTransformation.getLogicalEntityKey())
                .setRelations(entityTransformation.getAttributes().stream()
                        .filter(TransformationAttributeDTO::isDoReferencialIntegrity)
                        .map(SharedEntityTransformationServiceImpl::convertTransformationAttributeToRelation)
                        .collect(Collectors.toList()));
    }

    /**
     * Converts a single {@link TransformationAttributeDTO Transformation Attribute} to a {@link Relation Relation}.
     *
     * @param transformationAttribute The transformation attribute DTO
     * @return Post-conversion Relation
     */
    private static Relation convertTransformationAttributeToRelation(TransformationAttributeDTO transformationAttribute) {
        return new Relation()
                .setAttributeKey(transformationAttribute.getAttributeKey())
                .setParentAttributeKey(transformationAttribute.getParentAttributeKey())
                .setParentEntityKey(transformationAttribute.getParentEntityKey())
                .setParentSchemaKey(transformationAttribute.getParentSchemaKey());
    }
}