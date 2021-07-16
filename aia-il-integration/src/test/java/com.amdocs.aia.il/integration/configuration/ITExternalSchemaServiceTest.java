package com.amdocs.aia.il.integration.configuration;


import com.amdocs.aia.common.model.logical.LogicalEntity;
import com.amdocs.aia.common.model.logical.LogicalSchema;
import com.amdocs.aia.common.model.store.AttributeStore;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.il.configuration.client.dto.*;
import com.amdocs.aia.il.integration.BaseIntegrationTest;
import com.amdocs.aia.il.utils.DTOCreatorsUtils;
import com.amdocs.aia.il.utils.LogUtils;
import com.amdocs.aia.notifications.application.client.dto.NotificationDTO;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.amdocs.aia.common.core.test.utils.OpenShiftUtils.sleep;
import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.*;
import static org.testng.Assert.*;

public class ITExternalSchemaServiceTest extends BaseIntegrationTest {


    @BeforeClass
    public void setUpConf() {
        //Every service operation requires change request to be created
        createChangeRequest("IL External schema Changes - Test");
        LogUtils.log("Start running test 'ITExternalSchemaServiceTest'");
    }

    @Test
    public void T001_whenAddExternalSchema_shouldCreateExternalSchema() {
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("Schemakey" + methodName);
        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas",
                HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalSchemaDTO responseExternalSchemaDTO = response.as(ExternalSchemaDTO.class);
        assertEquals(externalSchemaDTO.getSchemaKey(), responseExternalSchemaDTO.getSchemaKey());
        assertEquals(AvailabilityDTO.EXTERNAL, responseExternalSchemaDTO.getAvailability());
    }

    @Test
    public void T002_whenUpdateExistExternalSchema_shouldUpdateExternalSchema() {
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("Schemakey" + methodName);
        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas",
                HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalSchemaDTO responseExternalSchemaDTO = response.as(ExternalSchemaDTO.class);
        assertEquals(externalSchemaDTO.getSchemaKey(), responseExternalSchemaDTO.getSchemaKey());
        responseExternalSchemaDTO.setDescription("updated description");
        Response responseUpdate = assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/" + "Schemakey" + methodName, HttpStatus.SC_CREATED, responseExternalSchemaDTO);
        ExternalSchemaDTO responseUpdateExternalSchemaDTO = responseUpdate.as(ExternalSchemaDTO.class);
        assertEquals(responseUpdateExternalSchemaDTO.getDescription(), responseExternalSchemaDTO.getDescription());
        //check that the schema been delted
        Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas", HttpStatus.SC_OK);

        assertEquals(responseExternalSchemaDTO.getAvailability(), AvailabilityDTO.EXTERNAL);
        assertEquals(responseUpdateExternalSchemaDTO.getAvailability(), AvailabilityDTO.EXTERNAL);
    }

    @Test
    public void T003_whenUpdateNonExistExternalSchema_shouldReturnError() {
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("Schemakey" + methodName);
        assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/" + "Schemakey" + methodName, HttpStatus.SC_NOT_FOUND, externalSchemaDTO);
    }

    @Test
    public void T004_whenUpdateExternalSchemaDifferentFromSchemaKey_shouldReturnError() {
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("Schemakey" + methodName);
        assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/" + "Schemakey2" + methodName, HttpStatus.SC_NOT_FOUND, externalSchemaDTO);
    }

