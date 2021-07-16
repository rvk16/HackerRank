package com.amdocs.aia.il.configuration.api.unit;

import com.amdocs.aia.il.common.model.configuration.tables.ReplicaStoreConfiguration;
import com.amdocs.aia.il.configuration.api.TransformationApiController;
import com.amdocs.aia.il.configuration.dto.CommonModelDTO;
import com.amdocs.aia.il.configuration.dto.TransformationDTO;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.TransformationRepository;
import com.amdocs.aia.il.configuration.service.TransformationConfigurationService;
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

@WebMvcTest(value = TransformationApiController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class TransformationApiControllerUnitTest extends AbstractApiControllerUnitTest {
    private static final String PROJECT_KEY = "aia";
    private static final String ROOT_URL = "/aia/api/v1/integration-layer/projects/" + PROJECT_KEY + "/configuration/transformations";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransformationConfigurationService transformationConfigurationService;
    @MockBean
    private TransformationRepository publisherTransformationRepository;

    @Autowired
    private MessageHelper messageHelper;

    private TransformationDTO mockPublisherTransformationDTO;

    @BeforeEach
    public void setup() {
        mockPublisherTransformationDTO = getPublisherTransformationDTO("aLDMCustomer", "Customer");
    }

    private static TransformationDTO getPublisherTransformationDTO(String schema, String entity) {
        TransformationDTO mockPublisherTransformationDTO = new TransformationDTO().contextKey(entity + "PublisherContext");
        mockPublisherTransformationDTO
                .targetSchemaStoreKey(schema)
                .targetEntityStoreKey(entity)
                .sourceType(TransformationDTO.SourceTypeEnum.CONTEXT)
                .implementationType(TransformationDTO.ImplementationTypeEnum.SQL)
                .customScript("select * from " + entity)
                .projectKey(PROJECT_KEY)
                .displayName("TABLE_" + entity.toUpperCase())
                .description(entity + "description")
                .storeName(schema + "-" + entity)
                .modelType(CommonModelDTO.ModelTypeEnum.TRANSFORMATION);
        return mockPublisherTransformationDTO;
    }

    @Test
    void canAddPublisherTransformation() throws Exception {
        when(transformationConfigurationService.save(eq(mockPublisherTransformationDTO.getProjectKey()), eq(mockPublisherTransformationDTO))).thenReturn(mockPublisherTransformationDTO);
        MvcResult result = mockMvc.perform(post(ROOT_URL).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockPublisherTransformationDTO)))
                .andExpect(status().isCreated()).andReturn();
        TransformationDTO resultViewDTO = new ObjectMapper().readValue(result.getResponse().getContentAsString(), TransformationDTO.class);
        assertEquals(mockPublisherTransformationDTO, resultViewDTO);
    }


    @Test
    void canAddPublisherTransformationWrongObject() throws Exception {
        doThrow(messageHelper.createElementAlreadyExistsException(ReplicaStoreConfiguration.ELEMENT_TYPE, mockPublisherTransformationDTO.getStoreName()))
                .when(transformationConfigurationService).save(PROJECT_KEY, mockPublisherTransformationDTO);
        mockMvc.perform(post(ROOT_URL).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockPublisherTransformationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void canGetPublisherTransformation() throws Exception {
        doReturn(mockPublisherTransformationDTO).when(transformationConfigurationService).get(eq(mockPublisherTransformationDTO.getProjectKey()), eq(mockPublisherTransformationDTO.getStoreName()));
        MvcResult result = mockMvc.perform(get(ROOT_URL + '/' + mockPublisherTransformationDTO.getStoreName()))
                .andExpect(status().isOk()).andReturn();
        TransformationDTO resultViewDTO = new ObjectMapper().readValue(result.getResponse().getContentAsString(), TransformationDTO.class);
        assertEquals(mockPublisherTransformationDTO, resultViewDTO);
    }

    @Test
    void canListPublisherTransformation() throws Exception {
        List<TransformationDTO> publisherTransformationDTOList = new ArrayList<>(Collections.singletonList(mockPublisherTransformationDTO));
        publisherTransformationDTOList.add(mockPublisherTransformationDTO);
        when(transformationConfigurationService.list(Mockito.anyString())).thenReturn(publisherTransformationDTOList);
        mockMvc.perform(get(ROOT_URL).content(new ObjectMapper().writeValueAsString(mockPublisherTransformationDTO)))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    void canUpdatePublisherTransformation() throws Exception {
        doReturn(mockPublisherTransformationDTO).when(transformationConfigurationService).update(eq(mockPublisherTransformationDTO.getProjectKey()), eq(mockPublisherTransformationDTO.getStoreName()), eq(mockPublisherTransformationDTO));
        MvcResult result = mockMvc.perform(put(ROOT_URL + '/' + mockPublisherTransformationDTO.getStoreName()).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockPublisherTransformationDTO)))
                .andExpect(status().isOk()).andReturn();
        TransformationDTO resultViewDTO = new ObjectMapper().readValue(result.getResponse().getContentAsString(), TransformationDTO.class);
        assertEquals(mockPublisherTransformationDTO, resultViewDTO);
    }

    @Test
    void canDeletePublisherTransformation_shouldReturn200() throws Exception {
        doNothing().when(transformationConfigurationService).delete(eq(mockPublisherTransformationDTO.getProjectKey()), eq(mockPublisherTransformationDTO.getStoreName()));
        mockMvc.perform(delete(ROOT_URL + '/' + mockPublisherTransformationDTO.getStoreName()))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    void canUpdatePublisherTransformation_shouldReturn200() throws Exception {
        when(transformationConfigurationService.update(eq(mockPublisherTransformationDTO.getProjectKey()), eq(mockPublisherTransformationDTO.getStoreName()), eq(mockPublisherTransformationDTO))).thenReturn(mockPublisherTransformationDTO);
        mockMvc.perform(put(ROOT_URL + '/' + mockPublisherTransformationDTO.getStoreName())
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsBytes(mockPublisherTransformationDTO)))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    void canUpdatePublisherTransformationWithWrongObject() throws Exception {
        doThrow(messageHelper.createElementAlreadyExistsException(ReplicaStoreConfiguration.ELEMENT_TYPE, mockPublisherTransformationDTO.getStoreName()))
                .when(transformationConfigurationService).update(PROJECT_KEY, mockPublisherTransformationDTO.getStoreName(), mockPublisherTransformationDTO);
        mockMvc.perform(put(ROOT_URL + '/' + mockPublisherTransformationDTO.getStoreName()).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(mockPublisherTransformationDTO))).andExpect(status().isBadRequest());
    }
}