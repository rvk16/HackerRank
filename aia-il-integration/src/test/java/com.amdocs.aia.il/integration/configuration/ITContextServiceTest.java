package com.amdocs.aia.il.integration.configuration;


import com.amdocs.aia.il.configuration.client.dto.ChangeStatusDTO;
import com.amdocs.aia.il.configuration.client.dto.CommonModelDTO;
import com.amdocs.aia.il.configuration.client.dto.ContextDTO;
import com.amdocs.aia.il.configuration.client.dto.ContextEntityDTO;
import com.amdocs.aia.il.integration.BaseIntegrationTest;
import com.amdocs.aia.il.utils.LogUtils;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.assertGetResponseGet;
import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.assertGetResponsePost;
import static org.testng.Assert.assertEquals;


public class ITContextServiceTest extends BaseIntegrationTest {

    public static final String CUSTOMER = "Customer";

    @BeforeClass
    public void setUpConf() {
        //Every service operation requires change request to be created
        createChangeRequest("IL Context Changes - Test");
        LogUtils.log("Start running test 'ITContextServiceTest'");
    }

    @Override
    protected File getConfigurationZip(){
        return new File("src/test/resources/data/TransformationConfig.zip");
    }

    @Test
    public void T00_whenAddPublisherContext_shouldCreatePublisherContext() {
        Response response = createPublisherContext(CUSTOMER);
        ContextDTO responsePublisherContextDTO = response.as(ContextDTO.class);
        assertEquals(getPublisherContextDTO(CUSTOMER),responsePublisherContextDTO);
    }

    @Test
    public void T01_whenGetPublisherContext_ShouldGetPublisherContext(){
        final Response response = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformation-contexts/" + CUSTOMER, HttpStatus.SC_OK);
        ContextDTO publisherContextDTO = response.as(ContextDTO.class);
        assertEquals(CUSTOMER,publisherContextDTO.getContextKey());
    }

    @Test
    public void T02_whenListPublisherContext_ShouldListPublisherContext(){
        createPublisherContext("Subscriber");
        createPublisherContext("Address");
        createPublisherContext("Account");
        createPublisherContext("Product");
        final Response response = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl+ "/transformation-contexts" , HttpStatus.SC_OK);
        ContextDTO[] publisherContextDTOs = response.as(ContextDTO[].class);
        assertEquals(12,publisherContextDTOs.length);
    }

    private Response createPublisherContext(String name) {
        ContextDTO publisherContextDTO = getPublisherContextDTO(name);
        return assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/transformation-contexts",
                HttpStatus.SC_CREATED, publisherContextDTO);
    }

    private ContextDTO getPublisherContextDTO(String contextKey) {
        ContextDTO publisherContextDTO = new ContextDTO();
        // Basics
        publisherContextDTO.setDescription("Integration test case 1: Creation of new "+contextKey+"Publisher Context");
        publisherContextDTO.setDisplayName(contextKey+"PublisherContext");
        publisherContextDTO.setOriginProcess("CUSTOM");
        publisherContextDTO.setProjectKey(PROJECT_KEY);
        publisherContextDTO.setStatus(ChangeStatusDTO.DRAFT);
        //Entity specific
        publisherContextDTO.setContextKey(contextKey);
        publisherContextDTO.setContextEntities( createContextEntity());
        // PKs
        publisherContextDTO.setStoreName(publisherContextDTO.getContextKey());
        publisherContextDTO.setModelType(CommonModelDTO.ModelTypeEnum.CONTEXT);

        return publisherContextDTO;
    }

    private List<ContextEntityDTO> createContextEntity() {
        List<ContextEntityDTO> contextEntityList=new ArrayList<>();
        ContextEntityDTO contextEntity1 = new ContextEntityDTO().schemaStoreKey("CRMAPP").entityStoreKey("TABLE_CUSTOMER").sourceAlias("CUST").relationType(ContextEntityDTO.RelationTypeEnum.LEAD).doPropagation(true).parentContextEntityKey(null).foreignKeys(null).noReferentAction(null).aliasedSourceEntityKey("CUST-TABLE_CUSTOMER");
         contextEntityList.add(contextEntity1);
         return contextEntityList;

    }

}
