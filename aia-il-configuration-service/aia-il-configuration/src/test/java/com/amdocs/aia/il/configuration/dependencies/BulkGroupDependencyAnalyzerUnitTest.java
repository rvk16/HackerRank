package com.amdocs.aia.il.configuration.dependencies;


import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.il.common.model.bulk.BulkGroup;
import com.amdocs.aia.il.common.model.bulk.EntityFilterRef;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class BulkGroupDependencyAnalyzerUnitTest {

    @InjectMocks
    private BulkGroupDependencyAnalyzer bulkGroupDependencyAnalyzer;

    @Test
    void whenGetBulkGroupDependency_shouldReturnDependency() {
        BulkGroup bulkGroup = createBulkGroup();
        List<ElementDependency> dependencies = bulkGroupDependencyAnalyzer.getDependencies(bulkGroup);
        assertEquals(2, dependencies.size());
        ElementDependency entity1 = dependencies.stream().filter(elementDependency -> elementDependency.getElementName().equals("entity1")).findAny().orElse(null);
        assertEquals(2, entity1.getFeatureKeys().size());
        assertNotNull(entity1.getFeatureKeys().stream().filter(s -> s.equals("Query1")).findAny().orElse(null));
        ElementDependency schemaKeyDependency = dependencies.stream().filter(elementDependency -> elementDependency.getElementName().equals("schemaKey")).findAny().orElse(null);
        assertNotNull(schemaKeyDependency);

    }

    @Test
    void whenGetBulkGroupPublicFeatures_shouldReturnEmptyList() {
        BulkGroup bulkGroup = createBulkGroup();
        List<ElementPublicFeature> publicFeatures = bulkGroupDependencyAnalyzer.getPublicFeatures(bulkGroup);
        assertEquals(publicFeatures, Collections.emptyList());
    }

    private BulkGroup createBulkGroup() {

        BulkGroup bulkGroup = new BulkGroup();
        bulkGroup.setKey("bulkGroupKey");
        bulkGroup.setSchemaKey("schemaKey");
        bulkGroup.setProjectKey("aia");

        Set<EntityFilterRef> entityFilters = new HashSet<>();
        EntityFilterRef entityFilterRef1 = new EntityFilterRef();
        entityFilterRef1.setEntityFilterKey("Query1");
        entityFilterRef1.setEntityKey("entity1");
        EntityFilterRef entityFilterRef2 = new EntityFilterRef();
        entityFilterRef2.setEntityFilterKey("Query2");
        entityFilterRef2.setEntityKey("entity1");
        entityFilters.add(entityFilterRef1);
        entityFilters.add(entityFilterRef2);
        bulkGroup.setEntityFilters(entityFilters);

        return bulkGroup;
    }

}
