package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.il.configuration.discovery.DiscoveryRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    private DatabaseProperties databaseProperties;
    private Connection connection;

    public void init(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
        connection = createConnection();
    }

    public void closeConnection() {
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

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(databaseProperties.getUrl(), databaseProperties.getUser(), databaseProperties.getPassword());
        } catch (SQLException e) {
            throw new DiscoveryRuntimeException(e);
        }
    }

    public boolean isConnectionClosed(){
        try {
            return connection.isClosed();
        } catch (SQLException sqlException) {
            LOGGER.warn("Failed to check if database connection closed", sqlException);
            return false;
        }
    }
}
