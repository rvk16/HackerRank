package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets ChangeStatus
 */
public enum ChangeStatusDTO {
  
  DRAFT("DRAFT"),
  
  MODIFIED("MODIFIED"),
  
  PUBLISHED("PUBLISHED"),
  
  NOT_EXIST("NOT_EXIST");

  private String value;

  ChangeStatusDTO(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ChangeStatusDTO fromValue(String text) {
    for (ChangeStatusDTO b : ChangeStatusDTO.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

