package com.amdocs.aia.il.common.publisher;

/**
 * Holds a message type and params which will be saved until processing in a queue
 */
public class CounterTypeAndParams {
    private final CounterType counterType;
    private final Object[] params;

    public CounterTypeAndParams(CounterType counterType, Object[] params) {
        this.counterType = counterType;
        this.params = params;
    }

    public CounterType getCounterType() {
        return counterType;
    }

    public Object[] getParams() {
        return params;
    }

}
