//package com.amdocs.aia.il.integration.configuration;
//
//import com.amdocs.aia.il.configuration.client.dto.ChangeStatusDTO;
//import com.amdocs.aia.il.configuration.client.dto.CommonModelDTO;
//import com.amdocs.aia.il.configuration.client.dto.ContextDTO;
//import com.amdocs.aia.il.configuration.client.dto.TransformationDTO;
//import com.amdocs.aia.il.integration.BaseIntegrationTest;
//import io.restassured.response.Response;
//import org.apache.http.HttpStatus;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//import java.util.Collections;
//
//
//import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.assertGetResponseGet;
//import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.assertGetResponsePost;
//import static org.testng.Assert.assertEquals;
//
//public class ITTransformationServiceTest extends BaseIntegrationTest {
//
//    @BeforeClass
//    public void setUpConf() {
//        //Every service operation requires change request to be created
//        createChangeRequest("IL Transformation Changes - Test");
//    }
//
//    public void createPublisherContext(String entity) {
//        ContextDTO publisherContextDTO = getPublisherContextDTO(entity);
//        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/transformation-contexts",
//                HttpStatus.SC_CREATED, publisherContextDTO);
//    }
//
//    @Test
//    public void T00_whenAddPublisherTransformation_shouldCreatePublisherTransformation() {
//        String schema = "aLDMCustomer";
//        String entity = "Customer";
//        createPublisherContext(entity);     //Adding Dependency
//        final Response response = createPublisherTransformation(schema, entity);
//        TransformationDTO responsePublisherTransformationDTO = response.as(TransformationDTO.class);
//        assertEquals(getPublisherTransformationDTO(schema, entity),responsePublisherTransformationDTO);
//    }
//
//    @Test
//    public void T01_whenGetPublisherContext_ShouldGetPublisherContext(){
//        String schema = "aLDMCustomer";
//        String entity = "Customer";
//        String id = schema + "-"+entity;
//        final Response response = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformations/" + id, HttpStatus.SC_OK);
//        TransformationDTO publisherTransformationDTO = response.as(TransformationDTO.class);
//        assertEquals("CustomerPublisherContext",publisherTransformationDTO.getContextKey());
//        assertEquals(schema,publisherTransformationDTO.getTargetSchemaStoreKey());
//        assertEquals(entity,publisherTransformationDTO.getTargetEntityStoreKey());
//    }
//
//    @Test
//    public void T02_whenListPublisherContext_ShouldListPublisherContext(){
//        //Adding dependency
//        createPublisherContext("Subscriber");
//        createPublisherContext("Product");
//        createPublisherContext("Charge");
//        createPublisherContext("Address");
//        //create transformation
//        createPublisherTransformation("aLDMSubscriber","Subscriber");
//        createPublisherTransformation("aLDMProduct","Product");
//        createPublisherTransformation("aLDMAr","Charge");
//        createPublisherTransformation("aLDMCustomer","Address");
//
//        final Response response = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl+ "/transformations" , HttpStatus.SC_OK);
//        TransformationDTO[] publisherTransformationDTOs = response.as(TransformationDTO[].class);
//        assertEquals(5,publisherTransformationDTOs.length);
//    }
//
//    private Response createPublisherTransformation(String schema, String entity) {
//        TransformationDTO publisherTransformationDTO = getPublisherTransformationDTO(schema,entity);
//        return assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/transformations",
//                HttpStatus.SC_CREATED, publisherTransformationDTO);
//
//    }
//
//    private TransformationDTO getPublisherTransformationDTO(String schema, String entity) {
//        TransformationDTO publisherTransformationDTO = new TransformationDTO();
//        // Basics
//        publisherTransformationDTO.setDescription("Integration test case 1: Creation of new "+entity+" Publisher Transformation");
//        publisherTransformationDTO.setDisplayName(entity);
//        publisherTransformationDTO.setOriginProcess("CUSTOM");
//        publisherTransformationDTO.setProjectKey(PROJECT_KEY);
//        publisherTransformationDTO.setStatus(ChangeStatusDTO.DRAFT);
//        //Entity specific
//        publisherTransformationDTO.setContextKey(entity+"PublisherContext");
//        publisherTransformationDTO.setCustomScript("Select * from "+entity);
//        publisherTransformationDTO.setCustomScriptForDeletionKeys("Select * from "+entity);
//        publisherTransformationDTO.setImplementationType(TransformationDTO.ImplementationTypeEnum.SQL);
//        publisherTransformationDTO.setTargetEntityStoreKey(entity);
//        publisherTransformationDTO.setTargetSchemaStoreKey(schema);
//        publisherTransformationDTO.setSourceType(TransformationDTO.SourceTypeEnum.CONTEXT);
//        publisherTransformationDTO.setReferenceSourceEntities(Collections.emptyList());
//        // PKs
//        publisherTransformationDTO.setStoreName(publisherTransformationDTO.getTargetSchemaStoreKey() + "-"+publisherTransformationDTO.getTargetEntityStoreKey());
//        publisherTransformationDTO.setModelType(CommonModelDTO.ModelTypeEnum.TRANSFORMATION);
//
//        return publisherTransformationDTO;
//    }
//
//    private static ContextDTO getPublisherContextDTO(String contextKey) {
//        ContextDTO publisherContextDTO = new ContextDTO();
//        // Basics
//        publisherContextDTO.setDescription("Dependency Analyzer: Creation of new "+contextKey+"Publisher Context");
//        publisherContextDTO.setDisplayName(contextKey+"PublisherContext");
//        publisherContextDTO.setOriginProcess("CUSTOM");
//        publisherContextDTO.setProjectKey(PROJECT_KEY);
//        publisherContextDTO.setStatus(ChangeStatusDTO.DRAFT);
//        //Entity specific
//        publisherContextDTO.setContextKey(contextKey+"PublisherContext");
//        publisherContextDTO.setContextEntities(Collections.emptyList());
//        // PKs
//        publisherContextDTO.setStoreName(publisherContextDTO.getContextKey());
//        publisherContextDTO.setModelType(CommonModelDTO.ModelTypeEnum.CONTEXT);
//
//        return publisherContextDTO;
//    }
//}
