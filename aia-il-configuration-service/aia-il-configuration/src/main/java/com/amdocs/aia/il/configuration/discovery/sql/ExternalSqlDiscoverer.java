package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.SqlTypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.TypeSystemFactory;
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

import javax.inject.Provider;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExternalSqlDiscoverer extends AbstractExternalModelDiscoverer<ExternalSqlDiscoveryParameters> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalSqlDiscoverer.class);

    private final ExternalSqlDiscoveryConfigurationProperties properties;
    private final DatabaseIntrospector databaseIntrospector;
    private ConnectionManager connectionManager;
    private final TypeSystemFactory typeSystemFactory;

    public ExternalSqlDiscoverer(AiaRepositoryOperations repositoryOperations, ExternalSqlDiscoveryConfigurationProperties properties,
                                 DatabaseIntrospector databaseIntrospector, SerializationIDAssigner serializationIDAssigner, Provider<ConnectionManager> connectionManagerProvider, TypeSystemFactory typeSystemFactory) {
        super(repositoryOperations, serializationIDAssigner);
        this.properties = properties;
        this.databaseIntrospector = databaseIntrospector;
        this.connectionManager = connectionManagerProvider.get();
        this.typeSystemFactory = typeSystemFactory;
    }


    @Override
    protected void initDiscovery() {
        DatabaseProperties databaseProperties = new DatabaseProperties();
        databaseProperties.setUser(getParameters().getDbUser());
        databaseProperties.setPassword(getParameters().getDbPassword());
        databaseProperties.setUrl(getParameters().getConnectionString());
        databaseProperties.setDbType(getParameters().getDbType());
        connectionManager.init(databaseProperties);
    }

    @Override
    protected void discoveryEnded(boolean isSuccessful) {
        connectionManager.closeConnection();
    }

    @Override
    protected ExternalSchemaStoreInfo createSchemaStoreInfo() {
        ExternalSqlSchemaStoreInfo externalSqlSchemaStoreInfo = new ExternalSqlSchemaStoreInfo();
        externalSqlSchemaStoreInfo.setDatabaseType(getParameters().getDbType());
        return externalSqlSchemaStoreInfo;
    }


    @Override
    protected ExternalSchemaCollectionRules createSchemaCollectionRules() {
        ExternalSqlSchemaCollectionRules collectionRules = new ExternalSqlSchemaCollectionRules();
        collectionRules.setOngoingChannel(CollectorChannelType.ATTUNITY);
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
        return SqlTypeSystem.NAME;
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
                    int keyPosition = tableInfo.getPrimaryKeyInfo() == null? -1 : tableInfo.getPrimaryKeyInfo().getColumnNames().indexOf(columnInfo.getName());
                    return createAttribute(columnInfo, keyPosition);
                })
                .collect(Collectors.toList()));
        getSerializationIDAssigner().autoAssignAttributesSerializationIDs(entity);
        getConsumer().acceptEntity(entity);
    }

    private ExternalAttribute createAttribute(ColumnInfo columnInfo, Integer keyPosition) {
        ExternalAttribute attribute = new ExternalAttribute();
        attribute.setAttributeKey(ModelUtils.toAllowedLocalKey(columnInfo.getName()));
        attribute.setName(DiscoveryUtils.getNameFromKey(attribute.getAttributeKey()));
        String sqlDatatype = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(columnInfo, properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        attribute.setDatatype(sqlDatatype);
        attribute.setLogicalDatatype(LogicalTypeSystem.format(typeSystemFactory.getTypeSystemForKey(SqlTypeSystem.NAME).toLogicalDatatype(sqlDatatype)));
        attribute.setKeyPosition(keyPosition > -1 ? keyPosition : null);
        attribute.setLogicalTime(false);
        attribute.setUpdateTime(false);
        attribute.setRequired(keyPosition != -1);
        attribute.setStoreInfo(new ExternalSqlAttributeStoreInfo());
        attribute.setOrigin(getSchema().getOrigin());
        return attribute;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
