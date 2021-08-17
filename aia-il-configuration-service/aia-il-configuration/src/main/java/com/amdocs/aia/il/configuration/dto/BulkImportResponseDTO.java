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
 * Bulk Import Response
 */
@ApiModel(description = "Bulk Import Response")
@Validated


public class BulkImportResponseDTO   {
  @JsonProperty("newSchemasCount")
  private Integer newSchemasCount = null;

  @JsonProperty("newEntitiesCount")
  private Integer newEntitiesCount = null;

  @JsonProperty("modifiedSchemasCount")
  private Integer modifiedSchemasCount = null;

  @JsonProperty("modifiedEntitiesCount")
  private Integer modifiedEntitiesCount = null;

  @JsonProperty("deletedSchemasCount")
  private Integer deletedSchemasCount = null;

  @JsonProperty("deletedEntitiesCount")
  private Integer deletedEntitiesCount = null;

  public BulkImportResponseDTO newSchemasCount(Integer newSchemasCount) {
    this.newSchemasCount = newSchemasCount;
    return this;
  }

  /**
   * Number of new Schemas in file
   * @return newSchemasCount
  **/
  @ApiModelProperty(value = "Number of new Schemas in file")


  public Integer getNewSchemasCount() {
    return newSchemasCount;
  }

  public void setNewSchemasCount(Integer newSchemasCount) {
    this.newSchemasCount = newSchemasCount;
  }

  public BulkImportResponseDTO newEntitiesCount(Integer newEntitiesCount) {
    this.newEntitiesCount = newEntitiesCount;
    return this;
  }

  /**
   * Number of new Entities in file
   * @return newEntitiesCount
  **/
  @ApiModelProperty(value = "Number of new Entities in file")


  public Integer getNewEntitiesCount() {
    return newEntitiesCount;
  }

  public void setNewEntitiesCount(Integer newEntitiesCount) {
    this.newEntitiesCount = newEntitiesCount;
  }

  public BulkImportResponseDTO modifiedSchemasCount(Integer modifiedSchemasCount) {
    this.modifiedSchemasCount = modifiedSchemasCount;
    return this;
  }

  /**
   * Number of modified schemas
   * @return modifiedSchemasCount
  **/
  @ApiModelProperty(value = "Number of modified schemas")


  public Integer getModifiedSchemasCount() {
    return modifiedSchemasCount;
  }

  public void setModifiedSchemasCount(Integer modifiedSchemasCount) {
    this.modifiedSchemasCount = modifiedSchemasCount;
  }

  public BulkImportResponseDTO modifiedEntitiesCount(Integer modifiedEntitiesCount) {
    this.modifiedEntitiesCount = modifiedEntitiesCount;
    return this;
  }

  /**
   * Number of modified entities
   * @return modifiedEntitiesCount
  **/
  @ApiModelProperty(value = "Number of modified entities")


  public Integer getModifiedEntitiesCount() {
    return modifiedEntitiesCount;
  }

  public void setModifiedEntitiesCount(Integer modifiedEntitiesCount) {
    this.modifiedEntitiesCount = modifiedEntitiesCount;
  }

  public BulkImportResponseDTO deletedSchemasCount(Integer deletedSchemasCount) {
    this.deletedSchemasCount = deletedSchemasCount;
    return this;
  }

  /**
   * Number of deleted schemas
   * @return deletedSchemasCount
  **/
  @ApiModelProperty(value = "Number of deleted schemas")


  public Integer getDeletedSchemasCount() {
    return deletedSchemasCount;
  }

  public void setDeletedSchemasCount(Integer deletedSchemasCount) {
    this.deletedSchemasCount = deletedSchemasCount;
  }

  public BulkImportResponseDTO deletedEntitiesCount(Integer deletedEntitiesCount) {
    this.deletedEntitiesCount = deletedEntitiesCount;
    return this;
  }

  /**
   * Number of deleted entities
   * @return deletedEntitiesCount
  **/
  @ApiModelProperty(value = "Number of deleted entities")


  public Integer getDeletedEntitiesCount() {
    return deletedEntitiesCount;
  }

  public void setDeletedEntitiesCount(Integer deletedEntitiesCount) {
    this.deletedEntitiesCount = deletedEntitiesCount;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BulkImportResponseDTO bulkImportResponse = (BulkImportResponseDTO) o;
    return Objects.equals(this.newSchemasCount, bulkImportResponse.newSchemasCount) &&
        Objects.equals(this.newEntitiesCount, bulkImportResponse.newEntitiesCount) &&
        Objects.equals(this.modifiedSchemasCount, bulkImportResponse.modifiedSchemasCount) &&
        Objects.equals(this.modifiedEntitiesCount, bulkImportResponse.modifiedEntitiesCount) &&
        Objects.equals(this.deletedSchemasCount, bulkImportResponse.deletedSchemasCount) &&
        Objects.equals(this.deletedEntitiesCount, bulkImportResponse.deletedEntitiesCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(newSchemasCount, newEntitiesCount, modifiedSchemasCount, modifiedEntitiesCount, deletedSchemasCount, deletedEntitiesCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BulkImportResponseDTO {\n");
    
    sb.append("    newSchemasCount: ").append(toIndentedString(newSchemasCount)).append("\n");
    sb.append("    newEntitiesCount: ").append(toIndentedString(newEntitiesCount)).append("\n");
    sb.append("    modifiedSchemasCount: ").append(toIndentedString(modifiedSchemasCount)).append("\n");
    sb.append("    modifiedEntitiesCount: ").append(toIndentedString(modifiedEntitiesCount)).append("\n");
    sb.append("    deletedSchemasCount: ").append(toIndentedString(deletedSchemasCount)).append("\n");
    sb.append("    deletedEntitiesCount: ").append(toIndentedString(deletedEntitiesCount)).append("\n");
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

