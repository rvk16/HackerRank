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
 * TransformationAttributeDTO
 */
@Validated


public class TransformationAttributeDTO   {
  @JsonProperty("attributeKey")
  private String attributeKey = null;

  @JsonProperty("attributeName")
  private String attributeName = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("type")
  private String type = null;

  @JsonProperty("origin")
  private String origin = null;

  @JsonProperty("sourceMapping")
  private String sourceMapping = null;

  @JsonProperty("designSource")
  private String designSource = null;

  @JsonProperty("keyPosition")
  private Boolean keyPosition = null;

  @JsonProperty("sortOrder")
  private Integer sortOrder = null;

  @JsonProperty("isLogicalTime")
  private Boolean isLogicalTime = null;

  @JsonProperty("isUpdateTime")
  private Boolean isUpdateTime = null;

  @JsonProperty("isRequired")
  private Boolean isRequired = null;

  @JsonProperty("doReferencialIntegrity")
  private Boolean doReferencialIntegrity = null;

  @JsonProperty("parentSchemaKey")
  private String parentSchemaKey = null;

  @JsonProperty("parentEntityKey")
  private String parentEntityKey = null;

  @JsonProperty("parentAttributeKey")
  private String parentAttributeKey = null;

  public TransformationAttributeDTO attributeKey(String attributeKey) {
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

  public TransformationAttributeDTO attributeName(String attributeName) {
    this.attributeName = attributeName;
    return this;
  }

  /**
   * Attribute name
   * @return attributeName
  **/
  @ApiModelProperty(required = true, value = "Attribute name")
  @NotNull


  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public TransformationAttributeDTO description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Attribute description
   * @return description
  **/
  @ApiModelProperty(value = "Attribute description")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public TransformationAttributeDTO type(String type) {
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

  public TransformationAttributeDTO origin(String origin) {
    this.origin = origin;
    return this;
  }

  /**
   * Attribute origin
   * @return origin
  **/
  @ApiModelProperty(value = "Attribute origin")


  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public TransformationAttributeDTO sourceMapping(String sourceMapping) {
    this.sourceMapping = sourceMapping;
    return this;
  }

  /**
   * Attribute origin
   * @return sourceMapping
  **/
  @ApiModelProperty(value = "Attribute origin")


  public String getSourceMapping() {
    return sourceMapping;
  }

  public void setSourceMapping(String sourceMapping) {
    this.sourceMapping = sourceMapping;
  }

  public TransformationAttributeDTO designSource(String designSource) {
    this.designSource = designSource;
    return this;
  }

  /**
   * Attribute designSource
   * @return designSource
  **/
  @ApiModelProperty(value = "Attribute designSource")


  public String getDesignSource() {
    return designSource;
  }

  public void setDesignSource(String designSource) {
    this.designSource = designSource;
  }

  public TransformationAttributeDTO keyPosition(Boolean keyPosition) {
    this.keyPosition = keyPosition;
    return this;
  }

  /**
   * Is the attribute a primary key
   * @return keyPosition
  **/
  @ApiModelProperty(required = true, value = "Is the attribute a primary key")
  @NotNull


  public Boolean isKeyPosition() {
    return keyPosition;
  }

  public void setKeyPosition(Boolean keyPosition) {
    this.keyPosition = keyPosition;
  }

  public TransformationAttributeDTO sortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
    return this;
  }

  /**
   * Attribute sort order
   * @return sortOrder
  **/
  @ApiModelProperty(value = "Attribute sort order")


  public Integer getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }

  public TransformationAttributeDTO isLogicalTime(Boolean isLogicalTime) {
    this.isLogicalTime = isLogicalTime;
    return this;
  }

  /**
   * Attribute is logical time.
   * @return isLogicalTime
  **/
  @ApiModelProperty(value = "Attribute is logical time.")


  public Boolean isIsLogicalTime() {
    return isLogicalTime;
  }

  public void setIsLogicalTime(Boolean isLogicalTime) {
    this.isLogicalTime = isLogicalTime;
  }

  public TransformationAttributeDTO isUpdateTime(Boolean isUpdateTime) {
    this.isUpdateTime = isUpdateTime;
    return this;
  }

  /**
   * Attribute is update time.
   * @return isUpdateTime
  **/
  @ApiModelProperty(value = "Attribute is update time.")


  public Boolean isIsUpdateTime() {
    return isUpdateTime;
  }

  public void setIsUpdateTime(Boolean isUpdateTime) {
    this.isUpdateTime = isUpdateTime;
  }

  public TransformationAttributeDTO isRequired(Boolean isRequired) {
    this.isRequired = isRequired;
    return this;
  }

  /**
   * Attribute is required (true/false)
   * @return isRequired
  **/
  @ApiModelProperty(value = "Attribute is required (true/false)")


  public Boolean isIsRequired() {
    return isRequired;
  }

  public void setIsRequired(Boolean isRequired) {
    this.isRequired = isRequired;
  }

