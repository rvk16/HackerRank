package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ExternalEntityCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityFilterDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityIncrementalAttributeDTO;
import com.amdocs.aia.il.configuration.dto.InvalidFilenameActionTypeDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ExternalCsvEntityCollectionRulesDTO
 */
@Validated


public class ExternalCsvEntityCollectionRulesDTO extends ExternalEntityCollectionRulesDTO  {
  @JsonProperty("invalidFilenameAction")
  private InvalidFilenameActionTypeDTO invalidFilenameAction = null;

  public ExternalCsvEntityCollectionRulesDTO invalidFilenameAction(InvalidFilenameActionTypeDTO invalidFilenameAction) {
    this.invalidFilenameAction = invalidFilenameAction;
    return this;
  }

  /**
   * file name mismatch behaviour
   * @return invalidFilenameAction
  **/
  @ApiModelProperty(value = "file name mismatch behaviour")

  @Valid

  public InvalidFilenameActionTypeDTO getInvalidFilenameAction() {
    return invalidFilenameAction;
  }

  public void setInvalidFilenameAction(InvalidFilenameActionTypeDTO invalidFilenameAction) {
    this.invalidFilenameAction = invalidFilenameAction;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalCsvEntityCollectionRulesDTO externalCsvEntityCollectionRules = (ExternalCsvEntityCollectionRulesDTO) o;
    return Objects.equals(this.invalidFilenameAction, externalCsvEntityCollectionRules.invalidFilenameAction) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(invalidFilenameAction, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalCsvEntityCollectionRulesDTO {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    invalidFilenameAction: ").append(toIndentedString(invalidFilenameAction)).append("\n");
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

