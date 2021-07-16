package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.il.common.model.AbstractIntegrationLayerEntityStoreModel;
import com.amdocs.aia.repo.client.AiaProjectElementRepository;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.PRODUCT_KEY;

public abstract class AbstractEntityStoreRepository<M extends AbstractIntegrationLayerEntityStoreModel> extends AiaProjectElementRepository<M> {
    protected static final String PROJECT_KEY = "projectKey";
    protected static final String ELEMENT_TYPE = "elementType";
    protected static final String PRODUCTKEY_KEY = "productKey";

    protected Class<M> mClass;

    protected AbstractEntityStoreRepository(final AiaRepositoryOperations operations, final Class<M> mClass) {
        super(operations);
        this.mClass = mClass;
    }

    public List<M> findByProjectKey(final String projectKey) {
        final Map<String, Object> properties = new HashMap<>(3);
        properties.put(PROJECT_KEY, projectKey);
        properties.put(ELEMENT_TYPE, mClass.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        return findByProperties(properties, mClass);
    }

    public List<M> findByProjectKeyAndSchemaStoreKey(final String projectKey, final String schemaStoreKey) {
        final Map<String, Object> properties = new HashMap<>(4);
        properties.put(PROJECT_KEY, projectKey);
        properties.put("schemaStoreKey", schemaStoreKey);
        properties.put(ELEMENT_TYPE, mClass.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        return findByProperties(properties, mClass);
    }

    public M getByKey(final String projectKey, final String schemaStoreKey, final String entityStoreKey) {
        final Map<String, Object> properties = new HashMap<>(5);
        properties.put(PROJECT_KEY, projectKey);
        properties.put("entityStoreKey", entityStoreKey);
        properties.put("schemaStoreKey", schemaStoreKey);
        properties.put(ELEMENT_TYPE, mClass.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        return findOneByProperties(properties, mClass);
    }
}