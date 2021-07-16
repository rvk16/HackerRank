package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.il.common.model.configuration.transformation.Transformation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransformationRepositoryUnitTest {

    @Spy
    @InjectMocks
    private TransformationRepository publisherTransformationRepository;

    @Test
    void when_findAllPublisherTransformation_shouldReturn() {
        doReturn(Collections.singletonList(createMockPublisherTransformation("schema", "entity")))
                .when(publisherTransformationRepository).findByProperties(Mockito.any());
        List<Transformation> transformations = publisherTransformationRepository.findAllPublisherTransformation("aia");

        assertEquals(1, transformations.size());
    }

    @Test
    void when_findPublisherTransformationByKeys_shouldReturn() {
        Transformation transformation = createMockPublisherTransformation("schema", "entity");
        doReturn(transformation).when(publisherTransformationRepository).findOneByProperties(Mockito.any());
        Transformation actual = publisherTransformationRepository.findPublisherTransformationByKeys("aia", "schema_key", "storeKey", "contextKey");

        assertEquals(transformation.getKey(), actual.getKey());
        assertEquals(transformation.getContextKey(), actual.getContextKey());
        assertEquals(transformation.getProjectKey(), actual.getProjectKey());
        assertEquals(transformation.getTargetEntityStoreKey(), actual.getTargetEntityStoreKey());
        assertEquals(transformation.getTargetSchemaStoreKey(), actual.getTargetSchemaStoreKey());
        assertEquals(transformation.getCustomScript(), actual.getCustomScript());
    }

    private static Transformation createMockPublisherTransformation(String schema, String entity) {
        Transformation transformation = new Transformation();
        transformation.setContextKey(entity + "PublisherContext");
        transformation.setKey(transformation.getContextKey());
        transformation.setProjectKey("aia");
        transformation.setTargetEntityStoreKey(entity);
        transformation.setTargetSchemaStoreKey(schema);
        transformation.setCustomScript("Select * from " + entity);
        transformation.setReferenceSourceEntities(new ArrayList<>());
        transformation.setDescription(entity + "description");
        transformation.setPublisherName(transformation.getContextKey());
        return transformation;
    }
}