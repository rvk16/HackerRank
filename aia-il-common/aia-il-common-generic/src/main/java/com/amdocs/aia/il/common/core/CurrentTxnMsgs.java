package com.amdocs.aia.il.common.core;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.common.serialization.messages.Transaction;
import com.amdocs.aia.il.common.model.configuration.entity.PublishEntity;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.stores.KeyColumn;
import com.amdocs.aia.il.common.utils.PublisherUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.CollectionUtils;

public class CurrentTxnMsgs implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentTxnMsgs.class);
    private static final long serialVersionUID=-4426099178874257415L;

    private int innerMessagesSize;
    private List<Transaction> currentTrxnMsgs;
    private Map<String, Map<KeyColumn, List<RepeatedMessage>>> innerMessages = new HashMap<>();
    private Map<String, HashMap<String, Map<KeyColumn, List<RepeatedMessage>>>> innerMessageWithTransactionID;
    private Map<String, ConfigurationRow> tableNameToTableConfigurationRowMap = new HashMap<> ();
    private Map<String, String> featureTableNameToEntityStoreKey = new HashMap<> ();

    public CurrentTxnMsgs (Map<String, PublishEntity> topicPublish) {
        this.innerMessagesSize = 0;
        this.currentTrxnMsgs = new ArrayList<> ();
        this.innerMessages = new HashMap<>();
        this.innerMessageWithTransactionID = new HashMap<>();
        deriveTableConfigurationFromPublishConfiguration(topicPublish);
    }

    public Map<String, ConfigurationRow> getTableNameToTableConfigurationRowMap() {
        return tableNameToTableConfigurationRowMap;
    }

    public List<Transaction> getCurrentTrxnMsgs() {
        return currentTrxnMsgs;
    }

    public void setCurrentTrxnMsgs(List<Transaction> currentTrxnMsgs) {
        this.currentTrxnMsgs = currentTrxnMsgs;
    }

    public Map<String, Map<KeyColumn, List<RepeatedMessage>>> getInnerMessages () {
        return innerMessages;
    }

    public void setInnerMessages (Map<String, Map<KeyColumn, List<RepeatedMessage>>> innerMessages) {
        this.innerMessages=innerMessages;
    }

    public int getInnerMessagesSize () {
        return innerMessagesSize;
    }

    public void setInnerMessagesSize (int innerMessagesSize) {
        this.innerMessagesSize=innerMessagesSize;
    }

    public Map<String, HashMap<String, Map<KeyColumn, List<RepeatedMessage>>>> getInnerMessageWithTransactionID () {
        return innerMessageWithTransactionID;
    }

    public void setInnerMessageWithTransactionID (Map<String, HashMap<String, Map<KeyColumn, List<RepeatedMessage>>>> innerMessageWithTransactionID) {
        this.innerMessageWithTransactionID=innerMessageWithTransactionID;
    }

    public void clear() {
        this.currentTrxnMsgs.clear();
        this.innerMessages.clear();
        this.innerMessageWithTransactionID.clear();
        this.innerMessagesSize = 0;
    }

    public void add(Transaction transaction) {
        String transactionId =(String) transaction.getValue("transactionId");
        this.currentTrxnMsgs.add (transaction);
        transaction.getRepeatedValues().forEach ((table, message) -> {
            String tableName = featureTableNameToEntityStoreKey.get(table);
            this.innerMessagesSize += message.size ();
            message.forEach (data -> {
                //cache the message key
                KeyColumn keyColumn = new KeyColumn(data,tableNameToTableConfigurationRowMap.get(tableName).getPkColumns());
                HashMap<String, Map<KeyColumn, List<RepeatedMessage>>> innerMessageWithTxnTiMap=innerMessageWithTransactionID.computeIfAbsent (transactionId, k -> new HashMap<> ());
                Map<KeyColumn, List<RepeatedMessage>> innerMsgWithTxnKeyColumnMap=innerMessageWithTxnTiMap.computeIfAbsent (tableName, k -> new HashMap<> ());
                List<RepeatedMessage> innerMsgWithTxnInnerMsgs=innerMsgWithTxnKeyColumnMap.computeIfAbsent (keyColumn, k -> new ArrayList<> ());
                innerMsgWithTxnInnerMsgs.add(data);
                innerMsgWithTxnKeyColumnMap.put(keyColumn, innerMsgWithTxnInnerMsgs);
                innerMessageWithTxnTiMap.put(tableName, innerMsgWithTxnKeyColumnMap);
                innerMessageWithTransactionID.put(transactionId, innerMessageWithTxnTiMap);
                Map<KeyColumn, List<RepeatedMessage>> keyColumnToGeneratedMessagesMap=innerMessages.computeIfAbsent (tableName, k -> new HashMap<> ());
                List<RepeatedMessage> messages=keyColumnToGeneratedMessagesMap.computeIfAbsent (keyColumn, k -> new ArrayList<> ());
                messages.add(data);
                keyColumnToGeneratedMessagesMap.put(keyColumn, messages);
            });
        });
    }

    public RepeatedMessage mergeMessages(List<RepeatedMessage> repeatedMessages) {
        //Take first message as is
        RepeatedMessage mergedRepeatedMessage = repeatedMessages.get(0);
        RepeatedMessage result = new RepeatedMessage();
        //Loop through remaining messages and merge it in resultant message one by one
        for (int i = 1; i < repeatedMessages.size(); i++) {
            result = PublisherUtils.mergeRepeatedMessages (mergedRepeatedMessage,repeatedMessages.get(i));
        }
        return result;
    }

    private void deriveTableConfigurationFromPublishConfiguration (Map<String, PublishEntity> topicPublish)
    {
        for (Map.Entry<String, PublishEntity> topicPublishEntry : topicPublish.entrySet()) {
            for (Map.Entry<String, PublishEntity.SchemaRow> tableConfigurationEntry : topicPublishEntry.getValue().getTableConfigurations().entrySet()) {
                for (ConfigurationRow configurationRow : tableConfigurationEntry.getValue().getRows()) {
                    tableNameToTableConfigurationRowMap.put (configurationRow.getTableName() ,configurationRow);
                    featureTableNameToEntityStoreKey.put(Transaction.getTransactionRepeatedFieldName(configurationRow.getTableName()), configurationRow.getTableName());
                }
            }
        }
    }

    /**
     * @return the number of current inner messages
     */
    public int innerMessagesSize() { return innerMessagesSize; }

    /**
     * @return true if there are messages in current transactions
     */
    public boolean isEmpty() { return this.currentTrxnMsgs.isEmpty(); }

}
