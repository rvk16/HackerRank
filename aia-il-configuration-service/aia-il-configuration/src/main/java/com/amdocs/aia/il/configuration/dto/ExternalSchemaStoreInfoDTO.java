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
 * ExternalSchemaStoreInfoDTO
 */
@Validated

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "storeType", visible = true )
@JsonSubTypes({
  @JsonSubTypes.Type(value = ExternalSqlSchemaStoreInfoDTO.class, name = "SQL"),
  @JsonSubTypes.Type(value = ExternalKafkaSchemaStoreInfoDTO.class, name = "KAFKA"),
  @JsonSubTypes.Type(value = ExternalCsvSchemaStoreInfoDTO.class, name = "CSV"),
})


public class ExternalSchemaStoreInfoDTO   {
  /**
   * Gets or Sets storeType
   */
  public enum StoreTypeEnum {
    CSV("CSV"),
    
    SQL("SQL"),
    
    KAFKA("KAFKA");

    private String value;

    StoreTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StoreTypeEnum fromValue(String text) {
      for (StoreTypeEnum b : StoreTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("storeType")
  private StoreTypeEnum storeType = null;

  public ExternalSchemaStoreInfoDTO storeType(StoreTypeEnum storeType) {
    this.storeType = storeType;
    return this;
  }

  /**
   * Get storeType
   * @return storeType
  **/
  @ApiModelProperty(value = "")


  public StoreTypeEnum getStoreType() {
    return storeType;
  }

  public void setStoreType(StoreTypeEnum storeType) {
    this.storeType = storeType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalSchemaStoreInfoDTO externalSchemaStoreInfo = (ExternalSchemaStoreInfoDTO) o;
    return Objects.equals(this.storeType, externalSchemaStoreInfo.storeType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(storeType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalSchemaStoreInfoDTO {\n");
    
    sb.append("    storeType: ").append(toIndentedString(storeType)).append("\n");
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

