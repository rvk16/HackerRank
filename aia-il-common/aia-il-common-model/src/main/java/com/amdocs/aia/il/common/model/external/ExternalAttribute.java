package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.common.model.ModelElement;

import java.io.Serializable;

public class ExternalAttribute extends ModelElement implements Serializable {
    private static final long serialVersionUID = 6273769871673080616L;

    private String attributeKey;
    private String datatype;
    private String logicalDatatype;
    private int serializationId = -1;
    private Integer keyPosition;
    private boolean isLogicalTime;
    private boolean isUpdateTime;
    private boolean isRequired;
    private String validationRegex;
    private String defaultValue;
    private ExternalAttributeStoreInfo storeInfo;

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

    public String getLogicalDatatype() {
        return logicalDatatype;
    }

    public void setLogicalDatatype(String logicalDatatype) {
        this.logicalDatatype = logicalDatatype;
    }

    public Integer getSerializationId() {
        return serializationId;
    }

    public void setSerializationId(int serializationId) {
        this.serializationId = serializationId;
    }

    public Integer getKeyPosition() {
        return keyPosition;
    }

    public void setKeyPosition(Integer keyPosition) {
        this.keyPosition = keyPosition;
    }

    public boolean isLogicalTime() {
        return isLogicalTime;
    }

    public void setLogicalTime(boolean logicalTime) {
        isLogicalTime = logicalTime;
    }

    public boolean isUpdateTime() {
        return isUpdateTime;
    }

    public void setUpdateTime(boolean updateTime) {
        isUpdateTime = updateTime;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public ExternalAttributeStoreInfo getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(ExternalAttributeStoreInfo storeInfo) {
        this.storeInfo = storeInfo;
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


}
