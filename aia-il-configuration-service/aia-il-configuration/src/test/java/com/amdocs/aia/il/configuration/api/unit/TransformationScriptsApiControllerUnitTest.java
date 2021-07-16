package com.amdocs.aia.il.configuration.api.unit;

import com.amdocs.aia.il.configuration.service.TransformationsScriptsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = TransformationScriptsApiControllerUnitTest.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class TransformationScriptsApiControllerUnitTest extends AbstractApiControllerUnitTest {
    private static final String PROJECT_KEY = "aia";
    private static final String BASE_PATH = "/aia/api/v1/integration-layer/projects/" + PROJECT_KEY + "/configuration/export-transformations-scripts";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransformationsScriptsService service;

    @Test
    void validRequestShouldReturnZipFile() throws Exception {
        byte[] bytes = new byte[0];
        doReturn(bytes).when(service).exportTransformationsScripts(PROJECT_KEY);
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk()).andReturn();
    }
}