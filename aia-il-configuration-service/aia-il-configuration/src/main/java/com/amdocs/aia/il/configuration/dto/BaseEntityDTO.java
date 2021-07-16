package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.BaseAttributeDTO;
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
 * BaseEntityDTO
 */
@Validated


public class BaseEntityDTO   {
  @JsonProperty("entityKey")
  private String entityKey = null;

  @JsonProperty("entityName")
  private String entityName = null;

  @JsonProperty("schemaKey")
  private String schemaKey = null;

  @JsonProperty("attributes")
  @Valid
  private List<BaseAttributeDTO> attributes = null;

  public BaseEntityDTO entityKey(String entityKey) {
    this.entityKey = entityKey;
    return this;
  }

  /**
   * Entity key
   * @return entityKey
  **/
  @ApiModelProperty(required = true, value = "Entity key")
  @NotNull


  public String getEntityKey() {
    return entityKey;
  }

  public void setEntityKey(String entityKey) {
    this.entityKey = entityKey;
  }

  public BaseEntityDTO entityName(String entityName) {
    this.entityName = entityName;
    return this;
  }

  /**
   * Entity name
   * @return entityName
  **/
  @ApiModelProperty(value = "Entity name")


  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public BaseEntityDTO schemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
    return this;
  }

  /**
   * Schema key
   * @return schemaKey
  **/
  @ApiModelProperty(value = "Schema key")


  public String getSchemaKey() {
    return schemaKey;
  }

  public void setSchemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
  }

  public BaseEntityDTO attributes(List<BaseAttributeDTO> attributes) {
    this.attributes = attributes;
    return this;
  }

  public BaseEntityDTO addAttributesItem(BaseAttributeDTO attributesItem) {
    if (this.attributes == null) {
      this.attributes = new ArrayList<>();
    }
    this.attributes.add(attributesItem);
    return this;
  }

  /**
   * List of attributes
   * @return attributes
  **/
  @ApiModelProperty(value = "List of attributes")

  @Valid

  public List<BaseAttributeDTO> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<BaseAttributeDTO> attributes) {
    this.attributes = attributes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseEntityDTO baseEntity = (BaseEntityDTO) o;
    return Objects.equals(this.entityKey, baseEntity.entityKey) &&
        Objects.equals(this.entityName, baseEntity.entityName) &&
        Objects.equals(this.schemaKey, baseEntity.schemaKey) &&
        Objects.equals(this.attributes, baseEntity.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityKey, entityName, schemaKey, attributes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseEntityDTO {\n");
    
    sb.append("    entityKey: ").append(toIndentedString(entityKey)).append("\n");
    sb.append("    entityName: ").append(toIndentedString(entityName)).append("\n");
    sb.append("    schemaKey: ").append(toIndentedString(schemaKey)).append("\n");
    sb.append("    attributes: ").append(toIndentedString(attributes)).append("\n");
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

