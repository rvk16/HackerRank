package com.amdocs.aia.il.common.audit;


import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPublisher.class);
    ProducerRecord<String, String> message = null;

    public void publish(PartitionHandler partitionHandler, String topicName, Producer<String, String> producer, Integer partitionNumber) {
        message = new ProducerRecord<>(topicName, partitionNumber, "", partitionHandler.getAuditDataList().toString());
        LOGGER.info("Sending message to audit topic '{}' partition: {}", topicName, partitionNumber);
        producer.send(message);

    }
}