package com.amdocs.aia.il.common.model.converters.external.schema;

import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.stores.NumericKeyAssignmentPolicy;
import com.amdocs.aia.il.common.model.stores.SchemaStoreCategory;

public abstract class AbstractExternalSchemaPrivateStoreConverter<T extends AbstractIntegrationLayerSchemaStoreModel> extends AbstractExternalSchemaConverter<T> {

    @Override
    public String getTargetProductKey() {
        return ConfigurationConstants.PRODUCT_KEY;
    }

    @Override
    public T convert(ExternalSchema source) {
        T target = super.convert(source);
        target.setSchemaStoreKey(getSchemaStoreKey(source.getSchemaKey()));
        target.setSchemaName(source.getSchemaKey());
        target.setLogicalSchemaKey(null);
        target.setCategory(SchemaStoreCategory.PRIVATE);
        if (source.getDataChannelInfo() != null) {
            target.setDataChannel(source.getDataChannelInfo().getDataChannelName());
        }
        target.setNumericKeyAssignmentPolicy(NumericKeyAssignmentPolicy.AUTOMATIC);
        target.setReference(source.getIsReference());
        target.setId(ModelUtils.generateGlobalUniqueId(target, target.getSchemaStoreKey()));
        target.setAvailability(source.getAvailability());
        target.setExternalSchemaType(source.getSchemaType());
        return target;
    }

    protected abstract String getSchemaStoreKey(String schemaKey);
}
