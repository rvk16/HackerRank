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
 * Information about a type-system which defines representation for attribute datatypes
 */
@ApiModel(description = "Information about a type-system which defines representation for attribute datatypes")
@Validated


public class TypeSystemInfoDTO   {
  @JsonProperty("typeSystem")
  private String typeSystem = null;

  @JsonProperty("displayName")
  private String displayName = null;

  public TypeSystemInfoDTO typeSystem(String typeSystem) {
    this.typeSystem = typeSystem;
    return this;
  }

  /**
   * Get typeSystem
   * @return typeSystem
  **/
  @ApiModelProperty(value = "")


  public String getTypeSystem() {
    return typeSystem;
  }

  public void setTypeSystem(String typeSystem) {
    this.typeSystem = typeSystem;
  }

  public TypeSystemInfoDTO displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get displayName
   * @return displayName
  **/
  @ApiModelProperty(value = "")


  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TypeSystemInfoDTO typeSystemInfo = (TypeSystemInfoDTO) o;
    return Objects.equals(this.typeSystem, typeSystemInfo.typeSystem) &&
        Objects.equals(this.displayName, typeSystemInfo.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeSystem, displayName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TypeSystemInfoDTO {\n");
    
    sb.append("    typeSystem: ").append(toIndentedString(typeSystem)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
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

