package com.amdocs.aia.il.common.model;

import com.amdocs.aia.common.model.ModelElement;

public class IntegrationLayerAttributeStore extends ModelElement {
    private static final long serialVersionUID = 9119891566164106328L;

    private String schemaStoreKey;
    private String entityStoreKey;
    private String attributeStoreKey;
    private String type;
    private int serializationId = -1;
    private Integer keyPosition;
    private boolean isLogicalTime;
    private boolean isUpdateTime;
    private boolean isRequired;
    private boolean doImplementAttribute;

    public String getSchemaStoreKey() {
        return schemaStoreKey;
    }

    public void setSchemaStoreKey(String schemaStoreKey) {
        this.schemaStoreKey = schemaStoreKey;
    }

    public String getEntityStoreKey() {
        return entityStoreKey;
    }

    public void setEntityStoreKey(String entityStoreKey) {
        this.entityStoreKey = entityStoreKey;
    }

    public String getAttributeStoreKey() {
        return attributeStoreKey;
    }

    public void setAttributeStoreKey(String attributeStoreKey) {
        this.attributeStoreKey = attributeStoreKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSerializationId() {
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

    public boolean isDoImplementAttribute() {
        return doImplementAttribute;
    }

    public void setDoImplementAttribute(boolean doImplementAttribute) {
        this.doImplementAttribute = doImplementAttribute;
    }
}