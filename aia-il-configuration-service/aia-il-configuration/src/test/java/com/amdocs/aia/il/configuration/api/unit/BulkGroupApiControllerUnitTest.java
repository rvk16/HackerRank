package com.amdocs.aia.il.configuration.api.unit;

import com.amdocs.aia.il.configuration.dto.BulkGroupDTO;
import com.amdocs.aia.il.configuration.service.physical.sql.BulkGroupService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BulkGroupApiControllerUnitTest.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class BulkGroupApiControllerUnitTest extends AbstractApiControllerUnitTest {
    private static final String PROJECT_KEY = "aia";
    private static final String ROOT_URL = "/aia/api/v1/integration-layer/projects/" + PROJECT_KEY + "/schemas";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BulkGroupService bulkGroupService;

    private BulkGroupDTO mockBulkGroupDTO;

    @BeforeEach
    public void setup() {
        mockBulkGroupDTO = getBulkGroupDTO("SOM");
    }

    private static BulkGroupDTO getBulkGroupDTO(String schemaStoreKey) {
        final BulkGroupDTO BulkGroupDTO = new BulkGroupDTO();
        BulkGroupDTO.schemaKey(schemaStoreKey).bulkGroupName("full").bulkGroupKey("full").entityFilters(null);
        return BulkGroupDTO;
    }

    @Test
    void canAddBulkGroup() throws Exception {
        when(bulkGroupService.save(eq(PROJECT_KEY), eq(mockBulkGroupDTO))).thenReturn(mockBulkGroupDTO);
        MvcResult result = mockMvc.perform(post(ROOT_URL + '/' + mockBulkGroupDTO.getSchemaKey() + "/bulk-groups").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockBulkGroupDTO)))
                .andExpect(status().isCreated()).andReturn();
        BulkGroupDTO resultDTO = new ObjectMapper().readValue(result.getResponse().getContentAsString(), BulkGroupDTO.class);
        assertEquals(mockBulkGroupDTO, resultDTO);
    }

    @Test
    void canGetBulkGroup() throws Exception {
        doReturn(mockBulkGroupDTO).when(bulkGroupService).get(PROJECT_KEY, mockBulkGroupDTO.getSchemaKey(), mockBulkGroupDTO.getBulkGroupKey());
        MvcResult result = mockMvc.perform(get(ROOT_URL + '/' + mockBulkGroupDTO.getSchemaKey() + "/bulk-groups/" + mockBulkGroupDTO.getBulkGroupKey()))
                .andExpect(status().isOk()).andReturn();
        BulkGroupDTO resultViewDTO = new ObjectMapper().readValue(result.getResponse().getContentAsString(), BulkGroupDTO.class);
        assertEquals(mockBulkGroupDTO, resultViewDTO);
    }

    @Test
    void canListBulkGroups() throws Exception {
        List<BulkGroupDTO> bulkGroupDTODTOList = new ArrayList<>(Collections.singletonList(mockBulkGroupDTO));
        when(bulkGroupService.list(Mockito.anyString(), Mockito.anyString())).thenReturn(bulkGroupDTODTOList);
        mockMvc.perform(get(ROOT_URL + '/' + mockBulkGroupDTO.getSchemaKey() + "/bulk-groups")).andExpect(status().isOk()).andReturn();
    }
}