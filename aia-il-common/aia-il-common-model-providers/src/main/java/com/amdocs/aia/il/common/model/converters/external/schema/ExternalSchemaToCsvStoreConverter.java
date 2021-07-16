package com.amdocs.aia.il.common.model.converters.external.schema;

import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaStoreInfo;
import com.amdocs.aia.il.common.model.physical.csv.CsvSchemaStore;

public class ExternalSchemaToCsvStoreConverter extends AbstractExternalSchemaPhysicalStoreConverter<CsvSchemaStore, ExternalCsvSchemaStoreInfo, ExternalCsvSchemaCollectionRules> {
    @Override
    public String getTargetElementType() {
        return CsvSchemaStore.ELEMENT_TYPE;
    }

    @Override
    public Class<CsvSchemaStore> getTargetClass() {
        return CsvSchemaStore.class;
    }

    @Override
    protected Class<ExternalCsvSchemaStoreInfo> getStoreInfoClass() {
        return ExternalCsvSchemaStoreInfo.class;
    }

    @Override
    protected void populatePhysicalStoreInfo(CsvSchemaStore target, ExternalCsvSchemaStoreInfo storeInfo) {
    }

    @Override
    protected void populateCollectionRules(CsvSchemaStore target, ExternalCsvSchemaCollectionRules collectionRules) {

    }
}
