package com.amdocs.aia.il.common.query;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.model.configuration.RTPEntityType;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.reference.table.AbstractReferenceTableInfo;
import com.amdocs.aia.il.common.reference.table.ReferenceTableQueryInfo;
import com.amdocs.aia.il.common.reference.table.ReferenceTableScriptInfo;
import com.amdocs.aia.il.common.sqlite.queryResult.IQueryResultSet;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Implementation of the query handler which runs the publisher SQL
 * Created by ORENKAF on 4/10/2016.
 */
public interface IQueryHandler {

    String DELETED_ENTITIES_QUERY_PREFIX = "delete_";

    /**
     * Init related Table info data
     * @param tableInfo
     */
    public void initTable(ConfigurationRow tableInfo);

    /**
     * Init the statement of a query
     * @param queryName
     * @param query
     */
    public void initQuery(String queryName, String query);

    /**
     * Init a custom query statement of a reference table
     * @param referenceTableInfo
     * @param queryName
     * @param query
     */
    public void initReferenceTableQuery(AbstractReferenceTableInfo referenceTableInfo, String queryName, String query);

    /**
     * Attach a reference dump to the main connection
     * @param referenceDBFilePath
     */
    public void attachReferenceDump(String referenceDBFilePath);

    /**
     * Init the statement of the delete query
     * @param queryName
     * @param query
     */
    default void initDeletedEntitiesQuery(String queryName, String query) {
        this.initQuery(DELETED_ENTITIES_QUERY_PREFIX + queryName, query);
    }

    /**
     * @param queryName
     * @return indication if query is initialized (has prepared statement ready)
     */
    public boolean isQueryInitialized(String queryName);

    /**
     * @param queryName
     * @return indication if delete query is initialized (has prepared statement ready)
     */
    default boolean isDeletedEntitiesQueryInitialized(String queryName) {
        return this.isQueryInitialized(DELETED_ENTITIES_QUERY_PREFIX + queryName);
    }

    /**
     * @param queryName
     * @return indication if query on the reference table is initialized (has prepared statement ready)
     */
    public boolean isReferenceQueryInitialized(AbstractReferenceTableInfo referenceTableInfo, String queryName);

    /**
     * Init the statement of a reference table info
     * @param referenceTableInfo
     */
    public void initReferenceTable(AbstractReferenceTableInfo referenceTableInfo);

    public boolean isLoadReferenceTablesFromDBFile();

    /**
     * Refresh reference table info data with lock - load the data of inner tables from store,
     * execute query or script and refresh memory
     * @param referenceTableInfo - query or script reference table
     * @param lock - lock while refreshing
     * @return number of rows loaded/refreshed
     */
    default long refreshReferenceTable(AbstractReferenceTableInfo referenceTableInfo, Lock lock) {
        if(isLoadReferenceTablesFromDBFile()) {
            return this.refreshReferenceTableFromDBFile(referenceTableInfo, lock);
        } else {
            if(referenceTableInfo.getRTPEntityType().equals(RTPEntityType.REFERENCE_TABLE_QUERY)) {
                return this.refreshReferenceTable((ReferenceTableQueryInfo)referenceTableInfo, lock);
            } else {
                return this.refreshReferenceTable((ReferenceTableScriptInfo)referenceTableInfo, lock);
            }
        }
    }

    /**
     * Refresh reference table info data with lock - load the data from DB file
     * @param referenceTableInfo - query or script reference table
     * @param lock - lock while refreshing
     * @return number of rows loaded/refreshed
     */
    public long refreshReferenceTableFromDBFile(AbstractReferenceTableInfo referenceTableInfo, Lock lock);

    /**
     * Refresh reference table info data with lock - load the data of inner tables from store,
     * execute query and refresh memory
     * @param referenceTableQueryInfo - query reference table
     * @param lock - lock while refreshing
     * @return number of rows loaded/refreshed
     */
    public long refreshReferenceTable(ReferenceTableQueryInfo referenceTableQueryInfo, Lock lock);

    /**
     * Refresh reference table info data with lock - load the data of inner tables from store,
     * execute script and refresh memory
     * @param referenceTableInfo - script reference table
     * @param lock - lock while refreshing
     * @return number of rows loaded/refreshed
     */
    public long refreshReferenceTable(ReferenceTableScriptInfo referenceTableInfo, Lock lock);

    /**
     * Load the inner tables of the reference table
     * @param referenceTableInfo
     * @throws Exception
     */
    public void loadReferenceTableData(AbstractReferenceTableInfo referenceTableInfo) throws Exception;

    /**
     * Execute the reference table query
     * @param referenceTableQueryInfo
     * @return {{@link IQueryResultSet}
     */
    default IQueryResultSet executeReferenceTableQuery(ReferenceTableQueryInfo referenceTableQueryInfo) {
        return this.executeReferenceTableQuery(referenceTableQueryInfo, referenceTableQueryInfo.getName());
    }

    /**
     * Execute the reference table custom query
     * @param referenceTableInfo
     * @return {{@link IQueryResultSet}
     */
    public IQueryResultSet executeReferenceTableQuery(AbstractReferenceTableInfo referenceTableInfo, String queryName);

    /**
     * Execute the reference table script
     * @param referenceTableInfo
     */
    public List<Object> executeReferenceTableScript(ReferenceTableScriptInfo referenceTableInfo);

    /**
     * Clean reference table
     * @param referenceTableInfo - reference table to clean
     */
    public void cleanReferenceTableData(AbstractReferenceTableInfo referenceTableInfo) throws Exception;

    /**
     * Clean tables before new transaction
     */
    public void clean() throws Exception;

    /**
     * Set the data that will be used for the query
     * @param tableName
     * @param messages
     */
    //todo : Change the type of message that will be retrieved from DB under loadAffectedData
    public void setQueryData(String tableName, Collection<RepeatedMessage> messages) throws Exception;

    /**
     * Execute the query
     * @param queryName
     * @return {{@link IQueryResultSet}
     */
    public IQueryResultSet executeQuery(String queryName) throws Exception;

    /**
     * Execute the delete query
     * @param queryName
     * @return ResultSet
     */
    default IQueryResultSet executeDeletedEntitiesQuery(String queryName) throws Exception {
        return this.executeQuery(DELETED_ENTITIES_QUERY_PREFIX + queryName);
    }

    /**
     * Option to backup the in memory data to file
     * @return {@link Path} of the backup file
     */
    public Path backup();

    /**
     * Option to backup the in memory data of reference table to file
     * @return {@link Path} of the backup file
     */
    public void backup(AbstractReferenceTableInfo referenceTableInfo);

    /**
     * Close resources
     */
    public void close();

}
