package com.amdocs.aia.il.common.model.configuration.properties;

import com.datastax.oss.driver.api.core.retry.RetryDecision;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * Database configuration properties.
 *
 * @author SWARNIMJ
 */
@ConfigurationProperties(prefix = "aia.il.db")
public class DatabaseConfigurationProperties implements Serializable {
    private static final long serialVersionUID = -9192116406796229093L;

    private String url;
    private String user;
    private String password;
    private String connStrings;
    private int port;
    private String localDatacenter;
    private String keyspace;
    private int maxLocalConnPerHost = 15;
    private int maxRemoteConnPerHost = 15;
    private int coreLocalConnPerHost = 15;
    private int coreRemoteConnPerHost = 15;
    private int maxLocalRequestPerConnection = 15;
    private int maxRemoteRequestPerConnection = 15;
    private Long baseDelayMs = 100l;
    private Long maxDelayMs = 100l;
    private int poolTimeoutMillis = 5000;
    private int heartbeatIntervalSeconds = 5;
    private int readTimeOut = 12000;
    private int connectionTimeOut = 5000;
    private String loadBalancingPolicy = "TOKENAWARE";
    private String consistencyLevel = "QUORUM";
    private int numOfRetry = 5;
    private RetryConfig retryConfig = new RetryConfig();
    private boolean isSSLEnabled;
    private int timeToLive;
    private int pageSize = 5000;
    private String nodeMetrics = "pool.open-connections,pool.in-flight";
    private String sessionMetrics = "connected-nodes,cql-requests";

    public String getNodeMetrics() {
        return nodeMetrics;
    }

    public void setNodeMetrics(String nodeMetrics) {
        this.nodeMetrics = nodeMetrics;
    }

    public String getSessionMetrics() {
        return sessionMetrics;
    }

