package com.amdocs.aia.il.configuration.api.unit;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import com.amdocs.aia.il.configuration.dto.CacheReferenceAttributeDTO;
import com.amdocs.aia.il.configuration.dto.CacheReferenceEntityDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.cache.CacheReferenceRepository;
import com.amdocs.aia.il.configuration.service.cache.CacheReferenceService;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CacheReferenceApiControllerUnitTest.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class CacheReferenceApiControllerUnitTest extends AbstractApiControllerUnitTest {
    private static final String PROJECT_KEY = "aia";
    private static final String ROOT_URL = "/aia/api/v1/integration-layer/projects/" + PROJECT_KEY + "/configuration/cache-reference";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CacheReferenceService cacheReferenceService;
    @MockBean
    protected CacheReferenceRepository cacheReferenceRepository;

    @Autowired
    private MessageHelper messageHelper;

    private CacheReferenceEntityDTO mockCacheReferenceEntityDTO;

    @BeforeEach
    public void setup() {
        mockCacheReferenceEntityDTO = getCacheReferenceEntityDTO("cacheReferenceEntityKey");
    }

    private static CacheReferenceEntityDTO getCacheReferenceEntityDTO(String cacheReferenceEntityKey) {
        CacheReferenceEntityDTO mockCacheReferenceEntityDTO = new CacheReferenceEntityDTO();
        mockCacheReferenceEntityDTO
                .cacheReferenceEntityKey(cacheReferenceEntityKey)
                .cacheReferenceEntityName("cacheReferenceEntityName")
                .description("description")
                .cacheReferenceAttributes(getAttributes());

        return mockCacheReferenceEntityDTO;
    }

    private static List<CacheReferenceAttributeDTO> getAttributes() {
        CacheReferenceAttributeDTO cacheReferenceAttributeDTO1 = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO1.setAttributeKey("attributeKey1");
        cacheReferenceAttributeDTO1.setAttributeName("Attribute Key1");
        cacheReferenceAttributeDTO1.setDescription("Attribute Description");
        cacheReferenceAttributeDTO1.setKeyPosition(1);
        cacheReferenceAttributeDTO1.setType("type");

        CacheReferenceAttributeDTO cacheReferenceAttributeDTO2 = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO2.setAttributeKey("attributeKey2");
        cacheReferenceAttributeDTO2.setAttributeName("Attribute Key2");
        cacheReferenceAttributeDTO2.setDescription("Attribute Description");
        cacheReferenceAttributeDTO2.setKeyPosition(0);
        cacheReferenceAttributeDTO2.setType("type");

        List<CacheReferenceAttributeDTO> cacheReferenceAttributeDTOS = new ArrayList<>();
        cacheReferenceAttributeDTOS.add(cacheReferenceAttributeDTO1);
        cacheReferenceAttributeDTOS.add(cacheReferenceAttributeDTO2);

        return cacheReferenceAttributeDTOS;
    }

    @Test
    void canAddCacheReferenceEntity() throws Exception {
        when(cacheReferenceService.save(eq(PROJECT_KEY), eq(mockCacheReferenceEntityDTO))).thenReturn(mockCacheReferenceEntityDTO);
        MvcResult result = mockMvc.perform(post(ROOT_URL).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockCacheReferenceEntityDTO)))
                .andExpect(status().isCreated()).andReturn();
        CacheReferenceEntityDTO resultViewDTO = new ObjectMapper().readValue(result.getResponse().getContentAsString(), CacheReferenceEntityDTO.class);
        assertEquals(mockCacheReferenceEntityDTO, resultViewDTO);
    }

    @Test
    void canAddCacheReferenceEntityWrongObject_ShouldThrowException() throws Exception {
        doThrow(messageHelper.createElementAlreadyExistsException(CacheEntity.ELEMENT_TYPE,
                mockCacheReferenceEntityDTO.getCacheReferenceEntityKey()))
                .when(cacheReferenceService).save(PROJECT_KEY, mockCacheReferenceEntityDTO);
        mockMvc.perform(post(ROOT_URL).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockCacheReferenceEntityDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void canGetCacheReferenceEntity() throws Exception {
        doReturn(mockCacheReferenceEntityDTO).when(cacheReferenceService).get(PROJECT_KEY,
                mockCacheReferenceEntityDTO.getCacheReferenceEntityKey());
        MvcResult result = mockMvc.perform(get(ROOT_URL + '/' + mockCacheReferenceEntityDTO.getCacheReferenceEntityKey()))
                .andExpect(status().isOk()).andReturn();
        CacheReferenceEntityDTO resultViewDTO = new ObjectMapper().readValue(result.getResponse().getContentAsString(), CacheReferenceEntityDTO.class);
        assertEquals(mockCacheReferenceEntityDTO, resultViewDTO);
    }

    @Test
    void canGetCacheReferenceEntityNotFound_ShouldThrowException() throws Exception {
        doThrow(messageHelper.createObjectDoesNotExistException("get Cache Entity", "CacheEntity",
                mockCacheReferenceEntityDTO.getCacheReferenceEntityKey()))
                .when(cacheReferenceService).get(PROJECT_KEY, mockCacheReferenceEntityDTO.getCacheReferenceEntityKey());
        mockMvc.perform(get(ROOT_URL + '/' + mockCacheReferenceEntityDTO.getCacheReferenceEntityKey()).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockCacheReferenceEntityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void canGetListCacheReferenceEntity() throws Exception {
        List<CacheReferenceEntityDTO> cacheReferenceEntityDTOList = new ArrayList<>(Collections.singletonList(mockCacheReferenceEntityDTO));
        when(cacheReferenceService.list(Mockito.anyString())).thenReturn(cacheReferenceEntityDTOList);
        mockMvc.perform(get(ROOT_URL)).andExpect(status().isOk()).andReturn();
    }

    @Test
    void canUpdateCacheReferenceEntity() throws Exception {
        doReturn(mockCacheReferenceEntityDTO).when(cacheReferenceService).update(PROJECT_KEY,
                mockCacheReferenceEntityDTO.getCacheReferenceEntityKey(), mockCacheReferenceEntityDTO);
        MvcResult result = mockMvc.perform(put(ROOT_URL + '/' + mockCacheReferenceEntityDTO.getCacheReferenceEntityKey()).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockCacheReferenceEntityDTO)))
                .andExpect(status().isCreated()).andReturn();
        CacheReferenceEntityDTO resultViewDTO = new ObjectMapper().readValue(result.getResponse().getContentAsString(), CacheReferenceEntityDTO.class);
        assertEquals(mockCacheReferenceEntityDTO, resultViewDTO);
    }

    @Test
    void canUpdateCacheReferenceEntityNotFound_ShouldThrowException() throws Exception {
        doThrow(messageHelper.createElementNotFoundException(CacheEntity.ELEMENT_TYPE,
                mockCacheReferenceEntityDTO.getCacheReferenceEntityKey()))
                .when(cacheReferenceService).update(PROJECT_KEY, mockCacheReferenceEntityDTO.getCacheReferenceEntityKey(), mockCacheReferenceEntityDTO);
        mockMvc.perform(put(ROOT_URL + '/' + mockCacheReferenceEntityDTO.getCacheReferenceEntityKey()).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockCacheReferenceEntityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void canDeleteCacheReferenceEntity() throws Exception {
        doNothing().when(cacheReferenceService).delete(eq(PROJECT_KEY), eq(mockCacheReferenceEntityDTO.getCacheReferenceEntityKey()));
        mockMvc.perform(delete(ROOT_URL + '/' + mockCacheReferenceEntityDTO.getCacheReferenceEntityKey()))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    void canDeleteCacheReferenceEntityNotFound_ShouldThrowException() throws Exception {
        doThrow(messageHelper.createElementNotFoundException(CacheEntity.ELEMENT_TYPE,
                mockCacheReferenceEntityDTO.getCacheReferenceEntityKey()))
                .when(cacheReferenceService).delete(PROJECT_KEY, mockCacheReferenceEntityDTO.getCacheReferenceEntityKey());
        mockMvc.perform(delete(ROOT_URL + '/' + mockCacheReferenceEntityDTO.getCacheReferenceEntityKey()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void canAddCacheReferenceEntity_DuplicateAttributeKeys_ShouldThrowException() throws Exception {
        doThrow(new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.DUPLICATE_ATTRIBUTE, mockCacheReferenceEntityDTO.getCacheReferenceAttributes().get(0).getAttributeKey()))
                .when(cacheReferenceService).save(PROJECT_KEY, mockCacheReferenceEntityDTO);
        mockMvc.perform(post(ROOT_URL).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockCacheReferenceEntityDTO)))
                .andExpect(status().isBadRequest());
    }
}