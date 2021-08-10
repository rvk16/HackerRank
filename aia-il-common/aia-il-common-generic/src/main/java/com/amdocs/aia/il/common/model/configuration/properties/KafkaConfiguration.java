package com.amdocs.aia.il.common.model.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * Kafka properties which are loaded from the yaml.
 *
 * @author SWARNIMJ
 */
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfiguration implements Serializable{

    private static final long serialVersionUID = 8099549146455798026L;
    private String brokers = "";
    private Consumer consumer;
    private Producer producer;
    private String securityProtocol;
    private String saslMechanism;
    private Ssl ssl;

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(final String brokers) {
        this.brokers = brokers;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(final Consumer consumer) {
        this.consumer = consumer;
    }

    public String getSecurityProtocol() { return securityProtocol; }

    public void setSecurityProtocol(final String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    public String getSaslMechanism() {
        return saslMechanism;
    }

    public void setSaslMechanism(final String saslMechanism) {
        this.saslMechanism = saslMechanism;
    }

    public Ssl getSsl() {
        return ssl;
    }

    public void setSsl(Ssl ssl) {
        this.ssl = ssl;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    @Override
    public String toString() {
        return "KafkaConfiguration{" +
                "brokers='" + brokers + '\'' +
                ", consumer=" + consumer +
                ", producer=" + producer +
                ", securityProtocol='" + securityProtocol + '\'' +
                ", saslMechanism='" + saslMechanism + '\'' +
                ", ssl=" + ssl +
                '}';
    }

    public static class Consumer implements Serializable {
        private static final long serialVersionUID = 163112238018031487L;

        private int maxPollRecords;
        private int fetchMaxBytes;
        private int sessionTimeOut = 30000;
        private int receiveBufferConfig = 16777216;
        private int maxPollIntervalMilliSec=1;
        private int maxFetchPartitionBytes;
        private String startingOffsets;
        private long maxOffsetsPerTrigger;
        private String partitionAssignedStrategy = "com.amdocs.aia.il.common.publisher.LagBasedPartitionAssignor";
        private boolean periodicRebalancing;
        private long periodicRebalancingIntervalInSec;

        public boolean isPeriodicRebalancing() {
            return periodicRebalancing;
        }

        public void setPeriodicRebalancing(boolean periodicRebalancing) {
            this.periodicRebalancing = periodicRebalancing;
        }

        public long getPeriodicRebalancingIntervalInSec() {
            return periodicRebalancingIntervalInSec;
        }

        public void setPeriodicRebalancingIntervalInSec(long periodicRebalancingIntervalInSec) {
            this.periodicRebalancingIntervalInSec = periodicRebalancingIntervalInSec;
        }

        public int getMaxPollRecords() {
            return maxPollRecords;
        }

        public void setMaxPollRecords(final int maxPollRecords) {
            this.maxPollRecords = maxPollRecords;
        }

        public int getFetchMaxBytes() {
            return fetchMaxBytes;
        }

        public void setFetchMaxBytes(final int fetchMaxBytes) {
            this.fetchMaxBytes = fetchMaxBytes;
        }

        public int getSessionTimeOut () { return sessionTimeOut; }

        public void setSessionTimeOut (int sessionTimeOut) { this.sessionTimeOut=sessionTimeOut; }

        public int getReceiveBufferConfig () { return receiveBufferConfig; }

        public void setReceiveBufferConfig(int receiveBufferConfig) {
            this.receiveBufferConfig = receiveBufferConfig;
        }

        public int getMaxPollIntervalMilliSec() {
            return maxPollIntervalMilliSec;
        }

        public void setMaxPollIntervalMilliSec(int maxPollIntervalMilliSec) {
            this.maxPollIntervalMilliSec = maxPollIntervalMilliSec;
        }

        public int getMaxFetchPartitionBytes() {
            return maxFetchPartitionBytes;
        }

        public void setMaxFetchPartitionBytes(int maxFetchPartitionBytes) {
            this.maxFetchPartitionBytes = maxFetchPartitionBytes;
        }

        public String getStartingOffsets() {
            return startingOffsets;
        }

        public void setStartingOffsets(String startingOffsets) {
            this.startingOffsets = startingOffsets;
        }

        public long getMaxOffsetsPerTrigger() {
            return maxOffsetsPerTrigger;
        }

        public void setMaxOffsetsPerTrigger(long maxOffsetsPerTrigger) {
            this.maxOffsetsPerTrigger = maxOffsetsPerTrigger;
        }

        public String getPartitionAssignedStrategy() {
            return partitionAssignedStrategy;
        }

        public void setPartitionAssignedStrategy(String partitionAssignedStrategy) {
            this.partitionAssignedStrategy = partitionAssignedStrategy;
        }

        @Override
        public String toString() {
            return "Consumer{" +
                    "maxPollRecords=" + maxPollRecords +
                    ", fetchMaxBytes=" + fetchMaxBytes +
                    ", sessionTimeOut=" + sessionTimeOut +
                    ", receiveBufferConfig=" + receiveBufferConfig +
                    ", maxPollIntervalMilliSec=" + maxPollIntervalMilliSec +
                    ", maxFetchPartitionBytes=" + maxFetchPartitionBytes +
                    ", startingOffsets='" + startingOffsets + '\'' +
                    ", maxOffsetsPerTrigger=" + maxOffsetsPerTrigger +
                    ", partitionAssignedStrategy='" + partitionAssignedStrategy + '\'' +
                    ", periodicRebalancing=" + periodicRebalancing +
                    ", periodicRebalancingIntervalInSec=" + periodicRebalancingIntervalInSec +
                    '}';
        }
    }

    public static class Ssl implements Serializable {
        private static final long serialVersionUID = -7386582638814036538L;

        private String truststoreLocation;
        private String truststorePassword;
        private String keystoreLocation;
        private String keystorePassword;
        private String keyPassword;

        public String getTruststoreLocation() {
            return truststoreLocation;
        }

        public void setTruststoreLocation(final String truststoreLocation) { this.truststoreLocation = truststoreLocation; }

        public String getTruststorePassword() {
            return truststorePassword;
        }

        public void setTruststorePassword(final String truststorePassword) { this.truststorePassword = truststorePassword; }

        public String getKeystoreLocation() {
            return keystoreLocation;
        }

        public void setKeystoreLocation(final String keystoreLocation) {
            this.keystoreLocation = keystoreLocation;
        }

        public String getKeystorePassword() {
            return keystorePassword;
        }

        public void setKeystorePassword(final String keystorePassword) {
            this.keystorePassword = keystorePassword;
        }

        public String getKeyPassword() {
            return keyPassword;
        }

        public void setKeyPassword(final String keyPassword) {
            this.keyPassword = keyPassword;
        }

        @Override
        public String toString() {
            return "Ssl{" +
                    "truststoreLocation='" + truststoreLocation + '\'' +
                    ", truststorePassword='" + truststorePassword + '\'' +
                    ", keystoreLocation='" + keystoreLocation + '\'' +
                    ", keystorePassword='" + keystorePassword + '\'' +
                    ", keyPassword='" + keyPassword + '\'' +
                    '}';
        }
    }

    public static class Producer implements Serializable {
        private static final long serialVersionUID =8410725178804740256L;

        private int retries;
        private int retryBackoffMs;
        private Integer batchSize;
        private String compressionType;
        private Integer bufferMemory;
        private Integer maxRequestSize;
        private Integer requestTimeoutMs;
        private String maxInFlightRequestsPerConnection;
        private String sendBuffer;
        private String maxBlockMs;
        private String acks;

        public int getRetries() {
            return retries;
        }

        public void setRetries(final int retries) {
            this.retries = retries;
        }

        public int getRetryBackoffMs() {
            return retryBackoffMs;
        }

        public void setRetryBackoffMs(int retryBackoffMs) {
            this.retryBackoffMs = retryBackoffMs;
        }

        public Integer getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(Integer batchSize) {
            this.batchSize = batchSize;
        }

        public String getCompressionType() {
            return compressionType;
        }

        public void setCompressionType(String compressionType) {
            this.compressionType = compressionType;
        }

        public Integer getBufferMemory() {
            return bufferMemory;
        }

        public void setBufferMemory(Integer bufferMemory) {
            this.bufferMemory = bufferMemory;
        }

        public Integer getMaxRequestSize() {
            return maxRequestSize;
        }

        public void setMaxRequestSize(Integer maxRequestSize) {
            this.maxRequestSize = maxRequestSize;
        }

        public Integer getRequestTimeoutMs() {
            return requestTimeoutMs;
        }

        public void setRequestTimeoutMs(Integer requestTimeoutMs) {
            this.requestTimeoutMs = requestTimeoutMs;
        }

        public String getMaxInFlightRequestsPerConnection() {
            return maxInFlightRequestsPerConnection;
        }

        public void setMaxInFlightRequestsPerConnection(String maxInFlightRequestsPerConnection) {
            this.maxInFlightRequestsPerConnection = maxInFlightRequestsPerConnection;
        }

        public String getSendBuffer() {
            return sendBuffer;
        }

        public void setSendBuffer(String sendBuffer) {
            this.sendBuffer = sendBuffer;
        }

        public String getMaxBlockMs() {
            return maxBlockMs;
        }

        public void setMaxBlockMs(String maxBlockMs) {
            this.maxBlockMs = maxBlockMs;
        }

        public String getAcks() {
            return acks;
        }

        public void setAcks(String acks) {
            this.acks = acks;
        }

        @Override
        public String toString() {
            return "Producer{" +
                    "retries=" + retries +
                    ", retryBackoffMs=" + retryBackoffMs +
                    ", batchSize=" + batchSize +
                    ", compressionType='" + compressionType + '\'' +
                    ", bufferMemory=" + bufferMemory +
                    ", maxRequestSize=" + maxRequestSize +
                    ", requestTimeoutMs=" + requestTimeoutMs +
                    ", maxInFlightRequestsPerConnection='" + maxInFlightRequestsPerConnection + '\'' +
                    ", sendBuffer='" + sendBuffer + '\'' +
                    ", maxBlockMs='" + maxBlockMs + '\'' +
                    ", acks='" + acks + '\'' +
                    '}';
        }
    }
}