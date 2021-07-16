/*
 * Integration Layer Configuration API
 * This is a REST API specification for Integration Layer Configuration application.
 *
 * OpenAPI spec version: TRUNK-SNAPSHOT
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.amdocs.aia.il.configuration.client.dto;

import java.util.Objects;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static StoreCategoryDTO fromValue(String value) {
    for (StoreCategoryDTO b : StoreCategoryDTO.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    return null;
  }
}

