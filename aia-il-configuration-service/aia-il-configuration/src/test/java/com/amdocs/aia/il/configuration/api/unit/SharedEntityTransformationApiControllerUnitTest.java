package com.amdocs.aia.il.configuration.api.unit;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.configuration.api.SharedEntityTransformationApiController;
import com.amdocs.aia.il.configuration.dto.EntityTransformationDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.service.SharedEntityTransformationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SharedEntityTransformationApiController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class SharedEntityTransformationApiControllerUnitTest extends AbstractApiControllerUnitTest {
    private static final String BASE_PATH = "/aia/api/v1/integration-layer/projects/aia/configuration/shared-entity-transformation";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SharedEntityTransformationService service;

    private EntityTransformationDTO dto;

    @Test
    void T01_POST_invalidDtoShouldReturn500() throws Exception {
        dto = new EntityTransformationDTO();
        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void T02_POST_validDtoShouldReturnDto() throws Exception {
        dto = instantiateEntityTransformation();
        doReturn(dto).when(service).create(anyString(), eq(dto));
        MvcResult response = mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
        EntityTransformationDTO returnedDto = new ObjectMapper().readValue(response.getResponse().getContentAsString(), EntityTransformationDTO.class);
        assertEquals(dto, returnedDto);
    }

    @Test
    void T03_POST_serviceExceptionShouldPropagate() throws Exception {
        dto = instantiateEntityTransformation();
        final String exceptionParam = "EXCEPTION_PARAM";
        doThrow(new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.UNEXPECTED_OPERATION_ERROR, exceptionParam)).when(service).create(anyString(), eq(dto));
        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(exceptionParam)));
    }

    private static EntityTransformationDTO instantiateEntityTransformation() {
        return new EntityTransformationDTO()
                .logicalSchemaKey("SCS")
                .logicalEntityKey("ENS")
                .attributes(Collections.emptyList())
                .transformations(Collections.emptyList())
                .contexts(Collections.emptyList());
    }
}