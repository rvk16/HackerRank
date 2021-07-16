package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.store.EntityStoreRef;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ReplicaStoreConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ReplicaStoreSourceConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.TableSourceConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
public class ReplicaStoreDependencyAnalyzerUnitTest {
    @InjectMocks
    private ReplicaStoreConfigurationDependencyAnalyzer replicaStoreConfigurationDependencyAnalyzer;

    @Test
    public void whenGetDependency_ShouldReturnDependency(){
        ReplicaStoreConfiguration replicaStoreConfiguration = new ReplicaStoreConfiguration();
        List<TableSourceConfiguration> sources = new ArrayList<>();
        sources.add(new ReplicaStoreSourceConfiguration(new EntityStoreRef("SchemaStoreKey","EntityStoreKey")));
        List<ColumnConfiguration> columns = new ArrayList<>();
        columns.add(createColumn("columnName1"));
        columns.add(createColumn("columnName2"));
        columns.add(createColumn("columnName3"));
        replicaStoreConfiguration.setColumns(columns);
        replicaStoreConfiguration.setProjectKey("aia");
        replicaStoreConfiguration.setSources(sources);

        List<ElementDependency> dependencies = replicaStoreConfigurationDependencyAnalyzer.getDependencies(replicaStoreConfiguration);
        assertEquals(1,dependencies.size());
        assertEquals("SHARED_aia_ENS_DATA_CHANNEL_SchemaStoreKey_EntityStoreKey",dependencies.get(0).getElementId());
        assertEquals("EntityStoreKey",dependencies.get(0).getElementName());
        assertEquals("ENS_DATA_CHANNEL",dependencies.get(0).getElementType());
        assertEquals(3,dependencies.get(0).getFeatureKeys().size());

    }

    @Test
    public void whenGetDependency_shouldReturnDependency_NoSource(){
        ReplicaStoreConfiguration replicaStoreConfiguration = new ReplicaStoreConfiguration();
        List<TableSourceConfiguration> sources = new ArrayList<>();
        replicaStoreConfiguration.setProjectKey("aia");
        replicaStoreConfiguration.setSources(sources);

        List<ElementDependency> dependencies = replicaStoreConfigurationDependencyAnalyzer.getDependencies(replicaStoreConfiguration);
        assertEquals(0,dependencies.size());
    }

    private ColumnConfiguration createColumn(String name) {
        ColumnConfiguration columnConfiguration = new ColumnConfiguration();
        columnConfiguration.setColumnName(name);
        return columnConfiguration;
    }
}
