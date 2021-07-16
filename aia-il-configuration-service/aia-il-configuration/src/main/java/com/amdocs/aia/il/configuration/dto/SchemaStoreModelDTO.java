package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ChangeStatusDTO;
import com.amdocs.aia.il.configuration.dto.NumericKeyAssignmentPolicyDTO;
import com.amdocs.aia.il.configuration.dto.SourceTargetTypeDTO;
import com.amdocs.aia.il.configuration.dto.StoreCategoryDTO;
import com.amdocs.aia.il.configuration.dto.StoreTypeDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Common fields for Schema Store objects
 */
@ApiModel(description = "Common fields for Schema Store objects")
@Validated

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "schemaStoreType", visible = true )
@JsonSubTypes({
})


public class SchemaStoreModelDTO   {
  /**
   * Schema Store type
   */
  public enum SchemaStoreTypeEnum {
    PHYSICALSCHEMA("PHYSICALSCHEMA"),
    
    DATACHANNELSCHEMA("DATACHANNELSCHEMA"),
    
    PUBLISHERSCHEMA("PUBLISHERSCHEMA"),
    
    PUBLISHERCACHESCHEMA("PUBLISHERCACHESCHEMA");

    private String value;

    SchemaStoreTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static SchemaStoreTypeEnum fromValue(String text) {
      for (SchemaStoreTypeEnum b : SchemaStoreTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("schemaStoreType")
  private SchemaStoreTypeEnum schemaStoreType = null;

  @JsonProperty("projectKey")
  private String projectKey = null;

  @JsonProperty("schemaName")
  private String schemaName = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("schemaStoreKey")
  private String schemaStoreKey = null;

  @JsonProperty("logicalSchemaKey")
  private String logicalSchemaKey = null;

  @JsonProperty("dataChannel")
  private String dataChannel = null;

  @JsonProperty("sourceTarget")
  private SourceTargetTypeDTO sourceTarget = null;

  @JsonProperty("storeType")
  private StoreTypeDTO storeType = null;

  @JsonProperty("typeSystem")
  private String typeSystem = null;

  @JsonProperty("isReference")
  private Boolean isReference = null;

  @JsonProperty("category")
  private StoreCategoryDTO category = null;

  @JsonProperty("numericKeyAssignmentPolicy")
  private NumericKeyAssignmentPolicyDTO numericKeyAssignmentPolicy = null;

  @JsonProperty("assignedEntityNumericKey")
  @Valid
  private Map<String, Integer> assignedEntityNumericKey = null;

  @JsonProperty("status")
  private ChangeStatusDTO status = null;

  @JsonProperty("originProcess")
  private String originProcess = null;

  @JsonProperty("createdBy")
  private String createdBy = null;

  @JsonProperty("createdAt")
  private OffsetDateTime createdAt = null;

  public SchemaStoreModelDTO schemaStoreType(SchemaStoreTypeEnum schemaStoreType) {
    this.schemaStoreType = schemaStoreType;
    return this;
  }

  /**
   * Schema Store type
   * @return schemaStoreType
  **/
  @ApiModelProperty(required = true, value = "Schema Store type")
  @NotNull


  public SchemaStoreTypeEnum getSchemaStoreType() {
    return schemaStoreType;
  }

  public void setSchemaStoreType(SchemaStoreTypeEnum schemaStoreType) {
    this.schemaStoreType = schemaStoreType;
  }

  public SchemaStoreModelDTO projectKey(String projectKey) {
    this.projectKey = projectKey;
    return this;
  }

  /**
   * The project key
   * @return projectKey
  **/
  @ApiModelProperty(value = "The project key")


  public String getProjectKey() {
    return projectKey;
  }

  public void setProjectKey(String projectKey) {
    this.projectKey = projectKey;
  }

  public SchemaStoreModelDTO schemaName(String schemaName) {
    this.schemaName = schemaName;
    return this;
  }

  /**
   * The name of the Source Schema
   * @return schemaName
  **/
  @ApiModelProperty(value = "The name of the Source Schema")


  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public SchemaStoreModelDTO description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The Source Schema description
   * @return description
  **/
  @ApiModelProperty(value = "The Source Schema description")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public SchemaStoreModelDTO schemaStoreKey(String schemaStoreKey) {
    this.schemaStoreKey = schemaStoreKey;
    return this;
  }

  /**
   * The Source Schema Store key
   * @return schemaStoreKey
  **/
  @ApiModelProperty(value = "The Source Schema Store key")


  public String getSchemaStoreKey() {
    return schemaStoreKey;
  }

  public void setSchemaStoreKey(String schemaStoreKey) {
    this.schemaStoreKey = schemaStoreKey;
  }

  public SchemaStoreModelDTO logicalSchemaKey(String logicalSchemaKey) {
    this.logicalSchemaKey = logicalSchemaKey;
    return this;
  }

  /**
   * The Logical Source Schema Store Key
   * @return logicalSchemaKey
  **/
  @ApiModelProperty(value = "The Logical Source Schema Store Key")


  public String getLogicalSchemaKey() {
    return logicalSchemaKey;
  }

  public void setLogicalSchemaKey(String logicalSchemaKey) {
    this.logicalSchemaKey = logicalSchemaKey;
  }

  public SchemaStoreModelDTO dataChannel(String dataChannel) {
    this.dataChannel = dataChannel;
    return this;
  }

  /**
   * Source schema data channel
   * @return dataChannel
  **/
  @ApiModelProperty(value = "Source schema data channel")


  public String getDataChannel() {
    return dataChannel;
  }

  public void setDataChannel(String dataChannel) {
    this.dataChannel = dataChannel;
  }

  public SchemaStoreModelDTO sourceTarget(SourceTargetTypeDTO sourceTarget) {
    this.sourceTarget = sourceTarget;
    return this;
  }

  /**
   * Get sourceTarget
   * @return sourceTarget
  **/
  @ApiModelProperty(value = "")

  @Valid

  public SourceTargetTypeDTO getSourceTarget() {
    return sourceTarget;
  }

  public void setSourceTarget(SourceTargetTypeDTO sourceTarget) {
    this.sourceTarget = sourceTarget;
  }

  public SchemaStoreModelDTO storeType(StoreTypeDTO storeType) {
    this.storeType = storeType;
    return this;
  }

  /**
   * Get storeType
   * @return storeType
  **/
  @ApiModelProperty(value = "")

  @Valid

  public StoreTypeDTO getStoreType() {
    return storeType;
  }

  public void setStoreType(StoreTypeDTO storeType) {
    this.storeType = storeType;
  }

  public SchemaStoreModelDTO typeSystem(String typeSystem) {
    this.typeSystem = typeSystem;
    return this;
  }

  /**
   * The Source Schema type system
   * @return typeSystem
  **/
  @ApiModelProperty(value = "The Source Schema type system")


  public String getTypeSystem() {
    return typeSystem;
  }

  public void setTypeSystem(String typeSystem) {
    this.typeSystem = typeSystem;
  }

  public SchemaStoreModelDTO isReference(Boolean isReference) {
    this.isReference = isReference;
    return this;
  }

  /**
   * The Reference Source Schema
   * @return isReference
  **/
  @ApiModelProperty(value = "The Reference Source Schema")


  public Boolean isIsReference() {
    return isReference;
  }

  public void setIsReference(Boolean isReference) {
    this.isReference = isReference;
  }

  public SchemaStoreModelDTO category(StoreCategoryDTO category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   * @return category
  **/
  @ApiModelProperty(value = "")

  @Valid

  public StoreCategoryDTO getCategory() {
    return category;
  }

  public void setCategory(StoreCategoryDTO category) {
    this.category = category;
  }

  public SchemaStoreModelDTO numericKeyAssignmentPolicy(NumericKeyAssignmentPolicyDTO numericKeyAssignmentPolicy) {
    this.numericKeyAssignmentPolicy = numericKeyAssignmentPolicy;
    return this;
  }

  /**
   * Get numericKeyAssignmentPolicy
   * @return numericKeyAssignmentPolicy
  **/
  @ApiModelProperty(value = "")

  @Valid

  public NumericKeyAssignmentPolicyDTO getNumericKeyAssignmentPolicy() {
    return numericKeyAssignmentPolicy;
  }

  public void setNumericKeyAssignmentPolicy(NumericKeyAssignmentPolicyDTO numericKeyAssignmentPolicy) {
    this.numericKeyAssignmentPolicy = numericKeyAssignmentPolicy;
  }

  public SchemaStoreModelDTO assignedEntityNumericKey(Map<String, Integer> assignedEntityNumericKey) {
    this.assignedEntityNumericKey = assignedEntityNumericKey;
    return this;
  }

  public SchemaStoreModelDTO putAssignedEntityNumericKeyItem(String key, Integer assignedEntityNumericKeyItem) {
    if (this.assignedEntityNumericKey == null) {
      this.assignedEntityNumericKey = new HashMap<>();
    }
    this.assignedEntityNumericKey.put(key, assignedEntityNumericKeyItem);
    return this;
  }

  /**
   * Get assignedEntityNumericKey
   * @return assignedEntityNumericKey
  **/
  @ApiModelProperty(value = "")


  public Map<String, Integer> getAssignedEntityNumericKey() {
    return assignedEntityNumericKey;
  }

  public void setAssignedEntityNumericKey(Map<String, Integer> assignedEntityNumericKey) {
    this.assignedEntityNumericKey = assignedEntityNumericKey;
  }

  public SchemaStoreModelDTO status(ChangeStatusDTO status) {
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

  public SchemaStoreModelDTO originProcess(String originProcess) {
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

  public SchemaStoreModelDTO createdBy(String createdBy) {
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

  public SchemaStoreModelDTO createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
  **/
  @ApiModelProperty(readOnly = true, value = "")

  @Valid

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SchemaStoreModelDTO schemaStoreModel = (SchemaStoreModelDTO) o;
    return Objects.equals(this.schemaStoreType, schemaStoreModel.schemaStoreType) &&
        Objects.equals(this.projectKey, schemaStoreModel.projectKey) &&
        Objects.equals(this.schemaName, schemaStoreModel.schemaName) &&
        Objects.equals(this.description, schemaStoreModel.description) &&
        Objects.equals(this.schemaStoreKey, schemaStoreModel.schemaStoreKey) &&
        Objects.equals(this.logicalSchemaKey, schemaStoreModel.logicalSchemaKey) &&
        Objects.equals(this.dataChannel, schemaStoreModel.dataChannel) &&
        Objects.equals(this.sourceTarget, schemaStoreModel.sourceTarget) &&
        Objects.equals(this.storeType, schemaStoreModel.storeType) &&
        Objects.equals(this.typeSystem, schemaStoreModel.typeSystem) &&
        Objects.equals(this.isReference, schemaStoreModel.isReference) &&
        Objects.equals(this.category, schemaStoreModel.category) &&
        Objects.equals(this.numericKeyAssignmentPolicy, schemaStoreModel.numericKeyAssignmentPolicy) &&
        Objects.equals(this.assignedEntityNumericKey, schemaStoreModel.assignedEntityNumericKey) &&
        Objects.equals(this.status, schemaStoreModel.status) &&
        Objects.equals(this.originProcess, schemaStoreModel.originProcess) &&
        Objects.equals(this.createdBy, schemaStoreModel.createdBy) &&
        Objects.equals(this.createdAt, schemaStoreModel.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemaStoreType, projectKey, schemaName, description, schemaStoreKey, logicalSchemaKey, dataChannel, sourceTarget, storeType, typeSystem, isReference, category, numericKeyAssignmentPolicy, assignedEntityNumericKey, status, originProcess, createdBy, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SchemaStoreModelDTO {\n");
    
    sb.append("    schemaStoreType: ").append(toIndentedString(schemaStoreType)).append("\n");
    sb.append("    projectKey: ").append(toIndentedString(projectKey)).append("\n");
    sb.append("    schemaName: ").append(toIndentedString(schemaName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    schemaStoreKey: ").append(toIndentedString(schemaStoreKey)).append("\n");
    sb.append("    logicalSchemaKey: ").append(toIndentedString(logicalSchemaKey)).append("\n");
    sb.append("    dataChannel: ").append(toIndentedString(dataChannel)).append("\n");
    sb.append("    sourceTarget: ").append(toIndentedString(sourceTarget)).append("\n");
    sb.append("    storeType: ").append(toIndentedString(storeType)).append("\n");
    sb.append("    typeSystem: ").append(toIndentedString(typeSystem)).append("\n");
    sb.append("    isReference: ").append(toIndentedString(isReference)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    numericKeyAssignmentPolicy: ").append(toIndentedString(numericKeyAssignmentPolicy)).append("\n");
    sb.append("    assignedEntityNumericKey: ").append(toIndentedString(assignedEntityNumericKey)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    originProcess: ").append(toIndentedString(originProcess)).append("\n");
    sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
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

