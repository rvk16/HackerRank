package com.amdocs.aia.il.sqlite.api.unit;

import com.amdocs.aia.il.sqlite.api.SqLiteApiController;
import com.amdocs.aia.il.sqlite.dto.ResultSetDTO;
import com.amdocs.aia.il.sqlite.service.SqliteRestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SqLiteApiController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class SqLiteApiControllerUnitTest extends AbstractApiControllerUnitTest {
    private static final String ROOT_URL = "/aia/api/v1/sqlite/projects/1/";

    @MockBean
    private SqliteRestService sqliteRestService;

    @Autowired
    private MockMvc mockMvc;

    private ResultSetDTO mockDto;

    @BeforeEach
    void setup() {
        mockDto = new ResultSetDTO();
        mockDto.setResultSet("resultSet");
    }

    @Test
    void whenExecuteQueryShouldReturn200OK() throws Exception {
        String query = "select * from contacts";
        when(this.sqliteRestService.executeQuery("1", query)).thenReturn(mockDto);
        mockMvc.perform(post(ROOT_URL + "query").contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\" : \"query\"}"))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    void whenExecuteUpdateShouldReturn200OK() throws Exception {
        doNothing().when(sqliteRestService).executeUpdate("1", "query");
        mockMvc.perform(post(ROOT_URL + "execute").contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\" : \"query\"}"))
                .andExpect(status().is2xxSuccessful()).andReturn();
    }
}
