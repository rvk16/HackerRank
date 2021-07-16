package com.amdocs.aia.il.deployer.database;

public class DatabaseQueryExecutionException extends RuntimeException {
    public DatabaseQueryExecutionException(String message, String... params) {
        super(String.format(message, params));
    }

    public DatabaseQueryExecutionException(String message, Throwable cause, String... params) {
        super(String.format(message, params), cause);
    }

    public DatabaseQueryExecutionException(Throwable cause) {
        super(cause);
    }
}
