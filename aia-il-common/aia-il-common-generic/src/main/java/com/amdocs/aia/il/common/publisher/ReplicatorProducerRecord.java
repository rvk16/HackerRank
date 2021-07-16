package com.amdocs.aia.il.common.publisher;

public class ReplicatorProducerRecord {

    final KafkaPublishTransaction kafkaPublishTransaction;
    final String dataChannelName;
    final String transformerTopic;
    final Integer partitionNumber;

    public ReplicatorProducerRecord(KafkaPublishTransaction kafkaPublishTransaction, String dataChannelName, String transformerTopic, Integer partitionNumber) {
        this.kafkaPublishTransaction = kafkaPublishTransaction;
        this.dataChannelName = dataChannelName;
        this.transformerTopic = transformerTopic;
        this.partitionNumber = partitionNumber;
    }


    public KafkaPublishTransaction getKafkaPublishTransaction() {
        return kafkaPublishTransaction;
    }

    public String getDataChannelName() {
        return dataChannelName;
    }

    public String getTransformerTopic() {
        return transformerTopic;
    }

    public Integer getPartitionNumber() {
        return partitionNumber;
    }
}
