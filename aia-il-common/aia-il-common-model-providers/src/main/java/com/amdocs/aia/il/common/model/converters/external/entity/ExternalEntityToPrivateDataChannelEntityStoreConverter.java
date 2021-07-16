package com.amdocs.aia.il.common.model.converters.external.entity;

import com.amdocs.aia.common.model.extensions.typesystems.ProtoTypeSystem;
import com.amdocs.aia.il.common.model.DataChannelEntityStore;
import com.amdocs.aia.il.common.model.converters.ConverterUtils;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public class ExternalEntityToPrivateDataChannelEntityStoreConverter extends AbstractExternalEntityPrivateStoreConverter<DataChannelEntityStore> {

    @Override
    public String getTargetElementType() {
        return DataChannelEntityStore.class.getSimpleName();
    }

    @Override
    public Class<DataChannelEntityStore> getTargetClass() {
        return DataChannelEntityStore.class;
    }

    @Override
    public boolean canConvert(ExternalEntity source) {
        return true;
    }

    @Override
    protected String getSchemaStoreKey(String schemaKey) {
        return ConverterUtils.getDataChannelSchemaStoreKey(schemaKey);
    }

    @Override
    public DataChannelEntityStore convert(ExternalEntity source) {
        DataChannelEntityStore entityStore = super.convert(source);
        entityStore.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        entityStore.setExternalSchemaType(source.getSchemaType());
        return entityStore;
    }

    @Override
    protected String getTargetTypeSystem(ExternalEntity externalEntity) {
        return ProtoTypeSystem.NAME;
    }
}
