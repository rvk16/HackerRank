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
 * Entity filter
 */
@ApiModel(description = "Entity filter")
@Validated


public class EntityFilterDTO   {
  @JsonProperty("entityFilterKey")
  private String entityFilterKey = null;

  @JsonProperty("schemaKey")
  private String schemaKey = null;

  @JsonProperty("entityKey")
  private String entityKey = null;

  @JsonProperty("query")
  private String query = null;

  public EntityFilterDTO entityFilterKey(String entityFilterKey) {
    this.entityFilterKey = entityFilterKey;
    return this;
  }

  /**
   * entity filter key
   * @return entityFilterKey
  **/
  @ApiModelProperty(value = "entity filter key")


  public String getEntityFilterKey() {
    return entityFilterKey;
  }

  public void setEntityFilterKey(String entityFilterKey) {
    this.entityFilterKey = entityFilterKey;
  }

  public EntityFilterDTO schemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
    return this;
  }

  /**
   * schema entity key
   * @return schemaKey
  **/
  @ApiModelProperty(value = "schema entity key")


  public String getSchemaKey() {
    return schemaKey;
  }

  public void setSchemaKey(String schemaKey) {
    this.schemaKey = schemaKey;
  }

  public EntityFilterDTO entityKey(String entityKey) {
    this.entityKey = entityKey;
    return this;
  }

  /**
   * schema entity key
   * @return entityKey
  **/
  @ApiModelProperty(value = "schema entity key")


  public String getEntityKey() {
    return entityKey;
  }

  public void setEntityKey(String entityKey) {
    this.entityKey = entityKey;
  }

  public EntityFilterDTO query(String query) {
    this.query = query;
    return this;
  }

  /**
   * The query
   * @return query
  **/
  @ApiModelProperty(value = "The query")


  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntityFilterDTO entityFilter = (EntityFilterDTO) o;
    return Objects.equals(this.entityFilterKey, entityFilter.entityFilterKey) &&
        Objects.equals(this.schemaKey, entityFilter.schemaKey) &&
        Objects.equals(this.entityKey, entityFilter.entityKey) &&
        Objects.equals(this.query, entityFilter.query);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityFilterKey, schemaKey, entityKey, query);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntityFilterDTO {\n");
    
    sb.append("    entityFilterKey: ").append(toIndentedString(entityFilterKey)).append("\n");
    sb.append("    schemaKey: ").append(toIndentedString(schemaKey)).append("\n");
    sb.append("    entityKey: ").append(toIndentedString(entityKey)).append("\n");
    sb.append("    query: ").append(toIndentedString(query)).append("\n");
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

