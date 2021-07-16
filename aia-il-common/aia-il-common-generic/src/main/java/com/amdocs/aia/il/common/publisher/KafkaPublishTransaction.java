package com.amdocs.aia.il.common.publisher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KafkaPublishTransaction implements Serializable {
    private static final long serialVersionUID =-2023754831712698235L;

    private List<ReplicatorOutputMessage> replicatorOutputMessageList = new ArrayList<>();

    public List<ReplicatorOutputMessage> getReplicatorOutputMessageList() {
        return replicatorOutputMessageList;
    }

    public void setReplicatorOutputMessageList(List<ReplicatorOutputMessage> replicatorOutputMessageList) {
        this.replicatorOutputMessageList = replicatorOutputMessageList;
    }

    @Override
    public String toString() {
        return "KafkaPublishTransaction{" +
            /*    "publishMessages=" + publishMessages +*/
                ", replicatorOutputMessageList=" + replicatorOutputMessageList +
                '}';
    }
}
