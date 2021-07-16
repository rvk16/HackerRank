package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.common.model.repo.ElementMetadata;
import com.amdocs.aia.il.common.model.configuration.transformation.Transformation;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TransformationRepository extends AbstractConfigurationRepository<Transformation> {

    protected TransformationRepository(final AiaRepositoryOperations operations) {
        super(operations, Transformation.class);
    }

    public List<Transformation> findAllPublisherTransformation(String projectKey) {
        final Map<String, Object> properties = new HashMap<>(2);
        properties.put(ElementMetadata.FIELD_ELEMENT_TYPE, Transformation.ELEMENT_TYPE);
        properties.put(ElementMetadata.FIELD_PROJECT_KEY, projectKey);
        return findByProperties(properties);
    }

    public Transformation findPublisherTransformationByKeys(String projectKey, String targetSchemaStoreKey, String targetEntityStoreKey, String contextKey) {
        final Map<String, Object> properties = new HashMap<>(5);
        properties.put(ElementMetadata.FIELD_ELEMENT_TYPE, Transformation.ELEMENT_TYPE);
        properties.put(ElementMetadata.FIELD_PROJECT_KEY, projectKey);
        properties.put("targetSchemaStoreKey", targetSchemaStoreKey);
        properties.put("targetEntityStoreKey", targetEntityStoreKey);
        properties.put("contextKey", contextKey);
        return findOneByProperties(properties);
    }

    public void deleteEntityTransformations(String projectKey, String targetSchemaStoreKey, String targetEntityStoreKey) {
        final Map<String, Object> properties = new HashMap<>(4);
        properties.put(ElementMetadata.FIELD_ELEMENT_TYPE, Transformation.ELEMENT_TYPE);
        properties.put(ElementMetadata.FIELD_PROJECT_KEY, projectKey);
        properties.put("targetSchemaStoreKey", targetSchemaStoreKey);
        properties.put("targetEntityStoreKey", targetEntityStoreKey);
        operations().deleteByProperties(properties);
    }

    public List<Transformation> findPublisherTransformationByLogicalEntity(String projectKey, String schemaStore, String entityStore) {
        final Map<String, Object> properties = new HashMap<>(4);
        properties.put(ElementMetadata.FIELD_ELEMENT_TYPE, Transformation.ELEMENT_TYPE);
        properties.put(ElementMetadata.FIELD_PROJECT_KEY, projectKey);
        properties.put("targetSchemaStoreKey", schemaStore);
        properties.put("targetEntityStoreKey", entityStore);


        return findByProperties(properties);
    }
}