package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.external.sql.*;
import com.amdocs.aia.il.configuration.discovery.AbstractExternalModelDiscoverer;
import com.amdocs.aia.il.configuration.discovery.DiscoveryUtils;
import com.amdocs.aia.il.configuration.service.external.SerializationIDAssigner;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExternalSqlDiscoverer extends AbstractExternalModelDiscoverer<ExternalSqlDiscoveryParameters> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalSqlDiscoverer.class);

    private final ExternalSqlDiscoveryConfigurationProperties properties;
    private final DatabaseIntrospector databaseIntrospector;
    private final ConnectionManager connectionManager;

    public ExternalSqlDiscoverer(AiaRepositoryOperations repositoryOperations, ExternalSqlDiscoveryConfigurationProperties properties,
                                 DatabaseIntrospector databaseIntrospector, ConnectionManager connectionManager, SerializationIDAssigner serializationIDAssigner) {
        super(repositoryOperations, serializationIDAssigner);
        this.properties = properties;
        this.databaseIntrospector = databaseIntrospector;
        this.connectionManager = connectionManager;
    }

    @Override
    protected ExternalSchemaStoreInfo createSchemaStoreInfo() {
        ExternalSqlSchemaStoreInfo externalSqlSchemaStoreInfo = new ExternalSqlSchemaStoreInfo();
        //externalSqlSchemaStoreInfo.setDatabaseType(properties.());
        return externalSqlSchemaStoreInfo;
    }


    @Override
    protected ExternalSchemaCollectionRules createSchemaCollectionRules() {
        ExternalSqlSchemaCollectionRules collectionRules = new ExternalSqlSchemaCollectionRules();
        collectionRules.setOngoingChannel(CollectorChannelType.SQL);
        collectionRules.setInitialLoadChannel(CollectorChannelType.SQL);
        collectionRules.setReplayChannel(CollectorChannelType.SQL);
        collectionRules.setInitialLoadRelativeURL(null);
        collectionRules.setPartialLoadRelativeURL(null);
        return collectionRules;
    }

    @Override
    public Class<ExternalSqlDiscoveryParameters> getParametersClass() {
        return ExternalSqlDiscoveryParameters.class;
    }

    @Override
    protected String getTypeSystem() {
        return LogicalTypeSystem.NAME;
    }

    @Override
    protected void discoverEntities() {
        databaseIntrospector.getTablesMetadata(connectionManager.getConnection()).forEach(this::addEntity);
    }

    @Override
    protected ExternalEntityCollectionRules createEntityCollectionRules(String entityKey) {
        return new ExternalSqlEntityCollectionRules();
    }

    @Override
    protected ExternalEntityStoreInfo createEntityStoreInfo(String entityKey) {
        return new ExternalSqlEntityStoreInfo();
    }

    private void addEntity(TableInfo tableInfo) {
        String entityKey = tableInfo.getTableName();
        LOGGER.info("Adding entity {}", entityKey);
        final ExternalEntity entity = createEntity(entityKey);
        entity.setAttributes(tableInfo.getColumns()
                .stream()
                .map(columnInfo -> {
                    int keyPosition = tableInfo.getPrimaryKeyInfo().getColumnNames().indexOf(columnInfo.getName());
                    return createAttribute(columnInfo, keyPosition);
                })
                .collect(Collectors.toList()));
        getSerializationIDAssigner().autoAssignAttributesSerializationIDs(entity);
        getConsumer().acceptEntity(entity);
    }

    private ExternalAttribute createAttribute(ColumnInfo columnInfo, int keyPosition) {
        ExternalAttribute attribute = new ExternalAttribute();
        attribute.setAttributeKey(ModelUtils.toAllowedLocalKey(columnInfo.getName()));
        attribute.setName(DiscoveryUtils.getNameFromKey(attribute.getAttributeKey()));
        attribute.setDatatype(getDatatype(columnInfo.getDatatype(), columnInfo.getColumnSize()));
        attribute.setLogicalDatatype(attribute.getDatatype());
        attribute.setKeyPosition(keyPosition > -1 ? keyPosition : null);
        attribute.setLogicalTime(false);
        attribute.setUpdateTime(false);
        attribute.setRequired(false);
        attribute.setStoreInfo(new ExternalSqlAttributeStoreInfo());
        attribute.setOrigin(getSchema().getOrigin());
        return attribute;
    }

    private String getDatatype(int dataType, int columnSize) {
        String result;
        switch (dataType) {
            case Types.CHAR:
                result = String.format("%s%s", "CHAR", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.VARCHAR:
                result = String.format("%s%s", "VARCHAR", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.LONGVARCHAR:
                result = String.format("%s%s", "LONGVARCHAR", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.NUMERIC:
                result = String.format("%s%s", "NUMERIC", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.DECIMAL:
                result = String.format("%s%s", "DECIMAL", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.BIT:
                result = String.format("%s%s", "BIT", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.TINYINT:
                result = String.format("%s%s", "TINYINT", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.SMALLINT:
                result = String.format("%s%s", "SMALLINT", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.INTEGER:
                result = String.format("%s%s", "INTEGER", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.BIGINT:
                result = String.format("%s%s", "BIGINT", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.REAL:
                result = String.format("%s%s", "REAL", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.FLOAT:
                result = String.format("%s%s", "FLOAT", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.DOUBLE:
                result = String.format("%s%s", "DOUBLE", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.BINARY:
                result = String.format("%s%s", "BINARY", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.VARBINARY:
                result = String.format("%s%s", "VARBINARY", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.LONGVARBINARY:
                result = String.format("%s%s", "LONGVARBINARY", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.DATE:
                result = String.format("%s%s", "DATE", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.TIME:
                result = String.format("%s%s", "TIME", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            case Types.TIMESTAMP:
                result = String.format("%s%s", "TIMESTAMP", (columnSize > 0 ? "(" + columnSize + ")" : ""));
                break;
            default:
                result = properties.getDefaultDatatype();
                break;
        }

        return result;
    }
}
