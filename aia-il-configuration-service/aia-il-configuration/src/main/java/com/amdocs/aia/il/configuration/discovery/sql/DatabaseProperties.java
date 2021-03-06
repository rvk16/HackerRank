package com.amdocs.aia.il.configuration.discovery.sql;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class DatabaseProperties {
    @NotNull
    private String url;
    @NotNull
    private String user;
    @NotNull
    private String password;
    @NotNull
    private String dbType;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
}
