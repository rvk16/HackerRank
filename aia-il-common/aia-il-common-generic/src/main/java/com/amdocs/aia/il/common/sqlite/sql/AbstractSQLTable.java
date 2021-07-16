package com.amdocs.aia.il.common.sqlite.sql;

import com.amdocs.aia.il.common.log.LogMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Abstract class for both reference and regular tables
 * Created by ORENKAF on 10/26/2016.
 */
public abstract class AbstractSQLTable {

    private final static Logger logger = LoggerFactory.getLogger(AbstractSQLTable.class);

    private static String DELETE_STMT_PREFIX = "DELETE FROM ";

    protected final static int BATCH_SIZE = 10000;

    protected String tableName;
    protected Connection conn;
    protected PreparedStatement insertStatement;
    protected PreparedStatement deleteStatement;

    public AbstractSQLTable(String tableName, Connection conn) {
        this.tableName = tableName;
        this.conn = conn;
    }

    /**
     * Initialize: create the table, create the insert & delete statements
     */
    protected void initTable()  {
        try {
            //create SQL table
            createSqlTable();
            //create indexes
            createIndexes();
            //create insert statements
            createInsertStatement();
            //create delete statement
            createDeleteStatement();
        } catch (SQLException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_INITIALIZING_TABLE", this.tableName, e.getMessage()), e);
        }
    }

    /**
     * Create the Table
     * @throws SQLException
     */
    protected abstract void createSqlTable() throws SQLException;

    protected void createIndexes() throws SQLException {
        //empty implementation
    }

    /**
     * Create the insert statement
     * @throws SQLException
     */
    protected abstract void createInsertStatement() throws SQLException;

    /**
     * Create the delete statement
     * @throws SQLException
     */
    protected void createDeleteStatement() throws SQLException {
        String deleteStmt = DELETE_STMT_PREFIX + this.tableName;
        this.deleteStatement = this.conn.prepareStatement(deleteStmt); //NOSONAR
    }

    /**
     * Clean the data in the table
     */
    public void clean() {
        try {
            this.deleteStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_CLEAN_TABLE", this.tableName, e.getMessage()), e);
        }

    }

    /**
     * Close the opened resources. Insert & delete statements
     */
    public void close() {
        try{
            //close delete statements
            this.deleteStatement.close();
            //close insert statement
            this.insertStatement.close();
        } catch (SQLException e) {
            logger.error(LogMsg.getMessage("ERROR_CLOSING_SQL_RESOURCES", e.getMessage()));
            //not doing anything else ... not throwing the exception
        }
    }
}

