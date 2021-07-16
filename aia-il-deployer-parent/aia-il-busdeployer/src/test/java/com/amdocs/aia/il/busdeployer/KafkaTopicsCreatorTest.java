package com.amdocs.aia.il.busdeployer;

import com.amdocs.aia.il.busdeployer.exception.KafkaTopicsCreatorException;
import com.amdocs.aia.il.busdeployer.utils.KafkaUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@SpringBootTest(properties = {
        "logging.level.com.amdocs.aia.il.busdeployer.utils.*=INFO",
        "aia.il.bus.deployer.default-partitions-count=2",
        "aia.il.bus.deployer.default-replication-factor=3",
        "aia.il.bus.deployer.kafka.namespace=",
        "aia.il.bus.deployer.topic-configurations.topic7.replication-factor=7",
        "aia.il.bus.deployer.topic-configurations.topic8.partition-count=8",
        "aia.il.bus.deployer.topic-configurations.topic9.replication-factor=9",
        "aia.il.bus.deployer.topic-configurations.topic9.partition-count=9",
        "aia.il.bus.deployer.topic-configurations.topic7_transformer.replication-factor=7",
        "aia.il.bus.deployer.topic-configurations.topic8_transformer.partition-count=8",
        "aia.il.bus.deployer.topic-configurations.topic9_transformer.replication-factor=9",
        "aia.il.bus.deployer.topic-configurations.topic9_transformer.partition-count=9",
        "aia.il.bus.deployer.topic-configurations.Table_Address.replication-factor=9",
        "aia.il.bus.deployer.topic-configurations.Table_Address.partition-count=9",
        "aia.il.bus.deployer.topic-configurations.Subscriber.partition-count=8",
}, classes = KafkaTopicsCreator.class)
public class KafkaTopicsCreatorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTopicsCreatorTest.class);

    @Autowired
    private KafkaTopicsCreator kafkaTopicsCreator;
    @MockBean
    private KafkaUtils kafkaUtils;

    @Test
    public void shouldUseSourceTopicPropertiesIfProvided() throws KafkaTopicsCreatorException {
        LOGGER.info("Testing that properties from yaml file are used for topics creation");
        List<String> topicsToCreate = Arrays.asList("topic7", "topic8", "topic9");
        kafkaTopicsCreator.createTopics(topicsToCreate);
        Mockito.verify(kafkaUtils, times(1)).createTopic(eq("topic7"), eq(1), eq((short) 7));
        Mockito.verify(kafkaUtils, times(1)).createTopic(eq("topic8"), eq(8), eq((short) 1));
        Mockito.verify(kafkaUtils, times(1)).createTopic(eq("topic9"), eq(9), eq((short) 9));
        Mockito.verify(kafkaUtils, times(1)).listTopics();
        Mockito.verifyNoMoreInteractions(kafkaUtils);
    }

    @Test
    public void shouldUseTransformerTopicPropertiesIfProvided() throws KafkaTopicsCreatorException {
        LOGGER.info("Testing that properties from yaml file are used for topics creation");
        List<String> topicsToCreate = Arrays.asList("topic7", "topic8", "topic9");
        kafkaTopicsCreator.createTransformerTopics(topicsToCreate);
        Mockito.verify(kafkaUtils, times(1)).createTopic(eq("topic7_transformer"), eq(3), eq((short) 7));
        Mockito.verify(kafkaUtils, times(1)).createTopic(eq("topic8_transformer"), eq(8), eq((short) 1));
        Mockito.verify(kafkaUtils, times(1)).createTopic(eq("topic9_transformer"), eq(9), eq((short) 9));
        Mockito.verify(kafkaUtils, times(1)).listTopics();
        Mockito.verifyNoMoreInteractions(kafkaUtils);
    }

    @Test
    public void shouldUseBulkTopicPropertiesIfProvided() throws KafkaTopicsCreatorException {
        LOGGER.info("Testing that properties from yaml file are used for topics creation");
        List<String> topicsToCreate = Arrays.asList("Agreement", "Subscriber", "Table_Address");
        kafkaTopicsCreator.createBulkTopics(topicsToCreate);
        Mockito.verify(kafkaUtils, times(1)).createTopic(eq("Agreement"), eq(10), eq((short) 1));
        Mockito.verify(kafkaUtils, times(1)).createTopic(eq("Subscriber"), eq(8), eq((short) 1));
        Mockito.verify(kafkaUtils, times(1)).createTopic(eq("Table_Address"), eq(9), eq((short) 9));
        Mockito.verify(kafkaUtils, times(1)).listTopics();
        Mockito.verifyNoMoreInteractions(kafkaUtils);
    }

    @Test
    public void shouldUseContextTransformerBulkTopicsPropertiesIfProvided() throws KafkaTopicsCreatorException {
        LOGGER.info("Testing that properties from yaml file are used for topics creation");
        List<String> topicsToCreate = Collections.singletonList("aLDMCustomer_Address");
        kafkaTopicsCreator.createContextTransformerBulkTopics(topicsToCreate);
        Mockito.verify(kafkaUtils, times(1)).createTopic(eq("aLDMCustomer_Address"), eq(5), eq((short) 1));
    }

    @Test
    public void shouldNotCreateTopicIfAlreadyExist() throws KafkaTopicsCreatorException {
        LOGGER.info("Testing that properties from yaml file are used for topics creation");
        List<String> topicsToCreate = Collections.singletonList("topic7");
        List<String> topicsAlreadyExist = Arrays.asList("topic7", "topic7_transformer");
        Set<String> exists = new HashSet<>(topicsAlreadyExist);

        when(kafkaUtils.listTopics()).thenReturn(exists);
        try {
            kafkaTopicsCreator.createTopics(topicsToCreate);
        } catch (KafkaTopicsCreatorException e) {
            assertEquals("Topics already Exist", e.getMessage());
        }
    }

    @Test
    public void shouldUseNamespaceWhenCallingKafkaAdmin() throws KafkaTopicsCreatorException {
        LOGGER.info("Testing that namespace from yaml file is used");
        String topicName = "topic8";
        List<String> topicsToCreate = Arrays.asList(topicName, topicName);
        String oldNamespace = kafkaTopicsCreator.getConfiguration().getKafka().getNamespace();
        String newNamespace = "ns1";
        String realTopicName = newNamespace + '_' + topicName;
        kafkaTopicsCreator.getConfiguration().getKafka().setNamespace(newNamespace);
        try {
            kafkaTopicsCreator.createTopics(topicsToCreate);
            Mockito.verify(kafkaUtils, times(topicsToCreate.size())).createTopic(eq(realTopicName), eq(8), eq((short) 1));
            Mockito.verify(kafkaUtils, times(1)).listTopics();
        } finally {
            kafkaTopicsCreator.getConfiguration().getKafka().setNamespace(oldNamespace);
        }
    }

    @Test
    public void shouldFailWithClearMessageIfReplicationFactorIsNull() {
        final IntegrationLayerBusDeployerConfiguration configuration = kafkaTopicsCreator.getConfiguration();
        final Short oldReplicationFactor = configuration.getDefaultReplicationFactorKafkaSource();
        configuration.setDefaultReplicationFactorKafkaSource(null);
        try {
            final KafkaTopicsCreatorException exception = assertThrows(KafkaTopicsCreatorException.class,
                    () -> kafkaTopicsCreator.createTopics(Arrays.asList("topic10", "topic11"))
            );
            assertEquals("ReplicationFactor is not provided for topic topic10", exception.getMessage());
        } finally {
            configuration.setDefaultReplicationFactorKafkaSource(oldReplicationFactor);
        }
    }

    @Test
    public void shouldFailWithClearMessageIfPartitionCountIsNull() {
        final IntegrationLayerBusDeployerConfiguration configuration = kafkaTopicsCreator.getConfiguration();
        final Integer oldPartitionCount = configuration.getDefaultPartitionsCountKafkaSource();
        configuration.setDefaultPartitionsCountKafkaSource(null);
        try {
            final KafkaTopicsCreatorException exception = assertThrows(KafkaTopicsCreatorException.class,
                    () -> kafkaTopicsCreator.createTopics(Arrays.asList("topic10", "topic11"))
            );
            assertEquals("PartitionCount is not provided for topic topic10", exception.getMessage());
        } finally {
            configuration.setDefaultPartitionsCountKafkaSource(oldPartitionCount);
        }
    }
}