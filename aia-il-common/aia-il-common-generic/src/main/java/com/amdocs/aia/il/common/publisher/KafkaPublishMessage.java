package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.il.common.stores.KeyColumn;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class KafkaPublishMessage implements Serializable {
    private static final long serialVersionUID =6929513195846149094L;

    String contextName;
    LinkedHashMap<Long, LeadingKeysWithTransactionIds> leadingKeysWithTransactionIdsPerTS;

    public KafkaPublishMessage(String contextName, LinkedHashMap<Long, LeadingKeysWithTransactionIds> leadingKeysWithTransactionIdsPerTS) {
        this.contextName = contextName;
        this.leadingKeysWithTransactionIdsPerTS = leadingKeysWithTransactionIdsPerTS;
    }

    public String getContextName () { return contextName; }

    public void setContextName (String contextName) { this.contextName=contextName; }

    public LinkedHashMap<Long, LeadingKeysWithTransactionIds> getLeadingKeysWithTransactionIdsPerTS() {
        return leadingKeysWithTransactionIdsPerTS;
    }

    public void setLeadingKeysWithTransactionIdsPerTS(LinkedHashMap<Long, LeadingKeysWithTransactionIds> leadingKeysWithTransactionIdsPerTS) {
        this.leadingKeysWithTransactionIdsPerTS = leadingKeysWithTransactionIdsPerTS;
    }

    public static class LeadingKeysWithTransactionIds implements Serializable{
        private Set<KeyColumn> leadingEntityKeys;
        private String transactionId;

        public LeadingKeysWithTransactionIds(Set<KeyColumn> leadingEntityKeys, String transactionId) {
            this.leadingEntityKeys = leadingEntityKeys;
            this.transactionId = transactionId;
        }

        public Set<KeyColumn> getLeadingEntityKeys() {
            return leadingEntityKeys;
        }

        public void setLeadingEntityKeys(Set<KeyColumn> leadingEntityKeys) {
            this.leadingEntityKeys = leadingEntityKeys;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        @Override
        public String toString() {
            return "LeadingKeysWithTransactionIds{" +
                    "leadingEntityKeys=" + leadingEntityKeys +
                    ", transactionId='" + transactionId + '\'' +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "KafkaPublishMessage{" +
                "contextName='" + contextName + '\'' +
                ", leadingKeysWithTransactionIdsPerTS=" + leadingKeysWithTransactionIdsPerTS +
                '}';
    }
}
