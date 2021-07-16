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
 * Async Process Response
 */
@ApiModel(description = "Async Process Response")
@Validated


public class AsyncResponseDTO   {
  @JsonProperty("processId")
  private Integer processId = null;

  public AsyncResponseDTO processId(Integer processId) {
    this.processId = processId;
    return this;
  }

  /**
   * Process Id
   * @return processId
  **/
  @ApiModelProperty(value = "Process Id")


  public Integer getProcessId() {
    return processId;
  }

  public void setProcessId(Integer processId) {
    this.processId = processId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AsyncResponseDTO asyncResponse = (AsyncResponseDTO) o;
    return Objects.equals(this.processId, asyncResponse.processId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(processId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AsyncResponseDTO {\n");
    
    sb.append("    processId: ").append(toIndentedString(processId)).append("\n");
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

