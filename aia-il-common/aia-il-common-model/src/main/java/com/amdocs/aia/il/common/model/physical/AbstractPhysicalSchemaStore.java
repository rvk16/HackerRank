package com.amdocs.aia.il.common.model.physical;

import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public abstract class AbstractPhysicalSchemaStore extends AbstractIntegrationLayerSchemaStoreModel {
    private static final long serialVersionUID = -4400099069956566238L;

    @RepoSearchable
    private PhysicalStoreType physicalStoreType;

    public AbstractPhysicalSchemaStore(final String elementType) {
        super.setStoreType(StoreTypeCategory.PHYSICAL);
        super.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        super.setElementType(elementType);
    }

    public PhysicalStoreType getPhysicalStoreType() {
        return physicalStoreType;
    }

    public void setPhysicalStoreType(final PhysicalStoreType physicalStoreType) {
        this.physicalStoreType = physicalStoreType;
    }

}