package com.amdocs.aia.il.common.model.converters.external.schema;

import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.external.ExternalSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreInfo;
import com.amdocs.aia.il.common.model.physical.AbstractPhysicalSchemaStore;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public abstract class AbstractExternalSchemaPhysicalStoreConverter<T extends AbstractPhysicalSchemaStore, S extends ExternalSchemaStoreInfo, U extends ExternalSchemaCollectionRules> extends AbstractExternalSchemaPrivateStoreConverter<T> {

    @Override
    public T convert(ExternalSchema source) {
        T target = super.convert(source);
        target.setTypeSystem(source.getTypeSystem());
        target.setStoreType(StoreTypeCategory.PHYSICAL);
        target.setPhysicalStoreType(source.getStoreInfo().getPhysicalStoreType());
        populatePhysicalStoreInfo(target, (S)source.getStoreInfo());
        if(source.getCollectionRules() != null) {
            populateCollectionRules(target, (U) source.getCollectionRules());
        }
        return target;
    }

    @Override
    public boolean canConvert(ExternalSchema source) {
        return source.getStoreInfo() != null && getStoreInfoClass().isAssignableFrom(source.getStoreInfo().getClass());
    }

    @Override
    protected String getSchemaStoreKey(String schemaKey) {
        return schemaKey;
    }

    protected abstract Class<S> getStoreInfoClass();

    protected abstract void populatePhysicalStoreInfo(T target, S storeInfo);

    protected abstract void populateCollectionRules(T target, U collectionRules);
}
