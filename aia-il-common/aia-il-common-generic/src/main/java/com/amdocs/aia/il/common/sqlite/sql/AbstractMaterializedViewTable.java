package com.amdocs.aia.il.common.sqlite.sql;

import com.amdocs.aia.common.model.logical.PrimitiveDatatype;
import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;
import com.amdocs.aia.il.common.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all materialized views
 * Created by ORENKAF on 10/27/2016.
 */
public class AbstractMaterializedViewTable extends AbstractSQLTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMaterializedViewTable.class);

    //a map that contains all SQL Type names and their IDs used to initialize the columnTypes array
    protected static final Map<String, Integer> ALL_JDBC_TYPE_IDS = new HashMap<>();

    protected String[] columnNames;
    protected int[] columnTypes;
    protected String[] columnTypesName;
    protected List<String> keyColumnNames;
    protected final TargetEntity targetEntity;

    static {
        try {
            for (Field field : Types.class.getFields()) {
                ALL_JDBC_TYPE_IDS.put(field.getName(), (Integer) field.get(null));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_CREATING_MATERIALIZED_VIEW_INSTANCE", e.getMessage()), e);
        }
    }

    public AbstractMaterializedViewTable(Connection conn, String tableName, TargetEntity targetEntity) {
        super(tableName, conn);
        this.targetEntity = targetEntity;
        this.keyColumnNames = new ArrayList<>();

        final Map<String, PrimitiveDatatype> fdsToSet = new HashMap<>();
        TargetEntity.TargetTable targetTable = targetEntity.getTargetTable();
        targetTable.getColumnsVsType().forEach((column, type) -> fdsToSet.put(column, type.getPrimitiveType()));
        keyColumnNames.addAll(targetTable.getPrimaryKeys());

        this.columnNames = new String[fdsToSet.size()];
        this.columnTypes = new int[fdsToSet.size()];
        this.columnTypesName = new String[fdsToSet.size()];
        int i = 0;
        for (Map.Entry<String, PrimitiveDatatype> fdEntry : fdsToSet.entrySet()) {
            this.columnNames[i] = fdEntry.getKey();
            this.columnTypesName[i] = ConversionUtils.getSQLTypeForTargetEntity(fdEntry.getValue());
            this.columnTypes[i] = ALL_JDBC_TYPE_IDS.get(getColumnTypesNameSimple(columnTypesName[i++]));
        }

        //initialize: create the table, create the insert & delete statements
        super.initTable();
    }

    /**
     * In case a column type name contains brackets as DECIMAL(x,y), remove the brackets
     *
     * @param columnTypesName
     * @return column type name without brackets
     */
    private static String getColumnTypesNameSimple(String columnTypesName) {
        return columnTypesName.contains("(") ? columnTypesName.substring(0, columnTypesName.indexOf('(')) : columnTypesName;
    }

    @Override
    protected void createSqlTable() throws SQLException {
        String[] fdNames = new String[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            fdNames[i] = columnNames[i] + ' ' + columnTypesName[i];
        }

        StringBuilder createTableStatement = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + '(');
        createTableStatement.append(String.join(",", fdNames));
        createTableStatement.append(')');
        try (Statement statement = this.conn.createStatement()) {
            statement.executeUpdate(createTableStatement.toString());
        }
    }

    @Override
    protected void createInsertStatement() throws SQLException {
        String[] colNames = new String[columnNames.length];
        String[] colQMark = new String[columnNames.length];

        for (int i = 0; i < columnNames.length; i++) {
            colNames[i] = columnNames[i];
            colQMark[i] = "?";
        }

        StringBuilder insertSql = new StringBuilder("INSERT INTO ");
        insertSql.append(this.tableName).append(" (");
        insertSql.append(String.join(",", colNames));
        insertSql.append(") VALUES (");
        insertSql.append(String.join(",", colQMark));
        insertSql.append(')');
        this.insertStatement = this.conn.prepareStatement(insertSql.toString());
    }

    /**
     * Inserts a row to the materialized view table
     *
     * @param rowData - represents data of a single row
     * @throws SQLException
     */
    protected void insertRow(Map<String, Object> rowData) throws SQLException {
        for (int i = 0; i < columnNames.length; i++) {
            Object value = rowData.get(columnNames[i]);
            boolean isNull = (value == null);
            if (isNull) {
                this.insertStatement.setNull(i + 1, columnTypes[i]);
            } else {
                insertStmtCondChk(i, value);
            }
        }
        this.insertStatement.addBatch();
    }

    private void insertStmtCondChk(int i, Object value) throws SQLException {
        switch (columnTypes[i]) {
            case Types.VARCHAR:
                this.insertStatement.setString(i + 1, value.toString());
                break;
            case Types.INTEGER:
                this.insertStatement.setInt(i + 1, (int) value);
                break;
            case Types.BIGINT:
                if (value instanceof Integer) {
                    value = ((Integer) value).longValue();
                }
                this.insertStatement.setLong(i + 1, (long) value);
                break;
            case Types.BOOLEAN:
                this.insertStatement.setBoolean(i + 1, (boolean) value);
                break;
            case Types.DECIMAL:
                this.insertStatement.setBigDecimal(i + 1, (BigDecimal) value);
                break;
            default:
                this.insertStatement.setString(i + 1, (String) value);
        }
    }

    public long setQueryWithCachedData(List<Map<String, Object>> rs) throws SQLException {
        int numberOfRowsInserted = 0;
        for (Map<String, Object> values : rs) {
            //insert a single row to the table
            insertRow(values);
            if (++numberOfRowsInserted % AbstractMaterializedViewTable.BATCH_SIZE == 0) {
                this.insertStatement.executeBatch();
            }
        }
        this.insertStatement.executeBatch();
        LOGGER.info(LogMsg.getMessage("INFO_LOAD_REFERENCE_TABLE_ROW_NUM", numberOfRowsInserted, this.tableName));
        return numberOfRowsInserted;
    }
}