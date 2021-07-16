package com.amdocs.aia.il.integration.configuration;

import com.amdocs.aia.il.configuration.client.dto.CacheReferenceAttributeDTO;
import com.amdocs.aia.il.configuration.client.dto.CacheReferenceEntityDTO;
import com.amdocs.aia.il.configuration.client.dto.ChangeStatusDTO;
import com.amdocs.aia.il.configuration.client.dto.ExternalSchemaDTO;
import com.amdocs.aia.il.configuration.client.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.integration.BaseIntegrationTest;
import com.amdocs.aia.il.utils.LogUtils;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.*;
import static org.testng.Assert.assertEquals;

public class ITCacheReferenceServiceTest extends BaseIntegrationTest {
    private static final String NOT_EXISTING_KEY = "notExistingKey";


    @BeforeClass
    public void setUpConf() {
        //Every service operation requires change request to be created
        createChangeRequest("IL Cache Reference Changes - Test");
        LogUtils.log("Start running test 'ITCacheReferenceServiceTest'");
    }

    @Test
    public void T00_whenAddCacheReferenceEntity_shouldCreateCacheReferenceEntity() {
        CacheReferenceEntityDTO cacheReferenceEntityDTO = createCacheReferenceEntityDTO("CacheReferenceEntityKey1");
        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/cache-reference",
                HttpStatus.SC_CREATED, cacheReferenceEntityDTO);
        CacheReferenceEntityDTO responseCacheReferenceEntityDTO = response.as(CacheReferenceEntityDTO.class);
        assertEquals(cacheReferenceEntityDTO.getCacheReferenceEntityKey(),responseCacheReferenceEntityDTO.getCacheReferenceEntityKey());
    }

    @Test
    public void T01_whenGetCacheReferenceEntities_shouldGetAll() {
        String getCacheReferenceUrl = configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/cache-reference";
        Response response = assertGetResponseGet(getCacheReferenceUrl, HttpStatus.SC_OK);
        CacheReferenceEntityDTO[] cacheReferenceEntity = response.as(CacheReferenceEntityDTO[].class);

        List<CacheReferenceEntityDTO> cacheReferenceEntityList = Arrays.asList(cacheReferenceEntity);
        assertEquals(cacheReferenceEntityList.get(0).getCacheReferenceEntityKey(), "CacheReferenceEntityKey1");
        assertEquals(cacheReferenceEntityList.get(0).getCacheReferenceEntityName(), "cacheReferenceEntityName");
        assertEquals(cacheReferenceEntityList.get(0).getDescription(), "Description");


        Optional<CacheReferenceAttributeDTO> cacheReferenceAttributeDTO1 = cacheReferenceEntityList.get(0).getCacheReferenceAttributes().stream().filter(cacheReferenceAttributeDTO -> cacheReferenceAttributeDTO.getAttributeKey().equals("attributeKey1")).findFirst();
        CacheReferenceAttributeDTO cacheReferenceAttribute1 =  cacheReferenceAttributeDTO1.get();
        assertEquals(cacheReferenceAttribute1.getAttributeKey(), "attributeKey1");
        assertEquals(cacheReferenceAttribute1.getAttributeName(), "attribute name");
        assertEquals(cacheReferenceAttribute1.getDescription(), "Description");
        assertEquals(cacheReferenceAttribute1.getKeyPosition().toString(), "1");
        assertEquals(cacheReferenceAttribute1.getType(), "type");

        Optional<CacheReferenceAttributeDTO> cacheReferenceAttributeDTO2 = cacheReferenceEntityList.get(0).getCacheReferenceAttributes().stream().filter(cacheReferenceAttributeDTO -> cacheReferenceAttributeDTO.getAttributeKey().equals("attributeKey2")).findFirst();
        CacheReferenceAttributeDTO cacheReferenceAttribute2 =  cacheReferenceAttributeDTO2.get();
        assertEquals(cacheReferenceAttribute2.getAttributeKey(), "attributeKey2");
        assertEquals(cacheReferenceAttribute2.getAttributeName(), "attribute name");
        assertEquals(cacheReferenceAttribute2.getDescription(), "Description");
        assertEquals(cacheReferenceAttribute2.getKeyPosition().toString(), "0");
        assertEquals(cacheReferenceAttribute2.getType(), "type");

        assertEquals(cacheReferenceEntityList.get(0).getDescription(), "Description");
        assertEquals(cacheReferenceEntityList.get(0).getStatus(), ChangeStatusDTO.DRAFT);
    }

    @Test
    public void T02_whenUpdateCacheReferenceEntity_shouldUpdate() {
        Response response = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/cache-reference/" + "CacheReferenceEntityKey1",
                HttpStatus.SC_OK);
        CacheReferenceEntityDTO responseCacheReferenceEntityDTO = response.as(CacheReferenceEntityDTO.class);

        responseCacheReferenceEntityDTO.setDescription("updated entity");
        response = assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl+ "/cache-reference/" + responseCacheReferenceEntityDTO.getCacheReferenceEntityKey(),
                HttpStatus.SC_CREATED, responseCacheReferenceEntityDTO);
        CacheReferenceEntityDTO responseCacheReferenceEntityDTOUpdated = response.as(CacheReferenceEntityDTO.class);
        assertEquals(responseCacheReferenceEntityDTOUpdated.getCacheReferenceEntityKey(),responseCacheReferenceEntityDTO.getCacheReferenceEntityKey());
        assertEquals(responseCacheReferenceEntityDTOUpdated.getCacheReferenceEntityName(),responseCacheReferenceEntityDTO.getCacheReferenceEntityName());
        assertEquals(responseCacheReferenceEntityDTOUpdated.getDescription(),responseCacheReferenceEntityDTO.getDescription());
        assertEquals(responseCacheReferenceEntityDTOUpdated.getDescription(), "updated entity");
    }

    @Test
    public void T03_whenUpdateNotExistingCacheReferenceEntity_shouldReturnError() {
        CacheReferenceEntityDTO cacheReferenceEntityDTONotExisting = new CacheReferenceEntityDTO()
                .cacheReferenceEntityKey(NOT_EXISTING_KEY)
                .cacheReferenceEntityName(NOT_EXISTING_KEY);
        Response response = assertGetResponsePut(
                configurationServiceUrl + configServiceAdditionalUrl
                        + "/cache-reference/" + cacheReferenceEntityDTONotExisting.getCacheReferenceEntityKey(), HttpStatus.SC_NOT_FOUND, cacheReferenceEntityDTONotExisting);
        assertEquals(response.statusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void T04_whenDeleteCacheReferenceEntity_shouldDelete() {
        assertGetResponseDelete(configurationServiceUrl + configServiceAdditionalUrl + "/cache-reference/" + "CacheReferenceEntityKey1",
                HttpStatus.SC_OK);

        Response response = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/cache-reference/" + "CacheReferenceEntityKey1",
                HttpStatus.SC_NOT_FOUND);
        assertEquals(response.statusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void T05_whenDeleteNotExistingCacheReferenceEntity_shouldReturnError() {
        Response response = assertGetResponseDelete(configurationServiceUrl + configServiceAdditionalUrl + "/cache-reference/" + "CacheReferenceEntityKey1",
                HttpStatus.SC_NOT_FOUND);
        assertEquals(response.statusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void T06_whenUpdateNonExistCacheReferenceEntity_DuplicateAttributeKeys_shouldReturnError() {
        CacheReferenceEntityDTO cacheReferenceEntityDTO = createCacheReferenceEntityDTO("CacheReferenceEntityKey1");

        CacheReferenceAttributeDTO cacheReferenceAttributeDTO1 = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO1.setAttributeKey("attributeKey1");
        cacheReferenceAttributeDTO1.setAttributeName("attribute name");
        cacheReferenceAttributeDTO1.setDescription("Description");
        cacheReferenceAttributeDTO1.setKeyPosition(1);
        cacheReferenceAttributeDTO1.setType("type");

        cacheReferenceEntityDTO.getCacheReferenceAttributes().add(cacheReferenceAttributeDTO1);

        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/cache-reference",
                HttpStatus.SC_BAD_REQUEST, cacheReferenceEntityDTO);
    }


    @Test
    public void T07_whenAddCacheReferenceEntitiesBulk_shouldCreateCacheReferenceEntities() {
        CacheReferenceEntityDTO cacheReferenceEntityDTO1 = createCacheReferenceEntityDTO("CacheReferenceEntityKey1");
        CacheReferenceEntityDTO cacheReferenceEntityDTO2 = createCacheReferenceEntityDTO("CacheReferenceEntityKey1");

        List<CacheReferenceEntityDTO> list = new ArrayList<>();
        list.add(cacheReferenceEntityDTO1);
        list.add(cacheReferenceEntityDTO2);

        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/cache-reference-bulk",
                HttpStatus.SC_CREATED, list);


        SaveElementsResponseDTO responseSaveElementsResponseDTO = response.as(SaveElementsResponseDTO.class);
        assertEquals( responseSaveElementsResponseDTO.getSavedElementsCount(),Long.valueOf(2));

    }

    private static CacheReferenceEntityDTO createCacheReferenceEntityDTO(String cacheReferenceEntityKey ) {
        CacheReferenceEntityDTO dto = new CacheReferenceEntityDTO();
        dto.setCacheReferenceEntityKey(cacheReferenceEntityKey);
        dto.setCacheReferenceEntityName("cacheReferenceEntityName");
        dto.setDescription("Description");
        dto.setCacheReferenceAttributes(getCacheReferenceAttributeDTOList());
        dto.setStatus(ChangeStatusDTO.DRAFT);

        return dto;
    }

    private static List<CacheReferenceAttributeDTO> getCacheReferenceAttributeDTOList(){
        CacheReferenceAttributeDTO cacheReferenceAttributeDTO1 = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO1.setAttributeKey("attributeKey1");
        cacheReferenceAttributeDTO1.setAttributeName("attribute name");
        cacheReferenceAttributeDTO1.setDescription("Description");
        cacheReferenceAttributeDTO1.setKeyPosition(1);
        cacheReferenceAttributeDTO1.setType("type");

        CacheReferenceAttributeDTO cacheReferenceAttributeDTO2 = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO2.setAttributeKey("attributeKey2");
        cacheReferenceAttributeDTO2.setAttributeName("attribute name");
        cacheReferenceAttributeDTO2.setDescription("Description");
        cacheReferenceAttributeDTO2.setKeyPosition(0);
        cacheReferenceAttributeDTO2.setType("type");

        List<CacheReferenceAttributeDTO> cacheReferenceAttributeDTOS = new ArrayList<>();
        cacheReferenceAttributeDTOS.add(cacheReferenceAttributeDTO1);
        cacheReferenceAttributeDTOS.add(cacheReferenceAttributeDTO2);

        return cacheReferenceAttributeDTOS;
    }
}

