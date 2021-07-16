package com.amdocs.aia.il.busdeployer;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "aia.il.bus.deployer")
public class IntegrationLayerBusDeployerConfiguration {
    private Integer defaultPartitionsCountKafkaSource;
    private Short defaultReplicationFactorKafkaSource;
    private Integer defaultPartitionsCountKafkaTransformer;
    private Short defaultReplicationFactorKafkaTransformer;
    private Integer defaultPartitionsCountKafkaBulk;
    private Short defaultReplicationFactorKafkaBulk;
    private Integer defaultPartitionsCountKafkaContextTransformerBulkTopics;
    private Short defaultReplicationFactorKafkaContextTransformerBulkTopics;
    private Map<String, TopicConfiguration> topicConfigurations;

    private Kafka kafka = new Kafka();

    public Integer getDefaultPartitionsCountKafkaSource() {
        return defaultPartitionsCountKafkaSource;
    }

    public void setDefaultPartitionsCountKafkaSource(Integer defaultPartitionsCountKafkaSource) {
        this.defaultPartitionsCountKafkaSource = defaultPartitionsCountKafkaSource;
    }

    public Short getDefaultReplicationFactorKafkaSource() {
        return defaultReplicationFactorKafkaSource;
    }

    public void setDefaultReplicationFactorKafkaSource(Short defaultReplicationFactorKafkaSource) {
        this.defaultReplicationFactorKafkaSource = defaultReplicationFactorKafkaSource;
    }

    public Integer getDefaultPartitionsCountKafkaTransformer() {
        return defaultPartitionsCountKafkaTransformer;
    }

    public void setDefaultPartitionsCountKafkaTransformer(Integer defaultPartitionsCountKafkaTransformer) {
        this.defaultPartitionsCountKafkaTransformer = defaultPartitionsCountKafkaTransformer;
    }

    public Short getDefaultReplicationFactorKafkaTransformer() {
        return defaultReplicationFactorKafkaTransformer;
    }

    public void setDefaultReplicationFactorKafkaTransformer(Short defaultReplicationFactorKafkaTransformer) {
        this.defaultReplicationFactorKafkaTransformer = defaultReplicationFactorKafkaTransformer;
    }

    public Integer getDefaultPartitionsCountKafkaBulk() {
        return defaultPartitionsCountKafkaBulk;
    }

    public void setDefaultPartitionsCountKafkaBulk(Integer defaultPartitionsCountKafkaBulk) {
        this.defaultPartitionsCountKafkaBulk = defaultPartitionsCountKafkaBulk;
    }

    public Short getDefaultReplicationFactorKafkaBulk() {
        return defaultReplicationFactorKafkaBulk;
    }

    public void setDefaultReplicationFactorKafkaBulk(Short defaultReplicationFactorKafkaBulk) {
        this.defaultReplicationFactorKafkaBulk = defaultReplicationFactorKafkaBulk;
    }

    public Integer getDefaultPartitionsCountKafkaContextTransformerBulkTopics() {
        return defaultPartitionsCountKafkaContextTransformerBulkTopics;
    }

    public void setDefaultPartitionsCountKafkaContextTransformerBulkTopics(
            Integer defaultPartitionsCountKafkaContextTransformerBulkTopics) {
        this.defaultPartitionsCountKafkaContextTransformerBulkTopics =
                defaultPartitionsCountKafkaContextTransformerBulkTopics;
    }

    public Short getDefaultReplicationFactorKafkaContextTransformerBulkTopics() {
        return defaultReplicationFactorKafkaContextTransformerBulkTopics;
    }

    public void setDefaultReplicationFactorKafkaContextTransformerBulkTopics(
            Short defaultReplicationFactorKafkaContextTransformerBulkTopics) {
        this.defaultReplicationFactorKafkaContextTransformerBulkTopics =
                defaultReplicationFactorKafkaContextTransformerBulkTopics;
    }

    public Map<String, TopicConfiguration> getTopicConfigurations() {
        return topicConfigurations;
    }

    public void setTopicConfigurations(final Map<String, TopicConfiguration> topicConfigurations) {
        this.topicConfigurations = topicConfigurations;
    }

    public Kafka getKafka() {
        return kafka;
    }

    public void setKafka(Kafka kafka) {
        this.kafka = kafka;
    }

    Integer getPartitionCountForSourceTopic(final String topicName) {
        final TopicConfiguration topicConfiguration = getTopicConfiguration(topicName);
        if (topicConfiguration != null && topicConfiguration.getPartitionCount() != null) {
            return topicConfiguration.getPartitionCount();
        }
        return getDefaultPartitionsCountKafkaSource();
    }

    private TopicConfiguration getTopicConfiguration(final String topicName) {
        return getTopicConfigurations() == null ? null : getTopicConfigurations().getOrDefault(topicName, null);
    }

    Short getReplicationFactorForSourceTopic(final String topicName) {
        final TopicConfiguration topicConfiguration = getTopicConfiguration(topicName);
        if (topicConfiguration != null && topicConfiguration.getReplicationFactor() != null) {
            return topicConfiguration.getReplicationFactor();
        }
        return getDefaultReplicationFactorKafkaSource();
    }

