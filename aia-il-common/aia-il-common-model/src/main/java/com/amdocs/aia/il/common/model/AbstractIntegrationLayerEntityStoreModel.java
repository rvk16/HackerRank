package com.amdocs.aia.il.common.model;

import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.common.model.repo.annotations.RepoTransient;
import com.amdocs.aia.common.model.store.AttributeStore;
import com.amdocs.aia.common.model.store.NumericKeyAlreadyExistsException;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractIntegrationLayerEntityStoreModel extends ProjectElement {
    private static final long serialVersionUID = -1753063519190131979L;

    @RepoSearchable
    private String projectKey;
    @RepoSearchable
    private String entityName;
    @RepoSearchable
    private String schemaStoreKey;
    @RepoSearchable
    private String logicalSchemaKey;
    @RepoSearchable
    private String entityStoreKey;
    @RepoSearchable
    private String logicalEntityKey;
    @RepoSearchable
    private StoreTypeCategory storeType;
    @RepoSearchable
    private Integer serializationId;
    @RepoTransient
    private transient List<ElementDependency> dependencies;
    @RepoTransient
    private transient List<ElementPublicFeature> publicFeatures;

    private Map<String, Integer> assignedAttributeNumericKey = new LinkedHashMap<>();
    private List<IntegrationLayerAttributeStore> attributeStores;

    public void autoAssignNumericKeys() {
        AtomicInteger currentNumericKey = new AtomicInteger(0);
        Set<Integer> usedNumericKeys = new HashSet<>(this.assignedAttributeNumericKey.values());
        for (IntegrationLayerAttributeStore attributeStore : attributeStores) {
            Integer assignedAttributeNumericKeyLocal = this.assignedAttributeNumericKey.get(attributeStore.getAttributeStoreKey());
            if (assignedAttributeNumericKeyLocal == null) {
                // we've never assigned a numeric key to this attribute yet
                if (attributeStore.getSerializationId() == AttributeStore.UNDEFINED_NUMERIC_KEY) {
                    // WE should assign an automatic key to the attribute
                    do {
                        assignedAttributeNumericKeyLocal = currentNumericKey.incrementAndGet();
                    }
                    while (usedNumericKeys.contains(assignedAttributeNumericKeyLocal));
                } else {
                    // the caller manually assigned a value
                    if (usedNumericKeys.contains(attributeStore.getSerializationId())) {
                        throw new NumericKeyAlreadyExistsException(attributeStore.getAttributeStoreKey(), attributeStore.getSerializationId());
                    }
                    assignedAttributeNumericKeyLocal = attributeStore.getSerializationId();
                }
                usedNumericKeys.add(assignedAttributeNumericKeyLocal);
            }
            this.assignedAttributeNumericKey.put(attributeStore.getAttributeStoreKey(), assignedAttributeNumericKeyLocal);
            attributeStore.setSerializationId(assignedAttributeNumericKeyLocal);
        }
    }

    @Override
    public String getProjectKey() {
        return projectKey;
    }

    @Override
    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getSchemaStoreKey() {
        return schemaStoreKey;
    }

    public void setSchemaStoreKey(String schemaStoreKey) {
        this.schemaStoreKey = schemaStoreKey;
    }

    public String getLogicalSchemaKey() {
        return logicalSchemaKey;
    }

    public void setLogicalSchemaKey(String logicalSchemaKey) {
        this.logicalSchemaKey = logicalSchemaKey;
    }

    public String getEntityStoreKey() {
        return entityStoreKey;
    }

    public void setEntityStoreKey(String entityStoreKey) {
        this.entityStoreKey = entityStoreKey;
    }

    public String getLogicalEntityKey() {
        return logicalEntityKey;
    }

    public void setLogicalEntityKey(String logicalEntityKey) {
        this.logicalEntityKey = logicalEntityKey;
    }

    public StoreTypeCategory getStoreType() {
        return storeType;
    }

    public void setStoreType(StoreTypeCategory storeType) {
        this.storeType = storeType;
    }

    public Integer getSerializationId() {
        return serializationId;
    }

    public void setSerializationId(Integer serializationId) {
        this.serializationId = serializationId;
    }

    public Map<String, Integer> getAssignedAttributeNumericKey() {
        return assignedAttributeNumericKey;
    }

    public void setAssignedAttributeNumericKey(Map<String, Integer> assignedAttributeNumericKey) {
        this.assignedAttributeNumericKey = assignedAttributeNumericKey;
    }

    public List<IntegrationLayerAttributeStore> getAttributeStores() {
        return attributeStores;
    }

    public void setAttributeStores(List<IntegrationLayerAttributeStore> attributeStores) {
        this.attributeStores = attributeStores;
    }

    public static String getElementTypeFor(Class<? extends AbstractIntegrationLayerEntityStoreModel> modelClass) {
        return modelClass.getSimpleName();
    }

    @Override
    public List<ElementDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<ElementDependency> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public List<ElementPublicFeature> getPublicFeatures() {
        return publicFeatures;
    }

    public void setPublicFeatures(List<ElementPublicFeature> publicFeatures) {
        this.publicFeatures = publicFeatures;
    }

    @Override
    public String toString() {
        return "AbstractIntegrationLayerEntityStoreModel{" +
                "projectKey='" + projectKey + '\'' +
                ", entityName='" + entityName + '\'' +
                ", schemaStoreKey='" + schemaStoreKey + '\'' +
                ", logicalSchemaKey='" + logicalSchemaKey + '\'' +
                ", entityStoreKey='" + entityStoreKey + '\'' +
                ", logicalEntityKey='" + logicalEntityKey + '\'' +
                ", storeType=" + storeType +
                ", serializationId=" + serializationId +
                ", dependencies=" + dependencies +
                ", publicFeatures=" + publicFeatures +
                ", assignedAttributeNumericKey=" + assignedAttributeNumericKey +
                ", attributeStores=" + attributeStores +
                '}';
    }
}