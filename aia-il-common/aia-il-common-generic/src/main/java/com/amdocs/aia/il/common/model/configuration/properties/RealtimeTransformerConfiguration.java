package com.amdocs.aia.il.common.model.configuration.properties;

import com.amdocs.aia.il.common.constant.RTPConstants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@ConfigurationProperties(prefix = "aia.il.transformer")
@Getter
@Setter
public class RealtimeTransformerConfiguration {

    private String subjectAreas = "";
    private int retryTTL;
    private int retryInterval;
    private int parallelThreads = 10;
    private int maxPendingUpdates = 1000;
    private boolean printStatistics = true;
    private boolean dayLightSaving;
    private boolean createDbDebugFile;
    private int referenceTablesRefreshIntervalInMin = 10;
    private String sharedStoragePath;
    private int maxPendingUpdatesSplitSize=10000;
    private String publisherType;
    private long kafkaPollTimeOut = 10;
    private boolean backupDBToFile; //default false cause it is only for DEBUG
    private boolean loadReferenceTablesFromDBFile; //indicator if to load the reference tables from DB dump file or regular
    // this is for storing the data for last refreshed time at runtime
    private long referenceTablesLastRefreshTime = RTPConstants.RefreshReferenceTableScheduler.NOT_INITIALIZED_REFRESH_VALUE;
    private int maxPendingResults = 100;
    private String kafkaGroupID = "non-transient-transformer-group";
    private boolean incrementalUpsert;
    private int timeToLive = 172800;
    private String jobName = "transformer";
    private long errorRecordThreshold = 1000L;
    private long metricsSchedulerTimeInMS = 15000;
    private int maxSqliteInstances;
    private boolean conflictingSubBatches;
    private String outputFormat;
    private boolean createErrorFile;
    private boolean errorHandlerEnabled;
    private String auditTopicSuffix;
    private String auditEnable;
    private String refDataUpdateNotificationConfigPath;

    public String getSharedStoragePath() {
        if (StringUtils.isNotBlank(this.jobName)) {
            return sharedStoragePath + File.separator + jobName;
        } else {
            return this.sharedStoragePath;
        }
    }

    public String getBaseSharedPath() {
        return sharedStoragePath;
    }

    public void resetReferenceTablesLastRefreshTime() {
        this.referenceTablesLastRefreshTime = RTPConstants.RefreshReferenceTableScheduler.NOT_INITIALIZED_REFRESH_VALUE;
    }

    @Override
    public String toString() {
        return "RealtimeTransformerConfiguration{" +
                "subjectAreas='" + subjectAreas + '\'' +
                ", retryTTL=" + retryTTL +
                ", retryInterval=" + retryInterval +
                ", parallelThreads=" + parallelThreads +
                ", maxPendingUpdates=" + maxPendingUpdates +
                ", printStatistics=" + printStatistics +
                ", dayLightSaving=" + dayLightSaving +
                ", createDbDebugFile=" + createDbDebugFile +
                ", referenceTablesRefreshIntervalInMin=" + referenceTablesRefreshIntervalInMin +
                ", sharedStoragePath='" + sharedStoragePath + '\'' +
                ", maxPendingUpdatesSplitSize=" + maxPendingUpdatesSplitSize +
                ", publisherType='" + publisherType + '\'' +
                ", kafkaPollTimeOut=" + kafkaPollTimeOut +
                ", backupDBToFile=" + backupDBToFile +
                ", loadReferenceTablesFromDBFile=" + loadReferenceTablesFromDBFile +
                ", referenceTablesLastRefreshTime=" + referenceTablesLastRefreshTime +
                ", maxPendingResults=" + maxPendingResults +
                ", kafkaGroupID='" + kafkaGroupID + '\'' +
                ", incrementalUpsert=" + incrementalUpsert +
                ", timeToLive=" + timeToLive +
                ", jobName='" + jobName + '\'' +
                ", errorRecordThreshold=" + errorRecordThreshold +
                ", metricsSchedulerTimeInMS=" + metricsSchedulerTimeInMS +
                ", maxSqliteInstances=" + maxSqliteInstances +
                ", conflictingSubBatches=" + conflictingSubBatches +
                ", outputFormat='" + outputFormat + '\'' +
                ", auditTopicSuffix='" + auditTopicSuffix + '\'' +
                ", auditEnable='" + auditEnable + '\'' +
                ", createErrorFile='" + createErrorFile + '\'' +
                '}';
    }
}