package com.amdocs.aia.il.common.stores;

public class RelationKeyInfo {
    private KeyColumn relationKey;
    private KeyColumn sourceKeys;
    private double tsVersion;
    private boolean bulkProcessedFlag;

    public RelationKeyInfo(KeyColumn relationKey, KeyColumn sourceKeys, double tsVersion, boolean bulkProcessedFlag) {
        this.relationKey = relationKey;
        this.sourceKeys = sourceKeys;
        this.tsVersion = tsVersion;
        this.bulkProcessedFlag = bulkProcessedFlag;
    }

    public Boolean getBulkProcessedFlag() {
        return bulkProcessedFlag;
    }

    public void setBulkProcessedFlag(Boolean bulkProcessedFlag) {
        this.bulkProcessedFlag = bulkProcessedFlag;
    }

    public KeyColumn getRelationKey() {
        return relationKey;
    }

    public void setRelationKey(KeyColumn relationKey) {
        this.relationKey = relationKey;
    }

    public KeyColumn getSourceKeys() {
        return sourceKeys;
    }

    public void setSourceKeys(KeyColumn sourceKeys) {
        this.sourceKeys = sourceKeys;
    }

    public Double getTsVersion() {
        return tsVersion;
    }

    public void setTsVersion(Double tsVersion) {
        this.tsVersion = tsVersion;
    }
}