package com.amdocs.aia.il.integration.configuration;

import com.amdocs.aia.il.configuration.client.dto.*;
import com.amdocs.aia.il.integration.BaseIntegrationTest;
import com.amdocs.aia.il.utils.LogUtils;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.assertGetResponseGet;
import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.assertGetResponsePost;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class ITContextSourcesServiceTest extends BaseIntegrationTest {
    public static final String CACHE = "CACHE";
    private static final String NOT_EXISTING_KEY = "notExistingKey";

    @BeforeClass
    public void setUpConf() {
        //Every service operation requires change request to be created
        createChangeRequest("IL Context sources Changes - Test");
        LogUtils.log("Start running test 'ITContextSourcesServiceTest'");
    }

    @Override
    protected File getConfigurationZip() {
        return new File("src/test/resources/data/data.zip");
    }

   @Test
    public void T00_whenGetContextSources_shouldGetAll() {
        String getContextSourcesUrl = configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/context-sources/shared";
        Response response = assertGetResponseGet(getContextSourcesUrl, HttpStatus.SC_OK);
        ContextSourceDTO[] contextSourceDTOs = response.as(ContextSourceDTO[].class);
        List<ContextSourceDTO> contextSources = Arrays.asList(contextSourceDTOs);
        assertEquals(contextSources.size(), 16);

        Optional<ContextSourceDTO> cacheContextSourceDTO = contextSources.stream().filter(contextSourceDTO -> contextSourceDTO.getSchemaKey().equals("CACHE")).findFirst();
        ContextSourceDTO cacheContextSource = cacheContextSourceDTO.get();
        assertEquals(cacheContextSource.getSchemaKey(), "CACHE");
        assertEquals(cacheContextSource.getSchemaName(), "Cached Entity");
        assertEquals(cacheContextSource.getSchemaType().toString(), ContextSourceDTO.SchemaTypeEnum.CACHE.name());
        assertEquals(cacheContextSource.getContextSourceEntities().size(), 3);

        Optional<ContextSourceEntityDTO> cacheContextSourceEntityDTO1 = cacheContextSource.getContextSourceEntities().stream().filter(contextSourceEntityDTO1 -> contextSourceEntityDTO1.getEntityKey().equals("GEONAME_REF")).findFirst();
        ContextSourceEntityDTO cacheContextSourceEntity1 = cacheContextSourceEntityDTO1.get();
        assertEquals(cacheContextSourceEntity1.getEntityKey(), "GEONAME_REF");
        assertEquals(cacheContextSourceEntity1.getEntityName(), "GEONAME_REF");

        Optional<ContextSourceEntityDTO> cacheContextSourceEntityDTO2 = cacheContextSource.getContextSourceEntities().stream().filter(contextSourceEntityDTO1 -> contextSourceEntityDTO1.getEntityKey().equals("ServiceRequestType")).findFirst();
        ContextSourceEntityDTO cacheContextSourceEntity2 = cacheContextSourceEntityDTO2.get();
        assertEquals(cacheContextSourceEntity2.getEntityKey(), "ServiceRequestType");
        assertEquals(cacheContextSourceEntity2.getEntityName(), "Service Request Type");


        Optional<ContextSourceDTO> externalContextSourceDTO = contextSources.stream().filter(contextSourceDTO -> contextSourceDTO.getSchemaKey().equals("CRM")).findFirst();
        ContextSourceDTO externalContextSource = externalContextSourceDTO.get();
        assertEquals(externalContextSource.getSchemaKey(), "CRM");
        assertEquals(externalContextSource.getSchemaName(), "CRM");
        assertEquals(externalContextSource.getSchemaType().toString(), ContextSourceDTO.SchemaTypeEnum.EXTERNAL.name());
        assertEquals(externalContextSource.getContextSourceEntities().size(), 46);

        Optional<ContextSourceEntityDTO> externalContextSourceEntityDTO1 = externalContextSource.getContextSourceEntities().stream().filter(contextSourceEntityDTO1 -> contextSourceEntityDTO1.getEntityKey().equals("TABLE_QUEUE")).findFirst();
        ContextSourceEntityDTO externalContextSourceEntity1 = externalContextSourceEntityDTO1.get();
        assertEquals(externalContextSourceEntity1.getEntityKey(), "TABLE_QUEUE");
        assertEquals(externalContextSourceEntity1.getEntityName(), "TABLE_QUEUE");

        Optional<ContextSourceEntityDTO> externalContextSourceEntityDTO2 = externalContextSource.getContextSourceEntities().stream().filter(contextSourceEntityDTO1 -> contextSourceEntityDTO1.getEntityKey().equals("TABLE_BLG_ARGMNT")).findFirst();
        ContextSourceEntityDTO externalContextSourceEntity2 = externalContextSourceEntityDTO2.get();
        assertEquals(externalContextSourceEntity2.getEntityKey(), "TABLE_BLG_ARGMNT");
        assertEquals(externalContextSourceEntity2.getEntityName(), "TABLE_BLG_ARGMNT");

        Optional<ContextSourceDTO> referenceContextSourceDTO = contextSources.stream().filter(contextSourceDTO -> contextSourceDTO.getSchemaKey().equals("aLDMCustomerService")).findFirst();
        ContextSourceDTO referenceContextSource = referenceContextSourceDTO.get();
        assertEquals(referenceContextSource.getSchemaKey(), "aLDMCustomerService");
        assertEquals(referenceContextSource.getSchemaName(), "CustomerService");
        assertEquals(referenceContextSource.getSchemaType().toString(), ContextSourceDTO.SchemaTypeEnum.REFERENCE.name());
        assertEquals(referenceContextSource.getContextSourceEntities().size(), 7);

        Optional<ContextSourceEntityDTO> referenceContextSourceEntityDTO1 = referenceContextSource.getContextSourceEntities().stream().filter(contextSourceEntityDTO1 -> contextSourceEntityDTO1.getEntityKey().equals("ServiceRequestSeverity")).findFirst();
        ContextSourceEntityDTO referenceContextSourceEntity1 = referenceContextSourceEntityDTO1.get();
        assertEquals(referenceContextSourceEntity1.getEntityKey(), "ServiceRequestSeverity");
        assertEquals(referenceContextSourceEntity1.getEntityName(), "Service Request Severity");

        Optional<ContextSourceEntityDTO> referenceContextSourceEntityDTO2 = referenceContextSource.getContextSourceEntities().stream().filter(contextSourceEntityDTO1 -> contextSourceEntityDTO1.getEntityKey().equals("ServiceRequestPriority")).findFirst();
        ContextSourceEntityDTO referenceContextSourceEntity2 = referenceContextSourceEntityDTO2.get();
        assertEquals(referenceContextSourceEntity2.getEntityKey(), "ServiceRequestPriority");
        assertEquals(referenceContextSourceEntity2.getEntityName(), "Service Request Priority");
    }

    @Test
    public void T01_whenGetContextSources_shouldGetAllForSpecificSchema() {
        String getContextSourcesUrl = configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/context-sources/shared?schemaType=CACHE";
        Response response = assertGetResponseGet(getContextSourcesUrl, HttpStatus.SC_OK);
        ContextSourceDTO[] contextSourceDTOs = response.as(ContextSourceDTO[].class);
        List<ContextSourceDTO> contextSources = Arrays.asList(contextSourceDTOs);
        assertEquals(contextSources.size(), 1);

        Optional<ContextSourceDTO> cacheContextSourceDTO = contextSources.stream().filter(contextSourceDTO -> contextSourceDTO.getSchemaKey().equals("CACHE")).findFirst();
        ContextSourceDTO cacheContextSource = cacheContextSourceDTO.get();
        assertEquals(cacheContextSource.getSchemaKey(), "CACHE");
        assertEquals(cacheContextSource.getSchemaName(), "Cached Entity");
        assertEquals(cacheContextSource.getSchemaType().toString(), ContextSourceDTO.SchemaTypeEnum.CACHE.name());
        assertEquals(cacheContextSource.getContextSourceEntities().size(), 3);

        getContextSourcesUrl = configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/context-sources/shared?schemaType=EXTERNAL";
        response = assertGetResponseGet(getContextSourcesUrl, HttpStatus.SC_OK);
        contextSourceDTOs = response.as(ContextSourceDTO[].class);
        contextSources = Arrays.asList(contextSourceDTOs);
        assertEquals(contextSources.size(), 9);
        Optional<ContextSourceDTO> externalContextSourceDTO = contextSources.stream().filter(contextSourceDTO -> contextSourceDTO.getSchemaKey().equals("CRM")).findFirst();
        ContextSourceDTO externalContextSource = externalContextSourceDTO.get();
        assertEquals(externalContextSource.getSchemaKey(), "CRM");
        assertEquals(externalContextSource.getSchemaName(), "CRM");
        assertEquals(externalContextSource.getSchemaType().toString(), ContextSourceDTO.SchemaTypeEnum.EXTERNAL.name());
        assertEquals(externalContextSource.getContextSourceEntities().size(), 46);

        getContextSourcesUrl = configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/context-sources/shared?schemaType=REFERENCE";
        response = assertGetResponseGet(getContextSourcesUrl, HttpStatus.SC_OK);
        contextSourceDTOs = response.as(ContextSourceDTO[].class);
        contextSources = Arrays.asList(contextSourceDTOs);
        assertEquals(contextSources.size(), 6);
        Optional<ContextSourceDTO> referenceContextSourceDTO = contextSources.stream().filter(contextSourceDTO -> contextSourceDTO.getSchemaKey().equals("aLDMCustomerService")).findFirst();
        ContextSourceDTO referenceContextSource = referenceContextSourceDTO.get();
        assertEquals(referenceContextSource.getSchemaKey(), "aLDMCustomerService");
        assertEquals(referenceContextSource.getSchemaName(), "CustomerService");
        assertEquals(referenceContextSource.getSchemaType().toString(), ContextSourceDTO.SchemaTypeEnum.REFERENCE.name());
        assertEquals(referenceContextSource.getContextSourceEntities().size(), 7);
    }

    @Test
    public void T02_whenGetContextEntitiesMetadata_shouldGetAll() {
        String getContextSourcesUrl = configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/context-sources/context-entities-metadata";
        EntitiesDTO entities = getContextEntitiesRef();
        Response response = assertGetResponsePost(getContextSourcesUrl, HttpStatus.SC_OK, entities);
        BaseEntityDTO[] baseEntityDTOS = response.as(BaseEntityDTO[].class);
        List<BaseEntityDTO> baseEntities = Arrays.asList(baseEntityDTOS);
        assertEquals(baseEntities.size(), 3);

        Optional<BaseEntityDTO> cacheBaseEntityDTO = baseEntities.stream().filter(baseEntityDTO -> baseEntityDTO.getEntityKey().equals("GEONAME_REF")).findFirst();
        BaseEntityDTO cacheBaseEntity = cacheBaseEntityDTO.get();
        assertEquals(cacheBaseEntity.getEntityKey(), "GEONAME_REF");
        assertEquals(cacheBaseEntity.getEntityName(), "GEONAME_REF");
        assertEquals(cacheBaseEntity.getSchemaKey(), "CACHE");
        assertEquals(cacheBaseEntity.getAttributes().size(), 9);

        Optional<BaseAttributeDTO> cacheBaseAttributeDTO = cacheBaseEntity.getAttributes().stream().filter(baseAttributeDTO -> baseAttributeDTO.getAttributeKey().equals("stateKey")).findFirst();
        BaseAttributeDTO cacheBaseAttribute = cacheBaseAttributeDTO.get();
        assertEquals(cacheBaseAttribute.getAttributeKey(), "stateKey");
        assertEquals(cacheBaseAttribute.getAttributeName(), "State Key");
        assertEquals(cacheBaseAttribute.getDatatype(), "STRING");
        assertNull(cacheBaseAttribute.getKeyPosition());

        Optional<BaseEntityDTO> externalBaseEntityDTO = baseEntities.stream().filter(baseEntityDTO -> baseEntityDTO.getEntityKey().equals("TBCATALOG_ITEM_CTG")).findFirst();
        BaseEntityDTO externalBaseEntity = externalBaseEntityDTO.get();
        assertEquals(externalBaseEntity.getEntityKey(), "TBCATALOG_ITEM_CTG");
        assertEquals(externalBaseEntity.getEntityName(), "TBCATALOG_ITEM_CTG");
        assertEquals(externalBaseEntity.getSchemaKey(), "OMS");
        assertEquals(externalBaseEntity.getAttributes().size(), 9);

        Optional<BaseAttributeDTO> externalBaseAttributeDTO = externalBaseEntity.getAttributes().stream().filter(baseAttributeDTO -> baseAttributeDTO.getAttributeKey().equals("CID")).findFirst();
        BaseAttributeDTO externalBaseAttribute = externalBaseAttributeDTO.get();
        assertEquals(externalBaseAttribute.getAttributeKey(), "CID");
        assertEquals(externalBaseAttribute.getAttributeName(), "CID");
        assertEquals(externalBaseAttribute.getDatatype(), "STRING");
        assertEquals(externalBaseAttribute.getKeyPosition().toString(), "1");

        Optional<BaseEntityDTO> referenceBaseEntityDTO = baseEntities.stream().filter(baseEntityDTO -> baseEntityDTO.getEntityKey().equals("CustomerSubType")).findFirst();
        BaseEntityDTO referenceBaseEntity = referenceBaseEntityDTO.get();
        assertEquals(referenceBaseEntity.getEntityKey(), "CustomerSubType");
        assertEquals(referenceBaseEntity.getEntityName(), "Customer Sub Type");
        assertEquals(referenceBaseEntity.getSchemaKey(), "aLDMCustomer");
        assertEquals(referenceBaseEntity.getAttributes().size(), 5);

        Optional<BaseAttributeDTO> referenceBaseAttributeDTO = referenceBaseEntity.getAttributes().stream().filter(baseAttributeDTO -> baseAttributeDTO.getAttributeKey().equals("customerTypeId")).findFirst();
        BaseAttributeDTO referenceBaseAttribute = referenceBaseAttributeDTO.get();
        assertEquals(referenceBaseAttribute.getAttributeKey(), "customerTypeId");
        assertEquals(referenceBaseAttribute.getAttributeName(), "Customer Type Id");
        assertEquals(referenceBaseAttribute.getDatatype(), "string");
        assertNull(referenceBaseAttribute.getKeyPosition());
    }

    @Test
    public void T03_whenGetContextEntitiesMetadata_WithNotExistingEntity_Return404() {
        EntitiesDTO entities = new EntitiesDTO();
        ContextEntityRefDTO contextEntityRef1 = new ContextEntityRefDTO();
        contextEntityRef1
                .entityKey(NOT_EXISTING_KEY)
                .schemaKey(CACHE)
                .type(ContextEntityRefDTO.TypeEnum.CACHE);

        entities.setContextEntities(Collections.singletonList(contextEntityRef1));

        assertGetResponsePost(
                configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/context-sources/context-entities-metadata"
                , HttpStatus.SC_NOT_FOUND, entities);
    }

    private EntitiesDTO getContextEntitiesRef() {
        EntitiesDTO entitiesDTO = new EntitiesDTO();

        ContextEntityRefDTO contextEntityRef1 = new ContextEntityRefDTO();
        contextEntityRef1
                .entityKey("GEONAME_REF")
                .schemaKey(CACHE)
                .type(ContextEntityRefDTO.TypeEnum.CACHE);

        ContextEntityRefDTO contextEntityRef2 = new ContextEntityRefDTO();
        contextEntityRef2
                .entityKey("TBCATALOG_ITEM_CTG")
                .schemaKey("OMS")
                .type(ContextEntityRefDTO.TypeEnum.EXTERNAL);

        ContextEntityRefDTO contextEntityRef3 = new ContextEntityRefDTO();
        contextEntityRef3
                .entityKey("CustomerSubType")
                .schemaKey("aLDMCustomer")
                .type(ContextEntityRefDTO.TypeEnum.REFERENCE);

        List<ContextEntityRefDTO> contextEntityRefList = new ArrayList<>();
        contextEntityRefList.add(contextEntityRef1);
        contextEntityRefList.add(contextEntityRef2);
        contextEntityRefList.add(contextEntityRef3);

        entitiesDTO.setContextEntities(contextEntityRefList);

        return entitiesDTO;
    }
}
