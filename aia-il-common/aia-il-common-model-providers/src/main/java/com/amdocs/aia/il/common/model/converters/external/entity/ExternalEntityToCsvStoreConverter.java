package com.amdocs.aia.il.common.model.converters.external.entity;

import com.amdocs.aia.il.common.model.IntegrationLayerAttributeStore;
import com.amdocs.aia.il.common.model.external.ExternalAttribute;
import com.amdocs.aia.il.common.model.external.ExternalAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvEntityStoreInfo;
import com.amdocs.aia.il.common.model.physical.csv.CsvEntityStore;
import org.springframework.util.StringUtils;

public class ExternalEntityToCsvStoreConverter extends AbstractExternalEntityPhysicalStoreConverter<CsvEntityStore, ExternalCsvEntityStoreInfo, ExternalCsvEntityCollectionRules> {
    @Override
    public String getTargetElementType() {
        return CsvEntityStore.ELEMENT_TYPE;
    }

    @Override
    public Class<CsvEntityStore> getTargetClass() {
        return CsvEntityStore.class;
    }

    @Override
    protected Class<ExternalCsvEntityStoreInfo> getStoreInfoClass() {
        return ExternalCsvEntityStoreInfo.class;
    }

    @Override
    protected void populatePhysicalStoreInfo(CsvEntityStore target, ExternalCsvEntityStoreInfo storeInfo) {
        target.setFileNameFormat(storeInfo.getFileNameFormat());
        target.setHeader(storeInfo.isHeader());
        target.setDateFormat(storeInfo.getDateFormat());
        target.setColumnDelimiter(storeInfo.getColumnDelimiter());
    }

    @Override
    protected void populateCollectionRules(CsvEntityStore target, ExternalEntity externalEntity, ExternalCsvEntityCollectionRules collectionRules) {
        target.setFileInvalidNameAction(collectionRules.getFileInvalidNameAction());
    }

    @Override
    protected void populateAttributeStoreDynamicProperties(IntegrationLayerAttributeStore targetAttributeStore, ExternalEntity externalEntity, ExternalAttribute externalAttribute) {
        super.populateAttributeStoreDynamicProperties(targetAttributeStore, externalEntity, externalAttribute);
        final ExternalAttributeStoreInfo storeInfo = externalAttribute.getStoreInfo();
        if (storeInfo != null) {
            if (!(storeInfo instanceof ExternalCsvAttributeStoreInfo)) {
                throw new IllegalArgumentException("Cannot convert external attribute with store info of type " + storeInfo.getClass() + " into a csv attribute store");
            }
            ExternalCsvAttributeStoreInfo csvAttributeStoreInfo = (ExternalCsvAttributeStoreInfo)storeInfo;
            String dateFormat = csvAttributeStoreInfo.getDateFormat();
            if (!StringUtils.hasText(dateFormat)) {
                final ExternalEntityStoreInfo entityStoreInfo = externalEntity.getStoreInfo();
                if (entityStoreInfo != null && entityStoreInfo instanceof ExternalCsvEntityStoreInfo) {
                    dateFormat = ((ExternalCsvEntityStoreInfo)entityStoreInfo).getDateFormat();
                }
            }
            populateDynamicPropertyIfNotEmpty(targetAttributeStore, CsvEntityStore.DATE_FORMAT, dateFormat);
        }
    }
}
