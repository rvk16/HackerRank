package com.amdocs.aia.il.common.stores.inmemorycache;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.model.configuration.entity.DataProcessingContextRelation;
import com.amdocs.aia.il.common.model.configuration.entity.DeletedRowInfo;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.publisher.CounterType;
import com.amdocs.aia.il.common.stores.*;
import com.amdocs.aia.il.common.utils.PublisherUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class InMemoryCacheRandomAccessTable implements RandomAccessTable, Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCacheRandomAccessTable.class);
    private static final long serialVersionUID = -5208034079165553133L;
    private final String tableName;
    private final List<KeyColumnDescriptor> indexedFks;

    private final ConfigurationRow configurationRow;

    //String - index name
    //First KeyColumn - Row ID
    //Second KeyColumn - Qualifier ID (as in HBase)
    private final Map<String, Map<KeyColumn, Map<KeyColumn, RepeatedMessage>>> data;

    public InMemoryCacheRandomAccessTable(ConfigurationRow configurationRow) {
        this.configurationRow = configurationRow;
        this.tableName = configurationRow.getTableName();
        this.data = new HashMap<>();
        this.indexedFks = new ArrayList<>();
        this.data.put(tableName, new HashMap<>());
    }

    public void registerFkIndex(KeyColumnDescriptor field) {
        this.data.put(field.getName(), new HashMap<>());
        this.indexedFks.add(field);
    }

    @Override
    public void registerFkIndex(KeyColumnDescriptor field, String mainTableName, String contextname, String relationtable, KeyColumnDescriptor relationKey, String relationtype , boolean isMainRelation , boolean doPropagation , String counterTableName , boolean isMTM) {
        registerFkIndex(field);
    }

    @Override
    public Map<KeyColumn, List<KeyColumn>> queryListChildData(String mainTable, Collection<KeyColumn> rowKeys, String contextName, String relType, String relationTable, Long batchProcessingTimestamp) {
        HashMap<KeyColumn, List<KeyColumn>> childParentKeys = new HashMap<>();
        rowKeys.forEach(keyColumn -> childParentKeys.put(keyColumn, new ArrayList<>(Collections.singletonList(keyColumn))));
        return childParentKeys;
    }

    @Override
    public List<KeyColumn> queryListChildDataWithTTLAndWriteTimeChecked(String mainTable, Collection<KeyColumn> rowKeys, String contextName,
                                                                        String relType, String relationTable, Long batchProcessingTimestamp) {
        return new ArrayList<>(rowKeys);
    }

    @Override
    public Collection<RepeatedMessage> queryDataWithTTLAndWriteTimeChecked(String mainTable, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) {
        return queryData(mainTable,rowsToQuery);

    }

    @Override
    public List<RelationKeyInfo> queryListChildDataWithBulkProcessedFlag(String mainTable, Collection<KeyColumn> rowKeys, String contextName, String relType, String relationTable, Long batchProcessingTimestamp) {
        return new ArrayList<>();
    }

    @Override
    public double[] asyncUpsert(String tableName, List<RepeatedMessage> repeatedMessages, Long batchProcessingTimestamp, List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted, List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving , boolean incrementalUpsert, List<DeletedRowInfo> deletedRowInfos, CounterType counterType, boolean isAuditEnable) {
        asyncUpsert(repeatedMessages);
        return new double[]{repeatedMessages.size()};
    }

    @Override
    public double[] asyncUpsert(String tableName , List<UpsertData> upsertDataList,
                                List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted,
                                List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving , boolean incrementalUpsert, Map<Long, List<DeletedRowInfo>> deletedRowInfosPerBatch, CounterType counterType, boolean isAuditEnable) {

        upsertDataList.forEach(upsertData ->  asyncUpsert(upsertData.getRepeatedMessageList()));
        return new double[]{};
    }

    @Override
    public double[] asyncUpsertForSingleMessage(String tableName, RepeatedMessage repeatedMessages, Long batchProcessingTimestamp, List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving, boolean incrementalUpsert, CounterType counterType) {
        return new double[0];
    }

    @Override
    public Collection<Integer> asyncUpsert(List<RepeatedMessage> repeatedMessages) {
        repeatedMessages.forEach(this::upsert);
        //empty collection which indicates that all messages were upserted successfully
        return Collections.emptyList();
    }

    public boolean upsert(RepeatedMessage rm) { // NOSONAR
        KeyColumn keyColumn = new KeyColumn(rm, configurationRow.getIdFields().getKeys()).createNewStrIds();
        Map<KeyColumn, RepeatedMessage> row = this.data.get(tableName).get(keyColumn);
        if (row != null) {
            RepeatedMessage existingMessage = row.values().iterator().next();
            long existingUpdateTime = getUpdateTimeFromCurrentObject(existingMessage);
            long updateTime = getUpdateTimeFromCurrentObject(rm);
            if (updateTime >= existingUpdateTime) {
                rm = PublisherUtils.mergeRepeatedMessages(existingMessage, rm);

                for (KeyColumnDescriptor fk : indexedFks) {
                    KeyColumn newValues = new KeyColumn(rm, fk.getKeys());
                    KeyColumn oldValues = new KeyColumn(existingMessage, fk.getKeys());

                    if (!newValues.compareTo(oldValues)) {
                        Map<KeyColumn, RepeatedMessage> oldFkRow = this.data.get(fk.getName()).get(oldValues);
                        if (oldFkRow != null) { // NOSONAR
                            oldFkRow.remove(keyColumn);
                        }
                    }
                }
            }
        } else {
            row = new HashMap<>();
            this.data.get(tableName).put(keyColumn, row);
        }

        row.put(keyColumn, rm);
        for (KeyColumnDescriptor fk : indexedFks) {
            if (!PublisherUtils.hasAllFields(rm, fk.getKeys())) {
                continue;
            }
            KeyColumn fkValue = new KeyColumn(rm, fk.getKeys()).createNewStrIds();
            Map<KeyColumn, RepeatedMessage> fkRow = this.data.get(fk.getName()).computeIfAbsent(fkValue, k -> new HashMap<>());
            fkRow.put(keyColumn, rm);
        }
        return true;
    }

    public Collection<RepeatedMessage> queryData(String mainTable, Collection<KeyColumn> rowsToQuery)  {
        List<RepeatedMessage> results = new ArrayList<>();
        Map<KeyColumn, Map<KeyColumn, RepeatedMessage>> keyColumnMapMap = data.get(mainTable);
        rowsToQuery.forEach(key -> {
            Map<KeyColumn, RepeatedMessage> keyColumnRepeatedMessageMap = keyColumnMapMap.get(key.createNewStrIds());
            if (keyColumnRepeatedMessageMap != null) {
                results.addAll(keyColumnRepeatedMessageMap.values());
            }
        });
        LOGGER.debug("Size of the results returned : "+results.size());
        return results;
    }

    @Override
    public Collection<RepeatedMessage> queryAllData(String indexName) {
        List<RepeatedMessage> results = new ArrayList<RepeatedMessage>();
        data.get(indexName).values().forEach(row -> results.addAll(row.values()));
        return results;
    }

    @Override
    public Collection<KeyColumn> queryIds(String indexName, Collection<KeyColumn> rowsToQuery) throws Exception {
        Collection<KeyColumn> results = new ArrayList<>();
        Map<KeyColumn, Map<KeyColumn, RepeatedMessage>> keyColumnMapMap = data.get(indexName);
        rowsToQuery.forEach(key -> {
            Map<KeyColumn, RepeatedMessage> keyColumnRepeatedMessageMap = keyColumnMapMap.get(key.createNewStrIds());
            if (keyColumnRepeatedMessageMap != null) {
                keyColumnRepeatedMessageMap.values().forEach(rm -> {
                    results.add(new KeyColumn(rm, configurationRow.getIdFields().getKeys()));
                });
            }
        });
        return results;
    }

    @Override
    public List<Boolean> checkExists(String indexName, Collection<KeyColumn> rowsToQuery) throws Exception {
        List<Boolean> results = new ArrayList<>();
        Map<KeyColumn, Map<KeyColumn, RepeatedMessage>> keyColumnMapMap = data.get(indexName);
        rowsToQuery.forEach(key -> {
            Map<KeyColumn, RepeatedMessage> keyColumnRepeatedMessageMap = keyColumnMapMap.get(key.createNewStrIds());
            results.add(keyColumnRepeatedMessageMap != null && !keyColumnRepeatedMessageMap.isEmpty());
        });
        return results;
    }

    @Override
    public void commit() {
        //do nothing here
    }

    public void shutdown() {
        //do nothing here
    }

    @Override
    public String getName() {
        return this.tableName;
    }

    @Override
    public void buildIndexes(List<String> indexNames) {
        //do nothing here
    }

    @Override
    public String getType() {
        return null;
    }

    public boolean isTransient() {
        return true;
    }

    @Override
    //This method will never be used in context of InMemoryRandomAccessTable.
    //Oveeriding the method from implemented interface.
    public Collection<RepeatedMessage> queryData(String indexName, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) throws Exception {
        return queryData(indexName, rowsToQuery);
    }

    @Override
    public Collection<RepeatedMessage> queryBulkData(String indexName, Collection<KeyColumn> rowsToQuery ,Long batchProcessingTimestamp) throws Exception{
        return queryData(indexName, rowsToQuery);
    }

    @Override
    //This method will never be used in context of InMemoryRandomAccessTable.
    //Oveeriding the method from implemented interface.
    public Collection<KeyColumn> queryIds(String indexName, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) throws Exception {
        return queryIds(indexName, rowsToQuery);
    }

    @Override
    public List<KeyColumnDescriptor> getIndexedFks() {
        return indexedFks;
    }

    @Override
    public void updateBulkProcessedFlag(long batchTimestamp, List<BulkRelationInfo> bulkRelationInfo, double[] messageCountArr){

    }

    private long getUpdateTimeFromCurrentObject(RepeatedMessage currentObject) {
        return currentObject.getValue(configurationRow.getUpdateTimeField()) != null ?
                Long.valueOf(currentObject.getValue(configurationRow.getUpdateTimeField()).toString()) : Long.valueOf(currentObject.getValue("transactionTime").toString());
    }

    @Override
    public double[] deleteRowsUsingTTL(Map<Long, List<DeletedRowInfo>> deletedRowInfosPerBatch){
        return new double[3];
    }

    @Override
    public void begin() {
        // clear all data associated with the current state - InMemoryRandomAccessTable is considered to be transient
        // and is not allowed to retain state across transactions
        data.values().forEach(m -> {
            m.clear();
        });
    }

}
