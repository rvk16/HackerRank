package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ColumnDatatypeDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Replica Store column
 */
@ApiModel(description = "Replica Store column")
@Validated


public class ColumnDTO   {
  @JsonProperty("columnName")
  private String columnName = null;

  @JsonProperty("datatype")
  private ColumnDatatypeDTO datatype = null;

  @JsonProperty("isLogicalTime")
  private Boolean isLogicalTime = null;

  public ColumnDTO columnName(String columnName) {
    this.columnName = columnName;
    return this;
  }

  /**
   * the name of the Replica Store column
   * @return columnName
  **/
  @ApiModelProperty(value = "the name of the Replica Store column")


  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public ColumnDTO datatype(ColumnDatatypeDTO datatype) {
    this.datatype = datatype;
    return this;
  }

  /**
   * Get datatype
   * @return datatype
  **/
  @ApiModelProperty(value = "")

  @Valid

  public ColumnDatatypeDTO getDatatype() {
    return datatype;
  }

  public void setDatatype(ColumnDatatypeDTO datatype) {
    this.datatype = datatype;
  }

  public ColumnDTO isLogicalTime(Boolean isLogicalTime) {
    this.isLogicalTime = isLogicalTime;
    return this;
  }

  /**
   * Get isLogicalTime
   * @return isLogicalTime
  **/
  @ApiModelProperty(value = "")


  public Boolean isIsLogicalTime() {
    return isLogicalTime;
  }

  public void setIsLogicalTime(Boolean isLogicalTime) {
    this.isLogicalTime = isLogicalTime;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ColumnDTO column = (ColumnDTO) o;
    return Objects.equals(this.columnName, column.columnName) &&
        Objects.equals(this.datatype, column.datatype) &&
        Objects.equals(this.isLogicalTime, column.isLogicalTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(columnName, datatype, isLogicalTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ColumnDTO {\n");
    
    sb.append("    columnName: ").append(toIndentedString(columnName)).append("\n");
    sb.append("    datatype: ").append(toIndentedString(datatype)).append("\n");
    sb.append("    isLogicalTime: ").append(toIndentedString(isLogicalTime)).append("\n");
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

