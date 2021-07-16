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
 * Filter for external entity instances (used, for example, for bulk loading)
 */
@ApiModel(description = "Filter for external entity instances (used, for example, for bulk loading)")
@Validated


public class ExternalEntityFilterDTO   {
  @JsonProperty("filterKey")
  private String filterKey = null;

  @JsonProperty("filterLogic")
  private String filterLogic = null;

  public ExternalEntityFilterDTO filterKey(String filterKey) {
    this.filterKey = filterKey;
    return this;
  }

  /**
   * entity filter key
   * @return filterKey
  **/
  @ApiModelProperty(value = "entity filter key")


  public String getFilterKey() {
    return filterKey;
  }

  public void setFilterKey(String filterKey) {
    this.filterKey = filterKey;
  }

  public ExternalEntityFilterDTO filterLogic(String filterLogic) {
    this.filterLogic = filterLogic;
    return this;
  }

  /**
   * A string representation of the logic for filtering entities (for example, for SQL entities, this will be an SQL query)
   * @return filterLogic
  **/
  @ApiModelProperty(value = "A string representation of the logic for filtering entities (for example, for SQL entities, this will be an SQL query)")


  public String getFilterLogic() {
    return filterLogic;
  }

  public void setFilterLogic(String filterLogic) {
    this.filterLogic = filterLogic;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalEntityFilterDTO externalEntityFilter = (ExternalEntityFilterDTO) o;
    return Objects.equals(this.filterKey, externalEntityFilter.filterKey) &&
        Objects.equals(this.filterLogic, externalEntityFilter.filterLogic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filterKey, filterLogic);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalEntityFilterDTO {\n");
    
    sb.append("    filterKey: ").append(toIndentedString(filterKey)).append("\n");
    sb.append("    filterLogic: ").append(toIndentedString(filterLogic)).append("\n");
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

