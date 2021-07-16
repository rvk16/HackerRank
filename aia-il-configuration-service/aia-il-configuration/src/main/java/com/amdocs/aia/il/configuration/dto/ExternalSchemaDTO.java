package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.AvailabilityDTO;
import com.amdocs.aia.il.configuration.dto.ChangeStatusDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaDataChannelInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaStoreInfoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * External Schema representation
 */
@ApiModel(description = "External Schema representation")
@Validated


public class ExternalSchemaDTO   {
  @JsonProperty("schemaKey")
  private String schemaKey = null;

  @JsonProperty("schemaName")
  private String schemaName = null;

  @JsonProperty("schemaType")
  private String schemaType = null;

  @JsonProperty("displayType")
  private String displayType = null;

  @JsonProperty("isActive")
  private Boolean isActive = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("typeSystem")
  private String typeSystem = null;

  @JsonProperty("isReference")
  private Boolean isReference = null;

  @JsonProperty("storeInfo")
  private ExternalSchemaStoreInfoDTO storeInfo = null;

  @JsonProperty("collectionRules")
  private ExternalSchemaCollectionRulesDTO collectionRules = null;

  @JsonProperty("dataChannelInfo")
  private ExternalSchemaDataChannelInfoDTO dataChannelInfo = null;

  @JsonProperty("ongoingCollector")
  private String ongoingCollector = null;

  @JsonProperty("initialCollector")
  private String initialCollector = null;

  @JsonProperty("selectiveCollector")
  private String selectiveCollector = null;

  @JsonProperty("availability")
  private AvailabilityDTO availability = null;

  @JsonProperty("subjectAreaName")
  private String subjectAreaName = null;

  @JsonProperty("subjectAreaKey")
  private String subjectAreaKey = null;

  @JsonProperty("createdBy")
  private String createdBy = null;

  @JsonProperty("createdAt")
  private Long createdAt = null;

  @JsonProperty("status")
  private ChangeStatusDTO status = null;

  @JsonProperty("originProcess")
  private String originProcess = null;

