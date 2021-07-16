package com.amdocs.aia.il.common.model.configuration.transformation;

import com.amdocs.aia.common.model.transformation.TransformationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({MockitoExtension.class})
class ContextTest {

    @Test
    void test_pojo_structure() {
        Context context = getMockedPublicPublisherContext();
        assertEquals(Context.ELEMENT_TYPE,"Context");
        assertEquals("context_key", context.getContextKey());
        assertEquals(0, context.getContextEntities().size());
    }

    @Test
    void test_toSharedElement() {
        Context context = getMockedPublicPublisherContext();
        TransformationContext transformationContext = context.toSharedElement();
        assertEquals("context_id", transformationContext.getSourceElementId());
        assertEquals("context_key", transformationContext.getContextKey());
        assertEquals(Transformation.TRANSFORMATION_TYPE, transformationContext.getTransformationType());
        assertEquals(0, transformationContext.getContextEntities().size());
    }

    private static Context getMockedPublicPublisherContext() {
        Context context = new Context();
        context.setId("context_id");
        context.setContextKey("context_key");
        context.setContextEntities(Collections.emptyList());
        return context;
    }
}