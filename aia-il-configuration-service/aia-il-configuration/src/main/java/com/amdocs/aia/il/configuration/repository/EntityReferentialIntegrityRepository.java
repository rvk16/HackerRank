package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;
import com.amdocs.aia.repo.client.AiaProjectElementRepository;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.PRODUCT_KEY;

@Component
public class EntityReferentialIntegrityRepository extends AiaProjectElementRepository<EntityReferentialIntegrity> {
    protected static final String PROJECT_KEY = "projectKey";
    protected static final String ELEMENT_TYPE = "elementType";
    protected static final String PRODUCTKEY_KEY = "productKey";

    @Autowired
    public EntityReferentialIntegrityRepository(final AiaRepositoryOperations operations) {
        super(operations);
    }

    public List<EntityReferentialIntegrity> findByProjectKey(final String projectKey) {
        final Map<String, Object> properties = new HashMap<>(3);
        properties.put(PROJECT_KEY, projectKey);
        properties.put(ELEMENT_TYPE, EntityReferentialIntegrity.class.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        return findByProperties(properties, EntityReferentialIntegrity.class);
    }

    public EntityReferentialIntegrity findByProjectKeyAndLogicalSchemaKeyAndLogicalEntityKey(final String projectKey, final String logicalSchemaKey, final String logicalEntityKey) {
        final Map<String, Object> properties = new HashMap<>(4);
        properties.put(PROJECT_KEY, projectKey);
        properties.put("logicalEntityKey", logicalEntityKey);
        properties.put("logicalSchemaKey", logicalSchemaKey);
        properties.put(ELEMENT_TYPE, EntityReferentialIntegrity.class.getSimpleName());
        properties.put(PRODUCTKEY_KEY, PRODUCT_KEY);
        return findOneByProperties(properties, EntityReferentialIntegrity.class);
    }
}