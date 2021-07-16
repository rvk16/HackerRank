package com.amdocs.aia.il.common.stores;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;

import java.util.ArrayList;
import java.util.List;

public class UpsertData {
    private Long timeStamp;
    private List<RepeatedMessage> repeatedMessageList = new ArrayList<>();

    public UpsertData(Long timeStamp, List<RepeatedMessage> repeatedMessageList) {
        this.timeStamp = timeStamp;
        this.repeatedMessageList = repeatedMessageList;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<RepeatedMessage> getRepeatedMessageList() {
        return repeatedMessageList;
    }

    public void setRepeatedMessageList(List<RepeatedMessage> repeatedMessageList) {
        this.repeatedMessageList = repeatedMessageList;
    }
}