    Integer getPartitionCountForTransformerTopic(final String topicName) {
        final TopicConfiguration topicConfiguration = getTopicConfiguration(topicName);
        if (topicConfiguration != null && topicConfiguration.getPartitionCount() != null) {
            return topicConfiguration.getPartitionCount();
        }
        return getDefaultPartitionsCountKafkaTransformer();
    }


    Short getReplicationFactorForTransformerTopic(final String topicName) {
        final TopicConfiguration topicConfiguration = getTopicConfiguration(topicName);
        if (topicConfiguration != null && topicConfiguration.getReplicationFactor() != null) {
            return topicConfiguration.getReplicationFactor();
        }
        return getDefaultReplicationFactorKafkaTransformer();
    }

    Integer getPartitionCountForBulkTopic(final String topicName) {
        final TopicConfiguration topicConfiguration = getTopicConfiguration(topicName);
        if (topicConfiguration != null && topicConfiguration.getPartitionCount() != null) {
            return topicConfiguration.getPartitionCount();
        }
        return getDefaultPartitionsCountKafkaBulk();
    }


    Short getReplicationFactorForBulkTopic(final String topicName) {
        final TopicConfiguration topicConfiguration = getTopicConfiguration(topicName);
        if (topicConfiguration != null && topicConfiguration.getReplicationFactor() != null) {
            return topicConfiguration.getReplicationFactor();
        }
        return getDefaultReplicationFactorKafkaBulk();
    }

    public Integer getPartitionCountForContextTransformerBulkTopic(String topicName) {
        final TopicConfiguration topicConfiguration = getTopicConfiguration(topicName);
        if (topicConfiguration != null && topicConfiguration.getPartitionCount() != null) {
            return topicConfiguration.getPartitionCount();
        }
        return getDefaultPartitionsCountKafkaContextTransformerBulkTopics();
    }

    public Short getReplicationFactorForContextTransformerBulkTopic(String topicName) {
        final TopicConfiguration topicConfiguration = getTopicConfiguration(topicName);
        if (topicConfiguration != null && topicConfiguration.getReplicationFactor() != null) {
            return topicConfiguration.getReplicationFactor();
        }
        return getDefaultReplicationFactorKafkaContextTransformerBulkTopics();
    }

    public static class Kafka {
        private String namespace = "";
        private String brokers = "";
        private String securityProtocol ;
        private String saslMechanism;
        private Ssl ssl;
        private String securityAuthLoginConfig;
        private String securityKrb5Conf;
        private String keySerializer = "org.apache.kafka.common.serialization.ByteArraySerializer";
        private String valueSerializer = "org.apache.kafka.common.serialization.ByteArraySerializer";
        private int chunkSize = 3;


        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getBrokers() {
            return brokers;
        }

        public void setBrokers(final String brokers) {
            this.brokers = brokers;
        }

        public String getSecurityProtocol() {
            return securityProtocol;
        }

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

        public String getSecurityAuthLoginConfig() {
            return securityAuthLoginConfig;
        }

        public void setSecurityAuthLoginConfig(final String securityAuthLoginConfig) {
            this.securityAuthLoginConfig = securityAuthLoginConfig;
        }

        public String getSecurityKrb5Conf() {
            return securityKrb5Conf;
        }

        public void setSecurityKrb5Conf(final String securityKrb5Conf) {
            this.securityKrb5Conf = securityKrb5Conf;
        }

        public int getChunkSize() {
            return chunkSize;
        }

        public void setChunkSize(final int chunkSize) {
            this.chunkSize = chunkSize;
        }

        public String getKeySerializer() {
            return keySerializer;
        }

        public void setKeySerializer(final String keySerializer) {
            this.keySerializer = keySerializer;
        }

        public String getValueSerializer() {
            return valueSerializer;
        }

        public void setValueSerializer(final String valueSerializer) {
            this.valueSerializer = valueSerializer;
        }

        public static class Ssl {
            private String truststoreLocation;
            private String truststorePassword;
            private String keystoreLocation;
            private String keystorePassword;
            private String keyPassword;

            public String getTruststoreLocation() {
                return truststoreLocation;
            }

            public void setTruststoreLocation(final String truststoreLocation) {
                this.truststoreLocation = truststoreLocation;
            }

            public String getTruststorePassword() {
                return truststorePassword;
            }

            public void setTruststorePassword(final String truststorePassword) {
                this.truststorePassword = truststorePassword;
            }

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
        }
    }

    public static class TopicConfiguration {
        private Integer partitionCount;
        private Short replicationFactor;

        public void setPartitionCount(Integer partitionCount) {
            this.partitionCount = partitionCount;
        }

        public void setReplicationFactor(Short replicationFactor) {
            this.replicationFactor = replicationFactor;
        }


        public Integer getPartitionCount() {
            return partitionCount;
        }

        public Short getReplicationFactor() {
            return replicationFactor;
        }
    }
}