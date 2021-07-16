package com.amdocs.aia.il.configuration.repository.cache;

import com.amdocs.aia.il.common.model.cache.CacheEntity;
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
public class CacheReferenceRepository extends AiaProjectElementRepository<CacheEntity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheReferenceRepository.class);

    protected static final String PROJECT_KEY = "projectKey";
    protected static final String ELEMENT_TYPE = "elementType";
    protected static final String PRODUCTKEY_KEY = "productKey";

    @Autowired
    public CacheReferenceRepository(final AiaRepositoryOperations operations) {
        super(operations);
    }

    public CacheEntity getByKey(final String projectKey, final String entityKey) {
        final Map<String, Object> properties = new HashMap<>(4);
        properties.put(PROJECT_KEY, projectKey);
        properties.put("entityKey", entityKey);
        properties.put(ELEMENT_TYPE, CacheEntity.class.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        LOGGER.info("Find by properties {}", properties);
        return findOneByProperties(properties, CacheEntity.class);
    }

    public List<CacheEntity> findByProjectKey(final String projectKey) {
        final Map<String, Object> properties = new HashMap<>(3);
        properties.put(PROJECT_KEY, projectKey);
        properties.put(ELEMENT_TYPE, CacheEntity.class.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        return findByProperties(properties, CacheEntity.class);
    }
}