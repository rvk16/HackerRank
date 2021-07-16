package com.amdocs.aia.il.common.model.converters.external.entity;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.extensions.typesystems.ProtoTypeSystem;
import com.amdocs.aia.common.model.store.*;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.converters.ConverterUtils;
import com.amdocs.aia.il.common.model.external.Availability;
import com.amdocs.aia.il.common.model.external.ExternalAttribute;
import com.amdocs.aia.il.common.model.external.ExternalEntity;

import java.util.stream.Collectors;

public class ExternalEntityToSharedDataChannelEntityStoreConverter extends AbstractExternalEntitySharedStoreConverter {

    @Override
    public boolean canConvert(ExternalEntity source) {
        return source.getAvailability() != Availability.SHARED;
    }

    @Override
    public EntityStore convert(ExternalEntity source) {
        EntityStore entityStore = super.convert(source);
        entityStore.setSchemaStoreKey(ConverterUtils.getDataChannelSchemaStoreKey(source.getSchemaKey()));
        entityStore.setStoreType(SharedStores.DataChannel.STORE_TYPE);
        entityStore.setId(ModelUtils.generateGlobalUniqueId(entityStore, entityStore.getEntityStoreKey()));
        return entityStore;
    }

    @Override
    protected String getTargetAttributeType(ExternalAttribute externalAttribute) {
        return convertDatatypeFromLogical(externalAttribute.getLogicalDatatype(), ProtoTypeSystem.NAME);
    }
}
