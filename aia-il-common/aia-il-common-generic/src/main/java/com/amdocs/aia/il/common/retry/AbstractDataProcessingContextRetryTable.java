package com.amdocs.aia.il.common.retry;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.model.configuration.properties.RealtimeTransformerConfiguration;
import com.amdocs.aia.il.common.retry.inmemory.InMemoryDataProcessingContextRetryTable;
import com.amdocs.aia.il.common.retry.scylla.ScyllaDataProcessingContextRetryTable;
import com.amdocs.aia.il.common.utils.FactoryUtils;

import java.util.Collection;
import java.util.List;

public abstract class AbstractDataProcessingContextRetryTable {

    protected String name;
    protected int interval;
    protected int ttl;

    public AbstractDataProcessingContextRetryTable(String name, RealtimeTransformerConfiguration config) {

        this.name = name;
        //get the interval from the RealTimeTransformerConfig
        this.interval = config.getRetryInterval();
        //get the ttls from the RealTimeTransformerConfig
        this.ttl = config.getRetryTTL();
    }

    public abstract void persistNew (String leadingKey, RepeatedMessage message);

    protected abstract void persistUpdate(String leadingKey, DataProcessingContextRetryInfo lastRetryInfoFromTable);

    /**
     * Delete a batch of rows
     *
     * @param leadingKeys - list of leading keys
     */
    public abstract void delete(List<String> leadingKeys);

    /**
     * Persist an updated or new message to the retry table
     * Check firsts if the record exists in HBase, in case not exists than persist new, in case exists then update or
     * report an error
     *
     * @param leadingKey
     * @param message    - message to persist as new in case key was not found in table
     */
    public abstract void persist(String leadingKey, RepeatedMessage message);

    /**
     * Load a retry leading key info from table
     *
     * @param leadingKey - key to load
     * @return {@link DataProcessingContextRetryInfo} if leadingKey found, else null
     */
    public abstract DataProcessingContextRetryInfo load(String leadingKey);

    /**
     * @return all retry leading messages from table
     */
    public abstract Collection<DataProcessingContextRetryInfo> loadAll(boolean filterMessagesThatShouldBeProcessed);

    /**
     * Get the TTL to store
     *
     * @param lastTTL
     * @return
     */
    protected int getNextTTL(Integer lastTTL) {
        if (lastTTL == null) {
            //return the ttl defined
            return this.ttl;
        }
        int nextTTL = lastTTL - 1;
        return (nextTTL < 0) ? 0 : nextTTL;
    }

    /**
     * Get the next time to retry and process the leading key
     * In case it is the first time, than lastScheduleTime will be null and result will be now + interval
     * In case it is NOT the first time, than result will be lastScheduleTime + interval
     *
     * @param lastScheduleTime
     * @return next time to retry and process the leading key
     */
    protected long getNextRetryScheduleTime(Long lastScheduleTime) {
        if (lastScheduleTime == null) {
            return System.currentTimeMillis() + (this.interval * 1000);
        }
        return lastScheduleTime + (this.interval * 1000);
    }

    public static AbstractDataProcessingContextRetryTable create(String name, RealtimeTransformerConfiguration config) {
        switch (FactoryUtils.resolvePublisherType(config.getPublisherType())) {
            case SCYLLA:
                return new ScyllaDataProcessingContextRetryTable(name, config);
            case INMEMORYDB:
                return new InMemoryDataProcessingContextRetryTable(name, config);
            default:
                throw new IllegalArgumentException("Invalid publisher type");
        }
    }


}
