package com.amdocs.aia.il.configuration.discovery.csv;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.external.csv.*;
import com.amdocs.aia.il.configuration.discovery.AbstractExternalModelDiscoverer;
import com.amdocs.aia.il.configuration.discovery.DiscoveryFilesRepository;
import com.amdocs.aia.il.configuration.discovery.DiscoveryUtils;
import com.amdocs.aia.il.configuration.discovery.csv.ExternalCsvDiscoveryConfigurationProperties.DatatypePattern;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.service.external.SerializationIDAssigner;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExternalCsvDiscoverer extends AbstractExternalModelDiscoverer<ExternalCsvDiscoveryParameters> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalCsvDiscoverer.class);

    private final ExternalCsvDiscoveryConfigurationProperties properties;
    private final DiscoveryFilesRepository discoveryFilesRepository;

    public ExternalCsvDiscoverer(AiaRepositoryOperations repositoryOperations, ExternalCsvDiscoveryConfigurationProperties properties, DiscoveryFilesRepository discoveryFilesRepository, SerializationIDAssigner serializationIDAssigner) {
        super(repositoryOperations, serializationIDAssigner);
        this.properties = properties;
        this.discoveryFilesRepository = discoveryFilesRepository;
    }

    @Override
    protected ExternalSchemaStoreInfo createSchemaStoreInfo() {
        ExternalCsvSchemaStoreInfo externalCsvSchemaStoreInfo = new ExternalCsvSchemaStoreInfo();
        externalCsvSchemaStoreInfo.setDefaultColumnDelimiter(getColumnDelimiter());
        externalCsvSchemaStoreInfo.setDefaultDateFormat(properties.getDefaultDateFormat());
        return externalCsvSchemaStoreInfo;
    }


    @Override
    protected ExternalSchemaCollectionRules createSchemaCollectionRules() {
        ExternalCsvSchemaCollectionRules collectionRules = new ExternalCsvSchemaCollectionRules();
        collectionRules.setOngoingChannel(CollectorChannelType.FILES);
        collectionRules.setInitialLoadChannel(CollectorChannelType.FILES);
        collectionRules.setReplayChannel(CollectorChannelType.FILES);
        collectionRules.setDefaultInvalidFilenameAction(properties.getDefaultInvalidFilenameAction());
        collectionRules.setInitialLoadRelativeURL(null);
        collectionRules.setPartialLoadRelativeURL(null);
        return collectionRules;
    }

    @Override
    public Class<ExternalCsvDiscoveryParameters> getParametersClass() {
        return ExternalCsvDiscoveryParameters.class;
    }

    @Override
    protected String getTypeSystem() {
        return LogicalTypeSystem.NAME;
    }

    @Override
    protected void discoverEntities() {
        getParameters().getFilenames().forEach(this::processSingleFile);
    }

    private void processSingleFile(String filename) {
        LOGGER.info("Performing External Entity csv discovery for file {}", filename);
        final String[] header = readHeader(filename);
        final String entityKey = getEntityKeyFromFilename(filename);
        ExternalEntity entity = createEntity(entityKey);
        entity.setAttributes(Arrays.stream(header).map(this::createAttribute).collect(Collectors.toList()));
        getSerializationIDAssigner().autoAssignAttributesSerializationIDs(entity);
        getConsumer().acceptEntity(entity);
    }

    @Override
    protected ExternalEntityCollectionRules createEntityCollectionRules(String entityKey) {
        return new ExternalCsvEntityCollectionRules();
    }

    @Override
    protected ExternalEntityStoreInfo createEntityStoreInfo(String entityKey) {
        ExternalCsvEntityStoreInfo storeInfo = new ExternalCsvEntityStoreInfo();
        storeInfo.setHeader(true);
        storeInfo.setFileNameFormat(properties.getDefaultFilenameFormat());
        storeInfo.setColumnDelimiter(getColumnDelimiter());
        storeInfo.setDateFormat(getParameters().getDateFormat() != null ? getParameters().getDateFormat() : properties.getDefaultDateFormat());
        return storeInfo;
    }

    private char getColumnDelimiter() {
        return getParameters().getColumnDelimiter() != null ? getParameters().getColumnDelimiter() : properties.getDefaultColumnDelimiter();
    }

    private ExternalAttribute createAttribute(String columnName) {
        ExternalAttribute attribute = new ExternalAttribute();
        attribute.setAttributeKey(ModelUtils.toAllowedLocalKey(columnName));
        attribute.setName(DiscoveryUtils.getNameFromKey(attribute.getAttributeKey()));
        attribute.setDatatype(getDatatype(columnName));
        attribute.setLogicalDatatype(attribute.getDatatype());
        attribute.setKeyPosition(null);
        attribute.setLogicalTime(false);
        attribute.setUpdateTime(false);
        attribute.setRequired(false);
        attribute.setStoreInfo(new ExternalCsvAttributeStoreInfo());
        attribute.setOrigin(getSchema().getOrigin());
        return attribute;
    }

    private String getDatatype(String columnName) {
        final Optional<DatatypePattern> datatypePattern = properties.getAttributeDatatypePatterns().stream()
                .filter(p -> columnName.matches(p.getPattern()))
                .findFirst();
        if (datatypePattern.isPresent()) {
            return datatypePattern.get().getDatatype();
        } else {
            return properties.getDefaultDatatype();
        }
    }

    private String getEntityKeyFromFilename(String filename) {
        final String baseName = FilenameUtils.getBaseName(filename);
        return ModelUtils.toAllowedLocalKey(baseName);
    }

    private String[] readHeader(String filename) {
        final LineIterator lineIterator = getLineIterator(filename);
        if (!lineIterator.hasNext()) {
            throw new ApiException(
                    AiaApiException.AiaApiHttpCodes.BAD_RERQUEST,
                    AiaApiMessages.GENERAL.DISCOVERY_MISSING_CSV_HEADER,
                    filename);
        }
        final String headerLine = lineIterator.nextLine();
        lineIterator.close();
        return StringUtils.split(headerLine, getColumnDelimiter());
    }

    private LineIterator getLineIterator(String filename) {
        final InputStream inputStream = discoveryFilesRepository.downloadFile(filename);
        try {
            return IOUtils.lineIterator(inputStream, null);
        } catch (Exception e) {
            throw new IllegalStateException("Failed opening file " + filename + " for reading", e);
        }
    }

}
