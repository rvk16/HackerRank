package com.amdocs.aia.il.common.model.configuration.transformation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class ModelContextEntityRelationTypeTest {

    @Test
    void test_factory_fromValue() {
        ContextEntityRelationType type = null;
        type = ContextEntityRelationType.fromValue("LEAD");
        Assertions.assertEquals("LEAD", type.toString());
        type = ContextEntityRelationType.fromValue("OTM");
        Assertions.assertEquals("OTM", type.toString());
        type = ContextEntityRelationType.fromValue("OTO");
        Assertions.assertEquals("OTO", type.toString());
        type = ContextEntityRelationType.fromValue("MTO");
        Assertions.assertEquals("MTO", type.toString());
        type = ContextEntityRelationType.fromValue("MTM");
        Assertions.assertEquals("MTM", type.toString());
        type = ContextEntityRelationType.fromValue("REF");
        Assertions.assertEquals("REF", type.toString());
        type = ContextEntityRelationType.fromValue("SOME");
        Assertions.assertNull(type);
    }

}
