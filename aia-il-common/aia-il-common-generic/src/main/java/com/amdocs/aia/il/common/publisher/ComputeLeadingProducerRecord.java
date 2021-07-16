package com.amdocs.aia.il.common.publisher;

public class ComputeLeadingProducerRecord {



    final KafkaBulkPublishTransaction kafkaBulkPublishTransaction;
    final String dataChannelName;
    final String transformerTopic;
    final Integer partitionNumber;

    public ComputeLeadingProducerRecord(KafkaBulkPublishTransaction kafkaBulkPublishTransaction, String dataChannelName, String transformerTopic, Integer partitionNumber) {
        this.kafkaBulkPublishTransaction = kafkaBulkPublishTransaction;
        this.dataChannelName = dataChannelName;
        this.transformerTopic = transformerTopic;
        this.partitionNumber = partitionNumber;
    }

    public KafkaBulkPublishTransaction getKafkaBulkPublishTransaction() {
        return kafkaBulkPublishTransaction;
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
