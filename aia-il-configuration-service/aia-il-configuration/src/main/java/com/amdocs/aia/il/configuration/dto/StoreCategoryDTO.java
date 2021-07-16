package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets StoreCategory
 */
public enum StoreCategoryDTO {
  
  EXTERNAL("EXTERNAL"),
  
  SHARED("SHARED"),
  
  PRIVATE("PRIVATE");

  private String value;

  StoreCategoryDTO(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static StoreCategoryDTO fromValue(String text) {
    for (StoreCategoryDTO b : StoreCategoryDTO.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

