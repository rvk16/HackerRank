package com.amdocs.aia.il.common.sqlite.sql;

import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.query.AbstractSQLQueryHandler;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Holds the reference table implementation for a reference table that is loaded from a DB file
 * Created by ORENKAF on 10/15/2017.
 */
public class DBFileMaterializedViewTable extends AbstractMaterializedViewTable {

    private final static Logger logger = LoggerFactory.getLogger(DBFileMaterializedViewTable.class);

    public DBFileMaterializedViewTable(Connection conn, String tableName, TargetEntity targetEntity) throws Exception {
        super(conn, tableName, targetEntity);
    }

    /**
     * Populate the materialized view data from the DB file
     * @return number of rows loaded/refreshed
     * @throws Exception
     */
    public long loadDataFromDBFile() throws Exception {
        int numberOfRowsInserted;
        try(Statement statement = this.conn.createStatement()) {
            numberOfRowsInserted = statement.executeUpdate(createReferenceInsertFromSelectStmt());
        }
        logger.info(LogMsg.getMessage("INFO_LOAD_REFERENCE_TABLE_ROW_NUM", numberOfRowsInserted, this.tableName));
        return numberOfRowsInserted;
    }

    /**
     * @return INSERT from SELECT statement for the reference table to be able to copy the data from the attached DB
     */
    private String createReferenceInsertFromSelectStmt() {
        return "INSERT INTO " + this.tableName + " SELECT * FROM " + AbstractSQLQueryHandler.REF_DB_ALIAS + "." + this.tableName;
    }

    @Override
    protected void createIndexes() throws SQLException {
        //create index for all keys
        for (String keyColumnName : this.keyColumnNames) {
            StringBuilder indexBuilder = new StringBuilder();
            indexBuilder.append("CREATE INDEX IF NOT EXISTS ").append(tableName).append("_").append(keyColumnName);
            indexBuilder.append(" ON ").append(tableName).append(" (").append(keyColumnName).append(")");
            try(Statement statement = this.conn.createStatement()) {
                statement.executeUpdate(indexBuilder.toString());
            }
        }
    }
}
