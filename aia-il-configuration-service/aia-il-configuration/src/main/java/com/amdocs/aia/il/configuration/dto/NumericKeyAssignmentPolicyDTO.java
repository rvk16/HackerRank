package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets NumericKeyAssignmentPolicy
 */
public enum NumericKeyAssignmentPolicyDTO {
  
  MANUAL("MANUAL"),
  
  AUTOMATIC("AUTOMATIC");

  private String value;

  NumericKeyAssignmentPolicyDTO(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static NumericKeyAssignmentPolicyDTO fromValue(String text) {
    for (NumericKeyAssignmentPolicyDTO b : NumericKeyAssignmentPolicyDTO.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

