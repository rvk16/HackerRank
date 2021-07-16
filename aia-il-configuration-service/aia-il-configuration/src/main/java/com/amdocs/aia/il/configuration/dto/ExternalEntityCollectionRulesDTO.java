package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ExternalEntityFilterDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityIncrementalAttributeDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ExternalEntityCollectionRulesDTO
 */
@Validated

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "storeType", visible = true )
@JsonSubTypes({
  @JsonSubTypes.Type(value = ExternalKafkaEntityCollectionRulesDTO.class, name = "KAFKA"),
  @JsonSubTypes.Type(value = ExternalSqlEntityCollectionRulesDTO.class, name = "SQL"),
  @JsonSubTypes.Type(value = ExternalCsvEntityCollectionRulesDTO.class, name = "CSV"),
})


public class ExternalEntityCollectionRulesDTO   {
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

  @JsonProperty("defaultFilter")
  private ExternalEntityFilterDTO defaultFilter = null;

  @JsonProperty("filters")
  @Valid
  private List<ExternalEntityFilterDTO> filters = null;

  @JsonProperty("incrementalAttribute")
  private ExternalEntityIncrementalAttributeDTO incrementalAttribute = null;

  public ExternalEntityCollectionRulesDTO storeType(StoreTypeEnum storeType) {
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

  public ExternalEntityCollectionRulesDTO defaultFilter(ExternalEntityFilterDTO defaultFilter) {
    this.defaultFilter = defaultFilter;
    return this;
  }

  /**
   * Get defaultFilter
   * @return defaultFilter
  **/
  @ApiModelProperty(value = "")

  @Valid

  public ExternalEntityFilterDTO getDefaultFilter() {
    return defaultFilter;
  }

  public void setDefaultFilter(ExternalEntityFilterDTO defaultFilter) {
    this.defaultFilter = defaultFilter;
  }

  public ExternalEntityCollectionRulesDTO filters(List<ExternalEntityFilterDTO> filters) {
    this.filters = filters;
    return this;
  }

  public ExternalEntityCollectionRulesDTO addFiltersItem(ExternalEntityFilterDTO filtersItem) {
    if (this.filters == null) {
      this.filters = new ArrayList<>();
    }
    this.filters.add(filtersItem);
    return this;
  }

  /**
   * Filters for this entity (in addition to the default filter)
   * @return filters
  **/
  @ApiModelProperty(value = "Filters for this entity (in addition to the default filter)")

  @Valid

  public List<ExternalEntityFilterDTO> getFilters() {
    return filters;
  }

  public void setFilters(List<ExternalEntityFilterDTO> filters) {
    this.filters = filters;
  }

  public ExternalEntityCollectionRulesDTO incrementalAttribute(ExternalEntityIncrementalAttributeDTO incrementalAttribute) {
    this.incrementalAttribute = incrementalAttribute;
    return this;
  }

  /**
   * Get incrementalAttribute
   * @return incrementalAttribute
  **/
  @ApiModelProperty(value = "")

  @Valid

  public ExternalEntityIncrementalAttributeDTO getIncrementalAttribute() {
    return incrementalAttribute;
  }

  public void setIncrementalAttribute(ExternalEntityIncrementalAttributeDTO incrementalAttribute) {
    this.incrementalAttribute = incrementalAttribute;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalEntityCollectionRulesDTO externalEntityCollectionRules = (ExternalEntityCollectionRulesDTO) o;
    return Objects.equals(this.storeType, externalEntityCollectionRules.storeType) &&
        Objects.equals(this.defaultFilter, externalEntityCollectionRules.defaultFilter) &&
        Objects.equals(this.filters, externalEntityCollectionRules.filters) &&
        Objects.equals(this.incrementalAttribute, externalEntityCollectionRules.incrementalAttribute);
  }

  @Override
  public int hashCode() {
    return Objects.hash(storeType, defaultFilter, filters, incrementalAttribute);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalEntityCollectionRulesDTO {\n");
    
    sb.append("    storeType: ").append(toIndentedString(storeType)).append("\n");
    sb.append("    defaultFilter: ").append(toIndentedString(defaultFilter)).append("\n");
    sb.append("    filters: ").append(toIndentedString(filters)).append("\n");
    sb.append("    incrementalAttribute: ").append(toIndentedString(incrementalAttribute)).append("\n");
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

