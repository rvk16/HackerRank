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
import com.amdocs.aia.il.common.model.configuration.ConfigurationUtils;
import com.amdocs.aia.il.common.model.configuration.transformation.Context;
import com.amdocs.aia.il.common.model.configuration.transformation.Transformation;
import com.amdocs.aia.il.common.model.configuration.transformation.TransformationSourceType;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TransformationDependencyAnalyzer implements AbstractConfigurationModelDependencyAnalyzer<Transformation> {


    @Override
    public List<ElementDependency> getDependencies(Transformation element) {
        List<ElementDependency> dependencies = new ArrayList<>();
        String projectKey = element.getProjectKey();
       //source
        if(TransformationSourceType.CONTEXT.name().equals(element.getSourceType().name())) {
            String contextId = ConfigurationUtils.getElementId(projectKey, Context.class, element.getContextKey());
            dependencies.add(ElementDependency.create(contextId, Context.ELEMENT_TYPE,element.getContextKey(), null).mayUseUnspecifiedFeatures(true));
        }
        if(TransformationSourceType.REFERENCE.name().equals(element.getSourceType().name())) {
            if(element.getReferenceSourceEntities() != null && !element.getReferenceSourceEntities().isEmpty()){
                element.getReferenceSourceEntities().forEach(refEntity -> {
                    String elementIdForEntity = EntityConfigurationUtils.getExternalEntityId(element.getProjectKey(), refEntity.getSchemaStoreKey(), refEntity.getEntityStoreKey());
                    dependencies.add(ElementDependency.create(elementIdForEntity, ExternalEntity.ELEMENT_TYPE, refEntity.getEntityStoreKey(), null));
               });
            }
        }

        if(element.getTargetSchemaStoreKey()!= null && element.getTargetSchemaStoreKey().equals("CACHE"))
        {
           String elementIdForCacheEntity = EntityConfigurationUtils.getCacheEntityId(element.getProjectKey(), element.getTargetEntityStoreKey());
           dependencies.add(ElementDependency.create(elementIdForCacheEntity, CacheEntity.ELEMENT_TYPE, element.getTargetEntityStoreKey(), null));
        }else{

           String entityStoreId = ModelUtils.generateGlobalUniqueIdForEntityStore(ModelConstants.SHARED_PRODUCT_KEY, projectKey, SharedStores.DataChannel.STORE_TYPE,  element.getTargetSchemaStoreKey(), element.getTargetEntityStoreKey());

           String entityStoreType = ModelUtils.getFullyQualifiedType(EntityStore.ELEMENT_TYPE_CODE,"");
           dependencies.add(ElementDependency.create(entityStoreId, entityStoreType, element.getTargetEntityStoreKey(), Collections.emptySet())); // need to confirm significance of name attribute

         }


        return dependencies;

    }

    @Override
    public List<ElementPublicFeature> getPublicFeatures(Transformation transformation) {
        return Collections.emptyList();
    }



}