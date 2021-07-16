package com.amdocs.aia.il.common.sqlite.queryResult;

import com.amdocs.aia.il.common.log.LogMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQL with no scrollable result set implementation for the IQueryResultSet which is the result of the query handler
 * Created by ORENKAF on 4/11/2016.
 */
public class SQLNonScrollableQueryResultSet extends SQLQueryResultSet {

    private final static Logger logger = LoggerFactory.getLogger(SQLNonScrollableQueryResultSet.class);

    private final String queryName;
    private final PreparedStatement statement;

    public SQLNonScrollableQueryResultSet(ResultSet resultSet, PreparedStatement statement, String queryName) {
        super(resultSet);
        this.statement = statement;
        this.queryName = queryName;
    }


    @Override
    public void beforeFirst() throws Exception {
        //since SQLIte do not supports ResultSet.TYPE_SCROLL_SENSITIVE
        //need to execute the query again
        //close first the current result set
        this.resultSet.close();

        try {
            this.resultSet = this.statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_EXECUTING_QUERY", queryName, e.getMessage()), e);
        }
    }
}
