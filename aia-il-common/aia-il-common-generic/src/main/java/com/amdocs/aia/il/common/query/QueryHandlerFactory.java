package com.amdocs.aia.il.common.query;

/**
 * Interface for the factory which creates the {@link IQueryHandler}
 * Created by ORENKAF on 6/29/2016.
 */
public interface QueryHandlerFactory {

    IQueryHandler getQueryHandler(String name, boolean isLoadReferenceTablesFromDBFile, boolean isBackupDBToFile, String sharedStoragePath, String jobName, boolean conflicting);

}
