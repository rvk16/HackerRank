package com.amdocs.aia.il.common.query.sqllite;

import com.amdocs.aia.il.common.constant.RTPConstants;
import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.model.configuration.entity.DataProcessingContext;
import com.amdocs.aia.il.common.model.configuration.entity.PublishingQuery;
import com.amdocs.aia.il.common.reference.table.AbstractReferenceTableInfo;
import com.amdocs.aia.il.common.reference.table.ReferenceTableQueryInfo;
import com.amdocs.aia.il.common.reference.table.ReferenceTableScriptInfo;
import com.amdocs.aia.il.common.sqlite.groovyResult.GroovyQueryResultRow;
import com.amdocs.aia.il.common.sqlite.queryResult.IQueryResultSet;
import com.amdocs.aia.il.common.sqlite.sql.*;
import com.amdocs.aia.il.common.utils.RTPUtils;
import com.amdocs.aia.il.common.utils.SharedPathUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * SQLIte implementation for the query handler for non conflicting sub batches processing.
 */

public class SQLiteQueryHandlerForConflicting extends SQLiteQueryHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(SQLiteQueryHandlerForConflicting.class);

    protected Map<String, SQLQueryHandlerConnection> sqlQueryHandlerConnectionVsThread = new ConcurrentHashMap<>();
    protected Map<String, List<Map<String, Object>>> cachedQueryDataMap;

    public SQLiteQueryHandlerForConflicting(String name, boolean loadReferenceTablesFromDBFile, boolean backupDBToFile, String baseSharedPath, String jobName) {
        super(name, loadReferenceTablesFromDBFile, backupDBToFile, baseSharedPath, jobName);
        this.sqlQueryHandlerConnectionVsThread = new ConcurrentHashMap<>();
        this.cachedQueryDataMap = new ConcurrentHashMap<>();
    }

    public Map<String, SQLQueryHandlerConnection> getSqlQueryHandlerConnectionVsThread() {
        return sqlQueryHandlerConnectionVsThread;
    }

    public Map<String, List<Map<String, Object>>> getCachedQueryDataMap() {
        return cachedQueryDataMap;
    }

    public void close(String threadName) {
        try {
            SQLQueryHandlerConnection connectionForThread = getSqlQueryHandlerConnectionVsThread().get(threadName);
            //connectionForThread.clean();may not be necessary to call clean as we are closing connection anyhow
            connectionForThread.close();
            getSqlQueryHandlerConnectionVsThread().remove(threadName);
        } catch (Exception e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_FAILED_TO_CLOSE_SQLITE_QUERY_HANDLER", threadName, e.getMessage()), e);
        }
    }

    public void setSqlQueryHandlerConnectionVsThread(HashMap<String, SQLQueryHandlerConnection> sqlQueryHandlerConnectionVsThread) {
        this.sqlQueryHandlerConnectionVsThread = sqlQueryHandlerConnectionVsThread;
    }

    @Override
    public long refreshReferenceTable(ReferenceTableScriptInfo referenceTableInfo, Lock lock) {
        boolean isLocked = false;
        try {
            //reload reference table source data
            this.loadReferenceTableData(referenceTableInfo);

            //backup the reference table DB in case specified
            if (this.backupDBToFile) {
                this.backup(referenceTableInfo);
            }

            //execute the script
            List<Object> messages = this.executeReferenceTableScript(referenceTableInfo);

            //lock before refreshing the data
            if (lock != null) {
                lock.lock();
                isLocked = true;
            }
            //add the result of the reference table query to the main connection
            long numberOfRowsInserted = 0l;
            cachedQueryDataMap.remove(referenceTableInfo.getName());//clearing cached data map just before next ref data is loaded.
            cacheQueryGroovyData(messages, referenceTableInfo.getName());
            for (Map.Entry<String, SQLQueryHandlerConnection> entry : this.sqlQueryHandlerConnectionVsThread.entrySet()) {
                entry.getValue().getMaterializedViewTables().get(referenceTableInfo.getName()).clean();
                numberOfRowsInserted += ((GroovyMaterializedViewTable) entry.getValue().getMaterializedViewTables().get(referenceTableInfo.getName())).setQueryData(messages);
            }
            //clean the reference table source data
            cleanReferenceTableData(referenceTableInfo);
            return numberOfRowsInserted;
        } catch (Exception e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_FAILED_TO_REFRESH_REFERENCE_TABLE_DATA_WITH_SCRIPT", referenceTableInfo.getName(), e.getMessage()), e);
        } finally {
            //unlock in any case
            if (lock != null && isLocked) {
                lock.unlock();
            }
        }
    }

    @Override
    public long refreshReferenceTableFromDBFile(AbstractReferenceTableInfo referenceTableInfo, Lock lock) {
        boolean isLocked = false;
        try {
            if (this.backupDBToFile) {
                this.backup(referenceTableInfo);
            }

            if (lock != null) {
                lock.lock();
                isLocked = true;
            }

            long numberOfRowsInserted = 0l;
            for (Map.Entry<String, SQLQueryHandlerConnection> entry : this.sqlQueryHandlerConnectionVsThread.entrySet()) {
                entry.getValue().getMaterializedViewTables().get(referenceTableInfo.getName()).clean();
                numberOfRowsInserted += ((DBFileMaterializedViewTable) entry.getValue().getMaterializedViewTables().get(referenceTableInfo.getName())).loadDataFromDBFile();
            }

            //copy the data from the DB file
            return numberOfRowsInserted;
        } catch (Exception e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_FAILED_TO_REFRESH_REFERENCE_TABLE_DATA_FROM_DBFILE", referenceTableInfo.getName(), e.getMessage()), e);
        } finally {
            //unlock in any case
            if (lock != null && isLocked) {
                lock.unlock();
            }
        }
    }

    @Override
    public long refreshReferenceTable(ReferenceTableQueryInfo referenceTableQueryInfo, Lock lock) {
        boolean isLocked = false;
        try {
            //reload reference table source data
            this.loadReferenceTableData(referenceTableQueryInfo);

            if (this.backupDBToFile) {
                this.backup(referenceTableQueryInfo);
            }

            //execute the query
            IQueryResultSet queryResultSet = this.executeReferenceTableQuery(referenceTableQueryInfo);

            //lock before refreshing the data
            if (lock != null) {
                lock.lock();
                isLocked = true;
            }

            //add the result of the reference table query to the main connection
            long numberOfRowsInserted = 0l;
            cachedQueryDataMap.remove(referenceTableQueryInfo.getName());//clearing cached data map just before next ref data is loaded.
            cacheQuerySQLData(queryResultSet, referenceTableQueryInfo.getName());
            for (Map.Entry<String, SQLQueryHandlerConnection> entry : this.sqlQueryHandlerConnectionVsThread.entrySet()) {
                entry.getValue().getMaterializedViewTables().get(referenceTableQueryInfo.getName()).clean();
                numberOfRowsInserted += queryDataForMaterializedTables(referenceTableQueryInfo, queryResultSet, entry.getValue());
            }
            //clean the reference table source data
            cleanReferenceTableData(referenceTableQueryInfo);
            return numberOfRowsInserted;
        } catch (Exception e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_FAILED_TO_REFRESH_REFERENCE_TABLE_DATA_USING_QUERY", referenceTableQueryInfo.getName(), e.getMessage()), e);
        } finally {
            //unlock in any case
            if (lock != null && isLocked) {
                lock.unlock();
            }
        }
    }

    private Long queryDataForMaterializedTables(ReferenceTableQueryInfo referenceTableQueryInfo, IQueryResultSet queryResultSet, SQLQueryHandlerConnection connHandler) throws Exception {
        return ((SQLMaterializedViewTable) connHandler.getMaterializedViewTables().get(referenceTableQueryInfo.getName())).setQueryData(queryResultSet);
    }

    public IQueryResultSet executeQuery(String queryName, SQLQueryHandlerConnection sqlQueryHandlerConnection) throws Exception {
        try {
            LOGGER.debug("********************************** Trying to acquire read lock in SQLiteQueryHandlerForConflicting class ********************************** {}", DateTime.now());
            RTPConstants.rwlock.readLock().lock();
            LOGGER.debug("********************************** Read lock acquired in SQLiteQueryHandlerForConflicting class ********************************** {}", DateTime.now());
            return createQueryResultSet(sqlQueryHandlerConnection.getStatement(queryName), queryName);
        } catch (SQLException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_EXECUTING_QUERY", queryName, e.getMessage()), e);
        } finally {
            LOGGER.debug("********************************** Releasing the read lock SQLiteQueryHandlerForConflicting class ********************************** {}", DateTime.now());
            RTPConstants.rwlock.readLock().unlock();
        }
    }


    /**
     * Execute the delete query
     *
     * @param sqlQueryHandlerConnection
     * @param queryName
     * @return ResultSet
     */
    public IQueryResultSet executeDeletedEntitiesQuery(String queryName, SQLQueryHandlerConnection sqlQueryHandlerConnection) throws Exception {
        return this.executeQuery(DELETED_ENTITIES_QUERY_PREFIX + queryName, sqlQueryHandlerConnection);
    }

    /**
     * Execute update query with regular statement
     *
     * @param query                     - query to update
     * @param sqlQueryHandlerConnection - connection to use
     * @return same as {#link Statement#executeUpdate(String)}
     */
    private int executeUpdateForConflicting(String query, SQLQueryHandlerConnection sqlQueryHandlerConnection) {
        LOGGER.debug("Executing update query :{}", query);
        try {
            if (!StringUtils.isEmpty(query)) {
                try (Statement statement = sqlQueryHandlerConnection.createStatement()) {
                    return statement.executeUpdate(query);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_EXECUTING_QUERY_WHILE_UPDATING", query, e.getMessage()), e);
        }
        return 0;
    }

    /**
     * Backup the in memory DB to a file with the main connection of Sub batch
     *
     * @param sqlQueryHandlerConnection
     * @return
     */
    public Path backup(SQLQueryHandlerConnection sqlQueryHandlerConnection, long batchProcessingTime) {
        Path backupFile = createBackupFileForConflicting(String.valueOf(batchProcessingTime));
        executeUpdateForConflicting(getBackupQuery(backupFile), sqlQueryHandlerConnection);
        LOGGER.info("Created a backup file : {}", backupFile.getFileName());
        return backupFile;
    }

    /**
     * Create the file for backup DB
     *
     * @param connectionName - name of the connection to use for the file name
     * @return backup file
     */
    private Path createBackupFileForConflicting(String connectionName) {
        StringBuilder fileName = getBackupFileName(connectionName);
        String backupDir = SharedPathUtils.getBackUpDirectory(this.baseSharedPath);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating a file for backup DB with file name : {} at location : {}", fileName, backupDir);
        }
        Path pathToBackup = Paths.get(backupDir, fileName.toString());
        try {
            //check first if directory exists
            if (Files.notExists(Paths.get(backupDir))) {
                Files.createDirectories(Paths.get(backupDir));
            }
            //check if file exists, if not create it
            if (Files.notExists(pathToBackup)) {
                Files.createFile(pathToBackup);
            }
        } catch (IOException e) {
            String errorMsg = LogMsg.getMessage("ERROR_CREATING_BACKUP_FILE", fileName, RTPUtils.getHostName(), RTPUtils.getUserName(), e.getMessage());
            LOGGER.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage("INFO_BACKING_DB_TO_FILE", this.name, pathToBackup.toAbsolutePath().toString(), RTPUtils.getHostName()));
        }
        return pathToBackup;
    }

    @Override
    public void close() {
        this.sqlQueryHandlerConnectionVsThread.forEach(
                (thread, sqlConnHandler) -> {
                    try {
                        sqlConnHandler.getConn().close();
                    } catch (SQLException e) {
                        LOGGER.error("SQL error", e);
                    }
                }
        );
    }

    /**
     * create SQLQueryConnectionHandler per sub batch.
     *
     * @throws SQLException
     */
    public void createSQLQueryConnectionHandler(DataProcessingContext dataProcessingContext, String threadName) throws SQLException {
        SQLiteQueryHandlerForConflicting queryHandler = (SQLiteQueryHandlerForConflicting) dataProcessingContext.getQueryHandler();
        queryHandler.getSqlConnection().getConn().close();
        Connection conn = queryHandler.createConnection();

        //tables
        Map<String, SQLTable> tables = new HashMap<>();
        dataProcessingContext.getRelations().forEach((relName, relation) -> {
            SQLTable sqlTable = new SQLTable(conn, relation.getConfigurationRow());
            tables.put(relation.getTableInfo(), sqlTable);
        });

        //ref tables
        Map<String, AbstractMaterializedViewTable> materializedViewTables = new HashMap<>();
        getMaterializedViewTables(dataProcessingContext, conn, materializedViewTables);

        //statements
        Map<String, PreparedStatement> statements = new HashMap<>();
        dataProcessingContext.getDataPublishingQueries().forEach(dataPublishingQuery -> {
            getStatementsForConnectionMap(conn, statements, dataPublishingQuery);
            getDeletedStatementsForConnectionMap(conn, statements, dataPublishingQuery);
        });

        SQLQueryHandlerConnection sqlQueryHandlerConnection = new SQLQueryHandlerConnection(conn, tables, materializedViewTables, statements);
        for (Map.Entry<String, List<Map<String, Object>>> entry : cachedQueryDataMap.entrySet()) {
            List<Map<String, Object>> data = entry.getValue();
            try {
                if (sqlQueryHandlerConnection.getMaterializedViewTables().get(entry.getKey()) != null) {
                    LOGGER.info("Refreshed data for table : {}", entry.getKey());
                    (sqlQueryHandlerConnection.getMaterializedViewTables().get(entry.getKey())).setQueryWithCachedData(data);
                }
            } catch (Exception e) {
                LOGGER.error("SQL error", e);
            }
        }
        ((SQLiteQueryHandlerForConflicting) dataProcessingContext.getQueryHandler()).getSqlQueryHandlerConnectionVsThread().put(threadName, sqlQueryHandlerConnection);
    }

    private static void getMaterializedViewTables(DataProcessingContext dataProcessingContext, Connection conn,
                                                  Map<String, AbstractMaterializedViewTable> materializedViewTables) {
        dataProcessingContext.getReferenceTables().forEach((refTableName, refTableInfos) -> {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Registering query materialized view for sub batch.");
            }
            try {
                //create the materialized view table using the result set metadata
                materializedViewTables.put(refTableName, new SQLMaterializedViewTable(conn, refTableName, refTableInfos.getTargetEntity()));
            } catch (Exception e) {
                throw new RuntimeException(LogMsg.getMessage("ERROR_INITIALIZING_TABLE_FOR_SUBBATCH", refTableName, e.getMessage()), e);
            }
        });
    }

    private static void getDeletedStatementsForConnectionMap(Connection conn, Map<String, PreparedStatement> statements, PublishingQuery dataPublishingQuery) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding a prepare statement for per Sub Batch connection : {}, query : {}",
                    DELETED_ENTITIES_QUERY_PREFIX + dataPublishingQuery.getName(), dataPublishingQuery.getQuery());
        }
        try {
            //init the deleted entities query
            if (!StringUtils.isEmpty(dataPublishingQuery.getDeletedEntitiesQuery())) {
                //in case a specific query for deleted entities is defined, use it
                statements.put(DELETED_ENTITIES_QUERY_PREFIX + dataPublishingQuery.getName(), conn.prepareStatement(dataPublishingQuery.getDeletedEntitiesQuery()));
            } else {
                //in case no query for deleted entities is defined, use the regular query for the deleted entities
                statements.put(DELETED_ENTITIES_QUERY_PREFIX + dataPublishingQuery.getName(), conn.prepareStatement(dataPublishingQuery.getQuery()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_CREATING_STATEMENT_FOR_SUB_BATCH", DELETED_ENTITIES_QUERY_PREFIX + dataPublishingQuery.getName(), e.getMessage()), e);
        }
    }

    private static void getStatementsForConnectionMap(Connection conn, Map<String, PreparedStatement> statements, PublishingQuery dataPublishingQuery) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding a prepare statement for per Sub Batch connection : {}, query : {}",
                    dataPublishingQuery.getName(), dataPublishingQuery.getQuery());
        }
        try {
            PreparedStatement preparedStatement = (conn.prepareStatement(dataPublishingQuery.getQuery()));
            statements.put(dataPublishingQuery.getName(), preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_CREATING_STATEMENT_FOR_SUB_BATCH", dataPublishingQuery.getName(), e.getMessage()), e);
        }
    }

    public void cacheQuerySQLData(IQueryResultSet rs, String referenceTableName) throws Exception {
        while (rs.next()) {
            List<Map<String, Object>> resultSetValueList = cachedQueryDataMap.get(referenceTableName);
            if (resultSetValueList == null) {
                resultSetValueList = new ArrayList<>();
                cachedQueryDataMap.put(referenceTableName, resultSetValueList);
            }
            resultSetValueList.add(rs.getAllValues());
            cachedQueryDataMap.put(referenceTableName, resultSetValueList);
        }
    }

    public void cacheQueryGroovyData(List<Object> dataList, String referenceTableName) {
        final boolean isGroovyObject = (dataList.iterator().next() instanceof GroovyQueryResultRow);
        for (Object rowDataObj : dataList) {
            Map<String, Object> rowData;
            if (isGroovyObject) {
                rowData = ((GroovyQueryResultRow) rowDataObj).asMap();
            } else {
                rowData = (Map<String, Object>) rowDataObj;
            }

            List<Map<String, Object>> resultSetValueList = cachedQueryDataMap.get(referenceTableName);
            if (resultSetValueList == null) {
                resultSetValueList = new ArrayList<>();
                cachedQueryDataMap.put(referenceTableName, resultSetValueList);
            }
            resultSetValueList.add(rowData);
            cachedQueryDataMap.put(referenceTableName, resultSetValueList);
        }
    }
}