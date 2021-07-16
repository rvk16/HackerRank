package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.logical.LogicalEntity;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;
import com.amdocs.aia.il.common.model.Relation;
import com.amdocs.aia.il.configuration.shared.AiaSharedProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class EntityReferentialIntegrityDependencyAnalyzer implements AbstractEntityReferentialIntegrityDependencyAnalyzer {

    @Autowired
    private AiaSharedProxy aiaSharedProxy;

    public List<ElementDependency> getDependencies(EntityReferentialIntegrity element) {
        if (element.getRelations() == null || element.getRelations().isEmpty()) {
            return Collections.emptyList();
        }
        List<ElementDependency> dependencies = new ArrayList<>();
        dependencies.add(createEntityReferentialIntegrityLogicalEntityDependency(element));
        for (Relation relation : element.getRelations()) {
            dependencies.add(createEntityReferentialIntegrityRelatedLogicalEntityDependency(element.getProjectKey(), relation.getParentSchemaKey(), relation.getParentEntityKey(), Collections.singleton(relation.getParentAttributeKey())));
        }
        return dependencies;
    }

    private ElementDependency createEntityReferentialIntegrityLogicalEntityDependency(EntityReferentialIntegrity m) {
        Optional<LogicalEntity> logicalEntity = aiaSharedProxy.searchLogicalEntityByEntityKeyAndSchemaKey(m.getProjectKey(), m.getLogicalEntityKey(), m.getLogicalSchemaKey());
        Set<String> attributeKeys = m.getRelations().stream().map(Relation::getAttributeKey).collect(Collectors.toSet());

        ElementDependency elementDependency = null;
        if(logicalEntity.isPresent()){
            elementDependency = ElementDependency.create(logicalEntity.get().getId(), logicalEntity.get().getFullyQualifiedType(), logicalEntity.get().getName(), attributeKeys);
            elementDependency.setMayUseUnspecifiedFeatures(true);
        }
        return elementDependency;

    }

    private ElementDependency createEntityReferentialIntegrityRelatedLogicalEntityDependency(String projectKey, String logicalSchemaKey, String logicalEntityKey, Set<String> perentAttributes) {
        Optional<LogicalEntity> logicalEntity = aiaSharedProxy.searchLogicalEntityByEntityKeyAndSchemaKey(projectKey, logicalEntityKey, logicalSchemaKey);
        ElementDependency elementDependency = null;
        if(logicalEntity.isPresent()){
            elementDependency = ElementDependency.create(logicalEntity.get().getId(), logicalEntity.get().getFullyQualifiedType(), logicalEntity.get().getName(), perentAttributes);
            elementDependency.setMayUseUnspecifiedFeatures(true);
        }
        return elementDependency;
    }

}