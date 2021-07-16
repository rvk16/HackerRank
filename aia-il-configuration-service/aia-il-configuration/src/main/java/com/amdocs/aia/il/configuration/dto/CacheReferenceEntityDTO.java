package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.CacheReferenceAttributeDTO;
import com.amdocs.aia.il.configuration.dto.ChangeStatusDTO;
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
 * CacheReferenceEntityDTO
 */
@Validated


public class CacheReferenceEntityDTO   {
  @JsonProperty("cacheReferenceEntityKey")
  private String cacheReferenceEntityKey = null;

  @JsonProperty("cacheReferenceEntityName")
  private String cacheReferenceEntityName = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("cacheReferenceAttributes")
  @Valid
  private List<CacheReferenceAttributeDTO> cacheReferenceAttributes = new ArrayList<>();

  @JsonProperty("createdBy")
  private String createdBy = null;

  @JsonProperty("createdAt")
  private Long createdAt = null;

  @JsonProperty("status")
  private ChangeStatusDTO status = null;

  @JsonProperty("originProcess")
  private String originProcess = null;

  public CacheReferenceEntityDTO cacheReferenceEntityKey(String cacheReferenceEntityKey) {
    this.cacheReferenceEntityKey = cacheReferenceEntityKey;
    return this;
  }

  /**
   * Cache Reference Entity Key
   * @return cacheReferenceEntityKey
  **/
  @ApiModelProperty(required = true, value = "Cache Reference Entity Key")
  @NotNull


  public String getCacheReferenceEntityKey() {
    return cacheReferenceEntityKey;
  }

  public void setCacheReferenceEntityKey(String cacheReferenceEntityKey) {
    this.cacheReferenceEntityKey = cacheReferenceEntityKey;
  }

  public CacheReferenceEntityDTO cacheReferenceEntityName(String cacheReferenceEntityName) {
    this.cacheReferenceEntityName = cacheReferenceEntityName;
    return this;
  }

  /**
   * Cache Reference Entity Name
   * @return cacheReferenceEntityName
  **/
  @ApiModelProperty(value = "Cache Reference Entity Name")


  public String getCacheReferenceEntityName() {
    return cacheReferenceEntityName;
  }

  public void setCacheReferenceEntityName(String cacheReferenceEntityName) {
    this.cacheReferenceEntityName = cacheReferenceEntityName;
  }

  public CacheReferenceEntityDTO description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Cache Reference Entity Description
   * @return description
  **/
  @ApiModelProperty(value = "Cache Reference Entity Description")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CacheReferenceEntityDTO cacheReferenceAttributes(List<CacheReferenceAttributeDTO> cacheReferenceAttributes) {
    this.cacheReferenceAttributes = cacheReferenceAttributes;
    return this;
  }

  public CacheReferenceEntityDTO addCacheReferenceAttributesItem(CacheReferenceAttributeDTO cacheReferenceAttributesItem) {
    this.cacheReferenceAttributes.add(cacheReferenceAttributesItem);
    return this;
  }

  /**
   * Cache Reference Attributes
   * @return cacheReferenceAttributes
  **/
  @ApiModelProperty(required = true, value = "Cache Reference Attributes")
  @NotNull

  @Valid

  public List<CacheReferenceAttributeDTO> getCacheReferenceAttributes() {
    return cacheReferenceAttributes;
  }

  public void setCacheReferenceAttributes(List<CacheReferenceAttributeDTO> cacheReferenceAttributes) {
    this.cacheReferenceAttributes = cacheReferenceAttributes;
  }

  public CacheReferenceEntityDTO createdBy(String createdBy) {
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

  public CacheReferenceEntityDTO createdAt(Long createdAt) {
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

  public CacheReferenceEntityDTO status(ChangeStatusDTO status) {
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

  public CacheReferenceEntityDTO originProcess(String originProcess) {
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
    CacheReferenceEntityDTO cacheReferenceEntity = (CacheReferenceEntityDTO) o;
    return Objects.equals(this.cacheReferenceEntityKey, cacheReferenceEntity.cacheReferenceEntityKey) &&
        Objects.equals(this.cacheReferenceEntityName, cacheReferenceEntity.cacheReferenceEntityName) &&
        Objects.equals(this.description, cacheReferenceEntity.description) &&
        Objects.equals(this.cacheReferenceAttributes, cacheReferenceEntity.cacheReferenceAttributes) &&
        Objects.equals(this.createdBy, cacheReferenceEntity.createdBy) &&
        Objects.equals(this.createdAt, cacheReferenceEntity.createdAt) &&
        Objects.equals(this.status, cacheReferenceEntity.status) &&
        Objects.equals(this.originProcess, cacheReferenceEntity.originProcess);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cacheReferenceEntityKey, cacheReferenceEntityName, description, cacheReferenceAttributes, createdBy, createdAt, status, originProcess);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CacheReferenceEntityDTO {\n");
    
    sb.append("    cacheReferenceEntityKey: ").append(toIndentedString(cacheReferenceEntityKey)).append("\n");
    sb.append("    cacheReferenceEntityName: ").append(toIndentedString(cacheReferenceEntityName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    cacheReferenceAttributes: ").append(toIndentedString(cacheReferenceAttributes)).append("\n");
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

