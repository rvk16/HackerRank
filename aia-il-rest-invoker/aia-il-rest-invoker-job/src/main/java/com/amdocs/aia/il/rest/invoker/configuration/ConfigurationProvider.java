package com.amdocs.aia.il.rest.invoker.configuration;

import com.amdocs.aia.common.model.logical.LogicalEntity;
import com.amdocs.aia.il.common.model.bulk.BulkGroup;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.repo.client.ElementsProvider;
import com.amdocs.aia.repo.client.local.LocalFileSystemElementsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConfigurationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationProvider.class);

    private static final String INTEGRATIONLAYER = "INTEGRATIONLAYER";
    private static final String SHARED = "SHARED";

    private final InvokerConfiguration configuration;

    private Map<String, ExternalSchema> externalSchemas;
    private Map<String, BulkGroup> bulkGroups;

    private boolean hasSharedData;
    @Inject
    public ConfigurationProvider(final InvokerConfiguration configuration) {
        this.configuration = configuration;
    }

    @PostConstruct
    public void init() {
        final ElementsProvider elementsProvider = getElementsProvider(configuration.getRepoElementsLocalPath());
        externalSchemas = getExternalSchemaConfigurations(elementsProvider);
        bulkGroups = getBulkGroupConfigurations(elementsProvider);
    }

    private static Map<String, ExternalSchema> getExternalSchemaConfigurations(final ElementsProvider elementsProvider) {
        final List<ExternalSchema> elements = elementsProvider.getElements("INTEGRATIONLAYER", ExternalSchema.class.getSimpleName() , ExternalSchema.class);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Number of loaded ExternalSchemas: {}", elements.size());
        }
        return elements.stream().collect(Collectors.toMap(ExternalSchema::getSchemaKey, Function.identity()));
    }

    private static Map<String, BulkGroup> getBulkGroupConfigurations(final ElementsProvider elementsProvider) {
        final List<BulkGroup> elements = elementsProvider.getElements("INTEGRATIONLAYER", BulkGroup.class.getSimpleName(), BulkGroup.class);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Number of loaded BulkGroup: {}", elements.size());
        }
        return elements.stream().collect(Collectors.toMap(BulkGroup::getKey, Function.identity()));
    }

    /**
     * Gets elements provider. If the configuration is empty, retries until timeout.
     *
     * @param repoElementsLocalPath the path to the configuration repository
     * @return elements provider
     */
    private ElementsProvider getElementsProvider(final String repoElementsLocalPath) {
        ElementsProvider elementsProvider = new LocalFileSystemElementsProvider(repoElementsLocalPath);
        hasSharedData = !elementsProvider.getElements(INTEGRATIONLAYER, LogicalEntity.ELEMENT_TYPE_CODE, LogicalEntity.class).isEmpty();
        int i = 0;
        while (!hasSharedData && i++ < 5) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Empty configuration, retrying: {}", i);
            }
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) { // NOSONAR
                throw new RuntimeException(e); // NOSONAR
            }
            elementsProvider = new LocalFileSystemElementsProvider(repoElementsLocalPath);
            hasSharedData = !elementsProvider.getElements(SHARED, LogicalEntity.ELEMENT_TYPE_CODE, LogicalEntity.class).isEmpty();
        }
        if (!hasSharedData && LOGGER.isWarnEnabled()) {
            LOGGER.warn("Missing configuration, after {} retries", i);
        }
        return elementsProvider;
    }

    public boolean hasSharedData() {
        return hasSharedData;
    }


    public Map<String, ExternalSchema> getExternalSchemas() {
        return externalSchemas;
    }

    public void setExternalSchemas(Map<String, ExternalSchema> externalSchemas) {
        this.externalSchemas = externalSchemas;
    }

    public Map<String, BulkGroup> getBulkGroups() {
        return bulkGroups;
    }

    public void setBulkGroups(Map<String, BulkGroup> profiles) {
        this.bulkGroups = profiles;
    }
}