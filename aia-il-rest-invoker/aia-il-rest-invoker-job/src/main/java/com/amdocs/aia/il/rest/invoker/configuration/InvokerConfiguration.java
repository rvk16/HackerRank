package com.amdocs.aia.il.rest.invoker.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Invoker configuration properties.
 */
@ConfigurationProperties(prefix = "invoker")
public class InvokerConfiguration {
    private String schemaKey;
    private String bulkGroupKey;
    private String baseUrl;
    private String loadType;
    private boolean secured;
    private String adminUser;
    private String user;
    private String authP;
    private String url;
    private String repoElementsLocalPath;

    public String getSchemaKey() {
        return schemaKey;
    }

    public void setSchemaKey(final String schemaKey) {
        this.schemaKey = schemaKey;
    }

    public String getBulkGroupKey() {
        return bulkGroupKey;
    }

    public void setBulkGroupKey(final String bulkGroupKey) {
        this.bulkGroupKey = bulkGroupKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public boolean isSecured() {
        return secured;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAuthP() {
        return authP;
    }

    public void setAuthP(String authP) {
        this.authP = authP;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRepoElementsLocalPath() {
        return repoElementsLocalPath;
    }

    public void setRepoElementsLocalPath(final String repoElementsLocalPath) {
        this.repoElementsLocalPath = repoElementsLocalPath;
    }
}