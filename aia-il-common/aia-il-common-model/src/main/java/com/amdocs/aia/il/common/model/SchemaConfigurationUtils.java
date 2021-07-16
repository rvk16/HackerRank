package com.amdocs.aia.il.common.model;

import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.external.ExternalSchema;

public final class SchemaConfigurationUtils {

    private SchemaConfigurationUtils() {
    }

    public static String getElementId(AbstractIntegrationLayerSchemaStoreModel model) {
        return getElementId(model.getProjectKey(), model.getClass(), model.getSchemaStoreKey());
    }

    public static String getElementId(String projectKey, Class<? extends AbstractIntegrationLayerSchemaStoreModel> modelClass, String key) {
        return ModelUtils.generateGlobalUniqueId(
                ConfigurationConstants.PRODUCT_KEY,
                projectKey,
                getElementType(modelClass),
                null,
                key);
    }

    public static String getElementId(ExternalSchema model) {
        return ModelUtils.generateGlobalUniqueId(
                ConfigurationConstants.PRODUCT_KEY,
                model.getProjectKey(),
                ExternalSchema.class.getSimpleName(),
                null,
                model.getSchemaKey());
    }

    public static String getElementType(Class<? extends AbstractIntegrationLayerSchemaStoreModel> modelClass) {
        return AbstractIntegrationLayerSchemaStoreModel.getElementTypeFor(modelClass);
    }
}