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
 * TransformationContextEntityDTO
 */
@Validated


public class TransformationContextEntityDTO   {
  @JsonProperty("entityStoreKey")
  private String entityStoreKey = null;

  @JsonProperty("filterDescription")
  private String filterDescription = null;

  @JsonProperty("schemaStoreKey")
  private String schemaStoreKey = null;

  public TransformationContextEntityDTO entityStoreKey(String entityStoreKey) {
    this.entityStoreKey = entityStoreKey;
    return this;
  }

  /**
   * Get entityStoreKey
   * @return entityStoreKey
  **/
  @ApiModelProperty(value = "")


  public String getEntityStoreKey() {
    return entityStoreKey;
  }

  public void setEntityStoreKey(String entityStoreKey) {
    this.entityStoreKey = entityStoreKey;
  }

  public TransformationContextEntityDTO filterDescription(String filterDescription) {
    this.filterDescription = filterDescription;
    return this;
  }

  /**
   * Get filterDescription
   * @return filterDescription
  **/
  @ApiModelProperty(value = "")


  public String getFilterDescription() {
    return filterDescription;
  }

  public void setFilterDescription(String filterDescription) {
    this.filterDescription = filterDescription;
  }

  public TransformationContextEntityDTO schemaStoreKey(String schemaStoreKey) {
    this.schemaStoreKey = schemaStoreKey;
    return this;
  }

  /**
   * Get schemaStoreKey
   * @return schemaStoreKey
  **/
  @ApiModelProperty(value = "")


  public String getSchemaStoreKey() {
    return schemaStoreKey;
  }

  public void setSchemaStoreKey(String schemaStoreKey) {
    this.schemaStoreKey = schemaStoreKey;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransformationContextEntityDTO transformationContextEntity = (TransformationContextEntityDTO) o;
    return Objects.equals(this.entityStoreKey, transformationContextEntity.entityStoreKey) &&
        Objects.equals(this.filterDescription, transformationContextEntity.filterDescription) &&
        Objects.equals(this.schemaStoreKey, transformationContextEntity.schemaStoreKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityStoreKey, filterDescription, schemaStoreKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransformationContextEntityDTO {\n");
    
    sb.append("    entityStoreKey: ").append(toIndentedString(entityStoreKey)).append("\n");
    sb.append("    filterDescription: ").append(toIndentedString(filterDescription)).append("\n");
    sb.append("    schemaStoreKey: ").append(toIndentedString(schemaStoreKey)).append("\n");
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

