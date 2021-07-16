package com.amdocs.aia.il.common.model.publisher;

import com.amdocs.aia.il.common.model.AbstractIntegrationLayerEntityStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public class PublisherEntityStore extends AbstractIntegrationLayerEntityStoreModel {
    private static final long serialVersionUID = -4946349433418187272L;

    private String publisherType;
    private boolean isReference;

    public PublisherEntityStore() {
        super.setElementType(getElementTypeFor(PublisherEntityStore.class));
        super.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        super.setStoreType(StoreTypeCategory.PUBLISHER_STORE);
    }

    public String getPublisherType() {
        return publisherType;
    }

    public void setPublisherType(String publisherType) {
        this.publisherType = publisherType;
    }

    public boolean isReference() {
        return isReference;
    }

    public void setReference(boolean reference) {
        isReference = reference;
    }

    @Override
    public String toString() {
        return "PublisherEntityStore{" +
                "AbstractIntegrationLayerEntityStoreModel=" + super.toString() +
                "publisherType='" + publisherType + '\'' +
                ", isReference=" + isReference +
                '}';
    }
}