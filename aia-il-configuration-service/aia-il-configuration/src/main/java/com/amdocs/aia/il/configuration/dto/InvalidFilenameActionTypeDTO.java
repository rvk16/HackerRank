package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets InvalidFilenameActionType
 */
public enum InvalidFilenameActionTypeDTO {
  
  KEEP("KEEP"),
  
  MOVE("MOVE");

  private String value;

  InvalidFilenameActionTypeDTO(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static InvalidFilenameActionTypeDTO fromValue(String text) {
    for (InvalidFilenameActionTypeDTO b : InvalidFilenameActionTypeDTO.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