    @Test
    public void T005_whenDeleteExternalSchemaWithConnectedExtenralEntityAndBulkGroup_returnsErrorValidation() {
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "entityKey" + methodName, "Query1");
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities", HttpStatus.SC_CREATED, externalEntityDTO);
        BulkGroupDTO bulkGroupDTO = DTOCreatorsUtils.createBulkGroup("bulkKey" + methodName, "schemaKey" + methodName, "entityKey" + methodName, "Query1");
        assertGetResponsePost(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/schemas/schemaKey" + methodName + "/bulk-groups", HttpStatus.SC_CREATED, bulkGroupDTO);
        //check if schema,entity and bulk been created
        assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName + "/entities/entityKey" + methodName, HttpStatus.SC_OK);
        assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName, HttpStatus.SC_OK);
        assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/schemas/schemaKey" + methodName + "/bulk-groups/bulkKey" + methodName, HttpStatus.SC_OK);
        //Delete
        assertGetResponseDelete(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName, HttpStatus.SC_NO_CONTENT);
        //Check that the schema and entity not exist anymore
        assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName + "/entities/entityKey" + methodName, HttpStatus.SC_NOT_FOUND);
        assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void T006_2_whenDeleteExternalSchema_shouldDeleteSchama() {
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);
        //check if schema been created
        Response responseGetSchema = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName, HttpStatus.SC_OK);
        //Delete
        assertGetResponseDelete(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName, HttpStatus.SC_NO_CONTENT);
        //check that the schema been deleted
        assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void T007_getExternalSchemaTypes() {
        String getExternalSchemaTypesUrl = configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schema-types";
        Response response = assertGetResponseGet(getExternalSchemaTypesUrl, HttpStatus.SC_OK);
        List<ExternalSchemaTypeInfoDTO> externalSchemaTypesList = Arrays.asList(response.getBody().as(ExternalSchemaTypeInfoDTO[].class));
        assertTrue(externalSchemaTypesList.stream().anyMatch(externalSchemaTypeDTO -> externalSchemaTypeDTO.getType().equals("CSV_FILES")));
        assertTrue(externalSchemaTypesList.stream().anyMatch(externalSchemaTypeDTO -> externalSchemaTypeDTO.getType().equals("JSON_OVER_KAFKA")));
        assertTrue(externalSchemaTypesList.stream().anyMatch(externalSchemaTypeDTO -> externalSchemaTypeDTO.getType().equals("CATALOG1")));
        assertTrue(externalSchemaTypesList.stream().anyMatch(externalSchemaTypeDTO -> externalSchemaTypeDTO.getType().equals("DIGITAL1")));
        assertTrue(externalSchemaTypesList.stream().anyMatch(externalSchemaTypeDTO -> externalSchemaTypeDTO.getType().equals("ORACLE")));
        assertFalse(externalSchemaTypesList.stream().anyMatch(externalSchemaTypeDTO -> externalSchemaTypeDTO.getType().equals("POSTGRESQL")));
        assertFalse(externalSchemaTypesList.stream().anyMatch(externalSchemaTypeDTO -> externalSchemaTypeDTO.getType().equals("COUCHBASE")));
    }

    @Test
    public void T008_whenAddExternalEntityWithStoreInfoAndCollectionRules_shouldCreateExternalEntity() {
        //Need to add a schema,and then entity that connect to the schema
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        Response responsePostSchema = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas",
                HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalSchemaDTO externalSchemaDTOresponse = responsePostSchema.as(ExternalSchemaDTO.class);
        ExternalCsvSchemaCollectionRulesDTO externalCsvSchemaCollectionRulesDTO = (ExternalCsvSchemaCollectionRulesDTO) externalSchemaDTOresponse.getCollectionRules();
        assertEquals(externalCsvSchemaCollectionRulesDTO.getDefaultInvalidFilenameAction(), InvalidFilenameActionTypeDTO.KEEP);

        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "EntityKey" + methodName, "Query1");

        ExternalCsvEntityCollectionRulesDTO externalCsvEntityCollectionRulesDTO = new ExternalCsvEntityCollectionRulesDTO();
        externalCsvEntityCollectionRulesDTO.setStoreType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.CSV);
        List<ExternalEntityFilterDTO> externalEntityFilterList = new ArrayList<>();
        externalCsvEntityCollectionRulesDTO.setFilters(externalEntityFilterList);
        externalCsvEntityCollectionRulesDTO.setInvalidFilenameAction(InvalidFilenameActionTypeDTO.MOVE);
        externalEntityDTO.setCollectionRules(externalCsvEntityCollectionRulesDTO);

        ExternalCsvEntityStoreInfoDTO externalCsvEntityStoreInfoDTO = new ExternalCsvEntityStoreInfoDTO();
        externalCsvEntityStoreInfoDTO.setStoreType(ExternalSqlEntityStoreInfoDTO.StoreTypeEnum.CSV);
        externalCsvEntityStoreInfoDTO.setFileHeader(false);
        externalCsvEntityStoreInfoDTO.setColumnDelimiter("|");
        externalCsvEntityStoreInfoDTO.setDateFormat("yyyy-MM-dd");
        externalCsvEntityStoreInfoDTO.setFileHeader(false);
        externalEntityDTO.setStoreInfo(externalCsvEntityStoreInfoDTO);

        Response responsePost = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities",
                HttpStatus.SC_CREATED, externalEntityDTO);
        ExternalEntityDTO externalEntityDTOresponse = responsePost.as(ExternalEntityDTO.class);
        assertEquals(externalEntityDTO.getSchemaKey(), externalEntityDTOresponse.getSchemaKey());
        assertEquals(externalEntityDTO.getEntityKey(), externalEntityDTOresponse.getEntityKey());
        //The Values should be taken from the Entity and not from the Schema
        ExternalCsvEntityCollectionRulesDTO collectionRules = (ExternalCsvEntityCollectionRulesDTO) externalEntityDTOresponse.getCollectionRules();
        assertEquals(collectionRules.getInvalidFilenameAction(), InvalidFilenameActionTypeDTO.MOVE);
        ExternalCsvEntityStoreInfoDTO storeInfo = (ExternalCsvEntityStoreInfoDTO) externalEntityDTOresponse.getStoreInfo();
        assertEquals(storeInfo.getColumnDelimiter(), "|");
        assertEquals(storeInfo.getDateFormat(), "yyyy-MM-dd");
    }

    @Test
    public void T009_2_whenAddExternalEntity_shouldCreateExternalEntity() {
        //Need to add a schema,and then entity that connect to the schema
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas",
                HttpStatus.SC_CREATED, externalSchemaDTO);

        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "EntityKey" + methodName, "Query1");
        Response responsePost = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities",
                HttpStatus.SC_CREATED, externalEntityDTO);
        ExternalEntityDTO externalEntityDTOresponse = responsePost.as(ExternalEntityDTO.class);
        assertEquals(externalEntityDTO.getSchemaKey(), externalEntityDTOresponse.getSchemaKey());
        assertEquals(externalEntityDTO.getEntityKey(), externalEntityDTOresponse.getEntityKey());
        //The defaults values will be taken from the Schema,when it's null at the Entity
        ExternalCsvEntityCollectionRulesDTO collectionRules = (ExternalCsvEntityCollectionRulesDTO) externalEntityDTOresponse.getCollectionRules();
        assertEquals(collectionRules.getInvalidFilenameAction(), InvalidFilenameActionTypeDTO.KEEP);
        ExternalCsvEntityStoreInfoDTO storeInfo = (ExternalCsvEntityStoreInfoDTO) externalEntityDTOresponse.getStoreInfo();
        assertEquals(storeInfo.getColumnDelimiter(), ",");
        assertEquals(storeInfo.getDateFormat(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    @Test
    public void T010_whenAddExternalEntityWithNonExistSchema_shouldReturnError() {
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "EntityKey" + methodName, "Query1");
        Response responsePost = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities",
                HttpStatus.SC_NOT_FOUND, externalEntityDTO);
    }

    @Test
    public void T011_whenUpdateExternalEntityDifferentFromEntityKey_shouldReturnError() {
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "EntityKey" + methodName, "Query1");
        assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/" + "schemaKey" + methodName + "/entities/entityKey2" + methodName, HttpStatus.SC_BAD_REQUEST, externalEntityDTO);

    }

    @Test
    public void T012_whenUpdateNonExistExternalEntity_shouldReturnError() {

        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "entityKey" + methodName, "Query1");
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas",
                HttpStatus.SC_CREATED, externalSchemaDTO);
        assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/" + "schemaKey" + methodName + "/entities/entityKey" + methodName, HttpStatus.SC_NOT_FOUND, externalEntityDTO);
    }

    @Test
    public void T013_whenUpdateExternalEntity_shouldUpdate() {
        //Need to add a schema,and then entity that connect to the schema
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas",
                HttpStatus.SC_CREATED, externalSchemaDTO);

        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "entityKey" + methodName, "Query1");
        Response responsePost = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities",
                HttpStatus.SC_CREATED, externalEntityDTO);
        ExternalEntityDTO externalEntityDTOResponse = responsePost.as(ExternalEntityDTO.class);
        //Update external entity
        externalEntityDTOResponse.setIsTransient(true);
        Response responseUpdate = assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities/entityKey" + methodName, HttpStatus.SC_CREATED, externalEntityDTOResponse);
        ExternalEntityDTO externalEntityDTOUpdate = responseUpdate.as(ExternalEntityDTO.class);
        assertEquals(externalEntityDTOResponse.isIsTransient(), externalEntityDTOUpdate.isIsTransient());
    }

    @Test
    public void T014_whenDeleteExternalEntity_shouldDelete() {
        String methodName = DTOCreatorsUtils.getMethodName();
        //create schema
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "entityKey" + methodName, "Query1");
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities", HttpStatus.SC_CREATED, externalEntityDTO);
        //check if get, returns the new entity been created
        Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName + "/entities/entityKey" + methodName, HttpStatus.SC_OK);
        //Delete
        assertGetResponseDelete(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities/entityKey" + methodName,
                HttpStatus.SC_NO_CONTENT);
        //check that  entity been deleted
        assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName + "/entities/entityKey" + methodName, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void T015_whenAddExternalEntityWithTheSameAttKeys_returnsErrorValidation() {
        String methodName = DTOCreatorsUtils.getMethodName();
        //create schema
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "entityKey" + methodName, "Query1");
        ExternalAttributeDTO externalAttributeDTO = new ExternalAttributeDTO();
        externalAttributeDTO.setAttributeKey("att1");
        externalAttributeDTO.setAttributeName("att1");
        externalAttributeDTO.setDatatype("STRING");
        externalAttributeDTO.setSerializationId(-1);
        externalAttributeDTO.setIsLogicalTime(false);
        externalAttributeDTO.setIsRequired(false);
        externalAttributeDTO.setIsUpdateTime(false);
        externalEntityDTO.addAttributesItem(externalAttributeDTO);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities", HttpStatus.SC_BAD_REQUEST, externalEntityDTO);
    }

    @Test
    public void T016_whenAddExternalEntityWithFiltersWithTheSameKey_returnsErrorValidation() {
        String methodName = DTOCreatorsUtils.getMethodName();
        //create schema
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "entityKey" + methodName, "Query1");
        ExternalEntityCollectionRulesDTO externalEntityCollectionRulesDTO = new ExternalEntityCollectionRulesDTO();
        externalEntityCollectionRulesDTO.setStoreType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.fromValue("SQL"));
        List<ExternalEntityFilterDTO> externalEntityFilterList = new ArrayList<>();
        ExternalEntityFilterDTO externalEntityFilterDTO = new ExternalEntityFilterDTO();
        externalEntityFilterDTO.setFilterKey("Query1");
        externalEntityFilterDTO.setFilterLogic("select ENTITY_ID, ENCODING, PAYLOAD, BUCKET from OSS_CONTEXT_STORE where BUCKET='[BUCKET]'");
        externalEntityFilterList.add(externalEntityFilterDTO);
        ExternalEntityFilterDTO externalEntityFilterDTO2 = new ExternalEntityFilterDTO();
        externalEntityFilterDTO2.setFilterKey("Query1");
        externalEntityFilterDTO2.setFilterLogic("select ENTITY_ID, ENCODING, PAYLOAD, BUCKET from OSS_CONTEXT_STORE where BUCKET='[BUCKET]'");
        externalEntityFilterList.add(externalEntityFilterDTO2);
        externalEntityCollectionRulesDTO.setFilters(externalEntityFilterList);
        externalEntityDTO.setCollectionRules(externalEntityCollectionRulesDTO);

        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities", HttpStatus.SC_BAD_REQUEST, externalEntityDTO);
    }

    @Test
    public void T017_whenAddExternalEntityWithTheSameAttAndFilterKey_returnsErrorValidation() {
        String methodName = DTOCreatorsUtils.getMethodName();
        //create schema
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "entityKey" + methodName, "att1");
        ExternalAttributeDTO externalAttributeDTO = new ExternalAttributeDTO();

        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities", HttpStatus.SC_BAD_REQUEST, externalEntityDTO);
    }

    @Test
    public void T018_CheckSerializationIdOnEntityAndAttributes_SouldReturnOK() {
        String methodName = DTOCreatorsUtils.getMethodName();
        //create schema
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);

        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "EntityKey" + methodName, "Query1");
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities",
                HttpStatus.SC_CREATED, externalEntityDTO);
        //Add another entity that related to the schema
        externalEntityDTO.setEntityKey("EntityKey2" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities",
                HttpStatus.SC_CREATED, externalEntityDTO);

        Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName + "/entities/EntityKey" + methodName, HttpStatus.SC_OK);
        ExternalEntityDTO externalEntityDTOResponse = responseGet.as(ExternalEntityDTO.class);
        //check SerializationId of the first entity
        assertEquals(Integer.valueOf(4000), externalEntityDTOResponse.getSerializationId());
        //check SerializationId Of the Attributes of first entity
        assertEquals(externalEntityDTOResponse.getAttributes().get(0).getSerializationId(), Integer.valueOf(1));
        assertEquals(externalEntityDTOResponse.getAttributes().get(1).getSerializationId(), Integer.valueOf(2));

        //check SerializationId of the second entity
        Response responseGet2 = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/schemaKey" + methodName + "/entities/EntityKey2" + methodName, HttpStatus.SC_OK);
        ExternalEntityDTO externalEntity2DTOResponse = responseGet2.as(ExternalEntityDTO.class);
        assertEquals(externalEntity2DTOResponse.getSerializationId(), Integer.valueOf(4001));
    }

    @Test
    public void T019_CheckSerializationIdOnEntityAndAttributesAtUpdate_SouldReturnOK() {
        String methodName = DTOCreatorsUtils.getMethodName();
        //create schema
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);

        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "EntityKey" + methodName, "Query1");
        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities",
                HttpStatus.SC_CREATED, externalEntityDTO);
        ExternalEntityDTO externalEntityDTOPost = response.as(ExternalEntityDTO.class);        //Add Attribute
        ExternalAttributeDTO externalAttributeDTO = new ExternalAttributeDTO();
        externalAttributeDTO.setAttributeKey("att3");
        externalAttributeDTO.setAttributeName("att3");
        externalAttributeDTO.setDatatype("STRING");
        externalAttributeDTO.setSerializationId(-1);
        externalAttributeDTO.setIsLogicalTime(false);
        externalAttributeDTO.setIsRequired(false);
        externalAttributeDTO.setIsUpdateTime(false);
        externalEntityDTOPost.addAttributesItem(externalAttributeDTO);

        Response responseUpdate = assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities/EntityKey" + methodName, HttpStatus.SC_CREATED, externalEntityDTOPost);
        ExternalEntityDTO responseUpdateExternalEntity = responseUpdate.as(ExternalEntityDTO.class);

        //check SerializationId of the  entity
        assertEquals(Integer.valueOf(4000), responseUpdateExternalEntity.getSerializationId());

        //check SerializationId Of the Attributes of first entity
        assertEquals(responseUpdateExternalEntity.getAttributes().get(0).getSerializationId(), Integer.valueOf(1));
        assertEquals(responseUpdateExternalEntity.getAttributes().get(1).getSerializationId(), Integer.valueOf(2));
        assertEquals(responseUpdateExternalEntity.getAttributes().get(2).getSerializationId(), Integer.valueOf(3));
    }

    @Test
    public void T020_updateSerializationIdOnEntity_SouldReturnERROR() {
        String methodName = DTOCreatorsUtils.getMethodName();
        //create schema
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);

        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "EntityKey" + methodName, "Query1");
        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities",
                HttpStatus.SC_CREATED, externalEntityDTO);
        ExternalEntityDTO responseExternalEntity = response.as(ExternalEntityDTO.class);
        //check SerializationId of the  entity
        assertEquals(Integer.valueOf(4000), responseExternalEntity.getSerializationId());
        //update SerializationId On Entity
        responseExternalEntity.setSerializationId(40001);
        assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities/EntityKey" + methodName, HttpStatus.SC_BAD_REQUEST, responseExternalEntity);
    }

    @Test
    public void T021_updateSerializationIdOnAttribute_SouldReturnERROR() {
        String methodName = DTOCreatorsUtils.getMethodName();
        //create schema
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("schemaKey" + methodName);
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);

        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("schemaKey" + methodName, "EntityKey" + methodName, "Query1");
        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities",
                HttpStatus.SC_CREATED, externalEntityDTO);
        ExternalEntityDTO responseExternalEntity = response.as(ExternalEntityDTO.class);

        //Add Attribute
        ExternalAttributeDTO externalAttributeDTO = new ExternalAttributeDTO();
        externalAttributeDTO.setAttributeKey("att1");
        externalAttributeDTO.setAttributeName("att1");
        externalAttributeDTO.setDatatype("STRING");
        externalAttributeDTO.setSerializationId(4);
        externalAttributeDTO.setIsLogicalTime(false);
        externalAttributeDTO.setIsRequired(false);
        externalAttributeDTO.setIsUpdateTime(false);
        responseExternalEntity.setAttributes(null);
        responseExternalEntity.addAttributesItem(externalAttributeDTO);

        assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemaKey" + methodName + "/entities/EntityKey" + methodName, HttpStatus.SC_BAD_REQUEST, responseExternalEntity);
    }

    @Test
    public void T022_whenAddExternalSchema_SHARED_withModel() {
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTOQueue();
        externalSchemaDTO.availability(AvailabilityDTO.SHARED);
        externalSchemaDTO.setSubjectAreaName("aLDM CustomerService");
        externalSchemaDTO.setSubjectAreaKey("aLDMCustomerService");

        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas", HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalSchemaDTO responseExternalSchemaDTO = response.as(ExternalSchemaDTO.class);
        assertEquals(AvailabilityDTO.SHARED, responseExternalSchemaDTO.getAvailability());
        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTOQueue();
        Response entityResponse = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/" + externalSchemaDTO.getSchemaKey() + "/entities", HttpStatus.SC_CREATED, externalEntityDTO);
        ExternalEntityDTO responseExternalEntity = entityResponse.as(ExternalEntityDTO.class);

        // add attribute to external entity
        ExternalAttributeDTO externalAttributeDTO = new ExternalAttributeDTO();
        externalAttributeDTO.setAttributeKey("newAtt");
        externalAttributeDTO.setAttributeName("newAtt");
        externalAttributeDTO.setDatatype("STRING");
        externalAttributeDTO.setIsLogicalTime(false);
        externalAttributeDTO.setIsRequired(false);
        externalAttributeDTO.setIsUpdateTime(false);
        externalAttributeDTO.setSerializationId(-1);
        responseExternalEntity.addAttributesItem(externalAttributeDTO);

        assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/" + externalSchemaDTO.getSchemaKey() + "/entities/" + responseExternalEntity.getEntityKey(), HttpStatus.SC_CREATED, responseExternalEntity);

        //check if entity store for queue had been updated
        final Response responseES = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemastores/aLDMCustomerServiceDataChannel/entitystores/SHARED_aia_ENS_DATA_CHANNEL_aLDMCustomerServiceDataChannel_Queue", HttpStatus.SC_OK);
        EntityStore entityStore = responseES.as(EntityStore.class);

        Optional<AttributeStore> attributeStore = entityStore.getAttributeStores().stream().filter(attr -> attr.getLogicalAttributeKey().equals("newAtt")).findFirst();
        AttributeStore attributeStore1 =  attributeStore.get();
        assertEquals(attributeStore1.getLogicalAttributeKey(), "newAtt");
        assertEquals(attributeStore1.getName(), "newAtt");
        assertEquals(attributeStore1.getType(), "STRING");
    }

    @Test
    public void T023_whenAddExternalSchemasBulk_shouldCreateExternalSchemas() {
        ExternalSchemaDTO externalSchemaDTO1 = DTOCreatorsUtils.createExternalSchemaDTO("Schemakey" +  "1");
        ExternalSchemaDTO externalSchemaDTO2 = DTOCreatorsUtils.createExternalSchemaDTO("Schemakey" +  "2");
        List<ExternalSchemaDTO> list = new ArrayList<>();
        list.add(externalSchemaDTO1);
        list.add(externalSchemaDTO2);

        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/schemas-bulk",
                HttpStatus.SC_CREATED, list);

        SaveElementsResponseDTO responseSaveElementsResponseDTO = response.as(SaveElementsResponseDTO.class);
        assertEquals( responseSaveElementsResponseDTO.getSavedElementsCount(),Long.valueOf(2));
    }

    @Test
    public void T024_whenAddExternalSchemasBulk_shouldCreateExternalEntities() {
        String methodName = DTOCreatorsUtils.getMethodName();
        ExternalEntityDTO externalEntityDTO1 = DTOCreatorsUtils.createExternalEntityDTO("Schemakey1" , "entityKey" + methodName + "1", "Query1");
        ExternalEntityDTO externalEntityDTO2 = DTOCreatorsUtils.createExternalEntityDTO("Schemakey2" , "entityKey" + methodName + "1", "Query1");

        List<ExternalEntityDTO> list = new ArrayList<>();
        list.add(externalEntityDTO1);
        list.add(externalEntityDTO2);
        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/entities-bulk",
                HttpStatus.SC_CREATED, list);

        SaveElementsResponseDTO responseSaveElementsResponseDTO = response.as(SaveElementsResponseDTO.class);
        assertEquals( responseSaveElementsResponseDTO.getSavedElementsCount(),Long.valueOf(2));
    }

    @Test
    public void T025_whenAddExternalSchema_SHARED_noModel_createLogicalSchemaAndStoreSchema() {
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("Schemakey25");

        externalSchemaDTO.availability(AvailabilityDTO.SHARED);
        externalSchemaDTO.setSubjectAreaName("aLDM Schemakey25");
        externalSchemaDTO.setSubjectAreaKey("aLDMSchemakey25");

        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas",
                HttpStatus.SC_CREATED, externalSchemaDTO);

        //check if external schema created
        ExternalSchemaDTO responseExternalSchemaDTO = response.as(ExternalSchemaDTO.class);
        assertEquals(externalSchemaDTO.getSchemaKey(), responseExternalSchemaDTO.getSchemaKey());
        assertEquals(AvailabilityDTO.SHARED, responseExternalSchemaDTO.getAvailability());

        //check if logical schema for Schemakey25 source created
        Response responseLogicalSchema = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemas/SHARED_aia_LS_aLDMSchemakey25", HttpStatus.SC_OK);
        LogicalSchema logicalSchema = responseLogicalSchema.as(LogicalSchema.class);
        assertEquals(logicalSchema.getSchemaKey(),"aLDMSchemakey25");
        assertEquals(logicalSchema.getId(), "SHARED_aia_LS_aLDMSchemakey25");
        assertEquals(logicalSchema.getName(),"aLDM Schemakey25");

        //check if schema store for Schemakey25 source created
        Response responseSchemaStore = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemastores/SHARED_aia_SCS_DATA_CHANNEL_aLDMSchemakey25DataChannel", HttpStatus.SC_OK);
        SchemaStore schemaStore = responseSchemaStore.as(SchemaStore.class);
        assertEquals(schemaStore.getSchemaStoreKey(),"aLDMSchemakey25DataChannel");
        assertEquals(schemaStore.getId(),"SHARED_aia_SCS_DATA_CHANNEL_aLDMSchemakey25DataChannel");
        assertEquals(schemaStore.getName(),"aLDM Schemakey25");
        assertEquals(schemaStore.getLogicalSchemaKey(),"aLDMSchemakey25");
    }

    @Test
    public void T026_whenAddExternalSchemaAndEntity_SHARED_withModel_withNoExistingEntity_createLogicalEntityAndEntityStore() {
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTOQueue();

        externalSchemaDTO.availability(AvailabilityDTO.SHARED);
        externalSchemaDTO.setSubjectAreaName("aLDM CustomerService");
        externalSchemaDTO.setSubjectAreaKey("aLDMCustomerService");
        Response response = assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/aLDMCustomerService", HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalSchemaDTO responseExternalSchemaDTO = response.as(ExternalSchemaDTO.class);
        assertEquals(externalSchemaDTO.getSchemaKey(), responseExternalSchemaDTO.getSchemaKey());
        assertEquals(AvailabilityDTO.SHARED, responseExternalSchemaDTO.getAvailability());

        ExternalEntityDTO externalEntityDTO = DTOCreatorsUtils.createExternalEntityDTO("aLDMCustomerService", "EntityKeyNew", "Query1");
        Response entityResponse = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/" + externalSchemaDTO.getSchemaKey() + "/entities", HttpStatus.SC_CREATED, externalEntityDTO);

        ExternalEntityDTO externalEntityDTOresponse = entityResponse.as(ExternalEntityDTO.class);
        assertEquals(externalEntityDTO.getSchemaKey(), externalEntityDTOresponse.getSchemaKey());
        assertEquals(externalEntityDTO.getEntityKey(), externalEntityDTOresponse.getEntityKey());

        //check if logical entity for EntityKeyNew created
        final Response responseLE = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemas/aLDMCustomerService/entities/getbykey/EntityKeyNew", HttpStatus.SC_OK);
        LogicalEntity logicalEntity = responseLE.as(LogicalEntity.class);

        assertEquals(logicalEntity.getEntityKey(), "EntityKeyNew");
        assertEquals(logicalEntity.getSchemaKey(), "aLDMCustomerService");

        //check if entity store for EntityKeyNew created
        final Response responseES = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemastores/aLDMCustomerServiceDataChannel/entitystores/SHARED_aia_ENS_DATA_CHANNEL_aLDMCustomerServiceDataChannel_EntityKeyNew", HttpStatus.SC_OK);
        EntityStore entityStore = responseES.as(EntityStore.class);

        assertEquals(entityStore.getEntityStoreKey(), "EntityKeyNew");
        assertEquals(entityStore.getLogicalEntityKey(), "EntityKeyNew");
        assertEquals(entityStore.getLogicalSchemaKey(), "aLDMCustomerService");
    }

    @Test
    public void T027_whenAddExternalSchemaWithNoSubjectArea_SHARED_noModel_Return400() {
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTO("Schemakey27");
        externalSchemaDTO.availability(AvailabilityDTO.SHARED);

        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas",
                HttpStatus.SC_BAD_REQUEST, externalSchemaDTO);
    }

    @Test
    public void T028_testDiscoverExternalCsv_SHARED_noModel_createSharedEntities() {
        final File file1 = new File("src/test/resources/data/discoverExternalCsv/orderHeader.csv");
        final File file2 = new File("src/test/resources/data/discoverExternalCsv/seller.csv");
        final String title = "Discover External CSV";

        assertGetResponsePostWithFile(configurationServiceUrl + configServiceAdditionalUrl + "/discovery/upload-file", HttpStatus.SC_CREATED, file1);
        assertGetResponsePostWithFile(configurationServiceUrl + configServiceAdditionalUrl + "/discovery/upload-file", HttpStatus.SC_CREATED, file2);

        final Response response = assertGetResponsePostWithBody(configurationServiceUrl + configServiceAdditionalUrl  + "/discovery/discover-external-csv", HttpStatus.SC_OK,
                "{\"columnDelimiter\": \",\",\"filenames\": [\"orderHeader.csv\",\"seller.csv\"],\"schemaName\": \"My Schema\", \"availability\": \"SHARED\", \"subjectAreaName\": \"aLDM MySchema\", \"subjectAreaKey\": \"aLDMMySchema\"}");

        // check async process of the “discover-external-model” finish successfully
        final AsyncResponseDTO asyncResponse = response.as(AsyncResponseDTO.class);
        assertTrue(getNotification(asyncResponse.getProcessId(), title, "SUCCESS (stage: Discover external model)"), "Async process didn't complete successfully");

        //check that the schema created
        Response externalSchemaResponse = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas", HttpStatus.SC_OK);
        ExternalSchemaDTO[] externalSchemas = externalSchemaResponse.as(ExternalSchemaDTO[].class);
        assertTrue(Arrays.stream(externalSchemas).anyMatch(externalSchemaDTO -> externalSchemaDTO.getSchemaKey().equals("MySchema")));

        //check if logical schema for MySchema source created
        Response responseLogicalSchema = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemas/SHARED_aia_LS_aLDMMySchema", HttpStatus.SC_OK);
        LogicalSchema logicalSchema = responseLogicalSchema.as(LogicalSchema.class);
        assertEquals(logicalSchema.getSchemaKey(),"aLDMMySchema");
        assertEquals(logicalSchema.getId(), "SHARED_aia_LS_aLDMMySchema");
        assertEquals(logicalSchema.getName(),"aLDM MySchema");

        //check if schema store for MySchema source created
        Response responseSchemaStore = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemastores/SHARED_aia_SCS_DATA_CHANNEL_aLDMMySchemaDataChannel", HttpStatus.SC_OK);
        SchemaStore schemaStore = responseSchemaStore.as(SchemaStore.class);
        assertEquals(schemaStore.getSchemaStoreKey(),"aLDMMySchemaDataChannel");
        assertEquals(schemaStore.getId(),"SHARED_aia_SCS_DATA_CHANNEL_aLDMMySchemaDataChannel");
        assertEquals(schemaStore.getName(),"aLDM MySchema");
        assertEquals(schemaStore.getLogicalSchemaKey(),"aLDMMySchema");

        //check 2 entities created
        Response externalEntityResponse = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/MySchema/entities", HttpStatus.SC_OK);
        ExternalEntityDTO[] externalEntities = externalEntityResponse.as(ExternalEntityDTO[].class);
        assertTrue(Arrays.stream(externalEntities).anyMatch(externalEntityDTO -> externalEntityDTO.getEntityKey().equals("orderHeader")));
        assertTrue(Arrays.stream(externalEntities).anyMatch(externalEntityDTO -> externalEntityDTO.getEntityKey().equals("seller")));

        //check if logical entity for orderHeader created
        final Response responseLEOrderHeader = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemas/aLDMMySchema/entities/getbykey/orderHeader", HttpStatus.SC_OK);
        LogicalEntity logicalEntityOrderHeader = responseLEOrderHeader.as(LogicalEntity.class);
        assertEquals(logicalEntityOrderHeader.getEntityKey(), "orderHeader");
        assertEquals(logicalEntityOrderHeader.getSchemaKey(), "aLDMMySchema");

        //check if entity store for orderHeader created
        final Response responseESOrderHeader = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemastores/aLDMMySchemaDataChannel/entitystores/SHARED_aia_ENS_DATA_CHANNEL_aLDMMySchemaDataChannel_orderHeader", HttpStatus.SC_OK);
        EntityStore entityStoreOrderHeader = responseESOrderHeader.as(EntityStore.class);
        assertEquals(entityStoreOrderHeader.getEntityStoreKey(), "orderHeader");
        assertEquals(entityStoreOrderHeader.getLogicalEntityKey(), "orderHeader");
        assertEquals(entityStoreOrderHeader.getLogicalSchemaKey(), "aLDMMySchema");

        //check if logical entity for seller created
        final Response responseLESeller = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemas/aLDMMySchema/entities/getbykey/seller", HttpStatus.SC_OK);
        LogicalEntity logicalEntitySeller = responseLESeller.as(LogicalEntity.class);
        assertEquals(logicalEntitySeller.getEntityKey(), "seller");
        assertEquals(logicalEntitySeller.getSchemaKey(), "aLDMMySchema");

        //check if entity store for seller created
        final Response responseESSeller = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemastores/aLDMMySchemaDataChannel/entitystores/SHARED_aia_ENS_DATA_CHANNEL_aLDMMySchemaDataChannel_seller", HttpStatus.SC_OK);
        EntityStore entityStoreSeller = responseESSeller.as(EntityStore.class);
        assertEquals(entityStoreSeller.getEntityStoreKey(), "seller");
        assertEquals(entityStoreSeller.getLogicalEntityKey(), "seller");
        assertEquals(entityStoreSeller.getLogicalSchemaKey(), "aLDMMySchema");
    }

    @Test
    public void T029_whenAddExternalSchema_shouldCreateExternalSchemaForJson() {
        ExternalSchemaDTO externalSchemaDTO = DTOCreatorsUtils.createExternalSchemaDTOForJson("Schemakey29");
        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas",
                HttpStatus.SC_CREATED, externalSchemaDTO);
        ExternalSchemaDTO responseExternalSchemaDTO = response.as(ExternalSchemaDTO.class);
        assertEquals(externalSchemaDTO.getSchemaKey(), responseExternalSchemaDTO.getSchemaKey());
        assertEquals(AvailabilityDTO.EXTERNAL, responseExternalSchemaDTO.getAvailability());

        //check schema created
        response = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/Schemakey29", HttpStatus.SC_OK);
        ExternalSchemaDTO responseExternalSchemaDTO2 = response.as(ExternalSchemaDTO.class);
        assertEquals(externalSchemaDTO.getSchemaKey(), responseExternalSchemaDTO2.getSchemaKey());
        assertEquals(AvailabilityDTO.EXTERNAL, responseExternalSchemaDTO.getAvailability());


        ExternalKafkaSchemaCollectionRulesDTO collectionRules = (ExternalKafkaSchemaCollectionRulesDTO) responseExternalSchemaDTO.getCollectionRules();
        assertEquals(collectionRules.getStoreType(), ExternalSchemaCollectionRulesDTO.StoreTypeEnum.KAFKA);
        assertEquals(collectionRules.getInputDataChannel(), "inputDataChannel");
        assertEquals(collectionRules.getSkipNodeFromParsing(), "skipNodeFromParsing");
        assertEquals(collectionRules.getDeleteEventJsonPath(), "deleteEventJsonPath");
        assertEquals(collectionRules.getDeleteEventOperation(), "deleteEventOperation");
        assertEquals(collectionRules.getImplicitHandlerPreviousNode(), "implicitHandlerPreviousNode");
        assertEquals(collectionRules.getImplicitHandlerCurrentNode(), "implicitHandlerCurrentNode");
    }

    @Test
    public void T030_whenCommitChangeRequest_shouldComplete() {
        commitChangeRequest();
    }

    private boolean getNotification(final int processId, final String title, final String expectedStatus) {
        LogUtils.log("notifications verification for " + title);
        final String url = notificationsServiceUrl + '/' + processId;
        int attempt = 1;
        while (attempt++ <= 100) {
            sleep(5000);
            Response response;
            try {
                response = assertGetResponseGet(url, HttpStatus.SC_OK);
            } catch (final AssertionError e) {
                response = null;
            }
            if (response != null) {
                final NotificationDTO notification = response.getBody().as(NotificationDTO.class);
                final String status = notification.getStatus();
                if (status.contains(expectedStatus)) {
                    LogUtils.log("Process finished successfully for " + title + ", attempt: " + attempt);
                    return true;
                }
                if (status.contains("FAILED")) {
                    LogUtils.log("Process failed for " + title + ", attempt: " + attempt);
                    return false;
                }
            }
            LogUtils.log("Waiting for result: " + title + ", attempt: " + attempt);
        }
        return false;
    }




}
