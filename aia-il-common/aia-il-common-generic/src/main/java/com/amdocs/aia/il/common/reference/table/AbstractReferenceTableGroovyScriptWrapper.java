package com.amdocs.aia.il.common.reference.table;

import com.amdocs.aia.il.common.query.IQueryHandler;
import com.amdocs.aia.il.common.sqlite.groovyResult.GroovyQueryResultRow;
import com.amdocs.aia.il.common.sqlite.groovyResult.GroovyQueryResultSet;
import com.amdocs.aia.il.common.sqlite.queryResult.IQueryResultSet;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The base class for running users groovy script
 * Contains the methods that the uses logic can use:
 * {@link #sql(String, String)} - execute the given SQL and return a {@link GroovyQueryResultSet}
 *  - execute an SQL loaded from a file and return a {@link GroovyQueryResultSet}
 * {@link #emit(Map)} - emit one message
 * {@link #emit(GroovyQueryResultRow)} - emit one message
 * {@link #emitGroup(List)} - emit a group of messages
 * Created by ORENKAF on 4/3/2017.
 */
abstract public class AbstractReferenceTableGroovyScriptWrapper {


    private String name;
    private IQueryHandler queryHandler;

    private ReferenceTableScriptInfo referenceTableInfo;
    private TargetEntity targetEntity;

    private List<Object> resultMessages = new ArrayList<>();

    //package permissions in order prevent setting the query handler from derived script
    public void setQueryHandler(IQueryHandler queryHandler) {
        this.queryHandler = queryHandler;
    }

    public void setReferenceTableInfo(ReferenceTableScriptInfo referenceTableInfo) {
        this.referenceTableInfo = referenceTableInfo;
    }

    public void setTargetEntity(TargetEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * Execute the given SQL and return a {@link GroovyQueryResultSet}
     * @param sqlName - unique identifier of the SQL
     * @param sql - sql to execute on the data processing context data
     * @return {@link GroovyQueryResultSet}
     * @throws Exception
     */
    protected GroovyQueryResultSet sql(String sqlName, String sql) throws Exception {
        //since the query initialization is in runtime, need on first run to initialized it
        if(!this.queryHandler.isReferenceQueryInitialized(this.referenceTableInfo, sqlName)) {
            this.queryHandler.initReferenceTableQuery(this.referenceTableInfo, sqlName, sql);
        }
        return this.executeSQL(sqlName);
    }

    /**
     * Executes an SQL by it's name. SQL should have been prepared statement already
     * @param sqlName - unique identifier of the SQL
     * @return {@link GroovyQueryResultSet}
     * @throws Exception
     */
    private GroovyQueryResultSet executeSQL(String sqlName) throws Exception {
        IQueryResultSet queryResultSet = this.queryHandler.executeReferenceTableQuery(this.referenceTableInfo, sqlName);
        return new GroovyQueryResultSet(queryResultSet);
    }

    /**
     * Emit one message
     * @param message - {@link GroovyQueryResultRow} as the message
     */
    protected void emit(GroovyQueryResultRow message) {
        this.emit(message.asMap());
    }


    /**
     * Emit one message
     * @param message - Map of name and values of a single message
     */
    protected void emit(Map<String, Object> message) {
        this.resultMessages.add(message);
    }

    /**
     * Emit a group of messages
     * @param messages - List of Maps, each entry represents a single message or either as a map.
     */
    protected void emitGroup(List<Object> messages) {
        this.resultMessages.addAll(messages);
    }

    /**
     * Clear the results for current run
     */
    public void clear() {
        this.resultMessages.clear();
    }

    public List<Object> getResultMessages() {
        return resultMessages;
    }

    /**
     * The main method which runs the user logic
     */
    public abstract void process();

}
