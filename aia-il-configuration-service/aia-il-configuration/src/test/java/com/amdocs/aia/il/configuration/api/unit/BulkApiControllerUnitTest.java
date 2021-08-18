package com.amdocs.aia.il.configuration.api.unit;


import com.amdocs.aia.il.configuration.repository.external.ExternalEntityRepository;
import com.amdocs.aia.il.configuration.repository.external.ExternalSchemaRepository;
import com.amdocs.aia.il.configuration.service.external.BulkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BulkApiControllerUnitTest.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class BulkApiControllerUnitTest extends AbstractApiControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean

    private BulkService bulkService;
    @MockBean
    protected ExternalEntityRepository externalEntityRepository;

    @MockBean
    protected ExternalSchemaRepository externalSchemaRepository;

    @Mock
    private InputStreamResource inputStreamResource;

    @Test
    void exportExternalSchemasToCSV_ShouldReturn200OK_and_file() throws Exception {
        when(this.bulkService.exportExternalSchemasToZIP("projectKey")).thenReturn(inputStreamResource);
        mockMvc.perform(get("/aia/api/v1/integration-layer/projects/aia/configuration/external-schemas/export")
                .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk()).andReturn();
    }

//    @Test
//    void importDatastoreTablesFromCSV_ShouldReturn200OK() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        BulkImportResponseDTO mockBulkImportResponseDTO = new BulkImportResponseDTO();
//        mockBulkImportResponseDTO.setTotalTableCount(10);
//        mockBulkImportResponseDTO.setModifiedTableCount(2);
//        mockBulkImportResponseDTO.setUnmodifiedTableCount(4);
//        mockBulkImportResponseDTO.setImportedTableCount(6);
//
//        MockMultipartFile importFile = new MockMultipartFile("file", "importFile.csv", null, "data data".getBytes());
//
//        when(this.bulkService.importDatastoreTablesFromZIP("projectKey", importFile)).thenReturn(mockBulkImportResponseDTO);
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/aia/api/v1/datastore/projects/aia/configuration/tables/import")
//                .file(importFile).content(objectMapper.writeValueAsString(mockBulkImportResponseDTO)))
//                .andExpect(status().isOk()).andReturn();
//    }
}