package com.amdocs.aia.il.configuration.mapper.unit;

import com.amdocs.aia.common.model.transformation.TransformationContextEntity;
import com.amdocs.aia.il.common.model.configuration.transformation.Transformation;
import com.amdocs.aia.il.common.model.configuration.transformation.TransformationImplementationType;
import com.amdocs.aia.il.common.model.configuration.transformation.TransformationSourceType;
import com.amdocs.aia.il.configuration.dto.TransformationContextEntityDTO;
import com.amdocs.aia.il.configuration.dto.TransformationDTO;
import com.amdocs.aia.il.configuration.mapper.TransformationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransformationMapperUnitTest {
    private TransformationMapper transformationMapper;

    @TestConfiguration
    static class PublisherTransformationMapperConfiguration {
    }

    @BeforeEach
    void before() {
        transformationMapper = new TransformationMapper();
    }

    @Test
    void whenPublisherTransformationToPublisherContextDTO_ShouldBeEqual() {
        final Transformation transformation = getPublisherTransformation();
        final TransformationDTO publisherTransformationDTO = transformationMapper.toDTO(transformation);

        assertEquals(transformation.getPublisherName(), publisherTransformationDTO.getStoreName());
        assertEquals(transformation.getDescription(), publisherTransformationDTO.getDescription());
        assertEquals(transformation.getName(), publisherTransformationDTO.getDisplayName());
        assertEquals(transformation.getProjectKey(), publisherTransformationDTO.getProjectKey());
        assertEquals(transformation.getContextKey(), publisherTransformationDTO.getContextKey());
    }

    @Test
    void whenPublisherTransformationToPublisherContextDTOAlternative_ShouldBeEqual() {
        final Transformation transformation = getPublisherTransformation();
        transformation.setSourceType(TransformationSourceType.CONTEXT);
        transformation.setImplementationType(TransformationImplementationType.CUSTOM_SQL);
        final TransformationContextEntity contextEntity = getTransformationContextEntity("customer");
        transformation.setReferenceSourceEntities(Collections.singletonList(contextEntity));
        final TransformationDTO publisherTransformationDTO = transformationMapper.toDTO(transformation);

        assertEquals(transformation.getPublisherName(), publisherTransformationDTO.getStoreName());
        assertEquals(transformation.getDescription(), publisherTransformationDTO.getDescription());
        assertEquals(transformation.getName(), publisherTransformationDTO.getDisplayName());
        assertEquals(transformation.getProjectKey(), publisherTransformationDTO.getProjectKey());
        assertEquals(transformation.getContextKey(), publisherTransformationDTO.getContextKey());
        assertEquals(TransformationDTO.SourceTypeEnum.CONTEXT, publisherTransformationDTO.getSourceType());
        assertEquals(TransformationDTO.ImplementationTypeEnum.SQL, publisherTransformationDTO.getImplementationType());
        assertEquals(transformation.getReferenceSourceEntities().size(),publisherTransformationDTO.getReferenceSourceEntities().size());
        assertEquals(2,publisherTransformationDTO.getReferenceAttributes().size());
        final TransformationContextEntityDTO transformationContextEntityDTO = publisherTransformationDTO.getReferenceSourceEntities().get(0);

        assertEquals(contextEntity.getEntityStoreKey(), transformationContextEntityDTO.getEntityStoreKey());
        assertEquals(contextEntity.getSchemaStoreKey(), transformationContextEntityDTO.getSchemaStoreKey());
        assertEquals(contextEntity.getFilterDescription(), transformationContextEntityDTO.getFilterDescription());
    }

    @Test
    void whenPublisherTransformationDTOToPublisherContext_ShouldBeEqual() {
        final TransformationDTO publisherTransformationDTO = getPublisherTransformationDTO();
        final Transformation transformation = transformationMapper.toModel("projectKey", publisherTransformationDTO);

        assertEquals(publisherTransformationDTO.getStoreName(), transformation.getPublisherName());
        assertEquals(publisherTransformationDTO.getDescription(), transformation.getDescription());
        assertEquals(publisherTransformationDTO.getDisplayName(), transformation.getName());
        assertEquals(publisherTransformationDTO.getStoreName(), transformation.getKey());
        assertEquals(publisherTransformationDTO.getProjectKey(), transformation.getProjectKey());
    }

    @Test
    void whenPublisherTransformationDTOToPublisherContextAlternative_ShouldBeEqual() {
        final TransformationDTO publisherTransformationDTO = getPublisherTransformationDTO();
        publisherTransformationDTO.sourceType(TransformationDTO.SourceTypeEnum.CONTEXT);
        publisherTransformationDTO.implementationType(TransformationDTO.ImplementationTypeEnum.SQL);
        publisherTransformationDTO.originProcess("CUSTOM");
        final TransformationContextEntityDTO entityDTO = getTransformationContextEntityDTO("test");
        publisherTransformationDTO.referenceSourceEntities(Collections.singletonList(entityDTO));
        final Transformation transformation = transformationMapper.toModel("projectKey", publisherTransformationDTO);

        assertEquals(publisherTransformationDTO.getStoreName(), transformation.getPublisherName());
        assertEquals(publisherTransformationDTO.getDescription(), transformation.getDescription());
        assertEquals(publisherTransformationDTO.getDisplayName(), transformation.getName());
        assertEquals(publisherTransformationDTO.getStoreName(), transformation.getKey());
        assertEquals(publisherTransformationDTO.getProjectKey(), transformation.getProjectKey());
        assertEquals(TransformationSourceType.CONTEXT, transformation.getSourceType());
        assertEquals(TransformationImplementationType.CUSTOM_SQL, transformation.getImplementationType());
        assertEquals(publisherTransformationDTO.getReferenceSourceEntities().size(), transformation.getReferenceSourceEntities().size());
        assertEquals(2,publisherTransformationDTO.getReferenceAttributes().size());

        final TransformationContextEntity entity = transformation.getReferenceSourceEntities().get(0);
        assertEquals(entityDTO.getEntityStoreKey(), entity.getEntityStoreKey());
        assertEquals(entityDTO.getSchemaStoreKey(), entity.getSchemaStoreKey());
        assertEquals(entityDTO.getFilterDescription(), entity.getFilterDescription());
    }

    private static Transformation getPublisherTransformation() {
        final Transformation transformation = new Transformation();
        transformation.setKey("key");
        transformation.setDescription("description");
        transformation.setName("name");
        transformation.setProjectKey("projectKey");
        transformation.setContextKey("CustomerContext");
        transformation.setPublisherName("CustomerContext");
        transformation.setId("0");
        transformation.setProductKey("productKey");
        transformation.setTargetEntityStoreKey("Customer");
        transformation.setTargetSchemaStoreKey("aLDMCustomer");
        transformation.setCustomScript("Select * from Customer");
        transformation.setReferenceSourceEntities(new ArrayList<>());
        transformation.setReferenceAttributes(Arrays.asList("Attribute1","Attribute2"));
        return transformation;
    }

    private static TransformationContextEntityDTO getTransformationContextEntityDTO(final String entity) {
        TransformationContextEntityDTO entityDTO = new TransformationContextEntityDTO();
        entityDTO.entityStoreKey(entity + "_store_key");
        entityDTO.schemaStoreKey(entity + "_schema_store_key");
        entityDTO.filterDescription(entity + "_filter_description");
        return entityDTO;
    }

    private static TransformationDTO getPublisherTransformationDTO() {
        final TransformationDTO publisherTransformationDTO = new TransformationDTO();
        publisherTransformationDTO.projectKey("projectKey");
        publisherTransformationDTO.storeName("publisherName");
        publisherTransformationDTO.displayName("displayName");
        publisherTransformationDTO.description("description");
        publisherTransformationDTO.setIsPublished(false);
        publisherTransformationDTO.setTargetSchemaName("targetSchemaName");
        publisherTransformationDTO.setReferenceAttributes(Arrays.asList("Att1","Att2"));
        return publisherTransformationDTO;
    }

    private static TransformationContextEntity getTransformationContextEntity(final String entity) {
        final TransformationContextEntity transformationContextEntity = new TransformationContextEntity();
        transformationContextEntity.setEntityStoreKey(entity + "_store_key");
        transformationContextEntity.setSchemaStoreKey(entity + "_schema_key");
        transformationContextEntity.setFilterDescription(entity + "_filter_description");
        return transformationContextEntity;
    }
}