package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.il.common.model.bulk.BulkGroup;
import com.amdocs.aia.repo.client.AiaProjectElementRepository;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.PRODUCT_KEY;

@Component
public class BulkGroupRepository extends AiaProjectElementRepository<BulkGroup> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkGroupRepository.class);

    protected static final String PROJECT_KEY = "projectKey";
    protected static final String ELEMENT_TYPE = "elementType";
    protected static final String PRODUCTKEY_KEY = "productKey";

    @Autowired
    public BulkGroupRepository(final AiaRepositoryOperations operations) {
        super(operations);
    }

    public List<BulkGroup> findByProjectKey(final String projectKey) {
        final Map<String, Object> properties = new HashMap<>(3);
        properties.put(PROJECT_KEY, projectKey);
        properties.put(ELEMENT_TYPE, BulkGroup.class.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        return findByProperties(properties, BulkGroup.class);
    }

    public List<BulkGroup> findByProjectKeyAndSchemaStoreKey(final String projectKey, final String schemaKey) {
        final Map<String, Object> properties = new HashMap<>(3);
        properties.put(PROJECT_KEY, projectKey);
        properties.put("schemaKey", schemaKey);
        properties.put(ELEMENT_TYPE, BulkGroup.class.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        return findByProperties(properties, BulkGroup.class);
    }

    public BulkGroup getByKey(final String projectKey, final String schemaKey, final String key) {
        final Map<String, Object> properties = new HashMap<>(4);
        properties.put(PROJECT_KEY, projectKey);
        properties.put("key", key);
        properties.put("schemaKey", schemaKey);
        properties.put(ELEMENT_TYPE, BulkGroup.class.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        LOGGER.info("Find by properties {}", properties);
        return findOneByProperties(properties, BulkGroup.class);
    }
}