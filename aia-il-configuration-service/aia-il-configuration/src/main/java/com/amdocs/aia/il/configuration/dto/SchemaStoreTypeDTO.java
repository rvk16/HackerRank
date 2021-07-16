package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets SchemaStoreType
 */
public enum SchemaStoreTypeDTO {
  
  PHYSICALSCHEMA("PHYSICALSCHEMA"),
  
  DATACHANNELSCHEMA("DATACHANNELSCHEMA"),
  
  PUBLISHERSCHEMA("PUBLISHERSCHEMA"),
  
  PUBLISHERCACHESCHEMA("PUBLISHERCACHESCHEMA");

  private String value;

  SchemaStoreTypeDTO(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static SchemaStoreTypeDTO fromValue(String text) {
    for (SchemaStoreTypeDTO b : SchemaStoreTypeDTO.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

