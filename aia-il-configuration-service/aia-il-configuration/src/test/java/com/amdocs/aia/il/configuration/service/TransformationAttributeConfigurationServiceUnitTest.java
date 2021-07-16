package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AIAAPIMessageTemplate;
import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.logical.*;
import com.amdocs.aia.common.model.properties.PropertyData;
import com.amdocs.aia.common.model.store.AttributeStore;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.common.model.store.SharedStores;
import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;
import com.amdocs.aia.il.configuration.dto.TransformationAttributeDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.shared.AiaSharedProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.DATA_CHANNEL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransformationAttributeConfigurationServiceUnitTest {
    private static final String SEARCH_SCHEMA_STORES_QUERY = "logicalSchemaKey:%s AND storeType:%s";
    private static final String PROJECT_KEY = "aia";

    @InjectMocks
    private TransformationAttributeConfigurationServiceImpl service;

    @Mock
    private AiaSharedProxy aiaSharedProxy;

    @Mock
    private MessageHelper messageHelper;

    @Mock
    EntityReferentialIntegrityService   entityReferentialIntegrityService;

    @Mock
    DataChannelStoreType dataChannelStoreType;


    @Mock
    TransformationAttributeConfigurationServiceImpl transformationAttributeConfigurationService;

    EntityReferentialIntegrity entityReferentialIntegrityMock = mock(EntityReferentialIntegrity.class);


    @Test
    void getTransformationAttributeReturned() {
        LogicalSchema logicalSchema = createLogicalSchemaForTest();
        SchemaStore schemaStore = createSchemaStoreForTest();
        final List<SchemaStore> schemaStoreList = new ArrayList<>();
        schemaStoreList.add(schemaStore);
        LogicalEntity logicalEntity = createLogicalEntityForTest();
        final List<EntityStore> entityStores = new ArrayList<>();
        EntityStore entityStore = createEntityStoreForTest();
        entityStores.add(entityStore);

        when(aiaSharedProxy.searchLogicalSchemaBySchemaKey(eq(PROJECT_KEY), eq(logicalSchema.getSchemaKey()))).thenReturn(Optional.of(logicalSchema));
        when(aiaSharedProxy.searchSchemaStores(eq(PROJECT_KEY), eq(String.format(SEARCH_SCHEMA_STORES_QUERY, logicalSchema.getSchemaKey(), SharedStores.DataChannel.STORE_TYPE)))).thenReturn(schemaStoreList);
        when(aiaSharedProxy.searchLogicalEntityByEntityKeyAndSchemaKey(eq(PROJECT_KEY), eq(logicalEntity.getEntityKey()), eq(logicalSchema.getSchemaKey()))).thenReturn(Optional.of(logicalEntity));
        when(entityReferentialIntegrityService.get(eq(PROJECT_KEY), eq(schemaStore.getSchemaStoreKey()),
            eq(schemaStore.getEntityStores().get(0).getEntityStoreKey()))).thenReturn(Optional.of(entityReferentialIntegrityMock));
        when(aiaSharedProxy.searchEntityStores(eq(PROJECT_KEY), eq(String.format("logicalEntityKey:%s AND storeType:" + DATA_CHANNEL, logicalEntity.getEntityKey())))).thenReturn(entityStores);

        List <TransformationAttributeDTO> result = service.list(PROJECT_KEY, logicalSchema.getSchemaKey(),logicalEntity.getEntityKey());
        assertEquals(2, result.size());
        for (TransformationAttributeDTO dto : result){
            if(dto.getAttributeKey().equals("mainContactKey")){
                assertEquals(dto.getAttributeName(),"Main Contact Key");
                assertEquals(dto.getDescription(),"mainContactKey description test");
                assertEquals(dto.getType(),"STRING");
                assertEquals(dto.getParentSchemaKey(),"aLDMCustomer");
                assertEquals(dto.getParentEntityKey(), "Contact");
                assertEquals(dto.getParentAttributeKey(),"contactKey");
                assertEquals(dto.getSourceMapping(),"test source mapping");
                assertEquals(dto.getSortOrder(),1000);
                assertTrue(dto.isIsRequired());
                assertFalse(dto.isIsLogicalTime());
                assertFalse(dto.isIsUpdateTime());
            } else {
                assertEquals(dto.getAttributeKey(),"bssModificationDate");
                assertEquals(dto.getAttributeName(),"BSS Modification Date");
                assertEquals(dto.getType(),"TIMESTAMP");
                assertNull(dto.getParentSchemaKey());
                assertNull(dto.getParentEntityKey());
                assertNull(dto.getParentAttributeKey());
                assertTrue(dto.isIsLogicalTime());
                assertFalse(dto.isIsUpdateTime());
                assertFalse(dto.isIsRequired());
            }

        }
    }

    @Test
    void getTransformationAvailableAttributeReturned() {
        LogicalSchema logicalSchema = createLogicalSchemaForTest();
        SchemaStore schemaStore = createSchemaStoreForTest();
        final List<SchemaStore> schemaStoreList = new ArrayList<>();
        schemaStoreList.add(schemaStore);
        LogicalEntity logicalEntity = createLogicalEntityForTest();
        EntityStore entityStore = createEntityStoreForTest();

        when(aiaSharedProxy.searchLogicalSchemaBySchemaKey(eq(PROJECT_KEY), eq(logicalSchema.getSchemaKey()))).thenReturn(Optional.of(logicalSchema));
        when(aiaSharedProxy.searchSchemaStores(eq(PROJECT_KEY), eq(String.format(SEARCH_SCHEMA_STORES_QUERY, logicalSchema.getSchemaKey(), SharedStores.DataChannel.STORE_TYPE)))).thenReturn(schemaStoreList);
        when(aiaSharedProxy.searchLogicalEntityByEntityKeyAndSchemaKey(eq(PROJECT_KEY), eq(logicalEntity.getEntityKey()), eq(logicalSchema.getSchemaKey()))).thenReturn(Optional.of(logicalEntity));
        when(entityReferentialIntegrityService.get(eq(PROJECT_KEY), eq(schemaStore.getSchemaStoreKey()),
                eq(schemaStore.getEntityStores().get(0).getEntityStoreKey()))).thenReturn(Optional.of(entityReferentialIntegrityMock));

        when(dataChannelStoreType.createSchemaStoreFromLogical(logicalSchema, logicalSchema.getSchemaKey())).thenReturn(schemaStore);
        when(dataChannelStoreType.createEntityStoreFromLogical(logicalEntity, schemaStore)).thenReturn(entityStore);

        List <TransformationAttributeDTO> result = service.getAvailableAttributes(PROJECT_KEY, logicalSchema.getSchemaKey(),logicalEntity.getEntityKey());
        assertEquals(2, result.size());
        for (TransformationAttributeDTO dto : result){
            if(dto.getAttributeKey().equals("mainContactKey")){
                assertEquals(dto.getAttributeName(),"Main Contact Key");
                assertEquals(dto.getDescription(),"mainContactKey description test");
                assertEquals(dto.getType(),"STRING");
                assertEquals(dto.getParentSchemaKey(),"aLDMCustomer");
                assertEquals(dto.getParentEntityKey(), "Contact");
                assertEquals(dto.getParentAttributeKey(),"contactKey");
                assertEquals(dto.getSourceMapping(),"test source mapping");
                assertEquals(dto.getSortOrder(),1000);
                assertTrue(dto.isIsRequired());
                assertFalse(dto.isIsLogicalTime());
                assertFalse(dto.isIsUpdateTime());
            } else {
                assertEquals(dto.getAttributeKey(),"bssModificationDate");
                assertEquals(dto.getAttributeName(),"BSS Modification Date");
                assertEquals(dto.getType(),"TIMESTAMP");
                assertNull(dto.getParentSchemaKey());
                assertNull(dto.getParentEntityKey());
                assertNull(dto.getParentAttributeKey());
                assertTrue(dto.isIsLogicalTime());
                assertFalse(dto.isIsUpdateTime());
                assertFalse(dto.isIsRequired());
            }

        }
    }



    private LogicalSchema createLogicalSchemaForTest(){
        final LogicalSchema logicalSchema = new LogicalSchema();
        logicalSchema.setProjectKey(PROJECT_KEY);
        logicalSchema.setSchemaKey("aLDMCustomer");
        logicalSchema.setProductKey("SHARED");
        logicalSchema.setName("Customer");
        return logicalSchema;
    }

    private LogicalEntity createLogicalEntityForTest(){
        final LogicalAttribute logicalAttribute1 = new LogicalAttribute();
        logicalAttribute1.setAttributeKey("mainContactKey");
        logicalAttribute1.setName("Main Contact Key");
        logicalAttribute1.setKeyPosition(1);
        Datatype datatype1 = new Datatype();
        datatype1.setDatatypeKey("STRING");
        logicalAttribute1.setDatatype(datatype1);
        PropertyData propertyData = new PropertyData();
        propertyData.setValue("test source mapping");
        Map<String,PropertyData> stringPropertyDataMap = new HashMap<>();
        stringPropertyDataMap.put("SourceMapping", propertyData);
        logicalAttribute1.setProperties(stringPropertyDataMap);
        logicalAttribute1.setSortOrder(1000);
        AttributeRelation attributeRelation = new AttributeRelation();
        attributeRelation.setParentSchemaKey("aLDMCustomer");
        attributeRelation.setParentEntityKey("Contact");
        attributeRelation.setParentAttributeKey("contactKey");
        logicalAttribute1.setAttributeRelation(attributeRelation);

        final LogicalAttribute logicalAttribute2 = new LogicalAttribute();
        logicalAttribute2.setAttributeKey("bssModificationDate");
        logicalAttribute2.setName("BSS Modification Date");
        logicalAttribute2.setKeyPosition(null);
        Datatype datatype2 = new Datatype();
        datatype2.setDatatypeKey("TIMESTAMP");
        logicalAttribute2.setDatatype(datatype2);
        logicalAttribute2.setAttributeRelation(null);

        final LogicalAttribute logicalAttribute3 = new LogicalAttribute();
        logicalAttribute3.setAttributeKey("forTest");
        logicalAttribute3.setName("for test not in attributeStores");
        logicalAttribute3.setKeyPosition(null);
        logicalAttribute3.setDatatype(datatype2);
        logicalAttribute3.setAttributeRelation(null);

        final List<LogicalAttribute> logicalAttributes = new ArrayList<>();
        logicalAttributes.add(logicalAttribute1);
        logicalAttributes.add(logicalAttribute2);
        logicalAttributes.add(logicalAttribute3);

        final LogicalEntity logicalEntity = new LogicalEntity();
        logicalEntity.setEntityKey("Customer");
        logicalEntity.setAttributes(logicalAttributes);
        logicalEntity.setEntityType("Master");
        logicalEntity.setName("Customer");
        logicalEntity.setSchemaKey("aLDMUsage");

        return logicalEntity;
    }

    private SchemaStore createSchemaStoreForTest(){
        final SchemaStore schemaStore = new SchemaStore();
        schemaStore.setSchemaStoreKey("aLDMCustomerDataChannel");
        schemaStore.setLogicalSchemaKey("aLDMCustomer");
        schemaStore.setProductKey("SHARED");
        schemaStore.setName("Customer");
        List<EntityStore> entityStoreList = new ArrayList<>();
        entityStoreList.add(createEntityStoreForTest());
        schemaStore.setEntityStores(entityStoreList);
        return schemaStore;
    }

    private EntityStore createEntityStoreForTest(){
        final AttributeStore attributeStore1 = new AttributeStore();
        attributeStore1.setLogicalAttributeKey("mainContactKey");
        attributeStore1.setName("Main Contact Key");
        attributeStore1.setDescription("mainContactKey description test");
        attributeStore1.setOrigin("origin test");
        attributeStore1.setType("STRING");
        attributeStore1.setIsLogicalTime(false);
        attributeStore1.setIsUpdateTime(false);
        attributeStore1.setIsRequired(true);

        final AttributeStore attributeStore2 = new AttributeStore();
        attributeStore2.setName("BSS Modification Date");
        attributeStore2.setLogicalAttributeKey("bssModificationDate");
        attributeStore2.setType("TIMESTAMP");
        attributeStore2.setDescription("bssModificationDate description test");
        attributeStore2.setOrigin("origin test");
        attributeStore2.setIsLogicalTime(true);
        attributeStore2.setIsUpdateTime(false);
        attributeStore2.setIsRequired(false);

        final List<AttributeStore> attributeStores = new ArrayList<>();
        attributeStores.add(attributeStore1);
        attributeStores.add(attributeStore2);

        final EntityStore entityStore = new EntityStore();
        entityStore.setLogicalEntityKey("Customer");
        entityStore.setName("Customer");
        entityStore.setEntityStoreKey("Customer");
        entityStore.setLogicalSchemaKey("aLDMCustomer");
        entityStore.setSchemaStoreKey("aLDMCustomerDataChannel");
        entityStore.setAttributeStores(attributeStores);

        return entityStore;
    }

    private static ApiException getTransformationApiException(AiaApiException.AiaApiHttpCodes status, AIAAPIMessageTemplate message) {
        return new ApiException(status, message, "display_name", "element_key");
    }
}
