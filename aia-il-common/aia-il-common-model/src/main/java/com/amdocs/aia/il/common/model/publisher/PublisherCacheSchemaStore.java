package com.amdocs.aia.il.common.model.publisher;

import com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public class PublisherCacheSchemaStore extends AbstractIntegrationLayerSchemaStoreModel {
    private static final long serialVersionUID = -8751793117988271565L;

    public PublisherCacheSchemaStore() {
        super.setElementType(getElementTypeFor(PublisherCacheSchemaStore.class));
        super.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        super.setStoreType(StoreTypeCategory.PUBLISHER_CACHE);
    }
}