package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.common.serialization.formatter.MessageFormatter;
import com.amdocs.aia.common.serialization.messages.Transaction;

public class TransformerProducerRecord {

    final String transformerTopic;
    final Integer partitionNumber;
    final String dataChannelName;
    final Transaction transaction;
    final MessageFormatter messageFormatter;

    public TransformerProducerRecord(String transformerTopic, Integer partitionNumber, String dataChannelName, Transaction transaction, MessageFormatter messageFormatter) {
        this.transformerTopic = transformerTopic;
        this.partitionNumber = partitionNumber;
        this.dataChannelName = dataChannelName;
        this.transaction = transaction;
        this.messageFormatter = messageFormatter;
    }

    public String getTransformerTopic() {
        return transformerTopic;
    }

    public Integer getPartitionNumber() {
        return partitionNumber;
    }

    public String getDataChannelName() {
        return dataChannelName;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public MessageFormatter getMessageFormatter() {
        return messageFormatter;
    }
}
