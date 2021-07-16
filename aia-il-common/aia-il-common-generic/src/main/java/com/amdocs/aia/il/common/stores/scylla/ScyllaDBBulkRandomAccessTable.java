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
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static com.amdocs.aia.il.common.stores.scylla.ScyllaDBRandomAccessTable.printDatabaseStats;

public class ScyllaDBBulkRandomAccessTable implements RandomAccessTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScyllaDBBulkRandomAccessTable.class);

    private static final String OPERATION = "operation";
    private static final String DELETE = "delete";
    private static final String PUBLISHER_STORE = "PublisherStore";

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

    public ScyllaDBBulkRandomAccessTable(ConfigurationRow configurationRow, JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter) {
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
        });

        /* if mergeMode set to false select query to scylla db will be skipped directly it will insert records into db */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing NonMergeMode");
        }
        return upsertResultList(tableName, repeatedMessages, idList, dbTimestampList, counterType, isAuditEnable);
    }

    private double[] upsertResultList(String tableName, List<RepeatedMessage> repeatedMessages,
                                      List<String> rowKeys,
                                      List<Long> databaseTimestampList, CounterType counterType, boolean isAuditEnable) {
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        final ScyllaQueryGeneratorHolder scyllaQueryGeneratorHolder = ScyllaQueryGeneratorHolder.getInstance();
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        //Index wise description - 0) main data count, 1) relational data count, 2) failed message count, 3) filtered message count, 4)Avg query time, 5) Avg merge time 6) Avg Upsert time
        double[] messageCountArr = new double[7];
        final long startTime = System.currentTimeMillis();
        try {
            for (int i = 0; i < repeatedMessages.size(); i++) {
                final RepeatedMessage currentMessage = repeatedMessages.get(i);
                final String rowKey = rowKeys.get(i);
                final Long dataBaseTimeStamp = databaseTimestampList.get(i);
                final String currentMessageOperation = ((EnumValue) currentMessage.getValue(OPERATION)).getValueName();

                    if (DELETE.equalsIgnoreCase(currentMessageOperation)) {
                        insertDataIntoDatabaseWithTTL(completionStage, scyllaQueryGeneratorHolder, currentMessage,
                                rowKey, dataBaseTimeStamp, resultSets, dataBaseTimeStamp.doubleValue(), messageCountArr);
                        if(isAuditEnable) {
                            RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                                    configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), CounterType.RECORDSDELETED, currentMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME),
                                    this.tableName, 1L);
                        }
                    } else {
                        insertDataIntoDatabase(completionStage, scyllaQueryGeneratorHolder, currentMessage, rowKey, dataBaseTimeStamp, resultSets, messageCountArr);
                        if(isAuditEnable) {
                            RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                                    configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), CounterType.RECORDSSTORED, currentMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME),
                                    this.tableName, 1L);
                        }
                    }
            }
            executeResultSetFutures(resultSets, messageCountArr);
            final long totalUpsertTime = System.currentTimeMillis() - startTime;
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Wait time in upserting is {} for table {} ", totalUpsertTime, this.tableName);
            }

            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.REPLICATOR, CounterType.UPSERTWRITECOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), resultSets.size());
            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.TRANSFORMER, CounterType.UPSERTWRITECOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), resultSets.size());
            messageCountArr[3] = 0;
            messageCountArr[5] = 0;
            messageCountArr[6] = (messageCountArr[0] + messageCountArr[1]) != 0 ? totalUpsertTime / (messageCountArr[0] + messageCountArr[1]) : 0;
        } catch (Exception e) {
            printDatabaseStats("async Upsert for " + this.tableName);
            throw e;
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("In Async upsert for table:{}:Input Size:{}:Index Size:{}:Output Size: {} :Missing: {}:Time: {}",
                    this.tableName, repeatedMessages.size(), indexedFks.size(), 0, repeatedMessages.size(), (System.currentTimeMillis() - startTime));
        }
        return messageCountArr;
    }

    @Override
    public double[] asyncUpsert(String tableName, List<UpsertData> upsertDataList,
                                List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted,
                                List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving, boolean incrementalUpsert, Map<Long, List<DeletedRowInfo>> deletedRowInfosPerBatch, CounterType counterType, boolean isAuditEnable) {
        return null;
    }

    @Override
    public double[] asyncUpsertForSingleMessage(String tableName, RepeatedMessage message, Long batchProcessingTimestamp, List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving, boolean incrementalUpsert, CounterType counterType) {
        final Long dbTimestampList;
        KeyColumn ids = new KeyColumn(message, this.configurationRow.getPkColumns());
        if (!rowKeysPerTransactionIDList.isEmpty() && checkIfIdInserted(ids, rowKeysPerTransactionIDList)) {
            dbTimestampList = 0L;
        } else {
            dbTimestampList = batchProcessingTimestamp;
        }
        String idList = buildDataBaseRowKey(ids.getIds());

        /* if mergeMode set to false select query to scylla db will be skipped directly it will insert records into db */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing NonMergeMode");
        }
        return upsertResultListForSingleMessage(tableName, message, idList, dbTimestampList, counterType);
    }

    private double[] upsertResultListForSingleMessage(String tableName, RepeatedMessage repeatedMessage,
                                                      String singleRowKey, Long databaseTimestamp, CounterType counterType) {
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        ScyllaQueryGeneratorHolder scyllaQueryGeneratorHolder = ScyllaQueryGeneratorHolder.getInstance();
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        //Index wise description - 0)main data count , 1) relational data count , 2) failed message count , 3) filtered message count, 4)Avg query time, 5) Avg merge time 6) Avg Upsert time
        double[] messageCountArr = new double[7];
        try {
            String currentMessageOperation = ((EnumValue) repeatedMessage.getValue(OPERATION)).getValueName();
            if (!DELETE.equalsIgnoreCase(currentMessageOperation)) {
                insertDataIntoDatabase(completionStage, scyllaQueryGeneratorHolder, repeatedMessage, singleRowKey, databaseTimestamp, resultSets, messageCountArr);
                RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                        configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""),CounterType.RECORDSSTORED, String.valueOf(repeatedMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME)),
                        this.tableName, 1L);
            } else {
                insertDataIntoDatabaseWithTTL(completionStage, scyllaQueryGeneratorHolder, repeatedMessage, singleRowKey, databaseTimestamp, resultSets, databaseTimestamp.doubleValue(), messageCountArr);
                RealtimeAuditDataPublisherManager.getInstance().getAuditRecordsAccumulator().addCounter(counterType,
                        configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""),CounterType.RECORDSDELETED, String.valueOf(repeatedMessage.getValue(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME)),
                        this.tableName, 1L);
            }
            long startTimeUpsert = System.currentTimeMillis();
            executeResultSetFutures(resultSets, messageCountArr);
            long totalUpsertTime = System.currentTimeMillis() - startTimeUpsert;
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Wait time in upserting is {} for table {} ", totalUpsertTime, this.tableName);
            }

            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.REPLICATOR, CounterType.UPSERTWRITECOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), resultSets.size());
            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.TRANSFORMER, CounterType.UPSERTWRITECOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace("PublisherStore", ""), resultSets.size());
            messageCountArr[3] = 0;
            messageCountArr[5] = 0;
            messageCountArr[6] = (messageCountArr[0] + messageCountArr[1]) != 0 ? totalUpsertTime / (messageCountArr[0] + messageCountArr[1]) : 0;
        } catch (Exception e) {
            printDatabaseStats("async Upsert for " + this.tableName);
            throw e;
        }
        return messageCountArr;
    }

    @Override
    public Collection<RepeatedMessage> queryData(String mainTable, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) {
        return Collections.emptyList();
    }

    @Override
    public Collection<RepeatedMessage> queryBulkData(String mainTable, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) {
        return Collections.emptyList();
    }

    @Override
    public Collection<RepeatedMessage> queryAllData(String mainTable) {
        return Collections.emptyList();
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

    private static void executeResultSetFutures(final List<CompletionStage<AsyncResultSet>> resultSets, final double[] messageCountArr) {
        for (final CompletionStage<AsyncResultSet> resultSet : resultSets) {
            try {
                resultSet.toCompletableFuture().get();
            } catch (final Exception e) {
                // Failed message counter
                messageCountArr[2]++;
                LOGGER.error("Error while executing result sets");
                throw new RuntimeException(e);
            }
        }
    }

    private void insertDataIntoDatabase(final CompletionStage<CqlSession> completionStage, ScyllaQueryGeneratorHolder holder,
                                        RepeatedMessage currentMessage, String rowKey, Long dataBaseTimeStamp,
                                        final List<CompletionStage<AsyncResultSet>> resultSets, double[] messageCountArr) {
        final String currentMessageJson = serialize(currentMessage);
        final ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) holder.getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());
        final PreparedStatement psQueryForInsert = qGen.getPsForInsert();
        final CompletionStage<AsyncResultSet> asyncResult = completionStage.thenCompose(s -> s.executeAsync(psQueryForInsert.bind(this.tableName, rowKey, dataBaseTimeStamp.doubleValue(),
                currentMessageJson, this.tableName, Long.parseLong(dataBaseTimeStamp + "000"))));
        resultSets.add(asyncResult);

        // main data counter
        messageCountArr[0]++;

        handleInsertsForAllForeignKeys(completionStage, qGen, currentMessage, resultSets, dataBaseTimeStamp, messageCountArr);
    }

    private void handleInsertsForAllForeignKeys(final CompletionStage<CqlSession> completionStage,
                                                ScyllaQueryGenerator qGen, RepeatedMessage currentMessage,
                                                final List<CompletionStage<AsyncResultSet>> resultSets,
                                                Long dataBaseTimeStamp, double[] messageCountArr) {
        PreparedStatement psQueryForInsertRelation = qGen.getPsForInsertRelation();
        for (int j = 0; j < indexedFks.size(); j++) {
            KeyColumnDescriptor foreignKeys = indexedFks.get(j);
            KeyColumnDescriptor relationKeys = relationKey.get(j);

            String rowKeyFromMessage = buildDataBaseRowKey(currentMessage, foreignKeys.getKeys());
            String relationKeyFromMessage = buildDataBaseRowKey(currentMessage, relationKeys.getKeys());

            if (!hasAllForeignKeysAndRelationKeys(isMainRelation.get(j), currentMessage, foreignKeys, relationKeys)) {
                continue;
            }
            String relTable = relationTables.get(j);
            String ctx = contextNames.get(j);
            String mainTableName = mainTableNames.get(j);
            String relType = relationTypes.get(j);

            final CompletionStage<AsyncResultSet> asyncResult = completionStage.thenCompose(s -> s.executeAsync(psQueryForInsertRelation.bind(mainTableName, rowKeyFromMessage,
                    ctx, relType, relTable, relationKeyFromMessage, dataBaseTimeStamp.doubleValue(), mainTableName,
                    Long.parseLong(dataBaseTimeStamp + "000"))));
            resultSets.add(asyncResult);
            // Relational data counter
            messageCountArr[1]++;
        }
    }

    @Override
    public Map<KeyColumn, List<KeyColumn>> queryListChildData(String mainTable, Collection<KeyColumn> rowKeys, String contextName, String relType, String relationTable, Long batchProcessingTimestamp) {
        return Collections.emptyMap();
    }

    private static boolean hasAllFields(RepeatedMessage repeatedMessage, List<ColumnConfiguration> keys) {
        for (ColumnConfiguration key : keys) {
            if (repeatedMessage.getValue(key.getColumnName()) == null) {
                return false;
            }
        }
        return true;
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
    public void updateBulkProcessedFlag(long batchTimestamp, List<BulkRelationInfo> bulkRelationInfo, double[] messageCountArr) {

    }

    @Override
    public double[] deleteRowsUsingTTL(Map<Long, List<DeletedRowInfo>> deletedRowInfosPerBatch) {
        return new double[3];
    }

    @Override
    public void begin() {
    }

    @Override
    public List<KeyColumn> queryListChildDataWithTTLAndWriteTimeChecked(String mainTable, Collection<KeyColumn> rowKeys, String contextName,
                                                                        String relType, String relationTable, Long batchProcessingTimestamp) {
        if (rowKeys.isEmpty()) {
            return Collections.emptyList();
        }
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        final CompletionStage<CqlSession> completionStage = ScyllaConnection.getCompletionStage();
        List<KeyColumn> matches = new ArrayList<>();
        ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) ScyllaQueryGeneratorHolder.getInstance().getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());
        try {
            rowKeys.forEach(rowKey -> {
                String strRowKey = buildDataBaseRowKey(rowKey.getIds());
                PreparedStatement ps1 = qGen.getPsForSelect();
                final CompletionStage<AsyncResultSet> asyncResult = completionStage.thenCompose(s -> s.executeAsync(ps1.bind(mainTable, strRowKey, contextName,
                        relType, relationTable)));
                resultSets.add(asyncResult);
            });

            String relationKeys = extractKeyColumnsFromResultSetWithTTLAndWriteTimeChecked(resultSets, matches, batchProcessingTimestamp);
            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.REPLICATOR, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, ""),
                            Long.parseLong(resultSets.size() + ""));
            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.TRANSFORMER, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, ""),
                            Long.parseLong(resultSets.size() + ""));
        } catch (Exception e) {
            LOGGER.error("ERROR_QUERY_DATA_FOR_CHILD" + tableName + e.getMessage());
            printDatabaseStats("Query List ChildData for " + this.tableName);
            throw new RuntimeException(e);
        }
        return matches;
    }

    @Override
    public Collection<RepeatedMessage> queryDataWithTTLAndWriteTimeChecked(String mainTable, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) {
        if (rowsToQuery.isEmpty()) {
            return Collections.emptyList();
        }
        final List<CompletionStage<AsyncResultSet>> resultSets = new ArrayList<>();
        List<RepeatedMessage> matches = new ArrayList<>();
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
                    addCounter(CounterType.REPLICATOR, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, ""),
                            Long.parseLong(resultSets.size() + ""));
            RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                    addCounter(CounterType.TRANSFORMER, CounterType.UPSERTREADCOMPLETED, configurationRow.getEntityStore().getSchemaStoreKey().replace(PUBLISHER_STORE, ""),
                            Long.parseLong(resultSets.size() + ""));
        } catch (Exception e) {
            LOGGER.error("ERROR_QUERY_DATA" + tableName + e.getMessage(), e);
            printDatabaseStats("Query List ChildData for " + this.tableName);
            throw new RuntimeException(e);
        }
        return matches;
    }

    private void extractRepeatedMessagesFromResultSetWithTTLAndWriteTimeChecked(final List<CompletionStage<AsyncResultSet>> resultSets,
                                                                                final List<RepeatedMessage> matches,
                                                                                final Long batchProcessingTimestamp) throws ExecutionException, InterruptedException {
        for (final CompletionStage<AsyncResultSet> resultSet : resultSets) {
            final Iterator<Row> iter = resultSet.toCompletableFuture().get().currentPage().iterator();
            if (iter.hasNext()) {
                final Row row = iter.next();
                final String jsonMsg = row.getString(0);
                final Object ttl = row.getObject(2);
                final Object writetime = row.getObject(3);
                if (ttl == null || Long.parseLong(writetime.toString()) >= Long.parseLong(batchProcessingTimestamp + "000")) {
                    matches.add(deserialize(jsonMsg));
                }
            }
        }
    }

    private static String extractKeyColumnsFromResultSetWithTTLAndWriteTimeChecked(final List<CompletionStage<AsyncResultSet>> resultSets,
                                                                                   List<KeyColumn> matches, Long batchProcessingTimestamp) throws ExecutionException, InterruptedException {
        final StringBuilder allRelationKey = new StringBuilder();
        for (final CompletionStage<AsyncResultSet> resultSet : resultSets) {
            final Iterator<Row> iter = resultSet.toCompletableFuture().get().currentPage().iterator();
            if (iter.hasNext()) {
                final Row row = iter.next();
                final String relationKey = row.getString(0);
                final Object ttl = row.getObject(1);
                final Object writetime = row.getObject(2);
                if (ttl == null || Long.parseLong(writetime.toString()) >= Long.parseLong(batchProcessingTimestamp + "000")) {
                    allRelationKey.append(relationKey).append(':');
                    matches.add(new KeyColumn(relationKey.split(KeyColumn.ROWKEY_SEPARATOR)));
                }
            }
        }
        return allRelationKey.toString();
    }

    @Override
    public List<RelationKeyInfo> queryListChildDataWithBulkProcessedFlag(String mainTable, Collection<KeyColumn> rowKeys, String contextName, String relType, String relationTable, Long batchProcessingTimestamp) {
        return new ArrayList<>();
    }

    private static double getExistingTimeStamp(ScyllaQueryGenerator qGen, String mainTableName,
                                               String rowKeyFromExistingMessage, String ctx, String relType, String relTable,
                                               String relationKeyFromExistingMessage, double existingMessageTsVersion) {
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

    private static boolean hasAllForeignKeysAndRelationKeys(boolean isMainRelation, RepeatedMessage currentMessage, KeyColumnDescriptor foreignKeys, KeyColumnDescriptor relationKeys) {
        if (isMainRelation && !hasAllFields(currentMessage, foreignKeys.getKeys())) {
            return false;
        }
        return isMainRelation || hasAllFields(currentMessage, relationKeys.getKeys());
    }

    private void insertDataIntoDatabaseWithTTL(final CompletionStage<CqlSession> completionStage, ScyllaQueryGeneratorHolder holder,
                                               RepeatedMessage currentMessage, String rowKey, Long dataBaseTimeStamp,
                                               final List<CompletionStage<AsyncResultSet>> resultSets,
                                               double existingMessageTsVersion, double[] messageCountArr) {
        final String currentMessageJson = serialize(currentMessage);
        final ScyllaQueryGenerator qGen = (ScyllaQueryGenerator) holder.getQueryGenerator(configurationRow.getEntityStore().getSchemaStoreKey());
        final PreparedStatement psQueryForInsert = qGen.getPsForInsertWithTTL();
        final PreparedStatement psQueryForInsertRelation = qGen.getPsForInsertRelationWithTTL();

        final CompletionStage<AsyncResultSet> asyncResult = completionStage.thenCompose(s -> s.executeAsync(psQueryForInsert.bind(this.tableName, rowKey, dataBaseTimeStamp.doubleValue(), currentMessageJson, this.tableName)));
        resultSets.add(asyncResult);
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
            final double existingTimestampForForeignKeys = getExistingTimeStamp(qGen, mainTableName,
                    rowKeyFromMessage, ctx, relType, relTable, relationKeyFromMessage, existingMessageTsVersion);
            completionStage.thenCompose(s -> s.executeAsync(psQueryForInsertRelation.bind(mainTableName, rowKeyFromMessage,
                    ctx, relType, relTable, relationKeyFromMessage, existingTimestampForForeignKeys, mainTableName)));
            messageCountArr[1]++;
        }
    }
}