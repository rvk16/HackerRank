package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.AttributeStorePropertyDTO;
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
 * Attribute Store representation
 */
@ApiModel(description = "Attribute Store representation")
@Validated


public class AttributeStoreDTO   {
  @JsonProperty("schemaStoreKey")
  private String schemaStoreKey = null;

  @JsonProperty("entityStoreKey")
  private String entityStoreKey = null;

  @JsonProperty("attributeStoreKey")
  private String attributeStoreKey = null;

  @JsonProperty("serializationId")
  private Integer serializationId = null;

  @JsonProperty("type")
  private String type = null;

  @JsonProperty("keyPosition")
  private Integer keyPosition = null;

  @JsonProperty("isUpdateTime")
  private Boolean isUpdateTime = null;

  @JsonProperty("isLogicalTime")
  private Boolean isLogicalTime = null;

  @JsonProperty("isRequired")
  private Boolean isRequired = null;

  @JsonProperty("doImplementAttribute")
  private Boolean doImplementAttribute = null;

  @JsonProperty("dynamicProperties")
  @Valid
  private List<AttributeStorePropertyDTO> dynamicProperties = null;

  public AttributeStoreDTO schemaStoreKey(String schemaStoreKey) {
    this.schemaStoreKey = schemaStoreKey;
    return this;
  }

  /**
   * The schema store key
   * @return schemaStoreKey
  **/
  @ApiModelProperty(value = "The schema store key")


  public String getSchemaStoreKey() {
    return schemaStoreKey;
  }

  public void setSchemaStoreKey(String schemaStoreKey) {
    this.schemaStoreKey = schemaStoreKey;
  }

  public AttributeStoreDTO entityStoreKey(String entityStoreKey) {
    this.entityStoreKey = entityStoreKey;
    return this;
  }

  /**
   * The entity store key
   * @return entityStoreKey
  **/
  @ApiModelProperty(value = "The entity store key")


  public String getEntityStoreKey() {
    return entityStoreKey;
  }

  public void setEntityStoreKey(String entityStoreKey) {
    this.entityStoreKey = entityStoreKey;
  }

  public AttributeStoreDTO attributeStoreKey(String attributeStoreKey) {
    this.attributeStoreKey = attributeStoreKey;
    return this;
  }

  /**
   * The attribute store key
   * @return attributeStoreKey
  **/
  @ApiModelProperty(value = "The attribute store key")


  public String getAttributeStoreKey() {
    return attributeStoreKey;
  }

  public void setAttributeStoreKey(String attributeStoreKey) {
    this.attributeStoreKey = attributeStoreKey;
  }

  public AttributeStoreDTO serializationId(Integer serializationId) {
    this.serializationId = serializationId;
    return this;
  }

  /**
   * The serialization ID
   * @return serializationId
  **/
  @ApiModelProperty(value = "The serialization ID")


  public Integer getSerializationId() {
    return serializationId;
  }

  public void setSerializationId(Integer serializationId) {
    this.serializationId = serializationId;
  }

  public AttributeStoreDTO type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Attribute data type
   * @return type
  **/
  @ApiModelProperty(value = "Attribute data type")


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public AttributeStoreDTO keyPosition(Integer keyPosition) {
    this.keyPosition = keyPosition;
    return this;
  }

  /**
   * Primary Key Columns
   * @return keyPosition
  **/
  @ApiModelProperty(value = "Primary Key Columns")


  public Integer getKeyPosition() {
    return keyPosition;
  }

  public void setKeyPosition(Integer keyPosition) {
    this.keyPosition = keyPosition;
  }

  public AttributeStoreDTO isUpdateTime(Boolean isUpdateTime) {
    this.isUpdateTime = isUpdateTime;
    return this;
  }

  /**
   * Update time column field
   * @return isUpdateTime
  **/
  @ApiModelProperty(value = "Update time column field")


  public Boolean isIsUpdateTime() {
    return isUpdateTime;
  }

  public void setIsUpdateTime(Boolean isUpdateTime) {
    this.isUpdateTime = isUpdateTime;
  }

  public AttributeStoreDTO isLogicalTime(Boolean isLogicalTime) {
    this.isLogicalTime = isLogicalTime;
    return this;
  }

  /**
   * Logical time column field
   * @return isLogicalTime
  **/
  @ApiModelProperty(value = "Logical time column field")


