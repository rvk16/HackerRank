package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets EntityTransformationType
 */
public enum EntityTransformationTypeDTO {
  
  SHARED("SHARED"),
  
  CACHED("CACHED");

  private String value;

  EntityTransformationTypeDTO(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static EntityTransformationTypeDTO fromValue(String text) {
    for (EntityTransformationTypeDTO b : EntityTransformationTypeDTO.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

