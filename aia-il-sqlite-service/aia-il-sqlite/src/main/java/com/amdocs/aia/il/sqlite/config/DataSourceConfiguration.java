package com.amdocs.aia.il.sqlite.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

@ConfigurationProperties(prefix = "sqlite")
public class DataSourceConfiguration {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Bean
    DataSource dataSource() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        return dataSource;
    }
}
