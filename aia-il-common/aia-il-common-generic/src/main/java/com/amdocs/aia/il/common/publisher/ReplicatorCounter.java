package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.il.common.stores.scylla.monitor.DbMetrics;
import com.datastax.oss.driver.api.core.CqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ReplicatorCounter implements IAccumulatorCounter {
    private static final long serialVersionUID = 1351152824219500853L;

    private final Set<String> dataChannels;
    //Counters
    private Map<String, AtomicLong> publishCount;
    private Map<String, AtomicLong> upsertWriteCompleted;
    private Map<String, AtomicLong> upsertReadCompleted;
    private Map<String, AtomicLong> leadingKeysQueryCount;
    private Map<String, AtomicLong> recordsProcessed;
    private Map<String, AtomicLong> upsertcallCountPerDataChannelPerMB;
    private Map<String, AtomicLong> publishcallCountPerDataChannelPerMB;
    private Map<String, AtomicLong> upsertMainDataCounterPerTablePerMB;
    private Map<String, AtomicLong> upsertRelationalDataCounterPerTablePerMB;
    private Map<String, AtomicLong> upsertFilteredMessagesCounterPerTablePerMB;
    private Map<String, AtomicLong> upsertFailedMessagesCounterPerTablePerMB;
    private Map<String, AtomicLong> upsertStageFilterCounterPerTablePerMB;
    private Map<String, AtomicLong> deleteCounterPerTablePerMB;
    private Map<String, Map<String, AtomicLong>> leadingKeysPerContextPerSourceDataChannel;
    private Map<String, Map<String, AtomicLong>> leadingKeysPerContextPerSourceDataChannelComputed;
    private Map<String, Map<String, AtomicLong>> publishStageFileteredLeadingKeysPerContextPerSourceDataChannel;
    private Map<String, AtomicLong> messageSentToIntermediateKafkaCounterPerSubAreaPerMB;
    private Map<String, AtomicLong> inputMessagesCountPerDataChannelPerMB;
    private Map<String, AtomicLong> inputMessagesCountPerTableDataChannelPerMB;
    private Map<String, AtomicLong> partialTrxMessagesCountPerDataChannelPerMB;
    private Map<String, AtomicLong> partialTrxMessagesTotalReadtimePerDataChannelPerMB;
    private Map<String, AtomicLong> partialTrxMessagesTotalWritetimePerDataChannelPerMB;
    private AtomicLong numberOfMicroBatchesCompletedPerMB;
    private Map<String, AtomicLong> numberOfConflictsPerDatachannelPerMB;
    private Map<String, AtomicLong> numberOfIncompleteTransactionsPerDatachannelPerMB;
    private Map<String, AtomicLong> transactionMessageSizePerDataChannelPerMB;
    private Map<String, AtomicLong> transactionMessageSizePerIntermediateKafkaPerMB;


    public ReplicatorCounter(Set<String> dataChannels) {
        this.dataChannels = dataChannels;
        initCounters();
    }

    @Override
    public CounterType getType() {
        return CounterType.REPLICATOR;
    }

    @Override
    public void addCounter(Object... args) {
        CounterType counterSubType = CounterType.valueOf(args[0].toString());
        Object param1 = args[1];
        Object param2 = args[2];
        Object param3 = null;
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
            case LEADINGKEYSQUERYCOUNT:
                incrementLeadingKeysQueryCount((String) param1, (Long) param2);
                break;
            case RECORDSPROCESSED:
                incrementRecordsProcessed((String) param1, (Long) param2);
                break;
            case UPSERTCALLCOUNTPERDATACHANNELPERMB:
                incrementUpsertcallCountPerDataChannel((String) param1, (Long) param2);
                break;
            case PUBLISHCALLCOUNTPERDATACHANNELPERMB:
                incrementPublishcallCountPerDataChannel((String) param1, (Long) param2);
                break;
            case INPUTMESSAGESCOUNTPERDATACHANNELPERMB:
                incrementInputMessagesCountPerDataChannelPerMB((String) param1, (Long) param2);
                break;
            case INPUTMESSAGESCOUNTPERTABLEDATACHANNELPERMB:
                incrementInputMessagesCountPerTableDataChannelPerMB((String) param1, (Long) param2);
                break;
            case UPSERTMAINDATACOUNTERPERTABLEPERMB:
                incrementUpsertMainDataCounterPerTablePerMB((String) param1, (Long) param2);
                break;
            case UPSERTRELATIONALDATACOUNTERPERTABLEPERMB:
                incrementUpsertRelationalDataCounterPerTablePerMB((String) param1, (Long) param2);
                break;
            case UPSERTFILTEREDMESSAGESCOUNTERPERTABLEPERMB:
                incrementUpsertFilteredMessagesCounterPerTablePerMB((String) param1, (Long) param2);
                break;
            case UPSERTFAILEDMESSAGESCOUNTERPERTABLEPERMB:
                incrementUpsertFailedMessagesCounterPerTablePerMB((String) param1, (Long) param2);
                break;
            case DELETECOUNTERPERTABLEPERMB:
                incrementDeleteCounterPerTablePerMB((String) param1, (Long) param2);
                break;
            case MESSAGESENTTOINTERMEDIATEKAFKACOUNTERPERSUBAREAPERMB:
                incrementMessageSentToIntermediateKafkaCounterPerSubAreaPerMB((String) param1, (Long) param2);
                break;
            case LEADINGKEYSPERCONTEXTPERSOURCEDATACHANNEL:
                param3 = args[3];
                incrementLeadingKeysPerContextPerSourceDataChannel((String) param1, (String) param2, (Long) param3);
                break;
            case LEADINGKEYSPERCONTEXTPERSOURCEDATACHANNELCOMPUTED:
                param3 = args[3];
                incrementLeadingKeysPerContextPerSourceDataChannelComputed((String) param1, (String) param2, (Long) param3);
                break;
            case PARTIALTRXMESSAGESCOUNTPERDATACHANNELPERMB:
                incrementPartialTrxMessagesCountPerDataChannelPerMB((String) param1, (Long) param2);
                break;
            case PARTIALTRXMESSAGESTOTALREADTIMEPERDATACHANNELPERMB:
                incrementPartialTrxMessagesTotalReadtimePerDataChannelPerMB((String) param1, (Long) param2);
                break;
            case PARTIALTRXMESSAGESTOTALWRITETIMEPERDATACHANNELPERMB:
                incrementPartialTrxMessagesTotalWritetimePerDataChannelPerMB((String) param1, (Long) param2);
                break;
            case NUMBEROFMICROBATCHESPERMB:
                incrementNumberOfMicroBatchesCompletedPerMB((Long) param2);
                break;
            case NUMBEROFCONFLICTSPERMB:
                incrementNumberOfConflictsPerDatachannelPerMB((String) param1, (Long) param2);
                break;
            case NUMBEROFINCOMPLETETRXPERMB:
                incrementNumberOfIncompleteTransactionsPerDatachannelPerMB((String) param1, (Long) param2);
                break;
            case TRANSACTIONMESSAGESIZEFROMKAFKA1PERDATACHANNEL:
                incrementTransactionMessageSizePerDataChannelPerMB((String) param1, (Long) param2);
                break;
            case TRANSACTIONMESSAGESIZETOINTERMEDIATEKAFKAPERMB:
                incrementTransactionMessageSizePerIntermediateKafkaPerMB((String) param1, (Long) param2);
                break;
        }
    }



    @Override
    public void report(DbMetrics metrics, CqlSession session) {
        publishCount.forEach((key, value) -> metrics.setPublishCountPerDataChannel(key, value.get()));
        recordsProcessed.forEach((key, value) -> metrics.setRecordsProcessedPerDataChannel(key, value.get()));
        upsertReadCompleted.forEach((key, value) -> metrics.setUpsertReadCompletedPerDataChannel(key, value.get()));
        upsertWriteCompleted.forEach((key, value) -> metrics.setUpsertWriteCompletedPerDataChannel(key, value.get()));
        leadingKeysQueryCount.forEach((key, value) -> metrics.setLeadingKeysQueryCountPerDataChannel(key, value.get()));

        for (Map.Entry<String, Map<String, AtomicLong>> entry : leadingKeysPerContextPerSourceDataChannelComputed.entrySet()) {
            for (String contextName : entry.getValue().keySet()) {
                long diff = entry.getValue().get(contextName).longValue();
                if (leadingKeysPerContextPerSourceDataChannel.containsKey(entry.getKey()) && leadingKeysPerContextPerSourceDataChannel.get(entry.getKey()).containsKey(contextName)) {
                    diff = diff - leadingKeysPerContextPerSourceDataChannel.get(entry.getKey()).get(contextName).longValue();
                }
                incrementPublishStageFileteredLeadingKeysPerContextPerSourceDataChannel(entry.getKey(), contextName, diff);
            }
        }
        metrics.setLeadingKeysCounterPerContextPerSourceDataChannelPerMB(publishStageFileteredLeadingKeysPerContextPerSourceDataChannel);
        metrics.setUpsertcallCountPerDataChannelPerMB(upsertcallCountPerDataChannelPerMB);
        metrics.setPublishcallCountPerDataChannelPerMB(publishcallCountPerDataChannelPerMB);
        metrics.setInputMessagesCountPerDataChannelPerMB(inputMessagesCountPerDataChannelPerMB);
        metrics.setNumberOfConflictsPerDatachannelPerMB(numberOfConflictsPerDatachannelPerMB);
        metrics.setNumberOfIncompleteTransactionsPerDatachannelPerMB(numberOfIncompleteTransactionsPerDatachannelPerMB);
        metrics.setUpsertMainDataCounterPerTablePerMB(upsertMainDataCounterPerTablePerMB);
        metrics.setUpsertRelationalDataCounterPerTablePerMB(upsertRelationalDataCounterPerTablePerMB);
        metrics.setUpsertFilteredMessagesCounterPerTablePerMB(upsertFilteredMessagesCounterPerTablePerMB);
        metrics.setUpsertFailedMessagesCounterPerTablePerMB(upsertFailedMessagesCounterPerTablePerMB);
        metrics.setTransactionMessageLengthPerDataChannelPerMB(transactionMessageSizePerDataChannelPerMB);
        metrics.setTransactionMessageLengthPerIntermediateKafkaPerMB(transactionMessageSizePerIntermediateKafkaPerMB);

        for (Map.Entry<String, AtomicLong> entry : inputMessagesCountPerTableDataChannelPerMB.entrySet()) {
            String tableName = entry.getKey().substring(entry.getKey().indexOf('_') + 1);
            long diff = entry.getValue().longValue();
            if (upsertMainDataCounterPerTablePerMB.containsKey(tableName)) {
                diff = diff - upsertMainDataCounterPerTablePerMB.get(tableName).longValue();
            }
            incrementUpsertStageFilterCounterPerTablePerMB(entry.getKey(), diff);
        }
        metrics.setUpsertStageFilterCounterPerTablePerMB(upsertStageFilterCounterPerTablePerMB);
        metrics.setDeleteCounterPerTablePerMB(deleteCounterPerTablePerMB);
        metrics.setMessageSentToIntermediateKafkaCounterPerSubAreaPerMB(messageSentToIntermediateKafkaCounterPerSubAreaPerMB);

        metrics.setPartialTrxMessagesCountPerDataChannelPerMB(partialTrxMessagesCountPerDataChannelPerMB);
        metrics.setPartialTrxMessagesTotalReadtimePerDataChannelPerMB(partialTrxMessagesTotalReadtimePerDataChannelPerMB);
        metrics.setPartialTrxMessagesTotalWritetimePerDataChannelPerMB(partialTrxMessagesTotalWritetimePerDataChannelPerMB);
        metrics.setNumberOfMicroBatchesCompletedPerMB(numberOfMicroBatchesCompletedPerMB);
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

    public void incrementLeadingKeysQueryCount(String key, Long newVal) {
        if (leadingKeysQueryCount.get(key) != null) {
            leadingKeysQueryCount.get(key).getAndAccumulate(newVal, Long::sum);
        }
    }

    public void incrementRecordsProcessed(String key, Long newVal) {
        if (recordsProcessed.get(key) != null) {
            recordsProcessed.get(key).getAndAccumulate(newVal, Long::sum);
        }
    }

    public void incrementUpsertcallCountPerDataChannel(String dataChannelName, long newVal) {
        if (!upsertcallCountPerDataChannelPerMB.containsKey(dataChannelName)) {
            upsertcallCountPerDataChannelPerMB.put(dataChannelName, new AtomicLong());
        }
        upsertcallCountPerDataChannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementPublishcallCountPerDataChannel(String dataChannelName, long newVal) {
        if (!publishcallCountPerDataChannelPerMB.containsKey(dataChannelName)) {
            publishcallCountPerDataChannelPerMB.put(dataChannelName, new AtomicLong());
        }
        publishcallCountPerDataChannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementInputMessagesCountPerDataChannelPerMB(String dataChannelName, long newVal) {
        if (!inputMessagesCountPerDataChannelPerMB.containsKey(dataChannelName)) {
            inputMessagesCountPerDataChannelPerMB.put(dataChannelName, new AtomicLong());
        }
        inputMessagesCountPerDataChannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementTransactionMessageSizePerDataChannelPerMB(String dataChannelName, long newVal){

        if (!transactionMessageSizePerDataChannelPerMB.containsKey(dataChannelName)) {
            transactionMessageSizePerDataChannelPerMB.put(dataChannelName, new AtomicLong());
            }
        transactionMessageSizePerDataChannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementTransactionMessageSizePerIntermediateKafkaPerMB(String dataChannelName, long newVal){
        if (!transactionMessageSizePerIntermediateKafkaPerMB.containsKey(dataChannelName)) {
            transactionMessageSizePerIntermediateKafkaPerMB.put(dataChannelName, new AtomicLong());
        }
        transactionMessageSizePerIntermediateKafkaPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }


    public void incrementInputMessagesCountPerTableDataChannelPerMB(String dataChannelName, long newVal) {
        if (!inputMessagesCountPerTableDataChannelPerMB.containsKey(dataChannelName)) {
            inputMessagesCountPerTableDataChannelPerMB.put(dataChannelName, new AtomicLong());
        }
        inputMessagesCountPerTableDataChannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementUpsertMainDataCounterPerTablePerMB(String tableName, long newVal) {
        if (!upsertMainDataCounterPerTablePerMB.containsKey(tableName)) {
            upsertMainDataCounterPerTablePerMB.put(tableName, new AtomicLong());
        }
        upsertMainDataCounterPerTablePerMB.get(tableName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementUpsertStageFilterCounterPerTablePerMB(String tableName, long newVal) {
        if (!upsertStageFilterCounterPerTablePerMB.containsKey(tableName)) {
            upsertStageFilterCounterPerTablePerMB.put(tableName, new AtomicLong());
        }
        upsertStageFilterCounterPerTablePerMB.get(tableName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementUpsertRelationalDataCounterPerTablePerMB(String tableName, long newVal) {
        if (!upsertRelationalDataCounterPerTablePerMB.containsKey(tableName)) {
            upsertRelationalDataCounterPerTablePerMB.put(tableName, new AtomicLong());
        }
        upsertRelationalDataCounterPerTablePerMB.get(tableName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementUpsertFilteredMessagesCounterPerTablePerMB(String tableName, long newVal) {
        if (!upsertFilteredMessagesCounterPerTablePerMB.containsKey(tableName)) {
            upsertFilteredMessagesCounterPerTablePerMB.put(tableName, new AtomicLong());
        }
        upsertFilteredMessagesCounterPerTablePerMB.get(tableName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementUpsertFailedMessagesCounterPerTablePerMB(String tableName, long newVal) {
        if (!upsertFailedMessagesCounterPerTablePerMB.containsKey(tableName)) {
            upsertFailedMessagesCounterPerTablePerMB.put(tableName, new AtomicLong());
        }
        upsertFailedMessagesCounterPerTablePerMB.get(tableName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementDeleteCounterPerTablePerMB(String tableName, long newVal) {
        if (!deleteCounterPerTablePerMB.containsKey(tableName)) {
            deleteCounterPerTablePerMB.put(tableName, new AtomicLong());
        }
        deleteCounterPerTablePerMB.get(tableName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementMessageSentToIntermediateKafkaCounterPerSubAreaPerMB(String subArea, long newVal) {
        if (!messageSentToIntermediateKafkaCounterPerSubAreaPerMB.containsKey(subArea)) {
            messageSentToIntermediateKafkaCounterPerSubAreaPerMB.put(subArea, new AtomicLong());
        }
        messageSentToIntermediateKafkaCounterPerSubAreaPerMB.get(subArea).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementLeadingKeysPerContextPerSourceDataChannel(String datachannelName, String contextName, long newVal) {
        Map<String, AtomicLong> contextMap;
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

    public void incrementLeadingKeysPerContextPerSourceDataChannelComputed(String datachannelName, String contextName, long newVal) {
        Map<String, AtomicLong> contextMap;
        if (leadingKeysPerContextPerSourceDataChannelComputed.containsKey(datachannelName)) {
            contextMap = leadingKeysPerContextPerSourceDataChannelComputed.get(datachannelName);
        } else {
            contextMap = new ConcurrentHashMap<>();
            leadingKeysPerContextPerSourceDataChannelComputed.put(datachannelName, contextMap);
        }
        if (!contextMap.containsKey(contextName)) {
            contextMap.put(contextName, new AtomicLong());
        }
        contextMap.get(contextName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementPublishStageFileteredLeadingKeysPerContextPerSourceDataChannel(String datachannelName, String contextName, long newVal) {
        Map<String, AtomicLong> contextMap;
        if (publishStageFileteredLeadingKeysPerContextPerSourceDataChannel.containsKey(datachannelName)) {
            contextMap = publishStageFileteredLeadingKeysPerContextPerSourceDataChannel.get(datachannelName);
        } else {
            contextMap = new ConcurrentHashMap<>();
            publishStageFileteredLeadingKeysPerContextPerSourceDataChannel.put(datachannelName, contextMap);
        }
        if (!contextMap.containsKey(contextName)) {
            contextMap.put(contextName, new AtomicLong());
        }
        contextMap.get(contextName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementPartialTrxMessagesCountPerDataChannelPerMB(String dataChannelName, long newVal) {
        if (!partialTrxMessagesCountPerDataChannelPerMB.containsKey(dataChannelName)) {
            partialTrxMessagesCountPerDataChannelPerMB.put(dataChannelName, new AtomicLong());
        }
        partialTrxMessagesCountPerDataChannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementPartialTrxMessagesTotalReadtimePerDataChannelPerMB(String dataChannelName, long newVal) {
        if (!partialTrxMessagesTotalReadtimePerDataChannelPerMB.containsKey(dataChannelName)) {
            partialTrxMessagesTotalReadtimePerDataChannelPerMB.put(dataChannelName, new AtomicLong());
        }
        partialTrxMessagesTotalReadtimePerDataChannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementPartialTrxMessagesTotalWritetimePerDataChannelPerMB(String dataChannelName, long newVal) {
        if (!partialTrxMessagesTotalWritetimePerDataChannelPerMB.containsKey(dataChannelName)) {
            partialTrxMessagesTotalWritetimePerDataChannelPerMB.put(dataChannelName, new AtomicLong());
        }
        partialTrxMessagesTotalWritetimePerDataChannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementNumberOfMicroBatchesCompletedPerMB(long newVal) {
        numberOfMicroBatchesCompletedPerMB.getAndAccumulate(newVal, Long::sum);
    }

    public void incrementNumberOfConflictsPerDatachannelPerMB(String dataChannelName, long newVal) {
        if (!numberOfConflictsPerDatachannelPerMB.containsKey(dataChannelName)) {
            numberOfConflictsPerDatachannelPerMB.put(dataChannelName, new AtomicLong());
        }
        numberOfConflictsPerDatachannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementNumberOfIncompleteTransactionsPerDatachannelPerMB(String dataChannelName, long newVal) {
        if (!numberOfIncompleteTransactionsPerDatachannelPerMB.containsKey(dataChannelName)) {
            numberOfIncompleteTransactionsPerDatachannelPerMB.put(dataChannelName, new AtomicLong());
        }
        numberOfIncompleteTransactionsPerDatachannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }


    private void initCounters() {
        publishCount = getFullMap();
        upsertWriteCompleted = getFullMap();
        upsertReadCompleted = getFullMap();
        leadingKeysQueryCount = getFullMap();
        recordsProcessed = getFullMap();
        upsertcallCountPerDataChannelPerMB = new ConcurrentHashMap<>();
        publishcallCountPerDataChannelPerMB = new ConcurrentHashMap<>();
        inputMessagesCountPerDataChannelPerMB = new ConcurrentHashMap<>();
        inputMessagesCountPerTableDataChannelPerMB = new ConcurrentHashMap<>();
        upsertMainDataCounterPerTablePerMB = new ConcurrentHashMap<>();
        upsertRelationalDataCounterPerTablePerMB = new ConcurrentHashMap<>();
        upsertFilteredMessagesCounterPerTablePerMB = new ConcurrentHashMap<>();
        upsertFailedMessagesCounterPerTablePerMB = new ConcurrentHashMap<>();
        upsertStageFilterCounterPerTablePerMB = new ConcurrentHashMap<>();
        deleteCounterPerTablePerMB = new ConcurrentHashMap<>();
        messageSentToIntermediateKafkaCounterPerSubAreaPerMB = new ConcurrentHashMap<>();
        leadingKeysPerContextPerSourceDataChannel = new ConcurrentHashMap<>();
        leadingKeysPerContextPerSourceDataChannelComputed = new ConcurrentHashMap<>();
        publishStageFileteredLeadingKeysPerContextPerSourceDataChannel = new ConcurrentHashMap<>();
        partialTrxMessagesCountPerDataChannelPerMB = new ConcurrentHashMap<>();
        partialTrxMessagesTotalReadtimePerDataChannelPerMB = new ConcurrentHashMap<>();
        partialTrxMessagesTotalWritetimePerDataChannelPerMB = new ConcurrentHashMap<>();
        numberOfMicroBatchesCompletedPerMB = new AtomicLong();
        numberOfConflictsPerDatachannelPerMB = new ConcurrentHashMap<>();
        numberOfIncompleteTransactionsPerDatachannelPerMB = new ConcurrentHashMap<>();
        transactionMessageSizePerDataChannelPerMB= new ConcurrentHashMap<>();
        transactionMessageSizePerIntermediateKafkaPerMB= new ConcurrentHashMap<>();
    }

    private Map<String, AtomicLong> getFullMap() {
        Map<String, AtomicLong> map = new HashMap<>();
        dataChannels.forEach(one -> map.put(one, new AtomicLong()));
        return map;
    }
}