  public TransformationAttributeDTO doReferencialIntegrity(Boolean doReferencialIntegrity) {
    this.doReferencialIntegrity = doReferencialIntegrity;
    return this;
  }

  /**
   * boolean according to EntityReferentialIntegrity.
   * @return doReferencialIntegrity
  **/
  @ApiModelProperty(value = "boolean according to EntityReferentialIntegrity.")


  public Boolean isDoReferencialIntegrity() {
    return doReferencialIntegrity;
  }

  public void setDoReferencialIntegrity(Boolean doReferencialIntegrity) {
    this.doReferencialIntegrity = doReferencialIntegrity;
  }

  public TransformationAttributeDTO parentSchemaKey(String parentSchemaKey) {
    this.parentSchemaKey = parentSchemaKey;
    return this;
  }

  /**
   * The attribute parentSchemaKey from attributeRelation in LGE
   * @return parentSchemaKey
  **/
  @ApiModelProperty(value = "The attribute parentSchemaKey from attributeRelation in LGE")


  public String getParentSchemaKey() {
    return parentSchemaKey;
  }

  public void setParentSchemaKey(String parentSchemaKey) {
    this.parentSchemaKey = parentSchemaKey;
  }

  public TransformationAttributeDTO parentEntityKey(String parentEntityKey) {
    this.parentEntityKey = parentEntityKey;
    return this;
  }

  /**
   * The attribute parentEntityKey from attributeRelation in LGE
   * @return parentEntityKey
  **/
  @ApiModelProperty(value = "The attribute parentEntityKey from attributeRelation in LGE")


  public String getParentEntityKey() {
    return parentEntityKey;
  }

  public void setParentEntityKey(String parentEntityKey) {
    this.parentEntityKey = parentEntityKey;
  }

  public TransformationAttributeDTO parentAttributeKey(String parentAttributeKey) {
    this.parentAttributeKey = parentAttributeKey;
    return this;
  }

  /**
   * The attribute parentAttributeKey from attributeRelation in LGE
   * @return parentAttributeKey
  **/
  @ApiModelProperty(value = "The attribute parentAttributeKey from attributeRelation in LGE")


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
    TransformationAttributeDTO transformationAttribute = (TransformationAttributeDTO) o;
    return Objects.equals(this.attributeKey, transformationAttribute.attributeKey) &&
        Objects.equals(this.attributeName, transformationAttribute.attributeName) &&
        Objects.equals(this.description, transformationAttribute.description) &&
        Objects.equals(this.type, transformationAttribute.type) &&
        Objects.equals(this.origin, transformationAttribute.origin) &&
        Objects.equals(this.sourceMapping, transformationAttribute.sourceMapping) &&
        Objects.equals(this.designSource, transformationAttribute.designSource) &&
        Objects.equals(this.keyPosition, transformationAttribute.keyPosition) &&
        Objects.equals(this.sortOrder, transformationAttribute.sortOrder) &&
        Objects.equals(this.isLogicalTime, transformationAttribute.isLogicalTime) &&
        Objects.equals(this.isUpdateTime, transformationAttribute.isUpdateTime) &&
        Objects.equals(this.isRequired, transformationAttribute.isRequired) &&
        Objects.equals(this.doReferencialIntegrity, transformationAttribute.doReferencialIntegrity) &&
        Objects.equals(this.parentSchemaKey, transformationAttribute.parentSchemaKey) &&
        Objects.equals(this.parentEntityKey, transformationAttribute.parentEntityKey) &&
        Objects.equals(this.parentAttributeKey, transformationAttribute.parentAttributeKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributeKey, attributeName, description, type, origin, sourceMapping, designSource, keyPosition, sortOrder, isLogicalTime, isUpdateTime, isRequired, doReferencialIntegrity, parentSchemaKey, parentEntityKey, parentAttributeKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransformationAttributeDTO {\n");
    
    sb.append("    attributeKey: ").append(toIndentedString(attributeKey)).append("\n");
    sb.append("    attributeName: ").append(toIndentedString(attributeName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    origin: ").append(toIndentedString(origin)).append("\n");
    sb.append("    sourceMapping: ").append(toIndentedString(sourceMapping)).append("\n");
    sb.append("    designSource: ").append(toIndentedString(designSource)).append("\n");
    sb.append("    keyPosition: ").append(toIndentedString(keyPosition)).append("\n");
    sb.append("    sortOrder: ").append(toIndentedString(sortOrder)).append("\n");
    sb.append("    isLogicalTime: ").append(toIndentedString(isLogicalTime)).append("\n");
    sb.append("    isUpdateTime: ").append(toIndentedString(isUpdateTime)).append("\n");
    sb.append("    isRequired: ").append(toIndentedString(isRequired)).append("\n");
    sb.append("    doReferencialIntegrity: ").append(toIndentedString(doReferencialIntegrity)).append("\n");
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

