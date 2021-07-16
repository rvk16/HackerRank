package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TransformerPartitionHandler {

    private Integer partitionId;
    private boolean isMessageEmpty = true;
    Map<TargetEntity, Set<RepeatedMessage>> messages = new HashMap ();

    public TransformerPartitionHandler (Integer partitionId) { this.partitionId=partitionId; }

    public Integer getPartitionId () { return partitionId; }

    public boolean isMessageEmpty () { return isMessageEmpty; }

    public Map<TargetEntity, Set<RepeatedMessage>> getMessages () { return messages; }

    public void addMessages(TargetEntity targetEntity, RepeatedMessage repeatedMessage) {
        if (messages.containsKey(targetEntity)) {
            messages.get(targetEntity).add(repeatedMessage);
        } else {
            HashSet<RepeatedMessage> set = new HashSet<> ();
            set.add(repeatedMessage);
            messages.put(targetEntity, set);
        }
        isMessageEmpty = false;
    }
}
