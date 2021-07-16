package com.amdocs.aia.il.configuration.repository.external;

import com.amdocs.aia.il.common.model.external.ExternalEntity;
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
public class ExternalEntityRepository extends AiaProjectElementRepository<ExternalEntity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalEntityRepository.class);

    private static final String PROJECT_KEY = "projectKey";
    private static final String ELEMENT_TYPE = "elementType";
    private static final String PRODUCTKEY_KEY = "productKey";

    @Autowired
    public ExternalEntityRepository(final AiaRepositoryOperations operations) {
        super(operations);
    }

    public ExternalEntity getByKey(final String projectKey, final String schemaKey, final String entityKey) {
        final Map<String, Object> properties = new HashMap<>(5);
        properties.put(PROJECT_KEY, projectKey);
        properties.put("entityKey", entityKey);
        properties.put("schemaKey", schemaKey);
        properties.put(ELEMENT_TYPE, ExternalEntity.class.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Find by properties {}", properties);
        }
        return findOneByProperties(properties, ExternalEntity.class);
    }

    public List<ExternalEntity> findByProjectKey(final String projectKey) {
        final Map<String, Object> properties = new HashMap<>(3);
        properties.put(PROJECT_KEY, projectKey);
        properties.put(ELEMENT_TYPE, ExternalEntity.class.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        return findByProperties(properties, ExternalEntity.class);
    }

    public List<ExternalEntity> findByExternalSchemaKey(final String projectKey, final String externalSchemaKey) {
        final Map<String, Object> properties = new HashMap<>(4);
        properties.put(PROJECT_KEY, projectKey);
        properties.put("schemaKey", externalSchemaKey);
        properties.put(ELEMENT_TYPE, ExternalEntity.class.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        return findByProperties(properties, ExternalEntity.class);
    }
}