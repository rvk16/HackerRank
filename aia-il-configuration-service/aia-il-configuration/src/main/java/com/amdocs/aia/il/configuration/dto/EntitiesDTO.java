package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ContextEntityRefDTO;
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
 * EntitiesDTO
 */
@Validated


public class EntitiesDTO   {
  @JsonProperty("contextEntities")
  @Valid
  private List<ContextEntityRefDTO> contextEntities = new ArrayList<>();

  public EntitiesDTO contextEntities(List<ContextEntityRefDTO> contextEntities) {
    this.contextEntities = contextEntities;
    return this;
  }

  public EntitiesDTO addContextEntitiesItem(ContextEntityRefDTO contextEntitiesItem) {
    this.contextEntities.add(contextEntitiesItem);
    return this;
  }

  /**
   * List of Context Entity Ref
   * @return contextEntities
  **/
  @ApiModelProperty(required = true, value = "List of Context Entity Ref")
  @NotNull

  @Valid

  public List<ContextEntityRefDTO> getContextEntities() {
    return contextEntities;
  }

  public void setContextEntities(List<ContextEntityRefDTO> contextEntities) {
    this.contextEntities = contextEntities;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntitiesDTO entities = (EntitiesDTO) o;
    return Objects.equals(this.contextEntities, entities.contextEntities);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contextEntities);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntitiesDTO {\n");
    
    sb.append("    contextEntities: ").append(toIndentedString(contextEntities)).append("\n");
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

