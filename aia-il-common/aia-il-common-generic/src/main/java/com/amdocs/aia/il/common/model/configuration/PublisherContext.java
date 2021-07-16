package com.amdocs.aia.il.common.model.configuration;

import com.amdocs.aia.il.common.model.configuration.entity.DataProcessingContext;
import com.amdocs.aia.il.common.model.configuration.entity.ModelContextEntity;
import com.amdocs.aia.il.common.model.configuration.entity.PublishEntity;
import com.amdocs.aia.il.common.model.configuration.properties.ConfigurationProperties;
import com.amdocs.aia.il.common.model.configuration.properties.DatabaseConfigurationProperties;

import java.util.Map;

public class PublisherContext {

    private final Map<String, PublishEntity> topicPublish;
    private final ConfigurationProperties configurationProperties;
    private final DatabaseConfigurationProperties databaseConfigurationProperties;
    private final Map<String, ModelContextEntity> contextConfiguration;
    private final Map<String, String> kafkaProperties;
    private final Map<String, Map<String, DataProcessingContext>> dataProcessingContextsPerDataChannel;

    public PublisherContext(Map<String, PublishEntity> topicPublish, ConfigurationProperties configurationProperties,
                            DatabaseConfigurationProperties databaseConfigurationProperties, Map<String, ModelContextEntity> contextConfiguration,
                            Map<String, String> kafkaProperties, Map<String, Map<String, DataProcessingContext>> dataProcessingContextsPerDataChannel) {
        this.topicPublish = topicPublish;
        this.configurationProperties = configurationProperties;
        this.databaseConfigurationProperties = databaseConfigurationProperties;
        this.contextConfiguration = contextConfiguration;
        this.kafkaProperties = kafkaProperties;
        this.dataProcessingContextsPerDataChannel = dataProcessingContextsPerDataChannel;
    }

    public Map<String, PublishEntity> getTopicPublish() { return topicPublish; }

    public ConfigurationProperties getConfigurationProperties() { return configurationProperties; }

    public DatabaseConfigurationProperties getDatabaseConfigurationProperties() { return databaseConfigurationProperties; }

    public Map<String, ModelContextEntity> getContextConfiguration() { return contextConfiguration; }

    public Map<String, String> getKafkaProperties() { return kafkaProperties; }

    public Map<String, Map<String, DataProcessingContext>> getDataProcessingContextsPerDataChannel() {
        return dataProcessingContextsPerDataChannel;
    }
}
