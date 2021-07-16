package com.amdocs.aia.il.common.model.converters.external.schema;

import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.common.model.store.SchemaStoreCategory;
import com.amdocs.aia.common.model.store.SharedStores;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.converters.ConverterUtils;
import com.amdocs.aia.il.common.model.external.ExternalSchema;

public class ExternalSchemaToSharedPhysicalSchemaStoreConverter extends AbstractExternalSchemaSharedStoreConverter {

    @Override
    public boolean canConvert(ExternalSchema source) {
        return source.getStoreInfo() != null && ConverterUtils.getSharedStoreType(source.getStoreInfo().getPhysicalStoreType()) != null;
    }

    @Override
    public SchemaStore convert(ExternalSchema source) {
        SchemaStore schemaStore = super.convert(source);
        schemaStore.setSchemaStoreKey(source.getSchemaKey());
        schemaStore.setCategory(SchemaStoreCategory.EXTERNAL);
        schemaStore.setStoreType(ConverterUtils.getSharedStoreType(source.getStoreInfo().getPhysicalStoreType()));
        schemaStore.setTypeSystem(source.getTypeSystem());
        schemaStore.setId(ModelUtils.generateGlobalUniqueId(schemaStore, schemaStore.getSchemaStoreKey()));
        return schemaStore;
    }
}
