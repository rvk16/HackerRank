package com.amdocs.aia.il.common.audit;

import com.amdocs.aia.il.common.publisher.KafkaPartitioner;
import com.amdocs.aia.il.common.utils.PublisherUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AuditPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditPublisher.class);

    private List<String> jsonMessages;
    private KafkaPublisher kafkaPublisher;
    private AuditLogger auditLogger;
    private PartitionHandler[] partitionHandlers;

    public AuditPublisher() {
        jsonMessages = new ArrayList<>();
        auditLogger = new AuditLogger();
        kafkaPublisher = new KafkaPublisher();
    }

    public void init() {
        jsonMessages = new ArrayList<>();
        auditLogger = new AuditLogger();
        kafkaPublisher = new KafkaPublisher();
    }

    public void merge(List<String> messages) {
        jsonMessages = messages;
    }

    public void publish(String topicName, Producer<String, String> producer, boolean auditLogsEnabled) {
        ObjectMapper mapper = new ObjectMapper();
        final int partitions = Math.max(fetchTransformerTopicPartitionCount(topicName, producer), 1);
        this.partitionHandlers = new PartitionHandler[partitions];
        IntStream.range(0, partitions).forEach(i -> partitionHandlers[i] = new PartitionHandler(i));
        jsonMessages.forEach(item -> {
                    AuditData auditData = null;
                    try {
                        auditData = mapper.readValue(item, AuditData.class);
                    } catch (JsonProcessingException e) {
                        LOGGER.error("publish failed", e);
                    }
                    addToPartition(partitions, item, auditData.getCorrelationID());
                }
        );

        for (PartitionHandler partitionHandler : partitionHandlers) {
            if (!partitionHandler.isMessageEmpty()) {
                Integer partitionNumber = partitionHandler.getPartitionId();
                auditLogger.logAudit(partitionHandler, auditLogsEnabled);
                kafkaPublisher.publish(partitionHandler, topicName, producer, partitionNumber);
            }
        }
    }

    public void addToPartition(Integer partitions, String auditMsgs, String correlationId) {
        int partitionId = 0;
        try (KafkaPartitioner kafkaPartitioner = new KafkaPartitioner()) {
            if (partitions > 1) {
                String partitionKey = PublisherUtils.buildKeyForAudit(correlationId);
                partitionId = kafkaPartitioner.partition(partitionKey, partitions);
            }
            JSONObject jsonMsg = null;
            try {
                jsonMsg = new JSONObject(auditMsgs);
            } catch (JSONException e) {
                LOGGER.error("JSON failed", e);
            }
            partitionHandlers[partitionId].addMessages(jsonMsg);
        }
    }

    private static int fetchTransformerTopicPartitionCount(String topicName, Producer<String, String> producer) {
        return producer.partitionsFor(topicName).size();
    }
}