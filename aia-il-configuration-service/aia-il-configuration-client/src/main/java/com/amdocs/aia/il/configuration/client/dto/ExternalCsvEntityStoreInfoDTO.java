/*
 * Integration Layer Configuration API
 * This is a REST API specification for Integration Layer Configuration application.
 *
 * OpenAPI spec version: TRUNK-SNAPSHOT
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.amdocs.aia.il.configuration.client.dto;

import java.util.Objects;
import java.util.Arrays;
import com.amdocs.aia.il.configuration.client.dto.ExternalEntityStoreInfoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ExternalCsvEntityStoreInfoDTO
 */

public class ExternalCsvEntityStoreInfoDTO extends ExternalEntityStoreInfoDTO {
  @JsonProperty("fileHeader")
  private Boolean fileHeader = null;

  @JsonProperty("fileNameFormat")
  private String fileNameFormat = null;

  @JsonProperty("dateFormat")
  private String dateFormat = null;

  @JsonProperty("columnDelimiter")
  private String columnDelimiter = null;

  public ExternalCsvEntityStoreInfoDTO fileHeader(Boolean fileHeader) {
    this.fileHeader = fileHeader;
    return this;
  }

   /**
   * CSV file header
   * @return fileHeader
  **/
  @ApiModelProperty(value = "CSV file header")
  public Boolean isFileHeader() {
    return fileHeader;
  }

  public void setFileHeader(Boolean fileHeader) {
    this.fileHeader = fileHeader;
  }

  public ExternalCsvEntityStoreInfoDTO fileNameFormat(String fileNameFormat) {
    this.fileNameFormat = fileNameFormat;
    return this;
  }

   /**
   * Input file name format
   * @return fileNameFormat
  **/
  @ApiModelProperty(value = "Input file name format")
  public String getFileNameFormat() {
    return fileNameFormat;
  }

  public void setFileNameFormat(String fileNameFormat) {
    this.fileNameFormat = fileNameFormat;
  }

  public ExternalCsvEntityStoreInfoDTO dateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
    return this;
  }

   /**
   * Date format for attributes of type Date/DateTime
   * @return dateFormat
  **/
  @ApiModelProperty(value = "Date format for attributes of type Date/DateTime")
  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public ExternalCsvEntityStoreInfoDTO columnDelimiter(String columnDelimiter) {
    this.columnDelimiter = columnDelimiter;
    return this;
  }

   /**
   * CSV column delimiter
   * @return columnDelimiter
  **/
  @ApiModelProperty(value = "CSV column delimiter")
  public String getColumnDelimiter() {
    return columnDelimiter;
  }

  public void setColumnDelimiter(String columnDelimiter) {
    this.columnDelimiter = columnDelimiter;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalCsvEntityStoreInfoDTO externalCsvEntityStoreInfo = (ExternalCsvEntityStoreInfoDTO) o;
    return Objects.equals(this.fileHeader, externalCsvEntityStoreInfo.fileHeader) &&
        Objects.equals(this.fileNameFormat, externalCsvEntityStoreInfo.fileNameFormat) &&
        Objects.equals(this.dateFormat, externalCsvEntityStoreInfo.dateFormat) &&
        Objects.equals(this.columnDelimiter, externalCsvEntityStoreInfo.columnDelimiter) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileHeader, fileNameFormat, dateFormat, columnDelimiter, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalCsvEntityStoreInfoDTO {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    fileHeader: ").append(toIndentedString(fileHeader)).append("\n");
    sb.append("    fileNameFormat: ").append(toIndentedString(fileNameFormat)).append("\n");
    sb.append("    dateFormat: ").append(toIndentedString(dateFormat)).append("\n");
    sb.append("    columnDelimiter: ").append(toIndentedString(columnDelimiter)).append("\n");
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

