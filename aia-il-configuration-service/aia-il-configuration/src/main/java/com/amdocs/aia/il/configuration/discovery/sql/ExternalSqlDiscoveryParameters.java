package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.il.configuration.discovery.AbstractExternalModelDiscoveryParameters;
import com.amdocs.aia.il.configuration.discovery.annotations.DiscoveryParameter;

import java.util.List;

public class ExternalSqlDiscoveryParameters extends AbstractExternalModelDiscoveryParameters {

    public static final String CONNECTION_STRING = "connectionString";
    public static final String DB_USER = "dbUser";
    public static final String DB_PASSWORD = "dbPassword";
    public static final String DB_TYPE = "dbType";

    @DiscoveryParameter
    private String connectionString;

    @DiscoveryParameter
    private String dbUser;

    @DiscoveryParameter
    private String dbPassword;

    @DiscoveryParameter
    private String dbType;

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
}
