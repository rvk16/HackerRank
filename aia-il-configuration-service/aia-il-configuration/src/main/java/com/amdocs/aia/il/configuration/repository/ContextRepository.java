package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.common.model.repo.ElementMetadata;
import com.amdocs.aia.il.common.model.configuration.transformation.Context;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ContextRepository extends AbstractConfigurationRepository<Context> {
    @Autowired
    protected ContextRepository(final AiaRepositoryOperations operations) {
        super(operations, Context.class);
    }

    public List<Context> findAllPublisherContextsByProjectKey(String projectKey) {
        final Map<String, Object> properties = new HashMap<>(2);
        properties.put(ElementMetadata.FIELD_ELEMENT_TYPE, Context.ELEMENT_TYPE);
        properties.put(ElementMetadata.FIELD_PROJECT_KEY, projectKey);
        return findByProperties(properties);
    }

    public Context findPublisherContextByKeys(String projectKey, String contextKey) {
        final Map<String, Object> properties = new HashMap<>(3);
        properties.put(ElementMetadata.FIELD_ELEMENT_TYPE, Context.ELEMENT_TYPE);
        properties.put(ElementMetadata.FIELD_PROJECT_KEY, projectKey);
        properties.put("contextKey", contextKey);
        return findOneByProperties(properties);
    }

    public Context findPublisherContextByPublisherName(String projectKey, String publisherName) {
        final Map<String, Object> properties = new HashMap<>(3);
        properties.put(ElementMetadata.FIELD_ELEMENT_TYPE, Context.ELEMENT_TYPE);
        properties.put(ElementMetadata.FIELD_PROJECT_KEY, projectKey);
        properties.put("publisherName", publisherName);
        return findOneByProperties(properties);
    }
}