package com.amdocs.aia.il.common.model.configuration.transformation;

import com.amdocs.aia.common.model.transformation.EntityTransformation;
import com.amdocs.aia.common.model.transformation.TransformationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({MockitoExtension.class})
class TransformationTest {

    @Test
    void test_pojo_structure() {
        Transformation transformation = getPublisherTransformation();
        assertEquals("target_schema_key", transformation.getTargetSchemaStoreKey());
        assertEquals("target_entity_key", transformation.getTargetEntityStoreKey());
        assertEquals("context_key", transformation.getContextKey());
        assertEquals(0, transformation.getReferenceSourceEntities().size());
        assertEquals(TransformationImplementationType.CUSTOM_SQL, transformation.getImplementationType());
        assertEquals("custom_script", transformation.getCustomScript());
        assertEquals("deletion_keys", transformation.getCustomScriptForDeletionKeys());
        assertEquals(TransformationSourceType.CONTEXT, transformation.getSourceType());
        assertEquals("custom_groovy_script", transformation.getCustomGroovyScript());
        assertEquals("custom_groovy_deletion_keys", transformation.getCustomGroovyScriptForDeletionKeys());
        assertEquals("context_key", transformation.getSharedContextKey());
    }

    @Test
    void test_extractReferenceSharedContext() {
        Transformation transformation = getPublisherTransformation();
        transformation.setSourceType(TransformationSourceType.REFERENCE);
        transformation.setImplementationType(TransformationImplementationType.CUSTOM_GROOVY);
        TransformationContext transformationContext = transformation.extractReferenceSharedContext();
        assertEquals("REFERENCE", transformationContext.getContextKey());
        assertEquals(Transformation.TRANSFORMATION_TYPE, transformationContext.getTransformationType());
        assertEquals(0, transformationContext.getContextEntities().size());
    }

    @Test
    void test_toSharedElement() {
        Transformation publisherTransformation = getPublisherTransformation();
        EntityTransformation transformation = publisherTransformation.toSharedElement();
        assertEquals(Transformation.TRANSFORMATION_TYPE, transformation.getTransformationType());
        assertEquals("target_schema_key", transformation.getTargetSchemaStoreKey());
        assertEquals("target_entity_key", transformation.getTargetEntityStoreKey());
    }

    private static Transformation getPublisherTransformation() {
        Transformation transformation = new Transformation();
        transformation.setTargetSchemaStoreKey("target_schema_key");
        transformation.setTargetEntityStoreKey("target_entity_key");
        transformation.setContextKey("context_key");
        transformation.setReferenceSourceEntities(Collections.emptyList());
        transformation.setImplementationType(TransformationImplementationType.CUSTOM_SQL);
        transformation.setCustomScript("custom_script");
        transformation.setCustomScriptForDeletionKeys("deletion_keys");
        transformation.setSourceType(TransformationSourceType.CONTEXT);
        transformation.setCustomGroovyScript("custom_groovy_script");
        transformation.setCustomGroovyScriptForDeletionKeys("custom_groovy_deletion_keys");
        return transformation;
    }
}