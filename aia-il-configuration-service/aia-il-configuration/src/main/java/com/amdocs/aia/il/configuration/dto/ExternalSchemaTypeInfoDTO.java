package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.CollectionChannelTypeInfoDTO;
import com.amdocs.aia.il.configuration.dto.TypeSystemInfoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * External Schema Type Information
 */
@ApiModel(description = "External Schema Type Information")
@Validated


public class ExternalSchemaTypeInfoDTO   {
  @JsonProperty("type")
  private String type = null;

  @JsonProperty("displayName")
  private String displayName = null;

  @JsonProperty("supportedTypeSystems")
  @Valid
  private List<TypeSystemInfoDTO> supportedTypeSystems = null;

  @JsonProperty("supportedOngoingChannels")
  @Valid
  private List<CollectionChannelTypeInfoDTO> supportedOngoingChannels = null;

  @JsonProperty("supportedInitialLoadChannels")
  @Valid
  private List<CollectionChannelTypeInfoDTO> supportedInitialLoadChannels = null;

  @JsonProperty("supportedReplayChannels")
  @Valid
  private List<CollectionChannelTypeInfoDTO> supportedReplayChannels = null;

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

  public ExternalSchemaTypeInfoDTO type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  **/
  @ApiModelProperty(value = "")


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ExternalSchemaTypeInfoDTO displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get displayName
   * @return displayName
  **/
  @ApiModelProperty(value = "")


  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public ExternalSchemaTypeInfoDTO supportedTypeSystems(List<TypeSystemInfoDTO> supportedTypeSystems) {
    this.supportedTypeSystems = supportedTypeSystems;
    return this;
  }

  public ExternalSchemaTypeInfoDTO addSupportedTypeSystemsItem(TypeSystemInfoDTO supportedTypeSystemsItem) {
    if (this.supportedTypeSystems == null) {
      this.supportedTypeSystems = new ArrayList<>();
    }
    this.supportedTypeSystems.add(supportedTypeSystemsItem);
    return this;
  }

  /**
   * List of type systems that can be used by external schemas of this type
   * @return supportedTypeSystems
  **/
  @ApiModelProperty(value = "List of type systems that can be used by external schemas of this type")

  @Valid

  public List<TypeSystemInfoDTO> getSupportedTypeSystems() {
    return supportedTypeSystems;
  }

  public void setSupportedTypeSystems(List<TypeSystemInfoDTO> supportedTypeSystems) {
    this.supportedTypeSystems = supportedTypeSystems;
  }

  public ExternalSchemaTypeInfoDTO supportedOngoingChannels(List<CollectionChannelTypeInfoDTO> supportedOngoingChannels) {
    this.supportedOngoingChannels = supportedOngoingChannels;
    return this;
  }

  public ExternalSchemaTypeInfoDTO addSupportedOngoingChannelsItem(CollectionChannelTypeInfoDTO supportedOngoingChannelsItem) {
    if (this.supportedOngoingChannels == null) {
      this.supportedOngoingChannels = new ArrayList<>();
    }
    this.supportedOngoingChannels.add(supportedOngoingChannelsItem);
    return this;
  }

  /**
   * List of ongoing channels that are supported by data collectors for this external schema type
   * @return supportedOngoingChannels
  **/
  @ApiModelProperty(value = "List of ongoing channels that are supported by data collectors for this external schema type")

  @Valid

  public List<CollectionChannelTypeInfoDTO> getSupportedOngoingChannels() {
    return supportedOngoingChannels;
  }

  public void setSupportedOngoingChannels(List<CollectionChannelTypeInfoDTO> supportedOngoingChannels) {
    this.supportedOngoingChannels = supportedOngoingChannels;
  }

  public ExternalSchemaTypeInfoDTO supportedInitialLoadChannels(List<CollectionChannelTypeInfoDTO> supportedInitialLoadChannels) {
    this.supportedInitialLoadChannels = supportedInitialLoadChannels;
    return this;
  }

  public ExternalSchemaTypeInfoDTO addSupportedInitialLoadChannelsItem(CollectionChannelTypeInfoDTO supportedInitialLoadChannelsItem) {
    if (this.supportedInitialLoadChannels == null) {
      this.supportedInitialLoadChannels = new ArrayList<>();
    }
    this.supportedInitialLoadChannels.add(supportedInitialLoadChannelsItem);
    return this;
  }

  /**
   * List of initial-load channels that are supported by data collectors for this external schema type
   * @return supportedInitialLoadChannels
  **/
  @ApiModelProperty(value = "List of initial-load channels that are supported by data collectors for this external schema type")

  @Valid

  public List<CollectionChannelTypeInfoDTO> getSupportedInitialLoadChannels() {
    return supportedInitialLoadChannels;
  }

  public void setSupportedInitialLoadChannels(List<CollectionChannelTypeInfoDTO> supportedInitialLoadChannels) {
    this.supportedInitialLoadChannels = supportedInitialLoadChannels;
  }

  public ExternalSchemaTypeInfoDTO supportedReplayChannels(List<CollectionChannelTypeInfoDTO> supportedReplayChannels) {
    this.supportedReplayChannels = supportedReplayChannels;
    return this;
  }

  public ExternalSchemaTypeInfoDTO addSupportedReplayChannelsItem(CollectionChannelTypeInfoDTO supportedReplayChannelsItem) {
    if (this.supportedReplayChannels == null) {
      this.supportedReplayChannels = new ArrayList<>();
    }
    this.supportedReplayChannels.add(supportedReplayChannelsItem);
    return this;
  }

  /**
   * List of replay channels that are supported by data collectors for this external schema type
   * @return supportedReplayChannels
  **/
  @ApiModelProperty(value = "List of replay channels that are supported by data collectors for this external schema type")

  @Valid

  public List<CollectionChannelTypeInfoDTO> getSupportedReplayChannels() {
    return supportedReplayChannels;
  }

  public void setSupportedReplayChannels(List<CollectionChannelTypeInfoDTO> supportedReplayChannels) {
    this.supportedReplayChannels = supportedReplayChannels;
  }

  public ExternalSchemaTypeInfoDTO storeType(StoreTypeEnum storeType) {
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
    ExternalSchemaTypeInfoDTO externalSchemaTypeInfo = (ExternalSchemaTypeInfoDTO) o;
    return Objects.equals(this.type, externalSchemaTypeInfo.type) &&
        Objects.equals(this.displayName, externalSchemaTypeInfo.displayName) &&
        Objects.equals(this.supportedTypeSystems, externalSchemaTypeInfo.supportedTypeSystems) &&
        Objects.equals(this.supportedOngoingChannels, externalSchemaTypeInfo.supportedOngoingChannels) &&
        Objects.equals(this.supportedInitialLoadChannels, externalSchemaTypeInfo.supportedInitialLoadChannels) &&
        Objects.equals(this.supportedReplayChannels, externalSchemaTypeInfo.supportedReplayChannels) &&
        Objects.equals(this.storeType, externalSchemaTypeInfo.storeType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, displayName, supportedTypeSystems, supportedOngoingChannels, supportedInitialLoadChannels, supportedReplayChannels, storeType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalSchemaTypeInfoDTO {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    supportedTypeSystems: ").append(toIndentedString(supportedTypeSystems)).append("\n");
    sb.append("    supportedOngoingChannels: ").append(toIndentedString(supportedOngoingChannels)).append("\n");
    sb.append("    supportedInitialLoadChannels: ").append(toIndentedString(supportedInitialLoadChannels)).append("\n");
    sb.append("    supportedReplayChannels: ").append(toIndentedString(supportedReplayChannels)).append("\n");
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

