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
 * Attribute store property
 */
@ApiModel(description = "Attribute store property")
@Validated


public class AttributeStorePropertyDTO   {
  @JsonProperty("propertyKey")
  private String propertyKey = null;

  @JsonProperty("propertyValue")
  private Object propertyValue = null;

  public AttributeStorePropertyDTO propertyKey(String propertyKey) {
    this.propertyKey = propertyKey;
    return this;
  }

  /**
   * The property key
   * @return propertyKey
  **/
  @ApiModelProperty(value = "The property key")


  public String getPropertyKey() {
    return propertyKey;
  }

  public void setPropertyKey(String propertyKey) {
    this.propertyKey = propertyKey;
  }

  public AttributeStorePropertyDTO propertyValue(Object propertyValue) {
    this.propertyValue = propertyValue;
    return this;
  }

  /**
   * The property value
   * @return propertyValue
  **/
  @ApiModelProperty(value = "The property value")


  public Object getPropertyValue() {
    return propertyValue;
  }

  public void setPropertyValue(Object propertyValue) {
    this.propertyValue = propertyValue;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttributeStorePropertyDTO attributeStoreProperty = (AttributeStorePropertyDTO) o;
    return Objects.equals(this.propertyKey, attributeStoreProperty.propertyKey) &&
        Objects.equals(this.propertyValue, attributeStoreProperty.propertyValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyKey, propertyValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttributeStorePropertyDTO {\n");
    
    sb.append("    propertyKey: ").append(toIndentedString(propertyKey)).append("\n");
    sb.append("    propertyValue: ").append(toIndentedString(propertyValue)).append("\n");
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

