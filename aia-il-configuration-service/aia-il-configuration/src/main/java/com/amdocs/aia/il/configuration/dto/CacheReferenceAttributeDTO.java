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
 * CacheReferenceAttributeDTO
 */
@Validated


public class CacheReferenceAttributeDTO   {
  @JsonProperty("attributeKey")
  private String attributeKey = null;

  @JsonProperty("attributeName")
  private String attributeName = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("keyPosition")
  private Integer keyPosition = null;

  @JsonProperty("type")
  private String type = null;

  public CacheReferenceAttributeDTO attributeKey(String attributeKey) {
    this.attributeKey = attributeKey;
    return this;
  }

  /**
   * The Attribute key
   * @return attributeKey
  **/
  @ApiModelProperty(required = true, value = "The Attribute key")
  @NotNull


  public String getAttributeKey() {
    return attributeKey;
  }

  public void setAttributeKey(String attributeKey) {
    this.attributeKey = attributeKey;
  }

  public CacheReferenceAttributeDTO attributeName(String attributeName) {
    this.attributeName = attributeName;
    return this;
  }

  /**
   * The attribute's display name
   * @return attributeName
  **/
  @ApiModelProperty(value = "The attribute's display name")


  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public CacheReferenceAttributeDTO description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The Attribute description
   * @return description
  **/
  @ApiModelProperty(value = "The Attribute description")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CacheReferenceAttributeDTO keyPosition(Integer keyPosition) {
    this.keyPosition = keyPosition;
    return this;
  }

  /**
   * The ordinal position of the attribute inside the entity's primary key (Optional. Only relevant for the entity's primary key attributes)
   * @return keyPosition
  **/
  @ApiModelProperty(value = "The ordinal position of the attribute inside the entity's primary key (Optional. Only relevant for the entity's primary key attributes)")


  public Integer getKeyPosition() {
    return keyPosition;
  }

  public void setKeyPosition(Integer keyPosition) {
    this.keyPosition = keyPosition;
  }

  public CacheReferenceAttributeDTO type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Attribute data type
   * @return type
  **/
  @ApiModelProperty(required = true, value = "Attribute data type")
  @NotNull


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CacheReferenceAttributeDTO cacheReferenceAttribute = (CacheReferenceAttributeDTO) o;
    return Objects.equals(this.attributeKey, cacheReferenceAttribute.attributeKey) &&
        Objects.equals(this.attributeName, cacheReferenceAttribute.attributeName) &&
        Objects.equals(this.description, cacheReferenceAttribute.description) &&
        Objects.equals(this.keyPosition, cacheReferenceAttribute.keyPosition) &&
        Objects.equals(this.type, cacheReferenceAttribute.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributeKey, attributeName, description, keyPosition, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CacheReferenceAttributeDTO {\n");
    
    sb.append("    attributeKey: ").append(toIndentedString(attributeKey)).append("\n");
    sb.append("    attributeName: ").append(toIndentedString(attributeName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    keyPosition: ").append(toIndentedString(keyPosition)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

