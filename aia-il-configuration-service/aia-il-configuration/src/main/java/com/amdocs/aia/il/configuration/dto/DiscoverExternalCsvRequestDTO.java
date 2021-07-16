package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.AvailabilityDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DiscoverExternalCsvRequestDTO
 */
@Validated


public class DiscoverExternalCsvRequestDTO   {
  @JsonProperty("schemaName")
  private String schemaName = null;

  @JsonProperty("columnDelimiter")
  private String columnDelimiter = null;

  @JsonProperty("filenames")
  @Valid
  private List<String> filenames = null;

  @JsonProperty("availability")
  private AvailabilityDTO availability = null;

  @JsonProperty("subjectAreaName")
  private String subjectAreaName = null;

  @JsonProperty("subjectAreaKey")
  private String subjectAreaKey = null;

  public DiscoverExternalCsvRequestDTO schemaName(String schemaName) {
    this.schemaName = schemaName;
    return this;
  }

  /**
   * The schemaName of the discover External Csv Schema
   * @return schemaName
  **/
  @ApiModelProperty(value = "The schemaName of the discover External Csv Schema")


  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public DiscoverExternalCsvRequestDTO columnDelimiter(String columnDelimiter) {
    this.columnDelimiter = columnDelimiter;
    return this;
  }

  /**
   * The column Delimiter of the Csv
   * @return columnDelimiter
  **/
  @ApiModelProperty(value = "The column Delimiter of the Csv")

@Size(min=1,max=1) 
  public String getColumnDelimiter() {
    return columnDelimiter;
  }

  public void setColumnDelimiter(String columnDelimiter) {
    this.columnDelimiter = columnDelimiter;
  }

  public DiscoverExternalCsvRequestDTO filenames(List<String> filenames) {
    this.filenames = filenames;
    return this;
  }

  public DiscoverExternalCsvRequestDTO addFilenamesItem(String filenamesItem) {
    if (this.filenames == null) {
      this.filenames = new ArrayList<>();
    }
    this.filenames.add(filenamesItem);
    return this;
  }

  /**
   * A list of the file names
   * @return filenames
  **/
  @ApiModelProperty(value = "A list of the file names")


  public List<String> getFilenames() {
    return filenames;
  }

  public void setFilenames(List<String> filenames) {
    this.filenames = filenames;
  }

  public DiscoverExternalCsvRequestDTO availability(AvailabilityDTO availability) {
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

  public DiscoverExternalCsvRequestDTO subjectAreaName(String subjectAreaName) {
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

  public DiscoverExternalCsvRequestDTO subjectAreaKey(String subjectAreaKey) {
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
    DiscoverExternalCsvRequestDTO discoverExternalCsvRequest = (DiscoverExternalCsvRequestDTO) o;
    return Objects.equals(this.schemaName, discoverExternalCsvRequest.schemaName) &&
        Objects.equals(this.columnDelimiter, discoverExternalCsvRequest.columnDelimiter) &&
        Objects.equals(this.filenames, discoverExternalCsvRequest.filenames) &&
        Objects.equals(this.availability, discoverExternalCsvRequest.availability) &&
        Objects.equals(this.subjectAreaName, discoverExternalCsvRequest.subjectAreaName) &&
        Objects.equals(this.subjectAreaKey, discoverExternalCsvRequest.subjectAreaKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemaName, columnDelimiter, filenames, availability, subjectAreaName, subjectAreaKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DiscoverExternalCsvRequestDTO {\n");
    
    sb.append("    schemaName: ").append(toIndentedString(schemaName)).append("\n");
    sb.append("    columnDelimiter: ").append(toIndentedString(columnDelimiter)).append("\n");
    sb.append("    filenames: ").append(toIndentedString(filenames)).append("\n");
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

