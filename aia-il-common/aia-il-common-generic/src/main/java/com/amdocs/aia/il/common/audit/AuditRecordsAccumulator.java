package com.amdocs.aia.il.common.audit;

import com.amdocs.aia.il.common.publisher.CounterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AuditRecordsAccumulator implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditRecordsAccumulator.class);

    LinkedBlockingQueue<AuditCounterTypeAndParams> counterTypeAndParamsQueue;
    boolean isRunning;
    Lock lock = null;

    //counters per dataChannel per service
    Map<CounterType, Map<String, AuditCounter>> counters;
    CounterType counterType;
    Set<String> dataChannels;

    public AuditRecordsAccumulator(CounterType counterType, Set<String> dataChannels){
        this.lock = new ReentrantLock();
        this.counterTypeAndParamsQueue = new LinkedBlockingQueue<>();
        this.isRunning = true;

        this.counterType = counterType;
        this.dataChannels = dataChannels;
        this.counters = new ConcurrentSkipListMap<>();
    }

    //Initialize counter with passed counter(Counter per service)
    public void registerCounter(AuditCounter statisticsCounter){
        Map<String, AuditCounter> nested = new HashMap<>();
        dataChannels.forEach(dataChannel -> nested.put(dataChannel, statisticsCounter));
        this.counters.put(statisticsCounter.getType(),nested);
    }

    //Add audit stats to corresponding accumulator per data channel per service
    public void addCounter(CounterType counterType, String dataChannel, Object... args) {
        try {
            Boolean offerValue=this.counterTypeAndParamsQueue.offer(new AuditCounterTypeAndParams(counterType, dataChannel, args), 2, TimeUnit.SECONDS);
            LOGGER.debug("OFFER VALUE", offerValue);
        } catch (Exception e) {
            LOGGER.warn("Problem in Accumulator counter while adding to queue new message", e);
        }
    }

    public Map<CounterType, Map<String, AuditCounter>> getCounters() {
        return this.counters;
    }

    @Override
    public void run() {
        while (this.isRunning) {
            try {
                AuditCounterTypeAndParams auditCounterTypeAndParams = this.counterTypeAndParamsQueue.take();
                this.lock.lock();
                this.counters.get(auditCounterTypeAndParams.getCounterType()).get(auditCounterTypeAndParams.getDataChannel()).addCounter(auditCounterTypeAndParams.getParams());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            } finally {
                this.lock.unlock();
            }
        }

    }

    public void shutdown() {
        this.isRunning = false;
    }
}
