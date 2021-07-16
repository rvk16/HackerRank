package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaStoreInfoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ExternalSqlSchemaStoreInfoDTO
 */
@Validated


public class ExternalSqlSchemaStoreInfoDTO extends ExternalSchemaStoreInfoDTO  {
  @JsonProperty("databaseType")
  private String databaseType = null;

  public ExternalSqlSchemaStoreInfoDTO databaseType(String databaseType) {
    this.databaseType = databaseType;
    return this;
  }

  /**
   * The underlying db technology of the SQL store (e.g. Oracle, PostgreSQL)
   * @return databaseType
  **/
  @ApiModelProperty(value = "The underlying db technology of the SQL store (e.g. Oracle, PostgreSQL)")


  public String getDatabaseType() {
    return databaseType;
  }

  public void setDatabaseType(String databaseType) {
    this.databaseType = databaseType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalSqlSchemaStoreInfoDTO externalSqlSchemaStoreInfo = (ExternalSqlSchemaStoreInfoDTO) o;
    return Objects.equals(this.databaseType, externalSqlSchemaStoreInfo.databaseType) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(databaseType, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalSqlSchemaStoreInfoDTO {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    databaseType: ").append(toIndentedString(databaseType)).append("\n");
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

