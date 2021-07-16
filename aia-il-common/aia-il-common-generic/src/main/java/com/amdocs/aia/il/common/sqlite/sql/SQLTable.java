package com.amdocs.aia.il.common.sqlite.sql;


import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.constant.JavaTypes;
import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.utils.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collection;

/**
 * Holds a regular table implementation
 */
public class SQLTable extends AbstractSQLTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLTable.class);

    private final ConfigurationRow tableInfo;

    public SQLTable(Connection conn, ConfigurationRow tableInfo) {
        super(tableInfo.getTableName(), conn);
        this.tableInfo = tableInfo;
        super.initTable();
    }

    @Override
    protected void createSqlTable() throws SQLException {
        String[] fdNames = new String[tableInfo.getTableConfiguration().getColumns().size()];
        int index = 0;
        for (ColumnConfiguration column : tableInfo.getTableConfiguration().getColumns()) {
            fdNames[index++] = column.getColumnName() + " " + column.getDatatype().getSqlType();
        }

        StringBuilder createTableStatement = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + "( ");
        createTableStatement.append(StringUtils.join(fdNames, ","));
        createTableStatement.append(")");
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL query: {}" , createTableStatement);
        }
        try (Statement statement = this.conn.createStatement()) {
            statement.executeUpdate(createTableStatement.toString());
        }
    }

    @Override
    protected void createInsertStatement() throws SQLException {
        int fieldCount = tableInfo.getTableConfiguration().getColumns().size();
        String[] fdNames = new String[fieldCount];
        String[] fdQMark = new String[fieldCount];
        int index = 0;
        for (ColumnConfiguration column : tableInfo.getTableConfiguration().getColumns()) {
            fdNames[index] = column.getColumnName();
            fdQMark[index++] = "?";
        }

        StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + " ( ");
        insertSql.append(StringUtils.join(fdNames, ","));
        insertSql.append(" ) VALUES ( ");
        insertSql.append(StringUtils.join(fdQMark, ","));
        insertSql.append(')');
        this.insertStatement = this.conn.prepareStatement(insertSql.toString());
    }

    public void setQueryData(Collection<RepeatedMessage> messages) throws SQLException {
        int numberOfRowsInserted = 0;
        for (RepeatedMessage message : messages) {
            try {
                int index = 1;
                for (ColumnConfiguration column : tableInfo.getTableConfiguration().getColumns()) {
                    Object field = message.getValue(column.getColumnName());
                    if (field == null) {
                        setNullData(insertStatement, column.getDatatype().getSqlType(), index);
                    } else {
                        setData(insertStatement, column.getDatatype().getSqlType(), index, field);
                    }
                    index++;
                }
                insertStatement.addBatch();
                if (++numberOfRowsInserted % AbstractMaterializedViewTable.BATCH_SIZE == 0) {
                    this.insertStatement.executeBatch();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(LogMsg.getMessage("ERROR_FAILED_IN_INSERT_STMT", tableInfo.getTableName(), ex.getMessage()), ex);
            }
        }
        insertStatement.executeBatch();
    }

    /**
     * Set NULL for a field in the insert statement
     *
     * @param insertStatement
     * @param datatype
     * @param index
     * @throws SQLException
     */
    private static void setNullData(PreparedStatement insertStatement, String datatype, int index) throws SQLException {
        switch (ConversionUtils.resolveJavaType(datatype)) {
            case JavaTypes.LONG:
                insertStatement.setNull(index, Types.BIGINT);
                break;
            case JavaTypes.FLOAT:
                insertStatement.setNull(index, Types.FLOAT);
                break;
            case JavaTypes.INT:
                insertStatement.setNull(index, Types.INTEGER);
                break;
            case JavaTypes.DOUBLE:
                insertStatement.setNull(index, Types.DOUBLE);
                break;
            case JavaTypes.BOOLEAN:
                insertStatement.setNull(index, Types.BOOLEAN);
                break;
            case JavaTypes.STRING:
            default:
                insertStatement.setNull(index, Types.VARCHAR);
                break;
        }
    }

    /**
     * Set a value for a field in the insert statement
     *
     * @param insertStatement
     * @param datatype
     * @param index
     * @param value
     * @throws SQLException
     */
    private static void setData(PreparedStatement insertStatement, String datatype, int index, Object value) throws SQLException {
        switch (ConversionUtils.resolveJavaType(datatype)) {
            case JavaTypes.LONG:
                insertStatement.setLong(index, Long.parseLong(value.toString()));
                break;
            case JavaTypes.FLOAT:
                insertStatement.setFloat(index, Float.parseFloat(value.toString()));
                break;
            case JavaTypes.INT:
                insertStatement.setInt(index, Integer.parseInt(value.toString()));
                break;
            case JavaTypes.DOUBLE:
                insertStatement.setDouble(index, Double.parseDouble(value.toString()));
                break;
            case JavaTypes.BOOLEAN:
                insertStatement.setBoolean(index, Boolean.parseBoolean(value.toString()));
                break;
            case JavaTypes.STRING:
            default:
                insertStatement.setString(index, value.toString());
                break;
        }
    }
}