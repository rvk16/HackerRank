package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.il.common.stores.scylla.monitor.DbMetrics;
import com.datastax.oss.driver.api.core.CqlSession;

import java.io.Serializable;

public interface IAccumulatorCounter extends Serializable {

    CounterType getType();

    void addCounter(Object... args);

    void report(DbMetrics metrics, CqlSession session);

    void clear();
}