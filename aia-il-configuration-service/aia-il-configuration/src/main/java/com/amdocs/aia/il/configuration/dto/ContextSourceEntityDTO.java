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
 * ContextSourceEntityDTO
 */
@Validated


public class ContextSourceEntityDTO   {
  @JsonProperty("entityKey")
  private String entityKey = null;

  @JsonProperty("entityName")
  private String entityName = null;

  public ContextSourceEntityDTO entityKey(String entityKey) {
    this.entityKey = entityKey;
    return this;
  }

  /**
   * Context source entity key
   * @return entityKey
  **/
  @ApiModelProperty(required = true, value = "Context source entity key")
  @NotNull


  public String getEntityKey() {
    return entityKey;
  }

  public void setEntityKey(String entityKey) {
    this.entityKey = entityKey;
  }

  public ContextSourceEntityDTO entityName(String entityName) {
    this.entityName = entityName;
    return this;
  }

  /**
   * Context source entity name
   * @return entityName
  **/
  @ApiModelProperty(value = "Context source entity name")


  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContextSourceEntityDTO contextSourceEntity = (ContextSourceEntityDTO) o;
    return Objects.equals(this.entityKey, contextSourceEntity.entityKey) &&
        Objects.equals(this.entityName, contextSourceEntity.entityName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityKey, entityName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContextSourceEntityDTO {\n");
    
    sb.append("    entityKey: ").append(toIndentedString(entityKey)).append("\n");
    sb.append("    entityName: ").append(toIndentedString(entityName)).append("\n");
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

