package com.amdocs.aia.il.common.query.sqllite;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.sqlite.queryResult.IQueryResultSet;
import com.amdocs.aia.il.common.sqlite.sql.*;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SQLQueryHandlerConnection {

    private final static Logger LOGGER = LoggerFactory.getLogger(SQLQueryHandlerConnection.class);
    private Connection conn;
    private Map<String, SQLTable> tables = new HashMap<>();
    private Map<String, AbstractMaterializedViewTable> materializedViewTables = new HashMap<>();
    private Map<String, PreparedStatement> statements = new HashMap<>();

    public SQLQueryHandlerConnection(Connection conn) {
        this.conn = conn;
    }

    public Connection getConn() {
        return conn;
    }

    public Map<String, SQLTable> getTables() {
        return tables;
    }

    public Map<String, AbstractMaterializedViewTable> getMaterializedViewTables() {
        return materializedViewTables;
    }

    public Map<String, PreparedStatement> getStatements() {
        return statements;
    }

    public SQLQueryHandlerConnection(Connection conn, Map<String, SQLTable> tables, Map<String, AbstractMaterializedViewTable> materializedViewTables, Map<String, PreparedStatement> statements) {
        this.conn = conn;
        this.tables = tables;
        this.materializedViewTables = materializedViewTables;
        this.statements = statements;
    }

    /**
     * Init a regular table: create the table, create the insert and delete statements
     *
     * @param tableInfo
     */
    public void initTable(ConfigurationRow tableInfo) {
        this.tables.put(tableInfo.getTableName(), new SQLTable(this.conn, tableInfo));
    }

    /**
     * Create a {@link PreparedStatement} and cache it
     *
     * @param queryName
     * @param query     - sql to create the PreparedStatement from
     */
    public void addStatement(String queryName, String query) {
        try {
            LOGGER.debug("Adding a prepare statement : {}, query : {}", queryName, query);
            this.statements.put(queryName, this.conn.prepareStatement(query));
        } catch (SQLException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_CREATING_STATEMENT", queryName, e.getMessage()), e);
        }
    }

    /**
     * @return a {@lisk Statement}
     */
    public Statement createStatement() {
        try {
            return this.conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_CREATING_REGULAR_STATEMENT", e.getMessage()), e);
        }
    }

    /**
     * @param queryName
     * @return {@link PreparedStatement} according to the query name
     */
    public PreparedStatement getStatement(String queryName) {
        return statements.get(queryName);
    }

    /**
     * Clean data from all tables
     *
     * @throws Exception
     */
    public void clean() throws Exception {
        this.tables.values().forEach(SQLTable::clean);
    }


    /**
     * Clean data from all tables
     *
     * @throws Exception
     */
    public void clean(Map<String, SQLTable> tables) throws Exception {
        tables.values().forEach(SQLTable::clean);
    }

    /**
     * Set the data for the regular table
     *
     * @param tableName
     * @param messages
     * @throws Exception
     */
    public void setQueryData(String tableName, Collection<RepeatedMessage> messages) throws Exception {
        this.tables.get(tableName).setQueryData(messages);
    }

    /**
     * Set the data to connection specific regular tables.
     *
     * @param tables
     * @param tableInfo
     * @param messages
     * @throws Exception
     */
    public void setQueryData(Map<String, SQLTable> tables, String tableInfo, Collection<RepeatedMessage> messages) throws Exception {
        tables.get(tableInfo).setQueryData(messages);
    }

    /**
     * Register query reference table
     *
     * @param tableName    - reference table name
     * @param targetEntity - target entity which represents the structure of the table in memory
     */
    public void registerDBFileMaterializedView(String tableName, TargetEntity targetEntity) {
        try {
            LOGGER.debug("Registering DB file materialized view");
            //create the materialized view table using the result set metadata
            this.materializedViewTables.put(tableName, new DBFileMaterializedViewTable(this.conn, tableName, targetEntity));
        } catch (Exception e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_INITIALIZING_DBFILE_MATERIALIZED_VIEW_TABLE", tableName, e.getMessage()), e);
        }
    }

    /**
     * Register query reference table
     *
     * @param tableName    - reference table name
     * @param targetEntity - target entity which represents the structure of the table in memory
     */
    public void registerQueryMaterializedView(String tableName, TargetEntity targetEntity) {
        try {
            LOGGER.debug("Registering query materialized view");
            //create the materialized view table using the result set metadata
            this.materializedViewTables.put(tableName, new SQLMaterializedViewTable(this.conn, tableName, targetEntity));
        } catch (Exception e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_INITIALIZING_QUERY_MATERIALIZED_VIEW_TABLE", tableName, e.getMessage()), e);
        }
    }

    /**
     * Register script reference table
     *
     * @param tableName    - reference table name
     * @param targetEntity - target entity which represents the structure of the table in memory
     */
    public void registerScriptMaterializedView(String tableName, TargetEntity targetEntity) {
        try {
            LOGGER.debug("Registering script materialized view");
            //create the materialized view table using the result set metadata
            this.materializedViewTables.put(tableName, new GroovyMaterializedViewTable(this.conn, tableName, targetEntity));
        } catch (Exception e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_INITIALIZING_SCRIPT_MATERIALIZED_VIEW_TABLE", tableName, e.getMessage()), e);
        }
    }

    /**
     * Set the data for the reference table from the query
     *
     * @param tableName
     * @param resultSet
     * @return number of rows loaded/refreshed
     * @throws Exception
     */
    public long setQueryMaterializedViewData(String tableName, IQueryResultSet resultSet) throws Exception {
        //clean first the data if exists in the table
        this.materializedViewTables.get(tableName).clean();
        //insert the new data
        return ((SQLMaterializedViewTable) this.materializedViewTables.get(tableName)).setQueryData(resultSet);
    }

    /**
     * Set the data for the reference table from the script
     *
     * @param tableName
     * @param data
     * @throws Exception
     */
    public long setScriptMaterializedViewData(String tableName, List<Object> data) throws Exception {
        //clean first the data if exists in the table
        this.materializedViewTables.get(tableName).clean();
        //insert the new data
        return ((GroovyMaterializedViewTable) this.materializedViewTables.get(tableName)).setQueryData(data);
    }

    /**
     * Set the data for the reference table from the DB file
     *
     * @param tableName
     * @return number of rows loaded/refreshed
     * @throws Exception
     */
    public long setDBFileMaterializedViewData(String tableName) throws Exception {
        //clean first the data if exists in the table
        this.materializedViewTables.get(tableName).clean();
        //insert the new data
        return ((DBFileMaterializedViewTable) this.materializedViewTables.get(tableName)).loadDataFromDBFile();
    }

    /**
     * Close all connections and statements
     */
    public void close() {
        try {
            LOGGER.debug("Closing all the connections and statements");
            Stream.of(this.tables.values(), this.materializedViewTables.values()).forEach(abstractSQLTables -> {
                abstractSQLTables.forEach(AbstractSQLTable::close);
            });

            //close connection
            if (this.conn != null) {
                this.conn.close();
            }
        } catch (SQLException e) {
            LOGGER.error(LogMsg.getMessage("ERROR_CLOSING_SQL_RESOURCES", e.getMessage()));
            //not doing anything else ... not throwing the exception
        }
    }
}
