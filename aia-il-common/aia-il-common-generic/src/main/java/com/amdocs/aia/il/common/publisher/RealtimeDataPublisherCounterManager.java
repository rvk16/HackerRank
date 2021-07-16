package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.il.common.stores.scylla.monitor.DbMetrics;
import com.datastax.oss.driver.api.core.CqlSession;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Created by SATARALK.
 */
public class RealtimeDataPublisherCounterManager {
    public static RealtimeDataPublisherCounterManager INSTANCE;

    private final RecordsAccumulator recordsAccumulator;
    private final ExecutorService executorService;

    public RealtimeDataPublisherCounterManager(CounterType counterType, Set<String> dataChannels, CqlSession session, DbMetrics metrics,long metricsSchedulerTimeInMS) {
        this.recordsAccumulator = new RecordsAccumulator(session, metrics,metricsSchedulerTimeInMS);
        this.executorService = Executors.newSingleThreadExecutor();
        this.executorService.execute(this.recordsAccumulator);
        registerAllCounters(counterType, dataChannels);
    }

    public static void init(CounterType counterType, Set<String> dataChannels, CqlSession session, DbMetrics metrics,long metricsSchedulerTimeInMS) {
        INSTANCE = new RealtimeDataPublisherCounterManager(counterType, dataChannels, session, metrics,metricsSchedulerTimeInMS);
    }

    /**
     * Register all counters
     */
    private void registerAllCounters(CounterType counterType, Set<String> dataChannels) {
        switch (counterType) {
            case REPLICATOR:
                this.getRecordsAccumulator().registerCounter(new ReplicatorCounter(dataChannels));
                break;
            case TRANSFORMER:
                this.getRecordsAccumulator().registerCounter(new TransformerCounter(dataChannels));
                break;
            case COMPUTELEADINGKEYS:
                this.getRecordsAccumulator().registerCounter(new ComputeLeadingKeyCounter(dataChannels));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + counterType);
        }
    }

    public void shutdown() {
        getRecordsAccumulator().shutdown();
        this.executorService.shutdown();
    }

    public RecordsAccumulator getRecordsAccumulator() {
        return recordsAccumulator;
    }
}