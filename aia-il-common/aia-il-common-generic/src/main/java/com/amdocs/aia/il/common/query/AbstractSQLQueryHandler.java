package com.amdocs.aia.il.common.query;

import com.amdocs.aia.il.common.query.sqllite.SQLQueryHandlerConnection;
import com.amdocs.aia.il.common.reference.table.AbstractReferenceTableInfo;
import com.amdocs.aia.il.common.sqlite.queryResult.IQueryResultSet;
import com.amdocs.aia.il.common.sqlite.queryResult.SQLQueryResultSet;
import com.amdocs.aia.il.common.sqlite.sql.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ORENKAF on 10/27/2016.
 */
public abstract class AbstractSQLQueryHandler implements IQueryHandler {

    protected final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss.SSS");
    public final static String REF_DB_ALIAS = "REF_DB";

    protected String name;
    protected boolean loadReferenceTablesFromDBFile;
    protected boolean backupDBToFile;
    protected String baseSharedPath;
    protected String jobName;

    //holds the connection/statement ... for the reference tables
    protected Map<String, SQLQueryHandlerConnection> sqlReferenceConnections;

    public AbstractSQLQueryHandler(String name, boolean loadReferenceTablesFromDBFile, boolean backupDBToFile, String baseSharedPath, String jobName) {
        this.name = name;
        this.loadReferenceTablesFromDBFile = loadReferenceTablesFromDBFile;
        this.backupDBToFile = backupDBToFile;
        this.sqlReferenceConnections = new HashMap<>();
        this.baseSharedPath = baseSharedPath;
        this.jobName = jobName;
    }

    @Override
    public boolean isLoadReferenceTablesFromDBFile() {
        return this.loadReferenceTablesFromDBFile;
    }

    @Override
    public void initReferenceTableQuery(AbstractReferenceTableInfo referenceTableInfo, String queryName, String query) {
        SQLQueryHandlerConnection sqlQueryHandlerConnection = this.sqlReferenceConnections.get(referenceTableInfo.getName());
        sqlQueryHandlerConnection.addStatement(queryName, query);
    }

    /**
     * @param dbFilePath
     * @param alias
     * @return the ATTACH query statement
     */
    protected abstract String getAttachQuery(String dbFilePath, String alias);

    /**
     * @param alias
     * @return the DETACH query statement
     */
    protected abstract String getDetachQuery(String alias);

    /**
     * @param preparedStatement
     * @param queryName
     * @return {@link IQueryResultSet} which related to this type of connection
     * @throws SQLException
     */
    protected IQueryResultSet createQueryResultSet(PreparedStatement preparedStatement, String queryName) throws SQLException {
        return new SQLQueryResultSet(preparedStatement.executeQuery());
    }
    /**
     * @param backupFile - file to backup
     * @return the query to backup the in memory DB to a file (for debug mode) OR NULL for not backing up at all
     */
    public abstract String getBackupQuery(Path backupFile);

    /**
     * @return {@link Connection} according to the SQL type
     */
    protected abstract Connection createConnection();

    /**
     * Holds the SQL {@link Connection}, regular and materialized view tables and statements
     */

}
