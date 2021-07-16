package com.amdocs.aia.il.busdeployer;

import com.amdocs.aia.il.busdeployer.exception.KafkaTopicsCreatorException;
import com.amdocs.aia.il.busdeployer.logs.Constants;
import com.amdocs.aia.il.busdeployer.logs.LogMsg;
import com.amdocs.aia.il.busdeployer.utils.KafkaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

@Component
@EnableConfigurationProperties(IntegrationLayerBusDeployerConfiguration.class)
public class KafkaTopicsCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTopicsCreator.class);


    private final IntegrationLayerBusDeployerConfiguration configuration;
    private KafkaUtils kafkaUtils;

    @Autowired
    public KafkaTopicsCreator(final IntegrationLayerBusDeployerConfiguration configuration) {
        this.configuration = configuration;
    }
    public IntegrationLayerBusDeployerConfiguration getConfiguration() {
        return configuration;
    }

    @Autowired
    @Lazy
    @Bean
    public void setKafkaUtils(KafkaUtils kafkaUtils) {
        this.kafkaUtils = kafkaUtils;
    }

    public void createTopics(final Iterable<String> topics) {
        final Set<String> existingTopics = kafkaUtils.listTopics();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_EXISTING_TOPICS), existingTopics);
        }
        topics.forEach(topicName -> createSourceTopicIfAbsent(topicName, existingTopics));
    }

    public void createTransformerTopics(final Iterable<String> topics) {
        final Set<String> existingTopics = kafkaUtils.listTopics();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_EXISTING_TOPICS), existingTopics);
        }
        topics.forEach(topicName -> createTransformerTopicIfAbsent(topicName+Constants.TRANSFORMER, existingTopics));
    }

    public void createBulkTopics(final Iterable<String> topics) {
        final Set<String> existingTopics = kafkaUtils.listTopics();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_EXISTING_TOPICS), existingTopics);
        }
        topics.forEach(topicName -> createBulkTopicIfAbsent(topicName, existingTopics));
    }

    public void createContextTransformerBulkTopics(Iterable<String> topics) {
        final Set<String> existingTopics = kafkaUtils.listTopics();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_EXISTING_TOPICS), existingTopics);
        }
        topics.forEach(topicName -> createContextTransformerBulkTopicsIfAbsent(topicName, existingTopics));
    }

    private void createSourceTopicIfAbsent(final String topicName, final Set<String> existingTopics) {
        final String fullName = getFullName(topicName);
        if (existingTopics.stream().anyMatch(fullName::equalsIgnoreCase)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPIC_EXIST_SKIPPING), fullName);
            }
            kafkaUtils.alterPartitionsForTopic(fullName,configuration.getDefaultPartitionsCountKafkaSource());
            return;
        }
        final Integer partitionCount = configuration.getPartitionCountForSourceTopic(topicName);
        if (partitionCount == null) {
            throw new KafkaTopicsCreatorException(String.format(Constants.PARTITION_COUNT_IS_NOT_PROVIDED, topicName));
        }
        final Short replicationFactor = configuration.getReplicationFactorForSourceTopic(topicName);
        if (replicationFactor == null) {
            throw new KafkaTopicsCreatorException(String.format(Constants.REPLICATION_FACTOR_IS_NOT_PROVIDED, topicName));
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_CREATING_TOPICS_WITH_PARTITION_AND_REPLICATION),
                    fullName, partitionCount, replicationFactor);
        }
        kafkaUtils.createTopic(fullName, partitionCount, replicationFactor);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPIC_CREATED_SUCCESSFULLY), fullName);
        }
    }
    private void createTransformerTopicIfAbsent(final String topicName, final Set<String> existingTopics) {
        final String fullName = getFullName(topicName);
        if (existingTopics.stream().anyMatch(fullName::equalsIgnoreCase)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPIC_EXIST_SKIPPING), fullName);
            }
            kafkaUtils.alterPartitionsForTopic(fullName,configuration.getDefaultPartitionsCountKafkaTransformer());
            return;
        }
        final Integer partitionCount = configuration.getPartitionCountForTransformerTopic(topicName);
        if (partitionCount == null) {
            throw new KafkaTopicsCreatorException(String.format(Constants.PARTITION_COUNT_IS_NOT_PROVIDED, topicName));
        }
        final Short replicationFactor = configuration.getReplicationFactorForTransformerTopic(topicName);
        if (replicationFactor == null) {
            throw new KafkaTopicsCreatorException(String.format(Constants.REPLICATION_FACTOR_IS_NOT_PROVIDED, topicName));
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_CREATING_TOPICS_WITH_PARTITION_AND_REPLICATION),
                    fullName, partitionCount, replicationFactor);
        }
        kafkaUtils.createTopic(fullName, partitionCount, replicationFactor);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPIC_CREATED_SUCCESSFULLY), fullName);
        }
    }

    private void createBulkTopicIfAbsent(final String topicName, final Set<String> existingTopics) {
        final String fullName = getFullName(topicName);
        if (existingTopics.stream().anyMatch(fullName::equalsIgnoreCase)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPIC_EXIST_SKIPPING), fullName);
            }
            kafkaUtils.alterPartitionsForTopic(fullName,configuration.getDefaultPartitionsCountKafkaBulk());
            return;
        }
        final Integer partitionCount = configuration.getPartitionCountForBulkTopic(topicName);
        if (partitionCount == null) {
            throw new KafkaTopicsCreatorException(String.format(Constants.PARTITION_COUNT_IS_NOT_PROVIDED, topicName));
        }
        final Short replicationFactor = configuration.getReplicationFactorForBulkTopic(topicName);
        if (replicationFactor == null) {
            throw new KafkaTopicsCreatorException(String.format(Constants.REPLICATION_FACTOR_IS_NOT_PROVIDED, topicName));
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_CREATING_TOPICS_WITH_PARTITION_AND_REPLICATION),
                    fullName, partitionCount, replicationFactor);
        }
        kafkaUtils.createTopic(fullName, partitionCount, replicationFactor);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPIC_CREATED_SUCCESSFULLY), fullName);
        }
    }

    private void createContextTransformerBulkTopicsIfAbsent(String topicName, Set<String> existingTopics) {
        final String fullName = getFullName(topicName);
        if (existingTopics.stream().anyMatch(fullName::equalsIgnoreCase)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPIC_EXIST_SKIPPING), fullName);
            }
            kafkaUtils.alterPartitionsForTopic(fullName,configuration.getDefaultPartitionsCountKafkaContextTransformerBulkTopics());
            return;
        }
        final Integer partitionCount = configuration.getPartitionCountForContextTransformerBulkTopic(topicName);
        if (partitionCount == null) {
            throw new KafkaTopicsCreatorException(String.format(Constants.PARTITION_COUNT_IS_NOT_PROVIDED, topicName));
        }
        final Short replicationFactor = configuration.getReplicationFactorForContextTransformerBulkTopic(topicName);
        if (replicationFactor == null) {
            throw new KafkaTopicsCreatorException(String.format(Constants.REPLICATION_FACTOR_IS_NOT_PROVIDED, topicName));
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_CREATING_TOPICS_WITH_PARTITION_AND_REPLICATION),
                    fullName, partitionCount, replicationFactor);
        }
        kafkaUtils.createTopic(fullName, partitionCount, replicationFactor);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPIC_CREATED_SUCCESSFULLY), fullName);
        }
    }

    private String getFullName(final String topicName) {
        final String namespace = configuration.getKafka().getNamespace();
        return StringUtils.hasText(namespace) ? namespace + '_' + topicName : topicName;
    }
}