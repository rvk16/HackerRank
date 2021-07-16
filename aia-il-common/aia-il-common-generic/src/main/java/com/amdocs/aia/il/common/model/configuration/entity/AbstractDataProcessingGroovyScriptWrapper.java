package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.query.IQueryHandler;
import com.amdocs.aia.il.common.query.sqllite.SQLQueryHandlerConnection;
import com.amdocs.aia.il.common.query.sqllite.SQLiteQueryHandlerForConflicting;
import com.amdocs.aia.il.common.sqlite.groovyResult.GroovyQueryResultSet;
import com.amdocs.aia.il.common.sqlite.queryResult.IQueryResultSet;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

abstract public class AbstractDataProcessingGroovyScriptWrapper {

    private final static Logger LOGGER= LoggerFactory.getLogger(AbstractDataProcessingGroovyScriptWrapper.class);

    protected String name;
    protected String dataProcessingContextName;
    protected IQueryHandler queryHandler;
    protected boolean isDeletedEntitiesQuery;
    protected PublishingGroovy publishingGroovy;
    protected Map<String, TargetEntity> targetEntities;
    private String threadName;

    public void setQueryHandler(IQueryHandler queryHandler) {
        this.queryHandler = queryHandler;
    }

    public void setDeletedEntitiesQuery(boolean deletedEntitiesQuery) {
        isDeletedEntitiesQuery = deletedEntitiesQuery;
    }

    public void setPublishingGroovy(PublishingGroovy publishingGroovy) {
        this.publishingGroovy = publishingGroovy;
    }

    public void setTargetEntities(Map<String, TargetEntity> targetEntities) {
        this.targetEntities = targetEntities;
    }

    public void setName(String name) {
        this.name = name;
        //set also the counter name prefix
    }

    public void setDataProcessingContextName(String dataProcessingContextName) {
        this.dataProcessingContextName = dataProcessingContextName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    /*
     * Execute the given SQL and return a {@link GroovyQueryResultSet}
     * @param sqlName - unique identifier of the SQL
     * @param sql - sql to execute on the data processing context data
     * @return {@link GroovyQueryResultSet}
     * @throws Exception
    */
    protected GroovyQueryResultSet sql(String sqlName, String sql) throws Exception {
        //since the query initialization is in runtime, need on first run to initialized it
        if(threadName == null) {
            if (!this.queryHandler.isQueryInitialized(sqlName)) {
                this.queryHandler.initQuery(sqlName, sql);
            }
            return this.executeSQL(sqlName);
        }
        SQLQueryHandlerConnection sqlQueryHandlerConnection = ((SQLiteQueryHandlerForConflicting) this.queryHandler).getSqlQueryHandlerConnectionVsThread().get(threadName);
        if (sqlQueryHandlerConnection.getStatement(sqlName) == null) {
            sqlQueryHandlerConnection.addStatement(sqlName, sql);
        }
        return this.executeSQL(sqlName,sqlQueryHandlerConnection);
    }

    /**
     * Executes an SQL by it's name. SQL should contain prepared statement already
     * @param sqlName - unique identifier of the SQL
     * @return {@link GroovyQueryResultSet}
     * @throws Exception
     */
    private GroovyQueryResultSet executeSQL(String sqlName) throws Exception {
        IQueryResultSet queryResultSet = this.queryHandler.executeQuery(sqlName);
        return new GroovyQueryResultSet(queryResultSet);
    }

    private GroovyQueryResultSet executeSQL(String sqlName, SQLQueryHandlerConnection sqlQueryHandlerConnection) throws Exception {
        IQueryResultSet queryResultSet = ((SQLiteQueryHandlerForConflicting)this.queryHandler).executeQuery(sqlName,sqlQueryHandlerConnection);
        return new GroovyQueryResultSet(queryResultSet);
    }

    private TargetEntity getTargetEntity(String targetEntityName) {
        TargetEntity targetEntity = this.targetEntities.get(targetEntityName);
        if(targetEntity == null) {
            LOGGER.error(LogMsg.getMessage("ERROR_TARGET_ENTITY_NOT_EXISTS_IN_GROOVY", targetEntityName, this.name, this.dataProcessingContextName));
            throw new RuntimeException(LogMsg.getMessage("ERROR_TARGET_ENTITY_NOT_EXISTS_IN_GROOVY", targetEntityName, this.name, this.dataProcessingContextName));
        }
        return targetEntity;
    }


    /**
     * The main method which runs the user logic
    */
    public abstract void process();
}
