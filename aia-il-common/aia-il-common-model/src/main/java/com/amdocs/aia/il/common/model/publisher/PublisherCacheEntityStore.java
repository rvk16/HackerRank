package com.amdocs.aia.il.common.model.publisher;

import com.amdocs.aia.il.common.model.AbstractIntegrationLayerEntityStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public class PublisherCacheEntityStore extends AbstractIntegrationLayerEntityStoreModel {
    private static final long serialVersionUID = 8628028799577957188L;

    public PublisherCacheEntityStore() {
        super.setElementType(getElementTypeFor(PublisherCacheEntityStore.class));
        super.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        super.setStoreType(StoreTypeCategory.PUBLISHER_CACHE);
    }
}