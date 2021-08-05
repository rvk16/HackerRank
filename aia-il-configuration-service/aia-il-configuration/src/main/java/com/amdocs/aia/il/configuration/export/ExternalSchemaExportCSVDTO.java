package com.amdocs.aia.il.configuration.export;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonPropertyOrder(value = { "schemaKey","schemaName","schemaType","isActive",  "description", "typeSystem","isReference", "storeType",
        "databaseType (sql)","defaultDateFormat (kafka, csv)","defaultColumnDelimiter (csv)","defaultInvalidFilenameAction (csv)",
        "inputDataChannel (kafka)","skipNodeFromParsing (kafka)","deleteEventJsonPath (kafka)","deleteEventOperation (kafka)",
        "implicitHandlerPreviousNode (kafka)","implicitHandlerCurrentNode (kafka)","serializationMethod",
        "availability","subjectAreaName"})
public class ExternalSchemaExportCSVDTO implements Serializable {

    @JsonProperty("schemaKey")
    private String schemaKey = null;

    @JsonProperty("schemaName")
    private String schemaName = null;

    @JsonProperty("schemaType")
    private String schemaType = null;

      @JsonProperty("isActive")
    private Boolean isActive = null;

    @JsonProperty("description")
    private String description = null;

    @JsonProperty("typeSystem")
    private String typeSystem = null;

    @JsonProperty("isReference")
    private Boolean isReference = null;

    @JsonProperty("storeType")
    private String storeType = null;

    @JsonProperty("databaseType (sql)")
    private String databaseType = null;

    @JsonProperty("defaultDateFormat (kafka, csv)")
    private String defaultDateFormat = null;

    @JsonProperty("defaultColumnDelimiter (csv)")
    private String defaultColumnDelimiter = null;

    @JsonProperty("defaultInvalidFilenameAction (csv)")
    private String defaultInvalidFilenameAction = null;

    @JsonProperty("inputDataChannel (kafka)")
    private String inputDataChannel = null;

    @JsonProperty("skipNodeFromParsing (kafka)")
    private String skipNodeFromParsing = null;

    @JsonProperty("deleteEventJsonPath (kafka)")
    private String deleteEventJsonPath = null;

    @JsonProperty("deleteEventOperation (kafka)")
    private String deleteEventOperation = null;

    @JsonProperty("implicitHandlerPreviousNode (kafka)")
    private String implicitHandlerPreviousNode = null;

    @JsonProperty("implicitHandlerCurrentNode (kafka)")
    private String implicitHandlerCurrentNode = null;

    @JsonProperty("serializationMethod")
    private String serializationMethod = null;

    @JsonProperty("availability")
    private String availability = null;

    @JsonProperty("subjectAreaName")
    private String subjectAreaName = null;

    public ExternalSchemaExportCSVDTO() {
    }

    public ExternalSchemaExportCSVDTO(String schemaKey, String schemaName, String schemaType, Boolean isActive, String description,
                                      String typeSystem, Boolean isReference, String storeType, String databaseType, String defaultDateFormat,
                                      String defaultColumnDelimiter, String defaultInvalidFilenameAction, String inputDataChannel,
                                      String skipNodeFromParsing, String deleteEventJsonPath, String deleteEventOperation,
                                      String implicitHandlerPreviousNode, String implicitHandlerCurrentNode, String serializationMethod,
                                      String availability, String subjectAreaName) {
        this.schemaKey = schemaKey;
        this.schemaName = schemaName;
        this.schemaType = schemaType;
        this.isActive = isActive;
        this.description = description;
        this.typeSystem = typeSystem;
        this.isReference = isReference;
        this.storeType = storeType;
        this.databaseType = databaseType;
        this.defaultDateFormat = defaultDateFormat;
        this.defaultColumnDelimiter = defaultColumnDelimiter;
        this.defaultInvalidFilenameAction = defaultInvalidFilenameAction;
        this.inputDataChannel = inputDataChannel;
        this.skipNodeFromParsing = skipNodeFromParsing;
        this.deleteEventJsonPath = deleteEventJsonPath;
        this.deleteEventOperation = deleteEventOperation;
        this.implicitHandlerPreviousNode = implicitHandlerPreviousNode;
        this.implicitHandlerCurrentNode = implicitHandlerCurrentNode;
        this.serializationMethod = serializationMethod;
        this.availability = availability;
        this.subjectAreaName = subjectAreaName;
    }

    public String getSchemaKey() {
        return schemaKey;
    }

    public void setSchemaKey(String schemaKey) {
        this.schemaKey = schemaKey;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(String schemaType) {
        this.schemaType = schemaType;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeSystem() {
        return typeSystem;
    }

    public void setTypeSystem(String typeSystem) {
        this.typeSystem = typeSystem;
    }

    public Boolean getReference() {
        return isReference;
    }

    public void setReference(Boolean reference) {
        isReference = reference;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    public void setDefaultDateFormat(String defaultDateFormat) {
        this.defaultDateFormat = defaultDateFormat;
    }

    public String getDefaultColumnDelimiter() {
        return defaultColumnDelimiter;
    }

    public void setDefaultColumnDelimiter(String defaultColumnDelimiter) {
        this.defaultColumnDelimiter = defaultColumnDelimiter;
    }

    public String getDefaultInvalidFilenameAction() {
        return defaultInvalidFilenameAction;
    }

    public void setDefaultInvalidFilenameAction(String defaultInvalidFilenameAction) {
        this.defaultInvalidFilenameAction = defaultInvalidFilenameAction;
    }

    public String getInputDataChannel() {
        return inputDataChannel;
    }

    public void setInputDataChannel(String inputDataChannel) {
        this.inputDataChannel = inputDataChannel;
    }

    public String getSkipNodeFromParsing() {
        return skipNodeFromParsing;
    }

    public void setSkipNodeFromParsing(String skipNodeFromParsing) {
        this.skipNodeFromParsing = skipNodeFromParsing;
    }

    public String getDeleteEventJsonPath() {
        return deleteEventJsonPath;
    }

    public void setDeleteEventJsonPath(String deleteEventJsonPath) {
        this.deleteEventJsonPath = deleteEventJsonPath;
    }

    public String getDeleteEventOperation() {
        return deleteEventOperation;
    }

    public void setDeleteEventOperation(String deleteEventOperation) {
        this.deleteEventOperation = deleteEventOperation;
    }

    public String getImplicitHandlerPreviousNode() {
        return implicitHandlerPreviousNode;
    }

    public void setImplicitHandlerPreviousNode(String implicitHandlerPreviousNode) {
        this.implicitHandlerPreviousNode = implicitHandlerPreviousNode;
    }

    public String getImplicitHandlerCurrentNode() {
        return implicitHandlerCurrentNode;
    }

    public void setImplicitHandlerCurrentNode(String implicitHandlerCurrentNode) {
        this.implicitHandlerCurrentNode = implicitHandlerCurrentNode;
    }

    public String getSerializationMethod() {
        return serializationMethod;
    }

    public void setSerializationMethod(String serializationMethod) {
        this.serializationMethod = serializationMethod;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getSubjectAreaName() {
        return subjectAreaName;
    }

    public void setSubjectAreaName(String subjectAreaName) {
        this.subjectAreaName = subjectAreaName;
    }
}
