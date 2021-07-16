package com.amdocs.aia.il.common.model.configuration.properties;


import com.amdocs.aia.il.common.constant.RTPConstants;
import com.amdocs.aia.il.common.error.handler.ErrorHandler;
import com.amdocs.aia.il.common.log.LogMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.Serializable;
import java.util.Optional;

@org.springframework.boot.context.properties.ConfigurationProperties(prefix = "aia.il.bulk.transformer")
@Component
public class BulkTransformerConfiguration extends RealtimeTransformerConfiguration implements Serializable, Cloneable {
    private static final long serialVersionUID = 6545740109606139678L;

    private String contexts;
    private String errorDir;
    private int parallelThreads = 10;
    private int maxSqlConnections;
    private int maxPendingResults;
    private int maxPendingUpdatesSplitSize = 100;
    private int referenceTablesRefreshInterveInMin;
    private String sharedStoragePath;
    private boolean backupDBToFile;
    private int maxRecordsPerPoll;
    private int kafkaPollTime = 10;
    private int timeToLive = 120;
    private long waitTime = 500L;
    private String publisherType;
    private boolean conflictingSubBatches = true;
    private int retryTTL;
    private int retryInterval;
    private boolean loadReferenceTablesFromDBFile = false;
    private long referenceTablesLastRefreshTime = RTPConstants.RefreshReferenceTableScheduler.NOT_INITIALIZED_REFRESH_VALUE;
    private String jobName = "bulktransformer";
    private long errorRecordThreshold = 1000l;
    private String outputFormat;
    private boolean createErrorFile;
    private String auditTopicSuffix;
    private boolean auditLogsEnabled;
    private boolean auditEnableFlag;
    private static final String NONE = "None";
    private static final String AUDITONLY = "AuditOnly";
    private static final String AUDITLOGS = "AuditLogs";
    private String auditEnable;

    public String getAuditEnable() {
        return auditEnable;
    }

    public void setAuditEnable(String auditEnable) {
        this.auditEnable = auditEnable;
    }

    public void setAuditFlag() throws RuntimeException {
        Optional<String> optional= Optional.ofNullable(auditEnable);
        switch(optional.get()) {
            case NONE:
                setAuditEnableFlag(false);
                setAuditLogsEnabled(false);
                break;
            case AUDITONLY:
                setAuditLogsEnabled(false);
                setAuditEnableFlag(true);
                break;
            case AUDITLOGS:
                setAuditLogsEnabled(true);
                setAuditEnableFlag(true);
                break;
            default:
                throw new RuntimeException();
        }
    }

    public boolean isAuditEnabled() {
        try {
            setAuditFlag();
        } catch (final Exception e) {
            throw new RuntimeException(LogMsg.getMessage("AUDIT_ENABLE_FLAG_IS_EMPTY", //NOSONAR
                    e.getMessage(), e));
        }

        return auditEnableFlag;
    }

    public void setAuditEnableFlag(boolean auditEnableFlag) {
        this.auditEnableFlag = auditEnableFlag;
    }

    public String getContexts() {
        return contexts;
    }

    public void setContexts(String contexts) {
        this.contexts = contexts;
    }

    public String getErrorDir() {
        return errorDir;
    }

    public void setErrorDir(String errorDir) {
        this.errorDir = errorDir;
    }

    public int getParallelThreads() {
        return parallelThreads;
    }

    public void setParallelThreads(int parallelThreads) {
        this.parallelThreads = parallelThreads;
    }

    public int getMaxSqlConnections() {
        return maxSqlConnections;
    }

