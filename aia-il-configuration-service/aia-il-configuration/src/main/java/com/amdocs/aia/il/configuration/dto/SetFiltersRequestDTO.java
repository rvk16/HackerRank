package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.BulkGroupDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityDTO;
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
 * SetFiltersRequestDTO
 */
@Validated


public class SetFiltersRequestDTO   {
  @JsonProperty("entities")
  @Valid
  private List<ExternalEntityDTO> entities = null;

  @JsonProperty("bulkGroup")
  private BulkGroupDTO bulkGroup = null;

  public SetFiltersRequestDTO entities(List<ExternalEntityDTO> entities) {
    this.entities = entities;
    return this;
  }

  public SetFiltersRequestDTO addEntitiesItem(ExternalEntityDTO entitiesItem) {
    if (this.entities == null) {
      this.entities = new ArrayList<>();
    }
    this.entities.add(entitiesItem);
    return this;
  }

  /**
   * A list of external entities
   * @return entities
  **/
  @ApiModelProperty(value = "A list of external entities")

  @Valid

  public List<ExternalEntityDTO> getEntities() {
    return entities;
  }

  public void setEntities(List<ExternalEntityDTO> entities) {
    this.entities = entities;
  }

  public SetFiltersRequestDTO bulkGroup(BulkGroupDTO bulkGroup) {
    this.bulkGroup = bulkGroup;
    return this;
  }

  /**
   * Get bulkGroup
   * @return bulkGroup
  **/
  @ApiModelProperty(value = "")

  @Valid

  public BulkGroupDTO getBulkGroup() {
    return bulkGroup;
  }

  public void setBulkGroup(BulkGroupDTO bulkGroup) {
    this.bulkGroup = bulkGroup;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SetFiltersRequestDTO setFiltersRequest = (SetFiltersRequestDTO) o;
    return Objects.equals(this.entities, setFiltersRequest.entities) &&
        Objects.equals(this.bulkGroup, setFiltersRequest.bulkGroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entities, bulkGroup);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SetFiltersRequestDTO {\n");
    
    sb.append("    entities: ").append(toIndentedString(entities)).append("\n");
    sb.append("    bulkGroup: ").append(toIndentedString(bulkGroup)).append("\n");
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

