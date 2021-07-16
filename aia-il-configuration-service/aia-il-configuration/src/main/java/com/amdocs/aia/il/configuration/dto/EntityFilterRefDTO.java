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
 * reference to entityFilter
 */
@ApiModel(description = "reference to entityFilter")
@Validated


public class EntityFilterRefDTO   {
  @JsonProperty("entityKey")
  private String entityKey = null;

  @JsonProperty("entityFilterKey")
  private String entityFilterKey = null;

  public EntityFilterRefDTO entityKey(String entityKey) {
    this.entityKey = entityKey;
    return this;
  }

  /**
   * Get entityKey
   * @return entityKey
  **/
  @ApiModelProperty(value = "")


  public String getEntityKey() {
    return entityKey;
  }

  public void setEntityKey(String entityKey) {
    this.entityKey = entityKey;
  }

  public EntityFilterRefDTO entityFilterKey(String entityFilterKey) {
    this.entityFilterKey = entityFilterKey;
    return this;
  }

  /**
   * Get entityFilterKey
   * @return entityFilterKey
  **/
  @ApiModelProperty(value = "")


  public String getEntityFilterKey() {
    return entityFilterKey;
  }

  public void setEntityFilterKey(String entityFilterKey) {
    this.entityFilterKey = entityFilterKey;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntityFilterRefDTO entityFilterRef = (EntityFilterRefDTO) o;
    return Objects.equals(this.entityKey, entityFilterRef.entityKey) &&
        Objects.equals(this.entityFilterKey, entityFilterRef.entityFilterKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityKey, entityFilterKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntityFilterRefDTO {\n");
    
    sb.append("    entityKey: ").append(toIndentedString(entityKey)).append("\n");
    sb.append("    entityFilterKey: ").append(toIndentedString(entityFilterKey)).append("\n");
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

