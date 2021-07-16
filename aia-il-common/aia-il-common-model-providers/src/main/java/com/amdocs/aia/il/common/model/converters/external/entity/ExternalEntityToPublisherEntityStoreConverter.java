package com.amdocs.aia.il.common.model.converters.external.entity;

import com.amdocs.aia.common.model.extensions.typesystems.SqlTypeSystem;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.converters.ConverterUtils;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalEntityReplicationPolicy;
import com.amdocs.aia.il.common.model.publisher.PublisherEntityStore;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

public class ExternalEntityToPublisherEntityStoreConverter extends AbstractExternalEntityPrivateStoreConverter<PublisherEntityStore> {

    @Override
    public String getTargetElementType() {
        return PublisherEntityStore.class.getSimpleName();
    }

    @Override
    public Class<PublisherEntityStore> getTargetClass() {
        return PublisherEntityStore.class;
    }

    @Override
    public boolean canConvert(ExternalEntity source) {
        return true; // every external entity has a publisher store
    }

    @Override
    protected String getSchemaStoreKey(String schemaKey) {
        return ConverterUtils.getPublisherStoreSchemaStoreKey(schemaKey);
    }

    @Override
    public PublisherEntityStore convert(ExternalEntity source) {
        PublisherEntityStore entityStore = super.convert(source);
        entityStore.setStoreType(StoreTypeCategory.PUBLISHER_STORE);
        entityStore.setPublisherType(convertPublisherStore(source.getReplicationPolicy()));
        return entityStore;
    }

    private String convertPublisherStore(ExternalEntityReplicationPolicy replicationPolicy) {
        if (replicationPolicy == null) {
            return ConfigurationConstants.SCYLLA_DB_PUBLISHER_STORE;
        }
        switch (replicationPolicy) {
            case NO_REPLICATION:
                return ConfigurationConstants.INMEMORY_DB_PUBLISHER_STORE;
            case REPLICATE:
                return ConfigurationConstants.SCYLLA_DB_PUBLISHER_STORE;
            default:
                throw new IllegalArgumentException("Unrecognized replication policy: " + replicationPolicy);
        }
    }

    @Override
    protected String getTargetTypeSystem(ExternalEntity externalEntity) {
        return SqlTypeSystem.NAME;
    }
}
