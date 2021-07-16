package com.amdocs.aia.il.configuration.api.unit;

import com.amdocs.aia.il.common.model.configuration.tables.ReplicaStoreConfiguration;
import com.amdocs.aia.il.configuration.api.ContextApiController;
import com.amdocs.aia.il.configuration.dto.CommonModelDTO;
import com.amdocs.aia.il.configuration.dto.ContextDTO;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.ContextRepository;
import com.amdocs.aia.il.configuration.service.ContextConfigurationService;
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

@WebMvcTest(value = ContextApiController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ContextApiControllerUnitTest extends AbstractApiControllerUnitTest {
    private static final String PROJECT_KEY = "aia";
    private static final String ROOT_URL = "/aia/api/v1/integration-layer/projects/" + PROJECT_KEY + "/configuration/transformation-contexts";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ContextConfigurationService contextConfigurationService;
    @MockBean
    protected ContextRepository publisherContextRepository;

    @Autowired
    private MessageHelper messageHelper;

    private ContextDTO mockPublisherContextDTO;

    @BeforeEach
    public void setup() {
        mockPublisherContextDTO = getPublisherContextDTO("Customer");
    }

    private static ContextDTO getPublisherContextDTO(String entity) {
        ContextDTO mockPublisherContextDTO = new ContextDTO().contextKey(entity + "Context");
        mockPublisherContextDTO.contextEntities(Collections.emptyList())
                .projectKey(PROJECT_KEY)
                .displayName(entity)
                .description(entity + "description")
                .storeName(mockPublisherContextDTO.getContextKey())
                .modelType(CommonModelDTO.ModelTypeEnum.CONTEXT);
        return mockPublisherContextDTO;
    }

    @Test
    void canAddPublisherContext() throws Exception {
        when(contextConfigurationService.save(eq(mockPublisherContextDTO.getProjectKey()), eq(mockPublisherContextDTO))).thenReturn(mockPublisherContextDTO);
        MvcResult result = mockMvc.perform(post(ROOT_URL).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockPublisherContextDTO)))
                .andExpect(status().isCreated()).andReturn();
        ContextDTO resultViewDTO = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ContextDTO.class);
        assertEquals(mockPublisherContextDTO, resultViewDTO);
    }

    @Test
    void canAddPublisherContextWrongObject() throws Exception {
        doThrow(messageHelper.createElementAlreadyExistsException(ReplicaStoreConfiguration.ELEMENT_TYPE, mockPublisherContextDTO.getStoreName()))
                .when(contextConfigurationService).save(PROJECT_KEY, mockPublisherContextDTO);
        mockMvc.perform(post(ROOT_URL).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockPublisherContextDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void canGetPublisherContext() throws Exception {
        doReturn(mockPublisherContextDTO).when(contextConfigurationService).get(eq(mockPublisherContextDTO.getProjectKey()), eq(mockPublisherContextDTO.getStoreName()));
        MvcResult result = mockMvc.perform(get(ROOT_URL + '/' + mockPublisherContextDTO.getStoreName()))
                .andExpect(status().isOk()).andReturn();
        ContextDTO resultViewDTO = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ContextDTO.class);
        assertEquals(mockPublisherContextDTO, resultViewDTO);
    }

    @Test
    void canListPublisherContexts() throws Exception {
        List<ContextDTO> publisherContextList = new ArrayList<>(Collections.singletonList(mockPublisherContextDTO));
        when(contextConfigurationService.list(Mockito.anyString())).thenReturn(publisherContextList);
        mockMvc.perform(get(ROOT_URL)).andExpect(status().isOk()).andReturn();
    }

    @Test
    void canUpdatePublisherContext() throws Exception {
        doReturn(mockPublisherContextDTO).when(contextConfigurationService).update(PROJECT_KEY, mockPublisherContextDTO.getStoreName(), mockPublisherContextDTO);
        MvcResult result = mockMvc.perform(put(ROOT_URL + '/' + mockPublisherContextDTO.getStoreName()).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockPublisherContextDTO)))
                .andExpect(status().isOk()).andReturn();
        ContextDTO resultViewDTO = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ContextDTO.class);
        assertEquals(mockPublisherContextDTO, resultViewDTO);
    }

    @Test
    void canDeletePublisherContext() throws Exception {
        doNothing().when(contextConfigurationService).delete(PROJECT_KEY, mockPublisherContextDTO.getStoreName());
        mockMvc.perform(delete(ROOT_URL + '/' + mockPublisherContextDTO.getStoreName())).andExpect(status().isOk()).andReturn();
    }

    @Test
    void canDeletePublisherContext_shouldReturn200() throws Exception {
        doNothing().when(contextConfigurationService).delete(eq(mockPublisherContextDTO.getProjectKey()), eq(mockPublisherContextDTO.getStoreName()));
        mockMvc.perform(delete(ROOT_URL + '/' + mockPublisherContextDTO.getStoreName()))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    void canUpdatePublisherContext_shouldReturn200() throws Exception {
        when(contextConfigurationService.update(eq(mockPublisherContextDTO.getProjectKey()), eq(mockPublisherContextDTO.getStoreName()), eq(mockPublisherContextDTO))).thenReturn(mockPublisherContextDTO);
        mockMvc.perform(put(ROOT_URL + '/' + mockPublisherContextDTO.getStoreName())
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsBytes(mockPublisherContextDTO)))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    void canUpdatePublisherContextWithWrongObject() throws Exception {
        doThrow(messageHelper.createElementAlreadyExistsException(ReplicaStoreConfiguration.ELEMENT_TYPE, mockPublisherContextDTO.getStoreName()))
                .when(contextConfigurationService).update(PROJECT_KEY, mockPublisherContextDTO.getStoreName(), mockPublisherContextDTO);
        mockMvc.perform(put(ROOT_URL + '/' + mockPublisherContextDTO.getStoreName()).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(mockPublisherContextDTO))).andExpect(status().isBadRequest());
    }
}