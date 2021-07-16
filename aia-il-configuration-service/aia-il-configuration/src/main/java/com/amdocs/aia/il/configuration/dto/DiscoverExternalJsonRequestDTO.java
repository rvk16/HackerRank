package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.AvailabilityDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DiscoverExternalJsonRequestDTO
 */
@Validated


public class DiscoverExternalJsonRequestDTO   {
  @JsonProperty("schemaName")
  private String schemaName = null;

  @JsonProperty("filename")
  private String filename = null;

  @JsonProperty("externalSchemaType")
  private String externalSchemaType = null;

  @JsonProperty("availability")
  private AvailabilityDTO availability = null;

  @JsonProperty("subjectAreaName")
  private String subjectAreaName = null;

  @JsonProperty("subjectAreaKey")
  private String subjectAreaKey = null;

  public DiscoverExternalJsonRequestDTO schemaName(String schemaName) {
    this.schemaName = schemaName;
    return this;
  }

  /**
   * The schemaName of the discover External Json Schema
   * @return schemaName
  **/
  @ApiModelProperty(value = "The schemaName of the discover External Json Schema")


  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public DiscoverExternalJsonRequestDTO filename(String filename) {
    this.filename = filename;
    return this;
  }

  /**
   * The the file names
   * @return filename
  **/
  @ApiModelProperty(value = "The the file names")


  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public DiscoverExternalJsonRequestDTO externalSchemaType(String externalSchemaType) {
    this.externalSchemaType = externalSchemaType;
    return this;
  }

  /**
   * A schema types,valid types are CSV_OVER_JSON, DIGITAL1, CATALOG1
   * @return externalSchemaType
  **/
  @ApiModelProperty(value = "A schema types,valid types are CSV_OVER_JSON, DIGITAL1, CATALOG1")


  public String getExternalSchemaType() {
    return externalSchemaType;
  }

  public void setExternalSchemaType(String externalSchemaType) {
    this.externalSchemaType = externalSchemaType;
  }

  public DiscoverExternalJsonRequestDTO availability(AvailabilityDTO availability) {
    this.availability = availability;
    return this;
  }

  /**
   * Get availability
   * @return availability
  **/
  @ApiModelProperty(value = "")

  @Valid

  public AvailabilityDTO getAvailability() {
    return availability;
  }

  public void setAvailability(AvailabilityDTO availability) {
    this.availability = availability;
  }

  public DiscoverExternalJsonRequestDTO subjectAreaName(String subjectAreaName) {
    this.subjectAreaName = subjectAreaName;
    return this;
  }

  /**
   * The external schema's subjectArea name
   * @return subjectAreaName
  **/
  @ApiModelProperty(value = "The external schema's subjectArea name")


  public String getSubjectAreaName() {
    return subjectAreaName;
  }

  public void setSubjectAreaName(String subjectAreaName) {
    this.subjectAreaName = subjectAreaName;
  }

  public DiscoverExternalJsonRequestDTO subjectAreaKey(String subjectAreaKey) {
    this.subjectAreaKey = subjectAreaKey;
    return this;
  }

  /**
   * The external schema's subjectArea key
   * @return subjectAreaKey
  **/
  @ApiModelProperty(value = "The external schema's subjectArea key")


  public String getSubjectAreaKey() {
    return subjectAreaKey;
  }

  public void setSubjectAreaKey(String subjectAreaKey) {
    this.subjectAreaKey = subjectAreaKey;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DiscoverExternalJsonRequestDTO discoverExternalJsonRequest = (DiscoverExternalJsonRequestDTO) o;
    return Objects.equals(this.schemaName, discoverExternalJsonRequest.schemaName) &&
        Objects.equals(this.filename, discoverExternalJsonRequest.filename) &&
        Objects.equals(this.externalSchemaType, discoverExternalJsonRequest.externalSchemaType) &&
        Objects.equals(this.availability, discoverExternalJsonRequest.availability) &&
        Objects.equals(this.subjectAreaName, discoverExternalJsonRequest.subjectAreaName) &&
        Objects.equals(this.subjectAreaKey, discoverExternalJsonRequest.subjectAreaKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemaName, filename, externalSchemaType, availability, subjectAreaName, subjectAreaKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DiscoverExternalJsonRequestDTO {\n");
    
    sb.append("    schemaName: ").append(toIndentedString(schemaName)).append("\n");
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    externalSchemaType: ").append(toIndentedString(externalSchemaType)).append("\n");
    sb.append("    availability: ").append(toIndentedString(availability)).append("\n");
    sb.append("    subjectAreaName: ").append(toIndentedString(subjectAreaName)).append("\n");
    sb.append("    subjectAreaKey: ").append(toIndentedString(subjectAreaKey)).append("\n");
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

