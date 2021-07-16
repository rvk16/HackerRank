package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.il.common.stores.KeyColumn;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class KafkaBulkPublishTransaction implements Serializable {
    private static final long serialVersionUID = 6929513195846149094L;
    private Set<KeyColumn> leadingEntityKeys;
    private String contextName;
    private Map<KeyColumn, Map<String,String>> leadingTimeId;

    public Map<KeyColumn, Map<String, String>> getLeadingTimeId() {
        return leadingTimeId;
    }

    public void setLeadingTimeId(Map<KeyColumn, Map<String, String>> leadingTimeId) {
        this.leadingTimeId = leadingTimeId;
    }

    public Set<KeyColumn> getLeadingEntityKeys() {
        return leadingEntityKeys;
    }

    public void setLeadingEntityKeys(Set<KeyColumn> leadingEntityKeys) {
        this.leadingEntityKeys = leadingEntityKeys;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    @Override
    public String toString() {
        return "KafkaBulkPublishTransaction{" +
                "leadingEntityKeys=" + leadingEntityKeys +
                ", contextName='" + contextName + '\'' +
                ", leadingTimeId=" + leadingTimeId +
                '}';
    }
}
