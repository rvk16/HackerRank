package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.il.common.query.sqllite.SQLQueryHandlerConnection;
import com.amdocs.aia.il.common.query.sqllite.SQLiteQueryHandlerForConflicting;
import com.amdocs.aia.il.common.sqlite.groovyResult.GroovyQueryResultSet;
import com.amdocs.aia.il.common.sqlite.queryResult.IQueryResultSet;

public abstract class AbstractDataProcessingGroovyScriptWrapperForConflicting extends AbstractDataProcessingGroovyScriptWrapper{

    private String threadName;

    AbstractDataProcessingGroovyScriptWrapperForConflicting(){
        super();
    }

    @Override
    public String getThreadName() {
        return threadName;
    }

    @Override
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    protected GroovyQueryResultSet sql(String sqlName, String sql) throws Exception {
        //since the query initialization is in runtime, need on first run to initialized it

        SQLQueryHandlerConnection sqlQueryHandlerConnection = ((SQLiteQueryHandlerForConflicting) this.queryHandler).getSqlQueryHandlerConnectionVsThread().get(threadName);
        if (sqlQueryHandlerConnection.getStatement(sqlName) == null) {
            sqlQueryHandlerConnection.addStatement(sqlName, sql);
        }
        return this.executeSQLForGroovy(sqlName,sqlQueryHandlerConnection);
    }


    private GroovyQueryResultSet executeSQLForGroovy(String sqlName, SQLQueryHandlerConnection sqlQueryHandlerConnection) throws Exception {
        IQueryResultSet queryResultSet = ((SQLiteQueryHandlerForConflicting)this.queryHandler).executeQuery(sqlName,sqlQueryHandlerConnection);
        return new GroovyQueryResultSet(queryResultSet);
    }

}
