/*
 * Integration Layer Configuration API
 * This is a REST API specification for Integration Layer Configuration application.
 *
 * OpenAPI spec version: TRUNK-SNAPSHOT
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.amdocs.aia.il.configuration.client.dto;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SchemaDiscoveryRequestDTO
 */

public class SchemaDiscoveryRequestDTO {
  @JsonProperty("externalSchemaType")
  private String externalSchemaType = null;

  @JsonProperty("schemaName")
  private String schemaName = null;

  @JsonProperty("parameters")
  private Map<String, Object> parameters = null;

  public SchemaDiscoveryRequestDTO externalSchemaType(String externalSchemaType) {
    this.externalSchemaType = externalSchemaType;
    return this;
  }

   /**
   * The external schema type (available types can be retrieved via the &#39;list external schema types&#39; API)
   * @return externalSchemaType
  **/
  @ApiModelProperty(value = "The external schema type (available types can be retrieved via the 'list external schema types' API)")
  public String getExternalSchemaType() {
    return externalSchemaType;
  }

  public void setExternalSchemaType(String externalSchemaType) {
    this.externalSchemaType = externalSchemaType;
  }

  public SchemaDiscoveryRequestDTO schemaName(String schemaName) {
    this.schemaName = schemaName;
    return this;
  }

   /**
   * The name of the external schema (note that this can be a human-readable name. The schemaKey will be automatically generated from this name)
   * @return schemaName
  **/
  @ApiModelProperty(value = "The name of the external schema (note that this can be a human-readable name. The schemaKey will be automatically generated from this name)")
  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public SchemaDiscoveryRequestDTO parameters(Map<String, Object> parameters) {
    this.parameters = parameters;
    return this;
  }

  public SchemaDiscoveryRequestDTO putParametersItem(String key, Object parametersItem) {
    if (this.parameters == null) {
      this.parameters = new HashMap<>();
    }
    this.parameters.put(key, parametersItem);
    return this;
  }

   /**
   * Additional parameters required for the discovery (such as connection details, file ids, etc.)
   * @return parameters
  **/
  @ApiModelProperty(value = "Additional parameters required for the discovery (such as connection details, file ids, etc.)")
  public Map<String, Object> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SchemaDiscoveryRequestDTO schemaDiscoveryRequest = (SchemaDiscoveryRequestDTO) o;
    return Objects.equals(this.externalSchemaType, schemaDiscoveryRequest.externalSchemaType) &&
        Objects.equals(this.schemaName, schemaDiscoveryRequest.schemaName) &&
        Objects.equals(this.parameters, schemaDiscoveryRequest.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalSchemaType, schemaName, parameters);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SchemaDiscoveryRequestDTO {\n");
    
    sb.append("    externalSchemaType: ").append(toIndentedString(externalSchemaType)).append("\n");
    sb.append("    schemaName: ").append(toIndentedString(schemaName)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
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

