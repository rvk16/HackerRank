package com.amdocs.aia.il.common.model.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@ConfigurationProperties(prefix = "aia.il.reference.store.publisher")
public class ReferenceStorePublisherConfigurations extends RealtimeTransformerConfiguration implements Serializable {

    private static final long serialVersionUID = 6545740109606139678L;

    private int parallelThreads=10;
    private int maxPendingUpdates;
    private String publisherType;
    private boolean dayLightSaving;
    private boolean incrementalUpsert;
    private int timeToLive = 120;
    private boolean isLoadReferenceTablesFromDBFile;
    private boolean isBackupDBToFile;
    private String sharedStoragePath;
    private String jobName;
    private boolean isConflictingSubBatches;
    private String referenceTablePublisherState;
    private String notifyTransformerState;
    private String outputFormat;
    private boolean createErrorFile;

    public int getParallelThreads() {
        return parallelThreads;
    }

    public void setParallelThreads(int parallelThreads) {
        this.parallelThreads = parallelThreads;
    }

    public int getMaxPendingUpdates() {
        return maxPendingUpdates;
    }

    public void setMaxPendingUpdates(int maxPendingUpdates) {
        this.maxPendingUpdates = maxPendingUpdates;
    }

    public String getPublisherType() { return publisherType; }

    public void setPublisherType(String publisherType) { this.publisherType = publisherType; }

    public boolean isDayLightSaving() {
        return dayLightSaving;
    }

    public void setDayLightSaving(boolean dayLightSaving) {
        this.dayLightSaving = dayLightSaving;
    }

    public boolean isIncrementalUpsert() {
        return incrementalUpsert;
    }

    public void setIncrementalUpsert(boolean incrementalUpsert) {
        this.incrementalUpsert = incrementalUpsert;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public void setLoadReferenceTablesFromDBFile(boolean loadReferenceTablesFromDBFile) { isLoadReferenceTablesFromDBFile = loadReferenceTablesFromDBFile; }

    public boolean isLoadReferenceTablesFromDBFile() { return isLoadReferenceTablesFromDBFile; }

    public void setBackupDBToFile(boolean backupDBToFile) { isBackupDBToFile = backupDBToFile; }

    public boolean isBackupDBToFile() { return isBackupDBToFile; }

    public void setSharedStoragePath(String sharedStoragePath) { this.sharedStoragePath = sharedStoragePath; }

    public String getSharedStoragePath() { return sharedStoragePath; }

    public void setJobName(String jobName) { this.jobName = jobName; }

    public String getJobName() { return jobName; }

    public void setConflictingSubBatches(boolean conflictingSubBatches) { isConflictingSubBatches = conflictingSubBatches; }

    public boolean isConflictingSubBatches() { return isConflictingSubBatches; }

    public String getReferenceTablePublisherState() { return referenceTablePublisherState; }

    public void setReferenceTablePublisherState(String referenceTablePublisherState) { this.referenceTablePublisherState = referenceTablePublisherState; }

    public String getNotifyTransformerState() { return notifyTransformerState; }

    public void setNotifyTransformerState(String notifyTransformerState) { this.notifyTransformerState = notifyTransformerState; }

    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }

    public String getOutputFormat() { return this.outputFormat; }

    public boolean isCreateErrorFile() {
        return createErrorFile;
    }

    public void setCreateErrorFile(boolean createErrorFile) {
        this.createErrorFile = createErrorFile;
    }

    @Override
    public String toString() {
        return "ReferenceStorePublisherConfigurations{" +
                "parallelThreads=" + parallelThreads +
                ", maxPendingUpdates=" + maxPendingUpdates +
                ", publisherType='" + publisherType + '\'' +
                ", dayLightSaving=" + dayLightSaving +
                ", incrementalUpsert=" + incrementalUpsert +
                ", timeToLive=" + timeToLive +
                ", isLoadReferenceTablesFromDBFile=" + isLoadReferenceTablesFromDBFile +
                ", isBackupDBToFile=" + isBackupDBToFile +
                ", sharedStoragePath='" + sharedStoragePath + '\'' +
                ", jobName='" + jobName + '\'' +
                ", isConflictingSubBatches=" + isConflictingSubBatches +
                ", referenceTablePublisherState='" + referenceTablePublisherState + '\'' +
                ", notifyTransformerState='" + notifyTransformerState + '\'' +
                ", outputFormat='" + outputFormat + '\'' +
                '}';
    }
}
