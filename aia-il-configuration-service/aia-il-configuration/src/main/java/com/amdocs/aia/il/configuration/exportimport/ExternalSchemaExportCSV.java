package com.amdocs.aia.il.configuration.exportimport;


import com.amdocs.aia.il.configuration.dto.AvailabilityDTO;
import com.amdocs.aia.il.configuration.dto.InvalidFilenameActionTypeDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonPropertyOrder(value = { "schemaKey","schemaName","schemaType","isActive",  "description", "typeSystem","isReference",
        "databaseType (sql)","defaultDateFormat (kafka, csv)","defaultColumnDelimiter (csv)","defaultInvalidFilenameAction (csv)",
        "inputDataChannel (kafka)","skipNodeFromParsing (kafka)","deleteEventJsonPath (kafka)","deleteEventOperation (kafka)",
        "implicitHandlerPreviousNode (kafka)","implicitHandlerCurrentNode (kafka)","serializationMethod",
        "availability","subjectAreaName"})
public class ExternalSchemaExportCSV extends AbstractCsvExternalModel implements Serializable {

    @JsonProperty(value = "schemaName",required = true)
    private String schemaName = null;

    @JsonProperty(value = "schemaType",required = true)
    private String schemaType = null;

    @JsonProperty("isActive")
    private Boolean isActive = Boolean.TRUE;

    @JsonProperty(value = "typeSystem",required = true)
    private String typeSystem = null;

    @JsonProperty(value = "isReference",required = true)
    private Boolean isReference = null;

    @JsonProperty("databaseType (sql)")
    private String databaseType = null;

    @JsonProperty("defaultDateFormat (kafka, csv)")
    private String defaultDateFormat = null;

    @JsonProperty("defaultColumnDelimiter (csv)")
    private String defaultColumnDelimiter = null;

    @JsonProperty("defaultInvalidFilenameAction (csv)")
    private InvalidFilenameActionTypeDTO defaultInvalidFilenameAction = null;

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

    @JsonProperty(value = "serializationMethod",required = true)
    private String serializationMethod = null;

    @JsonProperty("availability")
    private AvailabilityDTO availability = null;

    @JsonProperty("subjectAreaName")
    private String subjectAreaName = null;

    @JsonProperty("ongoingChannel")
    private String ongoingChannel = null;

    @JsonProperty("initialLoadChannel")
    private String initialLoadChannel = null;

    @JsonProperty("replayChannel")
    private String replayChannel = null;

    @JsonProperty("initialLoadRelativeURL")
    private String initialLoadRelativeURL = null;

    @JsonProperty("partialLoadRelativeURL")
    private String partialLoadRelativeURL = null;


    public ExternalSchemaExportCSV() {
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

    public InvalidFilenameActionTypeDTO getDefaultInvalidFilenameAction() {
        return defaultInvalidFilenameAction;
    }

    public void setDefaultInvalidFilenameAction(InvalidFilenameActionTypeDTO defaultInvalidFilenameAction) {
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

    public AvailabilityDTO getAvailability() {
        return availability;
    }

    public void setAvailability(AvailabilityDTO availability) {
        this.availability = availability;
    }

    public String getSubjectAreaName() {
        return subjectAreaName;
    }

    public void setSubjectAreaName(String subjectAreaName) {
        this.subjectAreaName = subjectAreaName;
    }

    public String getOngoingChannel() {
        return ongoingChannel;
    }

    public void setOngoingChannel(String ongoingChannel) {
        this.ongoingChannel = ongoingChannel;
    }

    public String getInitialLoadChannel() {
        return initialLoadChannel;
    }

    public void setInitialLoadChannel(String initialLoadChannel) {
        this.initialLoadChannel = initialLoadChannel;
    }

    public String getReplayChannel() {
        return replayChannel;
    }

    public void setReplayChannel(String replayChannel) {
        this.replayChannel = replayChannel;
    }

    public String getInitialLoadRelativeURL() {
        return initialLoadRelativeURL;
    }

    public void setInitialLoadRelativeURL(String initialLoadRelativeURL) {
        this.initialLoadRelativeURL = initialLoadRelativeURL;
    }

    public String getPartialLoadRelativeURL() {
        return partialLoadRelativeURL;
    }

    public void setPartialLoadRelativeURL(String partialLoadRelativeURL) {
        this.partialLoadRelativeURL = partialLoadRelativeURL;
    }


}
