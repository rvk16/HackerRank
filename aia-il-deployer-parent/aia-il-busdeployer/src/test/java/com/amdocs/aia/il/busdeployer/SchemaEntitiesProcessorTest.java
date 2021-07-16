package com.amdocs.aia.il.busdeployer;

import com.amdocs.aia.il.busdeployer.configurations.RuntimeConfiguration;
import com.amdocs.aia.il.busdeployer.exception.ElementsProviderException;
import com.amdocs.aia.il.common.model.DataChannelEntityStore;
import com.amdocs.aia.il.common.model.DataChannelSchemaStore;
import com.amdocs.aia.il.common.model.configuration.transformation.Transformation;
import com.amdocs.aia.il.common.model.configuration.transformation.TransformationSourceType;
import com.amdocs.aia.il.common.model.stores.SchemaStoreCategory;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;
import com.amdocs.aia.repo.client.ElementsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {SchemaEntitiesProcessorImpl.class})
class SchemaEntitiesProcessorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaEntitiesProcessorTest.class);

    @Autowired
    private SchemaEntitiesProcessorImpl schemaEntitiesProcessorimpl;
    @MockBean
    private ElementsProvider elementsProvider;
    @MockBean
    private RuntimeConfiguration runtimeConfiguration;
//    @Mock
//    private SchemaEntitiesProcessor schemaEntitiesProcessor;


