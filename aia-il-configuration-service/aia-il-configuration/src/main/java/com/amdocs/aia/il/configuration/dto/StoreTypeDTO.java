package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets StoreType
 */
public enum StoreTypeDTO {
  
  PHYSICAL("PHYSICAL"),
  
  DATA_CHANNEL("DATA_CHANNEL"),
  
  PUBLISHER_STORE("PUBLISHER_STORE"),
  
  PUBLISHER_CACHE("PUBLISHER_CACHE");

  private String value;

  StoreTypeDTO(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static StoreTypeDTO fromValue(String text) {
    for (StoreTypeDTO b : StoreTypeDTO.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

