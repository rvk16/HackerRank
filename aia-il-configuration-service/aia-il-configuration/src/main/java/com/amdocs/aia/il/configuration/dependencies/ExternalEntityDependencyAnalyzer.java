package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SharedStores;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.configuration.ConfigurationUtils;
import com.amdocs.aia.il.common.model.external.Availability;
import com.amdocs.aia.il.common.model.external.ExternalAttribute;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalSchema;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExternalEntityDependencyAnalyzer {


       private final DataChannelStoreType dataChannelStoreType;

    public ExternalEntityDependencyAnalyzer( DataChannelStoreType dataChannelStoreType) {
        this.dataChannelStoreType = dataChannelStoreType;
    }


    public List<ElementDependency> getDependencies(ExternalEntity element) {
        List<ElementDependency> dependencies = new ArrayList<>();
        String elementId = ConfigurationUtils.getElementId(element.getProjectKey(), ExternalSchema.class, element.getSchemaKey());
        dependencies.add(ElementDependency.create(elementId, ExternalSchema.ELEMENT_TYPE, element.getSchemaKey(), null));

        if(element.getAvailability().equals(Availability.SHARED)){
            String entityStoreId = ModelUtils.generateGlobalUniqueIdForEntityStore(ModelConstants.SHARED_PRODUCT_KEY, element.getProjectKey(), SharedStores.DataChannel.STORE_TYPE, dataChannelStoreType.generateSchemaStoreKeyForLogical(element.getSubjectAreaKey()), element.getEntityKey());
            String entityStoreType = ModelUtils.getFullyQualifiedType(EntityStore.ELEMENT_TYPE_CODE,"");
            dependencies.add(ElementDependency.create(entityStoreId, entityStoreType, element.getEntityKey(), element.getAttributes().stream().map(ExternalAttribute::getAttributeKey).collect(Collectors.toSet())));
        }
        return dependencies;
    }


    public List<ElementPublicFeature> getPublicFeatures(ExternalEntity externalEntity) {
        List<ElementPublicFeature> features = new ArrayList<>();
        features.addAll(externalEntity.getAttributes().stream().map(this::toPublicFeature).collect(Collectors.toList()));
        if (externalEntity.getCollectionRules() != null && externalEntity.getCollectionRules().getFilters() != null) {
            features.addAll(externalEntity.getCollectionRules().getFilters().stream().map(entityFilter -> new ElementPublicFeature(entityFilter.getFilterKey(), entityFilter.getFilterKey())).collect(Collectors.toList()));
        }
        return features;
    }

    protected ElementPublicFeature toPublicFeature(ExternalAttribute attribute) {
        return new ElementPublicFeature(attribute.getAttributeKey(), attribute.getName());
    }


}
