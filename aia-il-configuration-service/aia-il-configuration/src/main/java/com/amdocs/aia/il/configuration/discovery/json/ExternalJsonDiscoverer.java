package com.amdocs.aia.il.configuration.discovery.json;

import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.external.kafka.*;
import com.amdocs.aia.il.configuration.discovery.AbstractExternalModelDiscoverer;
import com.amdocs.aia.il.configuration.discovery.DiscoveryFilesRepository;
import com.amdocs.aia.il.configuration.service.external.SerializationIDAssigner;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.inject.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Discovers external json-based schema and entities from swagger file
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExternalJsonDiscoverer extends AbstractExternalModelDiscoverer<ExternalJsonDiscoveryParameters> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalJsonDiscoverer.class);

    private final ExternalJsonDiscoveryConfigurationProperties properties;
    private final SwaggerProcessor processor;

    protected ExternalJsonDiscoverer(AiaRepositoryOperations repositoryOperations, ExternalJsonDiscoveryConfigurationProperties properties, Provider<SwaggerProcessor> processorProvider, DiscoveryFilesRepository discoveryFilesRepository, SerializationIDAssigner serializationIDAssigner) {
        super(repositoryOperations, serializationIDAssigner);
        this.properties = properties;
        this.processor = processorProvider.get();
        this.discoveryFilesRepository = discoveryFilesRepository;
    }

    private final DiscoveryFilesRepository discoveryFilesRepository;

    @Override
    protected String getTypeSystem() {
        return LogicalTypeSystem.NAME;
    }

    @Override
    protected void initDiscovery() {
        final String filename = getParameters().getFilename();
        String fileContent;
        try {
            InputStream inputStream = discoveryFilesRepository.downloadFile(filename);
            fileContent = IOUtils.toString(inputStream);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
        processor.init(getSchemaType(), filename, fileContent, getParameters().getAvailability());
    }

    @Override
    protected void enrichSchema() {
        final ExternalSchema schema = getSchema();
        schema.setOrigin(getParameters().getFilename());
        schema.setDescription(processor.getServiceDescription());
    }

    @Override
    protected void discoverEntities() {
        processor.process();
        processor.getProcessedEntities().values().stream()
                .sorted(Comparator.comparing(ProcessedEntityInfo::getEntityKey))
                .forEach(this::addEntity);
    }


    private ExternalAttribute createAttribute(ProcessedAttributeInfo attributeInfo) {
        ExternalAttribute attribute = new ExternalAttribute();
        attribute.setAttributeKey(attributeInfo.getAttributeKey());
        attribute.setName(StringUtils.hasText(attributeInfo.getTitle()) ? attributeInfo.getTitle() : attributeInfo.getAttributeKey());
        attribute.setDescription(attributeInfo.getDescription());
        attribute.setDatatype(attributeInfo.getDatatype());
        attribute.setLogicalDatatype(attribute.getDatatype());
        attribute.setKeyPosition(null);
        attribute.setLogicalTime(false);
        attribute.setUpdateTime(false);
        attribute.setRequired(false);
        attribute.setStoreInfo(createAttributeStoreInfo(attributeInfo.getFormattedJsonPath()));
        attribute.setOrigin(getSchema().getOrigin());
        return attribute;
    }

    private ExternalKafkaAttributeStoreInfo createAttributeStoreInfo(String jsonPath) {
        final ExternalKafkaAttributeStoreInfo storeInfo = new ExternalKafkaAttributeStoreInfo();
        storeInfo.setJsonPath(jsonPath);
        return storeInfo;
    }

    private void addEntity(ProcessedEntityInfo entityInfo) {
        String entityKey = entityInfo.getFinalEntityKey();
        LOGGER.info("Adding entity {}", entityKey);
        final ExternalEntity entity = createEntity(entityKey);
        entity.setDescription(entityInfo.getDescription());

        entity.setAttributes(entityInfo.getProcessedAttributes()
                .values().stream()
                .map(this::createAttribute)
                .collect(Collectors.toList()));
        getSerializationIDAssigner().autoAssignAttributesSerializationIDs(entity);
        getConsumer().acceptEntity(entity);
    }

    @Override
    public Class<ExternalJsonDiscoveryParameters> getParametersClass() {
        return ExternalJsonDiscoveryParameters.class;
    }

    @Override
    protected ExternalSchemaStoreInfo createSchemaStoreInfo() {
        // NOTE: if, in the future, we will support other store types (e.g. JSON over FILES), we will need to support creating the appropriate store info class
        final ExternalKafkaSchemaStoreInfo storeInfo = new ExternalKafkaSchemaStoreInfo();
        storeInfo.setDefaultDateFormat(properties.getDefaultDateFormat());
        return storeInfo;
    }

    @Override
    protected ExternalSchemaCollectionRules createSchemaCollectionRules() {
        final ExternalKafkaSchemaCollectionRules collectionRules = new ExternalKafkaSchemaCollectionRules();
        final ExternalSchemaType schemaType = getSchemaType();
        collectionRules.setOngoingChannel(schemaType.getSupportedOngoingChannels().get(0));
        collectionRules.setInitialLoadChannel(schemaType.getSupportedInitialLoadChannels().get(0));
        collectionRules.setReplayChannel(schemaType.getSupportedReplayChannels().get(0));
        collectionRules.setInitialLoadRelativeURL(null);
        collectionRules.setPartialLoadRelativeURL(null);
        return collectionRules;
    }

    @Override
    protected ExternalEntityCollectionRules createEntityCollectionRules(String entityKey) {
        return new ExternalKafkaEntityCollectionRules();
    }

    @Override
    protected ExternalEntityStoreInfo createEntityStoreInfo(String entityKey) {
        final ExternalKafkaEntityStoreInfo storeInfo = new ExternalKafkaEntityStoreInfo();
        final ProcessedEntityInfo entityInfo = processor.findProcessedEntityInfoByFinalKey(entityKey);
        storeInfo.setRelativePaths(entityInfo.getRelativePaths().stream().collect(Collectors.joining(",")));
        final String mergedNodes = entityInfo.getMergedNodes().isEmpty() ?
                null :
                entityInfo.getMergedNodes().stream().collect(Collectors.joining(","));
        storeInfo.setMergedNodes(mergedNodes);
        return storeInfo;
    }
}
