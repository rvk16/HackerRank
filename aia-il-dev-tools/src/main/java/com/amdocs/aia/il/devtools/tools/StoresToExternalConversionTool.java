package com.amdocs.aia.il.devtools.tools;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.extensions.typesystems.*;
import com.amdocs.aia.common.model.logical.Datatype;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.common.model.store.SharedStores;
import com.amdocs.aia.il.common.model.*;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaStoreInfo;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaStoreInfo;
import com.amdocs.aia.il.common.model.physical.csv.CsvEntityStore;
import com.amdocs.aia.il.common.model.physical.csv.CsvSchemaStore;
import com.amdocs.aia.il.common.model.publisher.PublisherEntityStore;
import com.amdocs.aia.il.common.model.publisher.PublisherSchemaStore;
import com.amdocs.aia.il.common.model.stores.SchemaStoreCategory;
import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StoresToExternalConversionTool extends AbstractConfigZipConversionTool {
    private final static Logger LOGGER = LoggerFactory.getLogger(StoresToExternalConversionTool.class);

    private final Map<String, AbstractTypeSystem> typeSystems;
    private Map<String, List<DataChannelEntityStore>> dataChannelEntityStoresBySchemaStoreKey;
    private Map<String, List<PublisherEntityStore>> publisherEntityStoresBySchemaStoreKey;
    private Map<String, SchemaStore> sharedSchemaStores;

    public StoresToExternalConversionTool(String sourceZip, String targetZip) {
        super(sourceZip, targetZip);
        this.typeSystems = Arrays.asList(
                new ProtoTypeSystem(),
                new SqlTypeSystem(),
                new LogicalTypeSystem(),
                new OracleTypeSystem(),
                new PostgreSqlTypeSystem())
                .stream()
                .collect(Collectors.toMap(typeSystem -> typeSystem.getName().toLowerCase(), Function.identity()));
    }

    @Override
    protected void doConversions() {
        loadSharedSchemaStores();
        loadEntityStores();
        convertExternalSchemas();
        deletePrivateAndExternalStoresFromShared();
        deletePhysicalStores();
    }

    @Override
    protected boolean shouldPackMetadata() {
        return false;
    }

    private void loadSharedSchemaStores() {
        sharedSchemaStores = getElementsProvider()
                .getElements(ModelConstants.SHARED_PRODUCT_KEY, SchemaStore.ELEMENT_TYPE_CODE, SchemaStore.class)
                .stream()
                .collect(Collectors.toMap(SchemaStore::getSchemaStoreKey, Function.identity()));
    }

    private void loadEntityStores() {
        dataChannelEntityStoresBySchemaStoreKey = getElementsProvider()
                .getElements(ConfigurationConstants.PRODUCT_KEY, DataChannelEntityStore.class.getSimpleName(), DataChannelEntityStore.class)
                .stream()
                .collect(Collectors.groupingBy(DataChannelEntityStore::getSchemaStoreKey));
        publisherEntityStoresBySchemaStoreKey = getElementsProvider()
                .getElements(ConfigurationConstants.PRODUCT_KEY, PublisherEntityStore.class.getSimpleName(), PublisherEntityStore.class)
                .stream()
                .collect(Collectors.groupingBy(PublisherEntityStore::getSchemaStoreKey));
    }

    private void deletePrivateAndExternalStoresFromShared() {
        Set<String> deletedSchemaKeys = new HashSet<>();
        getElementsProvider()
                .getElements(ModelConstants.SHARED_PRODUCT_KEY, SchemaStore.ELEMENT_TYPE_CODE, SchemaStore.class)
                .stream()
                .filter(schemaStore -> !schemaStore.getCategory().equals(com.amdocs.aia.common.model.store.SchemaStoreCategory.SHARED))
                .forEach(schemaStore -> {
                    deletedSchemaKeys.add(schemaStore.getSchemaStoreKey());
                    deleteElement(schemaStore);
                });
        getElementsProvider()
                .getElements(ModelConstants.SHARED_PRODUCT_KEY, EntityStore.ELEMENT_TYPE_CODE, EntityStore.class)
                .stream()
                .filter(entityStore -> deletedSchemaKeys.contains(entityStore.getSchemaStoreKey()))
                .forEach(this::deleteElement);
    }

    private void deletePhysicalStores() {
        getElementsProvider()
                .getElements(ConfigurationConstants.PRODUCT_KEY, CsvSchemaStore.ELEMENT_TYPE, CsvSchemaStore.class)
                .stream()
                .forEach(this::deleteElement);
        getElementsProvider()
                .getElements(ConfigurationConstants.PRODUCT_KEY, CsvEntityStore.ELEMENT_TYPE, CsvEntityStore.class)
                .stream()
                .forEach(this::deleteElement);
    }

    private void convertExternalSchemas() {
        final Map<String, DataChannelSchemaStore> dataChannelStores = getElementsProvider()
                .getElements(ConfigurationConstants.PRODUCT_KEY, DataChannelSchemaStore.class.getSimpleName(), DataChannelSchemaStore.class)
                .stream()
                .filter(schemaStore -> schemaStore.getCategory().equals(SchemaStoreCategory.PRIVATE))
                .collect(Collectors.toMap(DataChannelSchemaStore::getSchemaName, Function.identity()));
        final Map<String, PublisherSchemaStore> publisherStores = getElementsProvider()
                .getElements(ConfigurationConstants.PRODUCT_KEY, PublisherSchemaStore.class.getSimpleName(), PublisherSchemaStore.class)
                .stream()
                .collect(Collectors.toMap(PublisherSchemaStore::getSchemaName, Function.identity()));

        Set<String> externalSchemaKeys = Sets.union(dataChannelStores.keySet(), publisherStores.keySet());
        externalSchemaKeys.forEach(schemaKey -> {
            final DataChannelSchemaStore dataChannelSchemaStore = dataChannelStores.get(schemaKey);
            final PublisherSchemaStore publisherSchemaStore = publisherStores.get(schemaKey);
            createExternalSchemaIfNeeded(schemaKey, dataChannelSchemaStore, publisherSchemaStore);
            deleteElement(dataChannelSchemaStore);
            deleteElement(publisherSchemaStore);
        });
    }

    private boolean createExternalSchemaIfNeeded(String schemaKey, DataChannelSchemaStore dataChannelSchemaStore, PublisherSchemaStore publisherSchemaStore) {
        AbstractIntegrationLayerSchemaStoreModel source = dataChannelSchemaStore != null ? dataChannelSchemaStore : publisherSchemaStore;
        if (source.getCategory() == SchemaStoreCategory.PRIVATE) {
            ExternalSchema externalSchema = new ExternalSchema();
            externalSchema.setSchemaKey(schemaKey);
            externalSchema.setProjectKey(source.getProjectKey());
            externalSchema.setName(source.getName());
            externalSchema.setDescription(source.getDescription());
            String typeSystem = publisherSchemaStore != null ? publisherSchemaStore.getTypeSystem() : source.getTypeSystem();
            if (StringUtils.isEmpty(typeSystem)) {
                // this is a workaround. some of the stores in the test zip files do not contain a type system definition
                typeSystem = OracleTypeSystem.NAME;
            }
            externalSchema.setTypeSystem(typeSystem);
            externalSchema.setIsReference(source.getReference() != null && source.getReference());

            ExternalSchemaDataChannelInfo dataChannelInfo = new ExternalSchemaDataChannelInfo();
            // we saw that in some zip files dataChannel is missing in some schema store definitions
            dataChannelInfo.setDataChannelName(source.getDataChannel());
            if (dataChannelSchemaStore != null) {
                dataChannelInfo.setSerializationMethod(dataChannelSchemaStore.getSerializationMethod());
            }
            externalSchema.setDataChannelInfo(dataChannelInfo);
            externalSchema.setStoreInfo(createExternalSchemaStoreInfo(schemaKey));

            externalSchema.setId(SchemaConfigurationUtils.getElementId(externalSchema));
            createExternalEntities(schemaKey, typeSystem, dataChannelSchemaStore, publisherSchemaStore);
            saveElement(externalSchema);
            return true;
        } else {
            return false;
        }
    }

    private ExternalSchemaStoreInfo createExternalSchemaStoreInfo(String schemaKey) {
        final SchemaStore sharedSchemaStore = sharedSchemaStores.get(schemaKey);
        if (sharedSchemaStore != null) {
            final String storeType = sharedSchemaStore.getStoreType();
            if (storeType.equals(SharedStores.RDBMS.STORE_TYPE)) {
                return new ExternalSqlSchemaStoreInfo();
            } else if (storeType.equals(SharedStores.FileStore.STORE_TYPE)) {
                return new ExternalCsvSchemaStoreInfo();
            }
        }
        return null; // currently no other stores are relevant
    }

    private ExternalEntityStoreInfo createExternalEntityStoreInfo(String schemaKey) {
        final SchemaStore sharedSchemaStore = sharedSchemaStores.get(schemaKey);
        if (sharedSchemaStore != null) {
            final String storeType = sharedSchemaStore.getStoreType();
            if (storeType.equals(SharedStores.RDBMS.STORE_TYPE)) {
                return new ExternalSqlEntityStoreInfo();
            } else if (storeType.equals(SharedStores.FileStore.STORE_TYPE)) {
                return new ExternalCsvEntityStoreInfo();
            }
        }
        return null; // currently no other stores are relevant
    }

    private void createExternalEntities(String schemaKey, String typeSystem, DataChannelSchemaStore dataChannelSchemaStore, PublisherSchemaStore publisherSchemaStore) {
        Map<String, DataChannelEntityStore> dataChannelEntityStores = dataChannelSchemaStore != null && dataChannelEntityStoresBySchemaStoreKey.containsKey(dataChannelSchemaStore.getSchemaStoreKey()) ?
                dataChannelEntityStoresBySchemaStoreKey.get(dataChannelSchemaStore.getSchemaStoreKey())
                        .stream()
                        .collect(Collectors.toMap(DataChannelEntityStore::getEntityName, Function.identity())) :
                Collections.emptyMap();
        Map<String, PublisherEntityStore> publisherEntityStores = publisherSchemaStore != null && publisherEntityStoresBySchemaStoreKey.containsKey(publisherSchemaStore.getSchemaStoreKey()) ?
                publisherEntityStoresBySchemaStoreKey.get(publisherSchemaStore.getSchemaStoreKey())
                        .stream()
                        .collect(Collectors.toMap(PublisherEntityStore::getEntityName, Function.identity())) :
                Collections.emptyMap();

        Set<String> externalEntityKeys = Sets.union(dataChannelEntityStores.keySet(), publisherEntityStores.keySet());
        externalEntityKeys.stream().forEach(entityKey -> {
            final DataChannelEntityStore dataChannelEntityStore = dataChannelEntityStores.get(entityKey);
            final PublisherEntityStore publisherEntityStore = publisherEntityStores.get(entityKey);
            createExternalEntity(schemaKey, entityKey, typeSystem, dataChannelEntityStore, publisherEntityStore);
            deleteElement(dataChannelEntityStore);
            deleteElement(publisherEntityStore);
        });
    }

    private void createExternalEntity(String schemaKey, String entityKey, String typeSystem, DataChannelEntityStore dataChannelEntityStore, PublisherEntityStore publisherEntityStore) {
        AbstractIntegrationLayerEntityStoreModel source = publisherEntityStore != null ? publisherEntityStore : dataChannelEntityStore; // we prefer the publisher store because it also contains the source type system
        ExternalEntity externalEntity = new ExternalEntity();
        externalEntity.setSchemaKey(schemaKey);
        externalEntity.setEntityKey(entityKey);
        externalEntity.setTypeSystem(typeSystem);
        externalEntity.setProjectKey(source.getProjectKey());
        externalEntity.setName(source.getName());
        externalEntity.setDescription(source.getDescription());
        externalEntity.setSerializationId(source.getSerializationId());
        externalEntity.setReplicationPolicy(getReplicationPolicy(publisherEntityStore));
        externalEntity.setId(EntityConfigurationUtils.getElementId(externalEntity));
        externalEntity.setStoreInfo(createExternalEntityStoreInfo(schemaKey));
        externalEntity.setAttributes(source.getAttributeStores().stream().map(attributeStore -> createAttribute(attributeStore, typeSystem)).collect(Collectors.toList()));
        saveElement(externalEntity);
    }

    private ExternalEntityReplicationPolicy getReplicationPolicy(PublisherEntityStore publisherEntityStore) {
        return publisherEntityStore != null && ConfigurationConstants.SCYLLA_DB_PUBLISHER_STORE.equals(publisherEntityStore.getPublisherType()) ?
                ExternalEntityReplicationPolicy.REPLICATE :
                ExternalEntityReplicationPolicy.NO_REPLICATION;
    }

    private ExternalAttribute createAttribute(IntegrationLayerAttributeStore source, String typeSystem) {
        ExternalAttribute externalAttribute = new ExternalAttribute();
        externalAttribute.setAttributeKey(source.getAttributeStoreKey());
        externalAttribute.setName(source.getName());
        externalAttribute.setDatatype(source.getType());
        externalAttribute.setLogicalDatatype(toLogicalDatatype(source.getType(), typeSystem));
        externalAttribute.setSerializationId(source.getSerializationId());
        externalAttribute.setKeyPosition(source.getKeyPosition());
        externalAttribute.setLogicalTime(source.isLogicalTime());
        externalAttribute.setUpdateTime(source.isUpdateTime());
        externalAttribute.setRequired(source.isRequired());
        externalAttribute.setAttributeKey(source.getAttributeStoreKey());
        return externalAttribute;
    }

    private String toLogicalDatatype(String datatype, String typeSystemName) {
        final AbstractTypeSystem typeSystem = typeSystems.get(typeSystemName.toLowerCase());
        if (typeSystem == null) {
            throw new IllegalArgumentException("Unknown type system " + typeSystemName);
        }
        final Datatype logicalDatatype = typeSystem.toLogicalDatatype(datatype);
        return LogicalTypeSystem.format(logicalDatatype);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            LOGGER.error("Usage: java {} <inputRoot> <outputRoot> <relativePathsTextFile>", StoresToExternalConversionTool.class.getName());
            System.exit(1);
        }
        String inputRoot = args[0];
        String outputRoot = args[1];

        final List<String> lines = Files.readAllLines(Paths.get(args[2]));
        for(String relativePath : lines) {
            if (relativePath.isEmpty() || !relativePath.endsWith(".zip")) {
                LOGGER.info("Read empty line or invalid zip file name - quitting process");
                break;
            }
            File sourceZip = new File(inputRoot, relativePath);
            File targetZip = new File(outputRoot, relativePath);

            if (!sourceZip.exists()) {
                if (targetZip.exists()) {
                    LOGGER.info("File {} does not exist - copying target file {} to {}", sourceZip, targetZip, sourceZip);
                    FileUtils.copyFile(targetZip, sourceZip);
                } else {
                    throw new IllegalArgumentException("Source file " + sourceZip + " does not exist and cannot be copied from the target as well since target zip " + targetZip + " does not exist as well");
                }
            }
            LOGGER.info("Converting {}", relativePath);
            new StoresToExternalConversionTool(sourceZip.getAbsolutePath(), targetZip.getAbsolutePath()).convertSingleZip();
        }
    }
}
