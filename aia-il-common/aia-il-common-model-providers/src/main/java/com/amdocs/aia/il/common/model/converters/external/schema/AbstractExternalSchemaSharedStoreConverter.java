package com.amdocs.aia.il.common.model.converters.external.schema;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.store.NumericKeyAssignmentPolicy;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.il.common.model.external.ExternalSchema;

public abstract class AbstractExternalSchemaSharedStoreConverter extends AbstractExternalSchemaConverter<SchemaStore> {
    @Override
    public String getTargetProductKey() {
        return ModelConstants.SHARED_PRODUCT_KEY;
    }

    @Override
    public String getTargetElementType() {
        return SchemaStore.ELEMENT_TYPE_CODE;
    }

    @Override
    public Class<SchemaStore> getTargetClass() {
        return SchemaStore.class;
    }

    @Override
    public SchemaStore convert(ExternalSchema source) {
        SchemaStore schemaStore = super.convert(source);
        schemaStore.setLogicalSchemaKey(null);
        schemaStore.setNumericKeyAssignmentPolicy(NumericKeyAssignmentPolicy.MANUAL);
        schemaStore.setIsReference(source.getIsReference());
        return schemaStore;
    }
}
