package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.il.common.stores.KeyColumn;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * PartitionHandlerMessage structure to be passed to Transformer.
 */
public class  ReplicatorOutputMessage implements Serializable,Comparable<ReplicatorOutputMessage> {
    private static final long serialVersionUID = 5727056044397805063L;
    private String contextName;
    private Set<KeyColumn> keyColumnSet;
    private String transactionIDs;
    private long timeStamp;
    private Map<KeyColumn, Map<String,String>> leadingTimeId = new HashMap<>();

    public ReplicatorOutputMessage(String contextName, Set<KeyColumn> keyColumnSet, String transactionIDs, long timeStamp) {
        this.contextName = contextName;
        this.keyColumnSet = keyColumnSet;
        this.transactionIDs = transactionIDs;
        this.timeStamp = timeStamp;
    }

    public ReplicatorOutputMessage(String contextName, Set<KeyColumn> keyColumnSet, String transactionIDs, long timeStamp, Map<KeyColumn, Map<String,String>> leadCorrelationSourceMap) {
        this.contextName = contextName;
        this.keyColumnSet = keyColumnSet;
        this.transactionIDs = transactionIDs;
        this.timeStamp = timeStamp;
        this.leadingTimeId = leadCorrelationSourceMap;
    }


    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public Set<KeyColumn> getKeyColumnSet() {
        return keyColumnSet;
    }

    public void setKeyColumnSet(Set<KeyColumn> keyColumnSet) {
        this.keyColumnSet = keyColumnSet;
    }

    public String getTransactionIDs() {
        return transactionIDs;
    }

    public void setTransactionIDs(String transactionIDs) {
        this.transactionIDs = transactionIDs;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Map<KeyColumn, Map<String,String>> getLeadingTimeId() {
        return leadingTimeId;
    }

    @Override
    public String toString() {
        return "ReplicatorOutputMessage{" +
                "contextName='" + contextName + '\'' +
                ", keyColumnSet=" + keyColumnSet +
                ", transactionIDs='" + transactionIDs + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }

    @Override
    public int compareTo(ReplicatorOutputMessage replicatorOutputMessage) {
        if(this.timeStamp > replicatorOutputMessage.getTimeStamp()){
            return 1;
        } else if(this.timeStamp < replicatorOutputMessage.getTimeStamp()){
            return -1;
        } else{
            return 0;
        }

    }
}