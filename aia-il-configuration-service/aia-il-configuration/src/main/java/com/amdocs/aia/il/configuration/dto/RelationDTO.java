package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * RelationDTO
 */
@Validated


public class RelationDTO   {
  @JsonProperty("attributeKey")
  private String attributeKey = null;

  @JsonProperty("parentSchemaKey")
  private String parentSchemaKey = null;

  @JsonProperty("parentEntityKey")
  private String parentEntityKey = null;

  @JsonProperty("parentAttributeKey")
  private String parentAttributeKey = null;

  public RelationDTO attributeKey(String attributeKey) {
    this.attributeKey = attributeKey;
    return this;
  }

  /**
   * Attribute key
   * @return attributeKey
  **/
  @ApiModelProperty(required = true, value = "Attribute key")
  @NotNull


  public String getAttributeKey() {
    return attributeKey;
  }

  public void setAttributeKey(String attributeKey) {
    this.attributeKey = attributeKey;
  }

  public RelationDTO parentSchemaKey(String parentSchemaKey) {
    this.parentSchemaKey = parentSchemaKey;
    return this;
  }

  /**
   * The attribute parentSchemaKey form Transformation
   * @return parentSchemaKey
  **/
  @ApiModelProperty(required = true, value = "The attribute parentSchemaKey form Transformation")
  @NotNull


  public String getParentSchemaKey() {
    return parentSchemaKey;
  }

  public void setParentSchemaKey(String parentSchemaKey) {
    this.parentSchemaKey = parentSchemaKey;
  }

  public RelationDTO parentEntityKey(String parentEntityKey) {
    this.parentEntityKey = parentEntityKey;
    return this;
  }

  /**
   * The attribute parentEntityKey form Transformation
   * @return parentEntityKey
  **/
  @ApiModelProperty(required = true, value = "The attribute parentEntityKey form Transformation")
  @NotNull


  public String getParentEntityKey() {
    return parentEntityKey;
  }

  public void setParentEntityKey(String parentEntityKey) {
    this.parentEntityKey = parentEntityKey;
  }

  public RelationDTO parentAttributeKey(String parentAttributeKey) {
    this.parentAttributeKey = parentAttributeKey;
    return this;
  }

  /**
   * The attribute parentAttributeKey form Transformation
   * @return parentAttributeKey
  **/
  @ApiModelProperty(required = true, value = "The attribute parentAttributeKey form Transformation")
  @NotNull


  public String getParentAttributeKey() {
    return parentAttributeKey;
  }

  public void setParentAttributeKey(String parentAttributeKey) {
    this.parentAttributeKey = parentAttributeKey;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RelationDTO relation = (RelationDTO) o;
    return Objects.equals(this.attributeKey, relation.attributeKey) &&
        Objects.equals(this.parentSchemaKey, relation.parentSchemaKey) &&
        Objects.equals(this.parentEntityKey, relation.parentEntityKey) &&
        Objects.equals(this.parentAttributeKey, relation.parentAttributeKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributeKey, parentSchemaKey, parentEntityKey, parentAttributeKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RelationDTO {\n");
    
    sb.append("    attributeKey: ").append(toIndentedString(attributeKey)).append("\n");
    sb.append("    parentSchemaKey: ").append(toIndentedString(parentSchemaKey)).append("\n");
    sb.append("    parentEntityKey: ").append(toIndentedString(parentEntityKey)).append("\n");
    sb.append("    parentAttributeKey: ").append(toIndentedString(parentAttributeKey)).append("\n");
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

