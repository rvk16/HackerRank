package com.amdocs.aia.il.configuration.export;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonPropertyOrder(value = { "entityKey","attributeKey","attributeName", "description", "datatype","logicalDatatype (read only)", "keyPosition",
        "isUpdateTime","isLogicalTime" , "isRequired", "defaultValue","validationRegex","dateFormat (csv, kafka)","jsonPath (kafka)"})
public class ExternalAttributeExportCSVDTO implements Serializable {

    @JsonProperty("entityKey")
    private String entityKey = null;

    @JsonProperty("attributeKey")
    private String attributeKey = null;

    @JsonProperty("attributeName")
    private String attributeName = null;

    @JsonProperty("description")
    private String description = null;

    @JsonProperty("datatype")
    private String datatype = null;

    @JsonProperty("logicalDatatype (read only)")
    private String logicalDatatype = null;

    @JsonProperty("keyPosition")
    private Integer keyPosition = null;

    @JsonProperty("isUpdateTime")
    private Boolean isUpdateTime = null;

    @JsonProperty("isLogicalTime")
    private Boolean isLogicalTime = null;

    @JsonProperty("isRequired")
    private Boolean isRequired = null;

    @JsonProperty("defaultValue")
    private String defaultValue = null;

    @JsonProperty("validationRegex")
    private String validationRegex = null;

    @JsonProperty("dateFormat (csv, kafka)")
    private String dateFormat = null;

    @JsonProperty("jsonPath (kafka)")
    private String jsonPath = null;


    public ExternalAttributeExportCSVDTO() {
    }

    public ExternalAttributeExportCSVDTO(String entityKey,String attributeKey, String attributeName, String description, String datatype,
                                         String logicalDatatype, Integer keyPosition, Boolean isUpdateTime, Boolean isLogicalTime,
                                         Boolean isRequired, String defaultValue, String validationRegex, String dateFormat,
                                         String jsonPath) {
        this.entityKey = entityKey;
        this.attributeKey = attributeKey;
        this.attributeName = attributeName;
        this.description = description;
        this.datatype = datatype;
        this.logicalDatatype = logicalDatatype;
        this.keyPosition = keyPosition;
        this.isUpdateTime = isUpdateTime;
        this.isLogicalTime = isLogicalTime;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
        this.validationRegex = validationRegex;
        this.dateFormat = dateFormat;
        this.jsonPath = jsonPath;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getKeyPosition() {
        return keyPosition;
    }

    public void setKeyPosition(Integer keyPosition) {
        this.keyPosition = keyPosition;
    }

    public Boolean getUpdateTime() {
        return isUpdateTime;
    }

    public void setUpdateTime(Boolean updateTime) {
        isUpdateTime = updateTime;
    }

    public Boolean getLogicalTime() {
        return isLogicalTime;
    }

    public void setLogicalTime(Boolean logicalTime) {
        isLogicalTime = logicalTime;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }
}
