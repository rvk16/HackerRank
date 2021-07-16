package com.amdocs.aia.il.common.model.converters.shared.entity;

import com.amdocs.aia.common.model.extensions.typesystems.ProtoTypeSystem;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SharedStores;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.DataChannelEntityStore;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public class EntityStoreToPrivateTargetDataChannelEntityStoreConverter extends AbstractTargetEntityPrivateStoreConverter<DataChannelEntityStore> {

    @Override
    public String getTargetElementType() {
        return DataChannelEntityStore.class.getSimpleName();
    }

    @Override
    public Class<DataChannelEntityStore> getTargetClass() {
        return DataChannelEntityStore.class;
    }

    @Override
    public boolean canConvert(EntityStore entityStore) {
        return !ConfigurationConstants.DYNAMICALLY_PROVIDED_ORIGIN.equals(entityStore.getOrigin()) && // this is kind of a workaround. Because we don't have access to the schema category here, and we know only private datachannel stores are dynamically provided
                entityStore.getStoreType().equals(SharedStores.DataChannel.STORE_TYPE);
    }

    @Override
    protected String getSchemaStoreKey(String schemaKey) {
        return schemaKey;
    }

    @Override
    public DataChannelEntityStore convert(EntityStore entityStore) {
        DataChannelEntityStore dataChannelEntityStore = super.convert(entityStore);
        dataChannelEntityStore.setLogicalSchemaKey(entityStore.getLogicalSchemaKey());
        dataChannelEntityStore.setLogicalEntityKey(entityStore.getLogicalEntityKey());
        dataChannelEntityStore.setStoreType(StoreTypeCategory.DATA_CHANNEL);
        return dataChannelEntityStore;
    }

    @Override
    protected String getTargetTypeSystem(EntityStore entityStore) {
        return ProtoTypeSystem.NAME;
    }
}
