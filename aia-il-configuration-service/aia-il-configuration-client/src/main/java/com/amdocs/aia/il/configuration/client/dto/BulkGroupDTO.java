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
import com.amdocs.aia.il.configuration.client.dto.EntityFilterRefDTO;
import com.amdocs.aia.il.configuration.client.dto.GroupFilterDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Bulk Group representation
 */
@ApiModel(description = "Bulk Group representation")

public class BulkGroupDTO {
  @JsonProperty("schemaKey")
  private String schemaKey = null;

  @JsonProperty("bulkGroupName")
  private String bulkGroupName = null;

  @JsonProperty("bulkGroupKey")
  private String bulkGroupKey = null;

  @JsonProperty("originProcess")
  private String originProcess = null;

  @JsonProperty("groupFilter")
  private GroupFilterDTO groupFilter = null;

  @JsonProperty("entityFilters")
  private List<EntityFilterRefDTO> entityFilters = null;

  public BulkGroupDTO schemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
    return this;
  }

   /**
   * The Schema Key
   * @return schemaKey
  **/
  @ApiModelProperty(value = "The Schema Key")
  public String getSchemaKey() {
    return schemaKey;
  }

  public void setSchemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
  }

  public BulkGroupDTO bulkGroupName(String bulkGroupName) {
    this.bulkGroupName = bulkGroupName;
    return this;
  }

   /**
   * The  Bulk Group Name
   * @return bulkGroupName
  **/
  @ApiModelProperty(value = "The  Bulk Group Name")
  public String getBulkGroupName() {
    return bulkGroupName;
  }

  public void setBulkGroupName(String bulkGroupName) {
    this.bulkGroupName = bulkGroupName;
  }

  public BulkGroupDTO bulkGroupKey(String bulkGroupKey) {
    this.bulkGroupKey = bulkGroupKey;
    return this;
  }

   /**
   * The Bulk Group Key
   * @return bulkGroupKey
  **/
  @ApiModelProperty(value = "The Bulk Group Key")
  public String getBulkGroupKey() {
    return bulkGroupKey;
  }

  public void setBulkGroupKey(String bulkGroupKey) {
    this.bulkGroupKey = bulkGroupKey;
  }

  public BulkGroupDTO originProcess(String originProcess) {
    this.originProcess = originProcess;
    return this;
  }

   /**
   * The Origin Process
   * @return originProcess
  **/
  @ApiModelProperty(value = "The Origin Process")
  public String getOriginProcess() {
    return originProcess;
  }

  public void setOriginProcess(String originProcess) {
    this.originProcess = originProcess;
  }

  public BulkGroupDTO groupFilter(GroupFilterDTO groupFilter) {
    this.groupFilter = groupFilter;
    return this;
  }

   /**
   * Get groupFilter
   * @return groupFilter
  **/
  @ApiModelProperty(value = "")
  public GroupFilterDTO getGroupFilter() {
    return groupFilter;
  }

  public void setGroupFilter(GroupFilterDTO groupFilter) {
    this.groupFilter = groupFilter;
  }

  public BulkGroupDTO entityFilters(List<EntityFilterRefDTO> entityFilters) {
    this.entityFilters = entityFilters;
    return this;
  }

  public BulkGroupDTO addEntityFiltersItem(EntityFilterRefDTO entityFiltersItem) {
    if (this.entityFilters == null) {
      this.entityFilters = new ArrayList<>();
    }
    this.entityFilters.add(entityFiltersItem);
    return this;
  }

   /**
   * Entity filters for bulk group
   * @return entityFilters
  **/
  @ApiModelProperty(value = "Entity filters for bulk group")
  public List<EntityFilterRefDTO> getEntityFilters() {
    return entityFilters;
  }

  public void setEntityFilters(List<EntityFilterRefDTO> entityFilters) {
    this.entityFilters = entityFilters;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BulkGroupDTO bulkGroup = (BulkGroupDTO) o;
    return Objects.equals(this.schemaKey, bulkGroup.schemaKey) &&
        Objects.equals(this.bulkGroupName, bulkGroup.bulkGroupName) &&
        Objects.equals(this.bulkGroupKey, bulkGroup.bulkGroupKey) &&
        Objects.equals(this.originProcess, bulkGroup.originProcess) &&
        Objects.equals(this.groupFilter, bulkGroup.groupFilter) &&
        Objects.equals(this.entityFilters, bulkGroup.entityFilters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemaKey, bulkGroupName, bulkGroupKey, originProcess, groupFilter, entityFilters);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BulkGroupDTO {\n");
    
    sb.append("    schemaKey: ").append(toIndentedString(schemaKey)).append("\n");
    sb.append("    bulkGroupName: ").append(toIndentedString(bulkGroupName)).append("\n");
    sb.append("    bulkGroupKey: ").append(toIndentedString(bulkGroupKey)).append("\n");
    sb.append("    originProcess: ").append(toIndentedString(originProcess)).append("\n");
    sb.append("    groupFilter: ").append(toIndentedString(groupFilter)).append("\n");
    sb.append("    entityFilters: ").append(toIndentedString(entityFilters)).append("\n");
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

