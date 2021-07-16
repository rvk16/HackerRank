package com.amdocs.aia.il.common.model.bulk;

import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.external.AbstractExternalModel;

import java.util.Collections;
import java.util.Set;

public class BulkGroup extends AbstractExternalModel {
    private static final long serialVersionUID = 3966631910025918339L;

    public static final String ELEMENT_TYPE = BulkGroup.class.getSimpleName();
    @RepoSearchable
    private String key;

    @RepoSearchable
    private String schemaKey;

    private GroupFilter groupFilter;

    private Set<EntityFilterRef> entityFilters = Collections.emptySet();

    public BulkGroup() {
        setElementType(ELEMENT_TYPE);
        setProductKey(ConfigurationConstants.PRODUCT_KEY);
        setGroupFilter(null);
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getSchemaKey() {
        return schemaKey;
    }

    public void setSchemaKey(String schemaKey) {
        this.schemaKey = schemaKey;
    }

    public Set<EntityFilterRef> getEntityFilters() {
        return entityFilters;
    }

    public void setEntityFilters(final Set<EntityFilterRef> entityFilters) {
        this.entityFilters = entityFilters;
    }

    public GroupFilter getGroupFilter() { return groupFilter; }

    public void setGroupFilter(GroupFilter groupFilter) { this.groupFilter = groupFilter; }
}