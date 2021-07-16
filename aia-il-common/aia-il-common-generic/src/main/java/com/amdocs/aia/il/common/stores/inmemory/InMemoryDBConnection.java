package com.amdocs.aia.il.common.stores.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class InMemoryDBConnection implements Serializable {

    private static final long serialVersionUID = 6597718192078774459L;
    private static Connection connection;

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDBConnection.class);

    public static Connection getConnection(){
        if(connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:hsqldb:mem:testdb"); //NOSONAR
            } catch (SQLException e) {
                LOGGER.error("Error creating In Memory SQL connection : {}", e.getMessage());
            }
        }

        return connection;
    }
}
