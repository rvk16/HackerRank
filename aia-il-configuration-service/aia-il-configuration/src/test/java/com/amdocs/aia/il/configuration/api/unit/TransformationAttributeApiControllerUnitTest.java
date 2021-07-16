package com.amdocs.aia.il.configuration.api.unit;

import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.il.configuration.api.TransformationAttributeApiController;
import com.amdocs.aia.il.configuration.dto.TransformationAttributeDTO;
import com.amdocs.aia.il.configuration.service.TransformationAttributeConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = TransformationAttributeApiController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class TransformationAttributeApiControllerUnitTest extends AbstractApiControllerUnitTest {
    private static final String PROJECT_KEY = "aia";
    private static final String SCHEMA_KEY = "schemaKey";
    private static final String ENTITY_KEY = "entityKey";
    private static final String ROOT_URL = "/aia/api/v1/integration-layer/projects/" + PROJECT_KEY + "/configuration/transformation-attributes";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransformationAttributeConfigurationService<TransformationAttributeDTO> transformationAttributeConfigurationService;

    private TransformationAttributeDTO transformationAttributeDTO;

    @MockBean
    private  DataChannelStoreType dataChannelStoreType;

    @BeforeEach
    public void setup() {
        setTransformationAttributeDTO();
    }

    private void setTransformationAttributeDTO() {
        transformationAttributeDTO = new TransformationAttributeDTO()
                .attributeKey("mainContactKey")
                .attributeName("Main Contact Key")
                .description("description test")
                .type("STRING")
                .origin("origin test")
                .sourceMapping("sourceMapping test")
                .designSource("designSource test")
                .keyPosition(false)
                .sortOrder(1000)
                .isLogicalTime(false)
                .isUpdateTime(false)
                .isRequired(false)
                .doReferencialIntegrity(true)
                .parentSchemaKey("aLDMCustomer")
                .parentEntityKey("Contact")
                .parentAttributeKey("contactKey");
    }

    @Test
    void getTransformationAttributeConfigurationShouldReturn200OK() throws Exception {
        final List<TransformationAttributeDTO> transformationAttributeDTOS = new ArrayList<>();
        transformationAttributeDTOS.add(transformationAttributeDTO);
        when(transformationAttributeConfigurationService.list(eq(PROJECT_KEY), eq(SCHEMA_KEY), eq(ENTITY_KEY))).thenReturn(transformationAttributeDTOS);
        String url = ROOT_URL + '/' + SCHEMA_KEY + "/entity-stores/" + ENTITY_KEY;
        mockMvc.perform(MockMvcRequestBuilders.get(url).contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].attributeKey").value(transformationAttributeDTO.getAttributeKey()))
                .andExpect(jsonPath("$[0].attributeName").value(transformationAttributeDTO.getAttributeName()))
                .andExpect(jsonPath("$[0].description").value(transformationAttributeDTO.getDescription()))
                .andExpect(jsonPath("$[0].type").value(transformationAttributeDTO.getType()))
                .andExpect(jsonPath("$[0].origin").value(transformationAttributeDTO.getOrigin()))
                .andExpect(jsonPath("$[0].sourceMapping").value(transformationAttributeDTO.getSourceMapping()))
                .andExpect(jsonPath("$[0].designSource").value(transformationAttributeDTO.getDesignSource()))
                .andExpect(jsonPath("$[0].keyPosition").value(transformationAttributeDTO.isKeyPosition()))
                .andExpect(jsonPath("$[0].sortOrder").value(transformationAttributeDTO.getSortOrder()))
                .andExpect(jsonPath("$[0].isLogicalTime").value(transformationAttributeDTO.isIsLogicalTime()))
                .andExpect(jsonPath("$[0].isUpdateTime").value(transformationAttributeDTO.isIsUpdateTime()))
                .andExpect(jsonPath("$[0].isRequired").value(transformationAttributeDTO.isIsRequired()))
                .andExpect(jsonPath("$[0].doReferencialIntegrity").value(transformationAttributeDTO.isDoReferencialIntegrity()))
                .andExpect(jsonPath("$[0].parentSchemaKey").value(transformationAttributeDTO.getParentSchemaKey()))
                .andExpect(jsonPath("$[0].parentEntityKey").value(transformationAttributeDTO.getParentEntityKey()))
                .andExpect(jsonPath("$[0].parentAttributeKey").value(transformationAttributeDTO.getParentAttributeKey()));
    }

    @Test
    void getTransformationAvailableAttributeConfigurationShouldReturn200OK() throws Exception {
        final List<TransformationAttributeDTO> transformationAttributeDTOS = new ArrayList<>();
        transformationAttributeDTOS.add(transformationAttributeDTO);
        when(transformationAttributeConfigurationService.getAvailableAttributes(eq(PROJECT_KEY), eq(SCHEMA_KEY), eq(ENTITY_KEY))).thenReturn(transformationAttributeDTOS);
        String url = ROOT_URL + '/' + SCHEMA_KEY + '/' + ENTITY_KEY;
        mockMvc.perform(MockMvcRequestBuilders.get(url).contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].attributeKey").value(transformationAttributeDTO.getAttributeKey()))
                .andExpect(jsonPath("$[0].attributeName").value(transformationAttributeDTO.getAttributeName()))
                .andExpect(jsonPath("$[0].description").value(transformationAttributeDTO.getDescription()))
                .andExpect(jsonPath("$[0].type").value(transformationAttributeDTO.getType()))
                .andExpect(jsonPath("$[0].origin").value(transformationAttributeDTO.getOrigin()))
                .andExpect(jsonPath("$[0].sourceMapping").value(transformationAttributeDTO.getSourceMapping()))
                .andExpect(jsonPath("$[0].designSource").value(transformationAttributeDTO.getDesignSource()))
                .andExpect(jsonPath("$[0].keyPosition").value(transformationAttributeDTO.isKeyPosition()))
                .andExpect(jsonPath("$[0].sortOrder").value(transformationAttributeDTO.getSortOrder()))
                .andExpect(jsonPath("$[0].isLogicalTime").value(transformationAttributeDTO.isIsLogicalTime()))
                .andExpect(jsonPath("$[0].isUpdateTime").value(transformationAttributeDTO.isIsUpdateTime()))
                .andExpect(jsonPath("$[0].isRequired").value(transformationAttributeDTO.isIsRequired()))
                .andExpect(jsonPath("$[0].doReferencialIntegrity").value(transformationAttributeDTO.isDoReferencialIntegrity()))
                .andExpect(jsonPath("$[0].parentSchemaKey").value(transformationAttributeDTO.getParentSchemaKey()))
                .andExpect(jsonPath("$[0].parentEntityKey").value(transformationAttributeDTO.getParentEntityKey()))
                .andExpect(jsonPath("$[0].parentAttributeKey").value(transformationAttributeDTO.getParentAttributeKey()));
    }
}