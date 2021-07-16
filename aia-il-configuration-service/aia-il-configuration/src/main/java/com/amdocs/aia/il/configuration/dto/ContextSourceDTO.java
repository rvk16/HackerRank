package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ContextSourceEntityDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ContextSourceDTO
 */
@Validated


public class ContextSourceDTO   {
  @JsonProperty("schemaKey")
  private String schemaKey = null;

  @JsonProperty("schemaName")
  private String schemaName = null;

  /**
   * Context source schema type
   */
  public enum SchemaTypeEnum {
    CACHE("CACHE"),
    
    REFERENCE("REFERENCE"),
    
    EXTERNAL("EXTERNAL");

    private String value;

    SchemaTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static SchemaTypeEnum fromValue(String text) {
      for (SchemaTypeEnum b : SchemaTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("schemaType")
  private SchemaTypeEnum schemaType = null;

  @JsonProperty("contextSourceEntities")
  @Valid
  private List<ContextSourceEntityDTO> contextSourceEntities = null;

  public ContextSourceDTO schemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
    return this;
  }

  /**
   * Context source schema key
   * @return schemaKey
  **/
  @ApiModelProperty(required = true, value = "Context source schema key")
  @NotNull


  public String getSchemaKey() {
    return schemaKey;
  }

  public void setSchemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
  }

  public ContextSourceDTO schemaName(String schemaName) {
    this.schemaName = schemaName;
    return this;
  }

  /**
   * Context source schema name
   * @return schemaName
  **/
  @ApiModelProperty(value = "Context source schema name")


  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public ContextSourceDTO schemaType(SchemaTypeEnum schemaType) {
    this.schemaType = schemaType;
    return this;
  }

  /**
   * Context source schema type
   * @return schemaType
  **/
  @ApiModelProperty(value = "Context source schema type")


  public SchemaTypeEnum getSchemaType() {
    return schemaType;
  }

  public void setSchemaType(SchemaTypeEnum schemaType) {
    this.schemaType = schemaType;
  }

  public ContextSourceDTO contextSourceEntities(List<ContextSourceEntityDTO> contextSourceEntities) {
    this.contextSourceEntities = contextSourceEntities;
    return this;
  }

  public ContextSourceDTO addContextSourceEntitiesItem(ContextSourceEntityDTO contextSourceEntitiesItem) {
    if (this.contextSourceEntities == null) {
      this.contextSourceEntities = new ArrayList<>();
    }
    this.contextSourceEntities.add(contextSourceEntitiesItem);
    return this;
  }

  /**
   * List of context source entities
   * @return contextSourceEntities
  **/
  @ApiModelProperty(value = "List of context source entities")

  @Valid

  public List<ContextSourceEntityDTO> getContextSourceEntities() {
    return contextSourceEntities;
  }

  public void setContextSourceEntities(List<ContextSourceEntityDTO> contextSourceEntities) {
    this.contextSourceEntities = contextSourceEntities;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContextSourceDTO contextSource = (ContextSourceDTO) o;
    return Objects.equals(this.schemaKey, contextSource.schemaKey) &&
        Objects.equals(this.schemaName, contextSource.schemaName) &&
        Objects.equals(this.schemaType, contextSource.schemaType) &&
        Objects.equals(this.contextSourceEntities, contextSource.contextSourceEntities);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemaKey, schemaName, schemaType, contextSourceEntities);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContextSourceDTO {\n");
    
    sb.append("    schemaKey: ").append(toIndentedString(schemaKey)).append("\n");
    sb.append("    schemaName: ").append(toIndentedString(schemaName)).append("\n");
    sb.append("    schemaType: ").append(toIndentedString(schemaType)).append("\n");
    sb.append("    contextSourceEntities: ").append(toIndentedString(contextSourceEntities)).append("\n");
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

