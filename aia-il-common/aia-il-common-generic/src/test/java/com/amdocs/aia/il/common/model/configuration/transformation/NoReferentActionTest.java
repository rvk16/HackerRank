package com.amdocs.aia.il.common.model.configuration.transformation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class NoReferentActionTest {

    @Test
    void test_factory_fromValue() {
        NoReferentAction action = null;
        action = NoReferentAction.fromValue("MANDATORY");
        Assertions.assertEquals("MANDATORY", action.toString());
        action = NoReferentAction.fromValue("OPTIONAL");
        Assertions.assertEquals("OPTIONAL", action.toString());
        action = NoReferentAction.fromValue("MANDATORY_PUBLISH");
        Assertions.assertEquals("MANDATORY_PUBLISH", action.toString());
        action = NoReferentAction.fromValue("SOME");
        Assertions.assertNull(action);
    }

}
