package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.common.model.store.EntityStoreRef;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnDatatype;
import com.amdocs.aia.il.common.model.configuration.tables.ReplicaStoreConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ReplicaStoreSourceConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.TableSourceConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ReplicaStoreConfigurationDependencyAnalyzerUnitTest {

    @InjectMocks
    private ReplicaStoreConfigurationDependencyAnalyzer replicaStoreConfigurationDependencyAnalyzer;

    /**
     * Test the flow of method getDependencies(PublisherTableConfiguration element)
     * When element.getSources() is not empty
     * doesn't throw any exception
     * returns not empty list
     */
    @Test
    void when_getDependencies_withSourcesNotEmpty() {
        final ReplicaStoreConfiguration replicaStoreConfiguration = getPublisherTableConfiguration("element");
        replicaStoreConfiguration.setSources(Arrays.asList(getTableSourceConfiguration("source")));
        replicaStoreConfiguration.setColumns(Arrays.asList(getColumnConfiguration("1")));
        List<ElementDependency> dependencies = replicaStoreConfigurationDependencyAnalyzer.getDependencies(replicaStoreConfiguration);

        assertEquals(1, dependencies.size());
        for (ElementDependency dep : dependencies) {
            assertEquals(1, dep.getFeatureKeys().size());
        }
    }

    /**
     * Test the flow of method getPublicFeatures(PublisherTableConfiguration element)
     * When element.getSources() is not empty
     * doesn't throw any exception
     * returns not empty list
     */
    @Test
    void when_getPublicFeatures_withNotEmptyColumns() {
        final ReplicaStoreConfiguration replicaStoreConfiguration = getPublisherTableConfiguration("element");
        replicaStoreConfiguration.setColumns(new ArrayList<>());
        final int count = 5;
        for (int i = 0; i < count; i++) {
            replicaStoreConfiguration.getColumns().add(getColumnConfiguration(Integer.toString(i)));
        }
        List<ElementPublicFeature> features = replicaStoreConfigurationDependencyAnalyzer.getPublicFeatures(replicaStoreConfiguration);
        assertEquals(count, features.size());
    }

    private ReplicaStoreConfiguration getPublisherTableConfiguration(final String element) {
        ReplicaStoreConfiguration replicaStoreConfiguration = new ReplicaStoreConfiguration();
        replicaStoreConfiguration.setTableName(element + "_table_name");
        replicaStoreConfiguration.setProjectKey(element + "_project_key");
        replicaStoreConfiguration.setColumns(Collections.emptyList());
        replicaStoreConfiguration.setSources(Collections.emptyList());
       // replicaStoreConfiguration.setIndexes(Collections.emptyList());
        return replicaStoreConfiguration;
    }

    private TableSourceConfiguration getTableSourceConfiguration(final String source) {
        ReplicaStoreSourceConfiguration tableSourceConfiguration = new ReplicaStoreSourceConfiguration();
        tableSourceConfiguration.setType(source + "_type");
        tableSourceConfiguration.setSourceEntityStore(getEntityStoreRef());
        return tableSourceConfiguration;
    }

    private EntityStoreRef getEntityStoreRef() {
        EntityStoreRef entityStoreRef = new EntityStoreRef();
        entityStoreRef.setEntityStoreKey("entity_store_key");
        entityStoreRef.setSchemaStoreKey("schema_store_key");
        return entityStoreRef;
    }

    private ColumnConfiguration getColumnConfiguration(final String index) {
        ColumnConfiguration configuration = new ColumnConfiguration();
        configuration.setColumnName("column_" + index + "_name");
        configuration.setDatatype(new ColumnDatatype());
        return configuration;
    }

}
