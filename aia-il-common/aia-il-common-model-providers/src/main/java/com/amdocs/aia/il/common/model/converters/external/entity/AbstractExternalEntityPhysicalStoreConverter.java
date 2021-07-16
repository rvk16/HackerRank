package com.amdocs.aia.il.common.model.converters.external.entity;

import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.ExternalEntityStoreInfo;
import com.amdocs.aia.il.common.model.physical.AbstractPhysicalEntityStore;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public abstract class AbstractExternalEntityPhysicalStoreConverter<T extends AbstractPhysicalEntityStore, S extends ExternalEntityStoreInfo, U extends ExternalEntityCollectionRules> extends AbstractExternalEntityPrivateStoreConverter<T> {

    @Override
    public T convert(ExternalEntity source) {
        T target = super.convert(source);
        target.setStoreType(StoreTypeCategory.PHYSICAL);
        target.setPhysicalStoreType(source.getStoreInfo().getPhysicalStoreType());
        target.setExternalSchemaType(source.getSchemaType());
        populatePhysicalStoreInfo(target, (S)source.getStoreInfo());
        if (source.getCollectionRules() != null) {
            populateCollectionRules(target, source, (U)source.getCollectionRules());
        }
        return target;
    }

    @Override
    public boolean canConvert(ExternalEntity source) {
        return source.getStoreInfo() != null && getStoreInfoClass().isAssignableFrom(source.getStoreInfo().getClass());
    }

    protected abstract Class<S> getStoreInfoClass();

    protected abstract void populatePhysicalStoreInfo(T target, S storeInfo);
    protected abstract void populateCollectionRules(T target, ExternalEntity externalEntity, U collectionRules);

    @Override
    protected String getSchemaStoreKey(String schemaKey) {
        return schemaKey;
    }

    @Override
    protected String getTargetTypeSystem(ExternalEntity externalEntity) {
        return externalEntity.getTypeSystem();
    }
}
