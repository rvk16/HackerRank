package com.amdocs.aia.il.common.sqlite.sql;

import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.sqlite.groovyResult.GroovyQueryResultRow;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/**
 * Holds the reference table implementation loaded from a groovy script
 * Created by ORENKAF on 10/27/2016.
 */
public class GroovyMaterializedViewTable extends AbstractMaterializedViewTable {

    private final static Logger logger = LoggerFactory.getLogger(GroovyMaterializedViewTable.class);

    public GroovyMaterializedViewTable(Connection conn, String tableName, TargetEntity targetEntity) throws Exception {
        super(conn, tableName, targetEntity);
    }

    /**
     * Populate the materialized view data
     * @param dataList - list of rows from the groovy script
     * @throws SQLException
     */
    public long setQueryData(List<Object> dataList) throws SQLException {
        int numberOfRowsInserted = 0;
        if(dataList == null || dataList.isEmpty()) {
            return 0;
        }

        //check if the results are from Map or GroovyQueryResultRow type
        final boolean isGroovyObject = (dataList.iterator().next() instanceof GroovyQueryResultRow);
        for (Object rowDataObj : dataList) {
            Map<String, Object> rowData;
            if(isGroovyObject) {
                rowData = ((GroovyQueryResultRow)rowDataObj).asMap();
            } else {
                rowData = (Map<String, Object>)rowDataObj;
            }
            //insert a single row to the table
            insertRow(rowData);
            if(++numberOfRowsInserted % AbstractMaterializedViewTable.BATCH_SIZE == 0) {
                this.insertStatement.executeBatch();
            }
        }
        this.insertStatement.executeBatch();
        logger.info(LogMsg.getMessage("INFO_LOAD_REFERENCE_TABLE_ROW_NUM", dataList.size(), this.tableName));
        return dataList.size();
    }
}
