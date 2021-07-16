package com.amdocs.aia.il.common.model.converters.shared.schema;

import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.stores.NumericKeyAssignmentPolicy;
import com.amdocs.aia.il.common.model.stores.SchemaStoreCategory;

public abstract class AbstractTargetSchemaPrivateStoreConverter<T extends AbstractIntegrationLayerSchemaStoreModel> extends AbstractTargetSchemaConverter<T> {

    @Override
    public String getTargetProductKey() {
        return ConfigurationConstants.PRODUCT_KEY;
    }

    @Override
    public T convert(SchemaStore schemaStore) {
        T target = super.convert(schemaStore);
        target.setSchemaStoreKey(getSchemaStoreKey(schemaStore.getSchemaStoreKey()));
        target.setSchemaName(schemaStore.getName());
        target.setNumericKeyAssignmentPolicy(NumericKeyAssignmentPolicy.AUTOMATIC);
        target.setReference(schemaStore.getIsReference());
        target.setId(ModelUtils.generateGlobalUniqueId(target, target.getSchemaStoreKey()));

        return target;
    }

    protected abstract String getSchemaStoreKey(String schemaKey);
}
