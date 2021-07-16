package com.amdocs.aia.il.common.model.converters.external.entity;

import com.amdocs.aia.common.model.extensions.typesystems.ProtoTypeSystem;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SharedStores;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.converters.ConverterUtils;
import com.amdocs.aia.il.common.model.external.ExternalAttribute;
import com.amdocs.aia.il.common.model.external.ExternalEntity;

public class ExternalEntityToSharedPhysicalEntityStoreConverter extends AbstractExternalEntitySharedStoreConverter {

    @Override
    public boolean canConvert(ExternalEntity source) {
        return source.getStoreInfo() != null && ConverterUtils.getSharedStoreType(source.getStoreInfo().getPhysicalStoreType()) != null;
    }

    @Override
    public EntityStore convert(ExternalEntity source) {
        EntityStore entityStore = super.convert(source);
        entityStore.setSchemaStoreKey(source.getSchemaKey());
        entityStore.setStoreType(ConverterUtils.getSharedStoreType(source.getStoreInfo().getPhysicalStoreType()));
        entityStore.setId(ModelUtils.generateGlobalUniqueId(entityStore, entityStore.getEntityStoreKey()));
        return entityStore;
    }

    @Override
    protected String getTargetAttributeType(ExternalAttribute externalAttribute) {
        return externalAttribute.getDatatype();
    }
}
