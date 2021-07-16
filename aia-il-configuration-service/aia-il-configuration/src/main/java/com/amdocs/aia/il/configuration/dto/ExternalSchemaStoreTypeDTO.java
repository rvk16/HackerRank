package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets ExternalSchemaStoreType
 */
public enum ExternalSchemaStoreTypeDTO {
  
  CSV("CSV"),
  
  SQL("SQL"),
  
  KAFKA("KAFKA");

  private String value;

  ExternalSchemaStoreTypeDTO(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ExternalSchemaStoreTypeDTO fromValue(String text) {
    for (ExternalSchemaStoreTypeDTO b : ExternalSchemaStoreTypeDTO.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

