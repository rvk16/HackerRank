package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
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
 * Primary Key
 */
@ApiModel(description = "Primary Key")
@Validated


public class PrimaryKeyDTO   {
  @JsonProperty("columnNames")
  @Valid
  private List<String> columnNames = null;

  public PrimaryKeyDTO columnNames(List<String> columnNames) {
    this.columnNames = columnNames;
    return this;
  }

  public PrimaryKeyDTO addColumnNamesItem(String columnNamesItem) {
    if (this.columnNames == null) {
      this.columnNames = new ArrayList<>();
    }
    this.columnNames.add(columnNamesItem);
    return this;
  }

  /**
   * Get columnNames
   * @return columnNames
  **/
  @ApiModelProperty(value = "")


  public List<String> getColumnNames() {
    return columnNames;
  }

  public void setColumnNames(List<String> columnNames) {
    this.columnNames = columnNames;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PrimaryKeyDTO primaryKey = (PrimaryKeyDTO) o;
    return Objects.equals(this.columnNames, primaryKey.columnNames);
  }

  @Override
  public int hashCode() {
    return Objects.hash(columnNames);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PrimaryKeyDTO {\n");
    
    sb.append("    columnNames: ").append(toIndentedString(columnNames)).append("\n");
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

