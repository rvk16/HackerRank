package com.amdocs.aia.il.deployer;

import com.amdocs.aia.il.deployer.database.ConnectionManager;
import com.amdocs.aia.il.deployer.database.DatabaseQueryExecutor;
import com.amdocs.aia.il.deployer.logs.LogMsg;
import com.amdocs.aia.il.deployer.query.QueryGenerator;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_ADDING_ATTRIBUTES_TO_TABLE;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_NEW_ATTRIBUTES_OF_TABLE;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_NO_NEW_ATTRIBUTES_CONFIGURED;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.RELATIONAL;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SourceTableDeployer extends DatabaseQueryExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceTableDeployer.class);

    private final QueryGenerator queryGenerator;
    private final List<String> queries = new ArrayList<>();
    private String schemaName;

    @Autowired
    public SourceTableDeployer(final ConnectionManager connectionManager, final QueryGenerator queryGenerator) {
        super(connectionManager);
        this.queryGenerator = queryGenerator;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public boolean exists(final String schemaName) {
        final Optional<KeyspaceMetadata> keyspace = session.getMetadata().getKeyspace(connectionManager.getKeyspace());
        return keyspace.filter(keyspaceMetadata -> keyspaceMetadata.getTable(schemaName).isPresent()).isPresent();
    }

    public void createCommonSchema() {
        if (schemaName.endsWith(RELATIONAL)) {
            queries.add(queryGenerator.buildCreateSchemaRelationStatement(schemaName));
        } else {
            queries.add(queryGenerator.buildCreateStatement(schemaName));
        }
        executeBatch(queries);
    }

    public boolean alterIfNeeded() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(MSG_NEW_ATTRIBUTES_OF_TABLE), schemaName);
        }
        if (queries.isEmpty()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(LogMsg.getMessage(MSG_NO_NEW_ATTRIBUTES_CONFIGURED), schemaName);
            }
            return false;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(MSG_ADDING_ATTRIBUTES_TO_TABLE), schemaName);
        }
        executeBatch(queries);
        return true;
    }
}