//    @Autowired
//    public void setSchemaEntitiesProcessor(SchemaEntitiesProcessor schemaEntitiesProcessor) {
//        this.schemaEntitiesProcessor = schemaEntitiesProcessor;
//    }

    @BeforeEach
    public void setup() {
        when(runtimeConfiguration.getRepoElementsLocalPath()).thenReturn("src/test/resources/configurations/add");
    }

    @Test
    void shouldFailWithCorrectExceptionWhenElementsProviderFails() {
        LOGGER.info("Testing that the correct exception is raised when the ElementsProvider fails");
        SchemaEntitiesProcessorImpl spySchemaProcessor = Mockito.spy(schemaEntitiesProcessorimpl);
        when(spySchemaProcessor.getElementsProvider(anyString())).thenReturn(elementsProvider);
        when(spySchemaProcessor.getSchemaStores(elementsProvider)).thenThrow(new RuntimeException("ElementsProvider has failed"));

//        when(elementsProvider.getElements(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any()))
//                .thenThrow(new RuntimeException("ElementsProvider has failed"));
        assertThrows(ElementsProviderException.class, spySchemaProcessor::getTopicList);
    }

    @Test
    void shouldSelectDataChannelsOnly() {
        LOGGER.info("Testing that stores are correctly filtered");
        DataChannelSchemaStore schemaStore1 = new DataChannelSchemaStore();
        schemaStore1.setSchemaStoreKey("ss1");
        schemaStore1.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        schemaStore1.setSchemaName("ss1_Channel");
        schemaStore1.setDataChannel("ss1_Channel");
        schemaStore1.setCategory(SchemaStoreCategory.PRIVATE);

        DataChannelSchemaStore schemaStore2 = new DataChannelSchemaStore();
        schemaStore2.setSchemaStoreKey("ss2");
        schemaStore2.setSchemaName("ss2_Channel");
        schemaStore2.setDataChannel("ss2_Channel");
        schemaStore2.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        schemaStore2.setCategory(SchemaStoreCategory.SHARED);

        DataChannelSchemaStore schemaStore3 = new DataChannelSchemaStore();
        schemaStore3.setSchemaStoreKey("ss3");
        schemaStore3.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        schemaStore3.setSchemaName("ss3_Channel");
        schemaStore3.setDataChannel("ss3_Channel");
        schemaStore3.setCategory(SchemaStoreCategory.PRIVATE);
        //The store without storeKey
        DataChannelSchemaStore schemaStore4 = new DataChannelSchemaStore();
        schemaStore4.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        schemaStore4.setSchemaName("ss4_Channel");
        schemaStore4.setDataChannel("ss4_Channel");
        schemaStore4.setCategory(SchemaStoreCategory.PRIVATE);

        DataChannelSchemaStore schemaStore5 = new DataChannelSchemaStore();
        schemaStore5.setSchemaStoreKey("ss5");
        schemaStore5.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        schemaStore5.setSchemaName("ss5_Channel");
        schemaStore5.setDataChannel("ss5_Channel");
        schemaStore5.setCategory(SchemaStoreCategory.PRIVATE);

        SchemaEntitiesProcessorImpl spySchemaProcessor = Mockito.spy(schemaEntitiesProcessorimpl);
        when(spySchemaProcessor.getElementsProvider(anyString())).thenReturn(elementsProvider);
        when(spySchemaProcessor.getSchemaStores(elementsProvider)).thenReturn((Arrays.asList(schemaStore1, schemaStore2, schemaStore3, schemaStore4, schemaStore5)));
        List<String> dataChannelStores = spySchemaProcessor.getTopicList();
        System.out.println("getTopic List ->" + dataChannelStores);
        List<String> expected = Arrays.asList("ss1_Channel", "ss3_Channel", "ss5_Channel");
        assertEquals(expected, dataChannelStores);
    }

    @Test
    void bulkTopics() {

        DataChannelSchemaStore schemaStore1 = new DataChannelSchemaStore();
        schemaStore1.setSchemaStoreKey("ss1");
        schemaStore1.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        schemaStore1.setSchemaName("ss1_Channel");
        schemaStore1.setCategory(SchemaStoreCategory.PRIVATE);
        schemaStore1.setDataChannel("ss1_Channel");
        schemaStore1.setReference(false);

        DataChannelSchemaStore schemaStore2 = new DataChannelSchemaStore();
        schemaStore2.setSchemaStoreKey("ss2");
        schemaStore2.setSchemaName("ss2_Channel_REF");
        schemaStore2.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        schemaStore2.setCategory(SchemaStoreCategory.PRIVATE);
        schemaStore2.setDataChannel("ss2_Channel_REF");
        schemaStore2.setReference(true);

        DataChannelEntityStore schemaStore4 = new DataChannelEntityStore();
        schemaStore4.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        schemaStore4.setEntityName("ss4_Channel");
        schemaStore4.setEntityStoreKey("ss4");
        schemaStore4.setSchemaStoreKey("ss1");

        DataChannelEntityStore schemaStore5 = new DataChannelEntityStore();
        schemaStore5.setEntityStoreKey("ss5");
        schemaStore5.setSchemaStoreKey("ss2");
        schemaStore5.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        schemaStore5.setEntityName("ss5_Channel");


        SchemaEntitiesProcessorImpl spySchemaProcessor = Mockito.spy(schemaEntitiesProcessorimpl);
        when(spySchemaProcessor.getElementsProvider(anyString())).thenReturn(elementsProvider);
        when(spySchemaProcessor.getSchemaStores(elementsProvider)).thenReturn((Arrays.asList(schemaStore1, schemaStore2)));
        when(spySchemaProcessor.getEntityStores(elementsProvider)).thenReturn((Arrays.asList(schemaStore4, schemaStore5)));
        List<String> dataChannelStores = spySchemaProcessor.getBulkTopicList();
        List<String> expected = Collections.singletonList("ss1_Channel_ss4");
        assertEquals(expected, dataChannelStores);
    }

    @Test
    void shouldSelectTransformationOnly() {
        Transformation addressContext = new Transformation();
        addressContext.setSourceType(TransformationSourceType.CONTEXT);
        addressContext.setContextKey("AddressContext");
        addressContext.setTargetSchemaName("aLDMCustomer");

        Transformation arDisputeActivityContext = new Transformation();
        arDisputeActivityContext.setSourceType(TransformationSourceType.CONTEXT);
        arDisputeActivityContext.setContextKey("ArDisputeActivityContext");
        arDisputeActivityContext.setTargetSchemaName("aLDMAr");

        SchemaEntitiesProcessorImpl spySchemaProcessor = Mockito.spy(schemaEntitiesProcessorimpl);
        when(spySchemaProcessor.getElementsProvider(anyString())).thenReturn(elementsProvider);
        when(spySchemaProcessor.getTransformation(elementsProvider))
                .thenReturn((Arrays.asList(addressContext, arDisputeActivityContext)));
        List<String> contextTransformerBulkTopics = spySchemaProcessor.getContextTransformerBulkTopicList();
        List<String> expected = Arrays.asList("aLDMCustomer_Address", "aLDMAr_ArDisputeActivity");
        assertEquals(expected, contextTransformerBulkTopics);
    }
}