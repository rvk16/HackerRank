package com.amdocs.aia.il.common.stores;

import com.amdocs.aia.il.common.log.LogMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class created specifically to keep the track of data which has been upserted.
 */
public class RowKeysPerTransactionID implements Serializable {
    private static final long serialVersionUID =-7830632662978354760L;
    private static final Logger LOGGER = LoggerFactory.getLogger(RowKeysPerTransactionID.class);

    private String transactionId;
    private Map<String, Set<KeyColumn>> txnUpdatedRows = new HashMap<>();
    private long nonConflictingBatchTimeStamp;

    public RowKeysPerTransactionID() { }

        public RowKeysPerTransactionID(String transactionId, Map<String, Set<KeyColumn>> partialTxnUpdatedRows, long nonConflictingBatchTimeStamp) {
        try {
            this.transactionId = transactionId;
            this.txnUpdatedRows.putAll(partialTxnUpdatedRows);
            this.nonConflictingBatchTimeStamp = nonConflictingBatchTimeStamp;
        } catch (Exception e) {
            throw new RuntimeException(LogMsg.getMessage("Error parsing the partial rowkeys per transaction ID" + e.getMessage()), e);
        }
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Map<String, Set<KeyColumn>> getTxnUpdatedRows() {
        return txnUpdatedRows;
    }

    public void setTxnUpdatedRows(Map<String, Set<KeyColumn>> txnUpdatedRows) {
        this.txnUpdatedRows = txnUpdatedRows;
    }

    public long getNonConflictingBatchTimeStamp() {
        return nonConflictingBatchTimeStamp;
    }

    public void setNonConflictingBatchTimeStamp(long nonConflictingBatchTimeStamp) {
        this.nonConflictingBatchTimeStamp = nonConflictingBatchTimeStamp;
    }

    @Override
    public String toString() {
        return "RowKeysPerTransactionID{" +
                "transactionId='" + transactionId + '\'' +
                ", txnUpdatedRows=" + txnUpdatedRows +
                ", nonConflictingBatchTimeStamp=" + nonConflictingBatchTimeStamp +
                '}';
    }
}