package com.amdocs.aia.il.configuration.api.unit;

import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.service.context.ContextSourcesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ContextSourcesControllerUnitTest.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class ContextSourcesControllerUnitTest extends AbstractApiControllerUnitTest {
    private static final String PROJECT_KEY = "aia";
    private static final String ROOT_URL = "/aia/api/v1/integration-layer/projects/" + PROJECT_KEY + "/configuration/context-sources";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ContextSourcesService contextSourcesService;
    @Autowired
    private MessageHelper messageHelper;

    private ContextSourceDTO mockContextSourceDTO;
    private BaseEntityDTO mockBaseEntityDTO;
    private EntitiesDTO mockContextEntitiesRef;

    @BeforeEach
    public void setup() {
        mockContextSourceDTO = getMockContextSourceDTO("CACHE");
        mockBaseEntityDTO = getMockBaseEntityDTO("entityKey");
        mockContextEntitiesRef = getMockContextEntitiesRefDTO();
    }

    private static ContextSourceDTO getMockContextSourceDTO(String contextSourceKey) {
        ContextSourceDTO mockContextSourceDTO = new ContextSourceDTO();
        mockContextSourceDTO
                .schemaKey(contextSourceKey)
                .schemaName("contextSourceName")
                .schemaType(ContextSourceDTO.SchemaTypeEnum.CACHE)
                .contextSourceEntities(Collections.singletonList(getMockContextSourceEntityDTO()));

        return mockContextSourceDTO;
    }

    private static ContextSourceEntityDTO getMockContextSourceEntityDTO() {
        ContextSourceEntityDTO mockContextSourceEntityDTO = new ContextSourceEntityDTO();
        mockContextSourceEntityDTO
                .entityKey("contextSourceEntityKey")
                .entityName("contextSourceEntityName");

        return mockContextSourceEntityDTO;
    }

    private static BaseEntityDTO getMockBaseEntityDTO(String entityKey) {
        BaseEntityDTO mockBaseEntityDTO = new BaseEntityDTO();
        mockBaseEntityDTO
                .entityKey(entityKey)
                .entityName("entityName")
                .schemaKey("schemaKey")
                .attributes(getMockBaseAttributeDTO());

        return mockBaseEntityDTO;
    }

    private static List<BaseAttributeDTO> getMockBaseAttributeDTO() {
        BaseAttributeDTO baseAttributeDTO1 = new BaseAttributeDTO();
        baseAttributeDTO1
                .attributeKey("attr1")
                .attributeName("attribute1 name")
                .datatype("String")
                .keyPosition(1);

        BaseAttributeDTO baseAttributeDTO2 = new BaseAttributeDTO();
        baseAttributeDTO2
                .attributeKey("attr2")
                .attributeName("attribute2 name")
                .datatype("INTEGER")
                .keyPosition(null);

        List<BaseAttributeDTO> attributeDTOList = new ArrayList<>();
        attributeDTOList.add(baseAttributeDTO1);
        attributeDTOList.add(baseAttributeDTO2);

        return attributeDTOList;
    }

    private static EntitiesDTO getMockContextEntitiesRefDTO() {
        EntitiesDTO entitiesDTO = new EntitiesDTO();

        ContextEntityRefDTO contextEntityRefDTO1 = new ContextEntityRefDTO();
        contextEntityRefDTO1
                .entityKey("entityKey1")
                .schemaKey("schemaKey1")
                .type(ContextEntityRefDTO.TypeEnum.EXTERNAL);

        entitiesDTO.setContextEntities(Collections.singletonList(contextEntityRefDTO1));
        return entitiesDTO;
    }

    @Test
    void canGetContextSources() throws Exception {
        List<ContextSourceDTO> schemaSourceDTOList = new ArrayList<>(Collections.singletonList(mockContextSourceDTO));
        when(contextSourcesService.getContextSources(Mockito.anyString(), Mockito.anyString())).thenReturn(schemaSourceDTOList);
        mockMvc.perform(get(ROOT_URL + "/shared?schemaType=CACHE").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].schemaKey").value(mockContextSourceDTO.getSchemaKey()))
                .andExpect(jsonPath("$[0].schemaName").value(mockContextSourceDTO.getSchemaName()))
                .andExpect(jsonPath("$[0].schemaType").value(mockContextSourceDTO.getSchemaType().name()))
                .andExpect(jsonPath("$[0].contextSourceEntities[0].entityKey").value(mockContextSourceDTO.getContextSourceEntities().get(0).getEntityKey()))
                .andExpect(jsonPath("$[0].contextSourceEntities[0].entityName").value(mockContextSourceDTO.getContextSourceEntities().get(0).getEntityName()));
    }

    @Test
    void canGetContextEntitiesMetadata() throws Exception {
        List<BaseEntityDTO> baseEntityDTOs = new ArrayList<>(Collections.singletonList(mockBaseEntityDTO));
        when(contextSourcesService.searchContextEntitiesMetadata(Mockito.anyString(), Mockito.any())).thenReturn(baseEntityDTOs);
        mockMvc.perform(post(ROOT_URL + "/context-entities-metadata").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockContextEntitiesRef)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].entityKey").value(baseEntityDTOs.get(0).getEntityKey()))
                .andExpect(jsonPath("$[0].entityName").value(baseEntityDTOs.get(0).getEntityName()))
                .andExpect(jsonPath("$[0].schemaKey").value(baseEntityDTOs.get(0).getSchemaKey()))
                .andExpect(jsonPath("$[0].attributes[0].attributeKey").value(baseEntityDTOs.get(0).getAttributes().get(0).getAttributeKey()))
                .andExpect(jsonPath("$[0].attributes[0].attributeName").value(baseEntityDTOs.get(0).getAttributes().get(0).getAttributeName()))
                .andExpect(jsonPath("$[0].attributes[0].datatype").value(baseEntityDTOs.get(0).getAttributes().get(0).getDatatype()))
                .andExpect(jsonPath("$[0].attributes[0].keyPosition").value(baseEntityDTOs.get(0).getAttributes().get(0).getKeyPosition()))
                .andExpect(jsonPath("$[0].attributes[1].attributeKey").value(baseEntityDTOs.get(0).getAttributes().get(1).getAttributeKey()))
                .andExpect(jsonPath("$[0].attributes[1].attributeName").value(baseEntityDTOs.get(0).getAttributes().get(1).getAttributeName()))
                .andExpect(jsonPath("$[0].attributes[1].datatype").value(baseEntityDTOs.get(0).getAttributes().get(1).getDatatype()))
                .andExpect(jsonPath("$[0].attributes[1].keyPosition").value(baseEntityDTOs.get(0).getAttributes().get(1).getKeyPosition()));
    }

    @Test
    void canGetContextEntitiesMetadata_EntityNotFound_ShouldThrowException() throws Exception {
        doThrow(messageHelper.createObjectDoesNotExistException("get External Entity", "ExternalEntity",
                mockContextEntitiesRef.getContextEntities().get(0).getEntityKey()))
                .when(contextSourcesService).searchContextEntitiesMetadata(PROJECT_KEY, mockContextEntitiesRef);
        mockMvc.perform(post(ROOT_URL + "/context-entities-metadata").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockContextEntitiesRef)))
                .andExpect(status().isNotFound());
    }
}