package com.amdocs.aia.il.deployer.database;

public final class ConfigurationConstants {
    public static final String RELATIONAL = "_Relation";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS";
    public static final String QUERY_COLUMN = "(tablename text,tablepk text,tsversion double,maintabledata text,tablenamecopy text,";
    public static final String PRIMARY_COLUMN = "PRIMARY KEY ((tablename, tablepk),tsversion )";
    public static final String CLUSTERING_COLUMN = ") WITH CLUSTERING ORDER BY (tsversion DESC);";

    public static final String QUERY_COLUMN_RELATION = "(tablename text,tablepk text,contextname text,relationtype text,relationtable text,relationkey text,tsversion double,tablenamecopy text,bulkprocessed boolean, ";
    public static final String PRIMARY_COLUMN_RELATION = "PRIMARY KEY ((tablename, tablepk , contextname), relationtype, relationtable, tsversion,relationkey )";
    public static final String CLUSTERING_COLUMN_RELATION = ") WITH CLUSTERING ORDER BY (relationtype ASC, relationtable ASC, tsversion DESC,relationkey ASC);";

    public static final String ALTER_TABLE = "ALTER TABLE";

    public static final String APPLICATION_BUNDLE_NAME = "ApplicationResourceBundle";
    public static final String RESOURCE_BUNDLE_PATH = "ResourceBundle";


    public static final String MSG_LIST_OF_CONFIGURED_SCHEMA = "LIST_OF_CONFIGURED_SCHEMA";
    public static final String MSG_NO_TABLES_CONFIGURED = "NO_TABLES_CONFIGURED";
    public static final String MSG_PREPARING_EXECUTE_BATCH = "PREPARING_EXECUTE_BATCH";
    public static final String MSG_PREPARING_STATEMENT = "PREPARING_STATEMENT";
    public static final String MSG_FINISHING_EXECUTING_BATCH_OF_STATEMENT = "FINISHING_EXECUTING_BATCH_OF_STATEMENT";
    public static final String MSG_QUERY_FAILED = "QUERY_FAILED";
    public static final String MSG_NUMBER_OF_LOADED_SCHEMA_STORE = "NUMBER_OF_LOADED_SCHEMA_STORE";
    public static final String MSG_STARTING_IL_DEPLOYER = "STARTING_IL_DEPLOYER";
    public static final String MSG_TABLE_ALREADY_EXISTS = "TABLE_ALREADY_EXISTS";
    public static final String MSG_ALTER_TABLE = "ALTER_TABLE";
    public static final String MSG_SKIPPED_TABLE_NO_CHANGE = "SKIPPED_TABLE_NO_CHANGE";
    public static final String MSG_ABOUT_TO_CREATE_TABLE = "ABOUT_TO_CREATE_TABLE";
    public static final String MSG_CREATED_TABLE = "CREATED_TABLE";
    public static final String MSG_FAILED_DEPLOYING_TABLE = "FAILED_DEPLOYING_TABLE";
    public static final String MSG_TABLE_CREATED = "TABLE_CREATED";
    public static final String MSG_TABLE_ALTERED = "TABLE_ALTERED";
    public static final String MSG_TABLE_FAILED = "TABLE_FAILED";
    public static final String MSG_TABLE_UNCHANGED = "TABLE_UNCHANGED";
    public static final String MSG_TOTAL_OF_CONFIGURED_TABLES = "TOTAL_OF_CONFIGURED_TABLES";
    public static final String MSG_NEW_ATTRIBUTES_OF_TABLE = "NEW_ATTRIBUTES_OF_TABLE";
    public static final String MSG_ADDING_ATTRIBUTES_TO_TABLE = "ADDING_ATTRIBUTES_TO_TABLE";
    public static final String MSG_NO_NEW_ATTRIBUTES_CONFIGURED = "NO_NEW_ATTRIBUTES_CONFIGURED";
    public static final String MSG_IL_DEPLOYER_FINISH = "IL_DEPLOYER_FINISH";


    private ConfigurationConstants() {
        // singleton
    }
}