package com.amdocs.aia.il.common.model.converters.shared.schema;

import com.amdocs.aia.common.model.extensions.typesystems.ProtoTypeSystem;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.common.model.store.SharedStores;
import com.amdocs.aia.il.common.model.DataChannelSchemaStore;
import com.amdocs.aia.il.common.model.converters.ConverterUtils;
import com.amdocs.aia.il.common.model.stores.SchemaStoreCategory;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public class SchemaStoreToPrivateTargetDataChannelSchemaStoreConverter extends AbstractTargetSchemaPrivateStoreConverter<DataChannelSchemaStore> {

    @Override
    public String getTargetElementType() {
        return DataChannelSchemaStore.class.getSimpleName();
    }

    @Override
    public Class<DataChannelSchemaStore> getTargetClass() {
        return DataChannelSchemaStore.class;
    }

    @Override
    public boolean canConvert(SchemaStore schemaStore) {
        return schemaStore.getCategory().equals(com.amdocs.aia.common.model.store.SchemaStoreCategory.SHARED) &&
                schemaStore.getStoreType().equals(SharedStores.DataChannel.STORE_TYPE) &&
                schemaStore.hasProperty(SharedStores.DataChannel.DATA_CHANNEL_NAME_PROPERTY.getKey());
    }

    @Override
    public DataChannelSchemaStore convert(SchemaStore schemaStore) {
        DataChannelSchemaStore dataChannelSchemaStore = super.convert(schemaStore);
        dataChannelSchemaStore.setLogicalSchemaKey(schemaStore.getLogicalSchemaKey());
        dataChannelSchemaStore.setDataChannel(schemaStore.getPropertyValue(SharedStores.DataChannel.DATA_CHANNEL_NAME_PROPERTY.getKey()).toString());
        dataChannelSchemaStore.setTypeSystem(ProtoTypeSystem.NAME);
        dataChannelSchemaStore.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        dataChannelSchemaStore.setCategory(SchemaStoreCategory.SHARED);
        dataChannelSchemaStore.setSerializationMethod(schemaStore.getSerializationMethod());
        return dataChannelSchemaStore;
    }

    @Override
    protected String getSchemaStoreKey(String schemaKey) {
        return schemaKey;
    }
}
