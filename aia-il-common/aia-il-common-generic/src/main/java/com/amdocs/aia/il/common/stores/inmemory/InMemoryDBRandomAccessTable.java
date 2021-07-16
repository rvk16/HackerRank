package com.amdocs.aia.il.common.stores.inmemory;

import com.amdocs.aia.common.serialization.formatter.json.JsonRepeatedMessageFormatter;
import com.amdocs.aia.common.serialization.messages.Constants;
import com.amdocs.aia.common.serialization.messages.EnumValue;
import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.audit.RealtimeAuditDataPublisherManager;
import com.amdocs.aia.il.common.constant.RTPConstants;
import com.amdocs.aia.il.common.model.configuration.entity.DeletedRowInfo;
import com.amdocs.aia.il.common.model.configuration.properties.BulkReplicatorConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.publisher.CounterType;
import com.amdocs.aia.il.common.stores.*;
import com.amdocs.aia.il.common.utils.PublisherUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class InMemoryDBRandomAccessTable implements RandomAccessTable, Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDBRandomAccessTable.class);

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
    public static BulkReplicatorConfiguration bulkReplicatorConfiguration = new BulkReplicatorConfiguration();

    private final ConfigurationRow configurationRow;
    private final JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter;
    static HashMap<KeyColumn, RepeatedMessage> latestMergedMessages = new HashMap<>();

    private static final String PUBLISHER_STORE = "PublisherStore";
    private static final String OPERATION = "operation";
    private static final String DELETE = "delete";
    private static final String REPLICATOR_LOAD_COUNTER = "REPLICATORLOAD";

    public InMemoryDBRandomAccessTable(ConfigurationRow configurationRow, JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter) {
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
        this.jsonRepeatedMessageFormatter = jsonRepeatedMessageFormatter;
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
            updateTimeList.add(getUpdateTimeFromCurrentObject(rm));
        });

        List<List<RepeatedMessage>> currentList = queryList(idList, batchProcessingTimestamp);
        long starttime = System.currentTimeMillis();
        long queryTime = System.currentTimeMillis() - starttime;
        double[] messageCountArr = upsertResultList(repeatedMessages, idList, updateTimeList, currentList, dbTimestampList, rowKeysPerTableInfoListToBeUpserted, incrementalUpsert, dayLightSaving, counterType, isAuditEnable);
        messageCountArr[4] = currentList.isEmpty() ? 0L : queryTime / currentList.size();
        //Index wise description - 0)main data count , 1) relational data count , 2) failed message count , 3) filtered message count, 4)Avg query time, 5) Avg merge time 6) Avg Upsert time
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
    public double[] asyncUpsertForSingleMessage(String tableName, RepeatedMessage repeatedMessages, Long batchProcessingTimestamp, List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving, boolean incrementalUpsert, CounterType counterType) {
        return new double[0];
    }

    @Override
    public Collection<RepeatedMessage> queryData(String indexName, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) throws Exception {
        final long currTime = System.currentTimeMillis();
        final List<RepeatedMessage> matchesList = new ArrayList<>(0);
        final List<PreparedStatement> statements = new ArrayList<>();
        int counter = 0;
        try {
            rowsToQuery.forEach(rowKey -> {
                try {
                    PreparedStatement psQuery = InMemoryDBConnection.getConnection().prepareStatement(queryGenerator.getPsForUpsertSelectQueryString().replace("BYPASS CACHE", ""));
                    setParameterValueForUpsert(psQuery, configurationRow.getTableName(), rowKey.toString());
                    statements.add(psQuery);
                } catch (final SQLException e) {
                    LOGGER.error(RTPConstants.ERROR, e);
                }
            });

            for (PreparedStatement statement : statements) {
                try (ResultSet row = statement.executeQuery()) {
                    if (row != null && row.next()) {
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("Data retrieved from table {}", configurationRow.getTableName());
                        }
                        String json = row.getString("maintabledata");
                        matchesList.add(deserialize(json));
                        counter++;
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("In queryList: Input Size = {}, Output = {}, Missing = {}, Time = {}:{}",
                    rowsToQuery.size(), counter, (rowsToQuery.size() - counter), (System.currentTimeMillis() - currTime), configurationRow.getTableName());
        }
        return matchesList;
    }

    @Override
    public Collection<RepeatedMessage> queryBulkData(String indexName, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) throws Exception {
        final long currTime = System.currentTimeMillis();
        final List<RepeatedMessage> matchesList = new ArrayList<>(0);
        final List<PreparedStatement> statements = new ArrayList<>();
        int counter = 0;
        try {
            rowsToQuery.forEach(rowKey -> {
                try {
                    PreparedStatement psQuery = InMemoryDBConnection.getConnection().prepareStatement(queryGenerator.getPsForUpsertSelectBULKQueryString().replace("BYPASS CACHE", ""));
                    setParameterValueForUpsert(psQuery, configurationRow.getTableName(), rowKey.toString());
                    statements.add(psQuery);
                } catch (final SQLException e) {
                    LOGGER.error(RTPConstants.ERROR, e);
                }
            });

            for (PreparedStatement statement : statements) {
                try (ResultSet row = statement.executeQuery()) {
                    if (row != null && row.next()) {
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("Data retrieved from table {}", configurationRow.getTableName());
                        }
                        String json = row.getString("maintabledata");
                        matchesList.add(deserialize(json));
                        counter++;
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("In queryList: Input Size = {}, Output = {}, Missing = {}, Time = {}:{}",
                    rowsToQuery.size(), counter, (rowsToQuery.size() - counter), (System.currentTimeMillis() - currTime), configurationRow.getTableName());
        }
        return matchesList;
    }

    @Override
    public Collection<RepeatedMessage> queryAllData(String indexName) {
        return Collections.emptyList();
    }

    @Override
    public Collection<KeyColumn> queryIds(String indexName, Collection<KeyColumn> rowsToQuery) {
        return null;
    }

    @Override
    public Collection<KeyColumn> queryIds(String indexName, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) {
        return null;
    }

    @Override
    public List<Boolean> checkExists(String indexName, Collection<KeyColumn> rowsToQuery) {
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
    public void registerFkIndex(KeyColumnDescriptor fkey, String mainTableName, String contextname, String relationtable,
                                KeyColumnDescriptor relationKey, String relationtype, boolean isMainRelation,
                                boolean doPropagation, String counterTableName, boolean isMTM) {
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

    private static String buildDataBaseRowKey(Object[] ids) {
        return StringUtils.join(ids, KeyColumn.ROWKEY_SEPARATOR);
    }

    private static String buildDataBaseRowKey(RepeatedMessage repeatedMessage, List<ColumnConfiguration> ids) {
        return buildDataBaseRowKey(KeyColumn.getIds(repeatedMessage, ids));
    }

    private List<List<RepeatedMessage>> queryList(List<String> keyList, Long batchProcessingTimestamp) {
        final long currTime = System.currentTimeMillis();
        final List<List<RepeatedMessage>> matchesList = new ArrayList<>();
        final List<PreparedStatement> statements = new ArrayList<>();

        int counter = 0;
        try {
            keyList.forEach(rowKey -> {
                try {
                    PreparedStatement psQuery = InMemoryDBConnection.getConnection().prepareStatement(queryGenerator.getPsForUpsertSelectQueryString().replace("BYPASS CACHE", ""));
                    if (isReferenceData()) {
                        setParameterValueForUpsertForRef(psQuery, configurationRow.getTableName(), rowKey);
                    } else {
                        setParameterValueForUpsert(psQuery, configurationRow.getTableName(), rowKey);
                    }
                    statements.add(psQuery);
                } catch (SQLException e) {
                    LOGGER.error("SQL error", e);
                }
            });

            for (PreparedStatement statement : statements) {
                try (ResultSet row = statement.executeQuery()) {
                    row.next();
                    List<RepeatedMessage> matches = new ArrayList<>();
                    if (row.getFetchSize() > 0) {
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("Data retrieved from table {}", configurationRow.getTableName());
                        }
                        String json = row.getString("maintabledata");
                        matches.add(deserialize(json));
                    }
                    matchesList.add(matches);
                    counter++;
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("In queryList: Input Size = {}, Output = {}, Missing = {}, Time = {}:{}",
                    keyList.size(), counter, (keyList.size() - counter), (System.currentTimeMillis() - currTime), configurationRow.getTableName());
        }
        return matchesList;
    }

    private double[] upsertResultList(List<RepeatedMessage> repeatedMessages, List<String> rowKeys,
                                      List<Long> updateTimes, List<List<RepeatedMessage>> currentList,
                                      List<Long> dataBaseTimestampList,
                                      List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted,
                                      boolean incrementalUpdate, boolean dayLightSaving, CounterType counterType, boolean isAuditEnable) {// NOSONAR
        List<PreparedStatement> statements = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        int failedMessageCount = 0;
        int countOfFilteredMessages = 0;
        int mainDataCount = 0;
        int relationalDataCount = 0;
        int resultSetFuturesSize = 0;
        long totalUpsertTime = 0L;
        long totalMergeTime = 0L;
        long mergeMessageCount = 0L;
        long startTimeMerge;
        long startTimeUpsert;
        double[] messageCountArr = new double[7];
        String datachannel = configurationRow.getEntityStore().getSchemaStoreKey().replaceFirst("PublisherStore", "");

        try {
            for (int i = 0; i < repeatedMessages.size(); i++) {
                boolean ignoreThisMessage = false;
                RepeatedMessage existingMessage = null;
                RepeatedMessage repeatedMessage = repeatedMessages.get(i);
                String rowKey = rowKeys.get(i);
                long updateTime = updateTimes.get(i);
                Collection<RepeatedMessage> current = currentList.get(i);
                Map<Integer, KeyColumnDescriptor> updatedForeignKeys = new HashMap<>();

                //in case of main table, current will be with size of 1
                if (current.size() > 0) {
                    existingMessage = current.iterator().next();

                    //In case update time field is not there , then fetch transaction time from message itself.
                    long existingUpdateTime = getUpdateTimeFromCurrentObject(existingMessage);
                    if (isUpdateTimeGreaterThanExistingTime(updateTime, existingUpdateTime, dayLightSaving))  //Compare update Time from Proto message, and insert the record only if IncomingUpdateTime > ExistingUpdateTime
                    {
                        if (incrementalUpdate) {// NOSONAR
                            startTimeMerge = System.currentTimeMillis();
                            repeatedMessage = PublisherUtils.CompareAndMergeRepeatedMessages(existingMessage, repeatedMessage);
                            mergeMessageCount++;
                            totalMergeTime += System.currentTimeMillis() - startTimeMerge;
                            if (repeatedMessage == null && !rowKeysPerTableInfoListToBeUpserted.isEmpty()) {
                                deleteOldRowKeysPerTransactionId(existingMessage, rowKeysPerTableInfoListToBeUpserted, rowKey);
                                ignoreThisMessage = true;
                                countOfFilteredMessages++;

                            }
                            if (repeatedMessage == null) {
                                if (isAuditEnable) {
                                    RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                                            configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, ""), CounterType.RECORDSNOCHANGE, existingMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME),
                                            this.tableName, 1L);
                                }
                            }
                        } else {
                            startTimeMerge = System.currentTimeMillis();
                            repeatedMessage = PublisherUtils.mergeRepeatedMessages(existingMessage, repeatedMessage);
                            mergeMessageCount++;
                            totalMergeTime += System.currentTimeMillis() - startTimeMerge;
                        }
                        if (repeatedMessage != null) {// NOSONAR
                            updatedForeignKeys = deleteOldForeignKeysAndReturnUpdatedKeys(existingMessage, repeatedMessage);
                        }
                    } else if (isUpdateTimeLessThanExistingTime(updateTime, existingUpdateTime, dayLightSaving)) {
                        // If IncomingUpdateTime < ExistingUpdateTime log this scenario
                        ignoreThisMessage = true;
                        deleteOldRowKeysPerTransactionId(repeatedMessage, rowKeysPerTableInfoListToBeUpserted, rowKey);
                    }
                }

                if (!ignoreThisMessage) {
                    startTimeUpsert = System.currentTimeMillis();
                    Long dataBaseTimestamp = dataBaseTimestampList.get(i);
                    if (repeatedMessage != null) {
                            if (((EnumValue) repeatedMessage.getValue(OPERATION)).getValueName().equalsIgnoreCase(DELETE) && !REPLICATOR_LOAD_COUNTER.equals(counterType.name())) {
                                if (isAuditEnable) {
                                RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                                        configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), CounterType.RECORDSDELETED, repeatedMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME),
                                        this.tableName, 1L);
                            }
                        }

                        if (!((EnumValue) repeatedMessage.getValue(OPERATION)).getValueName().equalsIgnoreCase(DELETE)) {
                            failedMessageCount = insertDataIntoDataBase(repeatedMessage, existingMessage, rowKey, dataBaseTimestamp, failedMessageCount, updatedForeignKeys, messageCountArr);
                            if (isAuditEnable) {
                                RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                                        configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), CounterType.RECORDSSTORED, repeatedMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME),
                                        this.tableName, 1L);
                            }
                        }
                    }
                    totalUpsertTime += System.currentTimeMillis() - startTimeUpsert;

                }
            }
            messageCountArr[3] = countOfFilteredMessages;
            messageCountArr[5] = mergeMessageCount == 0 ? 0L : (totalMergeTime / mergeMessageCount);
            messageCountArr[6] = (messageCountArr[0] + messageCountArr[1]) != 0 ? totalUpsertTime / (messageCountArr[0] + messageCountArr[1]) : 0;
        } catch (SQLException e) {
            LOGGER.error("SQL error", e);
        } catch (Exception ex) {
            throw ex;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("In Async upsert for table:{}:Input Size:{}:Index Size:{}:Output Size: {} :Missing: {}:Time: {}",
                    this.tableName, repeatedMessages.size(), indexedFks.size(), statements.size(), (repeatedMessages.size() - statements.size()), (System.currentTimeMillis() - startTime));
        }
        return messageCountArr;
    }

    private static boolean isUpdateTimeGreaterThanExistingTime(long updateTime, long existingUpdateTime, boolean dayLightSaving) {
        return updateTime >= existingUpdateTime || dayLightSaving;
    }

    private static boolean isUpdateTimeLessThanExistingTime(long updateTime, long existingUpdateTime, boolean dayLightSaving) {
        return updateTime < existingUpdateTime && !dayLightSaving;
    }

    // in case of incremental update in HBase, if the data change is there we will always have some value in generated message....
    // or in case of non incremental update in DB, the data already exists we check for changes in
    // indexedFks, if there was a change
    // we need to delete the relevant cell from the secondary index table
    public Map<Integer, KeyColumnDescriptor> deleteOldForeignKeysAndReturnUpdatedKeys(RepeatedMessage currentObject, RepeatedMessage repeatedMessage) {
        Map<Integer, KeyColumnDescriptor> changedKeys = new HashMap<>();
        for (int j = 0; j < indexedFks.size(); j++) {
            KeyColumnDescriptor foreignKeys = indexedFks.get(j);
            KeyColumn newValues = new KeyColumn(repeatedMessage, foreignKeys.getKeys());
            KeyColumn oldValues = new KeyColumn(currentObject, foreignKeys.getKeys());
            if (!newValues.compareTo(oldValues)) {
                //TODO - Delete functionality
                changedKeys.put(j, foreignKeys);
            }
        }
        return changedKeys;
    }

    public void deleteOldRowKeysPerTransactionId(RepeatedMessage repeatedMessage, List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted, String rowKey) {
        if (!rowKeysPerTableInfoListToBeUpserted.isEmpty()) {
            KeyColumn ids = new KeyColumn(repeatedMessage, this.configurationRow.getPkColumns());
            rowKeysPerTableInfoListToBeUpserted.forEach(rowKeysPerTableInfo -> rowKeysPerTableInfo.getTxnUpdatedRows().forEach((ti, kc) -> kc.remove(ids)));
        } else if (LOGGER.isInfoEnabled()) {
            LOGGER.info("{} record has a recent version in store compared to incoming version. Ignoring the incoming version.", rowKey);
        }
    }

    public int insertDataIntoDataBase(RepeatedMessage repeatedMessage, RepeatedMessage existingMessage, String rowKey,
                                      Long dataBaseTimeStamp, int failedMessageCount,
                                      Map<Integer, KeyColumnDescriptor> updatedForeignKeys, double[] messageCountArr) throws SQLException {
        try {
            final String json = serialize(repeatedMessage);
            try (final PreparedStatement psQuery = InMemoryDBConnection.getConnection().prepareStatement(queryGenerator.getPsForInsertQueryString())) {
                setParameterValueForInsertRef(psQuery, this.tableName, rowKey, dataBaseTimeStamp.doubleValue(), json, this.tableName);
                final int rows = psQuery.executeUpdate();
                if (rows == 0) {
                    failedMessageCount++;
                }
            }
            // main data counter
            messageCountArr[0]++;
            if (existingMessage == null) {
                failedMessageCount = handleInsertsForAllForeignKeys(repeatedMessage, dataBaseTimeStamp, failedMessageCount, messageCountArr);
            } else {
                failedMessageCount = handleInsertsForUpdatedKeys(updatedForeignKeys, repeatedMessage, dataBaseTimeStamp, failedMessageCount, messageCountArr);
            }
        } catch (final SQLException e) {
            LOGGER.error(RTPConstants.ERROR, e);
        }

        return failedMessageCount;
    }

    private int handleInsertsForAllForeignKeys(RepeatedMessage repeatedMessage, Long dataBaseTimeStamp,
                                               int failedMessageCount, double[] messageCountArr) throws SQLException {
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

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Inside loop 2: {}", relationKeyFromMessage);
            }
            try (final PreparedStatement psQuery = InMemoryDBConnection.getConnection().prepareStatement(queryGenerator.getPsForInsertRelationQueryString())) {
                setParameterValueForInsert(psQuery, mainTableName, rowKeyFromMessage, ctx, relType, relTable, relationKeyFromMessage, dataBaseTimeStamp.doubleValue(), mainTableName);
                final int rows = psQuery.executeUpdate();
                if (rows == 0) {
                    failedMessageCount++;
                }
                messageCountArr[1]++; //Relational data counter
            }
        }
        return failedMessageCount;
    }

    private int handleInsertsForUpdatedKeys(Map<Integer, KeyColumnDescriptor> updatedForeignKeys,
                                            RepeatedMessage repeatedMessage, Long dataBaseTimeStamp,
                                            int failedMessageCount, double[] messageCountArr) throws SQLException {
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

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Inside loop 1: {}", relationKeyFromMessage);
            }
            try (final PreparedStatement psQuery = InMemoryDBConnection.getConnection().prepareStatement(queryGenerator.getPsForInsertRelationQueryString())) {
                setParameterValueForInsert(psQuery, mainTableName, rowKeyFromMessage, ctx, relType, relTable, relationKeyFromMessage, dataBaseTimeStamp.doubleValue(), mainTableName);
                final int rows = psQuery.executeUpdate();
                if (rows == 0) {
                    failedMessageCount++;
                }
                // Relational data counter
                messageCountArr[1]++;
            }
        }
        return failedMessageCount;
    }

    @Override
    public Map<KeyColumn, List<KeyColumn>> queryListChildData(String mainTable, Collection<KeyColumn> rowKeys, String contextName,
                                              String relType, String relationTable, Long batchProcessingTimestamp) {
        final long startTime = System.currentTimeMillis();
        Map<KeyColumn, List<KeyColumn>> matches = new HashMap<>();
        Map<KeyColumn, PreparedStatement> statements = new HashMap<>();
        StringBuilder allRelationKeys = new StringBuilder();

        try {
            rowKeys.forEach(rowKey -> {
                if(KeyColumn.isEmpty(rowKey)) {
                    String strRowKey = buildDataBaseRowKey(rowKey.getIds());
                    try {
                        PreparedStatement ps1 = InMemoryDBConnection.getConnection().prepareStatement(queryGenerator.getPsForSelectQueryString());
                        setParameterValueForSelect(ps1, mainTable, strRowKey, contextName, relType, relationTable);
                        statements.put(rowKey, ps1);
                    } catch (final SQLException e) {
                        LOGGER.error(RTPConstants.ERROR, e);
                    }
                }
            });

            if (!statements.isEmpty()) {
                String relationKeys = extractKeyColumnsFromResultSet(statements, matches);
                allRelationKeys.append(relationKeys).append(':');
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Exiting queryListChildData for {}:{}:{}:{}:{}:{}:{}:{} and relation keys are :{}",
                    contextName, mainTable, rowKeys.size(), matches.size(), relType, relationTable, batchProcessingTimestamp, (System.currentTimeMillis() - startTime), allRelationKeys.toString());
        }
        return matches;
    }

    @Override
    public List<KeyColumn> queryListChildDataWithTTLAndWriteTimeChecked(String mainTable, Collection<KeyColumn> rowKeys, String contextName,
                                                                        String relType, String relationTable, Long batchProcessingTimestamp) {
        final long startTime = System.currentTimeMillis();
        LinkedList<KeyColumn> matches = new LinkedList<>();
        LinkedList<PreparedStatement> statements = new LinkedList<>();
        StringBuilder allRelationKeys = new StringBuilder();

        try {
            rowKeys.forEach(rowKey -> {
                String strRowKey = buildDataBaseRowKey(rowKey.getIds());
                try {
                    PreparedStatement ps1 = InMemoryDBConnection.getConnection().prepareStatement(queryGenerator.getPsForSelectQueryString());
                    setParameterValueForSelect(ps1, mainTable, strRowKey, contextName, relType, relationTable);
                    statements.add(ps1);
                } catch (final SQLException e) {
                    LOGGER.error(RTPConstants.ERROR, e);
                }
            });

            if (!statements.isEmpty()) {
                String relationKeys = extractKeyColumnsFromResultSet(statements, matches);
                allRelationKeys.append(relationKeys).append(':');
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Exiting queryListChildData for {}:{}:{}:{}:{}:{}:{}:{} and relation keys are :{}",
                    contextName, mainTable, rowKeys.size(), matches.size(), relType, relationTable, batchProcessingTimestamp, (System.currentTimeMillis() - startTime), allRelationKeys.toString());
        }
        return matches;
    }

    @Override
    public Collection<RepeatedMessage> queryDataWithTTLAndWriteTimeChecked(String mainTable, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) {
        final long currTime = System.currentTimeMillis();
        final List<RepeatedMessage> matchesList = new ArrayList<>(0);
        final List<PreparedStatement> statements = new ArrayList<>();
        int counter = 0;
        try {
            rowsToQuery.forEach(rowKey -> {
                try {
                    PreparedStatement psQuery = InMemoryDBConnection.getConnection().prepareStatement(queryGenerator.getPsForUpsertSelectQueryString().replace("BYPASS CACHE", ""));
                    setParameterValueForUpsert(psQuery, configurationRow.getTableName(), rowKey.toString());
                    statements.add(psQuery);
                } catch (final SQLException e) {
                    LOGGER.error(RTPConstants.ERROR, e);
                }
            });

            for (PreparedStatement statement : statements) {
                try (ResultSet row = statement.executeQuery()) {
                    if (row != null && row.next()) {
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("Data retrieved from table {}", configurationRow.getTableName());
                        }
                        String json = row.getString("maintabledata");
                        matchesList.add(deserialize(json));
                        counter++;
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("In queryList: Input Size = {}, Output = {}, Missing = {}, Time = {}:{}",
                    rowsToQuery.size(), counter, (rowsToQuery.size() - counter), (System.currentTimeMillis() - currTime), configurationRow.getTableName());
        }
        return matchesList;
    }

    @Override
    public List<RelationKeyInfo> queryListChildDataWithBulkProcessedFlag(String mainTable, Collection<KeyColumn> rowKeys, String contextName, String relType, String relationTable, Long batchProcessingTimestamp) {
        return new ArrayList<>();
    }

    @Override
    public List<KeyColumnDescriptor> getIndexedFks() {
        return new ArrayList<>(indexedFks);
    }

    @Override
    public void updateBulkProcessedFlag(long batchTimestamp, List<BulkRelationInfo> bulkRelationInfo, double[] messageCountArr) {
    }

    @Override
    public double[] deleteRowsUsingTTL(Map<Long, List<DeletedRowInfo>> deletedRowInfosPerBatch) {
        return new double[3];
    }

    @Override
    public void begin() {
    }

    private static String extractKeyColumnsFromResultSet(LinkedList<PreparedStatement> statements, LinkedList<KeyColumn> matches) throws SQLException {
        StringBuilder allRelationKey = new StringBuilder();
        for (PreparedStatement statement : statements) {
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String relationKey = result.getString(1);
                    allRelationKey.append(relationKey).append(':');
                    matches.add(new KeyColumn(relationKey.split(KeyColumn.ROWKEY_SEPARATOR)));
                }
            }
        }
        return allRelationKey.toString();
    }
    private static String extractKeyColumnsFromResultSet(Map<KeyColumn, PreparedStatement> statements, Map<KeyColumn, List<KeyColumn>> matches) throws SQLException {
        StringBuilder allRelationKey = new StringBuilder();
        for (Map.Entry<KeyColumn, PreparedStatement> statement : statements.entrySet()) {
            try (ResultSet result = statement.getValue().executeQuery()) {
                List<KeyColumn> currentMatches = new ArrayList<>();
                if (result.next()) {
                    String relationKey = result.getString(1);
                    allRelationKey.append(relationKey).append(':');
                    currentMatches.add(new KeyColumn(relationKey.split(KeyColumn.ROWKEY_SEPARATOR)));
                }
                matches.put(statement.getKey(), currentMatches);
            }
        }
        return allRelationKey.toString();
    }

    private static boolean hasAllFields(RepeatedMessage repeatedMessage, List<ColumnConfiguration> keys) {
        for (ColumnConfiguration key : keys) {
            if (repeatedMessage.getValue(key.getColumnName()) == null) {
                return false;
            }
        }
        return true;
    }

    public RepeatedMessage deserialize(String json) {
        try {
            return jsonRepeatedMessageFormatter.parse(json);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String serialize(final RepeatedMessage repeatedMessage) {
        try {
            repeatedMessage.removeNullValues();
            return jsonRepeatedMessageFormatter.build(repeatedMessage);
        } catch (Exception exception) {
            throw new RuntimeException("Serializing to a JSON threw an Exception (should never happen).", exception);
        }
    }

    public static void setParameterValueForUpsert(PreparedStatement ps, String tableName, String roeKey) {
        try {
            ps.setString(1, tableName);
            ps.setString(2, roeKey);
        } catch (final SQLException e) {
            LOGGER.error(RTPConstants.ERROR, e);
        }
    }

    public static void setParameterValueForUpsertForRef(PreparedStatement ps, String tableName, String roeKey) {
        try {
            ps.setString(1, tableName);
            ps.setString(2, roeKey);
        } catch (final SQLException e) {
            LOGGER.error(RTPConstants.ERROR, e);
        }
    }

    public static void setParameterValueForSelect(PreparedStatement ps, String tableName, String roeKey, String contextName,
                                                  String relType, String relationTable) {
        try {
            ps.setString(1, tableName);
            ps.setString(2, roeKey);
            ps.setString(3, contextName);
            ps.setString(4, relType);
            ps.setString(5, relationTable);
        } catch (final SQLException e) {
            LOGGER.error(RTPConstants.ERROR, e);
        }
    }

    public static void setParameterValueForInsert(PreparedStatement ps, String tableName, String roeKey, String contextName,
                                                  String relType, String relationTable, String relationKey,
                                                  Double timestamp, String tableNameCopy) {
        try {
            ps.setString(1, tableName);
            ps.setString(2, roeKey);
            ps.setString(3, contextName);
            ps.setString(4, relType);
            ps.setString(5, relationTable);
            ps.setString(6, relationKey);
            ps.setDouble(7, timestamp);
            ps.setString(8, tableNameCopy);
        } catch (final SQLException e) {
            LOGGER.error(RTPConstants.ERROR, e);
        }
    }

    private static void setParameterValueForInsertRef(PreparedStatement ps, String tableName, String rowKey,
                                                      Double timestamp, String mainTableData, String tableNameCopy) {
        try {
            ps.setString(1, tableName);
            ps.setString(2, rowKey);
            ps.setDouble(3, timestamp);
            ps.setString(4, mainTableData);
            ps.setString(5, tableNameCopy);
        } catch (final SQLException e) {
            LOGGER.error(RTPConstants.ERROR, e);
        }
    }

    private static boolean hasAllForeignKeysAndRelationKeys(boolean isMainRelation, RepeatedMessage currentMessage,
                                                            KeyColumnDescriptor foreignKeys,
                                                            KeyColumnDescriptor relationKeys) {
        if (isMainRelation && !hasAllFields(currentMessage, foreignKeys.getKeys())) {
            return false;
        }
        return isMainRelation || hasAllFields(currentMessage, relationKeys.getKeys());
    }

    private long getUpdateTimeFromCurrentObject(RepeatedMessage currentObject) {
        return currentObject.getValue(configurationRow.getUpdateTimeField()) != null ?
                Long.valueOf(currentObject.getValue(configurationRow.getUpdateTimeField()).toString()) : Long.valueOf(currentObject.getValue(PublisherUtils.TRANSACTION_TIME_FIELD_NAME).toString());
    }

    private boolean isReferenceData() {
        return configurationRow.getEntityStore().isReference();
    }
}