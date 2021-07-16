package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.RelationDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * EntityReferentialIntegrityDTO
 */
@Validated


public class EntityReferentialIntegrityDTO   {
  @JsonProperty("schemaStoreKey")
  private String schemaStoreKey = null;

  @JsonProperty("entityStoreKey")
  private String entityStoreKey = null;

  @JsonProperty("relations")
  @Valid
  private List<RelationDTO> relations = null;

  public EntityReferentialIntegrityDTO schemaStoreKey(String schemaStoreKey) {
    this.schemaStoreKey = schemaStoreKey;
    return this;
  }

  /**
   * Schema store key
   * @return schemaStoreKey
  **/
  @ApiModelProperty(required = true, value = "Schema store key")
  @NotNull


  public String getSchemaStoreKey() {
    return schemaStoreKey;
  }

  public void setSchemaStoreKey(String schemaStoreKey) {
    this.schemaStoreKey = schemaStoreKey;
  }

  public EntityReferentialIntegrityDTO entityStoreKey(String entityStoreKey) {
    this.entityStoreKey = entityStoreKey;
    return this;
  }

  /**
   * Entity store key
   * @return entityStoreKey
  **/
  @ApiModelProperty(required = true, value = "Entity store key")
  @NotNull


  public String getEntityStoreKey() {
    return entityStoreKey;
  }

  public void setEntityStoreKey(String entityStoreKey) {
    this.entityStoreKey = entityStoreKey;
  }

  public EntityReferentialIntegrityDTO relations(List<RelationDTO> relations) {
    this.relations = relations;
    return this;
  }

  public EntityReferentialIntegrityDTO addRelationsItem(RelationDTO relationsItem) {
    if (this.relations == null) {
      this.relations = new ArrayList<>();
    }
    this.relations.add(relationsItem);
    return this;
  }

  /**
   * Relations list
   * @return relations
  **/
  @ApiModelProperty(value = "Relations list")

  @Valid

  public List<RelationDTO> getRelations() {
    return relations;
  }

  public void setRelations(List<RelationDTO> relations) {
    this.relations = relations;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntityReferentialIntegrityDTO entityReferentialIntegrity = (EntityReferentialIntegrityDTO) o;
    return Objects.equals(this.schemaStoreKey, entityReferentialIntegrity.schemaStoreKey) &&
        Objects.equals(this.entityStoreKey, entityReferentialIntegrity.entityStoreKey) &&
        Objects.equals(this.relations, entityReferentialIntegrity.relations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemaStoreKey, entityStoreKey, relations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntityReferentialIntegrityDTO {\n");
    
    sb.append("    schemaStoreKey: ").append(toIndentedString(schemaStoreKey)).append("\n");
    sb.append("    entityStoreKey: ").append(toIndentedString(entityStoreKey)).append("\n");
    sb.append("    relations: ").append(toIndentedString(relations)).append("\n");
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

