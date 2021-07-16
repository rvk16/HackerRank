package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ContextDTO;
import com.amdocs.aia.il.configuration.dto.TransformationAttributeDTO;
import com.amdocs.aia.il.configuration.dto.TransformationDTO;
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
 * EntityTransformationDTO
 */
@Validated


public class EntityTransformationDTO   {
  @JsonProperty("logicalSchemaKey")
  private String logicalSchemaKey = null;

  @JsonProperty("logicalEntityKey")
  private String logicalEntityKey = null;

  @JsonProperty("entityName")
  private String entityName = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("attributes")
  @Valid
  private List<TransformationAttributeDTO> attributes = new ArrayList<>();

  @JsonProperty("transformations")
  @Valid
  private List<TransformationDTO> transformations = new ArrayList<>();

  @JsonProperty("contexts")
  @Valid
  private List<ContextDTO> contexts = new ArrayList<>();

  @JsonProperty("usedBy")
  @Valid
  private List<String> usedBy = null;

  public EntityTransformationDTO logicalSchemaKey(String logicalSchemaKey) {
    this.logicalSchemaKey = logicalSchemaKey;
    return this;
  }

  /**
   * Logical Schema Key
   * @return logicalSchemaKey
  **/
  @ApiModelProperty(required = true, value = "Logical Schema Key")
  @NotNull


  public String getLogicalSchemaKey() {
    return logicalSchemaKey;
  }

  public void setLogicalSchemaKey(String logicalSchemaKey) {
    this.logicalSchemaKey = logicalSchemaKey;
  }

  public EntityTransformationDTO logicalEntityKey(String logicalEntityKey) {
    this.logicalEntityKey = logicalEntityKey;
    return this;
  }

  /**
   * Logical Entity Key
   * @return logicalEntityKey
  **/
  @ApiModelProperty(required = true, value = "Logical Entity Key")
  @NotNull


  public String getLogicalEntityKey() {
    return logicalEntityKey;
  }

  public void setLogicalEntityKey(String logicalEntityKey) {
    this.logicalEntityKey = logicalEntityKey;
  }

  public EntityTransformationDTO entityName(String entityName) {
    this.entityName = entityName;
    return this;
  }

  /**
   * Entity Name
   * @return entityName
  **/
  @ApiModelProperty(value = "Entity Name")


  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public EntityTransformationDTO description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Entity description
   * @return description
  **/
  @ApiModelProperty(value = "Entity description")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public EntityTransformationDTO attributes(List<TransformationAttributeDTO> attributes) {
    this.attributes = attributes;
    return this;
  }

  public EntityTransformationDTO addAttributesItem(TransformationAttributeDTO attributesItem) {
    this.attributes.add(attributesItem);
    return this;
  }

  /**
   * Transformation Attributes
   * @return attributes
  **/
  @ApiModelProperty(required = true, value = "Transformation Attributes")
  @NotNull

  @Valid

  public List<TransformationAttributeDTO> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<TransformationAttributeDTO> attributes) {
    this.attributes = attributes;
  }

  public EntityTransformationDTO transformations(List<TransformationDTO> transformations) {
    this.transformations = transformations;
    return this;
  }

  public EntityTransformationDTO addTransformationsItem(TransformationDTO transformationsItem) {
    this.transformations.add(transformationsItem);
    return this;
  }

  /**
   * Transformations
   * @return transformations
  **/
  @ApiModelProperty(required = true, value = "Transformations")
  @NotNull

  @Valid

  public List<TransformationDTO> getTransformations() {
    return transformations;
  }

  public void setTransformations(List<TransformationDTO> transformations) {
    this.transformations = transformations;
  }

  public EntityTransformationDTO contexts(List<ContextDTO> contexts) {
    this.contexts = contexts;
    return this;
  }

  public EntityTransformationDTO addContextsItem(ContextDTO contextsItem) {
    this.contexts.add(contextsItem);
    return this;
  }

  /**
   * Contexts
   * @return contexts
  **/
  @ApiModelProperty(required = true, value = "Contexts")
  @NotNull

  @Valid

  public List<ContextDTO> getContexts() {
    return contexts;
  }

  public void setContexts(List<ContextDTO> contexts) {
    this.contexts = contexts;
  }

  public EntityTransformationDTO usedBy(List<String> usedBy) {
    this.usedBy = usedBy;
    return this;
  }

  public EntityTransformationDTO addUsedByItem(String usedByItem) {
    if (this.usedBy == null) {
      this.usedBy = new ArrayList<>();
    }
    this.usedBy.add(usedByItem);
    return this;
  }

  /**
   * Get usedBy
   * @return usedBy
  **/
  @ApiModelProperty(value = "")


  public List<String> getUsedBy() {
    return usedBy;
  }

  public void setUsedBy(List<String> usedBy) {
    this.usedBy = usedBy;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntityTransformationDTO entityTransformation = (EntityTransformationDTO) o;
    return Objects.equals(this.logicalSchemaKey, entityTransformation.logicalSchemaKey) &&
        Objects.equals(this.logicalEntityKey, entityTransformation.logicalEntityKey) &&
        Objects.equals(this.entityName, entityTransformation.entityName) &&
        Objects.equals(this.description, entityTransformation.description) &&
        Objects.equals(this.attributes, entityTransformation.attributes) &&
        Objects.equals(this.transformations, entityTransformation.transformations) &&
        Objects.equals(this.contexts, entityTransformation.contexts) &&
        Objects.equals(this.usedBy, entityTransformation.usedBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(logicalSchemaKey, logicalEntityKey, entityName, description, attributes, transformations, contexts, usedBy);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntityTransformationDTO {\n");
    
    sb.append("    logicalSchemaKey: ").append(toIndentedString(logicalSchemaKey)).append("\n");
    sb.append("    logicalEntityKey: ").append(toIndentedString(logicalEntityKey)).append("\n");
    sb.append("    entityName: ").append(toIndentedString(entityName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    attributes: ").append(toIndentedString(attributes)).append("\n");
    sb.append("    transformations: ").append(toIndentedString(transformations)).append("\n");
    sb.append("    contexts: ").append(toIndentedString(contexts)).append("\n");
    sb.append("    usedBy: ").append(toIndentedString(usedBy)).append("\n");
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

