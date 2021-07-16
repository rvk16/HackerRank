package com.amdocs.aia.il.common.model.converters.external.schema;

import com.amdocs.aia.common.model.extensions.typesystems.ProtoTypeSystem;
import com.amdocs.aia.il.common.model.DataChannelSchemaStore;
import com.amdocs.aia.il.common.model.converters.ConverterUtils;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public class ExternalSchemaToPrivateDataChannelSchemaStoreConverter extends AbstractExternalSchemaPrivateStoreConverter<DataChannelSchemaStore> {

    @Override
    public String getTargetElementType() {
        return DataChannelSchemaStore.class.getSimpleName();
    }

    @Override
    public Class<DataChannelSchemaStore> getTargetClass() {
        return DataChannelSchemaStore.class;
    }

    @Override
    public boolean canConvert(ExternalSchema source) {
        return source.getDataChannelInfo() != null;
    }

    @Override
    public DataChannelSchemaStore convert(ExternalSchema source) {
        DataChannelSchemaStore schemaStore = super.convert(source);
        schemaStore.setTypeSystem(ProtoTypeSystem.NAME);
        schemaStore.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        schemaStore.setSerializationMethod(source.getDataChannelInfo().getSerializationMethod());
        return schemaStore;
    }

    @Override
    protected String getSchemaStoreKey(String schemaKey) {
        return ConverterUtils.getDataChannelSchemaStoreKey(schemaKey);
    }
}
