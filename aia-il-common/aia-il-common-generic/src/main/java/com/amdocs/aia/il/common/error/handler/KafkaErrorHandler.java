package com.amdocs.aia.il.common.error.handler;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.common.serialization.messages.Transaction;
import com.amdocs.aia.il.common.constant.RTPConstants;
import com.amdocs.aia.il.common.model.configuration.properties.KafkaConfiguration;
import com.amdocs.aia.il.common.publisher.*;
import com.amdocs.aia.il.common.stores.KeyColumn;
import com.amdocs.aia.il.common.utils.ClassLoaderUtils;
import com.amdocs.aia.il.common.utils.ConversionUtils;
import com.amdocs.aia.il.common.utils.SneakyThrowUtil;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Future;

@Component
public class KafkaErrorHandler implements Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaErrorHandler.class);

    private static Set<String> allowedKafkaExceptionTypes;
    private ErrorHandler errorHandler;
    private Producer producer;
    private static final long REPLICATOR_TRANSACTION_HEADER_SIZE = 730L;
    private static final long COMPUTE_LEADING_KEY_TRANSACTION_HEADER_SIZE = 545L;
    private static final long TRANSFORMER_TRANSACTION_HEADER_SIZE = 350L;
    private static final List<String> LARGE_RECORD_EXCEPTION_LIST = Arrays.asList("RecordBatchTooLargeException", "RecordTooLargeException");
    private static final int NON_RETRYABLE_EXCEPTION_PROCESS_LIMIT = 5;
    private static final int PRODUCER_RECORD_DEFAULT_SIZE = 100;

    private KafkaConfiguration kafkaConfiguration;

    @Autowired
    public KafkaErrorHandler(KafkaConfiguration kafkaConfiguration) {
        this.kafkaConfiguration = kafkaConfiguration;
    }

    static {
        try {
            allowedKafkaExceptionTypes = ClassLoaderUtils.getKafkaExceptionType();
        } catch (Exception e) {
            SneakyThrowUtil.sneakyThrow(e);
        }
    }

    public boolean isKafkaException(Class<?> exceptionType) {
        return allowedKafkaExceptionTypes.contains(exceptionType.getName());
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Autowired
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public <T> T handle(Checkpoint<T> checkpoint) {
        setErrorMetricFlag(checkpoint);
        if (checkpoint.getProducer() != null) {
            publishRetry(checkpoint);
        } else if (checkNonRetryableException(checkpoint)) {
            setExceptionMetricCounter(checkpoint);
            clearFailedTasks(checkpoint);
            consumeRetry(checkpoint);
        }
        return null;
    }

    @Override
    public <T> T runWithRetriesAndDelay(Checkpoint<T> checkpoint) {
        return null;
    }

    @Override
    public <T> T runWithRetriesAndDelay(Checkpoint<T> checkpoint, Set<Class<? extends Exception>> allowedExceptionTypes) {
        return null;
    }

    @Override
    public <T> void runWithRetriesAndDelay(Checkpoint<T> checkpoint, ErrorHandler.ThrowingOperation f) {
        return;
    }

    public <T> T consumeRetry(Checkpoint<T> checkpoint) {
        T output = null;
        try {
            LOGGER.warn("Kafka consumer retry mechanism is running, retryCount[{}], retryDelay[{}], threadName[{}], methodName[{}]", checkpoint.getRetryCounter() + 1, checkpoint.getRetryDelay(), checkpoint.getThreadName(), checkpoint.getMethodName());
            sleep(Duration.ofSeconds(checkpoint.getRetryDelay()).toMillis());
            checkpoint.setRetryCounter(checkpoint.getRetryCounter() + 1);
            if (checkpoint.getFunction() != null) {
                output = checkpoint.getFunction().execute();
            } else {
                checkpoint.getOperation().execute();
            }
            setErrorMetricFlag(checkpoint);
        } catch (Exception e) {
            SneakyThrowUtil.sneakyThrow(e);
        }
        return output;
    }

    private <T> boolean checkNonRetryableException(Checkpoint<T> checkpoint) {
        for (Map.Entry<String, ExecutionState> executionStatesMap : checkpoint.getExecutionStates().entrySet()) {
            for (Map.Entry<Object, ErrorType> failedTasks : executionStatesMap.getValue().getFailedTasks().entrySet()) {
                if (ClassLoaderUtils.getNonRetryableKafkaExceptionType().contains(failedTasks.getValue().getExceptionType().getName()) && checkpoint.getRetryCounter() > NON_RETRYABLE_EXCEPTION_PROCESS_LIMIT) {
                    LOGGER.info("Non-retryable kafka exception process limit is over, ExceptionType : {}", failedTasks.getValue().getExceptionType().getName());
                    return false;
                }
                LOGGER.warn("Non-retryable kafka exception occurred, please take appropriate action on this. ExceptionType : {}", failedTasks.getValue().getExceptionType().getName());
            }
        }
        return true;
    }

    public <T> void publishRetry(Checkpoint<T> checkpoint) {
        try {
            LOGGER.warn("Kafka producer retry mechanism is running, retryCount[{}], retryDelay[{}]", checkpoint.getRetryCounter() + 1, checkpoint.getRetryDelay());
            checkpoint.setRetryCounter(checkpoint.getRetryCounter() + 1);
            producer = checkpoint.getProducer();
            List<Object> failedMessages = new ArrayList<>();
            populateFailedMessages(checkpoint, failedMessages);
            publish(failedMessages, checkpoint);
            checkpoint.setErrorFlag(false);
            setErrorMetricFlag(checkpoint);
            return;
        } catch (Exception e) {
            if ((!ClassLoaderUtils.getKafkaExceptionType().contains(ConversionUtils.getExceptionType(e).getName()))) {
                LOGGER.error("Error occurred in kafka producer retry mechanism, {}", e);
                return;
            }
            sleep(Duration.ofSeconds(checkpoint.getRetryDelay()).toMillis());
            publishRetry(checkpoint);
        }
    }

    private <T> void populateFailedMessages(Checkpoint<T> checkpoint, List<Object> failedMessages) throws Exception {
        for (Map.Entry<String, ExecutionState> executionStatesMap : checkpoint.getCurrentExecutionStates().entrySet()) {
            for (Map.Entry<Object, ErrorType> failedTasks : executionStatesMap.getValue().getFailedTasks().entrySet()) {
                errorHandler.getMetrics().incrementCounterPerKafkaExceptionType(1, failedTasks.getValue().getExceptionType().getSimpleName());
                checkTransactionAndAddMessage(checkpoint, failedMessages, failedTasks);
            }
        }
    }

    private <T> void checkTransactionAndAddMessage(Checkpoint<T> checkpoint, List<Object> failedMessages, Map.Entry<Object, ErrorType> failedTasks) throws Exception {
        if (LARGE_RECORD_EXCEPTION_LIST.contains(failedTasks.getValue().getExceptionType().getSimpleName())) {
            addFailedMessages(failedTasks.getKey(), failedMessages);
        } else if (ClassLoaderUtils.getNonRetryableKafkaExceptionType().contains(failedTasks.getValue().getExceptionType().getName())) {
            if (checkpoint.getRetryCounter() > NON_RETRYABLE_EXCEPTION_PROCESS_LIMIT) {
                LOGGER.info("Non-retryable exception process limit is over, ExceptionType : {}", failedTasks.getValue().getExceptionType().getName());
                checkpoint.addFailedTask(RTPConstants.KAFKA_PUBLISH_TRANSACTION_FAILED_KEY, failedTasks.getKey(), failedTasks.getValue().getErrorMessage(), failedTasks.getValue().getExceptionType());
            } else {
                LOGGER.warn("Non-retryable exception occurred, please take appropriate action on this. ExceptionType : {}", failedTasks.getValue().getExceptionType().getName());
            }

        } else {
            failedMessages.add(failedTasks.getKey());
        }
    }

    private void addFailedMessages(Object failedMessage, List<Object> failedMessages) throws Exception { //NOSONAR
        if (failedMessage instanceof ReplicatorProducerRecord) {
            ReplicatorProducerRecord replicatorProducerRecord = (ReplicatorProducerRecord) failedMessage;
            long headerSize = REPLICATOR_TRANSACTION_HEADER_SIZE + ((replicatorProducerRecord.getKafkaPublishTransaction().getReplicatorOutputMessageList().size()) * 70);
            long repeatedMsgCount = getReplicatorRepeatedMsgCount(replicatorProducerRecord);
            long tranBreakCount = getTranMsgBreakCount(headerSize, getKafkaPublishTransactionByteArray(replicatorProducerRecord.getKafkaPublishTransaction()).length, repeatedMsgCount);
            if (tranBreakCount > 0 && repeatedMsgCount != tranBreakCount) {
                breakReplicatorTransaction(replicatorProducerRecord, failedMessages, tranBreakCount);
            } else {
                LOGGER.warn("Unable to split transaction due to tranBreakCount is less than 0 or repeatedMsgCount and tranBreakCount are same");
                failedMessages.add(failedMessage);
            }
        } else if (failedMessage instanceof ComputeLeadingProducerRecord) {
            ComputeLeadingProducerRecord computeLeadingProducerRecord = (ComputeLeadingProducerRecord) failedMessage;
            LOGGER.debug("TX before Split :: {}", computeLeadingProducerRecord.getKafkaBulkPublishTransaction());
            long repeatedMsgCount = getComputeLeadingKeyRepeatedMsgCount(computeLeadingProducerRecord);
            long tranBreakCount = getTranMsgBreakCount(COMPUTE_LEADING_KEY_TRANSACTION_HEADER_SIZE, getKafkaPublishTransactionByteArray(computeLeadingProducerRecord.getKafkaBulkPublishTransaction()).length, repeatedMsgCount);
            if (tranBreakCount > 0 && repeatedMsgCount != tranBreakCount) {
                breakComputeLeadingKeyTransaction(computeLeadingProducerRecord, failedMessages, tranBreakCount);
            } else {
                LOGGER.warn("Unable to split transaction due to tranBreakCount is less than  0 or repeatedMsgCount and tranBreakCount are same");
                failedMessages.add(failedMessage);
            }
        } else if (failedMessage instanceof TransformerProducerRecord) {
            breakTransformerTransaction((TransformerProducerRecord) failedMessage, failedMessages);
        }
        LOGGER.debug("Number of failed transaction after split: {}", failedMessages.size());
    }

    private void breakReplicatorTransaction(ReplicatorProducerRecord replicatorProducerRecord, List<Object> failedMessages, long tranBreakCount) {
        LOGGER.debug("Number of failed transaction : {}", failedMessages.size());
        int count = 0;
        KafkaPublishTransaction kafkaPublishTransactionNew = new KafkaPublishTransaction();
        List<ReplicatorOutputMessage> replicatorOutputMessageList = new ArrayList<>();
        for (ReplicatorOutputMessage replicatorOutputMessage : replicatorProducerRecord.getKafkaPublishTransaction().getReplicatorOutputMessageList()) {
            Set<KeyColumn> columnSet = new HashSet<>();
            Map<KeyColumn, Map<String, String>> leadingTimeId = new HashMap<>();
            for (KeyColumn keyColumn : replicatorOutputMessage.getKeyColumnSet()) {
                if (tranBreakCount > count) {
                    columnSet.add(keyColumn);
                    leadingTimeId.put(keyColumn, replicatorOutputMessage.getLeadingTimeId().get(keyColumn));
                    count++;
                } else {
                    ReplicatorOutputMessage replicatorOutputMessageNew = new ReplicatorOutputMessage(replicatorOutputMessage.getContextName(), columnSet, replicatorOutputMessage.getTransactionIDs(), System.currentTimeMillis(), leadingTimeId);
                    replicatorOutputMessageList.add(replicatorOutputMessageNew);
                    kafkaPublishTransactionNew.setReplicatorOutputMessageList(replicatorOutputMessageList);
                    ReplicatorProducerRecord producerRecord = new ReplicatorProducerRecord(kafkaPublishTransactionNew, replicatorProducerRecord.getDataChannelName(), replicatorProducerRecord.getTransformerTopic(), replicatorProducerRecord.getPartitionNumber());
                    failedMessages.add(producerRecord);
                    columnSet = new HashSet<>();
                    leadingTimeId = new HashMap<>();
                    kafkaPublishTransactionNew = new KafkaPublishTransaction();
                    replicatorOutputMessageList = new ArrayList<>();
                    columnSet.add(keyColumn);
                    leadingTimeId.put(keyColumn, replicatorOutputMessage.getLeadingTimeId().get(keyColumn));
                    count = 1;
                }
            }
            ReplicatorOutputMessage replicatorOutputMessageNew = new ReplicatorOutputMessage(replicatorOutputMessage.getContextName(), columnSet, replicatorOutputMessage.getTransactionIDs(), System.currentTimeMillis(), leadingTimeId);
            replicatorOutputMessageList.add(replicatorOutputMessageNew);
        }
        kafkaPublishTransactionNew.setReplicatorOutputMessageList(replicatorOutputMessageList);
        ReplicatorProducerRecord producerRecord = new ReplicatorProducerRecord(kafkaPublishTransactionNew, replicatorProducerRecord.getDataChannelName(), replicatorProducerRecord.getTransformerTopic(), replicatorProducerRecord.getPartitionNumber());
        failedMessages.add(producerRecord);
    }

    private void breakComputeLeadingKeyTransaction(ComputeLeadingProducerRecord computeLeadingProducerRecord, List<Object> failedMessages, long tranBreakCount) throws Exception { //NOSONAR
        LOGGER.debug("Number of failed compute leading key transaction : {}", failedMessages.size());
        int initialCount = 0;
        KafkaBulkPublishTransaction kafkaBulkPublishTransactionNew = new KafkaBulkPublishTransaction();
        Set<KeyColumn> columnSet = new HashSet<>();
        Map<KeyColumn, Map<String, String>> leadingTimeId = new HashMap<>();
        String contextName = "";
        for (KeyColumn keyColumn : computeLeadingProducerRecord.getKafkaBulkPublishTransaction().getLeadingEntityKeys()) {
            if (tranBreakCount > initialCount) {
                columnSet.add(keyColumn);
                leadingTimeId.put(keyColumn, computeLeadingProducerRecord.getKafkaBulkPublishTransaction().getLeadingTimeId().get(keyColumn));
                contextName = computeLeadingProducerRecord.getKafkaBulkPublishTransaction().getContextName();
                initialCount++;
            } else {
                kafkaBulkPublishTransactionNew.setLeadingEntityKeys(columnSet);
                kafkaBulkPublishTransactionNew.setLeadingTimeId(leadingTimeId);
                kafkaBulkPublishTransactionNew.setContextName(contextName);
                ComputeLeadingProducerRecord producerRecord = new ComputeLeadingProducerRecord(kafkaBulkPublishTransactionNew, computeLeadingProducerRecord.getDataChannelName(), computeLeadingProducerRecord.getTransformerTopic(), computeLeadingProducerRecord.getPartitionNumber());
                failedMessages.add(producerRecord);
                columnSet = new HashSet<>();
                leadingTimeId = new HashMap<>();
                kafkaBulkPublishTransactionNew = new KafkaBulkPublishTransaction();
                columnSet.add(keyColumn);
                leadingTimeId.put(keyColumn, computeLeadingProducerRecord.getKafkaBulkPublishTransaction().getLeadingTimeId().get(keyColumn));
                initialCount = 1;
            }
        }
        kafkaBulkPublishTransactionNew.setLeadingEntityKeys(columnSet);
        kafkaBulkPublishTransactionNew.setLeadingTimeId(leadingTimeId);
        kafkaBulkPublishTransactionNew.setContextName(computeLeadingProducerRecord.getKafkaBulkPublishTransaction().getContextName());
        ComputeLeadingProducerRecord producerRecord = new ComputeLeadingProducerRecord(kafkaBulkPublishTransactionNew, computeLeadingProducerRecord.getDataChannelName(), computeLeadingProducerRecord.getTransformerTopic(), computeLeadingProducerRecord.getPartitionNumber());
        failedMessages.add(producerRecord);
    }

    private Future<Object> sendReplicatorMessages(ReplicatorProducerRecord producerRecord, Checkpoint checkpoint) throws IOException {
        ProducerRecord<String, byte[]> message = new ProducerRecord<>(producerRecord.getTransformerTopic(), producerRecord.getPartitionNumber(), producerRecord.getDataChannelName(), getKafkaPublishTransactionByteArray(producerRecord.getKafkaPublishTransaction()));
        return sendMessage(message, checkpoint, producerRecord);
    }

    private Future<Object> sendComputeLeadingKeyMessages(ComputeLeadingProducerRecord producerRecord, Checkpoint checkpoint) throws Exception { //NOSONAR
        ProducerRecord<String, byte[]> message = new ProducerRecord<>(producerRecord.getTransformerTopic(), producerRecord.getPartitionNumber(), producerRecord.getDataChannelName(), getKafkaPublishTransactionByteArray(producerRecord.getKafkaBulkPublishTransaction()));
        return sendMessage(message, checkpoint, producerRecord);
    }

    private static int getReplicatorRepeatedMsgCount(ReplicatorProducerRecord replicatorProducerRecord) {
        int msgCount = 0;
        for (ReplicatorOutputMessage replicatorOutputMessage : replicatorProducerRecord.getKafkaPublishTransaction().getReplicatorOutputMessageList()) {
            for (KeyColumn keyColumn : replicatorOutputMessage.getKeyColumnSet()) {
                msgCount++;
            }
        }
        return msgCount;
    }

    private static int getComputeLeadingKeyRepeatedMsgCount(ComputeLeadingProducerRecord computeLeadingProducerRecord) {
        return (computeLeadingProducerRecord.getKafkaBulkPublishTransaction().getLeadingEntityKeys().size());
    }

    private void breakTransformerTransaction(TransformerProducerRecord transformerProducerRecord, List<Object> failedMessages) {
        LOGGER.debug("Number of failed transaction : {}", failedMessages.size());
        Transaction transaction = transformerProducerRecord.getTransaction();
        int repeatedMsgCount = transaction.getRepeatedValues().values().stream().mapToInt(Collection::size).sum();
        long transactionSize = transformerProducerRecord.getMessageFormatter().buildTransactionMessage(transaction, false).length;
        long tranBreakCount = getTranMsgBreakCount(TRANSFORMER_TRANSACTION_HEADER_SIZE, transactionSize, repeatedMsgCount);
        if (tranBreakCount > 0 && repeatedMsgCount != tranBreakCount) {
            addSplitTransaction(transformerProducerRecord, transaction, failedMessages, tranBreakCount);
        } else {
            LOGGER.warn("Unable to split transaction due to tranBreakCount is less than 0");
            failedMessages.add(transformerProducerRecord);
        }
    }

    private Future<Object> sendTransformerMessages(TransformerProducerRecord recordPublish, Checkpoint checkpoint) {
        byte[] value = recordPublish.getMessageFormatter().buildTransactionMessage(recordPublish.getTransaction(), false);
        ProducerRecord<String, byte[]> message = new ProducerRecord<>(recordPublish.getTransformerTopic(), recordPublish.getPartitionNumber(), recordPublish.getDataChannelName(), value);
        return sendMessage(message, checkpoint, recordPublish);
    }

    private void addSplitTransaction(TransformerProducerRecord transformerProducerRecord, Transaction transaction, List<Object> failedMessages, long tranBreakCount) {
        int initialCounterSize = 0;
        Transaction splitTransaction = null;
        for (Map.Entry<String, List<RepeatedMessage>> entry : transaction.getRepeatedValues().entrySet()) {

            if (splitTransaction == null) {
                splitTransaction = new Transaction();
                transactionValues(transaction, splitTransaction);
            }
            for (RepeatedMessage repeatedMessage : entry.getValue()) {
                if (initialCounterSize < tranBreakCount) {
                    initialCounterSize++;
                    splitTransaction.putRepeatedValue(getKey(entry.getKey()), repeatedMessage);
                } else {
                    TransformerProducerRecord transformerProducerRecordSplitObj = new TransformerProducerRecord(transformerProducerRecord.getTransformerTopic(), transformerProducerRecord.getPartitionNumber(), transformerProducerRecord.getDataChannelName(), splitTransaction, transformerProducerRecord.getMessageFormatter());
                    failedMessages.add(transformerProducerRecordSplitObj);
                    splitTransaction = new Transaction();
                    transactionValues(transaction, splitTransaction);
                    splitTransaction.putRepeatedValue(getKey(entry.getKey()), repeatedMessage);
                    initialCounterSize = 1;
                }
            }
        }
        TransformerProducerRecord nonTransientProducerRecordSplitObj = new TransformerProducerRecord(transformerProducerRecord.getTransformerTopic(), transformerProducerRecord.getPartitionNumber(), transformerProducerRecord.getDataChannelName(), splitTransaction, transformerProducerRecord.getMessageFormatter());
        failedMessages.add(nonTransientProducerRecordSplitObj);
    }

    private static String getKey(String key) {
        if (key.startsWith("f_")) {
            return key.substring(2);
        }
        return key;
    }

    private static void transactionValues(Transaction transaction, Transaction splitTransaction) {
        splitTransaction.putValue("startTransaction", transaction.getValue("startTransaction"));
        splitTransaction.putValue("transactionId", transaction.getValue("transactionId"));
        splitTransaction.putValue("endTransaction", transaction.getValue("endTransaction"));
    }

    private long getTranMsgBreakCount(long txHeaderSize, long messageSize, long totalMsgCount) {
        long tranBrkCount = 0;
        double msgBreakCount = (double) (messageSize - txHeaderSize) / (kafkaConfiguration.getProducer().getMaxRequestSize() - txHeaderSize - PRODUCER_RECORD_DEFAULT_SIZE);
        if (totalMsgCount > msgBreakCount) {
            tranBrkCount = Math.round(totalMsgCount / msgBreakCount);
        }
        LOGGER.debug("tranBrkCount : {}, totalMsgCount : {}, txHeaderSize : {}, messageSize : {}, msgBreakCount : {}", tranBrkCount, totalMsgCount, txHeaderSize, messageSize, msgBreakCount);
        return tranBrkCount;
    }

    private Future<Object> sendMessage(ProducerRecord<String, byte[]> message, Checkpoint checkpoint, Object retryMessage) {
        Future<Object> objectFuture = producer.send(message, (metadata, e) -> {
            if (e != null) {
                LOGGER.error("Failed to send a message to Kafka from kafka producer retry mechanism {}", e);
                checkpoint.setErrorFlag(true);
                checkpoint.addFailedTask(RTPConstants.KAFKA_PUBLISH_TRANSACTION_FAILED_KEY, retryMessage, e.getMessage(), e.getClass());
            } else {
                LOGGER.debug("Send message to Kafka producer in retry mechanism");
            }
        });
        return objectFuture;
    }

    private <T> void publish(List<Object> failedMessages, Checkpoint<T> checkpoint) throws Exception { //NOSONAR
        List<Future<Object>> objectFutureList = new ArrayList<>();
        for (Object failedMessage : failedMessages) {
            if (failedMessage instanceof TransformerProducerRecord) {
                objectFutureList.add(sendTransformerMessages((TransformerProducerRecord) failedMessage, checkpoint));
            } else if (failedMessage instanceof ComputeLeadingProducerRecord) {
                objectFutureList.add(sendComputeLeadingKeyMessages((ComputeLeadingProducerRecord) failedMessage, checkpoint));
            } else if (failedMessage instanceof ReplicatorProducerRecord){
                ReplicatorProducerRecord replicatorProducerRecord = (ReplicatorProducerRecord) failedMessage;
                updateTimestamp(replicatorProducerRecord.getKafkaPublishTransaction());
                objectFutureList.add(sendReplicatorMessages(replicatorProducerRecord, checkpoint));
            }
        }

        for (Future<Object> objectFuture : objectFutureList) {
            try {
                objectFuture.get();
            } catch (Exception e) {
                throw e; //NOSONAR
            }
        }
    }

    private static byte[] getKafkaPublishTransactionByteArray(Object kafkaObject) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream objectWriter = new ObjectOutputStream(os);
        objectWriter.writeObject(kafkaObject);
        return os.toByteArray();
    }

    private static void updateTimestamp(KafkaPublishTransaction kafkaPublishTransaction) {
        kafkaPublishTransaction.getReplicatorOutputMessageList().forEach(f -> f.setTimeStamp(System.currentTimeMillis()));
    }

    private static void sleep(long delay) {
        long startTime = System.currentTimeMillis();
        while (true) {
            if ((System.currentTimeMillis() - startTime) >= delay) {
                break;
            }
        }
    }

    private <T> void setExceptionMetricCounter(Checkpoint<T> checkpoint) {
        Optional<ExecutionState> executionState = checkpoint.getExecutionStates().entrySet().stream().map(Map.Entry::getValue).findFirst();
        if (executionState.isPresent()) {
            for (Map.Entry<Object, ErrorType> failedTaskMap : executionState.get().getFailedTasks().entrySet()) {
                errorHandler.getMetrics().incrementCounterPerKafkaExceptionType(1, failedTaskMap.getValue().getExceptionType().getSimpleName());
                break; //NOSONAR
            }
        }
    }

    private static <T> void clearFailedTasks(Checkpoint<T> checkpoint) {
        for (Map.Entry<String, ExecutionState> executionStatesMap : checkpoint.getExecutionStates().entrySet()) {
            checkpoint.setErrorFlag(false);
            checkpoint.getExecutionStates().get(executionStatesMap.getKey()).setFailedTasks(Collections.emptyMap());
        }
    }

    private <T> void setErrorMetricFlag(Checkpoint<T> checkpoint) {
        if (checkpoint.isErrorFlag()) {
            if (checkpoint.getRetryCounter() == 0) {
                errorHandler.getMetrics().setKafkaErrorFlag(1);
            }
        } else {
            errorHandler.getMetrics().setKafkaErrorFlag(0);
        }
    }

}
