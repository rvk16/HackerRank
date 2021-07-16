package com.amdocs.aia.il.common.model.configuration.transformation;

import com.amdocs.aia.il.common.model.publisher.PublisherEntityStore;
import com.amdocs.aia.il.common.model.publisher.PublisherSchemaStore;
import com.amdocs.aia.il.common.model.configuration.entity.*;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ReplicaStoreConfiguration;
import com.amdocs.aia.il.common.model.IntegrationLayerAttributeStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith({MockitoExtension.class})
class ModelContextEntityTest {

    @Mock
    private ModelContextEntity contextEntity;
    @Mock
    private ReplicaStoreConfiguration publisherTableConfiguration;

    private ContextEntity leadingEntity;
    private ContextEntity childEntity;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        contextEntity = new ModelContextEntity("Testing");
        Map<String, PublishEntity> publishEntityMap = new HashMap<>();
        publishEntityMap.put("Testing_BCMAPP", getPublishEntityMock());
        //publisherTableConfiguration = getPublisherTableConfiguration("CM_USR_GRP_MEMBERS");
        Map<String, String> schemaNameToDataChannelMap = getMockSchemaNameToDataChannelMap();
        leadingEntity = getMockedPublisherContextEntity(ContextEntityRelationType.LEAD, true, null, null, "BCMAPP", "SUBSCRIBER", "S");
        childEntity = getMockedPublisherContextEntity(ContextEntityRelationType.OTM, false, "LINK_PREV_SUB_NO", "S-SUBSCRIBER", "BCMAPP", "CM_USR_GRP_MEMBERS", "M");
        FieldSetter.setField(contextEntity, contextEntity.getClass().getDeclaredField("context"), getMockedPublisherContext());
        FieldSetter.setField(contextEntity, contextEntity.getClass().getDeclaredField("publishEntity"), publishEntityMap);
        FieldSetter.setField(contextEntity, contextEntity.getClass().getDeclaredField("schemaNameToDataChannelMap"), schemaNameToDataChannelMap);
    }

    private Map<String, String> getMockSchemaNameToDataChannelMap() {
        Map<String, String> mockSchemaNameToDataChannelMap = new HashMap<>();
        mockSchemaNameToDataChannelMap.put("BCMAPP","BCMAPP");
        return mockSchemaNameToDataChannelMap;
    }

    @Test
    void test_get_relation_name() {
        contextEntity.getRelationName(leadingEntity, childEntity);
        String relationName = leadingEntity.getEntityStoreKey() + '_' + childEntity.getEntityStoreKey();
        assertEquals("SUBSCRIBER_CM_USR_GRP_MEMBERS", relationName);
    }

    @Test
    void test_get_Parent_Relation() {
        ContextEntity result = contextEntity.getParentRelation(childEntity);
        assertEquals(leadingEntity.getEntityStoreKey(), result.getEntityStoreKey());
    }

    @Test
    void test_get_do_propagation_mode() {
        RelationPropagationMode resultChild = contextEntity.getRelationPropagationMode(childEntity);
        RelationPropagationMode resultParent = contextEntity.getRelationPropagationMode(leadingEntity);
        assertEquals(RelationPropagationMode.NONE, resultChild);
        assertEquals(RelationPropagationMode.PROPAGATE, resultParent);
    }

    @Test
    void test_convert_dataProcessingRelation_Lead_Table() {
        DataProcessingContextRelation result = contextEntity.convertToDataProcessingContextRelation(leadingEntity, Collections.emptyMap());
        assertEquals(getMockedPublisherContext().getContextKey() + '_' + leadingEntity.getEntityStoreKey(), result.getName());
        assertEquals(leadingEntity.getEntityStoreKey(), result.getTableInfo());
        assertNull(result.getParentRelation());
        assertNull(result.getKeyColumns());
        assertNull(result.getParentKeyColumns());
        assertEquals(RelationPropagationMode.NONE, result.getPropagationMode());
        assertEquals(RelationType.ROOT, result.getType());
    }

    @Test
    void test_convert_dataProcessingRelation_OTM_Table() {
        DataProcessingContextRelation result = contextEntity.convertToDataProcessingContextRelation(childEntity, Collections.emptyMap());
        assertEquals("SUBSCRIBER_CM_USR_GRP_MEMBERS_LINK_PREV_SUB_NO", result.getName());
        assertEquals(RelationType.ONE_TO_MANY, result.getType());
    }

    private static ContextEntity getMockedPublisherContextEntity(ContextEntityRelationType type, boolean doPropagation,
                                                                 String foreignKeys, String parentContextEntityKey,
                                                                 String schemaStoreKey, String entityStoreKey, String sourceALias) {
        ContextEntity publisherContextEntity = new ContextEntity();
        publisherContextEntity.setAliasedSourceEntityKey("aliasedSourceEntityKey");
        publisherContextEntity.setSchemaStoreKey(schemaStoreKey);
        publisherContextEntity.setEntityStoreKey(entityStoreKey);
        publisherContextEntity.setSourceAlias(sourceALias);
        publisherContextEntity.setParentContextEntityKey(parentContextEntityKey);
        publisherContextEntity.setForeignKeys(foreignKeys);
        publisherContextEntity.setNoReferentAction(NoReferentAction.MANDATORY);
        publisherContextEntity.setDoPropagation(doPropagation);
        publisherContextEntity.setRelationType(type);
        return publisherContextEntity;
    }

    private Context getMockedPublisherContext() {
        Context context = new Context();
        context.setContextKey("Subscriber");
        List<ContextEntity> contextEntitesList = new ArrayList<>();
        contextEntitesList.add(leadingEntity);
        contextEntitesList.add(childEntity);
        context.setContextEntities(contextEntitesList);
        return context;
    }

    private static PublishEntity getPublishEntityMock() {
        PublishEntity publishEntity = new PublishEntity("Testing");
        PublisherSchemaStore schemaStore = new PublisherSchemaStore();
        schemaStore.setSchemaStoreKey("BCMAPPPublisherStore");

        PublisherEntityStore entityStore = new PublisherEntityStore();
        entityStore.setEntityStoreKey("Subscriber");
        entityStore.setEntityName("Subscriber");
        List<IntegrationLayerAttributeStore> attributeStores = new ArrayList<>();
        IntegrationLayerAttributeStore attributeStore = new IntegrationLayerAttributeStore();
        attributeStore.setName("OBJID");
        attributeStore.setAttributeStoreKey("OBJID");
        attributeStore.setLogicalTime(false);
        attributeStore.setUpdateTime(false);
        attributeStores.add(attributeStore);
        attributeStore.setKeyPosition(1);
        attributeStore.setType("DOUBLE");
        entityStore.setAttributeStores(attributeStores);

        publishEntity.put(entityStore, schemaStore);
        return publishEntity;
    }

    private static List<ColumnConfiguration> getColumns() {
        List<ColumnConfiguration> columnConfigurations = new ArrayList<>();
        ColumnConfiguration columnConfiguration = new ColumnConfiguration();
        columnConfiguration.setColumnName("OBJID");
        columnConfiguration.setLogicalTime(false);
        columnConfigurations.add(columnConfiguration);
        return columnConfigurations;
    }
}