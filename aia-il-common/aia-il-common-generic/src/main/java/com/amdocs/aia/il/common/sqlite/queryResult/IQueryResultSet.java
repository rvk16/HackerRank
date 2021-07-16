package com.amdocs.aia.il.common.sqlite.queryResult;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.util.Map;

/**
 * Holds the result of the publisher query
 * Created by ORENKAF on 4/11/2016.
 */
public interface IQueryResultSet {

    //row data APIs

    public ResultSetMetaData getMetaData() throws Exception;
    /**
     * Move to next row
     * @return true if next row exists, else false
     * @throws Exception
     */
    public boolean next() throws Exception;


    /**
     * Get a Object column value
     * @param columnIndex - index to row to fetch the value from
     * @return a Object column value
     * @throws Exception
     */
    public Object getObject(int columnIndex) throws Exception;

    /**
     * Get a String column value
     * @param columnIndex - index to row to fetch the value from
     * @return a String column value
     * @throws Exception
     */
    public String getString(int columnIndex) throws Exception;

    /**
     * Get a Double column value
     * @param columnIndex - index to row to fetch the value from
     * @return a Double column value
     * @throws Exception
     */
    public double getDouble(int columnIndex) throws Exception;

    /**
     * Get a Float column value
     * @param columnIndex - index to row to fetch the value from
     * @return a Float column value
     * @throws Exception
     */
    public float getFloat(int columnIndex) throws Exception;

    /**
     * Get a boolean column value
     * @param columnIndex - index to row to fetch the value from
     * @return a boolean column value
     * @throws Exception
     */
    public boolean getBoolean(int columnIndex) throws Exception;
    /**
     * Get a int column value
     * @param columnIndex - index to row to fetch the value from
     * @return a int column value
     * @throws Exception
     */
    public int getInt(int columnIndex) throws Exception;
    /**
     * Get a long column value
     * @param columnIndex - index to row to fetch the value from
     * @return a long column value
     * @throws Exception
     */
    public long getLong(int columnIndex) throws Exception;

    /**
     * Get a BigDecimal column value
     * @param columnIndex - index to row to fetch the value from
     * @return a BigDecimal column value
     * @throws Exception
     */
    public BigDecimal getBigDecimal(int columnIndex) throws Exception;

    //schema APIs

    /**
     * @return the number of columns in a row
     * @throws Exception
     */
    public int getColumnCount() throws Exception;

    /**
     * @param column - column index
     * @return the column label
     * @throws Exception
     */
    public String getColumnLabel(int column) throws Exception;
    /**
     * @param column - column index
     * @return the column name
     * @throws Exception
     */
    public String getColumnName(int column) throws Exception;

    /**
     * Close the result set
     * @throws Exception
     */
    public void close() throws Exception;

    /**
     * Reset the result set to the beginning
     * @throws Exception
     */
    public void beforeFirst() throws Exception;

    /**
     * Reports whether
     * the last column read had a value of SQL <code>NULL</code>.
     * @return <code>true</code> if the last column value read was SQL
     *         <code>NULL</code> and <code>false</code> otherwise
     */
    boolean wasNull() throws Exception;

    /**
     * @return all the values in the result set as a Map of name and value
     * @throws Exception
     */
    Map<String, Object> getAllValues() throws Exception;
}
