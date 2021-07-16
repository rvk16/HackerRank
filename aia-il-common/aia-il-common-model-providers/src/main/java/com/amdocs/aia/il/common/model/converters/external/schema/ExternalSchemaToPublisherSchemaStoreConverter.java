package com.amdocs.aia.il.common.model.converters.external.schema;

import com.amdocs.aia.common.model.extensions.typesystems.SqlTypeSystem;
import com.amdocs.aia.il.common.model.converters.ConverterUtils;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.publisher.PublisherSchemaStore;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public class ExternalSchemaToPublisherSchemaStoreConverter extends AbstractExternalSchemaPrivateStoreConverter<PublisherSchemaStore> {

    @Override
    public String getTargetElementType() {
        return PublisherSchemaStore.class.getSimpleName();
    }

    @Override
    public Class<PublisherSchemaStore> getTargetClass() {
        return PublisherSchemaStore.class;
    }

    @Override
    public boolean canConvert(ExternalSchema source) {
        return true; // every external schema has a publisher store
    }

    @Override
    public PublisherSchemaStore convert(ExternalSchema source) {
        PublisherSchemaStore schemaStore = super.convert(source);
        schemaStore.setTypeSystem(SqlTypeSystem.NAME);
        schemaStore.setStoreType(StoreTypeCategory.PUBLISHER_STORE);
        return schemaStore;
    }

    @Override
    protected String getSchemaStoreKey(String schemaKey) {
        return ConverterUtils.getPublisherStoreSchemaStoreKey(schemaKey);
    }
}
