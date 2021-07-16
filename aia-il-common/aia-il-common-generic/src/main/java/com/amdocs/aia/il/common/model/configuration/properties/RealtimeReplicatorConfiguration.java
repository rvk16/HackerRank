package com.amdocs.aia.il.common.model.configuration.properties;

import com.amdocs.aia.il.common.log.LogMsg;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@org.springframework.boot.context.properties.ConfigurationProperties(prefix = "aia.il.replicator")
@Component
public class RealtimeReplicatorConfiguration implements Serializable {
    private static final long serialVersionUID = 6545740109606139678L;

    private String datachannels = "";
    private int parallelThreads = 10;
    private int maxPendingUpdates;
    private int maxPendingResults;
    private int maxPendingUpdatesSplitSize;
    private boolean printStatistics = true;
    private boolean printStatisticsMB;
    private boolean dayLightSaving;
    private int batchSize;
    private String publisherType;
    private int replicatorParallelProcessThreads;
    private boolean incrementalUpsert = false;
    private long kafkaPollTime = 10;
    private int timeToLive = 120;
    private String kafkaGroupID = "replicatorgroup";
    private long metricsSchedulerTimeInMS = 500;
    private boolean contextOptimization = false;
    private String auditTopicSuffix;
    private boolean errorHandlerEnabled;

    //auditlog
    private String auditEnable;

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

    public String getDatachannels() {
        return datachannels;
    }

    public void setDatachannels(String datachannels) {
        this.datachannels = datachannels;
    }

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

    public int getMaxPendingResults() {
        return maxPendingResults;
    }

    public void setMaxPendingResults(int maxPendingResults) {
        this.maxPendingResults = maxPendingResults;
    }

    public int getMaxPendingUpdatesSplitSize() {
        return maxPendingUpdatesSplitSize;
    }

    public void setMaxPendingUpdatesSplitSize(int maxPendingUpdatesSplitSize) {
        this.maxPendingUpdatesSplitSize = maxPendingUpdatesSplitSize;
    }

    public boolean isPrintStatistics() {
        return printStatistics;
    }

    public void setPrintStatistics(boolean printStatistics) {
        this.printStatistics = printStatistics;
    }

    public boolean isPrintStatisticsMB() {
        return printStatisticsMB;
    }

    public void setPrintStatisticsMB(boolean printStatisticsMB) {
        this.printStatisticsMB = printStatisticsMB;
    }

    public boolean isDayLightSaving() {
        return dayLightSaving;
    }

    public void setDayLightSaving(boolean dayLightSaving) {
        this.dayLightSaving = dayLightSaving;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getPublisherType() {
        return publisherType;
    }

    public void setPublisherType(String publisherType) {
        this.publisherType = publisherType;
    }

    public int getReplicatorParallelProcessThreads() {
        return replicatorParallelProcessThreads;
    }

    public void setReplicatorParallelProcessThreads(int replicatorParallelProcessThreads) {
        this.replicatorParallelProcessThreads = replicatorParallelProcessThreads;
    }

    public boolean isIncrementalUpsert() {
        return incrementalUpsert;
    }

    public void setIncrementalUpsert(boolean incrementalUpsert) {
        this.incrementalUpsert = incrementalUpsert;
    }

    public long getKafkaPollTime() {
        return kafkaPollTime;
    }

    public void setKafkaPollTime(long kafkaPollTime) {
        this.kafkaPollTime = kafkaPollTime;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public String getKafkaGroupID() {
        return kafkaGroupID;
    }

    public void setKafkaGroupID(String kafkaGroupID) {
        this.kafkaGroupID = kafkaGroupID;
    }

    public boolean isContextOptimization() {
        return contextOptimization;
    }

    public void setContextOptimization(boolean contextOptimization) {
        this.contextOptimization = contextOptimization;
    }

    public long getMetricsSchedulerTimeInMS() {
        return metricsSchedulerTimeInMS;
    }

    @Override
    public String toString() {
        return "RealtimeReplicatorConfiguration{" +
                "datachannels='" + datachannels + '\'' +
                ", parallelThreads=" + parallelThreads +
                ", maxPendingUpdates=" + maxPendingUpdates +
                ", maxPendingResults=" + maxPendingResults +
                ", maxPendingUpdatesSplitSize=" + maxPendingUpdatesSplitSize +
                ", printStatistics=" + printStatistics +
                ", printStatisticsMB=" + printStatisticsMB +
                ", dayLightSaving=" + dayLightSaving +
                ", batchSize=" + batchSize +
                ", publisherType='" + publisherType + '\'' +
                ", replicatorParallelProcessThreads=" + replicatorParallelProcessThreads +
                ", incrementalUpsert=" + incrementalUpsert +
                ", kafkaPollTime=" + kafkaPollTime +
                ", timeToLive=" + timeToLive +
                ", kafkaGroupID='" + kafkaGroupID + '\'' +
                ", metricsSchedulerTimeInMS=" + metricsSchedulerTimeInMS +
                ", contextOptimization=" + contextOptimization +
                '}';
    }

    public void setMetricsSchedulerTimeInMS(long metricsSchedulerTimeInMS) {
        this.metricsSchedulerTimeInMS = metricsSchedulerTimeInMS;
    }

    public boolean isErrorHandlerEnabled() {
        return errorHandlerEnabled;
    }

    public void setErrorHandlerEnabled(boolean errorHandlerEnabled) {
        this.errorHandlerEnabled = errorHandlerEnabled;
    }
}