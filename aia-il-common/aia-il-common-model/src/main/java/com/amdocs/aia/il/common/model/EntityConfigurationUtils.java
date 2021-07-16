package com.amdocs.aia.il.common.model;

import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.bulk.BulkGroup;

public final class EntityConfigurationUtils {

    private EntityConfigurationUtils() {
    }

    public static String getElementId(BulkGroup model) {
        return ModelUtils.generateGlobalUniqueId(
                ConfigurationConstants.PRODUCT_KEY,
                model.getProjectKey(),
                BulkGroup.class.getSimpleName(),
                null,
                model.getSchemaKey() + '_' + model.getKey());
    }

    public static String getElementId(ExternalSchema model) {
        return ModelUtils.generateGlobalUniqueId(
                ConfigurationConstants.PRODUCT_KEY,
                model.getProjectKey(),
                ExternalSchema.class.getSimpleName(),
                null,
                model.getSchemaKey());
    }

    public static String getElementId(ExternalEntity model) {
        return getExternalEntityId(model.getProjectKey(), model.getSchemaKey(), model.getEntityKey());
    }

    public static String getExternalEntityId(String projectKey, String schemaKey, String entityKey) {
        return ModelUtils.generateGlobalUniqueId(
                ConfigurationConstants.PRODUCT_KEY,
                projectKey,
                ExternalEntity.ELEMENT_TYPE,
                null,
                schemaKey + '_' + entityKey);
    }

    public static String getElementId(CacheEntity model) {
        return ModelUtils.generateGlobalUniqueId(
                ConfigurationConstants.PRODUCT_KEY,
                model.getProjectKey(),
                CacheEntity.class.getSimpleName(),
                null,
                model.getEntityKey());
    }

    public static String getCacheEntityId(String projectKey, String entityKey) {
        return ModelUtils.generateGlobalUniqueId(
                ConfigurationConstants.PRODUCT_KEY,
                projectKey,
                CacheEntity.class.getSimpleName(),
                null,
                entityKey);
    }

    public static String getElementId(AbstractIntegrationLayerEntityStoreModel model) {
        return getElementId(model.getProjectKey(), model.getClass(), model.getSchemaStoreKey() + '_' + model.getEntityStoreKey());
    }

    public static String getElementId(final String projectKey,
                                      final Class<? extends AbstractIntegrationLayerEntityStoreModel> modelClass,
                                      final String key) {
        return ModelUtils.generateGlobalUniqueId(
                ConfigurationConstants.PRODUCT_KEY,
                projectKey,
                getElementType(modelClass),
                null,
                key);
    }

    public static String getElementType(Class<? extends AbstractIntegrationLayerEntityStoreModel> modelClass) {
        return AbstractIntegrationLayerEntityStoreModel.getElementTypeFor(modelClass);
    }
}