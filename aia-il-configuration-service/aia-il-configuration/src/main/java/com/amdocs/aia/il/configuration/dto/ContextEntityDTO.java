package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ContextEntityDTO
 */
@Validated


public class ContextEntityDTO   {
  @JsonProperty("aliasedSourceEntityKey")
  private String aliasedSourceEntityKey = null;

  @JsonProperty("doPropagation")
  private Boolean doPropagation = null;

  @JsonProperty("entityStoreKey")
  private String entityStoreKey = null;

  @JsonProperty("foreignKeys")
  private String foreignKeys = null;

  /**
   * Gets or Sets noReferentAction
   */
  public enum NoReferentActionEnum {
    MANDATORY("MANDATORY"),
    
    OPTIONAL("OPTIONAL"),
    
    MANDATORY_PUBLISH("MANDATORY_PUBLISH");

    private String value;

    NoReferentActionEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static NoReferentActionEnum fromValue(String text) {
      for (NoReferentActionEnum b : NoReferentActionEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("noReferentAction")
  private NoReferentActionEnum noReferentAction = null;

  @JsonProperty("parentContextEntityKey")
  private String parentContextEntityKey = null;

  /**
   * Gets or Sets relationType
   */
  public enum RelationTypeEnum {
    LEAD("LEAD"),
    
    OTO("OTO"),
    
    OTM("OTM"),
    
    MTO("MTO"),
    
    MTM("MTM"),
    
    REF("REF");

    private String value;

    RelationTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static RelationTypeEnum fromValue(String text) {
      for (RelationTypeEnum b : RelationTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("relationType")
  private RelationTypeEnum relationType = null;

  @JsonProperty("schemaStoreKey")
  private String schemaStoreKey = null;

  @JsonProperty("sourceAlias")
  private String sourceAlias = null;

  public ContextEntityDTO aliasedSourceEntityKey(String aliasedSourceEntityKey) {
    this.aliasedSourceEntityKey = aliasedSourceEntityKey;
    return this;
  }

  /**
   * Get aliasedSourceEntityKey
   * @return aliasedSourceEntityKey
  **/
  @ApiModelProperty(value = "")


  public String getAliasedSourceEntityKey() {
    return aliasedSourceEntityKey;
  }

  public void setAliasedSourceEntityKey(String aliasedSourceEntityKey) {
    this.aliasedSourceEntityKey = aliasedSourceEntityKey;
  }

  public ContextEntityDTO doPropagation(Boolean doPropagation) {
    this.doPropagation = doPropagation;
    return this;
  }

  /**
   * Get doPropagation
   * @return doPropagation
  **/
  @ApiModelProperty(value = "")


  public Boolean isDoPropagation() {
    return doPropagation;
  }

  public void setDoPropagation(Boolean doPropagation) {
    this.doPropagation = doPropagation;
  }

  public ContextEntityDTO entityStoreKey(String entityStoreKey) {
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

  public ContextEntityDTO foreignKeys(String foreignKeys) {
    this.foreignKeys = foreignKeys;
    return this;
  }

  /**
   * Get foreignKeys
   * @return foreignKeys
  **/
  @ApiModelProperty(value = "")


  public String getForeignKeys() {
    return foreignKeys;
  }

  public void setForeignKeys(String foreignKeys) {
    this.foreignKeys = foreignKeys;
  }

  public ContextEntityDTO noReferentAction(NoReferentActionEnum noReferentAction) {
    this.noReferentAction = noReferentAction;
    return this;
  }

  /**
   * Get noReferentAction
   * @return noReferentAction
  **/
  @ApiModelProperty(value = "")


  public NoReferentActionEnum getNoReferentAction() {
    return noReferentAction;
  }

  public void setNoReferentAction(NoReferentActionEnum noReferentAction) {
    this.noReferentAction = noReferentAction;
  }

  public ContextEntityDTO parentContextEntityKey(String parentContextEntityKey) {
    this.parentContextEntityKey = parentContextEntityKey;
    return this;
  }

  /**
   * Get parentContextEntityKey
   * @return parentContextEntityKey
  **/
  @ApiModelProperty(value = "")


  public String getParentContextEntityKey() {
    return parentContextEntityKey;
  }

  public void setParentContextEntityKey(String parentContextEntityKey) {
    this.parentContextEntityKey = parentContextEntityKey;
  }

  public ContextEntityDTO relationType(RelationTypeEnum relationType) {
    this.relationType = relationType;
    return this;
  }

  /**
   * Get relationType
   * @return relationType
  **/
  @ApiModelProperty(value = "")


  public RelationTypeEnum getRelationType() {
    return relationType;
  }

  public void setRelationType(RelationTypeEnum relationType) {
    this.relationType = relationType;
  }

  public ContextEntityDTO schemaStoreKey(String schemaStoreKey) {
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

  public ContextEntityDTO sourceAlias(String sourceAlias) {
    this.sourceAlias = sourceAlias;
    return this;
  }

  /**
   * Get sourceAlias
   * @return sourceAlias
  **/
  @ApiModelProperty(value = "")


  public String getSourceAlias() {
    return sourceAlias;
  }

  public void setSourceAlias(String sourceAlias) {
    this.sourceAlias = sourceAlias;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContextEntityDTO contextEntity = (ContextEntityDTO) o;
    return Objects.equals(this.aliasedSourceEntityKey, contextEntity.aliasedSourceEntityKey) &&
        Objects.equals(this.doPropagation, contextEntity.doPropagation) &&
        Objects.equals(this.entityStoreKey, contextEntity.entityStoreKey) &&
        Objects.equals(this.foreignKeys, contextEntity.foreignKeys) &&
        Objects.equals(this.noReferentAction, contextEntity.noReferentAction) &&
        Objects.equals(this.parentContextEntityKey, contextEntity.parentContextEntityKey) &&
        Objects.equals(this.relationType, contextEntity.relationType) &&
        Objects.equals(this.schemaStoreKey, contextEntity.schemaStoreKey) &&
        Objects.equals(this.sourceAlias, contextEntity.sourceAlias);
  }

  @Override
  public int hashCode() {
    return Objects.hash(aliasedSourceEntityKey, doPropagation, entityStoreKey, foreignKeys, noReferentAction, parentContextEntityKey, relationType, schemaStoreKey, sourceAlias);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContextEntityDTO {\n");
    
    sb.append("    aliasedSourceEntityKey: ").append(toIndentedString(aliasedSourceEntityKey)).append("\n");
    sb.append("    doPropagation: ").append(toIndentedString(doPropagation)).append("\n");
    sb.append("    entityStoreKey: ").append(toIndentedString(entityStoreKey)).append("\n");
    sb.append("    foreignKeys: ").append(toIndentedString(foreignKeys)).append("\n");
    sb.append("    noReferentAction: ").append(toIndentedString(noReferentAction)).append("\n");
    sb.append("    parentContextEntityKey: ").append(toIndentedString(parentContextEntityKey)).append("\n");
    sb.append("    relationType: ").append(toIndentedString(relationType)).append("\n");
    sb.append("    schemaStoreKey: ").append(toIndentedString(schemaStoreKey)).append("\n");
    sb.append("    sourceAlias: ").append(toIndentedString(sourceAlias)).append("\n");
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