    public void setMaxSqlConnections(int maxSqlConnections) {
        this.maxSqlConnections = maxSqlConnections;
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

    public void setMaxPendingUpdatesSplitSize(int maxPendingUpdatesSplitsSize) {
        this.maxPendingUpdatesSplitSize = maxPendingUpdatesSplitsSize;
    }

    public int getReferenceTablesRefreshInterveInMin() {
        return referenceTablesRefreshInterveInMin;
    }

    public void setReferenceTablesRefreshInterveInMin(int referenceTablesRefreshInterveInMin) {
        this.referenceTablesRefreshInterveInMin = referenceTablesRefreshInterveInMin;
    }

    public String getSharedStoragePath() {
        if (StringUtils.isNotBlank(this.jobName)) {
            return sharedStoragePath + File.separator + jobName;
        } else {
            return this.sharedStoragePath;
        }
    }

    public void setSharedStoragePath(String sharedStoragePath) {
        this.sharedStoragePath = sharedStoragePath;
    }

    public boolean isBackupDBToFile() {
        return backupDBToFile;
    }

    public void setBackupDBToFile(boolean backupDBToFile) {
        this.backupDBToFile = backupDBToFile;
    }

    public int getMaxRecordsPerPoll() {
        return maxRecordsPerPoll;
    }

    public void setMaxRecordsPerPoll(int maxRecordsPerPoll) {
        this.maxRecordsPerPoll = maxRecordsPerPoll;
    }

    public int getKafkaPollTime() {
        return kafkaPollTime;
    }

    public void setKafkaPollTime(int kafkaPollTime) {
        this.kafkaPollTime = kafkaPollTime;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public String getPublisherType() {
        return publisherType;
    }

    public void setPublisherType(String publisherType) {
        this.publisherType = publisherType;
    }

    public boolean isConflictingSubBatches() {
        return conflictingSubBatches;
    }

    public void setConflictingSubBatches(boolean conflictingSubBatches) {
        this.conflictingSubBatches = conflictingSubBatches;
    }

    public int getRetryTTL() {
        return retryTTL;
    }

    public void setRetryTTL(int retryTTL) {
        this.retryTTL = retryTTL;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public boolean isLoadReferenceTablesFromDBFile() {
        return loadReferenceTablesFromDBFile;
    }

    public void setLoadReferenceTablesFromDBFile(boolean loadReferenceTablesFromDBFile) {
        this.loadReferenceTablesFromDBFile = loadReferenceTablesFromDBFile;
    }

    public long getReferenceTablesLastRefreshTime() {
        return referenceTablesLastRefreshTime;
    }

    public void setReferenceTablesLastRefreshTime(long referenceTablesLastRefreshTime) {
        this.referenceTablesLastRefreshTime = referenceTablesLastRefreshTime;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public long getErrorRecordThreshold() {
        return errorRecordThreshold;
    }

    public void setErrorRecordThreshold(long errorRecordThreshold) {
        this.errorRecordThreshold = errorRecordThreshold;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public boolean isCreateErrorFile() {
        return createErrorFile;
    }

    public void setCreateErrorFile(boolean createErrorFile) {
        this.createErrorFile = createErrorFile;
    }

    public String getAuditTopicSuffix() {
        return auditTopicSuffix;
    }

    public void setAuditTopicSuffix(String auditTopicSuffix) {
        this.auditTopicSuffix = auditTopicSuffix;
    }

    public boolean isAuditLogsEnabled() {
        return auditLogsEnabled;
    }

    public void setAuditLogsEnabled(boolean auditLogsEnabled) {
        this.auditLogsEnabled = auditLogsEnabled;
    }

    @Override
    public String toString() {
        return "BulkTransformerConfiguration{" +
                ", errorDir='" + errorDir + '\'' +
                ", parallelThreads=" + parallelThreads +
                ", contexts=" + contexts +
                ", paralleThreadsPerSplit=" + maxSqlConnections +
                ", maxPendingResults=" + maxPendingResults +
                ", maxPendingUpdatesSplitsSize=" + maxPendingUpdatesSplitSize +
                ", referenceTablesRefreshInterveInMin=" + referenceTablesRefreshInterveInMin +
                ", sharedStoragePath=" + sharedStoragePath +
                ", backupDBToFile=" + backupDBToFile +
                ", maxRecordsPerPoll=" + maxRecordsPerPoll +
                ", auditTopicSuffix=" + auditTopicSuffix +
                ", auditLogsEnabled=" + auditLogsEnabled +
                ", timeToLive=" + timeToLive +
                ", waitTime=" + waitTime +
                ", outputFormat='" + outputFormat + '\'' +
                ", createErrorFile='" + createErrorFile + '\'' +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}