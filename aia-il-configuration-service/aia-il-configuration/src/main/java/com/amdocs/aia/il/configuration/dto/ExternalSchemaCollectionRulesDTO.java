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
 * ExternalSchemaCollectionRulesDTO
 */
@Validated

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "storeType", visible = true )
@JsonSubTypes({
  @JsonSubTypes.Type(value = ExternalCsvSchemaCollectionRulesDTO.class, name = "CSV"),
  @JsonSubTypes.Type(value = ExternalKafkaSchemaCollectionRulesDTO.class, name = "KAFKA"),
  @JsonSubTypes.Type(value = ExternalSqlSchemaCollectionRulesDTO.class, name = "SQL"),
})


public class ExternalSchemaCollectionRulesDTO   {
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

  @JsonProperty("ongoingChannel")
  private String ongoingChannel = null;

  @JsonProperty("initialLoadChannel")
  private String initialLoadChannel = null;

  @JsonProperty("replayChannel")
  private String replayChannel = null;

  @JsonProperty("initialLoadRelativeURL")
  private String initialLoadRelativeURL = null;

  @JsonProperty("partialLoadRelativeURL")
  private String partialLoadRelativeURL = null;

  public ExternalSchemaCollectionRulesDTO storeType(StoreTypeEnum storeType) {
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

  public ExternalSchemaCollectionRulesDTO ongoingChannel(String ongoingChannel) {
    this.ongoingChannel = ongoingChannel;
    return this;
  }

  /**
   * The channel used for collecting ongoing data
   * @return ongoingChannel
  **/
  @ApiModelProperty(value = "The channel used for collecting ongoing data")


  public String getOngoingChannel() {
    return ongoingChannel;
  }

  public void setOngoingChannel(String ongoingChannel) {
    this.ongoingChannel = ongoingChannel;
  }

  public ExternalSchemaCollectionRulesDTO initialLoadChannel(String initialLoadChannel) {
    this.initialLoadChannel = initialLoadChannel;
    return this;
  }

  /**
   * The channel used for collecting initial data
   * @return initialLoadChannel
  **/
  @ApiModelProperty(value = "The channel used for collecting initial data")


  public String getInitialLoadChannel() {
    return initialLoadChannel;
  }

  public void setInitialLoadChannel(String initialLoadChannel) {
    this.initialLoadChannel = initialLoadChannel;
  }

  public ExternalSchemaCollectionRulesDTO replayChannel(String replayChannel) {
    this.replayChannel = replayChannel;
    return this;
  }

  /**
   * The channel used for collecting selective data
   * @return replayChannel
  **/
  @ApiModelProperty(value = "The channel used for collecting selective data")


  public String getReplayChannel() {
    return replayChannel;
  }

  public void setReplayChannel(String replayChannel) {
    this.replayChannel = replayChannel;
  }

  public ExternalSchemaCollectionRulesDTO initialLoadRelativeURL(String initialLoadRelativeURL) {
    this.initialLoadRelativeURL = initialLoadRelativeURL;
    return this;
  }

  /**
   * The URL used for initial load
   * @return initialLoadRelativeURL
  **/
  @ApiModelProperty(value = "The URL used for initial load")


  public String getInitialLoadRelativeURL() {
    return initialLoadRelativeURL;
  }

  public void setInitialLoadRelativeURL(String initialLoadRelativeURL) {
    this.initialLoadRelativeURL = initialLoadRelativeURL;
  }

  public ExternalSchemaCollectionRulesDTO partialLoadRelativeURL(String partialLoadRelativeURL) {
    this.partialLoadRelativeURL = partialLoadRelativeURL;
    return this;
  }

  /**
   * The URL used for partial load
   * @return partialLoadRelativeURL
  **/
  @ApiModelProperty(value = "The URL used for partial load")


  public String getPartialLoadRelativeURL() {
    return partialLoadRelativeURL;
  }

  public void setPartialLoadRelativeURL(String partialLoadRelativeURL) {
    this.partialLoadRelativeURL = partialLoadRelativeURL;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalSchemaCollectionRulesDTO externalSchemaCollectionRules = (ExternalSchemaCollectionRulesDTO) o;
    return Objects.equals(this.storeType, externalSchemaCollectionRules.storeType) &&
        Objects.equals(this.ongoingChannel, externalSchemaCollectionRules.ongoingChannel) &&
        Objects.equals(this.initialLoadChannel, externalSchemaCollectionRules.initialLoadChannel) &&
        Objects.equals(this.replayChannel, externalSchemaCollectionRules.replayChannel) &&
        Objects.equals(this.initialLoadRelativeURL, externalSchemaCollectionRules.initialLoadRelativeURL) &&
        Objects.equals(this.partialLoadRelativeURL, externalSchemaCollectionRules.partialLoadRelativeURL);
  }

  @Override
  public int hashCode() {
    return Objects.hash(storeType, ongoingChannel, initialLoadChannel, replayChannel, initialLoadRelativeURL, partialLoadRelativeURL);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalSchemaCollectionRulesDTO {\n");
    
    sb.append("    storeType: ").append(toIndentedString(storeType)).append("\n");
    sb.append("    ongoingChannel: ").append(toIndentedString(ongoingChannel)).append("\n");
    sb.append("    initialLoadChannel: ").append(toIndentedString(initialLoadChannel)).append("\n");
    sb.append("    replayChannel: ").append(toIndentedString(replayChannel)).append("\n");
    sb.append("    initialLoadRelativeURL: ").append(toIndentedString(initialLoadRelativeURL)).append("\n");
    sb.append("    partialLoadRelativeURL: ").append(toIndentedString(partialLoadRelativeURL)).append("\n");
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

