package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets Availability
 */
public enum AvailabilityDTO {
  
  EXTERNAL("EXTERNAL"),
  
  SHARED("SHARED");

  private String value;

  AvailabilityDTO(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static AvailabilityDTO fromValue(String text) {
    for (AvailabilityDTO b : AvailabilityDTO.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

