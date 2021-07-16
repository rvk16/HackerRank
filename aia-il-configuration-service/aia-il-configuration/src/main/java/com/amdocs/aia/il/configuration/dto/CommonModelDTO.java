package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ChangeStatusDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Common fields for Replica objects
 */
@ApiModel(description = "Common fields for Replica objects")
@Validated

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "modelType", visible = true )
@JsonSubTypes({
  @JsonSubTypes.Type(value = TransformationDTO.class, name = "TRANSFORMATION"),
  @JsonSubTypes.Type(value = ContextDTO.class, name = "CONTEXT"),
})


public class CommonModelDTO   {
  /**
   * Common Model type
   */
  public enum ModelTypeEnum {
    CONTEXT("CONTEXT"),
    
    TRANSFORMATION("TRANSFORMATION");

    private String value;

    ModelTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ModelTypeEnum fromValue(String text) {
      for (ModelTypeEnum b : ModelTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("modelType")
  private ModelTypeEnum modelType = null;

  @JsonProperty("projectKey")
  private String projectKey = null;

  @JsonProperty("displayName")
  private String displayName = null;

  @JsonProperty("storeName")
  private String storeName = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("status")
  private ChangeStatusDTO status = null;

  @JsonProperty("originProcess")
  private String originProcess = null;

  @JsonProperty("createdBy")
  private String createdBy = null;

  @JsonProperty("createdAt")
  private OffsetDateTime createdAt = null;

  public CommonModelDTO modelType(ModelTypeEnum modelType) {
    this.modelType = modelType;
    return this;
  }

  /**
   * Common Model type
   * @return modelType
  **/
  @ApiModelProperty(required = true, value = "Common Model type")
  @NotNull


  public ModelTypeEnum getModelType() {
    return modelType;
  }

  public void setModelType(ModelTypeEnum modelType) {
    this.modelType = modelType;
  }

  public CommonModelDTO projectKey(String projectKey) {
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

  public CommonModelDTO displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * The Replica Entity display name
   * @return displayName
  **/
  @ApiModelProperty(value = "The Replica Entity display name")


  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public CommonModelDTO storeName(String storeName) {
    this.storeName = storeName;
    return this;
  }

  /**
   * The name of the Store Entity
   * @return storeName
  **/
  @ApiModelProperty(value = "The name of the Store Entity")


  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public CommonModelDTO description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The Replica Entity description
   * @return description
  **/
  @ApiModelProperty(value = "The Replica Entity description")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CommonModelDTO status(ChangeStatusDTO status) {
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

  public CommonModelDTO originProcess(String originProcess) {
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

  public CommonModelDTO createdBy(String createdBy) {
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

  public CommonModelDTO createdAt(OffsetDateTime createdAt) {
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
    CommonModelDTO commonModel = (CommonModelDTO) o;
    return Objects.equals(this.modelType, commonModel.modelType) &&
        Objects.equals(this.projectKey, commonModel.projectKey) &&
        Objects.equals(this.displayName, commonModel.displayName) &&
        Objects.equals(this.storeName, commonModel.storeName) &&
        Objects.equals(this.description, commonModel.description) &&
        Objects.equals(this.status, commonModel.status) &&
        Objects.equals(this.originProcess, commonModel.originProcess) &&
        Objects.equals(this.createdBy, commonModel.createdBy) &&
        Objects.equals(this.createdAt, commonModel.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(modelType, projectKey, displayName, storeName, description, status, originProcess, createdBy, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommonModelDTO {\n");
    
    sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
    sb.append("    projectKey: ").append(toIndentedString(projectKey)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    storeName: ").append(toIndentedString(storeName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

