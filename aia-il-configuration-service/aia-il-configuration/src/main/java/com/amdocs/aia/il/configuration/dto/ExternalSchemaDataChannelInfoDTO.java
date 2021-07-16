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
 * additional information regarding the management of the external schema in the system&#39;s data channel which serves as the input for transformations
 */
@ApiModel(description = "additional information regarding the management of the external schema in the system's data channel which serves as the input for transformations")
@Validated


public class ExternalSchemaDataChannelInfoDTO   {
  @JsonProperty("dataChannelName")
  private String dataChannelName = null;

  /**
   * The format in which messages are serialized in the data channel
   */
  public enum SerializationMethodEnum {
    SHAREDPROTOBUF("SharedProtobuf"),
    
    SHAREDJSON("SharedJson");

    private String value;

    SerializationMethodEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static SerializationMethodEnum fromValue(String text) {
      for (SerializationMethodEnum b : SerializationMethodEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("serializationMethod")
  private SerializationMethodEnum serializationMethod = null;

  public ExternalSchemaDataChannelInfoDTO dataChannelName(String dataChannelName) {
    this.dataChannelName = dataChannelName;
    return this;
  }

  /**
   * The PHYSICAL name of the data channel
   * @return dataChannelName
  **/
  @ApiModelProperty(value = "The PHYSICAL name of the data channel")


  public String getDataChannelName() {
    return dataChannelName;
  }

  public void setDataChannelName(String dataChannelName) {
    this.dataChannelName = dataChannelName;
  }

  public ExternalSchemaDataChannelInfoDTO serializationMethod(SerializationMethodEnum serializationMethod) {
    this.serializationMethod = serializationMethod;
    return this;
  }

  /**
   * The format in which messages are serialized in the data channel
   * @return serializationMethod
  **/
  @ApiModelProperty(value = "The format in which messages are serialized in the data channel")


  public SerializationMethodEnum getSerializationMethod() {
    return serializationMethod;
  }

  public void setSerializationMethod(SerializationMethodEnum serializationMethod) {
    this.serializationMethod = serializationMethod;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalSchemaDataChannelInfoDTO externalSchemaDataChannelInfo = (ExternalSchemaDataChannelInfoDTO) o;
    return Objects.equals(this.dataChannelName, externalSchemaDataChannelInfo.dataChannelName) &&
        Objects.equals(this.serializationMethod, externalSchemaDataChannelInfo.serializationMethod);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataChannelName, serializationMethod);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalSchemaDataChannelInfoDTO {\n");
    
    sb.append("    dataChannelName: ").append(toIndentedString(dataChannelName)).append("\n");
    sb.append("    serializationMethod: ").append(toIndentedString(serializationMethod)).append("\n");
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

