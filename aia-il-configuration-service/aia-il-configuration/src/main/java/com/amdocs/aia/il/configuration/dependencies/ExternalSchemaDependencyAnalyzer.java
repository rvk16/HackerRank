package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.common.model.store.SharedStores;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.external.Availability;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ExternalSchemaDependencyAnalyzer {

    private final DataChannelStoreType dataChannelStoreType;

    public ExternalSchemaDependencyAnalyzer(DataChannelStoreType dataChannelStoreType) {
        this.dataChannelStoreType = dataChannelStoreType;
    }

    public List<ElementDependency> getDependencies(ExternalSchema element) {
        if(element.getAvailability().equals(Availability.SHARED)) {
            List<ElementDependency> dependencies = new ArrayList<>();
            String schemaStoreId = ModelUtils.generateGlobalUniqueId(ModelConstants.SHARED_PRODUCT_KEY, element.getProjectKey(),SchemaStore.ELEMENT_TYPE_CODE, SharedStores.DataChannel.STORE_TYPE,dataChannelStoreType.generateSchemaStoreKeyForLogical(element.getSubjectAreaKey()));
            String schemaStoreType = ModelUtils.getFullyQualifiedType(SchemaStore.ELEMENT_TYPE_CODE,"");
            dependencies.add(ElementDependency.create(schemaStoreId, schemaStoreType, element.getSchemaKey(), Collections.emptySet()));

            return dependencies;
        }else{
            return Collections.emptyList();
        }
    }
    public List<ElementPublicFeature> getPublicFeatures(ExternalSchema externalEntity) {
        return Collections.emptyList();
    }

}