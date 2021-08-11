package com.amdocs.aia.il.common.publisher;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Holds info for publishing the transaction
 * Created by ORENKAF on 11/15/2016.
 */
public class TrxPublishingInfo implements Serializable {

    private static final long serialVersionUID = 5150426778352997260L;

    private final String txnId;
    private long updateTime;
    private long queryTime;
    private String sourceSystemId;
    private long timestamp;
    private Map<String, Map<String, String>> leadCorrelationSourceMap;
    private List<String> contextContributingColumns;
    private String dataChannel;
    private long arrivalTime;
    private long rtpQueryTime;
    private String targetEntity;

    public TrxPublishingInfo(String txnId, long updateTime, long queryTime, long currentBatchTime) {
        this.txnId = txnId;
        this.updateTime = updateTime;
        this.queryTime = queryTime;
        this.timestamp = currentBatchTime;
    }

    public TrxPublishingInfo(String txnId, long updateTime, long queryTime) {
        this.txnId = txnId;
        this.updateTime = updateTime;
        this.queryTime = queryTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTxnId() {
        return txnId;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public long getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(long queryTime) {
        this.queryTime = queryTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getSourceSystemId() {
        return this.sourceSystemId;
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public Map<String, Map<String, String>> getLeadCorrelationSourceMap() {
        return leadCorrelationSourceMap;
    }

    public void setLeadCorrelationSourceMap(Map<String, Map<String, String>> leadCorrelationSourceMap) {
        this.leadCorrelationSourceMap = leadCorrelationSourceMap;
    }
    public String getDataChannel() {
        return dataChannel;
    }

    public void setDataChannel(String dataChannel) {
        this.dataChannel = dataChannel;
    }
    public List<String> getContextContributingColumns() {
        return contextContributingColumns;
    }

    public void setContextContributingColumns(List<String> contextContributingColumns) {
        this.contextContributingColumns = contextContributingColumns;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public long getRtpQueryTime() {
        return rtpQueryTime;
    }

    public void setRtpQueryTime(long rtpQueryTime) {
        this.rtpQueryTime = rtpQueryTime;
    }

    @Override
    public String toString() {
        return "TrxPublishingInfo{" +
                "txnId='" + txnId + '\'' +
                ", updateTime=" + updateTime +
                ", queryTime=" + queryTime +
                ", sourceSystemId='" + sourceSystemId + '\'' +
                ", timestamp=" + timestamp +
                ", arrivalTime=" + arrivalTime +
                ", rtpQueryTime=" + rtpQueryTime +
                ", dataChannel=" + dataChannel +
                ", targetEntity=" + targetEntity +
                '}';
    }
}
