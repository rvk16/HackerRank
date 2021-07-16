package com.amdocs.aia.il.common.model.configuration.tables;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class PrimaryKeyConfigurationTest {

    @Test
    void test_pojo_structure() {
        PrimaryKeyConfiguration pkConfiguration = getMockPrimaryKeyConfiguration();
        Assertions.assertEquals(3, pkConfiguration.getColumnNames().size());
        pkConfiguration = new PrimaryKeyConfiguration();
        pkConfiguration.setColumnNames(Collections.emptyList());
        Assertions.assertEquals(0, pkConfiguration.getColumnNames().size());
    }

    private PrimaryKeyConfiguration getMockPrimaryKeyConfiguration() {
        return new PrimaryKeyConfiguration(Arrays.asList("col1", "col2", "col3"));
    }

}
