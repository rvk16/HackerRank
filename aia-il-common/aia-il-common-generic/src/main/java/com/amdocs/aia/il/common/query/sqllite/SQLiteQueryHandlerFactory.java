package com.amdocs.aia.il.common.query.sqllite;

import com.amdocs.aia.il.common.query.IQueryHandler;
import com.amdocs.aia.il.common.query.QueryHandlerFactory;

import java.io.Serializable;

/**
 * Factory to create the IQueryHandler for the SQLLite implementation
 * Created by ORENKAF on 4/10/2016.
 */
public class SQLiteQueryHandlerFactory implements QueryHandlerFactory, Serializable {

    private static final long serialVersionUID = 8314559960429337138L;

    public IQueryHandler getQueryHandler(String name, boolean isLoadReferenceTablesFromDBFile, boolean isBackupDBToFile, String sharedStoragePath, String jobName, boolean conflicting) {
       if(!conflicting) {
           return new SQLiteQueryHandler(name, isLoadReferenceTablesFromDBFile, isBackupDBToFile, sharedStoragePath, jobName);
       }else{
           return new SQLiteQueryHandlerForConflicting(name, isLoadReferenceTablesFromDBFile, isBackupDBToFile, sharedStoragePath, jobName);
       }
    }
}
