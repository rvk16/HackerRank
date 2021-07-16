package com.amdocs.aia.il.common.model.converters.external.schema;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.extensions.typesystems.ProtoTypeSystem;
import com.amdocs.aia.common.model.store.*;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.converters.ConverterUtils;
import com.amdocs.aia.il.common.model.external.Availability;
import com.amdocs.aia.il.common.model.external.ExternalSchema;

public class ExternalSchemaToSharedDataChannelSchemaStoreConverter extends AbstractExternalSchemaSharedStoreConverter {

    @Override
    public boolean canConvert(ExternalSchema source) {
        return source.getDataChannelInfo() != null && source.getAvailability() != Availability.SHARED;
    }

    @Override
    public SchemaStore convert(ExternalSchema source) {
        SchemaStore schemaStore = super.convert(source);
        schemaStore.setSchemaStoreKey(ConverterUtils.getDataChannelSchemaStoreKey(source.getSchemaKey()));
        schemaStore.setCategory(SchemaStoreCategory.PRIVATE);
        schemaStore.setStoreType(SharedStores.DataChannel.STORE_TYPE);
        schemaStore.setSerializationMethod(source.getDataChannelInfo().getSerializationMethod());
        schemaStore.setPropertyValue(SharedStores.DataChannel.DATA_CHANNEL_NAME_PROPERTY.getKey(), source.getDataChannelInfo().getDataChannelName());
        schemaStore.setTypeSystem(ProtoTypeSystem.NAME);
        schemaStore.setId(ModelUtils.generateGlobalUniqueId(schemaStore, schemaStore.getSchemaStoreKey()));
        return schemaStore;
    }
}
