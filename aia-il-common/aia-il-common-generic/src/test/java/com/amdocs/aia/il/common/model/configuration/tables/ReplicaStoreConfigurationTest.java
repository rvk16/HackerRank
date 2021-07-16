package com.amdocs.aia.il.common.model.configuration.tables;

import com.amdocs.aia.common.model.repo.ElementVisibility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class ReplicaStoreConfigurationTest {

    @Test
    void test_pojo_structure() {
        Assertions.assertEquals("ReplicaStoreConfiguration", ReplicaStoreConfiguration.ELEMENT_TYPE);
        ReplicaStoreConfiguration replicaStoreConfiguration = getMockPublisherTableConfiguration();
        Assertions.assertEquals("table_name", replicaStoreConfiguration.getTableName());
        Assertions.assertEquals(0, replicaStoreConfiguration.getColumns().size());
        Assertions.assertEquals(0, replicaStoreConfiguration.getSources().size());
        //Assertions.assertEquals(0, replicaStoreConfiguration.getIndexes().size());
        //Assertions.assertEquals(TableWriteMode.UPSERT, replicaStoreConfiguration.getWriteMode());
        Assertions.assertNotNull(replicaStoreConfiguration.getPrimaryKey());
        Assertions.assertEquals(ElementVisibility.EVERYONE, replicaStoreConfiguration.getVisibility());
    }

    private ReplicaStoreConfiguration getMockPublisherTableConfiguration() {
        ReplicaStoreConfiguration replicaStoreConfiguration = new ReplicaStoreConfiguration();
        replicaStoreConfiguration.setTableName("table_name");
        replicaStoreConfiguration.setColumns(Collections.emptyList());
        replicaStoreConfiguration.setSources(Collections.emptyList());
        replicaStoreConfiguration.setPrimaryKey(new PrimaryKeyConfiguration());
       // replicaStoreConfiguration.setIndexes(Collections.emptyList());
//        replicaStoreConfiguration.setWriteMode(TableWriteMode.UPSERT);
        return replicaStoreConfiguration;
    }

}
