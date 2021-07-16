package com.amdocs.aia.il.common.model.configuration.tables;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class AbstractTableSourceConfigurationTest {

    @Test
    void test_pojo_structure() {
        final AbstractTableSourceConfiguration configuration = getMockedAbstractTableSourceConfiguration();
        Assertions.assertEquals("type", configuration.getType());
        configuration.setType("new_type");
        Assertions.assertEquals("new_type", configuration.getType());
    }

    private AbstractTableSourceConfiguration getMockedAbstractTableSourceConfiguration() {
        return new AbstractTableSourceConfiguration("type") {
        };
    }

}
