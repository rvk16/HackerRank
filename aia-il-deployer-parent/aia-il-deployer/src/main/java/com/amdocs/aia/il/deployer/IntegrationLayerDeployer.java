package com.amdocs.aia.il.deployer;

import com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.publisher.PublisherSchemaStore;
import com.amdocs.aia.il.deployer.exception.DeployerException;
import com.amdocs.aia.il.deployer.logs.LogMsg;
import com.amdocs.aia.il.deployer.properties.RuntimeConfiguration;
import com.amdocs.aia.il.deployer.utils.ExecutionReport;
import com.amdocs.aia.repo.client.ElementsProvider;
import com.amdocs.aia.repo.client.local.LocalFileSystemElementsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_ABOUT_TO_CREATE_TABLE;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_ALTER_TABLE;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_CREATED_TABLE;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_FAILED_DEPLOYING_TABLE;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_IL_DEPLOYER_FINISH;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_LIST_OF_CONFIGURED_SCHEMA;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_NO_TABLES_CONFIGURED;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_NUMBER_OF_LOADED_SCHEMA_STORE;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_SKIPPED_TABLE_NO_CHANGE;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_STARTING_IL_DEPLOYER;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_TABLE_ALREADY_EXISTS;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_TABLE_ALTERED;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_TABLE_CREATED;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_TABLE_FAILED;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_TABLE_UNCHANGED;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.MSG_TOTAL_OF_CONFIGURED_TABLES;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.RELATIONAL;

@Component
public class IntegrationLayerDeployer {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationLayerDeployer.class);

    private final RuntimeConfiguration runtimeConfiguration;
    private final SourceTableDeployerProvider tableDeployerProvider;
    private final ExecutionReport executionReport;
    private final List<String> schemaRelational = new ArrayList<>();

    public IntegrationLayerDeployer(SourceTableDeployerProvider tableDeployerProvider, ExecutionReport executionReport, RuntimeConfiguration runtimeConfiguration) {
        this.runtimeConfiguration = runtimeConfiguration;
        this.tableDeployerProvider = tableDeployerProvider;
        this.executionReport = executionReport;
    }

    public ElementsProvider getElementsProvider(final String repoElementsLocalPath) {
        return new LocalFileSystemElementsProvider(repoElementsLocalPath);
    }

    public List<PublisherSchemaStore> getSchemaStores(final ElementsProvider elementsProvider) {
        final List<PublisherSchemaStore> schemaStores = elementsProvider.getElements(ConfigurationConstants.PRODUCT_KEY, AbstractIntegrationLayerSchemaStoreModel.getElementTypeFor(PublisherSchemaStore.class), PublisherSchemaStore.class);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(MSG_NUMBER_OF_LOADED_SCHEMA_STORE), schemaStores.size());
        }
        return schemaStores;
    }

    public void execute() {

        LOGGER.info(LogMsg.getMessage(MSG_STARTING_IL_DEPLOYER)); // NOSONAR
        ElementsProvider elementsProvider = getElementsProvider(runtimeConfiguration.getRepoElementsLocalPath());
        List<PublisherSchemaStore> schemaStores = getSchemaStores(elementsProvider);
        schemaStores.stream().map(PublisherSchemaStore::getSchemaStoreKey).collect(Collectors.toList());

        if (schemaStores.isEmpty()) {
            LOGGER.info(LogMsg.getMessage(MSG_NO_TABLES_CONFIGURED)); // NOSONAR
        } else {
            schemaStores.sort(Comparator.comparing(PublisherSchemaStore::getSchemaName));
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(LogMsg.getMessage(MSG_LIST_OF_CONFIGURED_SCHEMA), schemaStores.stream().map(PublisherSchemaStore::getSchemaName).collect(Collectors.joining(",")));
            }
            List<String> schemaNames = schemaStores.stream().filter(publisherSchemaStore -> !publisherSchemaStore.getReference()).map(PublisherSchemaStore::getSchemaName).collect(Collectors.toList());
            for (String schema : schemaNames) {
                schemaRelational.add(schema + RELATIONAL);
            }

            schemaNames.forEach(this::deploySingleTable);
            schemaRelational.forEach(this::deploySingleTable);
            LOGGER.info(LogMsg.getMessage(MSG_IL_DEPLOYER_FINISH));
            logReport();
        }
    }

    private void deploySingleTable(String tableName) {
        final SourceTableDeployer tableDeployer = tableDeployerProvider.getTableDeployer(tableName);
        try {
            if (tableDeployer.exists(tableName)) {
                LOGGER.info(LogMsg.getMessage(MSG_TABLE_ALREADY_EXISTS), tableName);
                if (tableDeployer.alterIfNeeded()) {
                    executionReport.addAlteredTable(tableName);
                    LOGGER.info(LogMsg.getMessage(MSG_ALTER_TABLE), tableName);
                } else {
                    executionReport.addUnchangedTable(tableName);
                    LOGGER.info(LogMsg.getMessage(MSG_SKIPPED_TABLE_NO_CHANGE), tableName);
                }
            } else {
                LOGGER.info(LogMsg.getMessage(MSG_ABOUT_TO_CREATE_TABLE), tableName);
                tableDeployer.createCommonSchema();
                executionReport.addCreatedTable(tableName);
                LOGGER.info(LogMsg.getMessage(MSG_CREATED_TABLE), tableName);
            }
        } catch (DeployerException de) {
            executionReport.addFailedTable(tableName);
            LOGGER.error(LogMsg.getMessage(MSG_FAILED_DEPLOYING_TABLE), tableName, de);
        }
    }

    private void logReport() {
        executionReport.getCreatedTables().forEach(tableName -> LOGGER.info(LogMsg.getMessage(MSG_TABLE_CREATED), tableName));
        executionReport.getAlteredTables().forEach(tableName -> LOGGER.info(LogMsg.getMessage(MSG_TABLE_ALTERED), tableName));
        executionReport.getFailedTables().forEach(tableName -> LOGGER.info(LogMsg.getMessage(MSG_TABLE_FAILED), tableName));
        executionReport.getUnchangedTables().forEach(tableName -> LOGGER.info(LogMsg.getMessage(MSG_TABLE_UNCHANGED), tableName));

        LOGGER.info(LogMsg.getMessage(MSG_TOTAL_OF_CONFIGURED_TABLES),
                executionReport.getTotalTableCount(),
                executionReport.getCreatedTables().size(),
                executionReport.getAlteredTables().size(),
                executionReport.getFailedTables().size(),
                executionReport.getUnchangedTables().size());
    }

    public int getExitCode() {
        return executionReport.getFailedTables().isEmpty() ? 0 : 1;
    }
}