package com.amdocs.aia.il.deployer.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "aia.il.deployer.db")
@Validated
public class DatabaseProperties {
    @NotNull
    private String connStrings;
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private Integer nodePort;
    @NotNull
    private String localDatacenter;
    @NotNull
    private Integer maxConnPerHost;
    @NotNull
    private String keyspace;
    @NotNull
    private String replicationFactor;
    @NotNull
    private boolean isSSLEnabled;

    public boolean isSSLEnabled() {
        return isSSLEnabled;
    }

    public void setSSLEnabled(boolean isSSLEnabled) {
        this.isSSLEnabled = isSSLEnabled;
    }

    public String getConnStrings() {
        return connStrings;
    }

    public void setConnStrings(String connStrings) {
        this.connStrings = connStrings;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getNodePort() {
        return nodePort;
    }

    public void setNodePort(Integer nodePort) {
        this.nodePort = nodePort;
    }

    public String getLocalDatacenter() {
        return localDatacenter;
    }

    public void setLocalDatacenter(final String localDatacenter) {
        this.localDatacenter = localDatacenter;
    }

    public Integer getMaxConnPerHost() {
        return maxConnPerHost;
    }

    public void setMaxConnPerHost(Integer maxConnPerHost) {
        this.maxConnPerHost = maxConnPerHost;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public String getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(String replicationFactor) {
        this.replicationFactor = replicationFactor;
    }
}