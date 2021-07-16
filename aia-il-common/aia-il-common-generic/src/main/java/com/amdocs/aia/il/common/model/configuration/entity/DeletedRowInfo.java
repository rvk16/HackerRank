package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;

import java.io.Serializable;

public class DeletedRowInfo implements Serializable {

    private RepeatedMessage currentMessage;
    private String rowKey;
    private Long dataBaseTimeStamp;
    private Double existingMessageTsVersion;

    public DeletedRowInfo(RepeatedMessage currentMessage, String rowKey, Long dataBaseTimeStamp, Double existingMessageTsVersion) {
        this.currentMessage = currentMessage;
        this.rowKey = rowKey;
        this.dataBaseTimeStamp = dataBaseTimeStamp;
        this.existingMessageTsVersion = existingMessageTsVersion;
    }

    public void setCurrentMessage(RepeatedMessage currentMessage) {
        this.currentMessage = currentMessage;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public void setDataBaseTimeStamp(Long dataBaseTimeStamp) {
        this.dataBaseTimeStamp = dataBaseTimeStamp;
    }

    public void setExistingMessageTsVersion(Double existingMessageTsVersion) {
        this.existingMessageTsVersion = existingMessageTsVersion;
    }

    public RepeatedMessage getCurrentMessage() {
        return currentMessage;
    }

    public String getRowKey() {
        return rowKey;
    }

    public Long getDataBaseTimeStamp() {
        return dataBaseTimeStamp;
    }

    public Double getExistingMessageTsVersion() {
        return existingMessageTsVersion;
    }
}
