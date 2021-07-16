package com.amdocs.aia.il.common.retry;

import java.util.Arrays;

public class DataProcessingContextRetryInfo {

    private final String leadingKey;
    private final String contextName;
    private final int ttl;
    private final long nextRetryTime;
    private final int interval;
    private final byte[] leadingTableData;

    public DataProcessingContextRetryInfo(String leadingKey, String contextName, int ttl, long nextRetryTime, int interval, byte[] leadingTableData) {
        this.leadingKey = leadingKey;
        this.contextName = contextName;
        this.ttl = ttl;
        this.nextRetryTime = nextRetryTime;
        this.interval = interval;
        this.leadingTableData = leadingTableData;
    }

    public String getLeadingKey() {
        return leadingKey;
    }

    public String getContextName() {
        return contextName;
    }

    public int getTtl() {
        return ttl;
    }

    public long getNextRetryTime() {
        return nextRetryTime;
    }

    public int getInterval() {
        return interval;
    }

    public byte[] getLeadingTableData() {
        return leadingTableData;
    }

    @Override
    public String toString() {
        return "DataProcessingContextRetryInfo{" +
                "leadingKey='" + leadingKey + '\'' +
                ", contextName='" + contextName + '\'' +
                ", ttl=" + ttl +
                ", nextRetryTime=" + nextRetryTime +
                ", interval=" + interval +
                ", leadingTableData=" + Arrays.toString(leadingTableData) +
                '}';
    }
}
