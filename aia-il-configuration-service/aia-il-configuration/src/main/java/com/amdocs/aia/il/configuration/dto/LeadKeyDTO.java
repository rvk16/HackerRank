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
 * LeadKeyDTO
 */
@Validated


public class LeadKeyDTO   {
  @JsonProperty("sourceAttribute")
  private String sourceAttribute = null;

  @JsonProperty("targetAttribute")
  private String targetAttribute = null;

  public LeadKeyDTO sourceAttribute(String sourceAttribute) {
    this.sourceAttribute = sourceAttribute;
    return this;
  }

  /**
   * Get sourceAttribute
   * @return sourceAttribute
  **/
  @ApiModelProperty(value = "")


  public String getSourceAttribute() {
    return sourceAttribute;
  }

  public void setSourceAttribute(String sourceAttribute) {
    this.sourceAttribute = sourceAttribute;
  }

  public LeadKeyDTO targetAttribute(String targetAttribute) {
    this.targetAttribute = targetAttribute;
    return this;
  }

  /**
   * Get targetAttribute
   * @return targetAttribute
  **/
  @ApiModelProperty(value = "")


  public String getTargetAttribute() {
    return targetAttribute;
  }

  public void setTargetAttribute(String targetAttribute) {
    this.targetAttribute = targetAttribute;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeadKeyDTO leadKey = (LeadKeyDTO) o;
    return Objects.equals(this.sourceAttribute, leadKey.sourceAttribute) &&
        Objects.equals(this.targetAttribute, leadKey.targetAttribute);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceAttribute, targetAttribute);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeadKeyDTO {\n");
    
    sb.append("    sourceAttribute: ").append(toIndentedString(sourceAttribute)).append("\n");
    sb.append("    targetAttribute: ").append(toIndentedString(targetAttribute)).append("\n");
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

