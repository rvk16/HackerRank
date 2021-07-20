package com.amdocs.aia.il.configuration.export;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;


@JsonPropertyOrder(value = { "schemaKey","entityKey","isActive","entityName",  "description", "serializationId (READ ONLY)","isTransient",
        "isTransaction" })
public class ExternalEntityExportCSVDTO implements Serializable {

    @JsonProperty("schemaKey")
    private String schemaKey = null;

    @JsonProperty("entityKey")
    private String entityKey = null;

    @JsonProperty("isActive")
    private Boolean isActive = null;

    @JsonProperty("entityName")
    private String entityName = null;

    @JsonProperty("description")
    private String description = null;

    @JsonProperty("serializationId (READ ONLY)")
    private Integer serializationId = null;

    @JsonProperty("isTransient")
    private Boolean isTransient = null;

    @JsonProperty("isTransaction")
    private Boolean isTransaction = null;


    @JsonProperty("jsonTypeValue (kafka)")
    private String jsonTypeValue = null;

    @JsonProperty("jsonTypePath (kafka)")
    private String jsonTypePath = null;

    @JsonProperty("relativePaths (kafka)")
    private String relativePaths = null;

    @JsonProperty("mergedNodes (kafka)")
    private String mergedNodes = null;

    @JsonProperty("fileHeader (csv)")
    private Boolean fileHeader = null;

    @JsonProperty("fileNameFormat (csv)")
    private String fileNameFormat = null;

    @JsonProperty("dateFormat (csv)")
    private String dateFormat = null;

    @JsonProperty("columnDelimiter (csv)")
    private String columnDelimiter = null;

    @JsonProperty("invalidFilenameAction  (csv)")
    private String invalidFilenameAction = null;


    public ExternalEntityExportCSVDTO() {
    }

    public ExternalEntityExportCSVDTO(String schemaKey, String entityKey, Boolean isActive, String entityName, String description,
                                      Integer serializationId, Boolean isTransient, Boolean isTransaction, String jsonTypeValue,
                                      String jsonTypePath, String relativePaths, String mergedNodes, Boolean fileHeader, String fileNameFormat,
                                      String dateFormat, String columnDelimiter, String invalidFilenameAction) {
        this.schemaKey = schemaKey;
        this.entityKey = entityKey;
        this.isActive = isActive;
        this.entityName = entityName;
        this.description = description;
        this.serializationId = serializationId;
        this.isTransient = isTransient;
        this.isTransaction = isTransaction;
        this.jsonTypeValue = jsonTypeValue;
        this.jsonTypePath = jsonTypePath;
        this.relativePaths = relativePaths;
        this.mergedNodes = mergedNodes;
        this.fileHeader = fileHeader;
        this.fileNameFormat = fileNameFormat;
        this.dateFormat = dateFormat;
        this.columnDelimiter = columnDelimiter;
        this.invalidFilenameAction = invalidFilenameAction;
    }

    public String getSchemaKey() {
        return schemaKey;
    }

    public void setSchemaKey(String schemaKey) {
        this.schemaKey = schemaKey;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSerializationId() {
        return serializationId;
    }

    public void setSerializationId(Integer serializationId) {
        this.serializationId = serializationId;
    }

    public Boolean getTransient() {
        return isTransient;
    }

    public void setTransient(Boolean aTransient) {
        isTransient = aTransient;
    }

    public Boolean getTransaction() {
        return isTransaction;
    }

    public void setTransaction(Boolean transaction) {
        isTransaction = transaction;
    }

    public String getJsonTypeValue() {
        return jsonTypeValue;
    }

    public void setJsonTypeValue(String jsonTypeValue) {
        this.jsonTypeValue = jsonTypeValue;
    }

    public String getJsonTypePath() {
        return jsonTypePath;
    }

    public void setJsonTypePath(String jsonTypePath) {
        this.jsonTypePath = jsonTypePath;
    }

    public String getRelativePaths() {
        return relativePaths;
    }

    public void setRelativePaths(String relativePaths) {
        this.relativePaths = relativePaths;
    }

    public String getMergedNodes() {
        return mergedNodes;
    }

    public void setMergedNodes(String mergedNodes) {
        this.mergedNodes = mergedNodes;
    }

    public Boolean getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(Boolean fileHeader) {
        this.fileHeader = fileHeader;
    }

    public String getFileNameFormat() {
        return fileNameFormat;
    }

    public void setFileNameFormat(String fileNameFormat) {
        this.fileNameFormat = fileNameFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getColumnDelimiter() {
        return columnDelimiter;
    }

    public void setColumnDelimiter(String columnDelimiter) {
        this.columnDelimiter = columnDelimiter;
    }

    public String getInvalidFilenameAction() {
        return invalidFilenameAction;
    }

    public void setInvalidFilenameAction(String invalidFilenameAction) {
        this.invalidFilenameAction = invalidFilenameAction;
    }
}
