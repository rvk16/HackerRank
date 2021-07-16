package com.amdocs.aia.il.common.model.cache;

import com.amdocs.aia.common.model.ModelElement;


import java.io.Serializable;

public class CacheAttribute extends ModelElement implements Serializable {
    private static final long serialVersionUID = -3089382309748444516L;

    private String attributeKey;
    private String datatype;
    private Integer keyPosition;


    public String getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public Integer getKeyPosition() {
        return keyPosition;
    }

    public void setKeyPosition(Integer keyPosition) {
        this.keyPosition = keyPosition;
    }


}
