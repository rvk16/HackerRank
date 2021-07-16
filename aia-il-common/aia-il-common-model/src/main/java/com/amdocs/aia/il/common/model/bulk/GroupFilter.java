package com.amdocs.aia.il.common.model.bulk;

import com.amdocs.aia.common.model.ProjectElement;

public class GroupFilter extends ProjectElement {
    private static final long serialVersionUID = 6729268055495519516L;
    private String filter;

    public GroupFilter() {
        // default constructor
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }


}
