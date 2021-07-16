package com.amdocs.aia.il.common.publisher;


/**
 * Holds a message type, datachannels and params which will be saved until processing in a queue
 */
public class CounterTypeDataChannelAndParams {
    private final CounterType counterType;
    private final String dataChannel;
    private final Object[] params;

    public CounterTypeDataChannelAndParams(CounterType counterType, String dataChannel, Object[] params) {
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
