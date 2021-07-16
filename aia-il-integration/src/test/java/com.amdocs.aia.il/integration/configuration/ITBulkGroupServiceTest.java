package com.amdocs.aia.il.integration.configuration;

import com.amdocs.aia.il.configuration.client.dto.*;
import com.amdocs.aia.il.utils.LogUtils;
import org.testng.annotations.BeforeClass;
import com.amdocs.aia.il.integration.BaseIntegrationTest;
import com.amdocs.aia.il.utils.DTOCreatorsUtils;
import com.amdocs.aia.il.utils.LogUtils;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.*;
import static org.testng.Assert.*;


public class ITBulkGroupServiceTest extends BaseIntegrationTest {
    @BeforeClass
    public void setUpConf() {
        //Every service operation requires change request to be created
        createChangeRequest("IT Bulk Group Service CR - Test");
        LogUtils.log("Start running test 'ITBulkGroupServiceTest'");
    }

    @Test
    public void T001_whenDeleteExternalEntityThatIsPartOfBulkDependency_shouldReturnError() {
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "entityKey" + methodName,"Query1");
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities", HttpStatus.SC_CREATED, externalEntityDTO);
        //check  schema and entity been created
        assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName + "/entities/entityKey" + methodName, HttpStatus.SC_OK);
        assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName, HttpStatus.SC_OK);
        //Create bulkGroup
        BulkGroupDTO bulkGroupDTO = DTOCreatorsUtils.createBulkGroup("bulkKey" + methodName, "schemaKey" + methodName, "entityKey" + methodName, "Query1");
        assertGetResponsePost(configurationServiceUrl + "/projects/" + PROJECT_KEY+"/schemas/schemaKey" + methodName+"/bulk-groups", HttpStatus.SC_CREATED, bulkGroupDTO);
        //check bulkGroup been created
        Response response = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/schemas/schemaKey" + methodName + "/bulk-groups/bulkKey" + methodName, HttpStatus.SC_OK);
        //Delete the External Entity
        assertGetResponseDelete(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities/entityKey" + methodName,
                HttpStatus.SC_CONFLICT);
    }

    @Test
    public void T002_whenUpdateExternalEntityThatIsPartOfBulkDependency_shouldReturnError() {
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "entityKey" + methodName,"Query1");
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities", HttpStatus.SC_CREATED, externalEntityDTO);
        //check  schema and entity been created
        Response response = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName + "/entities/entityKey" + methodName, HttpStatus.SC_OK);
        assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName, HttpStatus.SC_OK);
        //Create bulkGroup
        BulkGroupDTO bulkGroupDTO = DTOCreatorsUtils.createBulkGroup("bulkKey" + methodName, "schemaKey" + methodName, "entityKey" + methodName, "Query1");
        assertGetResponsePost(configurationServiceUrl + "/projects/" + PROJECT_KEY+"/schemas/schemaKey" + methodName+"/bulk-groups", HttpStatus.SC_CREATED, bulkGroupDTO);
        //check bulkGroup been created
        assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/schemas/schemaKey" + methodName + "/bulk-groups/bulkKey" + methodName, HttpStatus.SC_OK);
        //remove from the entity,the Query that the bulkGroup is Using
        ExternalEntityDTO externalEntityDTOUpdate = response.as(ExternalEntityDTO.class);
        ExternalEntityDTO updatedDTO = DTOCreatorsUtils.UpdateQueryEntityDTO(externalEntityDTOUpdate);
        assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities/entityKey" + methodName, HttpStatus.SC_CONFLICT, updatedDTO);

    }

}
