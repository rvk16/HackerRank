package com.amdocs.aia.il.configuration.exportimport;


import com.amdocs.aia.il.configuration.dto.InvalidFilenameActionTypeDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;


@JsonPropertyOrder(value = { "schemaKey","entityKey","isActive","entityName",  "description", "serializationId (READ ONLY)","isTransient",
        "isTransaction" })
public class ExternalEntityExportCSV extends AbstractCsvExternalModel implements Serializable {


    @JsonProperty(value = "entityKey",required = true)
    private String entityKey = null;

    @JsonProperty("isActive")
    private Boolean isActive = null;

    @JsonProperty(value = "entityName",required = true)
    private String entityName = null;

   //to ignore in import - check update
    @JsonProperty("serializationId (READ ONLY)")
    private Integer serializationId = null;

    @JsonProperty(value = "isTransient",required = true)
    private Boolean isTransient = null;

    @JsonProperty(value = "isTransaction",required = true)
    private Boolean isTransaction = null;


    @JsonProperty("jsonTypeValue (kafka)")
    private String jsonTypeValue = null;

    @JsonProperty("jsonTypePath (kafka)")
    private String jsonTypePath = null;

    @JsonProperty("relativePaths (kafka)")//check in code that if it is kafka it is mandatory
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
    private InvalidFilenameActionTypeDTO invalidFilenameAction = null;

       public ExternalEntityExportCSV() {
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

    public InvalidFilenameActionTypeDTO getInvalidFilenameAction() {
        return invalidFilenameAction;
    }

    public void setInvalidFilenameAction(InvalidFilenameActionTypeDTO invalidFilenameAction) {
        this.invalidFilenameAction = invalidFilenameAction;
    }
}
