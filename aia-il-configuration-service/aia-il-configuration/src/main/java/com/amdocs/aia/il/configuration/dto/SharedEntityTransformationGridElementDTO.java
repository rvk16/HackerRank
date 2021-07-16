package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ChangeStatusDTO;
import com.amdocs.aia.il.configuration.dto.EntityTransformationGridElementDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * SharedEntityTransformationGridElementDTO
 */
@Validated


public class SharedEntityTransformationGridElementDTO extends EntityTransformationGridElementDTO  {
  @JsonProperty("entityName")
  private String entityName = null;

  @JsonProperty("entityKey")
  private String entityKey = null;

  @JsonProperty("subjectArea")
  private String subjectArea = null;

  @JsonProperty("entityType")
  private String entityType = null;

  @JsonProperty("creationDate")
  private String creationDate = null;

  @JsonProperty("status")
  private ChangeStatusDTO status = null;

  public SharedEntityTransformationGridElementDTO entityName(String entityName) {
    this.entityName = entityName;
    return this;
  }

  /**
   * Entity
   * @return entityName
  **/
  @ApiModelProperty(value = "Entity")


  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public SharedEntityTransformationGridElementDTO entityKey(String entityKey) {
    this.entityKey = entityKey;
    return this;
  }

  /**
   * Entity key
   * @return entityKey
  **/
  @ApiModelProperty(value = "Entity key")


  public String getEntityKey() {
    return entityKey;
  }

  public void setEntityKey(String entityKey) {
    this.entityKey = entityKey;
  }

  public SharedEntityTransformationGridElementDTO subjectArea(String subjectArea) {
    this.subjectArea = subjectArea;
    return this;
  }

  /**
   * Subject area
   * @return subjectArea
  **/
  @ApiModelProperty(value = "Subject area")


  public String getSubjectArea() {
    return subjectArea;
  }

  public void setSubjectArea(String subjectArea) {
    this.subjectArea = subjectArea;
  }

  public SharedEntityTransformationGridElementDTO entityType(String entityType) {
    this.entityType = entityType;
    return this;
  }

  /**
   * Entity type
   * @return entityType
  **/
  @ApiModelProperty(value = "Entity type")


  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public SharedEntityTransformationGridElementDTO creationDate(String creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  /**
   * Last modified by
   * @return creationDate
  **/
  @ApiModelProperty(value = "Last modified by")


  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public SharedEntityTransformationGridElementDTO status(ChangeStatusDTO status) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedEntityTransformationGridElementDTO sharedEntityTransformationGridElement = (SharedEntityTransformationGridElementDTO) o;
    return Objects.equals(this.entityName, sharedEntityTransformationGridElement.entityName) &&
        Objects.equals(this.entityKey, sharedEntityTransformationGridElement.entityKey) &&
        Objects.equals(this.subjectArea, sharedEntityTransformationGridElement.subjectArea) &&
        Objects.equals(this.entityType, sharedEntityTransformationGridElement.entityType) &&
        Objects.equals(this.creationDate, sharedEntityTransformationGridElement.creationDate) &&
        Objects.equals(this.status, sharedEntityTransformationGridElement.status) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityName, entityKey, subjectArea, entityType, creationDate, status, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedEntityTransformationGridElementDTO {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    entityName: ").append(toIndentedString(entityName)).append("\n");
    sb.append("    entityKey: ").append(toIndentedString(entityKey)).append("\n");
    sb.append("    subjectArea: ").append(toIndentedString(subjectArea)).append("\n");
    sb.append("    entityType: ").append(toIndentedString(entityType)).append("\n");
    sb.append("    creationDate: ").append(toIndentedString(creationDate)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

