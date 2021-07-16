package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ExternalEntityStoreInfoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ExternalKafkaEntityStoreInfoDTO
 */
@Validated


public class ExternalKafkaEntityStoreInfoDTO extends ExternalEntityStoreInfoDTO  {
  @JsonProperty("jsonTypeValue")
  private String jsonTypeValue = null;

  @JsonProperty("jsonTypePath")
  private String jsonTypePath = null;

  @JsonProperty("relativePaths")
  private String relativePaths = null;

  @JsonProperty("mergedNodes")
  private String mergedNodes = null;

  public ExternalKafkaEntityStoreInfoDTO jsonTypeValue(String jsonTypeValue) {
    this.jsonTypeValue = jsonTypeValue;
    return this;
  }

  /**
   * json type value
   * @return jsonTypeValue
  **/
  @ApiModelProperty(value = "json type value")


  public String getJsonTypeValue() {
    return jsonTypeValue;
  }

  public void setJsonTypeValue(String jsonTypeValue) {
    this.jsonTypeValue = jsonTypeValue;
  }

  public ExternalKafkaEntityStoreInfoDTO jsonTypePath(String jsonTypePath) {
    this.jsonTypePath = jsonTypePath;
    return this;
  }

  /**
   * json type path
   * @return jsonTypePath
  **/
  @ApiModelProperty(value = "json type path")


  public String getJsonTypePath() {
    return jsonTypePath;
  }

  public void setJsonTypePath(String jsonTypePath) {
    this.jsonTypePath = jsonTypePath;
  }

  public ExternalKafkaEntityStoreInfoDTO relativePaths(String relativePaths) {
    this.relativePaths = relativePaths;
    return this;
  }

  /**
   * relative paths
   * @return relativePaths
  **/
  @ApiModelProperty(value = "relative paths")


  public String getRelativePaths() {
    return relativePaths;
  }

  public void setRelativePaths(String relativePaths) {
    this.relativePaths = relativePaths;
  }

  public ExternalKafkaEntityStoreInfoDTO mergedNodes(String mergedNodes) {
    this.mergedNodes = mergedNodes;
    return this;
  }

  /**
   * merged nodes
   * @return mergedNodes
  **/
  @ApiModelProperty(value = "merged nodes")


  public String getMergedNodes() {
    return mergedNodes;
  }

  public void setMergedNodes(String mergedNodes) {
    this.mergedNodes = mergedNodes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalKafkaEntityStoreInfoDTO externalKafkaEntityStoreInfo = (ExternalKafkaEntityStoreInfoDTO) o;
    return Objects.equals(this.jsonTypeValue, externalKafkaEntityStoreInfo.jsonTypeValue) &&
        Objects.equals(this.jsonTypePath, externalKafkaEntityStoreInfo.jsonTypePath) &&
        Objects.equals(this.relativePaths, externalKafkaEntityStoreInfo.relativePaths) &&
        Objects.equals(this.mergedNodes, externalKafkaEntityStoreInfo.mergedNodes) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jsonTypeValue, jsonTypePath, relativePaths, mergedNodes, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalKafkaEntityStoreInfoDTO {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    jsonTypeValue: ").append(toIndentedString(jsonTypeValue)).append("\n");
    sb.append("    jsonTypePath: ").append(toIndentedString(jsonTypePath)).append("\n");
    sb.append("    relativePaths: ").append(toIndentedString(relativePaths)).append("\n");
    sb.append("    mergedNodes: ").append(toIndentedString(mergedNodes)).append("\n");
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

