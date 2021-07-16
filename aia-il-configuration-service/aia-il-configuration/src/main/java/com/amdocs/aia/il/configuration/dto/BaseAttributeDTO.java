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
 * BaseAttributeDTO
 */
@Validated


public class BaseAttributeDTO   {
  @JsonProperty("attributeKey")
  private String attributeKey = null;

  @JsonProperty("attributeName")
  private String attributeName = null;

  @JsonProperty("datatype")
  private String datatype = null;

  @JsonProperty("keyPosition")
  private Integer keyPosition = null;

  public BaseAttributeDTO attributeKey(String attributeKey) {
    this.attributeKey = attributeKey;
    return this;
  }

  /**
   * The attribute's key (unique inside the scope of the entity)
   * @return attributeKey
  **/
  @ApiModelProperty(required = true, value = "The attribute's key (unique inside the scope of the entity)")
  @NotNull


  public String getAttributeKey() {
    return attributeKey;
  }

  public void setAttributeKey(String attributeKey) {
    this.attributeKey = attributeKey;
  }

  public BaseAttributeDTO attributeName(String attributeName) {
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

  public BaseAttributeDTO datatype(String datatype) {
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

  public BaseAttributeDTO keyPosition(Integer keyPosition) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseAttributeDTO baseAttribute = (BaseAttributeDTO) o;
    return Objects.equals(this.attributeKey, baseAttribute.attributeKey) &&
        Objects.equals(this.attributeName, baseAttribute.attributeName) &&
        Objects.equals(this.datatype, baseAttribute.datatype) &&
        Objects.equals(this.keyPosition, baseAttribute.keyPosition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributeKey, attributeName, datatype, keyPosition);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseAttributeDTO {\n");
    
    sb.append("    attributeKey: ").append(toIndentedString(attributeKey)).append("\n");
    sb.append("    attributeName: ").append(toIndentedString(attributeName)).append("\n");
    sb.append("    datatype: ").append(toIndentedString(datatype)).append("\n");
    sb.append("    keyPosition: ").append(toIndentedString(keyPosition)).append("\n");
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

