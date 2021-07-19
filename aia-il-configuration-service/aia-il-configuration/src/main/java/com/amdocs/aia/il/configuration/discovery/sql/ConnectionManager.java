package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.il.configuration.discovery.DiscoveryRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@Lazy
public class ConnectionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    private final DatabaseProperties databaseProperties;
    private Connection connection;

    public ConnectionManager(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }


    @PostConstruct
    public void init() {
        connection = createConnection();
    }

    @PreDestroy
    public void destroy() {
        try {
            if (connection!= null) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.warn("Failed closing database connection", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection!= null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DiscoveryRuntimeException("failed closing connection", e);
        }
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(databaseProperties.getUrl(), databaseProperties.getUser(), databaseProperties.getPassword());
        } catch (SQLException e) {
            throw new DiscoveryRuntimeException(e);
        }
    }
}
