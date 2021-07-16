package com.amdocs.aia.il.common.utils;

public class SharedPathUtils {

    public static final String BACK_UP_DB_DIR = "back_up_db";
    public static final String REF_BACKUP_DIR = "ref_db_backup";
    public static final String REFRESH_TABLE_DIR = "refresh_table";
    public static final String ERROR_DIR = "errors";
    public static final String LOAD_REFERENCE_TABLES_RELATIVE_PATH = "aia.il.reference.store.publisher.notifyTransformerState";
    public static final String LAST_REFRESH_TIME_RELATIVE_PATH = "last_refresh_time";
    public static final String REFRESH_TABLE_BACKUP_PATH = "last_backup_file";
    public static final String FILE_SEPARATOR = "/";

    private SharedPathUtils() {
        // Util Class
    }

    private static String getPath(String basePath, String dir) {
        return basePath + FILE_SEPARATOR + dir;
    }

    public static String getLastRefreshedPath(String basePath) {
        return getPath(basePath, REFRESH_TABLE_DIR) + FILE_SEPARATOR + LAST_REFRESH_TIME_RELATIVE_PATH;
    }

    public static String getLoadRefTablePath(String basePath) {
        return basePath + FILE_SEPARATOR + LOAD_REFERENCE_TABLES_RELATIVE_PATH;
    }

    public static String getBackUpDirectory(String basePath) {
        return getPath(basePath, BACK_UP_DB_DIR);
    }

    public static String getRefTableBackUpDirectory(String basePath) {
        return getPath(basePath, REF_BACKUP_DIR);
    }

    public static String getRefTableBackupFile(String basePath) {
        return getPath(basePath, REFRESH_TABLE_DIR) + FILE_SEPARATOR + REFRESH_TABLE_BACKUP_PATH;
    }

    public static String getErrorDirectory(String basePath) {
        return getPath(basePath, ERROR_DIR);
    }
}
