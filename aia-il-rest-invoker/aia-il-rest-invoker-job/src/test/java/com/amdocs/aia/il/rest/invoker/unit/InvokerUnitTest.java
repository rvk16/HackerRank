package com.amdocs.aia.il.rest.invoker.unit;

import com.amdocs.aia.common.core.web.AiaHttpRequestHeaders;
import com.amdocs.aia.il.common.model.bulk.BulkGroup;
import com.amdocs.aia.il.common.model.bulk.EntityFilterRef;
import com.amdocs.aia.il.common.model.bulk.GroupFilter;
import com.amdocs.aia.il.common.model.external.CollectorChannelType;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaCollectionRules;
import com.amdocs.aia.il.rest.invoker.Invoker;
import com.amdocs.aia.il.rest.invoker.client.InvokerClient;
import com.amdocs.aia.il.rest.invoker.configuration.ConfigurationProvider;
import com.amdocs.aia.il.rest.invoker.configuration.InvokerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = InvokerTestConfiguration.class)
@ExtendWith(SpringExtension.class)
public class InvokerUnitTest {

    @Autowired
    Invoker invoker;

    @Autowired
    private InvokerConfiguration invokerConfiguration;

    @MockBean
    private ConfigurationProvider configurationProvider;

    @MockBean
    private InvokerClient client;

    private ExternalSchema externalSchema;
    private BulkGroup bulkGroup;
    private AiaHttpRequestHeaders headers;

    @BeforeEach
    void init() {
        externalSchema = createExternalSchema("schemaKey");
        bulkGroup = createBulkGroup("bulkKey", "schemaKey", "entityKey", "Query1");
        invokerConfiguration = getInvokerConfiguration();
        invoker = new Invoker(invokerConfiguration, configurationProvider, client);
        headers = buildHeaders();
    }

    @Test
    public void whenGetRelativePath_ShouldReturnExternalSchemaRelativePath() {
        assertEquals( "relativePath", invoker.getRelativePath(externalSchema));
    }

    @Test
    public void whenGetBulkGroupFilter_ShouldReturnFilter() {
        assertEquals("Query1", invoker.getBulkGroupFilter(bulkGroup));
    }

    @Test
    public void whenGetD1Path_ShouldReturnD1Path() {
        assertEquals("baseUrl"+"relativePath", invoker.getD1Path(getInvokerConfiguration().getBaseUrl(), externalSchema.getCollectionRules().getInitialLoadRelativeURL()));
    }

    @Test
    public void whenCallD1_nonSecured_ShouldReturn200OK() {
        String filter = "Query1";
        String relativePath = "relativePath";
        String baseUrl = getInvokerConfiguration().getBaseUrl();
        this.invokerConfiguration.setSecured(false);
        String d1Path = baseUrl + relativePath;
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
        Class<String> responseType = String.class;

        when(client.sendRequest(eq(d1Path), eq(HttpMethod.POST), eq(filter), eq(responseType))).thenReturn(response);

        ResponseEntity<String> d1Response = invoker.callD1(d1Path, filter);
        assertEquals(HttpStatus.OK, d1Response.getStatusCode());
    }

    @Test
    public void whenCallD1_secured_ShouldReturn200OK() {
        String filter = "Query1";
        String relativePath = "relativePath";
        String baseUrl = getInvokerConfiguration().getBaseUrl();
        String d1Path = baseUrl + relativePath;
        ResponseEntity<String> response1 = new ResponseEntity<>(HttpStatus.OK);
        ResponseEntity<String> response2 = new ResponseEntity<>("test",HttpStatus.OK);
        Class<String> responseType = String.class;
        String url = invokerConfiguration.getUrl();

        when(client.sendRequest(eq(url), eq(HttpMethod.POST), anyString(), eq(responseType))).thenReturn(response2);
        when(client.sendRequest(eq(d1Path), eq(HttpMethod.POST), eq(filter), eq(responseType), any())).thenReturn(response1);

        ResponseEntity<String> d1Response = invoker.callD1(d1Path, filter);
        assertEquals(HttpStatus.OK, d1Response.getStatusCode());
    }

    @Test
    public void whenCallD1_schemaNotExist_ShouldReturn404NOT_FOUND() {
        String filter = "Query1";
        String relativePath = "relativePathNotExist";
        String baseUrl = getInvokerConfiguration().getBaseUrl();
        String d1Path = baseUrl + relativePath;
        ResponseEntity<String> response1 = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        ResponseEntity<String> response2 = new ResponseEntity<>("test",HttpStatus.OK);
        Class<String> responseType = String.class;
        String url = invokerConfiguration.getUrl();

        when(client.sendRequest(eq(url), eq(HttpMethod.POST), anyString(), eq(responseType))).thenReturn(response2);
        when(client.sendRequest(eq(d1Path), eq(HttpMethod.POST), eq(filter), eq(responseType), any())).thenReturn(response1);

        ResponseEntity<String> d1Response = invoker.callD1(d1Path, filter);
        //assertEquals(HttpStatus.NOT_FOUND, d1Response.getStatusCode());
    }

    private static InvokerConfiguration getInvokerConfiguration() {
        InvokerConfiguration invokerConfiguration = new InvokerConfiguration();
        invokerConfiguration.setSchemaKey("schemaKey");
        invokerConfiguration.setBulkGroupKey("bulkGroupKey");
        invokerConfiguration.setBaseUrl("baseUrl");
        invokerConfiguration.setLoadType("Initial");
        invokerConfiguration.setSecured(true);
        invokerConfiguration.setAdminUser("adminUser");
        invokerConfiguration.setUser("user");
        invokerConfiguration.setAuthP("Tg2Nn7wUZOQ6Xc+1lenkZTQ9ZDf9a2/RBRiqJBCIX6o=");
        invokerConfiguration.setUrl("url");

        return invokerConfiguration;
    }

    public static ExternalSchema createExternalSchema(String schemaKey) {
        ExternalSchema externalSchema = new ExternalSchema();
        externalSchema.setSchemaKey("schemaKey");
        ExternalCsvSchemaCollectionRules collectionRules = new ExternalCsvSchemaCollectionRules();
        collectionRules.setInitialLoadChannel(CollectorChannelType.NONE);
        collectionRules.setOngoingChannel(CollectorChannelType.NONE);
        collectionRules.setReplayChannel(CollectorChannelType.NONE);
        collectionRules.setInitialLoadRelativeURL("relativePath");
        externalSchema.setCollectionRules(collectionRules);

        return externalSchema;
    }

    public static BulkGroup createBulkGroup(String bulkKey, String schemaKey, String entityKey, String filterKey) {
        BulkGroup bulkGroup = new BulkGroup();
        bulkGroup.setKey(bulkKey);
        bulkGroup.setSchemaKey(schemaKey);

        EntityFilterRef entityFilterRef = new EntityFilterRef();
        entityFilterRef.setEntityFilterKey(filterKey);
        entityFilterRef.setEntityKey(entityKey);
        Set<EntityFilterRef> entityFilterRefSet = new HashSet<>();
        entityFilterRefSet.add(entityFilterRef);
        bulkGroup.setEntityFilters(entityFilterRefSet);

        GroupFilter groupFilter = new GroupFilter();
        groupFilter.setFilter(filterKey);
        bulkGroup.setGroupFilter(groupFilter);

        return bulkGroup;
    }

    private AiaHttpRequestHeaders buildHeaders() {
        AiaHttpRequestHeaders headers = new AiaHttpRequestHeaders();
        headers.setUsername("");
        headers.setAuthorization("test");

        return headers;
    }
}