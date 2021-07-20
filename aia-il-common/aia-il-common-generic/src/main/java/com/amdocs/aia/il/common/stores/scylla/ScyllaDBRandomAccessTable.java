package com.amdocs.aia.il.common.stores.scylla;

import com.amdocs.aia.common.serialization.formatter.json.JsonRepeatedMessageFormatter;
import com.amdocs.aia.common.serialization.messages.Constants;
import com.amdocs.aia.common.serialization.messages.EnumValue;
import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.audit.RealtimeAuditDataPublisherManager;
import com.amdocs.aia.il.common.model.configuration.entity.DeletedRowInfo;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.publisher.CounterType;
import com.amdocs.aia.il.common.publisher.RealtimeDataPublisherCounterManager;
import com.amdocs.aia.il.common.stores.*;
import com.amdocs.aia.il.common.utils.PublisherUtils;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class ScyllaDBRandomAccessTable implements RandomAccessTable, Serializable {
    private static final long serialVersionUID = 3768807787769530803L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScyllaDBRandomAccessTable.class);
    public static final String EMPTY = "";

    private static final String OPERATION = "operation";
    private static final String DELETE = "delete";
    private static final String CHILD = "CHILD";
    private static final String PUBLISHER_STORE = "PublisherStore";
    private static final String QUERY_LIST_CHILD_DATA_FOR = "Query List ChildData for ";
    private static final Pattern ROWKEY_PATTERN = Pattern.compile(KeyColumn.ROWKEY_SEPARATOR);
    public static final String MICRO_POSTFIX = "000";

    private final String tableName;
    private final List<KeyColumnDescriptor> indexedFks;
    private final List<String> contextNames;
    private final List<String> mainTableNames;
    private final List<String> relationTables;
    private final List<String> relationTypes;
    private final List<KeyColumnDescriptor> relationKey;
    private final List<Boolean> isMainRelation;
    private final List<Boolean> doPropagation;
    private final List<String> counterTables;
    private final List<Boolean> isMTM;

    private final ConfigurationRow configurationRow;
    private final JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter;

    public ScyllaDBRandomAccessTable(ConfigurationRow configurationRow, JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter) {
        this.configurationRow = configurationRow;
        this.tableName = configurationRow.getTableName();
        this.indexedFks = new ArrayList<>();
        this.contextNames = new ArrayList<>();
        this.mainTableNames = new ArrayList<>();
        this.relationTables = new ArrayList<>();
        this.relationKey = new ArrayList<>();
        this.relationTypes = new ArrayList<>();
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
        return Collections.emptyList();
    }

    @Override
    public double[] asyncUpsert(String tableName, List<RepeatedMessage> repeatedMessages, Long batchProcessingTimestamp,
                                List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted,
                                List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving, boolean incrementalUpsert, List<DeletedRowInfo> deletedRowInfos, CounterType counterType, boolean isAuditEnable) {
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

        double[] messageCountArrQuery = new double[1];
        List<List<Pair<RepeatedMessage, Double>>> currentList = queryList(idList, batchProcessingTimestamp, messageCountArrQuery);
        double[] messageCountArr = upsertResultList(tableName, repeatedMessages, idList, updateTimeList, currentList, dbTimestampList, rowKeysPerTableInfoListToBeUpserted, incrementalUpsert, dayLightSaving, deletedRowInfos, counterType, isAuditEnable);
        messageCountArr[4] = messageCountArrQuery[0];
        //Index wise description - 0)main data count , 1) relational data count , 2) failed message count , 3) filtered message count, 4)Avg query time, 5) Avg merge time 6) Avg Upsert time
        return messageCountArr;
    }

    @Override
    public double[] asyncUpsert(String tableName, List<UpsertData> upsertDataList,
                                List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted,
                                List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving, boolean incrementalUpsert, Map<Long, List<DeletedRowInfo>> deletedRowInfosPerBatch, CounterType counterType, boolean isAuditEnable) {
        double[] messageCountArr = new double[7];
        upsertDataList.sort(Comparator.comparingLong(UpsertData::getTimeStamp));
        for (UpsertData upsertData : upsertDataList) {
            List<DeletedRowInfo> deletedRowInfos = new ArrayList<>();
            deletedRowInfosPerBatch.put(upsertData.getTimeStamp(), deletedRowInfos);
            double[] upsertResult = asyncUpsert(tableName, upsertData.getRepeatedMessageList(), upsertData.getTimeStamp(), rowKeysPerTableInfoListToBeUpserted, rowKeysPerTransactionIDList, dayLightSaving, incrementalUpsert, deletedRowInfos, counterType, isAuditEnable);
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
    public Collection<RepeatedMessage> queryData(String mainTable, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) {
        List<RepeatedMessage> matches = new ArrayList<>();
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) ScyllaQueryGeneratorHolder.getInstance().getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());

        try {
            rowsToQuery.forEach(rowKey -> {
                String strRowKey = buildDataBaseRowKey(rowKey.getIds());
                PreparedStatement preparedStatement = qGen.getPsForSelectUpsert();
                final CompletionStage<AsyncResultSet> asyncResult = completionStage.thenCompose(s -> s.executeAsync(preparedStatement.bind(mainTable, strRowKey)));
                resultSets.add(asyncResult);
            });
            extractRepeatedMessagesFromResultSet(resultSets, matches, batchProcessingTimestamp);
            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.REPLICATOR, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                            Long.parseLong(resultSets.size() + EMPTY));
            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.TRANSFORMER, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                            Long.parseLong(resultSets.size() + EMPTY));
        } catch (Exception e) {
            LOGGER.error("ERROR_QUERY_DATA" + tableName + e.getMessage(), e);
            printDatabaseStats(QUERY_LIST_CHILD_DATA_FOR + this.tableName);
            throw new RuntimeException(e);
        }
        return matches;
    }

    @Override
    public Collection<RepeatedMessage> queryBulkData(String mainTable, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) {
        List<RepeatedMessage> matches = new ArrayList<>();
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) ScyllaQueryGeneratorHolder.getInstance().getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());

        try {
            rowsToQuery.forEach(rowKey -> {
                String strRowKey = buildDataBaseRowKey(rowKey.getIds());
                PreparedStatement preparedStatement = qGen.getPsForSelectBulkUpsert();
                final CompletionStage<AsyncResultSet> asyncResult = completionStage.thenCompose(s -> s.executeAsync(preparedStatement.bind(mainTable, strRowKey)));
                resultSets.add(asyncResult);
            });
            extractRepeatedMessagesFromResultSet(resultSets, matches);
            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.REPLICATOR, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                            Long.parseLong(resultSets.size() + EMPTY));
            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.TRANSFORMER, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                            Long.parseLong(resultSets.size() + EMPTY));
        } catch (Exception e) {
            LOGGER.error("ERROR_QUERY_DATA" + tableName + e.getMessage(), e);
            printDatabaseStats(QUERY_LIST_CHILD_DATA_FOR + this.tableName);
            throw new RuntimeException(e);
        }
        return matches;
    }

    @Override
    public Collection<RepeatedMessage> queryDataWithTTLAndWriteTimeChecked(String mainTable, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) {
        List<RepeatedMessage> matches = new ArrayList<>();
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) ScyllaQueryGeneratorHolder.getInstance().getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());

        try {
            rowsToQuery.forEach(rowKey -> {
                String strRowKey = buildDataBaseRowKey(rowKey.getIds());
                PreparedStatement preparedStatement = qGen.getPsForSelectUpsert();
                final CompletionStage<AsyncResultSet> asyncResult = completionStage.thenCompose(s -> s.executeAsync(preparedStatement.bind(mainTable, strRowKey)));
                resultSets.add(asyncResult);
            });
            extractRepeatedMessagesFromResultSetWithTTLAndWriteTimeChecked(resultSets, matches, batchProcessingTimestamp);
            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.REPLICATOR, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                            Long.parseLong(resultSets.size() + EMPTY));
            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.TRANSFORMER, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                            Long.parseLong(resultSets.size() + EMPTY));
        } catch (Exception e) {
            LOGGER.error("ERROR_QUERY_DATA" + tableName + e.getMessage(), e);
            printDatabaseStats(QUERY_LIST_CHILD_DATA_FOR + this.tableName);
            throw new RuntimeException(e);
        }
        return matches;
    }

    @Override
    public Collection<RepeatedMessage> queryAllData(String mainTable) {
        List<RepeatedMessage> matches = new ArrayList<>();
        final CqlSession session = ScyllaConnection.getSession();
        try {
            PreparedStatement preparedStatement = ScyllaQueryGeneratorHolder.getInstance().getRefSelectAllStatement();
            final ResultSet resultSet = session.execute(preparedStatement.bind(mainTable));
            resultSet.forEach(row -> matches.add(deserialize(row.getString(0))));
            //Adding Null Check to facilitate reference store job since it does not use RealtimeDataPublisherCounterManager for metrices.
            if (RealtimeDataPublisherCounterManager.INSTANCE != null) {
                RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                        addCounter(CounterType.REPLICATOR, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                                1l);
                RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                        addCounter(CounterType.TRANSFORMER, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                                1l);
            }
        } catch (Exception e) {
            LOGGER.error("ERROR_QUERY_ALL_DATA" + mainTable + e.getMessage(), e);
            printDatabaseStats("Query All data for " + mainTable);
            throw new RuntimeException(e);
        }
        return matches;
    }

    @Override
    public Collection<KeyColumn> queryIds(String indexName, Collection<KeyColumn> rowsToQuery) {
        return Collections.emptyList();
    }

    @Override
    public Collection<KeyColumn> queryIds(String indexName, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) {
        return Collections.emptyList();
    }

    @Override
    public List<Boolean> checkExists(String indexName, Collection<KeyColumn> rowsToQuery) {
        return Collections.emptyList();
    }

    @Override
    public void shutdown() {
        //Do Nothing
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
        //Do Nothing
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void registerFkIndex(KeyColumnDescriptor fkey) {
        this.indexedFks.add(fkey);
    }

    @Override
    public void registerFkIndex(KeyColumnDescriptor fkey, String mainTableName, String contextname, String relationtable, KeyColumnDescriptor relationKey, String relationtype, boolean isMainRelation, boolean doPropagation, String counterTableName, boolean isMTM) {
        this.indexedFks.add(fkey);
        this.relationTypes.add(relationtype);
        this.contextNames.add(contextname);
        this.relationKey.add(relationKey);
        this.mainTableNames.add(mainTableName);
        this.relationTables.add(relationtable);
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

    private double[] upsertResultList(String tableName, List<RepeatedMessage> repeatedMessages,
                                      List<String> rowKeys, List<Long> updateTimes,
                                      List<List<Pair<RepeatedMessage, Double>>> currentList,
                                      List<Long> databaseTimestampList,
                                      List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted,
                                      boolean incrementalUpdate, boolean dayLightSaving, List<DeletedRowInfo> deletedRowInfos, CounterType counterType, boolean isAuditEnable) {// NOSONAR
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        ScyllaQueryGeneratorHolder scyllaQueryGeneratorHolder = ScyllaQueryGeneratorHolder.getInstance();
        //Index wise description - 0)main data count , 1) relational data count , 2) failed message count , 3) filtered message count, 4)Avg query time, 5) Avg merge time 6) Avg Upsert time
        double[] messageCountArr = new double[7];
        double countOfFilteredMessages = 0.0;
        long totalMergeTime = 0L;
        int mergeMessageCount = 0;
        int staleRecordsCount = 0;

        try {
            for (int i = 0; i < repeatedMessages.size(); i++) {
                boolean ignoreThisMessage = false;
                RepeatedMessage existingMessage = null;
                Double existingMessageTsVersion = null;
                RepeatedMessage currentMessage = repeatedMessages.get(i);
                String rowKey = rowKeys.get(i);
                long updateTime = updateTimes.get(i);
                Map<Integer, KeyColumnDescriptor> updatedForeignKeys = new HashMap<>();

                List<Pair<RepeatedMessage, Double>> current = currentList.get(i);
                //in case of main table, current will be with size of 1
                if (!current.isEmpty()) {
                    existingMessage = current.iterator().next().getLeft();
                    existingMessageTsVersion = current.iterator().next().getRight();
                    //In case update time field is not there , then fetch transaction time from message itself.
                    long existingUpdateTime = getUpdateTimeFromCurrentObject(existingMessage);
                    //Compare update Time from Proto message, and insert the record only if IncomingUpdateTime > ExistingUpdateTime
                    if (isUpdateTimeGreaterThanExistingTime(updateTime, existingUpdateTime, dayLightSaving)) {
                        if (incrementalUpdate) {
                            long startTimeMerge = System.currentTimeMillis();
                            currentMessage = PublisherUtils.CompareAndMergeRepeatedMessages(existingMessage, currentMessage);
                            mergeMessageCount++;
                            totalMergeTime += System.currentTimeMillis() - startTimeMerge;
                            if (currentMessage == null && !rowKeysPerTableInfoListToBeUpserted.isEmpty()) {
                                deleteOldRowKeysPerTransactionId(existingMessage, rowKeysPerTableInfoListToBeUpserted, rowKey);
                                ignoreThisMessage = true;
                                countOfFilteredMessages++;
                            }
                            if (currentMessage == null && isAuditEnable) {
                                RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                                        configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, ""), CounterType.RECORDSNOCHANGE, existingMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME),
                                        this.tableName, 1L);
                            }
                        } else {
                            long startTimeMerge = System.currentTimeMillis();
                            currentMessage = PublisherUtils.mergeRepeatedMessages(existingMessage, currentMessage);
                            mergeMessageCount++;
                            totalMergeTime += System.currentTimeMillis() - startTimeMerge;
                        }

                        if (currentMessage != null) {
                            Long dataBaseTimeStamp = databaseTimestampList.get(i);
                            updatedForeignKeys = deleteOldDataAndForeignKeys(completionStage, scyllaQueryGeneratorHolder,
                                    existingMessage, currentMessage, existingMessageTsVersion, dataBaseTimeStamp, rowKey, resultSets, messageCountArr);
                        }
                    } else if (isUpdateTimeLessThanExistingTime(updateTime, existingUpdateTime, dayLightSaving)) {
                        //If IncomingUpdateTime < ExistingUpdateTime log this scenario
                        ignoreThisMessage = true;
                        staleRecordsCount++;
                        deleteOldRowKeysPerTransactionId(currentMessage, rowKeysPerTableInfoListToBeUpserted, rowKey);
                    }
                }

                if (!ignoreThisMessage && currentMessage != null) {
                    Long dataBaseTimeStamp = databaseTimestampList.get(i);
                    String currentMessageOperation = ((EnumValue) currentMessage.getValue(OPERATION)).getValueName();
                    if (!DELETE.equalsIgnoreCase(currentMessageOperation)) {
                        insertDataIntoDatabase(completionStage, scyllaQueryGeneratorHolder, currentMessage, existingMessage, rowKey,
                                dataBaseTimeStamp, updatedForeignKeys, resultSets, messageCountArr);
                        if (isAuditEnable) {
                            RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                                    configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), CounterType.RECORDSSTORED, currentMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME),
                                    this.tableName, 1L);
                        }
                    } else {
                        if (existingMessageTsVersion == null) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("DELETE Fix : setting new timestamp. {}", dataBaseTimeStamp);
                            }
                            existingMessageTsVersion = dataBaseTimeStamp.doubleValue();
                        }
                        if (deletedRowInfos == null) {
                            insertDataIntoDatabaseWithTTL(completionStage, scyllaQueryGeneratorHolder, currentMessage, rowKey,
                                    dataBaseTimeStamp, existingMessageTsVersion, resultSets, messageCountArr);
                            if (isAuditEnable) {
                                RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                                        configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), CounterType.RECORDSDELETED, currentMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME),
                                        this.tableName, 1L);
                            }

                        } else {
                            deletedRowInfos.add(new DeletedRowInfo(currentMessage, rowKey, dataBaseTimeStamp, existingMessageTsVersion));
                        }
                    }
                }
            }

            if (LOGGER.isDebugEnabled() && !rowKeysPerTableInfoListToBeUpserted.isEmpty() && countOfFilteredMessages > 0) {
                LOGGER.debug("Count of filtered messages for table : {} in case of incremental update is: {}", tableName, countOfFilteredMessages);
            }
            final long startTimeUpsert = System.currentTimeMillis();
            executeResultSetFutures(resultSets, messageCountArr);
            final long totalUpsertTime = System.currentTimeMillis() - startTimeUpsert;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Wait time in upserting is {} for table {} ", totalUpsertTime, this.tableName);
            }

            if (staleRecordsCount > 0 && LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stale record count is {} out of total records {} for table {}", staleRecordsCount, repeatedMessages.size(), this.tableName);
            }

            if (RealtimeDataPublisherCounterManager.INSTANCE != null) {
                RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                        addCounter(CounterType.REPLICATOR, CounterType.UPSERTWRITECOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                                Long.parseLong(resultSets.size() + EMPTY));
                RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                        addCounter(CounterType.TRANSFORMER, CounterType.UPSERTWRITECOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                                Long.parseLong(resultSets.size() + EMPTY));
            }
            messageCountArr[3] = countOfFilteredMessages;
            messageCountArr[5] = mergeMessageCount != 0 ? (((double) totalMergeTime) / mergeMessageCount) : 0;
            messageCountArr[6] = (messageCountArr[0] + messageCountArr[1]) != 0 ? totalUpsertTime / (messageCountArr[0] + messageCountArr[1]) : 0;
        } catch (Exception e) {
            printDatabaseStats("async Upsert for " + this.tableName);
            throw e;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("In Async upsert for table:{}:Input Size:{}:Index Size:{}:Output Size: {} :Missing: {}:Time: {}",
                    this.tableName, repeatedMessages.size(), indexedFks.size(), 0, repeatedMessages.size(), (System.currentTimeMillis() - startTime));
        }
        return messageCountArr;
    }

    private long getUpdateTimeFromCurrentObject(RepeatedMessage currentObject) {
        return currentObject.getValue(configurationRow.getUpdateTimeField()) != null ?
                Long.valueOf(currentObject.getValue(configurationRow.getUpdateTimeField()).toString()) : Long.valueOf(currentObject.getValue(PublisherUtils.TRANSACTION_TIME_FIELD_NAME).toString());
    }

    /*private void executeResultSetFutures(Map<String,IndexedActions> actionsPerTable , double[] messageCountArr) {
        int count = 0; //count to maintain failed messages per data table and relational table.
        for(Map.Entry<String,IndexedActions> entry : actionsPerTable.entrySet()){
            for(ResultSetFuture future : entry.getValue().getMainDataFutureList()){
                ResultSet rows = future.getUninterruptibly();
                if (rows == null) {
                    count++;
                    messageCountArr[3]++;
                }
            }
            messageCountArr[0] = messageCountArr[0] + (entry.getValue().getMainDataFutureList().size() - count);
            count = 0;
            for(ResultSetFuture future : entry.getValue().getRelationalDataFutureList()){
                ResultSet rows = future.getUninterruptibly();
                if (rows == null) {
                    count++;
                    messageCountArr[3]++;
                }
            }
            messageCountArr[1] = messageCountArr[1] + (entry.getValue().getRelationalDataFutureList().size() - count);
        }
    }*/

    private static void executeResultSetFutures(final List<CompletionStage<AsyncResultSet>> resultSets, double[] messageCountArr) {
        for (final CompletionStage<AsyncResultSet> resultSet : resultSets) {
            try {
                resultSet.toCompletableFuture().get();
            } catch (Exception e) {
                // Failed message counter
                messageCountArr[2]++;
                LOGGER.error("Error while executing result sets");
                throw new RuntimeException(e);
            }
        }
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
    private Map<Integer, KeyColumnDescriptor> deleteOldDataAndForeignKeys(final CompletionStage<CqlSession> completionStage,
                                                                          ScyllaQueryGeneratorHolder holder,
                                                                          RepeatedMessage existingMessage,
                                                                          RepeatedMessage currentMessage,
                                                                          Double existingMessageTsVersion,
                                                                          Long dataBaseTimeStamp,
                                                                          String rowKey,
                                                                          final List<CompletionStage<AsyncResultSet>> resultSets,
                                                                          double[] messageCountArr) {
        Map<Integer, KeyColumnDescriptor> changedKeys = new HashMap<>();
        ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) holder.getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());
        PreparedStatement psQueryForInsertWithTTL = qGen.getPsForInsertWithTTLWriteTime();
        PreparedStatement psQueryForInsertRelationWithTTL = qGen.getPsForInsertRelationWithTTLWriteTime();

        String existingMessageBytes = serialize(existingMessage);
        if (!isReferenceData()) {
            resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psQueryForInsertWithTTL.bind(this.tableName, rowKey, existingMessageTsVersion, existingMessageBytes, this.tableName, Long.parseLong(dataBaseTimeStamp + MICRO_POSTFIX)))));
        }

        for (int j = 0; j < indexedFks.size(); j++) {
            KeyColumnDescriptor foreignKeys = indexedFks.get(j);
            KeyColumnDescriptor relationKeys = relationKey.get(j);
            String relTable = relationTables.get(j);
            String ctx = contextNames.get(j);
            String mainTableName = mainTableNames.get(j);
            String relType = relationTypes.get(j);
            boolean isMTMRelation = isMTM.get(j);
            String counterTable = counterTables.get(j);
            boolean isDoPropagation = doPropagation.get(j);

            KeyColumn newValues = new KeyColumn(currentMessage, foreignKeys.getKeys());
            KeyColumn oldValues = new KeyColumn(existingMessage, foreignKeys.getKeys());

            if (!newValues.compareTo(oldValues)) {
                String rowKeyFromExistingMessage = buildDataBaseRowKey(existingMessage, foreignKeys.getKeys());
                String relationKeyFromExistingMessage = buildDataBaseRowKey(existingMessage, relationKeys.getKeys());
                double existingTimestampForForeignKeys = getExistingTimeStamp(qGen, mainTableName, rowKeyFromExistingMessage, ctx, relType, relTable, relationKeyFromExistingMessage, existingMessageTsVersion);

                resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psQueryForInsertRelationWithTTL.bind(mainTableName,
                        rowKeyFromExistingMessage, ctx, relType, relTable, relationKeyFromExistingMessage,
                        existingTimestampForForeignKeys, mainTableName, Long.parseLong(dataBaseTimeStamp + MICRO_POSTFIX)))));
                //messageCountArr[1]++; //Relational data counter
                //For OTM or MTO always make counter relation delete but for MTM , only delete in case doPropagation is true.
                if (!isMTMRelation || isDoPropagation) {
                    //For counter relation , indexedFK hold value of PK which will never change , so if FK changes for main relation then
                    //we have to delete counter relation as well.
                    resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psQueryForInsertRelationWithTTL.bind(relTable,
                            relationKeyFromExistingMessage, ctx, getCounterRelation(relType), counterTable,
                            rowKeyFromExistingMessage, existingTimestampForForeignKeys, relTable, Long.parseLong(dataBaseTimeStamp + MICRO_POSTFIX)))));
                    //messageCountArr[1]++; //Relational data counter
                }
                changedKeys.put(j, foreignKeys);
            }
        }
        return changedKeys;
    }

    private void deleteOldRowKeysPerTransactionId(RepeatedMessage repeatedMessage, List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted, String rowKey) {
        if (!rowKeysPerTableInfoListToBeUpserted.isEmpty()) {
            KeyColumn ids = new KeyColumn(repeatedMessage, this.configurationRow.getPkColumns());
            rowKeysPerTableInfoListToBeUpserted.forEach(rowKeysPerTableInfo -> rowKeysPerTableInfo.getTxnUpdatedRows().forEach((ti, kc) -> {
                if (ti.equalsIgnoreCase(this.tableName)) {
                    kc.remove(ids);
                }
            }));
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} record has a recent version in store compared to incoming version. Ignoring the incoming version.", rowKey);
        }
    }

    private void insertDataIntoDatabase(final CompletionStage<CqlSession> completionStage, ScyllaQueryGeneratorHolder holder,
                                        RepeatedMessage currentMessage,
                                        RepeatedMessage existingMessage, String rowKey, Long dataBaseTimeStamp,
                                        Map<Integer, KeyColumnDescriptor> updatedForeignKeys,
                                        final List<CompletionStage<AsyncResultSet>> resultSets, double[] messageCountArr) {
        String currentMessageJson = serialize(currentMessage);
        ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) holder.getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());
        PreparedStatement psQueryForInsert = qGen.getPsForInsert();

        resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psQueryForInsert.bind(this.tableName, rowKey,
                dataBaseTimeStamp.doubleValue(), currentMessageJson, this.tableName, Long.parseLong(dataBaseTimeStamp + MICRO_POSTFIX)))));
        // main data counter
        messageCountArr[0]++;

        if (existingMessage == null) {
            handleInsertsForAllForeignKeys(completionStage, qGen, currentMessage, resultSets, dataBaseTimeStamp, messageCountArr);
        } else {
            handleInsertsForUpdatedKeys(completionStage, qGen, updatedForeignKeys, currentMessage, resultSets, dataBaseTimeStamp, messageCountArr);
        }
    }

    private void handleInsertsForAllForeignKeys(final CompletionStage<CqlSession> completionStage, ScyllaQueryGenerator qGen, RepeatedMessage currentMessage,
                                                final List<CompletionStage<AsyncResultSet>> resultSets, Long dataBaseTimeStamp,
                                                double[] messageCountArr) {
        PreparedStatement psQueryForInsertRelation = qGen.getPsForInsertRelation();
        for (int j = 0; j < indexedFks.size(); j++) {
            final KeyColumnDescriptor foreignKeys = indexedFks.get(j);
            final KeyColumnDescriptor relationKeys = relationKey.get(j);

            final String rowKeyFromMessage = buildDataBaseRowKey(currentMessage, foreignKeys.getKeys());
            final String relationKeyFromMessage = buildDataBaseRowKey(currentMessage, relationKeys.getKeys());

            if (!hasAllForeignKeysAndRelationKeys(isMainRelation.get(j), currentMessage, foreignKeys, relationKeys)) {
                continue;
            }
            final String relTable = relationTables.get(j);
            final String ctx = contextNames.get(j);
            final String mainTableName = mainTableNames.get(j);
            final String relType = relationTypes.get(j);

            resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psQueryForInsertRelation.bind(mainTableName,
                    rowKeyFromMessage, ctx, relType, relTable, relationKeyFromMessage, dataBaseTimeStamp.doubleValue(),
                    mainTableName, Long.parseLong(dataBaseTimeStamp + MICRO_POSTFIX)))));
            // Relational data counter
            messageCountArr[1]++;
        }
    }

    private void handleInsertsForUpdatedKeys(final CompletionStage<CqlSession> completionStage, ScyllaQueryGenerator qGen, Map<Integer, KeyColumnDescriptor> updatedForeignKeys,
                                             RepeatedMessage currentMessage, final List<CompletionStage<AsyncResultSet>> resultSets,
                                             Long dataBaseTimeStamp, double[] messageCountArr) {
        PreparedStatement psQueryForInsertRelation = qGen.getPsForInsertRelation();
        for (Map.Entry<Integer, KeyColumnDescriptor> entry : updatedForeignKeys.entrySet()) {
            int j = entry.getKey();
            final KeyColumnDescriptor foreignKeys = indexedFks.get(j);
            final KeyColumnDescriptor relationKeys = relationKey.get(j);

            final String rowKeyFromMessage = buildDataBaseRowKey(currentMessage, foreignKeys.getKeys());
            final String relationKeyFromMessage = buildDataBaseRowKey(currentMessage, relationKeys.getKeys());
            if (!hasAllForeignKeysAndRelationKeys(isMainRelation.get(j), currentMessage, foreignKeys, relationKeys)) {
                continue;
            }
            final String relTable = relationTables.get(entry.getKey());
            final String ctx = contextNames.get(j);
            final String mainTableName = mainTableNames.get(j);
            final String relType = relationTypes.get(j);
            final boolean isMTMRelation = isMTM.get(j);
            final String counterTable = counterTables.get(j);
            final boolean isDoPropagation = doPropagation.get(j);

            resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psQueryForInsertRelation.bind(mainTableName,
                    rowKeyFromMessage, ctx, relType, relTable, relationKeyFromMessage, dataBaseTimeStamp.doubleValue(),
                    mainTableName, Long.parseLong(dataBaseTimeStamp + MICRO_POSTFIX)))));
            messageCountArr[1]++; //Relational data counter
            //For OTM or MTO always make counter relation insert but for MTM , only insert in case doPropagation is true.
            if (isCounterRelationUpsertNeeded(isMTMRelation, isDoPropagation)) {
                //For counter relation , indexedFK hold value of PK which will never change , so if FK changes for main relation then
                //we have to make insert for counter relation as well.

                resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psQueryForInsertRelation.bind(relTable,
                        relationKeyFromMessage, ctx, getCounterRelation(relType), counterTable, rowKeyFromMessage,
                        dataBaseTimeStamp.doubleValue(), relTable, Long.parseLong(dataBaseTimeStamp + MICRO_POSTFIX)))));
                // Relational data counter
                messageCountArr[1]++;
            }
        }
    }

    @Override
    public Map<KeyColumn, List<KeyColumn>> queryListChildData(String mainTable, Collection<KeyColumn> rowKeys, String contextName,
                                                              String relType, String relationTable, Long batchProcessingTimestamp) {
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        final Map<KeyColumn, List<KeyColumn>> matches = new HashMap<>();
        final Map<KeyColumn, CompletionStage<AsyncResultSet>> resultSets = new LinkedHashMap<>();
        final ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) ScyllaQueryGeneratorHolder.getInstance().getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());
        try {
            rowKeys.forEach(rowKey -> {
                if (KeyColumn.isEmpty(rowKey)) {
                    String strRowKey = buildDataBaseRowKey(rowKey.getIds());
                    PreparedStatement ps1 = qGen.getPsForSelect();
                    resultSets.put(rowKey, completionStage.thenCompose(s -> s.executeAsync(ps1.bind(mainTable, strRowKey, contextName,
                            relType, relationTable))));
                }
            });
            if (!resultSets.isEmpty()) {
                extractKeyColumnsFromResultSet(resultSets, matches, batchProcessingTimestamp);
                if (RealtimeDataPublisherCounterManager.INSTANCE != null) {
                    RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                            addCounter(CounterType.REPLICATOR, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                                    Long.parseLong(resultSets.size() + EMPTY));
                    RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                            addCounter(CounterType.TRANSFORMER, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                                    Long.parseLong(resultSets.size() + EMPTY));
                }
            }
        } catch (Exception e) {
            LOGGER.error("ERROR_QUERY_DATA_FOR_CHILD {}", tableName + e.getMessage());
            printDatabaseStats(QUERY_LIST_CHILD_DATA_FOR + this.tableName);
            throw new RuntimeException(e);
        }
        return matches;
    }

    @Override
    public List<KeyColumn> queryListChildDataWithTTLAndWriteTimeChecked(String mainTable, Collection<KeyColumn> rowKeys, String contextName,
                                                                        String relType, String relationTable, Long batchProcessingTimestamp) {
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        final List<KeyColumn> matches = new ArrayList<>();
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        final ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) ScyllaQueryGeneratorHolder.getInstance().getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());
        try {
            rowKeys.forEach(rowKey -> {
                String strRowKey = buildDataBaseRowKey(rowKey.getIds());
                final PreparedStatement ps1 = qGen.getPsForSelect();
                final CompletionStage<AsyncResultSet> asyncResult = completionStage.thenCompose(s -> s.executeAsync(ps1.bind(mainTable, strRowKey, contextName,
                        relType, relationTable)));
                resultSets.add(asyncResult);
            });
            if (!resultSets.isEmpty()) {
                extractKeyColumnsFromResultSetWithTTLAndWriteTimeChecked(resultSets, matches, batchProcessingTimestamp);
                if (RealtimeDataPublisherCounterManager.INSTANCE != null) {
                    RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                            addCounter(CounterType.REPLICATOR, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                                    Long.parseLong(resultSets.size() + EMPTY));
                    RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                            addCounter(CounterType.TRANSFORMER, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                                    Long.parseLong(resultSets.size() + EMPTY));
                }
            }
        } catch (Exception e) {
            LOGGER.error("ERROR_QUERY_DATA_FOR_CHILD: {},{}", tableName, e.getMessage());
            printDatabaseStats(QUERY_LIST_CHILD_DATA_FOR + this.tableName);
            throw new RuntimeException(e);
        }
        return matches;
    }

    @Override
    public List<RelationKeyInfo> queryListChildDataWithBulkProcessedFlag(String mainTable, Collection<KeyColumn> rowKeys, String contextName, String relType, String relationTable, Long batchProcessingTimestamp) {
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        final List<RelationKeyInfo> matches = new ArrayList<>();
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        final ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) ScyllaQueryGeneratorHolder.getInstance().getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());
        try {
            List<KeyColumn> sourceRowKeys = new ArrayList<>();
            rowKeys.forEach(rowKey -> {
                sourceRowKeys.add(rowKey);
                String strRowKey = buildDataBaseRowKey(rowKey.getIds());
                PreparedStatement ps1 = qGen.getPsForSelectWithBulkProcessedFlag();
                final CompletionStage<AsyncResultSet> asyncResult = completionStage.thenCompose(s -> s.executeAsync(ps1.bind(mainTable, strRowKey, contextName,
                        relType, relationTable)));
                resultSets.add(asyncResult);
            });

            if (!resultSets.isEmpty()) {
                extractKeyColumnsFromResultSet(resultSets, matches, sourceRowKeys);
                if (RealtimeDataPublisherCounterManager.INSTANCE != null) {
                    RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                            addCounter(CounterType.REPLICATOR, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                                    Long.parseLong(resultSets.size() + EMPTY));
                    RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                            addCounter(CounterType.TRANSFORMER, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                                    Long.parseLong(resultSets.size() + EMPTY));
                }
            }
        } catch (Exception e) {
            LOGGER.error("ERROR_QUERY_DATA_FOR_CHILD:{},{}", tableName, e.getMessage());
            printDatabaseStats(QUERY_LIST_CHILD_DATA_FOR + this.tableName);
            throw new RuntimeException(e);
        }
        return matches;
    }

