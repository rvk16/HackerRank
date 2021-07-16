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
 * SaveElementsResponseDTO
 */
@Validated


public class SaveElementsResponseDTO   {
  @JsonProperty("savedElementsCount")
  private Long savedElementsCount = null;

  public SaveElementsResponseDTO savedElementsCount(Long savedElementsCount) {
    this.savedElementsCount = savedElementsCount;
    return this;
  }

  /**
   * Get savedElementsCount
   * @return savedElementsCount
  **/
  @ApiModelProperty(value = "")


  public Long getSavedElementsCount() {
    return savedElementsCount;
  }

  public void setSavedElementsCount(Long savedElementsCount) {
    this.savedElementsCount = savedElementsCount;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SaveElementsResponseDTO saveElementsResponse = (SaveElementsResponseDTO) o;
    return Objects.equals(this.savedElementsCount, saveElementsResponse.savedElementsCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(savedElementsCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SaveElementsResponseDTO {\n");
    
    sb.append("    savedElementsCount: ").append(toIndentedString(savedElementsCount)).append("\n");
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

