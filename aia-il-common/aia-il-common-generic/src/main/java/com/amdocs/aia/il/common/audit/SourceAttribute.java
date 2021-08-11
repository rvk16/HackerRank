package com.amdocs.aia.il.common.audit;

import com.amdocs.aia.il.common.stores.KeyColumn;

import java.io.Serializable;

public class SourceAttribute implements Comparable<SourceAttribute>, Serializable {
    private static final long serialVersionUID = 1879459975268597821L;
    String correlationId;
    long sourceUpdateTime;
    String entityName;
    KeyColumn sourceIds;

    public SourceAttribute(String correlationId, long sourceUpdateTime, String entityName, KeyColumn sourceIds){
        this.correlationId = correlationId;
        this.sourceUpdateTime = sourceUpdateTime;
        this.entityName = entityName;
        this.sourceIds = sourceIds;
    }

    public long getSourceUpdateTime() {
        return sourceUpdateTime;
    }

    public void setSourceUpdateTime(long sourceUpdateTime) {
        this.sourceUpdateTime = sourceUpdateTime;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public KeyColumn getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(KeyColumn sourceIds) {
        this.sourceIds = sourceIds;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public int compareTo(SourceAttribute sourceAttribute) {
        if(this.sourceUpdateTime >= sourceAttribute.getSourceUpdateTime()){
            return 1;
        }
        if(this.sourceUpdateTime < sourceAttribute.getSourceUpdateTime()){
            return -1;
        }

        return 1;
    }
}
