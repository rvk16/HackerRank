package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.il.common.stores.KeyColumn;

import java.util.*;

public class PartitionHandler {

    private Integer partitionId;
    private boolean isMessageEmpty = true;
    Map<String, Map<Long, ReplicatorOutputMessage>> messagesPerContext = new HashMap();
    private KafkaPublishTransaction kafkaPublishTransaction = new KafkaPublishTransaction();

    public PartitionHandler(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public Integer getPartitionId() {
        return partitionId;
    }

    public KafkaPublishTransaction getKafkaPublishTransaction() {
        return kafkaPublishTransaction;
    }

    public boolean isMessageEmpty() {
        return isMessageEmpty;
    }

    /**
     * Add messages to be published to Kafka 1.5.
     *
     * @param contextName
     * @param timeStamp
     * @param transactionIDs
     * @param leadingKeyInfo
     * @param keyColumn
     */
    public void addMessages(String contextName, Long timeStamp, String transactionIDs, Map<String, String> leadingKeyInfo, KeyColumn keyColumn) {
        if (messagesPerContext.containsKey(contextName)) {
            if (messagesPerContext.get(contextName).containsKey(timeStamp)) {
                messagesPerContext.get(contextName).get(timeStamp).getKeyColumnSet().add(keyColumn);
                messagesPerContext.get(contextName).get(timeStamp).getLeadingTimeId().put(keyColumn, leadingKeyInfo);
            } else {
                Set<KeyColumn> set = new HashSet<>();
                set.add(keyColumn);
                Map<KeyColumn, Map<String, String>> leadingTimeId = new HashMap<>();
                leadingTimeId.put(keyColumn, leadingKeyInfo);
                messagesPerContext.get(contextName).put(timeStamp, new ReplicatorOutputMessage(contextName, set, transactionIDs, timeStamp, leadingTimeId));
            }
        } else {
            Set<KeyColumn> set = new HashSet<>();
            set.add(keyColumn);
            Map<KeyColumn, Map<String, String>> leadingTimeId = new HashMap<>();
            leadingTimeId.put(keyColumn, leadingKeyInfo);
            ReplicatorOutputMessage replicatorOutputMessage = new ReplicatorOutputMessage(contextName, set, transactionIDs, timeStamp, leadingTimeId);
            Map<Long, ReplicatorOutputMessage> partitionHandlerMessagePerTS = new LinkedHashMap<>();
            partitionHandlerMessagePerTS.put(timeStamp, replicatorOutputMessage);
            messagesPerContext.put(contextName, partitionHandlerMessagePerTS);
        }
        isMessageEmpty = false;
    }

    public void addMessages(String contextName, Long timeStamp, String transactionIDs, KeyColumn keyColumn) {
        if (messagesPerContext.containsKey(contextName)) {
            if (messagesPerContext.get(contextName).containsKey(timeStamp)) {
                messagesPerContext.get(contextName).get(timeStamp).getKeyColumnSet().add(keyColumn);
            } else {
                Set<KeyColumn> set = new HashSet<>();
                set.add(keyColumn);
                messagesPerContext.get(contextName).put(timeStamp, new ReplicatorOutputMessage(contextName, set, transactionIDs, timeStamp));
            }
        } else {
            Set<KeyColumn> set = new HashSet<>();
            set.add(keyColumn);
            ReplicatorOutputMessage replicatorOutputMessage = new ReplicatorOutputMessage(contextName, set, transactionIDs, timeStamp);
            Map<Long, ReplicatorOutputMessage> partitionHandlerMessagePerTS = new LinkedHashMap<>();
            partitionHandlerMessagePerTS.put(timeStamp, replicatorOutputMessage);
            messagesPerContext.put(contextName, partitionHandlerMessagePerTS);
        }
        isMessageEmpty = false;
    }
    public void computeTransaction() {
        List<ReplicatorOutputMessage> replicatorOutputMessagesList = new ArrayList<>();
        messagesPerContext.forEach((contextName, repMsgPerTS) -> {
            repMsgPerTS.forEach((time, replicatorOutputMessage) -> {
                replicatorOutputMessagesList.add(replicatorOutputMessage);
            });
        });
        kafkaPublishTransaction.setReplicatorOutputMessageList(replicatorOutputMessagesList);
    }

    public void clear() {
        this.isMessageEmpty = true;
        this.messagesPerContext.clear();
        this.kafkaPublishTransaction = new KafkaPublishTransaction();
    }
}