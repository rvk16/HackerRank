package com.amdocs.aia.il.common.sqlite.sql;

import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.sqlite.queryResult.IQueryResultSet;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;


/**
 * Holds the reference table implementation
 * Created by ORENKAF on 10/27/2016.
 */
public class SQLMaterializedViewTable extends AbstractMaterializedViewTable {

    private final static Logger logger = LoggerFactory.getLogger(SQLMaterializedViewTable.class);

    public SQLMaterializedViewTable(Connection conn, String tableName, TargetEntity targetEntity) throws Exception {
        super(conn, tableName, targetEntity);
    }

    /**
     * Populate the materialized view data
     * @param rs - result set of the materialized view SQL
     * @return number of rows loaded/refreshed
     * @throws Exception
     */
    public long setQueryData(IQueryResultSet rs) throws Exception {
        int numberOfRowsInserted = 0;
        while (rs.next()) {
            //insert a single row to the table
            insertRow(rs.getAllValues());
            if(++numberOfRowsInserted % AbstractMaterializedViewTable.BATCH_SIZE == 0) {
                this.insertStatement.executeBatch();
            }
        }
        this.insertStatement.executeBatch();
        logger.info(LogMsg.getMessage("INFO_LOAD_REFERENCE_TABLE_ROW_NUM", numberOfRowsInserted, this.tableName));
        return numberOfRowsInserted;
    }

}
