package com.amdocs.aia.il.busdeployer;

import com.amdocs.aia.il.busdeployer.configurations.RuntimeConfiguration;
import com.amdocs.aia.il.busdeployer.exception.ElementsProviderException;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerEntityStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.DataChannelEntityStore;
import com.amdocs.aia.il.common.model.DataChannelSchemaStore;
import com.amdocs.aia.il.common.model.configuration.tables.AbstractPublisherConfigurationModel;
import com.amdocs.aia.il.common.model.configuration.transformation.Transformation;
import com.amdocs.aia.il.common.model.configuration.transformation.TransformationSourceType;
import com.amdocs.aia.il.common.model.stores.SchemaStoreCategory;
import com.amdocs.aia.repo.client.ElementsProvider;
import com.amdocs.aia.repo.client.local.LocalFileSystemElementsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel.getElementTypeFor;

@Component
public class SchemaEntitiesProcessorImpl implements SchemaEntitiesProcessor {

    private final RuntimeConfiguration runtimeConfiguration;

    @Autowired
    public SchemaEntitiesProcessorImpl(RuntimeConfiguration runtimeConfiguration) {
        this.runtimeConfiguration = runtimeConfiguration;
    }

    public ElementsProvider getElementsProvider(final String repoElementsLocalPath) {
        return new LocalFileSystemElementsProvider(repoElementsLocalPath);
    }

    public List<DataChannelSchemaStore> getSchemaStores(final ElementsProvider elementsProvider) {
        return elementsProvider.getElements(ConfigurationConstants.PRODUCT_KEY, getElementTypeFor(DataChannelSchemaStore.class), DataChannelSchemaStore.class);
    }

    public List<DataChannelEntityStore> getEntityStores(final ElementsProvider elementsProvider) {
        return elementsProvider.getElements(ConfigurationConstants.PRODUCT_KEY, AbstractIntegrationLayerEntityStoreModel.getElementTypeFor(DataChannelEntityStore.class), DataChannelEntityStore.class);
    }

    private static Map<String, DataChannelSchemaStore> getDataChannelSchemaStores(final ElementsProvider elementsProvider) {
        return elementsProvider.getElements(ConfigurationConstants.PRODUCT_KEY, getElementTypeFor(DataChannelSchemaStore.class), DataChannelSchemaStore.class)
                .stream().collect(Collectors.toMap(DataChannelSchemaStore::getSchemaStoreKey, Function.identity()));
    }

    public List<Transformation> getTransformation(final ElementsProvider elementsProvider) {
        return elementsProvider.getElements(ConfigurationConstants.PRODUCT_KEY, AbstractPublisherConfigurationModel.getElementTypeFor(Transformation.class),
                Transformation.class);
    }

    @Override
    public List<String> getTopicList() {
        try {
            ElementsProvider elementsProvider = getElementsProvider(runtimeConfiguration.getRepoElementsLocalPath());
            List<DataChannelSchemaStore> schemaStores = getSchemaStores(elementsProvider);
            return schemaStores.stream()
                    .filter(schemaStore -> schemaStore.getSchemaStoreKey() != null
                            && SchemaStoreCategory.PRIVATE == schemaStore.getCategory())
                    .map(DataChannelSchemaStore::getDataChannel)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ElementsProviderException(e);
        }
    }

    @Override
    public List<String> getBulkTopicList() {
        try {
            ElementsProvider elementsProvider = getElementsProvider(runtimeConfiguration.getRepoElementsLocalPath());
            List<DataChannelEntityStore> entityStores = getEntityStores(elementsProvider);
            final Map<String, DataChannelSchemaStore> dataChannelSchemaStores = getDataChannelSchemaStores(elementsProvider);
            return entityStores.stream().filter(entityStore -> {
                DataChannelSchemaStore dataChannelSchemaStore = dataChannelSchemaStores.get(entityStore.getSchemaStoreKey());
                if (Objects.isNull(dataChannelSchemaStore)) {
                    return false;
                }
                return (SchemaStoreCategory.PRIVATE == dataChannelSchemaStore.getCategory() && !dataChannelSchemaStore.getReference());
            }).map(en ->
                    dataChannelSchemaStores.get(en.getSchemaStoreKey()).getDataChannelForEntity(en.getEntityStoreKey())
            ).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ElementsProviderException(e);
        }
    }

    @Override
    public List<String> getTransformerTopicList() {
        try {
            ElementsProvider elementsProvider = getElementsProvider(runtimeConfiguration.getRepoElementsLocalPath());
            List<DataChannelSchemaStore> schemaStores = getSchemaStores(elementsProvider);
            return schemaStores.stream()
                    .filter(schemaStore -> schemaStore.getSchemaStoreKey() != null
                            && SchemaStoreCategory.SHARED == schemaStore.getCategory())
                    .map(DataChannelSchemaStore::getLogicalSchemaKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ElementsProviderException(e);
        }
    }

    @Override
    public List<String> getContextTransformerBulkTopicList() {
        ElementsProvider elementsProvider = getElementsProvider(runtimeConfiguration.getRepoElementsLocalPath());
        List<Transformation> transformationList = getTransformation(elementsProvider);
        return transformationList.stream()
                .filter(transformation -> transformation.getSourceType() == TransformationSourceType.CONTEXT)
                .map(trans -> trans.getTargetSchemaName() + '_' + trans.getContextKey().replace("Context", ""))
                .collect(Collectors.toList());
    }
}