package com.amdocs.aia.il.common.model.external;

import java.io.Serializable;

public class ExternalEntityFilter implements Serializable {
    private static final long serialVersionUID = -3059275406994681885L;
    private String filterKey;
    private String filterLogic;

    public String getFilterKey() {
        return filterKey;
    }

    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    public String getFilterLogic() {
        return filterLogic;
    }

    public void setFilterLogic(String filterLogic) {
        this.filterLogic = filterLogic;
    }
}
