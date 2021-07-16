package com.amdocs.aia.il.common.audit;

import com.amdocs.aia.il.common.publisher.CounterType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface AuditCounter extends Serializable {

    public CounterType getType();

    public void addCounter(Object... args);

    public List<String> getMessageStructure();

    public void setContextLead(Map<String, String> contextLead);

    public void clear();
}
