package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.il.common.stores.scylla.monitor.DbMetrics;
import com.datastax.oss.driver.api.core.CqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ComputeLeadingKeyCounter implements IAccumulatorCounter {
    private static final long serialVersionUID = 1351152824219500853L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputeLeadingKeyCounter.class);

    private final Set<String> dataChannels;
    //Counters
    private Map<String, AtomicLong> publishCount;
    private Map<String, AtomicLong> recordsProcessed;
    private Map<String, AtomicLong> publishCallCountPerDataChannelPerMB;
    private Map<String, Map<String, AtomicLong>> leadingKeysPerContextPerSourceDataChannel;
    private Map<String, Map<String, AtomicLong>> leadingKeysPerContextPerSourceDataChannelComputed;
    private Map<String, Map<String, AtomicLong>> publishStageFilteredLeadingKeysPerContextPerSourceDataChannel;
    private Map<String, AtomicLong> messageSentToIntermediateKafkaCounterPerSubAreaPerMB;
    private Map<String, AtomicLong> inputMessagesCountPerDataChannelPerMB;
    private Map<String, AtomicLong> inputMessagesCountPerTableDataChannelPerMB;
    private AtomicLong numberOfMicroBatchesCompletedPerMB;

    public ComputeLeadingKeyCounter(Set<String> dataChannels) {
        this.dataChannels = dataChannels;
        initCounters();
    }

    @Override
    public CounterType getType() {
        return CounterType.COMPUTELEADINGKEYS;
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
            case RECORDSPROCESSED:
                incrementRecordsProcessed((String) param1, (Long) param2);
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
            case NUMBEROFMICROBATCHESPERMB:
                incrementNumberOfMicroBatchesCompletedPerMB((Long) param2);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + counterSubType);
        }
    }

    public void incrementPublishCount(String key, Long newVal) {
        if (publishCount.get(key) != null) {
            publishCount.get(key).getAndAccumulate(newVal, Long::sum);
        }
    }

    public void incrementRecordsProcessed(String key, Long newVal) {
        if (recordsProcessed.get(key) != null) {
            recordsProcessed.get(key).getAndAccumulate(newVal, Long::sum);
        }
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

    public void incrementPublishcallCountPerDataChannel(String dataChannelName, long newVal) {
        if (!publishCallCountPerDataChannelPerMB.containsKey(dataChannelName)) {
            publishCallCountPerDataChannelPerMB.put(dataChannelName, new AtomicLong());
        }
        publishCallCountPerDataChannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
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

    public void incrementMessageSentToIntermediateKafkaCounterPerSubAreaPerMB(String subArea, long newVal) {
        if (!messageSentToIntermediateKafkaCounterPerSubAreaPerMB.containsKey(subArea)) {
            messageSentToIntermediateKafkaCounterPerSubAreaPerMB.put(subArea, new AtomicLong());
        }
        messageSentToIntermediateKafkaCounterPerSubAreaPerMB.get(subArea).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementInputMessagesCountPerDataChannelPerMB(String dataChannelName, long newVal) {
        if (!inputMessagesCountPerDataChannelPerMB.containsKey(dataChannelName)) {
            inputMessagesCountPerDataChannelPerMB.put(dataChannelName, new AtomicLong());
        }
        inputMessagesCountPerDataChannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementInputMessagesCountPerTableDataChannelPerMB(String dataChannelName, long newVal) {
        if (!inputMessagesCountPerTableDataChannelPerMB.containsKey(dataChannelName)) {
            inputMessagesCountPerTableDataChannelPerMB.put(dataChannelName, new AtomicLong());
        }
        inputMessagesCountPerTableDataChannelPerMB.get(dataChannelName).getAndAccumulate(newVal, Long::sum);
    }

    public void incrementNumberOfMicroBatchesCompletedPerMB(long newVal) {
        numberOfMicroBatchesCompletedPerMB.getAndAccumulate(newVal, Long::sum);
    }

    public void incrementPublishStageFileteredLeadingKeysPerContextPerSourceDataChannel(String datachannelName, String contextName, long newVal) {
        Map<String, AtomicLong> contextMap;
        if (publishStageFilteredLeadingKeysPerContextPerSourceDataChannel.containsKey(datachannelName)) {
            contextMap = publishStageFilteredLeadingKeysPerContextPerSourceDataChannel.get(datachannelName);
        } else {
            contextMap = new ConcurrentHashMap<>();
            publishStageFilteredLeadingKeysPerContextPerSourceDataChannel.put(datachannelName, contextMap);
        }
        if (!contextMap.containsKey(contextName)) {
            contextMap.put(contextName, new AtomicLong());
        }
        contextMap.get(contextName).getAndAccumulate(newVal, Long::sum);
    }

    @Override
    public void report(DbMetrics metrics, CqlSession session) {
        publishCount.forEach((key, value) -> metrics.setPublishCountPerDataChannel(key, value.get()));
        recordsProcessed.forEach((key, value) -> metrics.setRecordsProcessedPerDataChannel(key, value.get()));
        metrics.setPublishcallCountPerDataChannelPerMB(publishCallCountPerDataChannelPerMB);
        leadingKeysPerContextPerSourceDataChannelComputed.forEach((datachannel, value) -> {
            for (final Map.Entry<String, AtomicLong> entry : value.entrySet()) {
                String contextName = entry.getKey();
                long diff = entry.getValue().longValue();
                if (leadingKeysPerContextPerSourceDataChannel.containsKey(datachannel) && leadingKeysPerContextPerSourceDataChannel.get(datachannel).containsKey(contextName)) {
                    diff = diff - leadingKeysPerContextPerSourceDataChannel.get(datachannel).get(contextName).longValue();
                }
                incrementPublishStageFileteredLeadingKeysPerContextPerSourceDataChannel(datachannel, contextName, diff);
            }
        });
        metrics.setMessageSentToIntermediateKafkaCounterPerSubAreaPerMB(messageSentToIntermediateKafkaCounterPerSubAreaPerMB);
        metrics.setInputMessagesCountPerDataChannelPerMB(inputMessagesCountPerDataChannelPerMB);
        metrics.setInputMessagesCountPerTableDataChannelPerMB(inputMessagesCountPerTableDataChannelPerMB);
        LOGGER.info("Number Of MicroBatches Completed: {} ", numberOfMicroBatchesCompletedPerMB.longValue());
        metrics.setNumberOfMicroBatchesCompletedPerMB(numberOfMicroBatchesCompletedPerMB);
        metrics.setLeadingKeysCounterPerContextPerSourceDataChannelPerMB(leadingKeysPerContextPerSourceDataChannel);
    }

    @Override
    public void clear() {
        initCounters();
    }

    private void initCounters() {
        publishCount = getFullMap();
        recordsProcessed = getFullMap();
        publishCallCountPerDataChannelPerMB = new ConcurrentHashMap<>();
        leadingKeysPerContextPerSourceDataChannel = new ConcurrentHashMap<>();
        leadingKeysPerContextPerSourceDataChannelComputed = new ConcurrentHashMap<>();
        messageSentToIntermediateKafkaCounterPerSubAreaPerMB = new ConcurrentHashMap<>();
        inputMessagesCountPerDataChannelPerMB = new ConcurrentHashMap<>();
        inputMessagesCountPerTableDataChannelPerMB = new ConcurrentHashMap<>();
        numberOfMicroBatchesCompletedPerMB = new AtomicLong();

    }

    private Map<String, AtomicLong> getFullMap() {
        Map<String, AtomicLong> map = new ConcurrentHashMap<>();
        dataChannels.forEach(one -> map.put(one, new AtomicLong()));
        return map;
    }
}