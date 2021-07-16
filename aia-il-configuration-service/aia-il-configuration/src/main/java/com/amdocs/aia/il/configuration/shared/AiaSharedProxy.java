package com.amdocs.aia.il.configuration.shared;

import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.common.model.logical.LogicalEntity;
import com.amdocs.aia.common.model.logical.LogicalSchema;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.shared.client.AiaSharedOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AiaSharedProxy {

    private final AiaSharedOperations sharedOperations;

    public AiaSharedProxy(final AiaSharedOperations sharedOperations) {
        this.sharedOperations = sharedOperations;
    }

    public void reportAdd(ProjectElement projectElement) {
        sharedOperations.reportAdd(projectElement);
    }

    public void reportDelete(ProjectElement projectElement) {
        sharedOperations.reportDelete(projectElement);
    }

    public void reportUpdate(ProjectElement projectElement) {
        sharedOperations.reportUpdate(projectElement);
    }

    public Optional<LogicalSchema> searchLogicalSchemaBySchemaKey(String projectKey, String logicalSchemaKey) {  return sharedOperations.searchLogicalSchemaBySchemaKey(projectKey, logicalSchemaKey);}

    public List<SchemaStore> searchSchemaStores(String projectKey, String query) {  return sharedOperations.searchSchemaStores(projectKey, query);}

    public Optional<LogicalEntity> searchLogicalEntityByEntityKeyAndSchemaKey(String projectKey, String entityKey, String schemaKey) {  return sharedOperations.searchLogicalEntityByEntityKeyAndSchemaKey(projectKey, entityKey, schemaKey);}

    public List<EntityStore> searchEntityStores(String projectKey, String query) {  return sharedOperations.searchEntityStores(projectKey, query);}

    public Optional<SchemaStore> searchSchemaStoreBySchemaKey(String projectKey, String schemaKey){
        return sharedOperations.searchSchemaStoreBySchemaKey(projectKey, schemaKey);
    }

    public List<LogicalEntity> getAllLogicalEntities(String projectKey) {
        String filter = String.format("projectKey:%s", projectKey);
        return sharedOperations.searchLogicalEntities(projectKey,filter);
    }
}