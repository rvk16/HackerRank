package com.amdocs.aia.il.common.model.configuration.tables;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class ColumnConfigurationTest {

    @Test
    void test_pojo_structure() {
        final ColumnConfiguration columnConfiguration = new ColumnConfiguration();
        columnConfiguration.setDatatype(new ColumnDatatype());
        columnConfiguration.setColumnName("columnName");
        columnConfiguration.setLogicalTime(Boolean.TRUE);
        Assertions.assertEquals("columnName", columnConfiguration.getColumnName());
        Assertions.assertTrue(columnConfiguration.isLogicalTime());
        Assertions.assertNotNull(columnConfiguration.getDatatype());
    }

}
