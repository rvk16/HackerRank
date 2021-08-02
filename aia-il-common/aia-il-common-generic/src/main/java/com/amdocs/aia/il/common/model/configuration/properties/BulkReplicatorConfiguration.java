package com.amdocs.aia.il.common.model.configuration.properties;


import org.springframework.stereotype.Component;

import java.io.Serializable;

@org.springframework.boot.context.properties.ConfigurationProperties(prefix = "aia.il.bulk.replicator")
@Component
public class BulkReplicatorConfiguration implements Serializable {
    private static final long serialVersionUID = 6545740109606139678L;


    private String datachannels;
    private String replicateTables;
    private String excludeTables;
    private String errorDir;
    private int kafkaPollTime = 10;
    private int parallelThreadsToUpsertRecords = 10;
    private int maxPendingUpdates = 1000;
    private boolean printStatistics = true;
    private long executionIntervalInSec = 300L;
    private boolean dayLightSaving;
    private boolean incrementalUpdateInScylla;
    private int maxResultsetSize = 10;
    private String publisherType;
    private boolean useRelationTable;
    private boolean mergeMode;
    private boolean incrementalUpsert;
    private int timeToLive = 120;
    private long waitTime = 500L;
    private boolean threadPerRecord;
    private int parallelThreadsToProcessRecords = 1;
    private String auditTopicSuffix;
    private String auditEnable;
    private boolean errorHandlerEnabled;
    private long delayBeforejobEnd = 30000;

    public long getDelayBeforejobEnd() {
        return delayBeforejobEnd;
    }

    public void setDelayBeforejobEnd(long delayBeforejobEnd) {
        this.delayBeforejobEnd = delayBeforejobEnd;
    }

    public String getAuditEnable() {
        return auditEnable;
    }

    public void setAuditEnable(String auditEnable) {
        this.auditEnable = auditEnable;
    }

    public String getAuditTopicSuffix() {
        return auditTopicSuffix;
    }

    public void setAuditTopicSuffix(String auditTopicSuffix) {
        this.auditTopicSuffix = auditTopicSuffix;
    }

    public int getKafkaPollTime() {
        return kafkaPollTime;
    }

    public void setKafkaPollTime(int kafkaPollTime) {
        this.kafkaPollTime = kafkaPollTime;
    }

    public long getMetricsSchedulerTimeInMS() {
        return metricsSchedulerTimeInMS;
    }

    public void setMetricsSchedulerTimeInMS(long metricsSchedulerTimeInMS) {
        this.metricsSchedulerTimeInMS = metricsSchedulerTimeInMS;
    }

    private long metricsSchedulerTimeInMS = 500;

    public boolean isIncrementalUpsert() {
        return incrementalUpsert;
    }

    public void setIncrementalUpsert(boolean incrementalUpsert) {
        this.incrementalUpsert = incrementalUpsert;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public String getErrorDir() {
        return errorDir;
    }

    public void setErrorDir(String errorDir) {
        this.errorDir = errorDir;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public boolean isUseRelationTable() {
        return useRelationTable;
    }

    public void setUseRelationTable(boolean useRelationTable) {
        this.useRelationTable = useRelationTable;
    }

    public boolean isMergeMode() {
        return mergeMode;
    }

    public void setMergeMode(boolean mergeMode) {
        this.mergeMode = mergeMode;
    }

    public String getDatachannels() {
        return datachannels;
    }

    public void setDatachannels(String datachannels) {
        this.datachannels = datachannels;
    }

    public String getReplicateTables() {
        return replicateTables;
    }

    public void setReplicateTables(String replicateTables) {
        this.replicateTables = replicateTables;
    }

    public String getExcludeTables() {
        return excludeTables;
    }

    public void setExcludeTables(String excludeTables) {
        this.excludeTables = excludeTables;
    }


    public long getExecutionIntervalInSec() {
        return executionIntervalInSec;
    }

    public void setExecutionIntervalInSec(long executionIntervalInSec) {
        this.executionIntervalInSec = executionIntervalInSec;
    }

    public boolean isIncrementalUpdateInScylla() {
        return incrementalUpdateInScylla;
    }

    public void setIncrementalUpdateInScylla(boolean incrementalUpdateInScylla) {
        this.incrementalUpdateInScylla = incrementalUpdateInScylla;
    }

    public int getParallelThreadsToUpsertRecords() {
        return parallelThreadsToUpsertRecords;
    }

    public void setParallelThreadsToUpsertRecords(int parallelThreadsToUpsertRecords) {
        this.parallelThreadsToUpsertRecords = parallelThreadsToUpsertRecords;
    }

    public int getMaxPendingUpdates() {
        return maxPendingUpdates;
    }

    public void setMaxPendingUpdates(int maxPendingUpdates) {
        this.maxPendingUpdates = maxPendingUpdates;
    }

    public int getMaxResultsetSize() {
        return maxResultsetSize;
    }

    public void setMaxResultsetSize(int maxResultsetSize) {
        this.maxResultsetSize = maxResultsetSize;
    }

    public boolean isPrintStatistics() {
        return printStatistics;
    }

    public void setPrintStatistics(boolean printStatistics) {
        this.printStatistics = printStatistics;
    }

    public boolean isDayLightSaving() {
        return dayLightSaving;
    }

    public void setDayLightSaving(boolean dayLightSaving) {
        this.dayLightSaving = dayLightSaving;
    }

    public String getPublisherType() {
        return publisherType;
    }

    public void setPublisherType(String publisherType) {
        this.publisherType = publisherType;
    }

    public boolean isThreadPerRecord() {
        return threadPerRecord;
    }

    public void setThreadPerRecord(boolean threadPerRecord) {
        this.threadPerRecord = threadPerRecord;
    }

    public int getParallelThreadsToProcessRecords() {
        return parallelThreadsToProcessRecords;
    }

    public void setParallelThreadsToProcessRecords(int parallelThreadsToProcessRecords) {
        this.parallelThreadsToProcessRecords = parallelThreadsToProcessRecords;
    }
    public boolean isErrorHandlerEnabled() {
        return errorHandlerEnabled;
    }

    public void setErrorHandlerEnabled(boolean errorHandlerEnabled) {
        this.errorHandlerEnabled = errorHandlerEnabled;
    }

    @Override
    public String toString() {
        return "BulkReplicatorConfiguration{" +
                "datachannels='" + datachannels + '\'' +
                ", replicateTables='" + replicateTables + '\'' +
                ", excludeTables='" + excludeTables + '\'' +
                ", errorDir='" + errorDir + '\'' +
                ", parallelThreads=" + parallelThreadsToUpsertRecords +
                ", maxPendingUpdates=" + maxPendingUpdates +
                ", printStatistics=" + printStatistics +
                ", executionIntervalInSec=" + executionIntervalInSec +
                ", dayLightSaving=" + dayLightSaving +
                ", incrementalUpdateInScylla=" + incrementalUpdateInScylla +
                ", maxResultsetSize=" + maxResultsetSize +
                ", publisherType='" + publisherType + '\'' +
                ", useRelationTable=" + useRelationTable +
                ", isMergeMode=" + mergeMode +
                ", incrementalUpsert=" + incrementalUpsert +
                ", timeToLive=" + timeToLive +
                ", waitTime=" + waitTime +
                ", threadPerRecord=" + threadPerRecord +
                ", parallelThreadsToProcessRecords=" + parallelThreadsToProcessRecords +
                ", metricsSchedulerTimeInMS=" + metricsSchedulerTimeInMS +
                ", errorHandlerEnabled=" + errorHandlerEnabled +
                '}';
    }
}