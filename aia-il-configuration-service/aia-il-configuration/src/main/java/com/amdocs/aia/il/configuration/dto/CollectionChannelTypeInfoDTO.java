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
 * Information of a collection channel that is used by data collectors
 */
@ApiModel(description = "Information of a collection channel that is used by data collectors")
@Validated


public class CollectionChannelTypeInfoDTO   {
  @JsonProperty("channelType")
  private String channelType = null;

  @JsonProperty("displayName")
  private String displayName = null;

  public CollectionChannelTypeInfoDTO channelType(String channelType) {
    this.channelType = channelType;
    return this;
  }

  /**
   * Get channelType
   * @return channelType
  **/
  @ApiModelProperty(value = "")


  public String getChannelType() {
    return channelType;
  }

  public void setChannelType(String channelType) {
    this.channelType = channelType;
  }

  public CollectionChannelTypeInfoDTO displayName(String displayName) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollectionChannelTypeInfoDTO collectionChannelTypeInfo = (CollectionChannelTypeInfoDTO) o;
    return Objects.equals(this.channelType, collectionChannelTypeInfo.channelType) &&
        Objects.equals(this.displayName, collectionChannelTypeInfo.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channelType, displayName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CollectionChannelTypeInfoDTO {\n");
    
    sb.append("    channelType: ").append(toIndentedString(channelType)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
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

