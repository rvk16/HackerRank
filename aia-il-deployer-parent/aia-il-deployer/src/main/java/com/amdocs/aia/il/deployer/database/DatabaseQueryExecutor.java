package com.amdocs.aia.il.deployer.database;

import com.amdocs.aia.il.deployer.logs.LogMsg;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_FINISHING_EXECUTING_BATCH_OF_STATEMENT;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_PREPARING_EXECUTE_BATCH;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_PREPARING_STATEMENT;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_QUERY_FAILED;

@Component
public class DatabaseQueryExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseQueryExecutor.class);

    protected final ConnectionManager connectionManager;
    protected CqlSession session;

    @Autowired
    public DatabaseQueryExecutor(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        session = connectionManager.getSession();
    }

    protected void executeBatch(final List<String> queries) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(MSG_PREPARING_EXECUTE_BATCH), queries.size());
        }
        try {
            for (final String query : queries) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(LogMsg.getMessage(MSG_PREPARING_STATEMENT), query);
                }
                PreparedStatement ps = session.prepare(query);
                session.execute(ps.getQuery());
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(LogMsg.getMessage(MSG_FINISHING_EXECUTING_BATCH_OF_STATEMENT), queries.size());
            }
        } catch (Exception e) {
            LOGGER.error(LogMsg.getMessage(MSG_QUERY_FAILED), e);
        }
    }
}