    public void setSessionMetrics(String sessionMetrics) {
        this.sessionMetrics = sessionMetrics;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getUrl() {
        return url;
    }

    public boolean isSSLEnabled() {
        return isSSLEnabled;
    }

    public void setSSLEnabled(boolean isSSLEnabled) {
        this.isSSLEnabled = isSSLEnabled;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getConnStrings() {
        return connStrings;
    }

    public void setConnStrings(String connStrings) {
        this.connStrings = connStrings;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLocalDatacenter() {
        return localDatacenter;
    }

    public void setLocalDatacenter(final String localDatacenter) {
        this.localDatacenter = localDatacenter;
    }

    public int getMaxLocalConnPerHost() {
        return maxLocalConnPerHost;
    }

    public void setMaxLocalConnPerHost(int maxLocalConnPerHost) {
        this.maxLocalConnPerHost = maxLocalConnPerHost;
    }

    public int getMaxRemoteConnPerHost() {
        return maxRemoteConnPerHost;
    }

    public void setMaxRemoteConnPerHost(int maxRemoteConnPerHost) {
        this.maxRemoteConnPerHost = maxRemoteConnPerHost;
    }

    public int getCoreLocalConnPerHost() {
        return coreLocalConnPerHost;
    }

    public void setCoreLocalConnPerHost(int coreLocalConnPerHost) {
        this.coreLocalConnPerHost = coreLocalConnPerHost;
    }

    public int getCoreRemoteConnPerHost() {
        return coreRemoteConnPerHost;
    }

    public void setCoreRemoteConnPerHost(int coreRemoteConnPerHost) {
        this.coreRemoteConnPerHost = coreRemoteConnPerHost;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public int getMaxLocalRequestPerConnection() {
        return maxLocalRequestPerConnection;
    }

    public void setMaxLocalRequestPerConnection(int maxLocalRequestPerConnection) {
        this.maxLocalRequestPerConnection = maxLocalRequestPerConnection;
    }

    public int getMaxRemoteRequestPerConnection() {
        return maxRemoteRequestPerConnection;
    }

    public void setMaxRemoteRequestPerConnection(int maxRemoteRequestPerConnection) {
        this.maxRemoteRequestPerConnection = maxRemoteRequestPerConnection;
    }

    public int getPoolTimeoutMillis() {
        return poolTimeoutMillis;
    }

    public void setPoolTimeoutMillis(int poolTimeoutMillis) {
        this.poolTimeoutMillis = poolTimeoutMillis;
    }

    public int getHeartbeatIntervalSeconds() {
        return heartbeatIntervalSeconds;
    }

    public void setHeartbeatIntervalSeconds(int heartbeatIntervalSeconds) {
        this.heartbeatIntervalSeconds = heartbeatIntervalSeconds;
    }

    public long getBaseDelayMs() {
        return baseDelayMs;
    }

    public void setBaseDelayMs(long baseDelayMs) {
        this.baseDelayMs = baseDelayMs;
    }

    public long getMaxDelayMs() {
        return maxDelayMs;
    }

    public void setMaxDelayMs(long maxDelayMs) {
        this.maxDelayMs = maxDelayMs;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public String getLoadBalancingPolicy() {
        return loadBalancingPolicy;
    }

    public void setLoadBalancingPolicy(String loadBalancingPolicy) {
        this.loadBalancingPolicy = loadBalancingPolicy;
    }

    public String getConsistencyLevel() {
        return consistencyLevel;
    }

    public void setConsistencyLevel(String consistencyLevel) {
        this.consistencyLevel = consistencyLevel;
    }

    public int getNumOfRetry() {
        return numOfRetry;
    }

    public void setNumOfRetry(int numOfRetry) {
        this.numOfRetry = numOfRetry;
    }

    public RetryConfig getRetryConfig() {
        return retryConfig;
    }

    public void setRetryConfig(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public int getTimeToLive() {
        return this.timeToLive;
    }

    public static class RetryConfig implements Serializable {
        public static final String DEFAULT = "DEFAULT";

        String onReadTimeOut = DEFAULT;
        String onWriteTimeOut = DEFAULT;
        String onUnavailable = DEFAULT;
        String onOperationTimeout = DEFAULT;

        public RetryDecision getOnReadTimeOut() {
            return onReadTimeOut.equals(DEFAULT) ? RetryDecision.RETRY_SAME : RetryDecision.valueOf(onReadTimeOut);
        }

        public void setOnReadTimeOut(String onReadTimeOut) {
            this.onReadTimeOut = onReadTimeOut;
        }

        public RetryDecision getOnWriteTimeOut() {
            return onWriteTimeOut.equals(DEFAULT) ? RetryDecision.RETRY_SAME : RetryDecision.valueOf(onWriteTimeOut);
        }

        public void setOnWriteTimeOut(String onWriteTimeOut) {
            this.onWriteTimeOut = onWriteTimeOut;
        }

        public RetryDecision getOnUnavailable() {
            return onUnavailable.equals(DEFAULT) ? RetryDecision.RETRY_NEXT : RetryDecision.valueOf(onUnavailable);
        }

        public void setOnUnavailable(String onUnavailable) {
            this.onUnavailable = onUnavailable;
        }

        public RetryDecision getOnOperationTimeout() {
            return onOperationTimeout.equals(DEFAULT) ? RetryDecision.RETRY_NEXT : RetryDecision.valueOf(onOperationTimeout);
        }

        public void setOnOperationTimeout(String onOperationTimeout) {
            this.onOperationTimeout = onOperationTimeout;
        }

        @Override
        public String toString() {
            return "RetryConfig{" +
                    "onReadTimeOut='" + onReadTimeOut + '\'' +
                    ", onWriteTimeOut='" + onWriteTimeOut + '\'' +
                    ", onUnavailable='" + onUnavailable + '\'' +
                    ", onOperationTimeout='" + onOperationTimeout + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DatabaseConfigurationProperties{" +
                "url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + "******" + '\'' +
                ", connStrings='" + connStrings + '\'' +
                ", port=" + port +
                ", keyspace='" + keyspace + '\'' +
                ", maxLocalConnPerHost=" + maxLocalConnPerHost +
                ", maxRemoteConnPerHost=" + maxRemoteConnPerHost +
                ", coreLocalConnPerHost=" + coreLocalConnPerHost +
                ", coreRemoteConnPerHost=" + coreRemoteConnPerHost +
                ", maxLocalRequestPerConnection=" + maxLocalRequestPerConnection +
                ", maxRemoteRequestPerConnection=" + maxRemoteRequestPerConnection +
                ", baseDelayMs=" + baseDelayMs +
                ", maxDelayMs=" + maxDelayMs +
                ", poolTimeoutMillis=" + poolTimeoutMillis +
                ", heartbeatIntervalSeconds=" + heartbeatIntervalSeconds +
                ", readTimeOut=" + readTimeOut +
                ", connectionTimeOut=" + connectionTimeOut +
                ", loadBalancingPolicy='" + loadBalancingPolicy + '\'' +
                ", consistencyLevel='" + consistencyLevel + '\'' +
                ", numOfRetry=" + numOfRetry +
                ", retryConfig=" + retryConfig +
                ", isSSLEnabled=" + isSSLEnabled +
                '}';
    }
}