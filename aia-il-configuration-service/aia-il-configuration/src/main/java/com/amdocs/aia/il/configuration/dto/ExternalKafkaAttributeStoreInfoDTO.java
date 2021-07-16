package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ExternalAttributeStoreInfoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ExternalKafkaAttributeStoreInfoDTO
 */
@Validated


public class ExternalKafkaAttributeStoreInfoDTO extends ExternalAttributeStoreInfoDTO  {
  @JsonProperty("jsonPath")
  private String jsonPath = null;

  @JsonProperty("dateFormat")
  private String dateFormat = null;

  public ExternalKafkaAttributeStoreInfoDTO jsonPath(String jsonPath) {
    this.jsonPath = jsonPath;
    return this;
  }

  /**
   * The attribute's JSON path
   * @return jsonPath
  **/
  @ApiModelProperty(value = "The attribute's JSON path")


  public String getJsonPath() {
    return jsonPath;
  }

  public void setJsonPath(String jsonPath) {
    this.jsonPath = jsonPath;
  }

  public ExternalKafkaAttributeStoreInfoDTO dateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
    return this;
  }

  /**
   * The attribute's date format
   * @return dateFormat
  **/
  @ApiModelProperty(value = "The attribute's date format")


  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalKafkaAttributeStoreInfoDTO externalKafkaAttributeStoreInfo = (ExternalKafkaAttributeStoreInfoDTO) o;
    return Objects.equals(this.jsonPath, externalKafkaAttributeStoreInfo.jsonPath) &&
        Objects.equals(this.dateFormat, externalKafkaAttributeStoreInfo.dateFormat) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jsonPath, dateFormat, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalKafkaAttributeStoreInfoDTO {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    jsonPath: ").append(toIndentedString(jsonPath)).append("\n");
    sb.append("    dateFormat: ").append(toIndentedString(dateFormat)).append("\n");
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

