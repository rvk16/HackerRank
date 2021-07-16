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
 * column data type
 */
@ApiModel(description = "column data type")
@Validated


public class ColumnDatatypeDTO   {
  @JsonProperty("sqlType")
  private String sqlType = null;

  public ColumnDatatypeDTO sqlType(String sqlType) {
    this.sqlType = sqlType;
    return this;
  }

  /**
   * Sql type
   * @return sqlType
  **/
  @ApiModelProperty(value = "Sql type")


  public String getSqlType() {
    return sqlType;
  }

  public void setSqlType(String sqlType) {
    this.sqlType = sqlType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ColumnDatatypeDTO columnDatatype = (ColumnDatatypeDTO) o;
    return Objects.equals(this.sqlType, columnDatatype.sqlType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sqlType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ColumnDatatypeDTO {\n");
    
    sb.append("    sqlType: ").append(toIndentedString(sqlType)).append("\n");
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

