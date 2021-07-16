package com.amdocs.aia.il.common.model.external;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractExternalEntityCollectionRules implements ExternalEntityCollectionRules {
    private static final long serialVersionUID = -1584472438198154852L;

    private final String type;
    private ExternalEntityFilter defaultFilter;
    private List<ExternalEntityFilter> filters = new ArrayList<>();
    private ExternalAttributeIncrementalAttribute incrementalAttribute;

    protected AbstractExternalEntityCollectionRules(String type) {
        this.type = type;
    }

    public List<ExternalEntityFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<ExternalEntityFilter> filters) {
        this.filters = filters;
    }

    public ExternalEntityFilter getDefaultFilter() {
        return defaultFilter;
    }

    public void setDefaultFilter(ExternalEntityFilter defaultFilter) {
        this.defaultFilter = defaultFilter;
    }

    public ExternalAttributeIncrementalAttribute getIncrementalAttribute() {
        return incrementalAttribute;
    }

    public void setIncrementalAttribute(ExternalAttributeIncrementalAttribute incrementalAttribute) {
        this.incrementalAttribute = incrementalAttribute;
    }

    @Override
    public String getType() {
        return type;
    }
}
