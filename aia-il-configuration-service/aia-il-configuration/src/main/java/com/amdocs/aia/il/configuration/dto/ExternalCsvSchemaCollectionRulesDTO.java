package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.InvalidFilenameActionTypeDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ExternalCsvSchemaCollectionRulesDTO
 */
@Validated


public class ExternalCsvSchemaCollectionRulesDTO extends ExternalSchemaCollectionRulesDTO  {
  @JsonProperty("defaultInvalidFilenameAction")
  private InvalidFilenameActionTypeDTO defaultInvalidFilenameAction = null;

  public ExternalCsvSchemaCollectionRulesDTO defaultInvalidFilenameAction(InvalidFilenameActionTypeDTO defaultInvalidFilenameAction) {
    this.defaultInvalidFilenameAction = defaultInvalidFilenameAction;
    return this;
  }

  /**
   * Default file name mismatch behaviour
   * @return defaultInvalidFilenameAction
  **/
  @ApiModelProperty(value = "Default file name mismatch behaviour")

  @Valid

  public InvalidFilenameActionTypeDTO getDefaultInvalidFilenameAction() {
    return defaultInvalidFilenameAction;
  }

  public void setDefaultInvalidFilenameAction(InvalidFilenameActionTypeDTO defaultInvalidFilenameAction) {
    this.defaultInvalidFilenameAction = defaultInvalidFilenameAction;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalCsvSchemaCollectionRulesDTO externalCsvSchemaCollectionRules = (ExternalCsvSchemaCollectionRulesDTO) o;
    return Objects.equals(this.defaultInvalidFilenameAction, externalCsvSchemaCollectionRules.defaultInvalidFilenameAction) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(defaultInvalidFilenameAction, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalCsvSchemaCollectionRulesDTO {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    defaultInvalidFilenameAction: ").append(toIndentedString(defaultInvalidFilenameAction)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

