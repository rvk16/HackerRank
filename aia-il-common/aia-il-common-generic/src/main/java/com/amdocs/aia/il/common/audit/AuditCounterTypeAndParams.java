package com.amdocs.aia.il.common.audit;

import com.amdocs.aia.il.common.publisher.CounterType;

public class AuditCounterTypeAndParams {

    private final CounterType counterType;
    private final Object[] params;
    private final String dataChannel;

    public AuditCounterTypeAndParams(CounterType counterType, String dataChannel, Object[] params) {
        this.counterType = counterType;
        this.params = params;
        this.dataChannel = dataChannel;
    }

    public CounterType getCounterType() {
        return counterType;
    }

    public Object[] getParams() {
        return params;
    }

    public String getDataChannel() {
        return dataChannel;
    }


}
