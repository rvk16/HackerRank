package com.amdocs.aia.il.common.model.configuration.tables;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ColumnDataTypeTest {

    @Test
    void test_pojo_structure() {
        ColumnDatatype columnDatatype = new ColumnDatatype();
        columnDatatype.setSqlType("sql_type");
        Assertions.assertEquals("sql_type", columnDatatype.getSqlType());
    }

}
