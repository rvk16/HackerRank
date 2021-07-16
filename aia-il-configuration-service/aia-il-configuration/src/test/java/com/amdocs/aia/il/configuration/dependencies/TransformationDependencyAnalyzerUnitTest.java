package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.transformation.TransformationContextEntity;
import com.amdocs.aia.il.common.model.configuration.transformation.Transformation;
import com.amdocs.aia.il.common.model.configuration.transformation.TransformationImplementationType;
import com.amdocs.aia.il.common.model.configuration.transformation.TransformationSourceType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
public class TransformationDependencyAnalyzerUnitTest {

   @InjectMocks
    private TransformationDependencyAnalyzer transformationDependencyAnalyzer;

    @Test
    public void whenGetTransformationDependency_ShouldReturnDependency() {
        Transformation transformation = new Transformation();
        transformation.setProjectKey("aia");
        transformation.setSourceType(TransformationSourceType.CONTEXT);
        transformation.setContextKey("contextKey");
        transformation.setTargetSchemaStoreKey("TargetSchemaKeyDataChannel");
        transformation.setTargetEntityStoreKey("TargetEntityStoreKey");
        List<ElementDependency> dependencies = transformationDependencyAnalyzer.getDependencies(transformation);
        assertEquals(2, dependencies.size());
        assertEquals("INTEGRATIONLAYER_aia_Context_contextKey", dependencies.get(0).getElementId());
        assertEquals("SHARED_aia_ENS_DATA_CHANNEL_TargetSchemaKeyDataChannel_TargetEntityStoreKey", dependencies.get(1).getElementId());
        assertEquals("Context", dependencies.get(0).getElementType());
        assertEquals("ENS", dependencies.get(1).getElementType());
        assertEquals(0, dependencies.get(0).getFeatureKeys().size());
    }

    @Test
    public void whenGetTransformationDependency_ShouldReturnDependency_Reference() {
        Transformation transformation = new Transformation();
        transformation.setProjectKey("aia");
        transformation.setSourceType(TransformationSourceType.REFERENCE);
        transformation.setContextKey("REFContext");
        List<TransformationContextEntity> refEntityList = new ArrayList<>();
        refEntityList.add(createReferenceEntity("BCMAPP", "TABLE1"));
        refEntityList.add(createReferenceEntity("CRMAPP", "TABLE2"));
        transformation.setTargetSchemaStoreKey("TargetSchemaStoreKeyDataChannel");
        transformation.setTargetEntityStoreKey("EntityStoreKey");
        transformation.setReferenceSourceEntities(refEntityList);
        List<ElementDependency> dependencies = transformationDependencyAnalyzer.getDependencies(transformation);
        assertEquals(3, dependencies.size());
        assertEquals("INTEGRATIONLAYER_aia_ExternalEntity_BCMAPP_TABLE1", dependencies.get(0).getElementId());
        assertEquals("INTEGRATIONLAYER_aia_ExternalEntity_CRMAPP_TABLE2", dependencies.get(1).getElementId());
        assertEquals("SHARED_aia_ENS_DATA_CHANNEL_TargetSchemaStoreKeyDataChannel_EntityStoreKey", dependencies.get(2).getElementId());
        assertEquals("ExternalEntity", dependencies.get(0).getElementType());
        assertEquals("ExternalEntity", dependencies.get(1).getElementType());
        assertEquals("ENS", dependencies.get(2).getElementType());
    }

    /**
     * Test the flow of method getDependencies(PublisherTransformation element)
     * When the element.sourceType is PublisherTransformationSourceType.REFERENCE
     * When the element.referenceSourceEntities is not empty
     * doesn't throw any exception
     */

    @Test
    void when_getDependencies_withRefSourceType() {
        final Transformation element = getPublisherTransformation("transformation");
        final TransformationContextEntity context = createReferenceEntity("source", "context");
        element.setSourceType(TransformationSourceType.REFERENCE);
        element.setReferenceSourceEntities(Collections.singletonList(context));
        final List<ElementDependency> dependencies = transformationDependencyAnalyzer.getDependencies(element);
        assertEquals(2, dependencies.size());
    }

    /**
     * Test the flow of method getDependencies(PublisherTransformation element)
     * When the element.sourceType is PublisherTransformationSourceType.CONTEXT
     * doesn't throw any exception
     */
    @Test
    void when_getDependencies_withContextSourceType() {
        final Transformation element = getPublisherTransformation("transformation");
        element.setSourceType(TransformationSourceType.CONTEXT);
        final List<ElementDependency> dependencies = transformationDependencyAnalyzer.getDependencies(element);
        assertEquals(2, dependencies.size());
    }


    @Test
    public void whenGetTransformationDependency_ShouldReturnDependency_Cache() {
        Transformation transformation = new Transformation();
        transformation.setProjectKey("aia");
        transformation.setSourceType(TransformationSourceType.REFERENCE);
        transformation.setContextKey("REFContext");
        List<TransformationContextEntity> refEntityList = new ArrayList<>();
        refEntityList.add(createReferenceEntity("BCMAPP1", "TABLE3"));
        refEntityList.add(createReferenceEntity("CRMAPP1", "TABLE4"));
        transformation.setTargetSchemaStoreKey("CACHE");
        transformation.setTargetEntityStoreKey("EntityStoreKey");
        transformation.setReferenceSourceEntities(refEntityList);

        List<ElementDependency> dependencies = transformationDependencyAnalyzer.getDependencies(transformation);
        assertEquals(3, dependencies.size());
        assertEquals("INTEGRATIONLAYER_aia_ExternalEntity_BCMAPP1_TABLE3", dependencies.get(0).getElementId());
        assertEquals("INTEGRATIONLAYER_aia_ExternalEntity_CRMAPP1_TABLE4", dependencies.get(1).getElementId());
        assertEquals("INTEGRATIONLAYER_aia_CacheEntity_EntityStoreKey", dependencies.get(2).getElementId());
        assertEquals("ExternalEntity", dependencies.get(0).getElementType());
        assertEquals("ExternalEntity", dependencies.get(1).getElementType());
        assertEquals("CacheEntity", dependencies.get(2).getElementType());
    }


    /**
     * Test the flow of method getPublicFeatures(PublisherTransformation publisherTransformation)
     * doesn't throw any exception
     */
    @Test
    void when_getPublicFeatures_return() {
        final Transformation element = getPublisherTransformation("transformation");
        assertEquals(0, transformationDependencyAnalyzer.getPublicFeatures(element).size());
    }

    private Transformation getPublisherTransformation(final String element) {
        Transformation transformation = new Transformation();
        transformation.setProjectKey(element + "_project_key");
        transformation.setContextKey(element + "_context_key");
        transformation.setCustomGroovyScript("custom groovy script for " + element);
        transformation.setCustomScript("custom script for " + element);
        transformation.setTargetEntityStoreKey(element + "_target_entity_store_key");
        transformation.setTargetSchemaStoreKey(element + "_target_schema_store_key");
        transformation.setImplementationType(TransformationImplementationType.CUSTOM_SQL);
        return transformation;
    }

    public TransformationContextEntity createReferenceEntity(String sourceKey, String entityKey) {
        TransformationContextEntity transformationContextEntity = new TransformationContextEntity();
        transformationContextEntity.setSchemaStoreKey(sourceKey);
        transformationContextEntity.setEntityStoreKey(entityKey);
        return transformationContextEntity;
    }
}
