package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ChangeStatusDTO;
import com.amdocs.aia.il.configuration.dto.ExternalAttributeDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityStoreInfoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * External Entity representation
 */
@ApiModel(description = "External Entity representation")
@Validated


public class ExternalEntityDTO   {
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

  @JsonProperty("serializationId")
  private Integer serializationId = null;

  @JsonProperty("isTransient")
  private Boolean isTransient = null;

  @JsonProperty("isTransaction")
  private Boolean isTransaction = null;

  @JsonProperty("storeInfo")
  private ExternalEntityStoreInfoDTO storeInfo = null;

  @JsonProperty("collectionRules")
  private ExternalEntityCollectionRulesDTO collectionRules = null;

  @JsonProperty("attributes")
  @Valid
  private List<ExternalAttributeDTO> attributes = null;

  @JsonProperty("createdBy")
  private String createdBy = null;

  @JsonProperty("createdAt")
  private Long createdAt = null;

  @JsonProperty("status")
  private ChangeStatusDTO status = null;

  @JsonProperty("originProcess")
  private String originProcess = null;

  public ExternalEntityDTO schemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
    return this;
  }

  /**
   * The external schema key
   * @return schemaKey
  **/
  @ApiModelProperty(value = "The external schema key")


  public String getSchemaKey() {
    return schemaKey;
  }

  public void setSchemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
  }

  public ExternalEntityDTO entityKey(String entityKey) {
    this.entityKey = entityKey;
    return this;
  }

  /**
   * The external entity key (unique only inside the scope of the schema)
   * @return entityKey
  **/
  @ApiModelProperty(value = "The external entity key (unique only inside the scope of the schema)")


  public String getEntityKey() {
    return entityKey;
  }

  public void setEntityKey(String entityKey) {
    this.entityKey = entityKey;
  }

  public ExternalEntityDTO isActive(Boolean isActive) {
    this.isActive = isActive;
    return this;
  }

  /**
   * Indicates whether this the system's integration with this external entity is currently active or not
   * @return isActive
  **/
  @ApiModelProperty(value = "Indicates whether this the system's integration with this external entity is currently active or not")


  public Boolean isIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public ExternalEntityDTO entityName(String entityName) {
    this.entityName = entityName;
    return this;
  }

  /**
   * The external entity's display name
   * @return entityName
  **/
  @ApiModelProperty(value = "The external entity's display name")


  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public ExternalEntityDTO description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A textual description of the entity
   * @return description
  **/
  @ApiModelProperty(value = "A textual description of the entity")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ExternalEntityDTO serializationId(Integer serializationId) {
    this.serializationId = serializationId;
    return this;
  }

  /**
   * The numeric identifier of the entity (unique inside the scope of the schema)
   * @return serializationId
  **/
  @ApiModelProperty(value = "The numeric identifier of the entity (unique inside the scope of the schema)")


  public Integer getSerializationId() {
    return serializationId;
  }

  public void setSerializationId(Integer serializationId) {
    this.serializationId = serializationId;
  }

  public ExternalEntityDTO isTransient(Boolean isTransient) {
    this.isTransient = isTransient;
    return this;
  }

  /**
   * Indicates whether this entity is transient or whether it is maintained in the internal replica store
   * @return isTransient
  **/
  @ApiModelProperty(value = "Indicates whether this entity is transient or whether it is maintained in the internal replica store")


  public Boolean isIsTransient() {
    return isTransient;
  }

  public void setIsTransient(Boolean isTransient) {
    this.isTransient = isTransient;
  }

  public ExternalEntityDTO isTransaction(Boolean isTransaction) {
    this.isTransaction = isTransaction;
    return this;
  }

  /**
   * Indicates whether this entity holds transactional data
   * @return isTransaction
  **/
  @ApiModelProperty(value = "Indicates whether this entity holds transactional data")


  public Boolean isIsTransaction() {
    return isTransaction;
  }

  public void setIsTransaction(Boolean isTransaction) {
    this.isTransaction = isTransaction;
  }

  public ExternalEntityDTO storeInfo(ExternalEntityStoreInfoDTO storeInfo) {
    this.storeInfo = storeInfo;
    return this;
  }

  /**
   * Specific store-specific inormation about the entity
   * @return storeInfo
  **/
  @ApiModelProperty(value = "Specific store-specific inormation about the entity")

  @Valid

  public ExternalEntityStoreInfoDTO getStoreInfo() {
    return storeInfo;
  }

  public void setStoreInfo(ExternalEntityStoreInfoDTO storeInfo) {
    this.storeInfo = storeInfo;
  }

  public ExternalEntityDTO collectionRules(ExternalEntityCollectionRulesDTO collectionRules) {
    this.collectionRules = collectionRules;
    return this;
  }

  /**
   * Entity-level information describing how data should be collected from the external schema for this entity
   * @return collectionRules
  **/
  @ApiModelProperty(value = "Entity-level information describing how data should be collected from the external schema for this entity")

  @Valid

  public ExternalEntityCollectionRulesDTO getCollectionRules() {
    return collectionRules;
  }

  public void setCollectionRules(ExternalEntityCollectionRulesDTO collectionRules) {
    this.collectionRules = collectionRules;
  }

  public ExternalEntityDTO attributes(List<ExternalAttributeDTO> attributes) {
    this.attributes = attributes;
    return this;
  }

  public ExternalEntityDTO addAttributesItem(ExternalAttributeDTO attributesItem) {
    if (this.attributes == null) {
      this.attributes = new ArrayList<>();
    }
    this.attributes.add(attributesItem);
    return this;
  }

  /**
   * The entity's attributes
   * @return attributes
  **/
  @ApiModelProperty(value = "The entity's attributes")

  @Valid

  public List<ExternalAttributeDTO> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<ExternalAttributeDTO> attributes) {
    this.attributes = attributes;
  }

  public ExternalEntityDTO createdBy(String createdBy) {
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

  public ExternalEntityDTO createdAt(Long createdAt) {
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

  public ExternalEntityDTO status(ChangeStatusDTO status) {
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

  public ExternalEntityDTO originProcess(String originProcess) {
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
    ExternalEntityDTO externalEntity = (ExternalEntityDTO) o;
    return Objects.equals(this.schemaKey, externalEntity.schemaKey) &&
        Objects.equals(this.entityKey, externalEntity.entityKey) &&
        Objects.equals(this.isActive, externalEntity.isActive) &&
        Objects.equals(this.entityName, externalEntity.entityName) &&
        Objects.equals(this.description, externalEntity.description) &&
        Objects.equals(this.serializationId, externalEntity.serializationId) &&
        Objects.equals(this.isTransient, externalEntity.isTransient) &&
        Objects.equals(this.isTransaction, externalEntity.isTransaction) &&
        Objects.equals(this.storeInfo, externalEntity.storeInfo) &&
        Objects.equals(this.collectionRules, externalEntity.collectionRules) &&
        Objects.equals(this.attributes, externalEntity.attributes) &&
        Objects.equals(this.createdBy, externalEntity.createdBy) &&
        Objects.equals(this.createdAt, externalEntity.createdAt) &&
        Objects.equals(this.status, externalEntity.status) &&
        Objects.equals(this.originProcess, externalEntity.originProcess);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemaKey, entityKey, isActive, entityName, description, serializationId, isTransient, isTransaction, storeInfo, collectionRules, attributes, createdBy, createdAt, status, originProcess);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalEntityDTO {\n");
    
    sb.append("    schemaKey: ").append(toIndentedString(schemaKey)).append("\n");
    sb.append("    entityKey: ").append(toIndentedString(entityKey)).append("\n");
    sb.append("    isActive: ").append(toIndentedString(isActive)).append("\n");
    sb.append("    entityName: ").append(toIndentedString(entityName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    serializationId: ").append(toIndentedString(serializationId)).append("\n");
    sb.append("    isTransient: ").append(toIndentedString(isTransient)).append("\n");
    sb.append("    isTransaction: ").append(toIndentedString(isTransaction)).append("\n");
    sb.append("    storeInfo: ").append(toIndentedString(storeInfo)).append("\n");
    sb.append("    collectionRules: ").append(toIndentedString(collectionRules)).append("\n");
    sb.append("    attributes: ").append(toIndentedString(attributes)).append("\n");
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

