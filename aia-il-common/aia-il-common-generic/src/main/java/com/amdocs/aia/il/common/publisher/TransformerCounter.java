package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.il.common.stores.scylla.monitor.DbMetrics;
import com.datastax.oss.driver.api.core.CqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TransformerCounter implements IAccumulatorCounter {
    private static final long serialVersionUID = 1351152824219500853L;

    private final Set<String> dataChannels;
    //counters
    private Map<String, AtomicLong> publishCount;
    private Map<String, AtomicLong> upsertWriteCompleted;
    private Map<String, AtomicLong> upsertReadCompleted;
    private Map<String, AtomicLong> recordsProcessed;
    private Map<String, Map<String, AtomicLong>> leadingKeysPerContextPerSourceDataChannel;
    private Map<String, AtomicLong> leadingKeyWithMissingRootCounterPerContext;
    private Map<String, AtomicLong> transformedMessageCounterPerTargetEntity;
    private Map<String, AtomicLong> transformedMessageCounterPerTargetEntityPublished;
    private Map<String, AtomicLong> publishStageFileteredMessagesPerTarget;
    private Map<String, AtomicLong> outputTransactionsCounterPerSubjectArea;
    private Map<String, AtomicLong> inValidRepeatedMessageTargetEntity;
    private AtomicLong numberOfMicroBatchesCompletedPerMB;
    private Map<String, AtomicLong> transactionMessageLengthPerSubjectArea;
    private Map<String, AtomicLong> transactionMessageSizeFromIntermediateKafkaPerMB;
    private Map<String, AtomicLong> publishcallCountPerDataChannelPerMB;




    public TransformerCounter(Set<String> dataChannels) {
        this.dataChannels = dataChannels;
        initCounters();
    }

    @Override
    public CounterType getType() {
        return CounterType.TRANSFORMER;
    }

    @Override
    public void addCounter(Object... args) {
        CounterType counterSubType = CounterType.valueOf(args[0].toString());
        Object param1 = args[1];
        Object param2 = args[2];
        switch (counterSubType) {
            case PUBLISHCOUNT:
                incrementPublishCount((String) param1, (Long) param2);
                break;
            case UPSERTWRITECOMPLETED:
                incrementUpsertCompleted((String) param1, (Long) param2);
                break;
            case UPSERTREADCOMPLETED:
                incrementQueriesCompleted((String) param1, (Long) param2);
                break;
            case RECORDSPROCESSED:
                incrementRecordsProcessed((String) param1, (Long) param2);
                break;
            case LEADINGKEYSPERCONTEXTPERSOURCEDATACHANNEL:
                Object param3 = args[3];
                incrementLeadingKeysPerContextPerSourceDataChannel((String) param1, (String) param2, (Long) param3);
                break;
            case TRANSFORMEDMESSAGECOUNTERPERTARGETENTITY:
                incrementTransformedMessageCounterPerTargetEntity((String) param1, (Long) param2);
                break;
            case TRANSFORMEDMESSAGECOUNTERPERTARGETENTITYPUBLISHED:
                incrementTransformedMessageCounterPerTargetEntityPublished((String) param1, (Long) param2);
                break;

            case LEADINGKEYWITHMISSINGROOTCOUNTERPERCONTEXT:
                incrementLeadingKeyWithMissingRootCounterPerContext((String) param1, (Long) param2);
                break;
            case OUTPUTTRANSACTIONSCOUNTERPERSUBJECTAREA:
                incrementOutputTransactionsCounterPerSubjectArea((String) param1, (Long) param2);
                break;
            case NUMBEROFMICROBATCHESPERMB:
                incrementNumberOfMicroBatchesCompletedPerMB((Long) param2);
                break;
            case TRANSFORMEDMESSAGECOUNTERINVALIDTARGETENTITYVALIDATION:
                incrementInvalidMessageCounterTargetEntityValidation((String) param1, (Long) param2);
                break;
            case TRANSACTIONMESSAGELENGTHFORKAFKA2:
                incrementTransactionMessageLengthPerSubjectAreaPerMB((String) param1, (Long) param2);
                break;
            case TRANSACTIONMESSAGESIZEFROMINTERMEDIATEKAFKA:
                incrementTransactionMessageSizeFromIntermediateKafkaPerMB((String) param1, (Long) param2);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + counterSubType);
        }
    }

    @Override
    public void report(DbMetrics metrics, CqlSession session) {
        publishCount.forEach((key, value) -> metrics.setPublishCountPerDataChannel(key, value.get()));
        upsertReadCompleted.forEach((key, value) -> metrics.setUpsertReadCompletedPerDataChannel(key, value.get()));
        upsertWriteCompleted.forEach((key, value) -> metrics.setUpsertWriteCompletedPerDataChannel(key, value.get()));
        recordsProcessed.forEach((key, value) -> metrics.setRecordsProcessedPerDataChannel(key, value.get()));
        metrics.setLeadingKeysCounterPerContextPerSourceDataChannelPerMB(leadingKeysPerContextPerSourceDataChannel);
        for (final Map.Entry<String, AtomicLong> entry : transformedMessageCounterPerTargetEntity.entrySet()) {
            String entity = entry.getKey();
            long diff = entry.getValue().longValue();
            if (transformedMessageCounterPerTargetEntityPublished.containsKey(entity)) {
                diff = diff - transformedMessageCounterPerTargetEntityPublished.get(entity).longValue();
            }
            incrementPublishStageFileteredMessagesPerTarget(entity, diff);
        }
        metrics.setTransformedMessageCounterPerTargetEntity(publishStageFileteredMessagesPerTarget);
        metrics.setInvalidMessageCounterPerTargetEntityPerMB(inValidRepeatedMessageTargetEntity);
        metrics.setOutputTransactionsCounterPerSubjectArea(outputTransactionsCounterPerSubjectArea);
        metrics.setNumberOfMicroBatchesCompletedPerMB(numberOfMicroBatchesCompletedPerMB);
        metrics.setLeadingKeyWithMissingRootCounterPerContext(leadingKeyWithMissingRootCounterPerContext);
        metrics.setTransactionMessageLengthPerSubjectArea(transactionMessageLengthPerSubjectArea);
        metrics.setTransactionMessageSizeFromIntermediateKafka(transactionMessageSizeFromIntermediateKafkaPerMB);
        metrics.setPublishcallCountPerDataChannelPerMB(publishcallCountPerDataChannelPerMB);

    }

    @Override
    public void clear() {
        initCounters();
    }

    public void incrementPublishCount(String key, Long newVal) {
        if (publishCount.get(key) != null) {
            publishCount.get(key).getAndAccumulate(newVal, Long::sum);
        }
    }

    public void incrementUpsertCompleted(String key, Long newVal) {
        if (upsertWriteCompleted.get(key) != null) {
            upsertWriteCompleted.get(key).getAndAccumulate(newVal, Long::sum);
        }
    }

    public void incrementQueriesCompleted(String key, Long newVal) {
        if (upsertReadCompleted.get(key) != null) {
            upsertReadCompleted.get(key).getAndAccumulate(newVal, Long::sum);
        }
    }

    public void incrementRecordsProcessed(String key, Long newVal) {
        if (recordsProcessed.get(key) != null) {
            recordsProcessed.get(key).getAndAccumulate(newVal, Long::sum);
        }
    }

    public void incrementLeadingKeysPerContextPerSourceDataChannel(String datachannelName, String contextName, long newVal) {
        final Map<String, AtomicLong> contextMap;
        if (leadingKeysPerContextPerSourceDataChannel.containsKey(datachannelName)) {
            contextMap = leadingKeysPerContextPerSourceDataChannel.get(datachannelName);
        } else {
            contextMap = new ConcurrentHashMap<>();
            leadingKeysPerContextPerSourceDataChannel.put(datachannelName, contextMap);
        }
        if (!contextMap.containsKey(contextName)) {
            contextMap.put(contextName, new AtomicLong());
        }
        contextMap.get(contextName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementTransformedMessageCounterPerTargetEntity(String entityName, long newVal) {
        if (!transformedMessageCounterPerTargetEntity.containsKey(entityName)) {
            transformedMessageCounterPerTargetEntity.put(entityName, new AtomicLong());
        }
        transformedMessageCounterPerTargetEntity.get(entityName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementTransformedMessageCounterPerTargetEntityPublished(String entityName, long newVal) {
        if (!transformedMessageCounterPerTargetEntityPublished.containsKey(entityName)) {
            transformedMessageCounterPerTargetEntityPublished.put(entityName, new AtomicLong());
        }
        transformedMessageCounterPerTargetEntityPublished.get(entityName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementInvalidMessageCounterTargetEntityValidation(String entityName, long newVal) {
        if (!inValidRepeatedMessageTargetEntity.containsKey(entityName)) {
            inValidRepeatedMessageTargetEntity.put(entityName, new AtomicLong());
        }
        inValidRepeatedMessageTargetEntity.get(entityName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementPublishStageFileteredMessagesPerTarget(String entityName, long newVal) {
        if (!publishStageFileteredMessagesPerTarget.containsKey(entityName)) {
            publishStageFileteredMessagesPerTarget.put(entityName, new AtomicLong());
        }
        publishStageFileteredMessagesPerTarget.get(entityName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementLeadingKeyWithMissingRootCounterPerContext(String contextName, long newVal) {
        if (!leadingKeyWithMissingRootCounterPerContext.containsKey(contextName)) {
            leadingKeyWithMissingRootCounterPerContext.put(contextName, new AtomicLong());
        }
        leadingKeyWithMissingRootCounterPerContext.get(contextName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementOutputTransactionsCounterPerSubjectArea(String subArea, long newVal) {
        if (!outputTransactionsCounterPerSubjectArea.containsKey(subArea)) {
            outputTransactionsCounterPerSubjectArea.put(subArea, new AtomicLong());
        }
        outputTransactionsCounterPerSubjectArea.get(subArea).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementTransactionMessageLengthPerSubjectAreaPerMB(String subArea, long newVal) {
        if (!transactionMessageLengthPerSubjectArea.containsKey(subArea)) {
            transactionMessageLengthPerSubjectArea.put(subArea, new AtomicLong());
        }
        transactionMessageLengthPerSubjectArea.get(subArea).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementTransactionMessageSizeFromIntermediateKafkaPerMB(String subArea, long newVal) {
        if (!transactionMessageSizeFromIntermediateKafkaPerMB.containsKey(subArea)) {
            transactionMessageSizeFromIntermediateKafkaPerMB.put(subArea, new AtomicLong());
        }
        transactionMessageSizeFromIntermediateKafkaPerMB.get(subArea).getAndAccumulate(newVal, Long::sum);
    }
    

    public void incrementNumberOfMicroBatchesCompletedPerMB(long newVal) {
        numberOfMicroBatchesCompletedPerMB.getAndAccumulate(newVal, Long::sum);
    }

    private void initCounters() {
        publishCount = getFullMap();
        upsertWriteCompleted = getFullMap();
        upsertReadCompleted = getFullMap();
        recordsProcessed = getFullMap();
        leadingKeysPerContextPerSourceDataChannel = new ConcurrentHashMap<>();
        leadingKeyWithMissingRootCounterPerContext = new ConcurrentHashMap<>();
        transformedMessageCounterPerTargetEntity = new ConcurrentHashMap<>();
        transformedMessageCounterPerTargetEntityPublished = new ConcurrentHashMap<>();
        publishStageFileteredMessagesPerTarget = new ConcurrentHashMap<>();
        outputTransactionsCounterPerSubjectArea = new ConcurrentHashMap<>();
        numberOfMicroBatchesCompletedPerMB = new AtomicLong();
        inValidRepeatedMessageTargetEntity = new ConcurrentHashMap<>();
        transactionMessageLengthPerSubjectArea = new ConcurrentHashMap<>();
        transactionMessageSizeFromIntermediateKafkaPerMB = new ConcurrentHashMap<>();
        publishcallCountPerDataChannelPerMB = new ConcurrentHashMap<>();

    }

    private Map<String, AtomicLong> getFullMap() {
        Map<String, AtomicLong> map = new HashMap<>();
        dataChannels.forEach(one -> map.put(one, new AtomicLong()));
        return map;
    }
}