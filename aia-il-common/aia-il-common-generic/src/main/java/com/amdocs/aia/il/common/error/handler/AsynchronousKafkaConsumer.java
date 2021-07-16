package com.amdocs.aia.il.common.error.handler;

import com.amdocs.aia.il.common.model.configuration.properties.KafkaConfiguration;
import com.amdocs.aia.il.common.utils.SneakyThrowUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
@ConditionalOnExpression("${aia.il.non.transient.transformer.errorHandlerEnabled : true} || ${aia.il.transient.transformer.errorHandlerEnabled : true} || ${aia.il.replicator.errorHandlerEnabled : true} || ${aia.il.bulk.transformer.errorHandlerEnabled : true} || ${aia.il.bulk.replicator.errorHandlerEnabled : true} || ${aia.il.compute.leading.key.errorHandlerEnabled : true}")
public class AsynchronousKafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousKafkaConsumer.class);

    private static final long KAFKA_POLL_TIME = 2; //In Milli Seconds
    private static final long DEFAULT_POLL_INTERVAL_MILLI_SEC = 30000; //In Milli Seconds

    private static Consumer<String, byte[]> consumer;
    KafkaConfiguration kafkaConfiguration;

    @Autowired
    public AsynchronousKafkaConsumer(KafkaConfiguration kafkaConfiguration) {
        this.kafkaConfiguration = kafkaConfiguration;
    }

    @PostConstruct
    public void init() {
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(this::consumerPoll, getPollDelay(), getPollDelay(), TimeUnit.MILLISECONDS);
    }

    public void consumerPoll() {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Async kafka poll thread is running, delay [{}]", getPollDelay());
        }
        try {
            synchronized (consumer) {
                consumer.poll(Duration.ofMillis(KAFKA_POLL_TIME));
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while polling records from Async Kafka consumer, {}", e.getMessage());
            SneakyThrowUtil.sneakyThrow(e);
        }
    }

    private long getPollDelay() {
        if (kafkaConfiguration.getConsumer().getMaxPollIntervalMilliSec() <= DEFAULT_POLL_INTERVAL_MILLI_SEC) {
            return kafkaConfiguration.getConsumer().getMaxPollIntervalMilliSec();
        }
        return kafkaConfiguration.getConsumer().getMaxPollIntervalMilliSec() - DEFAULT_POLL_INTERVAL_MILLI_SEC;
    }

    public static void initConsumer(Consumer<String, byte[]> consumer) {
        AsynchronousKafkaConsumer.consumer = consumer;
    }

}
