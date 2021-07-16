package com.amdocs.aia.il.common.model.publisher;

import com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public class PublisherSchemaStore extends AbstractIntegrationLayerSchemaStoreModel {
    private static final long serialVersionUID = -5584508651282902149L;

    public PublisherSchemaStore() {
        super.setElementType(getElementTypeFor(PublisherSchemaStore.class));
        super.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        super.setStoreType(StoreTypeCategory.PUBLISHER_STORE);
    }
}