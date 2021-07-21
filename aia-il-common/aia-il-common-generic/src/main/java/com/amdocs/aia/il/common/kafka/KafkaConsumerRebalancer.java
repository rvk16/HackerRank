package com.amdocs.aia.il.common.kafka;


import com.amdocs.aia.il.common.model.configuration.properties.KafkaConfiguration;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.internals.ConsumerCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.amdocs.aia.il.common.utils.PublisherUtils.*;

@Component
@EnableScheduling
@ConditionalOnProperty({"kafka.consumer.periodicRebalancing"})
public class KafkaConsumerRebalancer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerRebalancer.class);

    private static Consumer<String, byte[]> consumer;

    @Autowired
    private KafkaConfiguration kafkaConfig;

    @PostConstruct
    public void init(){
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(this::rebalance, kafkaConfig.getConsumer().getPeriodicRebalancingIntervalInSec(), kafkaConfig.getConsumer().getPeriodicRebalancingIntervalInSec(), TimeUnit.SECONDS);
    }

    public void rebalance() {
        try {
            if (consumer != null && determineIfCurrentConsumerGroupLeader()) {
                LOGGER.warn(TRIGGERED_SCHEDULED_REBALANCING_BY_CONSUMER_ID, consumer.groupMetadata().memberId());
                synchronized (consumer) {
                    consumer.enforceRebalance();
                }
            } else {
                LOGGER.info(SCHEDULED_REBALANCING_NOT_TRIGGERED, (consumer != null ? consumer.groupMetadata().memberId() : BLANK));
            }
        } catch (Exception e) {
            e.printStackTrace();//NOSONAR
            LOGGER.error("Exception in rebalance {}", e.getMessage());
        }
    }

    private boolean determineIfCurrentConsumerGroupLeader() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<KafkaConsumer> consumerClass = (Class<KafkaConsumer>) Class.forName(KAFKA_CONSUMER_PCK);
        Field privateField = consumerClass.getDeclaredField(COORDINATOR_FIELD_NAME);
        privateField.setAccessible(true);
        ConsumerCoordinator coordinator = (ConsumerCoordinator) privateField.get(consumer);
        Class<KafkaConsumer> ConsumerCoordinatorClass = (Class<KafkaConsumer>)   Class.forName(CONSUMER_COORDINATOR_PKG);
        privateField = ConsumerCoordinatorClass.getDeclaredField(ISLEADER_FIELD_NAME);
        privateField.setAccessible(true);
        return (boolean) privateField.get(coordinator);
    }

    public static void initConsumer(Consumer<String, byte[]> consumer) {
        KafkaConsumerRebalancer.consumer = consumer;
    }
}
