package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaCollectionRulesDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ExternalKafkaSchemaCollectionRulesDTO
 */
@Validated


public class ExternalKafkaSchemaCollectionRulesDTO extends ExternalSchemaCollectionRulesDTO  {
  @JsonProperty("inputDataChannel")
  private String inputDataChannel = null;

  @JsonProperty("skipNodeFromParsing")
  private String skipNodeFromParsing = null;

  @JsonProperty("deleteEventJsonPath")
  private String deleteEventJsonPath = null;

  @JsonProperty("deleteEventOperation")
  private String deleteEventOperation = null;

  @JsonProperty("implicitHandlerPreviousNode")
  private String implicitHandlerPreviousNode = null;

  @JsonProperty("implicitHandlerCurrentNode")
  private String implicitHandlerCurrentNode = null;

  public ExternalKafkaSchemaCollectionRulesDTO inputDataChannel(String inputDataChannel) {
    this.inputDataChannel = inputDataChannel;
    return this;
  }

  /**
   * Input Data Channel
   * @return inputDataChannel
  **/
  @ApiModelProperty(value = "Input Data Channel")


  public String getInputDataChannel() {
    return inputDataChannel;
  }

  public void setInputDataChannel(String inputDataChannel) {
    this.inputDataChannel = inputDataChannel;
  }

  public ExternalKafkaSchemaCollectionRulesDTO skipNodeFromParsing(String skipNodeFromParsing) {
    this.skipNodeFromParsing = skipNodeFromParsing;
    return this;
  }

  /**
   * Skip node from parsing
   * @return skipNodeFromParsing
  **/
  @ApiModelProperty(value = "Skip node from parsing")


  public String getSkipNodeFromParsing() {
    return skipNodeFromParsing;
  }

  public void setSkipNodeFromParsing(String skipNodeFromParsing) {
    this.skipNodeFromParsing = skipNodeFromParsing;
  }

  public ExternalKafkaSchemaCollectionRulesDTO deleteEventJsonPath(String deleteEventJsonPath) {
    this.deleteEventJsonPath = deleteEventJsonPath;
    return this;
  }

  /**
   * Delete event json path
   * @return deleteEventJsonPath
  **/
  @ApiModelProperty(value = "Delete event json path")


  public String getDeleteEventJsonPath() {
    return deleteEventJsonPath;
  }

  public void setDeleteEventJsonPath(String deleteEventJsonPath) {
    this.deleteEventJsonPath = deleteEventJsonPath;
  }

  public ExternalKafkaSchemaCollectionRulesDTO deleteEventOperation(String deleteEventOperation) {
    this.deleteEventOperation = deleteEventOperation;
    return this;
  }

  /**
   * Delete event operation
   * @return deleteEventOperation
  **/
  @ApiModelProperty(value = "Delete event operation")


  public String getDeleteEventOperation() {
    return deleteEventOperation;
  }

  public void setDeleteEventOperation(String deleteEventOperation) {
    this.deleteEventOperation = deleteEventOperation;
  }

  public ExternalKafkaSchemaCollectionRulesDTO implicitHandlerPreviousNode(String implicitHandlerPreviousNode) {
    this.implicitHandlerPreviousNode = implicitHandlerPreviousNode;
    return this;
  }

  /**
   * Implicit handler previous node
   * @return implicitHandlerPreviousNode
  **/
  @ApiModelProperty(value = "Implicit handler previous node")


  public String getImplicitHandlerPreviousNode() {
    return implicitHandlerPreviousNode;
  }

  public void setImplicitHandlerPreviousNode(String implicitHandlerPreviousNode) {
    this.implicitHandlerPreviousNode = implicitHandlerPreviousNode;
  }

  public ExternalKafkaSchemaCollectionRulesDTO implicitHandlerCurrentNode(String implicitHandlerCurrentNode) {
    this.implicitHandlerCurrentNode = implicitHandlerCurrentNode;
    return this;
  }

  /**
   * Implicit handler current node
   * @return implicitHandlerCurrentNode
  **/
  @ApiModelProperty(value = "Implicit handler current node")


  public String getImplicitHandlerCurrentNode() {
    return implicitHandlerCurrentNode;
  }

  public void setImplicitHandlerCurrentNode(String implicitHandlerCurrentNode) {
    this.implicitHandlerCurrentNode = implicitHandlerCurrentNode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalKafkaSchemaCollectionRulesDTO externalKafkaSchemaCollectionRules = (ExternalKafkaSchemaCollectionRulesDTO) o;
    return Objects.equals(this.inputDataChannel, externalKafkaSchemaCollectionRules.inputDataChannel) &&
        Objects.equals(this.skipNodeFromParsing, externalKafkaSchemaCollectionRules.skipNodeFromParsing) &&
        Objects.equals(this.deleteEventJsonPath, externalKafkaSchemaCollectionRules.deleteEventJsonPath) &&
        Objects.equals(this.deleteEventOperation, externalKafkaSchemaCollectionRules.deleteEventOperation) &&
        Objects.equals(this.implicitHandlerPreviousNode, externalKafkaSchemaCollectionRules.implicitHandlerPreviousNode) &&
        Objects.equals(this.implicitHandlerCurrentNode, externalKafkaSchemaCollectionRules.implicitHandlerCurrentNode) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inputDataChannel, skipNodeFromParsing, deleteEventJsonPath, deleteEventOperation, implicitHandlerPreviousNode, implicitHandlerCurrentNode, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalKafkaSchemaCollectionRulesDTO {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    inputDataChannel: ").append(toIndentedString(inputDataChannel)).append("\n");
    sb.append("    skipNodeFromParsing: ").append(toIndentedString(skipNodeFromParsing)).append("\n");
    sb.append("    deleteEventJsonPath: ").append(toIndentedString(deleteEventJsonPath)).append("\n");
    sb.append("    deleteEventOperation: ").append(toIndentedString(deleteEventOperation)).append("\n");
    sb.append("    implicitHandlerPreviousNode: ").append(toIndentedString(implicitHandlerPreviousNode)).append("\n");
    sb.append("    implicitHandlerCurrentNode: ").append(toIndentedString(implicitHandlerCurrentNode)).append("\n");
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

