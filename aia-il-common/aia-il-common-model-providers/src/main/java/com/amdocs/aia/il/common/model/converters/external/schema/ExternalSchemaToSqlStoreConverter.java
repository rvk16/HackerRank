package com.amdocs.aia.il.common.model.converters.external.schema;

import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaStoreInfo;
import com.amdocs.aia.il.common.model.physical.sql.SqlSchemaStore;

public class ExternalSchemaToSqlStoreConverter extends AbstractExternalSchemaPhysicalStoreConverter<SqlSchemaStore, ExternalSqlSchemaStoreInfo, ExternalSqlSchemaCollectionRules> {
    @Override
    public String getTargetElementType() {
        return SqlSchemaStore.ELEMENT_TYPE;
    }

    @Override
    public Class<SqlSchemaStore> getTargetClass() {
        return SqlSchemaStore.class;
    }

    @Override
    protected Class<ExternalSqlSchemaStoreInfo> getStoreInfoClass() {
        return ExternalSqlSchemaStoreInfo.class;
    }

    @Override
    protected void populatePhysicalStoreInfo(SqlSchemaStore target, ExternalSqlSchemaStoreInfo storeInfo) {
    }

    @Override
    protected void populateCollectionRules(SqlSchemaStore target, ExternalSqlSchemaCollectionRules collectionRules) {

    }
}
