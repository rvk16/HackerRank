package com.amdocs.aia.il.common.model.physical;

import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerEntityStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public abstract class AbstractPhysicalEntityStore extends AbstractIntegrationLayerEntityStoreModel {
    private static final long serialVersionUID = -1561662669803704837L;

    @RepoSearchable
    private PhysicalStoreType physicalStoreType;

    private ExternalSchemaType externalSchemaType;

    public AbstractPhysicalEntityStore(final String elementType, final PhysicalStoreType physicalStoreType) {
        super.setElementType(elementType);
        super.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        super.setStoreType(StoreTypeCategory.PHYSICAL);
        this.physicalStoreType = physicalStoreType;
    }

    public PhysicalStoreType getPhysicalStoreType() {
        return physicalStoreType;
    }

    public void setPhysicalStoreType(final PhysicalStoreType physicalStoreType) {
        this.physicalStoreType = physicalStoreType;
    }

    public ExternalSchemaType getExternalSchemaType() {
        return externalSchemaType;
    }

    public void setExternalSchemaType(ExternalSchemaType externalSchemaType) {
        this.externalSchemaType = externalSchemaType;
    }
}