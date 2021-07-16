package com.amdocs.aia.il.common.model.physical.sql;

import com.amdocs.aia.il.common.model.physical.AbstractPhysicalEntityStore;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;

import java.util.Collections;
import java.util.List;

public class SqlEntityStore extends AbstractPhysicalEntityStore {
    private static final long serialVersionUID = 822063574873670722L;

    public static final String ELEMENT_TYPE = getElementTypeFor(SqlEntityStore.class);
    public static final String VALIDATE_REGEX = "validate-regex";
    public static final String DEFAULT_VALUE = "default-value";

    private EntityFilter allBulkSqlFilter;
    private List<EntityFilter> entityFilters = Collections.emptyList();
    private IncrementalColumn incrementalColumn;

    public SqlEntityStore() {
        super(ELEMENT_TYPE, PhysicalStoreType.SQL);
    }

    public EntityFilter getAllBulkSqlFilter() {
        return allBulkSqlFilter;
    }

    public void setAllBulkSqlFilter(EntityFilter allBulkSqlFilter) {
        this.allBulkSqlFilter = allBulkSqlFilter;
    }

    public List<EntityFilter> getEntityFilters() {
        return entityFilters;
    }

    public void setEntityFilters(final List<EntityFilter> entityFilters) {
        this.entityFilters = entityFilters;
    }

    public IncrementalColumn getIncrementalColumn() {
        return incrementalColumn;
    }

    public void setIncrementalColumn(final IncrementalColumn incrementalColumn) {
        this.incrementalColumn = incrementalColumn;
    }
}