package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.AttributeStoreDTO;
import com.amdocs.aia.il.configuration.dto.ChangeStatusDTO;
import com.amdocs.aia.il.configuration.dto.StoreTypeDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Common fields for Entity Store objects
 */
@ApiModel(description = "Common fields for Entity Store objects")
@Validated

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "entityStoreType", visible = true )
@JsonSubTypes({
})


public class EntityStoreModelDTO   {
  /**
   * Schema Store type
   */
  public enum EntityStoreTypeEnum {
    PUBLISHERCACHEENTITY("PUBLISHERCACHEENTITY");

    private String value;

    EntityStoreTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static EntityStoreTypeEnum fromValue(String text) {
      for (EntityStoreTypeEnum b : EntityStoreTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("entityStoreType")
  private EntityStoreTypeEnum entityStoreType = null;

  @JsonProperty("projectKey")
  private String projectKey = null;

  @JsonProperty("entityName")
  private String entityName = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("schemaStoreKey")
  private String schemaStoreKey = null;

  @JsonProperty("logicalSchemaKey")
  private String logicalSchemaKey = null;

  @JsonProperty("entityStoreKey")
  private String entityStoreKey = null;

  @JsonProperty("logicalEntityKey")
  private String logicalEntityKey = null;

  @JsonProperty("storeType")
  private StoreTypeDTO storeType = null;

  @JsonProperty("serializationId")
  private Integer serializationId = null;

  @JsonProperty("assignedAttributeNumericKey")
  @Valid
  private Map<String, Integer> assignedAttributeNumericKey = null;

  @JsonProperty("attributeStores")
  @Valid
  private List<AttributeStoreDTO> attributeStores = null;

  @JsonProperty("status")
  private ChangeStatusDTO status = null;

  @JsonProperty("originProcess")
  private String originProcess = null;

  @JsonProperty("createdBy")
  private String createdBy = null;

  @JsonProperty("createdAt")
  private OffsetDateTime createdAt = null;

  public EntityStoreModelDTO entityStoreType(EntityStoreTypeEnum entityStoreType) {
    this.entityStoreType = entityStoreType;
    return this;
  }

  /**
   * Schema Store type
   * @return entityStoreType
  **/
  @ApiModelProperty(required = true, value = "Schema Store type")
  @NotNull


  public EntityStoreTypeEnum getEntityStoreType() {
    return entityStoreType;
  }

  public void setEntityStoreType(EntityStoreTypeEnum entityStoreType) {
    this.entityStoreType = entityStoreType;
  }

  public EntityStoreModelDTO projectKey(String projectKey) {
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

  public EntityStoreModelDTO entityName(String entityName) {
    this.entityName = entityName;
    return this;
  }

  /**
   * The name of the Source Schema Entity
   * @return entityName
  **/
  @ApiModelProperty(value = "The name of the Source Schema Entity")


  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public EntityStoreModelDTO description(String description) {
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

  public EntityStoreModelDTO schemaStoreKey(String schemaStoreKey) {
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

  public EntityStoreModelDTO logicalSchemaKey(String logicalSchemaKey) {
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

  public EntityStoreModelDTO entityStoreKey(String entityStoreKey) {
    this.entityStoreKey = entityStoreKey;
    return this;
  }

  /**
   * The Source entity Store key
   * @return entityStoreKey
  **/
  @ApiModelProperty(value = "The Source entity Store key")


  public String getEntityStoreKey() {
    return entityStoreKey;
  }

  public void setEntityStoreKey(String entityStoreKey) {
    this.entityStoreKey = entityStoreKey;
  }

  public EntityStoreModelDTO logicalEntityKey(String logicalEntityKey) {
    this.logicalEntityKey = logicalEntityKey;
    return this;
  }

  /**
   * The Logical entity Store Key
   * @return logicalEntityKey
  **/
  @ApiModelProperty(value = "The Logical entity Store Key")


  public String getLogicalEntityKey() {
    return logicalEntityKey;
  }

  public void setLogicalEntityKey(String logicalEntityKey) {
    this.logicalEntityKey = logicalEntityKey;
  }

  public EntityStoreModelDTO storeType(StoreTypeDTO storeType) {
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

  public EntityStoreModelDTO serializationId(Integer serializationId) {
    this.serializationId = serializationId;
    return this;
  }

  /**
   * The Source entity serialization id
   * @return serializationId
  **/
  @ApiModelProperty(value = "The Source entity serialization id")


  public Integer getSerializationId() {
    return serializationId;
  }

  public void setSerializationId(Integer serializationId) {
    this.serializationId = serializationId;
  }

  public EntityStoreModelDTO assignedAttributeNumericKey(Map<String, Integer> assignedAttributeNumericKey) {
    this.assignedAttributeNumericKey = assignedAttributeNumericKey;
    return this;
  }

  public EntityStoreModelDTO putAssignedAttributeNumericKeyItem(String key, Integer assignedAttributeNumericKeyItem) {
    if (this.assignedAttributeNumericKey == null) {
      this.assignedAttributeNumericKey = new HashMap<>();
    }
    this.assignedAttributeNumericKey.put(key, assignedAttributeNumericKeyItem);
    return this;
  }

  /**
   * Get assignedAttributeNumericKey
   * @return assignedAttributeNumericKey
  **/
  @ApiModelProperty(value = "")


  public Map<String, Integer> getAssignedAttributeNumericKey() {
    return assignedAttributeNumericKey;
  }

  public void setAssignedAttributeNumericKey(Map<String, Integer> assignedAttributeNumericKey) {
    this.assignedAttributeNumericKey = assignedAttributeNumericKey;
  }

  public EntityStoreModelDTO attributeStores(List<AttributeStoreDTO> attributeStores) {
    this.attributeStores = attributeStores;
    return this;
  }

  public EntityStoreModelDTO addAttributeStoresItem(AttributeStoreDTO attributeStoresItem) {
    if (this.attributeStores == null) {
      this.attributeStores = new ArrayList<>();
    }
    this.attributeStores.add(attributeStoresItem);
    return this;
  }

  /**
   * Attributes of Entity Store
   * @return attributeStores
  **/
  @ApiModelProperty(value = "Attributes of Entity Store")

  @Valid

  public List<AttributeStoreDTO> getAttributeStores() {
    return attributeStores;
  }

  public void setAttributeStores(List<AttributeStoreDTO> attributeStores) {
    this.attributeStores = attributeStores;
  }

  public EntityStoreModelDTO status(ChangeStatusDTO status) {
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

  public EntityStoreModelDTO originProcess(String originProcess) {
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

  public EntityStoreModelDTO createdBy(String createdBy) {
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

  public EntityStoreModelDTO createdAt(OffsetDateTime createdAt) {
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
    EntityStoreModelDTO entityStoreModel = (EntityStoreModelDTO) o;
    return Objects.equals(this.entityStoreType, entityStoreModel.entityStoreType) &&
        Objects.equals(this.projectKey, entityStoreModel.projectKey) &&
        Objects.equals(this.entityName, entityStoreModel.entityName) &&
        Objects.equals(this.description, entityStoreModel.description) &&
        Objects.equals(this.schemaStoreKey, entityStoreModel.schemaStoreKey) &&
        Objects.equals(this.logicalSchemaKey, entityStoreModel.logicalSchemaKey) &&
        Objects.equals(this.entityStoreKey, entityStoreModel.entityStoreKey) &&
        Objects.equals(this.logicalEntityKey, entityStoreModel.logicalEntityKey) &&
        Objects.equals(this.storeType, entityStoreModel.storeType) &&
        Objects.equals(this.serializationId, entityStoreModel.serializationId) &&
        Objects.equals(this.assignedAttributeNumericKey, entityStoreModel.assignedAttributeNumericKey) &&
        Objects.equals(this.attributeStores, entityStoreModel.attributeStores) &&
        Objects.equals(this.status, entityStoreModel.status) &&
        Objects.equals(this.originProcess, entityStoreModel.originProcess) &&
        Objects.equals(this.createdBy, entityStoreModel.createdBy) &&
        Objects.equals(this.createdAt, entityStoreModel.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityStoreType, projectKey, entityName, description, schemaStoreKey, logicalSchemaKey, entityStoreKey, logicalEntityKey, storeType, serializationId, assignedAttributeNumericKey, attributeStores, status, originProcess, createdBy, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntityStoreModelDTO {\n");
    
    sb.append("    entityStoreType: ").append(toIndentedString(entityStoreType)).append("\n");
    sb.append("    projectKey: ").append(toIndentedString(projectKey)).append("\n");
    sb.append("    entityName: ").append(toIndentedString(entityName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    schemaStoreKey: ").append(toIndentedString(schemaStoreKey)).append("\n");
    sb.append("    logicalSchemaKey: ").append(toIndentedString(logicalSchemaKey)).append("\n");
    sb.append("    entityStoreKey: ").append(toIndentedString(entityStoreKey)).append("\n");
    sb.append("    logicalEntityKey: ").append(toIndentedString(logicalEntityKey)).append("\n");
    sb.append("    storeType: ").append(toIndentedString(storeType)).append("\n");
    sb.append("    serializationId: ").append(toIndentedString(serializationId)).append("\n");
    sb.append("    assignedAttributeNumericKey: ").append(toIndentedString(assignedAttributeNumericKey)).append("\n");
    sb.append("    attributeStores: ").append(toIndentedString(attributeStores)).append("\n");
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

