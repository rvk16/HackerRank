package com.amdocs.aia.il.common.model.converters.external.entity;

import com.amdocs.aia.il.common.model.external.ExternalAttributeIncrementalAttribute;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalEntityFilter;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlEntityStoreInfo;
import com.amdocs.aia.il.common.model.physical.sql.EntityFilter;
import com.amdocs.aia.il.common.model.physical.sql.IncrementalColumn;
import com.amdocs.aia.il.common.model.physical.sql.SqlEntityStore;

import java.util.stream.Collectors;

public class ExternalEntityToSqlStoreConverter extends AbstractExternalEntityPhysicalStoreConverter<SqlEntityStore, ExternalSqlEntityStoreInfo, ExternalSqlEntityCollectionRules> {
    @Override
    public String getTargetElementType() {
        return SqlEntityStore.ELEMENT_TYPE;
    }

    @Override
    public Class<SqlEntityStore> getTargetClass() {
        return SqlEntityStore.class;
    }

    @Override
    protected Class<ExternalSqlEntityStoreInfo> getStoreInfoClass() {
        return ExternalSqlEntityStoreInfo.class;
    }

    @Override
    protected void populatePhysicalStoreInfo(final SqlEntityStore target, final ExternalSqlEntityStoreInfo storeInfo) {
    }

    @Override
    protected void populateCollectionRules(SqlEntityStore target, ExternalEntity externalEntity, ExternalSqlEntityCollectionRules collectionRules) {
        target.setAllBulkSqlFilter(convertFilter(externalEntity, collectionRules.getDefaultFilter()));
        target.setEntityFilters(collectionRules.getFilters().stream().map(externalEntityFilter -> convertFilter(externalEntity, externalEntityFilter)).collect(Collectors.toList()));
        target.setIncrementalColumn(convertIncrementalColumn(collectionRules.getIncrementalAttribute()));
    }

    private EntityFilter convertFilter(ExternalEntity externalEntity, ExternalEntityFilter externalEntityFilter) {
        if (externalEntityFilter != null) {
            EntityFilter entityFilter = new EntityFilter();
            entityFilter.setSchemaKey(externalEntity.getSchemaKey());
            entityFilter.setEntityKey(externalEntity.getEntityKey());
            entityFilter.setEntityFilterKey(externalEntityFilter.getFilterKey());
            entityFilter.setQuery(externalEntityFilter.getFilterLogic());
            return entityFilter;
        }
        else {
            return null;
        }
    }

    private IncrementalColumn convertIncrementalColumn(ExternalAttributeIncrementalAttribute incrementalAttribute) {
        if (null != incrementalAttribute) {
            return new IncrementalColumn(incrementalAttribute.getKey(), IncrementalColumn.Type.valueOf(incrementalAttribute.getType().name()));
        }

        return null;
    }

}