package com.amdocs.aia.il.common.stores;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.model.configuration.entity.DeletedRowInfo;
import com.amdocs.aia.il.common.publisher.CounterType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for all stores - Random access tables
 */
public interface RandomAccessTable {

    void commit();

    /**
     * Update/insert a single Generated Message to the store
     * @param repeatedMessage
     * @return false if the message failed to be inserted either to main table or one of the FKs tables
     */
    boolean upsert(RepeatedMessage repeatedMessage);

    /**
     * Update/insert multiple Generated Messages to the store
     * @param repeatedMessages
     * @return Set of IDs of the failed messages which failed to insert either to main table or one of the FKs tables
     */
    Collection<Integer> asyncUpsert(List<RepeatedMessage> repeatedMessages);

    /** Method for working with incremental updates in HBASE
     *
     * @param dayLightSaving
     * @param upsertDataList
     * @param batchProcessingTimestamp
     * @param rowKeysPerTableInfoListToBeUpserted
     * @param rowKeysPerTransactionIDList
     * @param counterType
     * @return Set of IDs of the failed messages which failed to insert either to main table or one of the FKs tables
     */
    double[] asyncUpsert(String tableName , List<UpsertData> upsertDataList, List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted, List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving , boolean incrementalUpsert, Map<Long, List<DeletedRowInfo>> deletedRowInfosPerBatch, CounterType counterType, boolean isAuditEnable);

    /** Method for working with incremental updates in HBASE
     *
     * @param dayLightSaving
     * @param repeatedMessages
     * @param batchProcessingTimestamp
     * @param rowKeysPerTransactionIDList
     * @return Set of IDs of the failed messages which failed to insert either to main table or one of the FKs tables
     */
    double[] asyncUpsertForSingleMessage(String tableName , RepeatedMessage repeatedMessages, Long batchProcessingTimestamp, List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving , boolean incrementalUpsert, CounterType counterType);

    /** Method for working with incremental updates in HBASE
     *
     * @param dayLightSaving
     * @param repeatedMessages
     * @param batchProcessingTimestamp
     * @param rowKeysPerTableInfoListToBeUpserted
     * @param rowKeysPerTransactionIDList
     * @return Set of IDs of the failed messages which failed to insert either to main table or one of the FKs tables
     */
    double[] asyncUpsert(String tableName , List<RepeatedMessage> repeatedMessages, Long batchProcessingTimestamp, List<RowKeysPerTransactionID> rowKeysPerTableInfoListToBeUpserted, List<RowKeysPerTransactionID> rowKeysPerTransactionIDList, boolean dayLightSaving , boolean incrementalUpsert, List<DeletedRowInfo> deletedRowInfos, CounterType counterType, boolean isAuditEnable);
    /**
     * Query rows according to their RowId values and return a List of GeneratedMessage results
     * @param indexName - table name to query
     * @param rowsToQuery - row to fetch
     * @param batchProcessingTimestamp - versioned timestamp to retrieve data
     * @return a List of RepeatedMessage results
     * @throws Exception
     */
    Collection<RepeatedMessage> queryData(String indexName, Collection<KeyColumn> rowsToQuery ,Long batchProcessingTimestamp) throws Exception;

    Collection<RepeatedMessage> queryBulkData(String indexName, Collection<KeyColumn> rowsToQuery ,Long batchProcessingTimestamp) throws Exception;

    Collection<RepeatedMessage> queryDataWithTTLAndWriteTimeChecked(String indexName, Collection<KeyColumn> rowsToQuery ,Long batchProcessingTimestamp) throws Exception;

    /**
     * Query the entire row data
     * @param indexName - table to query
     * @return a Collection of GeneratedMessage results
     */
    Collection<RepeatedMessage> queryAllData(String indexName);

    /**
     * Query rows according to their RowId values and return a List of all results RowId
     * @param indexName - table name to query
     * @param rowsToQuery - row to fetch
     * @return a List of RowId of all returned results
     * @throws Exception
     */
    Collection<KeyColumn> queryIds(String indexName, Collection<KeyColumn> rowsToQuery) throws Exception;

    /**
     * Query rows according to their RowId values and return a List of all results RowId
     * @param indexName - table name to query
     * @param rowsToQuery - row to fetch
     * @param batchProcessingTimestamp - versioned timestamp to retrieve data
     * @return a List of RowId of all returned results
     * @throws Exception
     */
    Collection<KeyColumn> queryIds(String indexName, Collection<KeyColumn> rowsToQuery, Long batchProcessingTimestamp) throws Exception;

    /**
     * Check if keys exists in table
     * @param indexName - the index name of the table (i.e. MAIN = main table)
     * @param rowsToQuery - keys ot check
     * @return List of booleans which indicates if keys exists in table
     * @throws Exception
     */
    List<Boolean> checkExists(String indexName, Collection<KeyColumn> rowsToQuery) throws Exception;


    /**
     * Shutdown operation for the store
     */
    void shutdown();

    /**
     * @return the store type
     */
    String getType();

    /**
     * @return the store name
     */
    String getName();

    /**
     * Populates index tables with data from main table
     * @param indexNames
     */
    void buildIndexes(List<String> indexNames);

    boolean isTransient();

    /**
     * Used to register foreign key details
     * @param fkey - list of keys
     */
    public void registerFkIndex(KeyColumnDescriptor fkey);

    public void registerFkIndex(KeyColumnDescriptor fkey, String mainTableName, String contextname, String relationtable, KeyColumnDescriptor relationKey, String relationtype , boolean isMainRelation , boolean doPropagation , String counterTableName , boolean isMTM) ;

    public Map<KeyColumn, List<KeyColumn>> queryListChildData( String mainTable,Collection<KeyColumn> rowKeys,String contextName,String relType , String relationTable, Long batchProcessingTimestamp);

    public List<KeyColumn> queryListChildDataWithTTLAndWriteTimeChecked( String mainTable,Collection<KeyColumn> rowKeys,String contextName,String relType , String relationTable, Long batchProcessingTimestamp);

    public List<RelationKeyInfo> queryListChildDataWithBulkProcessedFlag(String mainTable, Collection<KeyColumn> rowKeys, String contextName, String relType , String relationTable, Long batchProcessingTimestamp);

    public List<KeyColumnDescriptor> getIndexedFks();

    public void updateBulkProcessedFlag(long batchTimestamp, List<BulkRelationInfo> bulkRelationInfo, double[] messageCountArr);

    public double[] deleteRowsUsingTTL(Map<Long, List<DeletedRowInfo>> deletedRowInfosPerBatch);

    public void begin();
}
