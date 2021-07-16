package com.amdocs.aia.il.common.stores.scylla.monitor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public interface DbMetrics {

    void setInflightRequest(long count);

    void incrementNumOfUnavailableErrors(int count);

    void incrementNumOfReadTimeouts(int count);

    void incrementNumOfWriteTimeouts(int count);

    void setPublishCountPerDataChannel(String dataChannel, long count);

    void setUpsertReadCompletedPerDataChannel(String dataChannel, long count);

    void setUpsertWriteCompletedPerDataChannel(String dataChannel, long count);

    void setLeadingKeysQueryCountPerDataChannel(String dataChannel, long count);

    void setRecordsProcessedPerDataChannel(String dataChannel, long count);

    void setLeadingKeysCounterPerContextPerSourceDataChannelPerMB(Map<String, Map<String, AtomicLong>> leadingKeysPerContextPerSourceDataChannel);

    void setTransformedMessageCounterPerTargetEntity(Map<String, AtomicLong> transformedMessageCounterPerTargetEntity);

    void setLeadingKeyWithMissingRootCounterPerContext(Map<String, AtomicLong> leadingKeyWithMissingRootCounterPerContext);

    void setOutputTransactionsCounterPerSubjectArea(Map<String, AtomicLong> outputTransactionsCounterPerSubjectArea);

    void setUpsertcallCountPerDataChannelPerMB(Map<String, AtomicLong> upsertcallCountPerDataChannelPerMB);

    void setPublishcallCountPerDataChannelPerMB(Map<String, AtomicLong> publishcallCountPerDataChannelPerMB);

    void setInputMessagesCountPerDataChannelPerMB(Map<String, AtomicLong> inputMessagesCountPerDataChannelPerMB);

    void setNumberOfConflictsPerDatachannelPerMB(Map<String, AtomicLong> numberOfConflictsPerDatachannelPerMB);

    void setNumberOfIncompleteTransactionsPerDatachannelPerMB(Map<String, AtomicLong> numberOfIncompleteTransactionsPerDatachannelPerMB);

    void setInputMessagesCountPerTableDataChannelPerMB(Map<String, AtomicLong> inputMessagesCountPerTableDataChannel);

    void setUpsertMainDataCounterPerTablePerMB(Map<String, AtomicLong> upsertMainDataCounterPerTablePerMB);

    void setUpsertRelationalDataCounterPerTablePerMB(Map<String, AtomicLong> upsertRelationalDataCounterPerTablePerMB);

    void setUpsertFilteredMessagesCounterPerTablePerMB(Map<String, AtomicLong> upsertFilteredMessagesCounterPerTablePerMB);

    void setUpsertFailedMessagesCounterPerTablePerMB(Map<String, AtomicLong> upsertFailedMessagesCounterPerTablePerMB);

    void setUpsertStageFilterCounterPerTablePerMB(Map<String, AtomicLong> upsertStageFilterCounterPerTable);

    void setDeleteCounterPerTablePerMB(Map<String, AtomicLong> deleteCounterPerTablePerMB);

    void setMessageSentToIntermediateKafkaCounterPerSubAreaPerMB(Map<String, AtomicLong> messageSentToIntermediateKafkaCounterPerSubAreaPerMB);

    void setPartialTrxMessagesCountPerDataChannelPerMB(Map<String, AtomicLong> partialTrxMessagesCountPerDataChannel);

    void setPartialTrxMessagesTotalReadtimePerDataChannelPerMB(Map<String, AtomicLong> partialTrxMessagesTotalReadtimePerDataChannel);

    void setPartialTrxMessagesTotalWritetimePerDataChannelPerMB(Map<String, AtomicLong> partialTrxMessagesTotalWritetimePerDataChannel);

    void setNumberOfMicroBatchesCompletedPerMB(AtomicLong numberOfMicroBatchesCompleted);

    void setInvalidMessageCounterPerTargetEntityPerMB(Map<String, AtomicLong> invalidMessageCounterPerTargetEntity);

    void setScyllaErrorFlagPerDataChannel(double amount, String dataChannelName);

    void incrementCounterPerScyllaExceptionType(double amount, String exceptionType);

    void incrementCounterPerKafkaExceptionType(double amount, String exceptionType);

    void setKafkaErrorFlag(int amount);

    void setKafkaBatchRecordCount(long count);

    void setKafkaBatchExecutionDuration(long time);
    void incrementCounterPerExceptionType(final double amount, final String exceptionType);

    void setTransactionMessageLengthPerDataChannelPerMB(Map<String, AtomicLong> transactionMessageLengthPerDataChannelPerMB);

    void setTransactionMessageLengthPerSubjectArea(Map<String, AtomicLong> transactionMessageLengthPerSubjectArea);

    void setTransactionMessageLengthPerIntermediateKafkaPerMB(Map<String, AtomicLong> transactionMessageLengthPerIntermediateKafka);

    void setTransactionMessageSizeFromIntermediateKafka(Map<String, AtomicLong> transactionMessageSizeFromIntermediateKafka);


}