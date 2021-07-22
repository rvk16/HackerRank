package com.amdocs.aia.il.common.query.sqllite;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.constant.RTPConstants;
import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.model.configuration.RTPEntityType;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.query.AbstractSQLQueryHandler;
import com.amdocs.aia.il.common.reference.table.AbstractReferenceTableInfo;
import com.amdocs.aia.il.common.reference.table.ReferenceTableQueryInfo;
import com.amdocs.aia.il.common.reference.table.ReferenceTableScriptInfo;
import com.amdocs.aia.il.common.sqlite.queryResult.IQueryResultSet;
import com.amdocs.aia.il.common.sqlite.queryResult.SQLNonScrollableQueryResultSet;
import com.amdocs.aia.il.common.stores.RandomAccessTable;
import com.amdocs.aia.il.common.utils.RTPUtils;
import com.amdocs.aia.il.common.utils.SharedPathUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * SQLIte implementation for the query handler
 * Created by ORENKAF on 4/10/2016.
 */
public class SQLiteQueryHandler extends AbstractSQLQueryHandler {

    private final static Logger logger = LoggerFactory.getLogger(SQLiteQueryHandler.class);
    protected final static String CONNECTION_URL = "jdbc:sqlite::memory:?journal_mode=OFF";
    protected SQLQueryHandlerConnection sqlConnection;

    public SQLiteQueryHandler(String name, boolean loadReferenceTablesFromDBFile, boolean backupDBToFile, String baseSharedPath, String jobName) {
        super(name, loadReferenceTablesFromDBFile, backupDBToFile, baseSharedPath, jobName);
        this.sqlConnection = new SQLQueryHandlerConnection(createConnection());
    }

    public SQLQueryHandlerConnection getSqlConnection() {
        return sqlConnection;
    }

    @Override
    protected String getAttachQuery(String dbFilePath, String alias) {
        return "ATTACH DATABASE \"" + dbFilePath + "\" AS " + alias;
    }

    protected String getDetachQuery(String alias) {
        return "DETACH DATABASE " + alias;
    }

