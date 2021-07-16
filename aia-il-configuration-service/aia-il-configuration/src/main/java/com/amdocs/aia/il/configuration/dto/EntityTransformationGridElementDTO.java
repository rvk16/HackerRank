package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * EntityTransformationGridElementDTO
 */
@Validated

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "entityTransformationType", visible = true )
@JsonSubTypes({
  @JsonSubTypes.Type(value = CachedEntityTransformationGridElementDTO.class, name = "CACHED"),
  @JsonSubTypes.Type(value = SharedEntityTransformationGridElementDTO.class, name = "SHARED"),
})


public class EntityTransformationGridElementDTO   {
  /**
   * Entity Transformation Type
   */
  public enum EntityTransformationTypeEnum {
    SHARED("SHARED"),
    
    CACHED("CACHED");

    private String value;

    EntityTransformationTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static EntityTransformationTypeEnum fromValue(String text) {
      for (EntityTransformationTypeEnum b : EntityTransformationTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("entityTransformationType")
  private EntityTransformationTypeEnum entityTransformationType = null;

  public EntityTransformationGridElementDTO entityTransformationType(EntityTransformationTypeEnum entityTransformationType) {
    this.entityTransformationType = entityTransformationType;
    return this;
  }

  /**
   * Entity Transformation Type
   * @return entityTransformationType
  **/
  @ApiModelProperty(required = true, value = "Entity Transformation Type")
  @NotNull


  public EntityTransformationTypeEnum getEntityTransformationType() {
    return entityTransformationType;
  }

  public void setEntityTransformationType(EntityTransformationTypeEnum entityTransformationType) {
    this.entityTransformationType = entityTransformationType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntityTransformationGridElementDTO entityTransformationGridElement = (EntityTransformationGridElementDTO) o;
    return Objects.equals(this.entityTransformationType, entityTransformationGridElement.entityTransformationType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityTransformationType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntityTransformationGridElementDTO {\n");
    
    sb.append("    entityTransformationType: ").append(toIndentedString(entityTransformationType)).append("\n");
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

