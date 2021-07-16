package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ExternalAttributeStoreInfoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * An attribute of an external entity
 */
@ApiModel(description = "An attribute of an external entity")
@Validated


public class ExternalAttributeDTO   {
  @JsonProperty("attributeKey")
  private String attributeKey = null;

  @JsonProperty("attributeName")
  private String attributeName = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("datatype")
  private String datatype = null;

  @JsonProperty("logicalDatatype")
  private String logicalDatatype = null;

  @JsonProperty("serializationId")
  private Integer serializationId = null;

  @JsonProperty("keyPosition")
  private Integer keyPosition = null;

  @JsonProperty("isUpdateTime")
  private Boolean isUpdateTime = null;

  @JsonProperty("isLogicalTime")
  private Boolean isLogicalTime = null;

  @JsonProperty("isRequired")
  private Boolean isRequired = null;

  @JsonProperty("defaultValue")
  private String defaultValue = null;

  @JsonProperty("validationRegex")
  private String validationRegex = null;

  @JsonProperty("storeInfo")
  private ExternalAttributeStoreInfoDTO storeInfo = null;

  public ExternalAttributeDTO attributeKey(String attributeKey) {
    this.attributeKey = attributeKey;
    return this;
  }

  /**
   * The attribute's key (unique inside the scope of the entity)
   * @return attributeKey
  **/
  @ApiModelProperty(value = "The attribute's key (unique inside the scope of the entity)")


  public String getAttributeKey() {
    return attributeKey;
  }

  public void setAttributeKey(String attributeKey) {
    this.attributeKey = attributeKey;
  }