    @Override
    public Connection createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(CONNECTION_URL); // NOSONAR
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_CREATING_CONNECTION", CONNECTION_URL, e.getMessage()), e);
        }
    }

    @Override
    protected IQueryResultSet createQueryResultSet(PreparedStatement preparedStatement, String queryName) throws SQLException {
        return new SQLNonScrollableQueryResultSet(preparedStatement.executeQuery(), preparedStatement, queryName);
    }

    @Override
    public String getBackupQuery(Path backupFile) {
        return "backup to " + backupFile.toAbsolutePath().toString();
    }

    @Override
    public void initTable(ConfigurationRow tableInfo) {
        this.sqlConnection.initTable(tableInfo);
    }

    @Override
    public void initQuery(String queryName, String query) {
        this.sqlConnection.addStatement(queryName, query);
    }

    @Override
    public void initReferenceTable(AbstractReferenceTableInfo referenceTableInfo) {
        logger.debug("Initializing reference table : {}", referenceTableInfo);
        if (isLoadReferenceTablesFromDBFile()) {
            logger.debug("Loading reference table from DB file");
            this.sqlConnection.registerDBFileMaterializedView(referenceTableInfo.getName(), referenceTableInfo.getTargetEntity());
        } else { //regular load
            //create the reference table connection
            SQLQueryHandlerConnection sqlQueryHandlerConnection = new SQLQueryHandlerConnection(createConnection());
            //init reference data source tables
            referenceTableInfo.getTableInfos().forEach((tableName, tableInfo) -> {
                sqlQueryHandlerConnection.initTable(tableInfo);
            });

            //set the metadata info on the main connection
            try {
                //in case the reference table is from type query, than there is one SQL which can be initialized
                if (referenceTableInfo.getRTPEntityType()==RTPEntityType.REFERENCE_TABLE_QUERY) {
                    initQueryReferenceTable((ReferenceTableQueryInfo) referenceTableInfo, sqlQueryHandlerConnection);
                } else { //RTPEntityType.REFERENCE_TABLE_GROOVY
                    initScriptReferenceTable((ReferenceTableScriptInfo) referenceTableInfo, sqlQueryHandlerConnection);
                }
            } catch (Exception e) {
                throw new RuntimeException(LogMsg.getMessage("ERROR_FAILED_TO_INIT_REFERENCE_TABLE_DATA",
                        referenceTableInfo.getName(), e.getMessage()), e); //NOSONAR
            }
        }
    }

    /**
     * Init the script reference table info
     *
     * @param referenceTableScriptInfo
     * @param sqlQueryHandlerConnection
     */
    private void initScriptReferenceTable(ReferenceTableScriptInfo referenceTableScriptInfo, SQLQueryHandlerConnection sqlQueryHandlerConnection) {
        this.sqlReferenceConnections.put(referenceTableScriptInfo.getName(), sqlQueryHandlerConnection);
        this.sqlConnection.registerScriptMaterializedView(referenceTableScriptInfo.getName(), referenceTableScriptInfo.getTargetEntity());
        referenceTableScriptInfo.getGroovyScriptWrapper().setQueryHandler(this);
    }

    /**
     * Init the query reference table info with it's prepared statement
     *
     * @param referenceTableQueryInfo
     * @param sqlQueryHandlerConnection
     */
    private void initQueryReferenceTable(ReferenceTableQueryInfo referenceTableQueryInfo, SQLQueryHandlerConnection sqlQueryHandlerConnection) {
        //prepare the SQL
        sqlQueryHandlerConnection.addStatement(referenceTableQueryInfo.getName(), referenceTableQueryInfo.getSql());
        this.sqlReferenceConnections.put(referenceTableQueryInfo.getName(), sqlQueryHandlerConnection);
        this.sqlConnection.registerQueryMaterializedView(referenceTableQueryInfo.getName(),
                referenceTableQueryInfo.getTargetEntity());
    }

    @Override
    public IQueryResultSet executeReferenceTableQuery(AbstractReferenceTableInfo referenceTableInfo, String queryName) {
        try {
            PreparedStatement preparedStatement = getReferenceTableStatement(referenceTableInfo, queryName);
            return createQueryResultSet(preparedStatement, queryName);
        } catch (Exception e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_FAILED_TO_LOAD_REFERENCE_TABLE_DATA_USING_QUERY", referenceTableInfo.getName(), e.getMessage()), e); //NOSONAR
        }
    }

    @Override
    public List<Object> executeReferenceTableScript(ReferenceTableScriptInfo referenceTableInfo) {
        logger.debug("executing reference table script");
        try {
            //process the script
            referenceTableInfo.getGroovyScriptWrapper().process();
            return referenceTableInfo.getGroovyScriptWrapper().getResultMessages();
        } catch (Exception e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_FAILED_TO_LOAD_REFERENCE_TABLE_DATA_USING_SCRIPT", referenceTableInfo.getName(), e.getMessage()), e); //NOSONAR
        }
    }

    /**
     * Get the reference table statement
     *
     * @param referenceTableInfo
     * @param queryName          - custom query name
     * @return {@link PreparedStatement}
     * @throws Exception
     */
    protected PreparedStatement getReferenceTableStatement(AbstractReferenceTableInfo referenceTableInfo, String queryName) {
        //get the prepared statement
        SQLQueryHandlerConnection sqlQueryHandlerConnection = this.sqlReferenceConnections.get(referenceTableInfo.getName());
        return sqlQueryHandlerConnection.getStatement(queryName);
    }

    @Override
    public boolean isReferenceQueryInitialized(AbstractReferenceTableInfo referenceTableInfo, String queryName) {
        return this.getReferenceTableStatement(referenceTableInfo, queryName) != null;
    }

    @Override
    public void loadReferenceTableData(AbstractReferenceTableInfo referenceTableInfo) throws Exception {

        logger.debug("Loading reference table data : {}", referenceTableInfo);
        //clean all previous data
        cleanReferenceTableData(referenceTableInfo);
        //get the reference connection
        SQLQueryHandlerConnection sqlQueryHandlerConnection = this.sqlReferenceConnections.get(referenceTableInfo.getName());
        //load reference data from table
        for (Map.Entry<String, ConfigurationRow> entry : referenceTableInfo.getTableInfos().entrySet()) {
            RandomAccessTable table = referenceTableInfo.getAccessTableInfos().get(entry.getKey());
            logger.debug("Loading data for :{}", entry.getKey());
            sqlQueryHandlerConnection.setQueryData(entry.getKey(), table.queryAllData(entry.getKey()));
        }
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
            long numberOfRowsInserted = this.sqlConnection.setScriptMaterializedViewData(referenceTableInfo.getName(), messages);
            //clean the reference table source data
            cleanReferenceTableData(referenceTableInfo);
            return numberOfRowsInserted;
        } catch (Exception e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_FAILED_TO_REFRESH_REFERENCE_TABLE_DATA_WITH_SCRIPT", referenceTableInfo.getName(), e.getMessage()), e); //NOSONAR
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
            //copy the data from the DB file
            return this.sqlConnection.setDBFileMaterializedViewData(referenceTableInfo.getName());
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
            long numberOfRowsInserted= this.sqlConnection.setQueryMaterializedViewData(referenceTableQueryInfo.getName(), queryResultSet);

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

    @Override
    public void cleanReferenceTableData(AbstractReferenceTableInfo referenceTableInfo) throws Exception {
        logger.debug("Cleaning the previous data for reference table :{}" , referenceTableInfo.getName());
        //get the reference connection
        SQLQueryHandlerConnection sqlQueryHandlerConnection = this.sqlReferenceConnections.get(referenceTableInfo.getName());
        //clean all previous data
        sqlQueryHandlerConnection.clean();
    }

    @Override
    public void clean() throws Exception {
        this.sqlConnection.clean();
    }

    @Override
    public void setQueryData(String tableName, Collection<RepeatedMessage> messages) throws Exception {
        this.sqlConnection.setQueryData(tableName, messages);
    }

    @Override
    public IQueryResultSet executeQuery(String queryName) throws Exception {
        try {
            RTPConstants.rwlock.readLock().lock();
            return createQueryResultSet(this.sqlConnection.getStatement(queryName), queryName);
        } catch (SQLException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_EXECUTING_QUERY", queryName, e.getMessage()), e);
        } finally {
            RTPConstants.rwlock.readLock().unlock();
        }
    }

    @Override
    public boolean isQueryInitialized(String queryName) {
        return this.sqlConnection.getStatement(queryName) != null;
    }

    /**
     * Execute update query with regular statement using main connection
     *
     * @param query - query to update
     * @return same as {#link Statement#executeUpdate(String)}
     */
    public int executeUpdate(String query) {
        return this.executeUpdate(query, this.sqlConnection);
    }


    @Override
    public void attachReferenceDump(String referenceDBFilePath) {
        try {
            logger.debug("Detaching reference DB query");
            //try to detach the DB first in case attached
            this.executeUpdate(getDetachQuery(REF_DB_ALIAS));
        } catch (Exception e) {
            //detach failed, probably cause this DB is not attached, nothing to do ...
        }
        logger.debug("Attaching reference DB query");
        this.executeUpdate(getAttachQuery(referenceDBFilePath, REF_DB_ALIAS));
    }

    /**
     * Execute update query with regular statement
     *
     * @param query                     - query to update
     * @param sqlQueryHandlerConnection - connection to use
     * @return same as {#link Statement#executeUpdate(String)}
     */
    private int executeUpdate(String query, SQLQueryHandlerConnection sqlQueryHandlerConnection) {
        logger.debug("Executing update query :{}", query);
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
     * Backup the in memory DB to a file the main connection
     */
    @Override
    public Path backup() {
        Path backupFile = createBackupFile("");
        this.executeUpdate(getBackupQuery(backupFile));
        logger.debug("Created a backup file : {}", backupFile.getFileName());
        return backupFile;
    }

    /**
     * Backup the in memory DB to a file reference connection
     */
    public void backup(AbstractReferenceTableInfo referenceTableInfo) {
        logger.debug("Creating a backup in memory DB for reference table :{}", referenceTableInfo);
        SQLQueryHandlerConnection sqlQueryHandlerConnection = this.sqlReferenceConnections.get(referenceTableInfo.getName());
        this.executeUpdate(getBackupQuery(createBackupFile(referenceTableInfo.getName())), sqlQueryHandlerConnection);
    }

    /**
     * Create the file for backup DB
     *
     * @param connectionName - name of the connection to use for the file name
     * @return backup file
     */
    private Path createBackupFile(String connectionName) {
        StringBuilder fileName = getBackupFileName(connectionName);
        String backupDir = SharedPathUtils.getBackUpDirectory(this.baseSharedPath);
        if (logger.isDebugEnabled()) {
            logger.debug("Creating a file for backup DB with file name : {} at location : {}", fileName, backupDir);
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
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
        return pathToBackup;
    }

    /**
     * @param connectionName
     * @return the backup file name to create
     */
    protected StringBuilder getBackupFileName(String connectionName) {
        String timeOfFile = DATE_TIME_FORMATTER.print(System.currentTimeMillis());
        StringBuilder fileName = new StringBuilder(this.jobName + '_' + this.name);
        if (!StringUtils.isEmpty(connectionName)) {
            fileName.append("_").append(connectionName);
        }
        fileName.append("_").append(timeOfFile).append(".db");
        return fileName;
    }

    @Override
    public void close() {
        this.sqlConnection.close();
        this.sqlReferenceConnections.values().forEach(SQLQueryHandlerConnection::close);
    }

}