//    private static void extractKeyColumnsFromResultSet(final List<CompletionStage<AsyncResultSet>> resultSets,
//                                                       final List<KeyColumn> matches, Long batchProcessingTimestamp) {
//        for (final CompletionStage<AsyncResultSet> resultSet : resultSets) {
//            processRows(resultSet.toCompletableFuture().join(), matches, batchProcessingTimestamp);
//        }
//    }

    private static void extractKeyColumnsFromResultSet(final Map<KeyColumn, CompletionStage<AsyncResultSet>> resultSets,
                                                       final Map<KeyColumn, List<KeyColumn>> matches, Long batchProcessingTimestamp) {
        for (final Map.Entry<KeyColumn, CompletionStage<AsyncResultSet>> resultSet : resultSets.entrySet()) {
            List<KeyColumn> currentMatches = new ArrayList<>();
            processRows(resultSet.getValue().toCompletableFuture().join(), currentMatches, batchProcessingTimestamp);
            matches.put(resultSet.getKey(), currentMatches);
        }
    }

    private static void processRows(final AsyncResultSet rs, final List<KeyColumn> matches, Long batchProcessingTimestamp) {
        for (final Row row : rs.currentPage()) {
            final Object ttlValue = row.getObject(1);
            final double tsVersion = row.getDouble(3);
            if (ttlValue == null && tsVersion <= batchProcessingTimestamp) {
                matches.add(new KeyColumn(ROWKEY_PATTERN.split(row.getString(0))));
            }
        }
        if (rs.hasMorePages()) {
            processRows(rs.fetchNextPage().toCompletableFuture().join(), matches, batchProcessingTimestamp);
        }
    }

    private static void extractKeyColumnsFromResultSetWithTTLAndWriteTimeChecked(final List<CompletionStage<AsyncResultSet>> resultSets,
                                                                                 final List<KeyColumn> matches,
                                                                                 final long batchProcessingTimestamp) {
        for (final CompletionStage<AsyncResultSet> resultSet : resultSets) {
            processRows(resultSet.toCompletableFuture().join(), matches, batchProcessingTimestamp);
        }
    }

    private static void processRows(final AsyncResultSet rs, final List<KeyColumn> matches, final long batchProcessingTimestamp) {
        for (final Row row : rs.currentPage()) {
            final String relationKey = row.getString(0);
            final Object ttl = row.getObject(1);
            final Object writeTime = row.getObject(2);
            final double tsVersion = row.getDouble(3);
            if ((ttl == null || Long.parseLong(writeTime.toString()) >= Long.parseLong(batchProcessingTimestamp + MICRO_POSTFIX)) && tsVersion <= batchProcessingTimestamp) {
                matches.add(new KeyColumn(ROWKEY_PATTERN.split(relationKey)));
            }
        }
        if (rs.hasMorePages()) {
            processRows(rs.fetchNextPage().toCompletableFuture().join(), matches, batchProcessingTimestamp);
        }
    }

    private static void extractKeyColumnsFromResultSet(final List<CompletionStage<AsyncResultSet>> resultSets,
                                                       final List<RelationKeyInfo> matches, final List<KeyColumn> sourceRowKeys) {
        int index = 0;
        for (final CompletionStage<AsyncResultSet> resultSet : resultSets) {
            processRows(resultSet.toCompletableFuture().join(), matches, sourceRowKeys.get(index));
            ++index;
        }
    }

    private static void processRows(final AsyncResultSet rs, final List<RelationKeyInfo> matches, final KeyColumn sourceRowKey) {
        for (final Row row : rs.currentPage()) {
            final String relationKey = row.getString(0);
            final boolean bulkProcessedFlag = row.getBoolean(1);
            final double tsVersion = row.getDouble(2);
            matches.add(new RelationKeyInfo(new KeyColumn(ROWKEY_PATTERN.split(relationKey)), sourceRowKey, tsVersion, bulkProcessedFlag));
        }
        if (rs.hasMorePages()) {
            processRows(rs.fetchNextPage().toCompletableFuture().join(), matches, sourceRowKey);
        }
    }

    private void extractRepeatedMessagesFromResultSet(final List<CompletionStage<AsyncResultSet>> resultSets, List<RepeatedMessage> matches) throws ExecutionException, InterruptedException {
        for (final CompletionStage<AsyncResultSet> resultSet : resultSets) {
            final Iterator<Row> iter = resultSet.toCompletableFuture().get().currentPage().iterator();
            if (iter.hasNext()) {
                final Row row = iter.next();
                matches.add(deserialize(row.getString(0)));
            }
        }
    }

    private void extractRepeatedMessagesFromResultSet(final List<CompletionStage<AsyncResultSet>> resultSets, List<RepeatedMessage> matches, Long batchProcessingTimestamp) throws ExecutionException, InterruptedException {
        for (final CompletionStage<AsyncResultSet> resultSet : resultSets) {
            processRows(resultSet.toCompletableFuture().join(), matches, batchProcessingTimestamp, 0L, null);
        }
    }

    private void processRows(final AsyncResultSet rs, final List<RepeatedMessage> matches, Long batchProcessingTimestamp, double selectedVersion, Row selectedRow) {
        for (final Row row : rs.currentPage()) {
            final double tsVersion = row.getDouble(1);
            if (tsVersion <= batchProcessingTimestamp && tsVersion > selectedVersion) {
                selectedVersion = tsVersion;
                selectedRow = row;
            }
        }
        if (rs.hasMorePages()) {
            processRows(rs.fetchNextPage().toCompletableFuture().join(), matches, batchProcessingTimestamp, selectedVersion, selectedRow);
        } else if (selectedRow != null) {
            matches.add(deserialize(selectedRow.getString(0)));
        }
    }

    private void extractRepeatedMessagesFromResultSetWithTTLAndWriteTimeChecked(final List<CompletionStage<AsyncResultSet>> resultSets,
                                                                                List<RepeatedMessage> matches, Long batchProcessingTimestamp) throws ExecutionException, InterruptedException {
        for (final CompletionStage<AsyncResultSet> resultSet : resultSets) {
            processRowsForRepeatedMessageWithTTLAndWriteTimeChecked(resultSet.toCompletableFuture().join(), matches, batchProcessingTimestamp, 0L, null);
        }
    }

    private void processRowsForRepeatedMessageWithTTLAndWriteTimeChecked(final AsyncResultSet rs, final List<RepeatedMessage> matches, Long batchProcessingTimestamp, double selectedVersion, Row selectedRow) {
        for (final Row row : rs.currentPage()) {
            final double tsVersion = row.getDouble(1);
            if (tsVersion <= batchProcessingTimestamp && tsVersion > selectedVersion) {
                selectedVersion = tsVersion;
                selectedRow = row;
            }
        }
        if (rs.hasMorePages()) {
            processRowsForRepeatedMessageWithTTLAndWriteTimeChecked(rs.fetchNextPage().toCompletableFuture().join(), matches, batchProcessingTimestamp, selectedVersion, selectedRow);
        } else if (selectedRow != null) {
            final Object ttl = selectedRow.getObject(2);
            final Object writeTime = selectedRow.getObject(3);
            if (ttl == null || Long.parseLong(writeTime.toString()) >= Long.parseLong(batchProcessingTimestamp + MICRO_POSTFIX)) {
                matches.add(deserialize(selectedRow.getString(0)));
            }
        }
    }

    private static boolean hasAllFields(RepeatedMessage repeatedMessage, List<ColumnConfiguration> keys) {
        for (ColumnConfiguration key : keys) {
            if (repeatedMessage.getValue(key.getColumnName()) == null) {
                return false;
            }
        }
        return true;
    }

    private List<List<Pair<RepeatedMessage, Double>>> queryList(List<String> keyList, Long batchProcessingTimestamp, double[] messageCountArr) {
        final long currTime = System.currentTimeMillis();
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        final List<List<Pair<RepeatedMessage, Double>>> matchesList = new ArrayList<>(0);
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) ScyllaQueryGeneratorHolder.getInstance().getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());
        long queryWaitTime;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query Data for table: {}, {}", configurationRow.getEntityStore().getSchemaStoreKey(), qGen.getPsForSelectUpsert().getQuery());
            LOGGER.debug("isReference: {}, EntityStore {}", configurationRow.getEntityStore().isReference(), configurationRow.getEntityStore());
        }
        int counter = 0;
        try {
            keyList.forEach(rowKey -> {
                PreparedStatement psQuery = qGen.getPsForSelectUpsert();
                resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psQuery.bind(configurationRow.getTableName(), rowKey))));
            });

            long futuresStartTime = System.currentTimeMillis();
            extractRepeatedMessagesPairFromResultSet(batchProcessingTimestamp, matchesList, resultSets);
            counter += matchesList.size();
            if (RealtimeDataPublisherCounterManager.INSTANCE != null) {
                RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                        addCounter(CounterType.REPLICATOR, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                                Long.parseLong(resultSets.size() + EMPTY));
                RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                        addCounter(CounterType.TRANSFORMER, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                                Long.parseLong(resultSets.size() + EMPTY));
            }
            queryWaitTime = System.currentTimeMillis() - futuresStartTime;
            messageCountArr[0] = keyList.isEmpty() ? 0 : ((double) queryWaitTime) / keyList.size();
        } catch (Exception ex) {
            LOGGER.error("ERROR_QUERY_DATA_QUERY_LIST:{},{}", tableName, ex.getMessage());
            printDatabaseStats("Query List for " + configurationRow.getTableName());
            throw new RuntimeException(ex);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("In queryList: Input Size = {}, Output = {}, Missing = {}, Time = {}, query wait time = {}:{}",
                    keyList.size(), counter, (keyList.size() - counter), (System.currentTimeMillis() - currTime), queryWaitTime, configurationRow.getTableName());
        }
        return matchesList;
    }

    private void extractRepeatedMessagesPairFromResultSet(Long batchProcessingTimestamp, List<List<Pair<RepeatedMessage, Double>>> matchesList, List<CompletionStage<AsyncResultSet>> resultSets) throws InterruptedException, ExecutionException {
        for (final CompletionStage<AsyncResultSet> resultSet : resultSets) {
            List<Pair<RepeatedMessage, Double>> matches = new ArrayList<>();
            processRowsForRepeatedMessagePair(resultSet.toCompletableFuture().join(), matches, batchProcessingTimestamp, 0L, null);
            matchesList.add(matches);
        }
    }

    private void processRowsForRepeatedMessagePair(final AsyncResultSet rs, final List<Pair<RepeatedMessage, Double>> matches, Long batchProcessingTimestamp, double selectedVersion, Row selectedRow) {
        for (final Row row : rs.currentPage()) {
            final double tsVersion = row.getDouble(1);
            if (tsVersion <= batchProcessingTimestamp && tsVersion > selectedVersion) {
                selectedVersion = tsVersion;
                selectedRow = row;
            }
        }
        if (rs.hasMorePages()) {
            processRowsForRepeatedMessagePair(rs.fetchNextPage().toCompletableFuture().join(), matches, batchProcessingTimestamp, selectedVersion, selectedRow);
        } else if (selectedRow != null) {
            matches.add(new ImmutablePair(deserialize(selectedRow.getString(0)), selectedVersion));
        }
    }

    private boolean isReferenceData() {
        return configurationRow.getEntityStore().isReference();
    }

    public static void printDatabaseStats(final String phase) {
        //prints scylla session stats
    }

    public String serialize(final RepeatedMessage repeatedMessage) {
        return jsonRepeatedMessageFormatter.build(repeatedMessage);
    }

    public RepeatedMessage deserialize(String json) {
        return jsonRepeatedMessageFormatter.parse(json);
    }

    @Override
    public List<KeyColumnDescriptor> getIndexedFks() {
        return new ArrayList<>(indexedFks);
    }

    @Override
    public void updateBulkProcessedFlag(long batchTimestamp, List<BulkRelationInfo> bulkRelationInfos, double[] messageCountArr) {
        ScyllaQueryGeneratorHolder holder = ScyllaQueryGeneratorHolder.getInstance();
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) holder.getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());
        PreparedStatement psQueryForInsertRelationWithTTL = qGen.getPsForInsertRelationWithTTL();
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        for (BulkRelationInfo bulkRelationInfo : bulkRelationInfos) {
            final CompletionStage<AsyncResultSet> asyncResult = completionStage.thenCompose(s -> s.executeAsync(psQueryForInsertRelationWithTTL.bind(bulkRelationInfo.getMainTable(), buildDataBaseRowKey(bulkRelationInfo.getRelationKeyInfo().getSourceKeys().getIds()), bulkRelationInfo.getContextName(), bulkRelationInfo.getRelType(), bulkRelationInfo.getRelationTable(), buildDataBaseRowKey(bulkRelationInfo.getRelationKeyInfo().getRelationKey().getIds()), bulkRelationInfo.getRelationKeyInfo().getTsVersion(), bulkRelationInfo.getMainTable())));
            resultSets.add(asyncResult);
        }
        executeResultSetFutures(resultSets, messageCountArr);
        RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                addCounter(CounterType.REPLICATOR, CounterType.UPSERTWRITECOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                        Long.parseLong(resultSets.size() + EMPTY));
        RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                addCounter(CounterType.TRANSFORMER, CounterType.UPSERTWRITECOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                        Long.parseLong(resultSets.size() + EMPTY));
        PreparedStatement psInsertRelationWithBulkProcessed = qGen.getPsForInsertRelationWithBulkProcessedTrue();
        for (BulkRelationInfo bulkRelationInfo : bulkRelationInfos) {
            resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psInsertRelationWithBulkProcessed.bind(bulkRelationInfo.getMainTable(), buildDataBaseRowKey(bulkRelationInfo.getRelationKeyInfo().getSourceKeys().getIds()), bulkRelationInfo.getContextName(), bulkRelationInfo.getRelType(), bulkRelationInfo.getRelationTable(), buildDataBaseRowKey(bulkRelationInfo.getRelationKeyInfo().getRelationKey().getIds()), bulkRelationInfo.getRelationKeyInfo().getTsVersion(), bulkRelationInfo.getMainTable()))));
        }
        executeResultSetFutures(resultSets, messageCountArr);
    }

    @Override
    public void begin() {
    }

    private static double getExistingTimeStamp(ScyllaQueryGenerator qGen, String mainTableName,
                                               String rowKeyFromExistingMessage, String ctx, String relType,
                                               String relTable, String relationKeyFromExistingMessage,
                                               double existingMessageTsVersion) {
        double existingTimeStamp = existingMessageTsVersion;
        final PreparedStatement psQuery = qGen.getPsForSelectTimestamp();

        final CqlSession session = ScyllaConnection.getSession();
        final ResultSet resultSet = session.execute(psQuery.bind(mainTableName, rowKeyFromExistingMessage, ctx, relType, relTable, existingMessageTsVersion));
        for (final Row row : resultSet.all()) {
            final String relationKey = row.getString(1);
            if (relationKeyFromExistingMessage.equals(relationKey)) {
                existingTimeStamp = row.getDouble(0);
                break;
            }
        }
        return existingTimeStamp;
    }

    private static String getCounterRelation(String relType) {
        return relType.equals(CHILD) ? "PARENT" : CHILD;
    }

    public static class IndexedActions {
        private final List<CompletionStage<AsyncResultSet>> mainDataFutureList = new ArrayList<>();
        private final List<CompletionStage<AsyncResultSet>> relationalDataFutureList = new ArrayList<>();

        public void addMainData(CompletionStage<AsyncResultSet> resultSetFuture) {
            mainDataFutureList.add(resultSetFuture);
        }

        public void addRelationalData(CompletionStage<AsyncResultSet> resultSetFuture) {
            relationalDataFutureList.add(resultSetFuture);
        }

        public List<CompletionStage<AsyncResultSet>> getMainDataFutureList() {
            return mainDataFutureList;
        }

        public List<CompletionStage<AsyncResultSet>> getRelationalDataFutureList() {
            return relationalDataFutureList;
        }
    }

    private static boolean hasAllForeignKeysAndRelationKeys(boolean isMainRelation, RepeatedMessage currentMessage,
                                                            KeyColumnDescriptor foreignKeys, KeyColumnDescriptor relationKeys) {
        if (isMainRelation && !hasAllFields(currentMessage, foreignKeys.getKeys())) {
            return false;
        }
        return isMainRelation || hasAllFields(currentMessage, relationKeys.getKeys());
    }

    private static boolean isCounterRelationUpsertNeeded(boolean isMTMRelation, boolean isDoPropagation) {
        return !isMTMRelation || isDoPropagation;
    }

    private void insertDataIntoDatabaseWithTTL(final CompletionStage<CqlSession> completionStage,
                                               ScyllaQueryGeneratorHolder holder, RepeatedMessage currentMessage,
                                               String rowKey, Long dataBaseTimeStamp,
                                               Double existingMessageTsVersion, final List<CompletionStage<AsyncResultSet>> resultSets, double[] messageCountArr) {
        String currentMessageBytes = serialize(currentMessage);
        ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) holder.getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());
        PreparedStatement psQueryForInsert = qGen.getPsForInsertWithTTL();
        PreparedStatement psQueryForInsertRelation = qGen.getPsForInsertRelationWithTTL();

        resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psQueryForInsert.bind(this.tableName, rowKey,
                dataBaseTimeStamp.doubleValue(), currentMessageBytes, this.tableName))));
        messageCountArr[0]++;

        for (int j = 0; j < indexedFks.size(); j++) {
            final KeyColumnDescriptor foreignKeys = indexedFks.get(j);
            final KeyColumnDescriptor relationKeys = relationKey.get(j);

            final String rowKeyFromMessage = buildDataBaseRowKey(currentMessage, foreignKeys.getKeys());
            final String relationKeyFromMessage = buildDataBaseRowKey(currentMessage, relationKeys.getKeys());

            if (!hasAllForeignKeysAndRelationKeys(isMainRelation.get(j), currentMessage, foreignKeys, relationKeys)) {
                continue;
            }
            final String relTable = relationTables.get(j);
            final String ctx = contextNames.get(j);
            final String mainTableName = mainTableNames.get(j);
            final String relType = relationTypes.get(j);

            //Below insert to mark existing record with TTL.
            double existingTimestampForForeignKeys = getExistingTimeStamp(qGen, mainTableName, rowKeyFromMessage, ctx, relType, relTable, relationKeyFromMessage, existingMessageTsVersion);
            resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psQueryForInsertRelation.bind(mainTableName,
                    rowKeyFromMessage, ctx, relType, relTable, relationKeyFromMessage, existingTimestampForForeignKeys, mainTableName))));
            messageCountArr[1]++;
        }
    }

    @Override
    public double[] deleteRowsUsingTTL(Map<Long, List<DeletedRowInfo>> deletedRowInfosPerBatch) {
        double[] messagesCountArr = new double[3];
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        ScyllaQueryGeneratorHolder holder = ScyllaQueryGeneratorHolder.getInstance();
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        deletedRowInfosPerBatch.forEach((key, value) -> {
            for (DeletedRowInfo deletedRowInfo : value) {
                markRowUsingTTLAndWriteTime(completionStage, holder, resultSets, deletedRowInfo, key, messagesCountArr);
            }
        });
        executeResultSetFutures(resultSets, messagesCountArr);
        RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                addCounter(CounterType.REPLICATOR, CounterType.UPSERTWRITECOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                        Long.parseLong(resultSets.size() + EMPTY));
        RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                addCounter(CounterType.TRANSFORMER, CounterType.UPSERTWRITECOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, EMPTY),
                        Long.parseLong(resultSets.size() + EMPTY));
        return messagesCountArr;
    }

    private void markRowUsingTTLAndWriteTime(final CompletionStage<CqlSession> completionStage, ScyllaQueryGeneratorHolder holder,
                                             final List<CompletionStage<AsyncResultSet>> resultSets, DeletedRowInfo deletedRowInfo,
                                             Long batchTimestamp, double[] messagesCountArr) {
        String currentMessageBytes = serialize(deletedRowInfo.getCurrentMessage());
        ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) holder.getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());
        PreparedStatement psQueryForInsert = qGen.getPsForInsertWithTTLWriteTime();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("psQueryForInsert: {}", psQueryForInsert);
        }
        PreparedStatement psQueryForInsertRelation = qGen.getPsForInsertRelationWithTTLWriteTime();

        resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psQueryForInsert.bind(this.tableName, deletedRowInfo.getRowKey(),
                deletedRowInfo.getDataBaseTimeStamp().doubleValue(), currentMessageBytes, this.tableName, Long.parseLong(batchTimestamp + MICRO_POSTFIX)))));
        messagesCountArr[0]++;
        for (int j = 0; j < indexedFks.size(); j++) {
            final KeyColumnDescriptor foreignKeys = indexedFks.get(j);
            final KeyColumnDescriptor relationKeys = relationKey.get(j);

            final String rowKeyFromMessage = buildDataBaseRowKey(deletedRowInfo.getCurrentMessage(), foreignKeys.getKeys());
            final String relationKeyFromMessage = buildDataBaseRowKey(deletedRowInfo.getCurrentMessage(), relationKeys.getKeys());

            if (!hasAllForeignKeysAndRelationKeys(isMainRelation.get(j), deletedRowInfo.getCurrentMessage(), foreignKeys, relationKeys)) {
                continue;
            }
            final String relTable = relationTables.get(j);
            final String ctx = contextNames.get(j);
            final String mainTableName = mainTableNames.get(j);
            final String relType = relationTypes.get(j);
            //Below insert to mark existing record with TTL.
            double existingTimestampForForeignKeys = getExistingTimeStamp(qGen, mainTableName, rowKeyFromMessage, ctx, relType, relTable, relationKeyFromMessage, deletedRowInfo.getExistingMessageTsVersion());
            resultSets.add(completionStage.thenCompose(s -> s.executeAsync(psQueryForInsertRelation.bind(mainTableName,
                    rowKeyFromMessage, ctx, relType, relTable, relationKeyFromMessage, existingTimestampForForeignKeys,
                    mainTableName, Long.parseLong(batchTimestamp + MICRO_POSTFIX)))));
            messagesCountArr[0]++;
        }
    }

    public List<String> getContextNames() {
        return contextNames;
    }

    public List<String> getMainTableNames() {
        return mainTableNames;
    }

    public List<String> getRelationTables() {
        return relationTables;
    }

    public List<String> getRelationTypes() {
        return relationTypes;
    }

    public List<KeyColumnDescriptor> getRelationKey() {
        return relationKey;
    }

    public List<Boolean> getIsMainRelation() {
        return isMainRelation;
    }

    public List<Boolean> getDoPropagation() {
        return doPropagation;
    }

    public List<String> getCounterTables() {
        return counterTables;
    }

    public List<Boolean> getIsMTM() {
        return isMTM;
    }
}