package com.amdocs.aia.il.common.model;

import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public class DataChannelEntityStore extends AbstractIntegrationLayerEntityStoreModel {
    private static final long serialVersionUID = 8948543130082534173L;

    private ExternalSchemaType externalSchemaType;

    public DataChannelEntityStore() {
        super.setElementType(getElementTypeFor(DataChannelEntityStore.class));
        super.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        super.setStoreType(StoreTypeCategory.DATA_CHANNEL);
    }

    public ExternalSchemaType getExternalSchemaType() {
        return externalSchemaType;
    }

    public void setExternalSchemaType(ExternalSchemaType externalSchemaType) {
        this.externalSchemaType = externalSchemaType;
    }
}