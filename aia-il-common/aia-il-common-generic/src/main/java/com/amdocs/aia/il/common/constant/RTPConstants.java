package com.amdocs.aia.il.common.constant;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RTPConstants {

    public static final String TRANSIENT = "INMEMORY";
    public static final ReentrantReadWriteLock rwlock ;

    static {
        rwlock = new ReentrantReadWriteLock();
    }

    public static class RefreshReferenceTableScheduler {

        public static final long NOT_INITIALIZED_REFRESH_VALUE = -1;
        public static final long DEFAULT_INITIALIZATION_REFRESH_VALUE = 0;
        public static final String DATA_PROCESSING_CONTEXTS_PARAM = "dataProcessingContexts";
        public static final String PUBLISHER_CONTEXT_PARAM = "publisherContext";
        public static final String REFERENCE_TABLES_PARAM = "referenceTables";
        public static final String REFRESH_PATH_PARAM = "refreshPathParam";
        public static final String LAST_UPDATED_PATH_PARAM = "lastUpdatedPathParam";
        public static final String POOL_PARAM = "forkJoinPool";
        public static final String REFERENCE_TABLES_TO_USE_PARAM = "referenceTableNamesToUse";
        public static final String IDENTIFIER_OPERATION_CONTEXT_PARAM = "identifierStorageOperation";
        public static final String LOCK_PARAM = "lockParam";
        public static final String QUERY_HANDLER_PARAM = "queryHandlerParam";
        public static final String BACKUP_DIR_PARAM = "backUpDirParam";
        public static final String DB_BACKUP_FILE_PATH_PARAM = "dbBackUpFilePathParam";
    }

    public static final String MAIN_TABLE = "DATA";
    public static final String REF = "REF";
    public static final String JSON = "JSON";
    public static final String SHAREDJSON = "SharedJson";
    public static final String SHAREDPROTOBUF = "SharedProtobuf";
    public static final String ERROR = "Error";
    public static final String TABLE = "Table";
    public static final String CONTEXT = "Context";
    public static final String REFERENTIALINTEGRITY = "ReferentialIntegrity";
    public static final String REFERENTIALINTEGRITYUPSERT = "ReferentialIntegrityUpsert";

    public static final String MARK_DELETED_RECORDS_WITH_TTL_METHOD = "markDeletedRecordsWithTTL";
    public static final String COMPUTE_LEADINGKEYS_METHOD = "computeLeadingKeys(dataChannelName)";
    public static final String UPSERT_MESSAGES_IN_TRX_METHOD = "upsertMessagesInTrx(dataChannelName, dataToBeUpsertedIntoDataStore)";

    public static final String LOAD_AFFECTED_DATA_METHOD = "loadAffectedData(dataChannelName, dataPerTablePerContext)";
    public static final String REFERENTIAL_INTEGRITY_METHOD = "referentialIntegrity(trxResultsData, subBatchesProcessingResult.getTrxPublishingDataList())";
    public static final String  PUBLISH_PROCESS_METHOD= "publishProcess(splits)";
    public static final String KAFKA_CONSUMER_POLLING = "consumerPoll()";
    public static final String THREAD = "Thread";
    public static final String KAFKA_PRODUCER = "Kafka_Producer";
    public static final String KAFKA_PUBLISH_TRANSACTION_FAILED_KEY = "KafkaPublishTransaction";

    public static class MetricsScheduler {

        public static final String SCYLLA_METRICS_PARAM = "scyllaMetricsParam";
        public static final String SESSION_PARAM = "sessionParam";
    }

}
