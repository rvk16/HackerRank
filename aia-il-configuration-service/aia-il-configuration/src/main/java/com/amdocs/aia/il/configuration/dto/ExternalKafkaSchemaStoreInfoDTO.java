package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaStoreInfoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ExternalKafkaSchemaStoreInfoDTO
 */
@Validated


public class ExternalKafkaSchemaStoreInfoDTO extends ExternalSchemaStoreInfoDTO  {
  @JsonProperty("defaultDateFormat")
  private String defaultDateFormat = null;

  public ExternalKafkaSchemaStoreInfoDTO defaultDateFormat(String defaultDateFormat) {
    this.defaultDateFormat = defaultDateFormat;
    return this;
  }

  /**
   * Default column date format of the schema store info
   * @return defaultDateFormat
  **/
  @ApiModelProperty(value = "Default column date format of the schema store info")


  public String getDefaultDateFormat() {
    return defaultDateFormat;
  }

  public void setDefaultDateFormat(String defaultDateFormat) {
    this.defaultDateFormat = defaultDateFormat;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalKafkaSchemaStoreInfoDTO externalKafkaSchemaStoreInfo = (ExternalKafkaSchemaStoreInfoDTO) o;
    return Objects.equals(this.defaultDateFormat, externalKafkaSchemaStoreInfo.defaultDateFormat) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(defaultDateFormat, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalKafkaSchemaStoreInfoDTO {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    defaultDateFormat: ").append(toIndentedString(defaultDateFormat)).append("\n");
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