  public ExternalAttributeDTO attributeName(String attributeName) {
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

  public ExternalAttributeDTO description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A textual description of the attribute
   * @return description
  **/
  @ApiModelProperty(value = "A textual description of the attribute")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ExternalAttributeDTO datatype(String datatype) {
    this.datatype = datatype;
    return this;
  }

  /**
   * The datatype of the attribute, described according to the owner schema's type system
   * @return datatype
  **/
  @ApiModelProperty(value = "The datatype of the attribute, described according to the owner schema's type system")


  public String getDatatype() {
    return datatype;
  }

  public void setDatatype(String datatype) {
    this.datatype = datatype;
  }

  public ExternalAttributeDTO logicalDatatype(String logicalDatatype) {
    this.logicalDatatype = logicalDatatype;
    return this;
  }

  /**
   * The datatype of the attribute, described according to the logical type system
   * @return logicalDatatype
  **/
  @ApiModelProperty(readOnly = true, value = "The datatype of the attribute, described according to the logical type system")


  public String getLogicalDatatype() {
    return logicalDatatype;
  }

  public void setLogicalDatatype(String logicalDatatype) {
    this.logicalDatatype = logicalDatatype;
  }

  public ExternalAttributeDTO serializationId(Integer serializationId) {
    this.serializationId = serializationId;
    return this;
  }

  /**
   * The numeric identifier of the attribute (unique inside the scope of the entity)
   * @return serializationId
  **/
  @ApiModelProperty(value = "The numeric identifier of the attribute (unique inside the scope of the entity)")


  public Integer getSerializationId() {
    return serializationId;
  }

  public void setSerializationId(Integer serializationId) {
    this.serializationId = serializationId;
  }

  public ExternalAttributeDTO keyPosition(Integer keyPosition) {
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

  public ExternalAttributeDTO isUpdateTime(Boolean isUpdateTime) {
    this.isUpdateTime = isUpdateTime;
    return this;
  }

  /**
   * Indicates whether this attribute represents the entity's last update time (can be specified only for Date/DateTime attributes)
   * @return isUpdateTime
  **/
  @ApiModelProperty(value = "Indicates whether this attribute represents the entity's last update time (can be specified only for Date/DateTime attributes)")


  public Boolean isIsUpdateTime() {
    return isUpdateTime;
  }

  public void setIsUpdateTime(Boolean isUpdateTime) {
    this.isUpdateTime = isUpdateTime;
  }

  public ExternalAttributeDTO isLogicalTime(Boolean isLogicalTime) {
    this.isLogicalTime = isLogicalTime;
    return this;
  }

  /**
   * Indicates whether this attribute represents the entity's instance logical time (can be specified only for Date/DateTime attributes, for entity's that are time based, such as transactions, events, etc.)
   * @return isLogicalTime
  **/
  @ApiModelProperty(value = "Indicates whether this attribute represents the entity's instance logical time (can be specified only for Date/DateTime attributes, for entity's that are time based, such as transactions, events, etc.)")


  public Boolean isIsLogicalTime() {
    return isLogicalTime;
  }

  public void setIsLogicalTime(Boolean isLogicalTime) {
    this.isLogicalTime = isLogicalTime;
  }

  public ExternalAttributeDTO isRequired(Boolean isRequired) {
    this.isRequired = isRequired;
    return this;
  }

  /**
   * Indicates whether this attribute should always be populated in all instances of the entity
   * @return isRequired
  **/
  @ApiModelProperty(value = "Indicates whether this attribute should always be populated in all instances of the entity")


  public Boolean isIsRequired() {
    return isRequired;
  }

  public void setIsRequired(Boolean isRequired) {
    this.isRequired = isRequired;
  }

  public ExternalAttributeDTO defaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  /**
   * A default value for this attribute (Optional)
   * @return defaultValue
  **/
  @ApiModelProperty(value = "A default value for this attribute (Optional)")


  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public ExternalAttributeDTO validationRegex(String validationRegex) {
    this.validationRegex = validationRegex;
    return this;
  }

  /**
   * A regular expression string for validating the attribute's values (Optional)
   * @return validationRegex
  **/
  @ApiModelProperty(value = "A regular expression string for validating the attribute's values (Optional)")


  public String getValidationRegex() {
    return validationRegex;
  }

  public void setValidationRegex(String validationRegex) {
    this.validationRegex = validationRegex;
  }

  public ExternalAttributeDTO storeInfo(ExternalAttributeStoreInfoDTO storeInfo) {
    this.storeInfo = storeInfo;
    return this;
  }

  /**
   * Specific store-specific inormation about the attribute
   * @return storeInfo
  **/
  @ApiModelProperty(value = "Specific store-specific inormation about the attribute")

  @Valid

  public ExternalAttributeStoreInfoDTO getStoreInfo() {
    return storeInfo;
  }

  public void setStoreInfo(ExternalAttributeStoreInfoDTO storeInfo) {
    this.storeInfo = storeInfo;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalAttributeDTO externalAttribute = (ExternalAttributeDTO) o;
    return Objects.equals(this.attributeKey, externalAttribute.attributeKey) &&
        Objects.equals(this.attributeName, externalAttribute.attributeName) &&
        Objects.equals(this.description, externalAttribute.description) &&
        Objects.equals(this.datatype, externalAttribute.datatype) &&
        Objects.equals(this.logicalDatatype, externalAttribute.logicalDatatype) &&
        Objects.equals(this.serializationId, externalAttribute.serializationId) &&
        Objects.equals(this.keyPosition, externalAttribute.keyPosition) &&
        Objects.equals(this.isUpdateTime, externalAttribute.isUpdateTime) &&
        Objects.equals(this.isLogicalTime, externalAttribute.isLogicalTime) &&
        Objects.equals(this.isRequired, externalAttribute.isRequired) &&
        Objects.equals(this.defaultValue, externalAttribute.defaultValue) &&
        Objects.equals(this.validationRegex, externalAttribute.validationRegex) &&
        Objects.equals(this.storeInfo, externalAttribute.storeInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributeKey, attributeName, description, datatype, logicalDatatype, serializationId, keyPosition, isUpdateTime, isLogicalTime, isRequired, defaultValue, validationRegex, storeInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalAttributeDTO {\n");
    
    sb.append("    attributeKey: ").append(toIndentedString(attributeKey)).append("\n");
    sb.append("    attributeName: ").append(toIndentedString(attributeName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    datatype: ").append(toIndentedString(datatype)).append("\n");
    sb.append("    logicalDatatype: ").append(toIndentedString(logicalDatatype)).append("\n");
    sb.append("    serializationId: ").append(toIndentedString(serializationId)).append("\n");
    sb.append("    keyPosition: ").append(toIndentedString(keyPosition)).append("\n");
    sb.append("    isUpdateTime: ").append(toIndentedString(isUpdateTime)).append("\n");
    sb.append("    isLogicalTime: ").append(toIndentedString(isLogicalTime)).append("\n");
    sb.append("    isRequired: ").append(toIndentedString(isRequired)).append("\n");
    sb.append("    defaultValue: ").append(toIndentedString(defaultValue)).append("\n");
    sb.append("    validationRegex: ").append(toIndentedString(validationRegex)).append("\n");
    sb.append("    storeInfo: ").append(toIndentedString(storeInfo)).append("\n");
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