  public ExternalSchemaDTO schemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
    return this;
  }

  /**
   * The external schema key
   * @return schemaKey
  **/
  @ApiModelProperty(required = true, value = "The external schema key")
  @NotNull


  public String getSchemaKey() {
    return schemaKey;
  }

  public void setSchemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
  }

  public ExternalSchemaDTO schemaName(String schemaName) {
    this.schemaName = schemaName;
    return this;
  }

  /**
   * The external schema's display name
   * @return schemaName
  **/
  @ApiModelProperty(required = true, value = "The external schema's display name")
  @NotNull


  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public ExternalSchemaDTO schemaType(String schemaType) {
    this.schemaType = schemaType;
    return this;
  }

  /**
   * The external schema's type (available types can be retrieved via the 'list external schema types' API)
   * @return schemaType
  **/
  @ApiModelProperty(value = "The external schema's type (available types can be retrieved via the 'list external schema types' API)")


  public String getSchemaType() {
    return schemaType;
  }

  public void setSchemaType(String schemaType) {
    this.schemaType = schemaType;
  }

  public ExternalSchemaDTO displayType(String displayType) {
    this.displayType = displayType;
    return this;
  }

  /**
   * Human-readable text describing the schema store type (typically used for display purposes)
   * @return displayType
  **/
  @ApiModelProperty(value = "Human-readable text describing the schema store type (typically used for display purposes)")


  public String getDisplayType() {
    return displayType;
  }

  public void setDisplayType(String displayType) {
    this.displayType = displayType;
  }

  public ExternalSchemaDTO isActive(Boolean isActive) {
    this.isActive = isActive;
    return this;
  }

  /**
   * Indicates whether this the system's integration with this schema is currently active or not
   * @return isActive
  **/
  @ApiModelProperty(value = "Indicates whether this the system's integration with this schema is currently active or not")


  public Boolean isIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public ExternalSchemaDTO description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A textual description of the schema
   * @return description
  **/
  @ApiModelProperty(value = "A textual description of the schema")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ExternalSchemaDTO typeSystem(String typeSystem) {
    this.typeSystem = typeSystem;
    return this;
  }

  /**
   * The unique name of the type system in which attribute datatypes are described. This value must match one of the type systems that are supported by the integration layer runtime engine.
   * @return typeSystem
  **/
  @ApiModelProperty(required = true, value = "The unique name of the type system in which attribute datatypes are described. This value must match one of the type systems that are supported by the integration layer runtime engine.")
  @NotNull


  public String getTypeSystem() {
    return typeSystem;
  }

  public void setTypeSystem(String typeSystem) {
    this.typeSystem = typeSystem;
  }

  public ExternalSchemaDTO isReference(Boolean isReference) {
    this.isReference = isReference;
    return this;
  }

  /**
   * Indicates whether all the entities in this schema are REFERENCE entities
   * @return isReference
  **/
  @ApiModelProperty(required = true, value = "Indicates whether all the entities in this schema are REFERENCE entities")
  @NotNull


  public Boolean isIsReference() {
    return isReference;
  }

  public void setIsReference(Boolean isReference) {
    this.isReference = isReference;
  }

  public ExternalSchemaDTO storeInfo(ExternalSchemaStoreInfoDTO storeInfo) {
    this.storeInfo = storeInfo;
    return this;
  }

  /**
   * Specific store-specific inormation about the schema
   * @return storeInfo
  **/
  @ApiModelProperty(value = "Specific store-specific inormation about the schema")

  @Valid

  public ExternalSchemaStoreInfoDTO getStoreInfo() {
    return storeInfo;
  }

  public void setStoreInfo(ExternalSchemaStoreInfoDTO storeInfo) {
    this.storeInfo = storeInfo;
  }

  public ExternalSchemaDTO collectionRules(ExternalSchemaCollectionRulesDTO collectionRules) {
    this.collectionRules = collectionRules;
    return this;
  }

  /**
   * Schema-level information describing how data should be collected from this external schema
   * @return collectionRules
  **/
  @ApiModelProperty(value = "Schema-level information describing how data should be collected from this external schema")

  @Valid

  public ExternalSchemaCollectionRulesDTO getCollectionRules() {
    return collectionRules;
  }

  public void setCollectionRules(ExternalSchemaCollectionRulesDTO collectionRules) {
    this.collectionRules = collectionRules;
  }

  public ExternalSchemaDTO dataChannelInfo(ExternalSchemaDataChannelInfoDTO dataChannelInfo) {
    this.dataChannelInfo = dataChannelInfo;
    return this;
  }

  /**
   * Get dataChannelInfo
   * @return dataChannelInfo
  **/
  @ApiModelProperty(value = "")

  @Valid

  public ExternalSchemaDataChannelInfoDTO getDataChannelInfo() {
    return dataChannelInfo;
  }

  public void setDataChannelInfo(ExternalSchemaDataChannelInfoDTO dataChannelInfo) {
    this.dataChannelInfo = dataChannelInfo;
  }

  public ExternalSchemaDTO ongoingCollector(String ongoingCollector) {
    this.ongoingCollector = ongoingCollector;
    return this;
  }

  /**
   * The collector used for loading ongoing changes
   * @return ongoingCollector
  **/
  @ApiModelProperty(value = "The collector used for loading ongoing changes")


  public String getOngoingCollector() {
    return ongoingCollector;
  }

  public void setOngoingCollector(String ongoingCollector) {
    this.ongoingCollector = ongoingCollector;
  }

  public ExternalSchemaDTO initialCollector(String initialCollector) {
    this.initialCollector = initialCollector;
    return this;
  }

  /**
   * The collector used for initial load
   * @return initialCollector
  **/
  @ApiModelProperty(value = "The collector used for initial load")


  public String getInitialCollector() {
    return initialCollector;
  }

  public void setInitialCollector(String initialCollector) {
    this.initialCollector = initialCollector;
  }

  public ExternalSchemaDTO selectiveCollector(String selectiveCollector) {
    this.selectiveCollector = selectiveCollector;
    return this;
  }

  /**
   * The collector used for selectively loading entities (e.g. for reconciliation)
   * @return selectiveCollector
  **/
  @ApiModelProperty(value = "The collector used for selectively loading entities (e.g. for reconciliation)")


  public String getSelectiveCollector() {
    return selectiveCollector;
  }

  public void setSelectiveCollector(String selectiveCollector) {
    this.selectiveCollector = selectiveCollector;
  }

  public ExternalSchemaDTO availability(AvailabilityDTO availability) {
    this.availability = availability;
    return this;
  }

  /**
   * Get availability
   * @return availability
  **/
  @ApiModelProperty(value = "")

  @Valid

  public AvailabilityDTO getAvailability() {
    return availability;
  }

  public void setAvailability(AvailabilityDTO availability) {
    this.availability = availability;
  }

  public ExternalSchemaDTO subjectAreaName(String subjectAreaName) {
    this.subjectAreaName = subjectAreaName;
    return this;
  }

  /**
   * The external schema's subjectArea name
   * @return subjectAreaName
  **/
  @ApiModelProperty(value = "The external schema's subjectArea name")


  public String getSubjectAreaName() {
    return subjectAreaName;
  }

  public void setSubjectAreaName(String subjectAreaName) {
    this.subjectAreaName = subjectAreaName;
  }

  public ExternalSchemaDTO subjectAreaKey(String subjectAreaKey) {
    this.subjectAreaKey = subjectAreaKey;
    return this;
  }

  /**
   * The external schema's subjectArea key
   * @return subjectAreaKey
  **/
  @ApiModelProperty(value = "The external schema's subjectArea key")


  public String getSubjectAreaKey() {
    return subjectAreaKey;
  }

  public void setSubjectAreaKey(String subjectAreaKey) {
    this.subjectAreaKey = subjectAreaKey;
  }

  public ExternalSchemaDTO createdBy(String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  /**
   * Get createdBy
   * @return createdBy
  **/
  @ApiModelProperty(readOnly = true, value = "")


  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public ExternalSchemaDTO createdAt(Long createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
  **/
  @ApiModelProperty(readOnly = true, value = "")


  public Long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
  }

  public ExternalSchemaDTO status(ChangeStatusDTO status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  **/
  @ApiModelProperty(value = "")

  @Valid

  public ChangeStatusDTO getStatus() {
    return status;
  }

  public void setStatus(ChangeStatusDTO status) {
    this.status = status;
  }

  public ExternalSchemaDTO originProcess(String originProcess) {
    this.originProcess = originProcess;
    return this;
  }

  /**
   * The Origin Process
   * @return originProcess
  **/
  @ApiModelProperty(value = "The Origin Process")


  public String getOriginProcess() {
    return originProcess;
  }

  public void setOriginProcess(String originProcess) {
    this.originProcess = originProcess;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalSchemaDTO externalSchema = (ExternalSchemaDTO) o;
    return Objects.equals(this.schemaKey, externalSchema.schemaKey) &&
        Objects.equals(this.schemaName, externalSchema.schemaName) &&
        Objects.equals(this.schemaType, externalSchema.schemaType) &&
        Objects.equals(this.displayType, externalSchema.displayType) &&
        Objects.equals(this.isActive, externalSchema.isActive) &&
        Objects.equals(this.description, externalSchema.description) &&
        Objects.equals(this.typeSystem, externalSchema.typeSystem) &&
        Objects.equals(this.isReference, externalSchema.isReference) &&
        Objects.equals(this.storeInfo, externalSchema.storeInfo) &&
        Objects.equals(this.collectionRules, externalSchema.collectionRules) &&
        Objects.equals(this.dataChannelInfo, externalSchema.dataChannelInfo) &&
        Objects.equals(this.ongoingCollector, externalSchema.ongoingCollector) &&
        Objects.equals(this.initialCollector, externalSchema.initialCollector) &&
        Objects.equals(this.selectiveCollector, externalSchema.selectiveCollector) &&
        Objects.equals(this.availability, externalSchema.availability) &&
        Objects.equals(this.subjectAreaName, externalSchema.subjectAreaName) &&
        Objects.equals(this.subjectAreaKey, externalSchema.subjectAreaKey) &&
        Objects.equals(this.createdBy, externalSchema.createdBy) &&
        Objects.equals(this.createdAt, externalSchema.createdAt) &&
        Objects.equals(this.status, externalSchema.status) &&
        Objects.equals(this.originProcess, externalSchema.originProcess);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemaKey, schemaName, schemaType, displayType, isActive, description, typeSystem, isReference, storeInfo, collectionRules, dataChannelInfo, ongoingCollector, initialCollector, selectiveCollector, availability, subjectAreaName, subjectAreaKey, createdBy, createdAt, status, originProcess);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalSchemaDTO {\n");
    
    sb.append("    schemaKey: ").append(toIndentedString(schemaKey)).append("\n");
    sb.append("    schemaName: ").append(toIndentedString(schemaName)).append("\n");
    sb.append("    schemaType: ").append(toIndentedString(schemaType)).append("\n");
    sb.append("    displayType: ").append(toIndentedString(displayType)).append("\n");
    sb.append("    isActive: ").append(toIndentedString(isActive)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    typeSystem: ").append(toIndentedString(typeSystem)).append("\n");
    sb.append("    isReference: ").append(toIndentedString(isReference)).append("\n");
    sb.append("    storeInfo: ").append(toIndentedString(storeInfo)).append("\n");
    sb.append("    collectionRules: ").append(toIndentedString(collectionRules)).append("\n");
    sb.append("    dataChannelInfo: ").append(toIndentedString(dataChannelInfo)).append("\n");
    sb.append("    ongoingCollector: ").append(toIndentedString(ongoingCollector)).append("\n");
    sb.append("    initialCollector: ").append(toIndentedString(initialCollector)).append("\n");
    sb.append("    selectiveCollector: ").append(toIndentedString(selectiveCollector)).append("\n");
    sb.append("    availability: ").append(toIndentedString(availability)).append("\n");
    sb.append("    subjectAreaName: ").append(toIndentedString(subjectAreaName)).append("\n");
    sb.append("    subjectAreaKey: ").append(toIndentedString(subjectAreaKey)).append("\n");
    sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    originProcess: ").append(toIndentedString(originProcess)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