  public Boolean isIsLogicalTime() {
    return isLogicalTime;
  }

  public void setIsLogicalTime(Boolean isLogicalTime) {
    this.isLogicalTime = isLogicalTime;
  }

  public AttributeStoreDTO isRequired(Boolean isRequired) {
    this.isRequired = isRequired;
    return this;
  }

  /**
   * Required Field or Not
   * @return isRequired
  **/
  @ApiModelProperty(value = "Required Field or Not")


  public Boolean isIsRequired() {
    return isRequired;
  }

  public void setIsRequired(Boolean isRequired) {
    this.isRequired = isRequired;
  }

  public AttributeStoreDTO doImplementAttribute(Boolean doImplementAttribute) {
    this.doImplementAttribute = doImplementAttribute;
    return this;
  }

  /**
   * The flag to identify attribute need to be implemented or not
   * @return doImplementAttribute
  **/
  @ApiModelProperty(value = "The flag to identify attribute need to be implemented or not")


  public Boolean isDoImplementAttribute() {
    return doImplementAttribute;
  }

  public void setDoImplementAttribute(Boolean doImplementAttribute) {
    this.doImplementAttribute = doImplementAttribute;
  }

  public AttributeStoreDTO dynamicProperties(List<AttributeStorePropertyDTO> dynamicProperties) {
    this.dynamicProperties = dynamicProperties;
    return this;
  }

  public AttributeStoreDTO addDynamicPropertiesItem(AttributeStorePropertyDTO dynamicPropertiesItem) {
    if (this.dynamicProperties == null) {
      this.dynamicProperties = new ArrayList<>();
    }
    this.dynamicProperties.add(dynamicPropertiesItem);
    return this;
  }

  /**
   * Additional properties for attribute store
   * @return dynamicProperties
  **/
  @ApiModelProperty(value = "Additional properties for attribute store")

  @Valid

  public List<AttributeStorePropertyDTO> getDynamicProperties() {
    return dynamicProperties;
  }

  public void setDynamicProperties(List<AttributeStorePropertyDTO> dynamicProperties) {
    this.dynamicProperties = dynamicProperties;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttributeStoreDTO attributeStore = (AttributeStoreDTO) o;
    return Objects.equals(this.schemaStoreKey, attributeStore.schemaStoreKey) &&
        Objects.equals(this.entityStoreKey, attributeStore.entityStoreKey) &&
        Objects.equals(this.attributeStoreKey, attributeStore.attributeStoreKey) &&
        Objects.equals(this.serializationId, attributeStore.serializationId) &&
        Objects.equals(this.type, attributeStore.type) &&
        Objects.equals(this.keyPosition, attributeStore.keyPosition) &&
        Objects.equals(this.isUpdateTime, attributeStore.isUpdateTime) &&
        Objects.equals(this.isLogicalTime, attributeStore.isLogicalTime) &&
        Objects.equals(this.isRequired, attributeStore.isRequired) &&
        Objects.equals(this.doImplementAttribute, attributeStore.doImplementAttribute) &&
        Objects.equals(this.dynamicProperties, attributeStore.dynamicProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemaStoreKey, entityStoreKey, attributeStoreKey, serializationId, type, keyPosition, isUpdateTime, isLogicalTime, isRequired, doImplementAttribute, dynamicProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttributeStoreDTO {\n");
    
    sb.append("    schemaStoreKey: ").append(toIndentedString(schemaStoreKey)).append("\n");
    sb.append("    entityStoreKey: ").append(toIndentedString(entityStoreKey)).append("\n");
    sb.append("    attributeStoreKey: ").append(toIndentedString(attributeStoreKey)).append("\n");
    sb.append("    serializationId: ").append(toIndentedString(serializationId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    keyPosition: ").append(toIndentedString(keyPosition)).append("\n");
    sb.append("    isUpdateTime: ").append(toIndentedString(isUpdateTime)).append("\n");
    sb.append("    isLogicalTime: ").append(toIndentedString(isLogicalTime)).append("\n");
    sb.append("    isRequired: ").append(toIndentedString(isRequired)).append("\n");
    sb.append("    doImplementAttribute: ").append(toIndentedString(doImplementAttribute)).append("\n");
    sb.append("    dynamicProperties: ").append(toIndentedString(dynamicProperties)).append("\n");
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

