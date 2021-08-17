package com.amdocs.aia.il.configuration.exportimport;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonPropertyOrder(value = { "schemaKey","entityKey","attributeKey","attributeName", "description", "datatype","logicalDatatype (read only)", "keyPosition",
        "isUpdateTime","isLogicalTime" , "isRequired", "defaultValue","validationRegex","dateFormat (csv, kafka)","jsonPath (kafka)",
        "serializationId (read only)","toDelete", })
public class ExternalAttributeExportCSV  extends AbstractCsvExternalModel implements Serializable {


    @JsonProperty(value = "entityKey",required = true)
    private String entityKey = null;

    @JsonProperty(value="attributeKey",required = true)
    private String attributeKey = null;

    @JsonProperty(value = "attributeName",required = true)
    private String attributeName = null;


    @JsonProperty(value = "datatype",required = true)
    private String datatype = null;

    //ignore service will fill
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

    //ignore
    @JsonProperty("serializationId (read only)")
    private Integer serializationId = null;



    public ExternalAttributeExportCSV() {
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

    public Integer getSerializationId() {
        return serializationId;
    }

    public void setSerializationId(Integer serializationId) {
        this.serializationId = serializationId;
    }
}
