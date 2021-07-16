package com.amdocs.aia.il.common.model.configuration.tables;

import com.amdocs.aia.common.model.store.EntityStoreRef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReplicaStoreSourceConfigurationTest {

    @Test
    void test_pojo_structure() {
        ReplicaStoreSourceConfiguration tableSourceConfiguration = new ReplicaStoreSourceConfiguration();
        final EntityStoreRef entityStoreRef = new EntityStoreRef();
        tableSourceConfiguration.setSourceEntityStore(entityStoreRef);
        Assertions.assertEquals(entityStoreRef, tableSourceConfiguration.getSourceEntityStore());
        tableSourceConfiguration = new ReplicaStoreSourceConfiguration(entityStoreRef);
        Assertions.assertEquals(entityStoreRef, tableSourceConfiguration.getSourceEntityStore());
    }

}
