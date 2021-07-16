package com.amdocs.aia.il.busdeployer.utils;

import com.amdocs.aia.il.busdeployer.IntegrationLayerBusDeployerConfiguration;
import com.amdocs.aia.il.busdeployer.exception.KafkaTopicsCreatorException;
import com.amdocs.aia.il.busdeployer.logs.Constants;
import com.amdocs.aia.il.busdeployer.logs.LogMsg;
import com.amdocs.aia.il.common.utils.PublisherUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Lazy
@Component
@EnableConfigurationProperties(IntegrationLayerBusDeployerConfiguration.class)
public class KafkaUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaUtils.class);

    private final IntegrationLayerBusDeployerConfiguration configuration;
    private AdminClient adminClient;


    @Autowired
    public KafkaUtils(final IntegrationLayerBusDeployerConfiguration configuration) {
        this.configuration = configuration;
    }

    @PostConstruct
    void init() {
        final IntegrationLayerBusDeployerConfiguration.Kafka kafkaConfiguration = configuration.getKafka();
        final String securityAuthLoginConfig = kafkaConfiguration.getSecurityAuthLoginConfig();
        if (StringUtils.hasText(securityAuthLoginConfig)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(LogMsg.getMessage(Constants.MSG_SETTING_JAVA_SECURITY_AUTH_LOGIN), securityAuthLoginConfig);
            }
            System.setProperty(Constants.JAVA_SECURITY_AUTH_LOGIN_CONFIG, securityAuthLoginConfig);
        }
        final String securityKrb5Conf = kafkaConfiguration.getSecurityKrb5Conf();
        if (StringUtils.hasText(securityKrb5Conf)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(LogMsg.getMessage(Constants.MSG_SETTING_JAVA_SECURITY_KRB5), securityKrb5Conf);
            }
            System.setProperty(Constants.JAVA_SECURITY_KRB5_CONF, securityKrb5Conf);
        }
        adminClient = AdminClient.create(getProperties(kafkaConfiguration));
    }

    public Set<String> listTopics() {
        try {
            return adminClient.listTopics().names().get();
        } catch (final InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new KafkaTopicsCreatorException(e);
        }
    }

    public Integer getTopicDetails(String topic){
        DescribeTopicsResult dt = adminClient.describeTopics(Collections.singleton(topic));
        try {
            dt.all().get();
            TopicDescription topicDescription = dt.values().get(topic).get();
            List<TopicPartitionInfo> topicPartitionInfos=topicDescription.partitions();
            return topicPartitionInfos.size();
        } catch (InterruptedException | ExecutionException  e) {
            Thread.currentThread().interrupt();
            if (e.getCause() instanceof UnknownTopicOrPartitionException) {
                return null;
            }
            throw new IllegalStateException("Exception occured during topic details retrieval. name: " + e);
        }
    }

    public void alterPartitionsForTopic(String topic, int newPartitionCount) {
        int existingPartitionCount=getTopicDetails(topic);
        if(existingPartitionCount<newPartitionCount) {
            Map<String, NewPartitions> newPatitionMap = new HashMap<>();
            NewPartitions np = NewPartitions.increaseTo(newPartitionCount);
            newPatitionMap.put(topic, np);
            CreatePartitionsOptions cpo = new CreatePartitionsOptions();
            cpo.timeoutMs(5 * 1000);
            try {
                adminClient.createPartitions(newPatitionMap).all().get();
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPIC_EXIST_CHECKING_CONFIGURATIONS), topic,existingPartitionCount,newPartitionCount);
                }
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("Exception occurred during modifying partitions for topic {} \n {}", topic, e.getMessage());
            }
        }else{
            LOGGER.info(" Topic currently has {} partitions, which is higher than or equal to the requested {}.",existingPartitionCount, newPartitionCount);
        }
    }

    public void createTopic(final String topicName, final int partitionCount, final short replicationFactor) {
        try {
            adminClient.createTopics(Collections.singletonList(new NewTopic(topicName, partitionCount, replicationFactor))).all().get();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaTopicsCreatorException(e);
        } catch (final ExecutionException e) {
            if (!(e.getCause() instanceof TopicExistsException)) {
                throw new KafkaTopicsCreatorException(e);
            }
            // TopicExistsException - swallow this exception, just means the topic already exists.
        }
    }

    private static Properties getProperties(final IntegrationLayerBusDeployerConfiguration.Kafka kafkaConfiguration) {
        final Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.getBrokers());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfiguration.getKeySerializer());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfiguration.getValueSerializer());
        final String securityProtocol = kafkaConfiguration.getSecurityProtocol();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_KAFKA_SECURITY_PROTOCOL), securityProtocol);
        }
        if (securityProtocol != null) {
            properties.put(Constants.SECURITY_PROTOCOL, securityProtocol);
            if (securityProtocol.contains("SSL")) {
                // http://kafka.apache.org/10/documentation.html#security_configclients
                properties.put(SslConfigs.SSL_PROTOCOL_CONFIG, Constants.TLSV);
                final IntegrationLayerBusDeployerConfiguration.Kafka.Ssl ssl = kafkaConfiguration.getSsl();
                if (ssl != null) {
                    LOGGER.info("password: {}", System.getenv(PublisherUtils.KAFKA_SSL_TRUSTSTORE_PASSWORD_DEPLOYER));
                    setPropertyNullSafe(properties, SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, ssl.getTruststoreLocation());
                    setPropertyNullSafe(properties, SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, System.getenv(PublisherUtils.KAFKA_SSL_TRUSTSTORE_PASSWORD_DEPLOYER));
                }
            }
            if (securityProtocol.startsWith(Constants.SASL)) {
                setPropertyNullSafe(properties, Constants.SASL_MECHANISM, kafkaConfiguration.getSaslMechanism());
                // http://kafka.apache.org/10/documentation.html#security_sasl_kerberos
                properties.put(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, "kafka");
            }
        }
        return properties;
    }

    private static void setPropertyNullSafe(final Properties properties, final String key, final String value) {
        if (LOGGER.isInfoEnabled() && !key.contains("password")) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_KAFKA_PROPERTY), key, value);
        }
        if (StringUtils.hasText(value)) {
            properties.put(key, value);
        }
    }
}