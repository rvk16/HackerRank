package com.amdocs.aia.il.common.stores;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NonConflictingSubBatchInfo {
    private long timestamp;
    private String transactionIds;
    private Map<String, Set<KeyColumn>> updatedRows;
    private Map<Long, List<Map<String,Set<KeyColumn>>>> conflictingKeysOfSubBatchPerTS;

    public NonConflictingSubBatchInfo(long timestamp, String transactionIds, Map<String, Set<KeyColumn>> updatedRows,
                                      Map<Long,List<Map<String,Set<KeyColumn>>>> conflictingKeysOfSubBatchPerTS) {
        this.timestamp = timestamp;
        this.transactionIds = transactionIds;
        this.updatedRows = updatedRows;
        this.conflictingKeysOfSubBatchPerTS = conflictingKeysOfSubBatchPerTS;
    }

    public Map<Long, List<Map<String, Set<KeyColumn>>>> getConflictingKeysOfSubBatchPerTS() {
        return conflictingKeysOfSubBatchPerTS;
    }

    public void setConflictingKeysOfSubBatchPerTSList(Map<Long, List<Map<String, Set<KeyColumn>>>> conflictingKeysOfSubBatchPerTS) {
        this.conflictingKeysOfSubBatchPerTS = conflictingKeysOfSubBatchPerTS;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Set<KeyColumn>> getUpdatedRows() {
        return updatedRows;
    }

    public void setUpdatedRows(Map<String, Set<KeyColumn>> updatedRows) {
        this.updatedRows = updatedRows;
    }

    public String getTransactionIds() {
        return transactionIds;
    }

    public void setTransactionIds(String transactionIds) {
        this.transactionIds = transactionIds;
    }
}
