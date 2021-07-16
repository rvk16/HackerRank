package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets SourceTargetType
 */
public enum SourceTargetTypeDTO {
  
  SOURCE("SOURCE"),
  
  TARGET("TARGET");

  private String value;

  SourceTargetTypeDTO(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static SourceTargetTypeDTO fromValue(String text) {
    for (SourceTargetTypeDTO b : SourceTargetTypeDTO.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

