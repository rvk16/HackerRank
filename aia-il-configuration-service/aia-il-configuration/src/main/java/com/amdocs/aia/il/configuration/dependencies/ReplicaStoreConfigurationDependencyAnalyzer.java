package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.common.model.repo.FeatureVisibility;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.EntityStoreRef;
import com.amdocs.aia.common.model.store.SharedStores;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ReplicaStoreConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ReplicaStoreSourceConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.TableSourceConfiguration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReplicaStoreConfigurationDependencyAnalyzer implements AbstractConfigurationModelDependencyAnalyzer<ReplicaStoreConfiguration> {

    public List<ElementDependency> getDependencies(final ReplicaStoreConfiguration element) {
        final List<ElementDependency> dependencies = new ArrayList<>();
        String projectKey = element.getProjectKey();
        final List<TableSourceConfiguration> sources = element.getSources();

        if(sources!=null && !sources.isEmpty()){
            EntityStoreRef entityStoreRef = ((ReplicaStoreSourceConfiguration)sources.get(0)).getSourceEntityStore();
            String entityStoreId = ModelUtils.generateGlobalUniqueIdForEntityStore(ModelConstants.SHARED_PRODUCT_KEY, projectKey, SharedStores.DataChannel.STORE_TYPE, entityStoreRef.getSchemaStoreKey(), entityStoreRef.getEntityStoreKey());
            String entityStoreType = ModelUtils.getFullyQualifiedType(EntityStore.ELEMENT_TYPE_CODE, SharedStores.DataChannel.STORE_TYPE);
            dependencies.add(ElementDependency.create(entityStoreId, entityStoreType, entityStoreRef.getEntityStoreKey(),
                    element.getColumns().stream().map(ColumnConfiguration :: getColumnName).collect(Collectors.toSet())));
        }
        return dependencies;
    }

    public List<ElementPublicFeature> getPublicFeatures(final ReplicaStoreConfiguration tableConfiguration) {
        return tableConfiguration.getColumns().stream().map(ReplicaStoreConfigurationDependencyAnalyzer::toPublicFeature).collect(Collectors.toList());
    }

    private static ElementPublicFeature toPublicFeature(final ColumnConfiguration columnConfiguration) {
        return new ElementPublicFeature(columnConfiguration.getColumnName(), columnConfiguration.getColumnName(), FeatureVisibility.EVERYONE);
    }
}