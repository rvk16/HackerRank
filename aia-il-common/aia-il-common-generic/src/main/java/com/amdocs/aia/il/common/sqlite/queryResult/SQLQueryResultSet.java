package com.amdocs.aia.il.common.sqlite.queryResult;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * SQL implementation for the IQueryResultSet which is the result of the query handler
 * Statement must supports ResultSet.TYPE_SCROLL_SENSITIVE
 * Created by ORENKAF on 4/11/2016.
 */
public class SQLQueryResultSet implements IQueryResultSet {

    protected ResultSet resultSet;

    public SQLQueryResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }


    @Override
    public ResultSetMetaData getMetaData() throws Exception {
        return resultSet.getMetaData();
    }

    @Override
    public boolean next() throws Exception {
        return resultSet.next();
    }

    @Override
    public Object getObject(int columnIndex) throws Exception {
        return resultSet.getObject(columnIndex);
    }

    @Override
    public String getString(int columnIndex) throws Exception {
        return resultSet.getString(columnIndex);
    }

    @Override
    public double getDouble(int columnIndex) throws Exception {
        return resultSet.getDouble(columnIndex);
    }

    @Override
    public float getFloat(int columnIndex) throws Exception {
        return resultSet.getFloat(columnIndex);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws Exception {
        return resultSet.getBoolean(columnIndex);
    }

    @Override
    public int getInt(int columnIndex) throws Exception {
        return resultSet.getInt(columnIndex);
    }

    @Override
    public long getLong(int columnIndex) throws Exception {
        return resultSet.getLong(columnIndex);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws Exception {
        return resultSet.getBigDecimal(columnIndex);
    }

    @Override
    public int getColumnCount() throws Exception {
        return resultSet.getMetaData().getColumnCount();
    }

    @Override
    public String getColumnLabel(int column) throws Exception {
        return resultSet.getMetaData().getColumnLabel(column);
    }

    @Override
    public String getColumnName(int column) throws Exception {
        return resultSet.getMetaData().getColumnName(column);
    }

    @Override
    public void close() throws Exception {
        this.resultSet.close();
    }

    @Override
    public void beforeFirst() throws Exception {
        this.resultSet.beforeFirst();
    }

    @Override
    public boolean wasNull() throws Exception {
        return this.resultSet.wasNull();
    }

    @Override
    public Map<String, Object> getAllValues() throws Exception {
        Map<String, Object> values = new HashMap<>();
        ResultSetMetaData metadata = this.getMetaData();
        int columnCount = metadata.getColumnCount();
        for (int i=1; i<=columnCount; i++) {
            //Type name and ID do not match, SQLite is doing optimizations, therefore, must use the name
            switch(metadata.getColumnTypeName(i)) {
                case "DECIMAL": //Types.DECIMAL
                    values.put(metadata.getColumnLabel(i), this.getBigDecimal(i));
                    break;
                case "BIGINT"://there is optimization of Long to Int therefore this case is needed
                    long longValue = this.getLong(i);
                    if(this.wasNull()) {
                        values.put(metadata.getColumnLabel(i), null);
                    } else {
                        values.put(metadata.getColumnLabel(i), longValue);
                    }
                    break;
                default:
                    values.put(metadata.getColumnLabel(i),this.getObject(i));
            }
        }
        return values;
    }

}
