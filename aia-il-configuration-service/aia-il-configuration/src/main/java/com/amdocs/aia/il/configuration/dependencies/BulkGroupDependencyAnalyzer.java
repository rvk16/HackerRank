package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.il.common.model.EntityConfigurationUtils;
import com.amdocs.aia.il.common.model.configuration.ConfigurationUtils;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.bulk.BulkGroup;
import com.amdocs.aia.il.common.model.bulk.EntityFilterRef;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class BulkGroupDependencyAnalyzer {


    public List<ElementDependency> getDependencies(BulkGroup element) {
        List<ElementDependency> dependencies = new ArrayList<>();
        String elementId = ConfigurationUtils.getElementId(element.getProjectKey(), ExternalSchema.class, element.getSchemaKey());
        dependencies.add(ElementDependency.create(elementId, ExternalSchema.class.getSimpleName(), element.getSchemaKey(), null));
        Map<String, List<EntityFilterRef>> filtersByEntityKey = element.getEntityFilters().stream().collect(Collectors.groupingBy(EntityFilterRef::getEntityKey));
        dependencies.addAll(filtersByEntityKey.entrySet().stream()
                .map(entry -> createElementDependencyInExternalEntity(element.getProjectKey(), element.getSchemaKey(), entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));
        return dependencies;

    }

    private ElementDependency createElementDependencyInExternalEntity(String projectKey, String schemaKey, String entityKey, List<EntityFilterRef> filters) {
        String elementId = EntityConfigurationUtils.getExternalEntityId(projectKey, schemaKey, entityKey);
        Set<String> filterKeys = filters.stream().map(EntityFilterRef::getEntityFilterKey).filter(Objects::nonNull).collect(Collectors.toSet());
        return ElementDependency.create(elementId, ExternalEntity.ELEMENT_TYPE, entityKey, filterKeys);
    }

    public List<ElementPublicFeature> getPublicFeatures(BulkGroup bulkGroup) {
        return Collections.emptyList();
    }
}
