package com.amdocs.aia.il.common.model.configuration.properties;

import java.io.Serializable;
import java.util.Optional;

import com.amdocs.aia.il.common.log.LogMsg;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aia.il.compute.leading.key")
@Component
@Getter
@Setter
public class ComputeLeadingConfiguration implements Serializable {
    private static final long serialVersionUID = 6545740109606139678L;
    private String datachannels;
    private String replicateTables;
    private String excludeTables;
    private String errorDir;
    private int parallelThreads = 10;
    private int parallelThreadsToProcessRecords = 1;
    private int maxPendingUpdates = 1000;
    private int maxPendingResults;
    private int maxRecordsPerPoll;
    private int pollingInterval;
    private boolean printStatistics = true;
    private long executionIntervalInSec = 300L;
    private boolean dayLightSaving;
    private boolean incrementalUpdateInScylla;
    private int maxResultsetSize = 10;
    private String publisherType;
    private boolean useRelationTable;
    private boolean isDaemonMode;
    private boolean isWithLeading;
    private boolean incrementalUpsert;
    private int timeToLive = 120;
    private String kafkaGroupID = "computeleadingkeysgroup";
    private long waitTime = 500L;
    private long metricsSchedulerTimeInMS = 15000;
    private boolean errorHandlerEnabled;
    private long kafkaPollTime = 10;



    private String auditTopicSuffix;
    //auditlog
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

    public boolean isAuditLogsEnabled() {
        try {
            setAuditFlag();
        } catch (final Exception e) {
            throw new RuntimeException(LogMsg.getMessage("AUDIT_ENABLE_FLAG_IS_EMPTY", //NOSONAR
                    e.getMessage(), e));
        }
        return auditLogsEnabled;
    }

    public void setAuditLogsEnabled(boolean auditLogsEnabled) {
        this.auditLogsEnabled = auditLogsEnabled;
    }

    public String getAuditTopicSuffix() {
        return auditTopicSuffix;
    }

    public void setAuditTopicSuffix(String auditTopicSuffix) {
        this.auditTopicSuffix = auditTopicSuffix;
    }

    public boolean isErrorHandlerEnabled() {
        return errorHandlerEnabled;
    }

    public void setErrorHandlerEnabled(boolean errorHandlerEnabled) {
        this.errorHandlerEnabled = errorHandlerEnabled;
    }

    @Override
    public String toString() {
        return "ComputeLeadingConfiguration{" +
                "datachannels='" + datachannels + '\'' +
                ", replicateTables='" + replicateTables + '\'' +
                ", excludeTables='" + excludeTables + '\'' +
                ", errorDir='" + errorDir + '\'' +
                ", parallelThreads=" + parallelThreads +
                ", maxPendingUpdates=" + maxPendingUpdates +
                ", printStatistics=" + printStatistics +
                ", executionIntervalInSec=" + executionIntervalInSec +
                ", dayLightSaving=" + dayLightSaving +
                ", incrementalUpdateInScylla=" + incrementalUpdateInScylla +
                ", maxResultsetSize=" + maxResultsetSize +
                ", publisherType='" + publisherType + '\'' +
                ", useRelationTable=" + useRelationTable +
                ", isDaemonMode=" + isDaemonMode +
                ", isWithLeading=" + isWithLeading +
                ", incrementalUpsert=" + incrementalUpsert +
                ", timeToLive=" + timeToLive +
                ", waitTime=" + waitTime +
                ", errorHandlerEnabled=" + errorHandlerEnabled +
                '}';
    }
}
