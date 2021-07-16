package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SharedStores;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.EntityConfigurationUtils;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import com.amdocs.aia.il.common.model.configuration.transformation.Context;
import com.amdocs.aia.il.common.model.configuration.transformation.ContextEntity;
import com.amdocs.aia.il.common.model.configuration.transformation.ContextEntityRelationType;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContextDependencyAnalyzer implements AbstractConfigurationModelDependencyAnalyzer<Context> {


    private final DataChannelStoreType dataChannelStoreType;

    public ContextDependencyAnalyzer(DataChannelStoreType dataChannelStoreType) {
        this.dataChannelStoreType = dataChannelStoreType;
    }


    @Override
    public List<ElementDependency> getDependencies(Context element) {
        if (element.getContextEntities() != null && !element.getContextEntities().isEmpty()) {
            List<ElementDependency> dependencies = new ArrayList<>();

            element.getContextEntities().stream().forEach(contextEntity -> {
                if(!contextEntity.getSchemaStoreKey().equals( "CACHE") ) {
                    if (!ContextEntityRelationType.REF.equals(contextEntity.getRelationType())) {
                        //external
                        String elementIdForEntity = EntityConfigurationUtils.getExternalEntityId(element.getProjectKey(), contextEntity.getSchemaStoreKey(), contextEntity.getEntityStoreKey());
                        dependencies.add(ElementDependency.create(elementIdForEntity, ExternalEntity.ELEMENT_TYPE, contextEntity.getEntityStoreKey(), null));
                    }else{
                        //ref

                        String entityStoreId = ModelUtils.generateGlobalUniqueIdForEntityStore(ModelConstants.SHARED_PRODUCT_KEY, element.getProjectKey(), SharedStores.DataChannel.STORE_TYPE, dataChannelStoreType.generateSchemaStoreKeyForLogical(contextEntity.getSchemaStoreKey()), contextEntity.getEntityStoreKey());
                        String entityStoreType = ModelUtils.getFullyQualifiedType(EntityStore.ELEMENT_TYPE_CODE,"");
                        dependencies.add(ElementDependency.create(entityStoreId, entityStoreType, contextEntity.getEntityStoreKey(), Collections.emptySet()));
                    }
                }else{
                    //cache
                    String elementIdForCacheEntity = EntityConfigurationUtils.getCacheEntityId(element.getProjectKey(), contextEntity.getEntityStoreKey());
                    dependencies.add(ElementDependency.create(elementIdForCacheEntity, CacheEntity.ELEMENT_TYPE, contextEntity.getEntityStoreKey(), null));
                }


             });
            return dependencies;

        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public List<ElementPublicFeature> getPublicFeatures(Context context) {
        List<ElementPublicFeature> features = new ArrayList<>();
        features.addAll(context.getContextEntities().stream().map(this::toPublicFeatureContextEntity).collect(Collectors.toList()));
        return features;
    }

    protected ElementPublicFeature toPublicFeatureContextEntity(ContextEntity contextEntity) {
        return new ElementPublicFeature(contextEntity.getSourceAlias()+"-"+contextEntity.getEntityStoreKey(),contextEntity.getSourceAlias()+"-"+contextEntity.getEntityStoreKey());
    }




}