package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets EntityStoreType
 */
public enum EntityStoreTypeDTO {
  
  PUBLISHERCACHEENTITY("PUBLISHERCACHEENTITY");

  private String value;

  EntityStoreTypeDTO(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static EntityStoreTypeDTO fromValue(String text) {
    for (EntityStoreTypeDTO b : EntityStoreTypeDTO.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

