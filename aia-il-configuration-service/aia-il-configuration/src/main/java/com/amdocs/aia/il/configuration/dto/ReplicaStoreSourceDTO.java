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
 * Replica Store source
 */
@ApiModel(description = "Replica Store source")
@Validated


public class ReplicaStoreSourceDTO   {
  @JsonProperty("schemaStoreKey")
  private String schemaStoreKey = null;

  @JsonProperty("entityStoreKey")
  private String entityStoreKey = null;

  public ReplicaStoreSourceDTO schemaStoreKey(String schemaStoreKey) {
    this.schemaStoreKey = schemaStoreKey;
    return this;
  }

  /**
   * Schema store's key of the entity
   * @return schemaStoreKey
  **/
  @ApiModelProperty(value = "Schema store's key of the entity")


  public String getSchemaStoreKey() {
    return schemaStoreKey;
  }

  public void setSchemaStoreKey(String schemaStoreKey) {
    this.schemaStoreKey = schemaStoreKey;
  }

  public ReplicaStoreSourceDTO entityStoreKey(String entityStoreKey) {
    this.entityStoreKey = entityStoreKey;
    return this;
  }

  /**
   * Entity store's key
   * @return entityStoreKey
  **/
  @ApiModelProperty(value = "Entity store's key")


  public String getEntityStoreKey() {
    return entityStoreKey;
  }

  public void setEntityStoreKey(String entityStoreKey) {
    this.entityStoreKey = entityStoreKey;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReplicaStoreSourceDTO replicaStoreSource = (ReplicaStoreSourceDTO) o;
    return Objects.equals(this.schemaStoreKey, replicaStoreSource.schemaStoreKey) &&
        Objects.equals(this.entityStoreKey, replicaStoreSource.entityStoreKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemaStoreKey, entityStoreKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReplicaStoreSourceDTO {\n");
    
    sb.append("    schemaStoreKey: ").append(toIndentedString(schemaStoreKey)).append("\n");
    sb.append("    entityStoreKey: ").append(toIndentedString(entityStoreKey)).append("\n");
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

