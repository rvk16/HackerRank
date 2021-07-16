package com.amdocs.aia.il.common.stores.inmemory;

import com.amdocs.aia.common.serialization.formatter.json.JsonRepeatedMessageFormatter;
import com.amdocs.aia.common.serialization.messages.Constants;
import com.amdocs.aia.common.serialization.messages.EnumValue;
import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.audit.RealtimeAuditDataPublisherManager;
import com.amdocs.aia.il.common.model.configuration.entity.DeletedRowInfo;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.publisher.CounterType;
import com.amdocs.aia.il.common.stores.*;
import com.amdocs.aia.il.common.utils.DbUtils;
import com.amdocs.aia.il.common.utils.PublisherUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class InMemoryDBBulkRandomAccessTable implements RandomAccessTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDBBulkRandomAccessTable.class);

    private static final String TRANSACTION_TIME = "transactionTime";

    private final String tableName;
    private final List<KeyColumnDescriptor> indexedFks;
    private final List<String> contextNames;
    private final List<String> mainTableNames;
    private final List<String> relationtables;
    private final List<String> relationtypes;
    private final List<KeyColumnDescriptor> relationKey;
    private final List<Boolean> isMainRelation;
    private final List<Boolean> doPropagation;
    private final List<String> counterTables;
    private final List<Boolean> isMTM;
    private final QueryGenerator queryGenerator;

    private final ConfigurationRow configurationRow;
    private static final String OPERATION = "operation";
    private static final String DELETE = "delete";

    public InMemoryDBBulkRandomAccessTable(ConfigurationRow configurationRow, JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter) {
        this.configurationRow = configurationRow;
        this.tableName = configurationRow.getTableName();
        this.queryGenerator = new InMemoryQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey().replace(PublisherUtils.PUBLISHER_STORE, ""), configurationRow.getEntityStore().isReference());
        this.indexedFks = new ArrayList<>();
        this.contextNames = new ArrayList<>();
        this.mainTableNames = new ArrayList<>();
        this.relationtables = new ArrayList<>();
        this.relationKey = new ArrayList<>();
        this.relationtypes = new ArrayList<>();
        this.isMainRelation = new ArrayList<>();
        this.doPropagation = new ArrayList<>();
        this.counterTables = new ArrayList<>();
        this.isMTM = new ArrayList<>();
    }

    @Override
    public void commit() {
        // Do Nothing
    }

    @Override
    public boolean upsert(RepeatedMessage repeatedMessage) {
        return false;
    }

    @Override
    public Collection<Integer> asyncUpsert(List<RepeatedMessage> repeatedMessages) {
        return null;
    }

    @Override
    public double[] asyncUpsert(String tableName, List<RepeatedMessage> repeatedMessages, Long batchProcessingTimestamp, List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted, List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving, boolean incrementalUpsert, List<DeletedRowInfo> deletedRowInfos, CounterType counterType, boolean isAuditEnable) {
        List<String> idList = new ArrayList<>(repeatedMessages.size());
        List<Long> updateTimeList = new ArrayList<>(repeatedMessages.size());
        List<Long> dbTimestampList = new ArrayList<>(repeatedMessages.size());
        //get all the IDs to query and update time value
        repeatedMessages.forEach(rm -> {
            KeyColumn ids = new KeyColumn(rm, this.configurationRow.getPkColumns());
            if (!rowKeysPerTransactionIDList.isEmpty() && checkIfIdInserted(ids, rowKeysPerTransactionIDList)) {
                dbTimestampList.add(0L);
            } else {
                dbTimestampList.add(batchProcessingTimestamp);
            }
            idList.add(buildDataBaseRowKey(ids.getIds()));
            updateTimeList.add(this.configurationRow.getUpdateTimeField() != null ?
                    Long.valueOf(rm.getValue(this.configurationRow.getUpdateTimeField()).toString()) :
                    Long.valueOf(rm.getValue(TRANSACTION_TIME).toString()));
        });

        /* if mergeMode set to false select query to scylla db will be skipped directly it will insert records into db */
        LOGGER.info("Processing NonMergeMode");
        double[] messageCountArr = upsertResultList(repeatedMessages, idList, dbTimestampList, counterType);
        return messageCountArr;
    }

    @Override
    public double[] asyncUpsert(String tableName,
                                List<UpsertData> upsertDataList,
                                List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted,
                                List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving, boolean incrementalUpsert, Map<Long, List<DeletedRowInfo>> deletedRowInfosPerBatch, CounterType counterType, boolean isAuditEnable) {

        double[] messageCountArr = new double[7];

        for (UpsertData upsertData : upsertDataList) {

            double[] upsertResult = asyncUpsert(tableName, upsertData.getRepeatedMessageList(), upsertData.getTimeStamp(), rowKeysPerTableInfoListToBeUpserted, rowKeysPerTransactionIDList, dayLightSaving, incrementalUpsert, null, counterType, isAuditEnable);

            for (int i = 0; i < messageCountArr.length; i++) {
                messageCountArr[i] += upsertResult[i];
            }
        }
        return messageCountArr;
    }

    @Override
    public double[] asyncUpsertForSingleMessage(String tableName, RepeatedMessage message, Long batchProcessingTimestamp, List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving, boolean incrementalUpsert, CounterType counterType) {
        String idList;
        Long updateTimeList;
        Long dbTimestampList;
        KeyColumn ids = new KeyColumn(message, this.configurationRow.getPkColumns());
        if (!rowKeysPerTransactionIDList.isEmpty() && checkIfIdInserted(ids, rowKeysPerTransactionIDList)) {
            dbTimestampList = 0L;
        } else {
            dbTimestampList = batchProcessingTimestamp;
        }
        idList = buildDataBaseRowKey(ids.getIds());
        updateTimeList = this.configurationRow.getUpdateTimeField() != null ?
                Long.valueOf(message.getValue(this.configurationRow.getUpdateTimeField()).toString()) :
                Long.valueOf(message.getValue(TRANSACTION_TIME).toString());


        /* if mergeMode set to false select query to scylla db will be skipped directly it will insert records into db */
        LOGGER.debug("Processing NonMergeMode");
        return upsertResultListForSinlgeMessage(tableName, message, idList, Collections.emptyList(), dbTimestampList, updateTimeList, dayLightSaving, counterType);
    }

    private double[] upsertResultListForSinlgeMessage(String tableName, RepeatedMessage message,
                                                      String singleRowKey,
                                                      List<List<Pair<RepeatedMessage, Double>>> currentList,
                                                      Long databaseTimestamp, Long updateTimeList,
                                                      boolean dayLightSaving, CounterType counterType) {// NOSONAR
        List<PreparedStatement> statements = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        int failedMessageCount = 0;
        int countOfFilteredMessages = 0;
        long totalUpsertTime = 0L;
        long totalMergeTime = 0L;
        long mergeMessageCount = 0L;
        long startTimeUpsert;

        double[] messageCountArr = new double[7];

        try {
            RepeatedMessage repeatedMessage = message;
            String rowKey = singleRowKey;

            Map<Integer, KeyColumnDescriptor> updatedForeignKeys = new HashMap<>();
            startTimeUpsert = System.currentTimeMillis();
            Long dataBaseTimestamp = databaseTimestamp;
            if(((EnumValue) repeatedMessage.getValue(OPERATION)).getValueName().equalsIgnoreCase(DELETE)) {
                RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                        configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), CounterType.RECORDSDELETED, repeatedMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME),
                        this.tableName, 1L);
            }else {
                failedMessageCount = insertDataIntoDataBase(repeatedMessage, rowKey, dataBaseTimestamp, failedMessageCount, updatedForeignKeys, messageCountArr);
                RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                        configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), CounterType.RECORDSSTORED, repeatedMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME),
                        this.tableName, 1L);
            }
            totalUpsertTime += System.currentTimeMillis() - startTimeUpsert;


            messageCountArr[3] = countOfFilteredMessages;
            messageCountArr[5] = 0L;
            messageCountArr[6] = (messageCountArr[0] + messageCountArr[1]) != 0 ? totalUpsertTime / (messageCountArr[0] + messageCountArr[1]) : 0;
        } catch (SQLException e) {
            LOGGER.error("Failure", e);
        } catch (Exception ex) {
            throw ex;
        }
        LOGGER.info("In Async upsert for table:{}:Input Size:{}:Index Size:{}:Output Size:{} :Missing: {} :Time: {}", this.tableName, message.getSize(), indexedFks.size(), statements.size(), message.getSize() - statements.size(), System.currentTimeMillis() - startTime);
        return messageCountArr;
    }

    @Override
    public Collection<RepeatedMessage> queryData(String indexName, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) throws Exception {
        return Collections.emptyList();
    }

    @Override
    public Collection<RepeatedMessage> queryBulkData(String indexName, Collection<KeyColumn> rowsToQuery ,Long batchProcessingTimestamp) throws Exception{
        return Collections.emptyList();
    }

    @Override
    public Collection<RepeatedMessage> queryAllData(String indexName) {
        return null;
    }

    @Override
    public Collection<KeyColumn> queryIds(String indexName, Collection<KeyColumn> rowsToQuery) throws Exception {
        return null;
    }

    @Override
    public Collection<KeyColumn> queryIds(String indexName, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) throws Exception {
        return null;
    }

    @Override
    public List<Boolean> checkExists(String indexName, Collection<KeyColumn> rowsToQuery) throws Exception {
        return null;
    }

    @Override
    public void shutdown() {
        // Do Nothing
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getName() {
        return this.tableName;
    }

    @Override
    public void buildIndexes(List<String> indexNames) {
        // Do Nothing
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void registerFkIndex(KeyColumnDescriptor fkey) {
        // Do Nothing
    }

    @Override
    public void registerFkIndex(KeyColumnDescriptor fkey, String mainTableName, String contextname, String relationtable, KeyColumnDescriptor relationKey, String relationtype, boolean isMainRelation, boolean doPropagation, String counterTableName, boolean isMTM) {
        this.indexedFks.add(fkey);
        this.relationtypes.add(relationtype);
        this.contextNames.add(contextname);
        this.relationKey.add(relationKey);
        this.mainTableNames.add(mainTableName);
        this.relationtables.add(relationtable);
        this.isMainRelation.add(isMainRelation);
        this.doPropagation.add(doPropagation);
        this.counterTables.add(counterTableName);
        this.isMTM.add(isMTM);
    }

    private boolean checkIfIdInserted(KeyColumn ids, List<RowKeysPerTransactionID> rowKeysPerTransactionIDList) {
        for (RowKeysPerTransactionID rowKeysPerTransactionID : rowKeysPerTransactionIDList) {
            if (rowKeysPerTransactionID.getTxnUpdatedRows().containsKey(configurationRow.getTableName())) {
                return rowKeysPerTransactionID.getTxnUpdatedRows().get(configurationRow.getTableName()).contains(ids);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No Table info found for table {} while processing upsert for ID {}", configurationRow.getTableName(), ids);
            }
        }
        return false;
    }

    private String buildDataBaseRowKey(Object[] ids) {
        return StringUtils.join(ids, KeyColumn.ROWKEY_SEPARATOR);
    }

    private String buildDataBaseRowKey(RepeatedMessage repeatedMessage, List<ColumnConfiguration> ids) {
        return buildDataBaseRowKey(KeyColumn.getIds(repeatedMessage, ids));
    }

    private double[] upsertResultList(List<RepeatedMessage> repeatedMessages, List<String> rowKeys, List<Long> dataBaseTimestampList, CounterType counterType) {// NOSONAR
        List<PreparedStatement> statements = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        int failedMessageCount = 0;
        int countOfFilteredMessages = 0;
        long totalUpsertTime = 0L;
        long totalMergeTime = 0L;
        long mergeMessageCount = 0L;
        long startTimeUpsert;

        double[] messageCountArr = new double[7];

        try {
            for (int i = 0; i < repeatedMessages.size(); i++) {
                RepeatedMessage repeatedMessage = repeatedMessages.get(i);
                String rowKey = rowKeys.get(i);

                Map<Integer, KeyColumnDescriptor> updatedForeignKeys = new HashMap<>();
                startTimeUpsert = System.currentTimeMillis();
                Long dataBaseTimestamp = dataBaseTimestampList.get(i);
                if(((EnumValue) repeatedMessage.getValue(OPERATION)).getValueName().equalsIgnoreCase(DELETE)) {
                    RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                            configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), CounterType.RECORDSDELETED, repeatedMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME),
                            this.tableName, 1L);
                }else {
                    failedMessageCount = insertDataIntoDataBase(repeatedMessage, rowKey, dataBaseTimestamp, failedMessageCount, updatedForeignKeys, messageCountArr);
                    RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                            configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), CounterType.RECORDSSTORED, repeatedMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME),
                            this.tableName, 1L);
                }
                totalUpsertTime += System.currentTimeMillis() - startTimeUpsert;

            }
            messageCountArr[3] = countOfFilteredMessages;
            messageCountArr[5] = 0L;
            messageCountArr[6] = (messageCountArr[0] + messageCountArr[1]) != 0 ? totalUpsertTime / (messageCountArr[0] + messageCountArr[1]) : 0;
        } catch (SQLException e) {
            LOGGER.error("Failure", e);
        } catch (Exception ex) {
            throw ex;
        }
        LOGGER.info("In Async upsert for table:{}:Input Size:{}:Index Size:{}:Output Size: {} :Missing: {}:Time: {}", this.tableName, repeatedMessages.size(), indexedFks.size(), statements.size(), repeatedMessages.size() - statements.size(), System.currentTimeMillis() - startTime);
        return messageCountArr;
    }

    public int insertDataIntoDataBase(RepeatedMessage repeatedMessage, String rowKey, Long dataBaseTimeStamp, Integer failedMessageCount, Map<Integer, KeyColumnDescriptor> updatedForeignKeys, double[] messageCountArr) throws SQLException { // NOSONAR
        try {
            byte[] bytes = DbUtils.serialize(repeatedMessage);
            Integer rows;
            try (PreparedStatement psQuery = InMemoryDBConnection.getConnection().prepareStatement(queryGenerator.getPsForInsertQueryString())) {
                setParameterValueForInsertRef(psQuery, this.tableName, rowKey, new Double(dataBaseTimeStamp), bytes, this.tableName);
                rows = psQuery.executeUpdate();
                if (rows == null) {
                    failedMessageCount++;
                }
            }
            // main data counter
            messageCountArr[0]++;
            failedMessageCount = handleInsertsForAllForeignKeys(repeatedMessage, dataBaseTimeStamp, failedMessageCount, messageCountArr);
        } catch (SQLException e) {
            LOGGER.error("Failure", e);
        }
        return failedMessageCount;
    }

    private Integer handleInsertsForAllForeignKeys(RepeatedMessage repeatedMessage, Long dataBaseTimeStamp, Integer failedMessageCount, double[] messageCountArr) throws SQLException {
        Integer rows;
        for (int j = 0; j < indexedFks.size(); j++) {
            KeyColumnDescriptor foreignKeys = indexedFks.get(j);
            KeyColumnDescriptor relationKeys = relationKey.get(j);
            String relTable = relationtables.get(j);
            String ctx = contextNames.get(j);
            String mainTableName = mainTableNames.get(j);
            String relType = relationtypes.get(j);
            boolean mainRelation = isMainRelation.get(j);

            String rowKeyFromMessage = buildDataBaseRowKey(repeatedMessage, foreignKeys.getKeys());
            String relationKeyFromMessage = buildDataBaseRowKey(repeatedMessage, relationKeys.getKeys());

            if (!hasAllForeignKeysAndRelationKeys(mainRelation, repeatedMessage, foreignKeys, relationKeys)) {
                continue;
            }

            LOGGER.info("Inside loop 2 :" + relationKeyFromMessage);
            try (PreparedStatement psQuery = InMemoryDBConnection.getConnection().prepareStatement(queryGenerator.getPsForInsertRelationQueryString())) {
                setParameterValueForInsert(psQuery, mainTableName, rowKeyFromMessage, ctx, relType, relTable, relationKeyFromMessage, new Double(dataBaseTimeStamp), mainTableName);
                rows = psQuery.executeUpdate();
                if (rows == null) {
                    failedMessageCount++;
                }
                messageCountArr[1]++; //Relational data counter
            }
        }
        return failedMessageCount;
    }

    private Integer handleInsertsForUpdatedKeys(Map<Integer, KeyColumnDescriptor> updatedForeignKeys, RepeatedMessage repeatedMessage, Long dataBaseTimeStamp, Integer failedMessageCount, double[] messageCountArr) throws SQLException {
        Integer rows;
        //Iterating over map of updated Foreign Keys
        for (Map.Entry<Integer, KeyColumnDescriptor> entry : updatedForeignKeys.entrySet()) {
            int j = entry.getKey();
            KeyColumnDescriptor foreignKeys = indexedFks.get(j);
            KeyColumnDescriptor relationKeys = relationKey.get(j);
            String relTable = relationtables.get(entry.getKey());
            String ctx = contextNames.get(j);
            String mainTableName = mainTableNames.get(j);
            String relType = relationtypes.get(j);
            boolean mainRelation = isMainRelation.get(j);

            String rowKeyFromMessage = buildDataBaseRowKey(repeatedMessage, foreignKeys.getKeys());
            String relationKeyFromMessage = buildDataBaseRowKey(repeatedMessage, relationKeys.getKeys());

            if (!hasAllForeignKeysAndRelationKeys(mainRelation, repeatedMessage, foreignKeys, relationKeys)) {
                continue;
            }

            LOGGER.info("Inside loop 1 :{}", relationKeyFromMessage);
            try (PreparedStatement psQuery = InMemoryDBConnection.getConnection().prepareStatement(queryGenerator.getPsForInsertRelationQueryString())) {
                setParameterValueForInsert(psQuery, mainTableName, rowKeyFromMessage, ctx, relType, relTable, relationKeyFromMessage, new Double(dataBaseTimeStamp), mainTableName);
                rows = psQuery.executeUpdate();
                if (rows == null) {// NOSONAR
                    failedMessageCount++;
                }
                messageCountArr[1]++; //Relational data counter
            }
        }
        return failedMessageCount;
    }

    @Override
    public Map<KeyColumn, List<KeyColumn>> queryListChildData(String mainTable, Collection<KeyColumn> rowKeys, String contextName, String relType, String relationTable, Long batchProcessingTimestamp) {
        return Collections.emptyMap();
    }


    @Override
    public List<KeyColumnDescriptor> getIndexedFks() {
        return new ArrayList<>(indexedFks);
    }

    @Override
    public List<KeyColumn> queryListChildDataWithTTLAndWriteTimeChecked(String mainTable, Collection<KeyColumn> rowKeys, String contextName,
                                                                        String relType, String relationTable, Long batchProcessingTimestamp) {
        return new ArrayList<>();
    }

    @Override
    public Collection<RepeatedMessage> queryDataWithTTLAndWriteTimeChecked(String mainTable, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) {
        return Collections.emptyList();
    }

    @Override
    public List<RelationKeyInfo> queryListChildDataWithBulkProcessedFlag(String mainTable, Collection<KeyColumn> rowKeys, String contextName, String relType, String relationTable, Long batchProcessingTimestamp) {
        return new ArrayList<>();
    }

    @Override
    public void updateBulkProcessedFlag(long batchTimestamp, List<BulkRelationInfo> bulkRelationInfo, double[] messageCountArr) {

    }

    public double[] deleteRowsUsingTTL(Map<Long, List<DeletedRowInfo>> deletedRowInfosPerBatch) {
        return new double[3];
    }

    @Override
    public void begin() {
    }

    private static boolean hasAllFields(RepeatedMessage repeatedMessage, List<ColumnConfiguration> keys) {
        for (ColumnConfiguration key : keys) {
            if (repeatedMessage.getValue(key.getColumnName()) == null) {
                return false;
            }
        }
        return true;
    }

    public static void setParameterValueForInsert(PreparedStatement ps, String tableName, String roeKey, String contextName, String relType, String relationTable, String relationKey, Double timestamp, String tableNameCopy) {
        try {
            ps.setString(1, tableName);
            ps.setString(2, roeKey);
            ps.setString(3, contextName);
            ps.setString(4, relType);
            ps.setString(5, relationTable);
            ps.setString(6, relationKey);
            ps.setDouble(7, timestamp);
            ps.setString(8, tableNameCopy);
        } catch (SQLException e) {
            LOGGER.error("Failure", e);
        }
    }

    private void setParameterValueForInsertRef(PreparedStatement ps, String tableName, String rowKey, Double timestamp, byte[] mainTableData, String tableNameCopy) {
        try {
            ps.setString(1, tableName);
            ps.setString(2, rowKey);
            ps.setDouble(3, timestamp);
            ps.setBytes(4, mainTableData);
            ps.setString(5, tableNameCopy);
        } catch (SQLException e) {
            LOGGER.error("Failure", e);
        }
    }

    private static boolean hasAllForeignKeysAndRelationKeys(boolean isMainRelation, RepeatedMessage currentMessage, KeyColumnDescriptor foreignKeys, KeyColumnDescriptor relationKeys) {
        if (isMainRelation && !hasAllFields(currentMessage, foreignKeys.getKeys())) {
            return false;
        }
        return isMainRelation || hasAllFields(currentMessage, relationKeys.getKeys());
    }
}