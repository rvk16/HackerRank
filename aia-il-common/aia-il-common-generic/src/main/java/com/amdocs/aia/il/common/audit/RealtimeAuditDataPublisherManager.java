package com.amdocs.aia.il.common.audit;

import com.amdocs.aia.il.common.publisher.CounterType;
import org.apache.kafka.clients.producer.Producer;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RealtimeAuditDataPublisherManager {

    private static RealtimeAuditDataPublisherManager instance;
    private final AuditRecordsAccumulator auditRecordsAccumulator;
    private final AuditPublisher publisher = new AuditPublisher();
    private ExecutorService executorService;


    public RealtimeAuditDataPublisherManager(CounterType counterType, Set<String> dataChannels) {
        this.auditRecordsAccumulator = new AuditRecordsAccumulator(counterType, dataChannels);
        registerCounter(counterType);
        this.executorService = Executors.newSingleThreadExecutor();
        this.executorService.execute(this.auditRecordsAccumulator);
        publisher.init();
    }

    public void registerCounter(CounterType counterType) {
        switch (counterType) {
            case REPLICATORLOAD:
                this.auditRecordsAccumulator.registerCounter(new ReplicatorLoadCounter());
                this.auditRecordsAccumulator.registerCounter(new ReplicatorGenCounter());
                break;
            case TRANSFORMERAUDIT:
                this.auditRecordsAccumulator.registerCounter(new TransformerAuditCounter());
                break;
            case KAFKACOLLECTOR:
                this.auditRecordsAccumulator.registerCounter(new KafkaLoadCounter());
                this.auditRecordsAccumulator.registerCounter(new KafkaStoreCounter());
                break;
            case COMPUTLEADINGKEY:
                this.auditRecordsAccumulator.registerCounter(new LeadingKeyCounter());
                break;
            case BULKREPLICATOR:
                this.auditRecordsAccumulator.registerCounter(new BulkReplicatorCounter());
                break;
            case BULKTRANSFORMER:
                this.auditRecordsAccumulator.registerCounter(new BulkTransformerAuditCounter());
                break;
            case TRANSIENTTRANSFORMER:
                this.auditRecordsAccumulator.registerCounter(new TransientTransformerLoadCounter());
                this.auditRecordsAccumulator.registerCounter(new TransientTransformerStoreCounter());
                break;
            case REFERENCESTOREPUBLISHER:
                this.auditRecordsAccumulator.registerCounter(new ReferenceStorePublisherReplicatorCounter());
                this.auditRecordsAccumulator.registerCounter(new ReferenceStorePublisherTransformerCounter());
                break;
            default:
                break;
        }
    }

    //Initialize singleton INSTANCE , Accumulators for ReplicatorLoad , ReplicatorGen etc.Also init AuditPublisher
    public static void init(CounterType counterType, Set<String> dataChannels) {
        instance = new RealtimeAuditDataPublisherManager(counterType, dataChannels);
    }

    //Clear accumulator for given data channel and service
    public void clear(CounterType counterType) {
        this.getAuditRecordsAccumulator().getCounters().get(counterType).values().forEach(AuditCounter::clear);
    }

    public void replicatorLoad(CounterType counterType, String topicName, Producer<String, String> producer, boolean auditLogsEnabled) {
        publisher.merge(getAuditRecordsAccumulator().getCounters().get(counterType).values().stream().iterator().next().getMessageStructure());
        publisher.publish(topicName, producer, auditLogsEnabled);
        clear(counterType);

        publisher.merge(getAuditRecordsAccumulator().getCounters().get(CounterType.REPLICATORGEN).values().stream().iterator().next().getMessageStructure());
        publisher.publish(topicName, producer, auditLogsEnabled);
        clear(CounterType.REPLICATORGEN);
    }

    public void bulkReplicatorAndTransformer(String topicName, Producer<String, String> producer, boolean auditLogsEnabled, CounterType counterType) {
        publisher.merge(getAuditRecordsAccumulator().getCounters().get(counterType).values().stream().iterator().next().getMessageStructure());
        publisher.publish(topicName, producer, auditLogsEnabled);
        clear(counterType);
    }

    //Merges and publishes audit data for passed dataChannel and service
    public void mergeAndPublish(CounterType counterType, String topicName, Producer<String, String> producer, boolean auditLogsEnabled) {
        switch (counterType) {
            case REPLICATORLOAD:
                replicatorLoad(counterType, topicName, producer, auditLogsEnabled);
                break;
            case TRANSFORMERAUDIT:
                publisher.merge(getAuditRecordsAccumulator().getCounters().get(counterType).values().stream().iterator().next().getMessageStructure());
                publisher.publish(topicName, producer, auditLogsEnabled);
                clear(counterType);
                break;
            case KAFKACOLLECTOR:
                kafkaCollectorMergeAndPublish(topicName, producer, auditLogsEnabled);
                break;
            case BULKREPLICATOR:
                bulkReplicatorAndTransformer(topicName, producer, auditLogsEnabled, counterType);
                break;
            case COMPUTLEADINGKEY:
                publisher.merge(getAuditRecordsAccumulator().getCounters().get(CounterType.COMPUTLEADINGKEY).values().stream().iterator().next().getMessageStructure());
                publisher.publish(topicName, producer, auditLogsEnabled);
                clear(CounterType.COMPUTLEADINGKEY);
                break;
            case BULKTRANSFORMER:
                bulkReplicatorAndTransformer(topicName, producer, auditLogsEnabled, counterType);
                break;
            case TRANSIENTTRANSFORMER:
                transientTransformerMergePublish(topicName, producer, auditLogsEnabled);
                break;
            case REFERENCESTOREPUBLISHER:
                referenceStorePublisherMergePublish(topicName, producer, auditLogsEnabled);
                break;
            default:
                break;
        }
    }

    private void referenceStorePublisherMergePublish(String topicName, Producer<String, String> producer, boolean auditLogsEnabled) {
        publisher.merge(getAuditRecordsAccumulator().getCounters().get(CounterType.REFERENCESTOREPUBLISHERREPLICATOR).values().stream().iterator().next().getMessageStructure());
        publisher.publish(topicName, producer, auditLogsEnabled);
        clear(CounterType.REFERENCESTOREPUBLISHERREPLICATOR);

        publisher.merge(getAuditRecordsAccumulator().getCounters().get(CounterType.REFERENCESTOREPUBLISHERTRANSFORMER).values().stream().iterator().next().getMessageStructure());
        publisher.publish(topicName, producer, auditLogsEnabled);
        clear(CounterType.REFERENCESTOREPUBLISHERTRANSFORMER);
    }

    private void kafkaCollectorMergeAndPublish(String topicName, Producer<String, String> producer, boolean auditLogsEnabled) {
        publisher.merge(getAuditRecordsAccumulator().getCounters().get(CounterType.KAFKALOAD).values().stream().iterator().next().getMessageStructure());
        publisher.publish(topicName, producer, auditLogsEnabled);
        clear(CounterType.KAFKALOAD);

        publisher.merge(getAuditRecordsAccumulator().getCounters().get(CounterType.KAFKASTORE).values().stream().iterator().next().getMessageStructure());
        publisher.publish(topicName, producer, auditLogsEnabled);
        clear(CounterType.KAFKASTORE);
    }

    private void transientTransformerMergePublish(String topicName, Producer<String, String> producer, boolean auditLogsEnabled) {
        publisher.merge(getAuditRecordsAccumulator().getCounters().get(CounterType.TRANSIENTTRANSFORMERLOAD).values().stream().iterator().next().getMessageStructure());
        publisher.publish(topicName, producer, auditLogsEnabled);
        clear(CounterType.TRANSIENTTRANSFORMERLOAD);

        publisher.merge(getAuditRecordsAccumulator().getCounters().get(CounterType.TRANSIENTTRANSFORMERSTORE).values().stream().iterator().next().getMessageStructure());
        publisher.publish(topicName, producer, auditLogsEnabled);
        clear(CounterType.TRANSIENTTRANSFORMERSTORE);
    }

    public void shutdown() {
        getAuditRecordsAccumulator().shutdown();
        this.executorService.shutdown();
    }

    public AuditRecordsAccumulator getAuditRecordsAccumulator() {
        return auditRecordsAccumulator;
    }

    public static RealtimeAuditDataPublisherManager getInstance() {
        return instance;
    }

}
