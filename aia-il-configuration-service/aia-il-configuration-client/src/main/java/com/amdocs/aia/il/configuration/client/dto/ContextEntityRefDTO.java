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

/**
 * ContextEntityRefDTO
 */

public class ContextEntityRefDTO {
  @JsonProperty("entityKey")
  private String entityKey = null;

  @JsonProperty("schemaKey")
  private String schemaKey = null;

  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    CACHE("CACHE"),
    
    REFERENCE("REFERENCE"),
    
    EXTERNAL("EXTERNAL");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("type")
  private TypeEnum type = null;

  public ContextEntityRefDTO entityKey(String entityKey) {
    this.entityKey = entityKey;
    return this;
  }

   /**
   * Entity key
   * @return entityKey
  **/
  @ApiModelProperty(required = true, value = "Entity key")
  public String getEntityKey() {
    return entityKey;
  }

  public void setEntityKey(String entityKey) {
    this.entityKey = entityKey;
  }

  public ContextEntityRefDTO schemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
    return this;
  }

   /**
   * Schema key
   * @return schemaKey
  **/
  @ApiModelProperty(value = "Schema key")
  public String getSchemaKey() {
    return schemaKey;
  }

  public void setSchemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
  }

  public ContextEntityRefDTO type(TypeEnum type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @ApiModelProperty(value = "")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContextEntityRefDTO contextEntityRef = (ContextEntityRefDTO) o;
    return Objects.equals(this.entityKey, contextEntityRef.entityKey) &&
        Objects.equals(this.schemaKey, contextEntityRef.schemaKey) &&
        Objects.equals(this.type, contextEntityRef.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityKey, schemaKey, type);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContextEntityRefDTO {\n");
    
    sb.append("    entityKey: ").append(toIndentedString(entityKey)).append("\n");
    sb.append("    schemaKey: ").append(toIndentedString(schemaKey)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

