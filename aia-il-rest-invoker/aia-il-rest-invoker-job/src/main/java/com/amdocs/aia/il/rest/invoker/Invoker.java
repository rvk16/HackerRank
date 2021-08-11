package com.amdocs.aia.il.rest.invoker;

import com.amdocs.aia.common.core.web.AiaHttpRequestHeaders;
import com.amdocs.aia.il.common.model.bulk.BulkGroup;
import com.amdocs.aia.il.common.model.bulk.GroupFilter;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.rest.invoker.client.InvokerClient;
import com.amdocs.aia.il.rest.invoker.configuration.ConfigurationProvider;
import com.amdocs.aia.il.rest.invoker.configuration.InvokerConfiguration;
import com.amdocs.aia.il.rest.invoker.configuration.InvokerResponse;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@EnableConfigurationProperties(InvokerConfiguration.class)
@Component
public class Invoker implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Invoker.class);
    public static final String INITIAL = "Initial";

    private final InvokerConfiguration invokerConfiguration;
    private final ConfigurationProvider configurationProvider;
    private final InvokerClient client;
    KubernetesClient kubernetesClient;
    private boolean processedWithError = false;

    @Autowired
    public Invoker(InvokerConfiguration invokerConfiguration,
                   ConfigurationProvider configurationProvider,
                   InvokerClient client) {
        this.invokerConfiguration = invokerConfiguration;
        this.configurationProvider = configurationProvider;
        this.client = client;
    }

    public InvokerResponse run(){
        InvokerResponse invokerResponse = new InvokerResponse();
        try {
            final String schemaKey = invokerConfiguration.getSchemaKey();
            final String bulkGroupKey = invokerConfiguration.getBulkGroupKey();
            final String baseUrl = invokerConfiguration.getBaseUrl();

            //Get external schema from repo
            Map<String, ExternalSchema> externalSchemas = configurationProvider.getExternalSchemas();
            if (externalSchemas == null) {
                invokerResponse.setSuccess(false);
                invokerResponse.setMsg("External schema not exist");
                return invokerResponse;
            }
            ExternalSchema externalSchema = externalSchemas.get(schemaKey);
            String relativePath = getRelativePath(externalSchema);

            //Get bulk group from repo
            Map<String, BulkGroup> bulkGroups = configurationProvider.getBulkGroups();
            if (bulkGroups == null) {
                invokerResponse.setSuccess(false);
                invokerResponse.setMsg("Bulk Group not exist");
                return invokerResponse;
            }
            BulkGroup bulkGroup = bulkGroups.get(bulkGroupKey);

            String groupFilter = getBulkGroupFilter(bulkGroup);
            String d1Path = getD1Path(baseUrl, relativePath);
            ResponseEntity<String> responseEntity = callD1(d1Path, groupFilter);

            if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                invokerResponse.setSuccess(true);
                invokerResponse.setMsg("Invoker job ended successfully");
            } else {
                invokerResponse.setSuccess(false);
                invokerResponse.setMsg("Invoker job failed");
            }

            LOGGER.info("status:" + responseEntity.getStatusCode() + " , body: " + responseEntity.getBody());
        }
        catch (final Exception e){
            this.processedWithError = true;
            System.err.println(e.getMessage()); // NOSONAR
            LOGGER.error("Invoker job failed", e);
            invokerResponse.setSuccess(false);
            invokerResponse.setMsg("Exception");
        }

        return invokerResponse;
    }

    public String getD1Path(String baseUrl, String relativePath) {
        return baseUrl + relativePath;
    }

    public String getBulkGroupFilter(BulkGroup bulkGroup) {
        String filter = "";

        if (bulkGroup != null) {
            GroupFilter groupFilter = bulkGroup.getGroupFilter();
            filter = groupFilter.getFilter();
        }
        return filter;
    }

    public String getRelativePath(ExternalSchema externalSchema) {
        String relativePath = "";

        if (externalSchema != null) {
                    relativePath = INITIAL.equals(invokerConfiguration.getLoadType()) ? externalSchema.getCollectionRules().getInitialLoadRelativeURL() :
                    externalSchema.getCollectionRules().getPartialLoadRelativeURL();
        }
        return relativePath;
    }

    public ResponseEntity<String> callD1(String d1Path, String groupFilter) {
           if(invokerConfiguration.isSecured()) {
               AiaHttpRequestHeaders headers = buildHeaders();
               //return this.client.sendRequest(d1Path, HttpMethod.POST, groupFilter, String.class, headers);
               //TODO: fix the call once integration with D1 will be tasted.
               return new ResponseEntity<String>(HttpStatus.OK);
           }
           else{
               //return this.client.sendRequest(d1Path, HttpMethod.POST, groupFilter, String.class);
               //TODO: fix the call once integration with D1 will be tasted.
               return new ResponseEntity<String>(HttpStatus.OK);
           }
    }

    private AiaHttpRequestHeaders buildHeaders() {
        AiaHttpRequestHeaders headers = new AiaHttpRequestHeaders();
        headers.setUsername("");
        ResponseEntity<String> response = retrieveToken();
        headers.setAuthorization(response.getBody());

        return headers;
    }

    public ResponseEntity<String> retrieveToken() {
        final String adminUser = invokerConfiguration.getAdminUser();
        final String user = invokerConfiguration.getUser();
        final String authP = decode(invokerConfiguration.getAuthP());
        final String url = invokerConfiguration.getUrl();
        final String data = "curl -k -H \"Content-Type: application/json\" -u 'a3sDeployer:'"+adminUser+"'!' -d '{\"auth\":{\"dialects\":[\"A3S_Core_Public\"],\"on-behalf-of\":{\"type\":\"USER_PASSWORD\",\"user-credentials\":{\"username\":\"'"+user+"'\",\"password\":\" '"+authP+"'\",\"properties\":{}}},\"token-expiration\":null,\"token-type\":\"JWT\",\"audience-restriction\":[],\"token-time-to-live\":null}}'";
        //return this.client.sendRequest(url, HttpMethod.POST, data, String.class);

        //TODO: fix the call once integration with D1 will be tasted.
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    private static String decode(final String value) {
        return StringUtils.hasText(value) ? new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8) : null;
    }

    public int getExitCode() {
        return processedWithError ? 1 : 0;
    }


    public void close() {
        if (this.kubernetesClient != null) {
            this.kubernetesClient.close();
        }
    }

}