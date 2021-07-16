package com.amdocs.aia.il.sqlite.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ResultSetDTO
 */
@Validated


public class ResultSetDTO   {
  @JsonProperty("resultSet")
  private Object resultSet = null;

  public ResultSetDTO resultSet(Object resultSet) {
    this.resultSet = resultSet;
    return this;
  }

  /**
   * Get resultSet
   * @return resultSet
  **/
  @ApiModelProperty(value = "")


  public Object getResultSet() {
    return resultSet;
  }

  public void setResultSet(Object resultSet) {
    this.resultSet = resultSet;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResultSetDTO resultSet = (ResultSetDTO) o;
    return Objects.equals(this.resultSet, resultSet.resultSet);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultSet);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResultSetDTO {\n");
    
    sb.append("    resultSet: ").append(toIndentedString(resultSet)).append("\n");
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

