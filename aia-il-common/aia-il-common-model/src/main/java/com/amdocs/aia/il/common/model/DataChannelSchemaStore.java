package com.amdocs.aia.il.common.model;

import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public class DataChannelSchemaStore extends AbstractIntegrationLayerSchemaStoreModel {
    private static final long serialVersionUID = 2937342446203136463L;

    @RepoSearchable
    private String serializationMethod;

    public DataChannelSchemaStore() {
        super.setElementType(getElementTypeFor(DataChannelSchemaStore.class));
        super.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        super.setStoreType(StoreTypeCategory.DATA_CHANNEL);
    }

    public String getSerializationMethod() {
        return serializationMethod;
    }

    public void setSerializationMethod(String serializationMethod) {
        this.serializationMethod = serializationMethod;
    }
}