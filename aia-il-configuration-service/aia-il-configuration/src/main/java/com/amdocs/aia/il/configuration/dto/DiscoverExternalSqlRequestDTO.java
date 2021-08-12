package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.AvailabilityDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DiscoverExternalSqlRequestDTO
 */
@Validated


public class DiscoverExternalSqlRequestDTO   {
  @JsonProperty("schemaName")
  private String schemaName = null;

  @JsonProperty("connectionString")
  private String connectionString = null;

  @JsonProperty("dbUser")
  private String dbUser = null;

  @JsonProperty("dbPassword")
  private String dbPassword = null;

  @JsonProperty("dbType")
  private String dbType = null;

  @JsonProperty("availability")
  private AvailabilityDTO availability = null;

  @JsonProperty("subjectAreaName")
  private String subjectAreaName = null;

  @JsonProperty("subjectAreaKey")
  private String subjectAreaKey = null;

  public DiscoverExternalSqlRequestDTO schemaName(String schemaName) {
    this.schemaName = schemaName;
    return this;
  }

  /**
   * The schemaName of the discover External Sql Schema
   * @return schemaName
  **/
  @ApiModelProperty(value = "The schemaName of the discover External Sql Schema")


  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public DiscoverExternalSqlRequestDTO connectionString(String connectionString) {
    this.connectionString = connectionString;
    return this;
  }

  /**
   * The jdbc connection string
   * @return connectionString
  **/
  @ApiModelProperty(value = "The jdbc connection string")


  public String getConnectionString() {
    return connectionString;
  }

  public void setConnectionString(String connectionString) {
    this.connectionString = connectionString;
  }

  public DiscoverExternalSqlRequestDTO dbUser(String dbUser) {
    this.dbUser = dbUser;
    return this;
  }

  /**
   * The jdbc user name
   * @return dbUser
  **/
  @ApiModelProperty(value = "The jdbc user name")


  public String getDbUser() {
    return dbUser;
  }

  public void setDbUser(String dbUser) {
    this.dbUser = dbUser;
  }

  public DiscoverExternalSqlRequestDTO dbPassword(String dbPassword) {
    this.dbPassword = dbPassword;
    return this;
  }

  /**
   * The jdbc user password
   * @return dbPassword
  **/
  @ApiModelProperty(value = "The jdbc user password")


  public String getDbPassword() {
    return dbPassword;
  }

  public void setDbPassword(String dbPassword) {
    this.dbPassword = dbPassword;
  }

  public DiscoverExternalSqlRequestDTO dbType(String dbType) {
    this.dbType = dbType;
    return this;
  }

  /**
   * A sql types,valid types are ORACLE, POSTGRESQL
   * @return dbType
  **/
  @ApiModelProperty(value = "A sql types,valid types are ORACLE, POSTGRESQL")


  public String getDbType() {
    return dbType;
  }

  public void setDbType(String dbType) {
    this.dbType = dbType;
  }

  public DiscoverExternalSqlRequestDTO availability(AvailabilityDTO availability) {
    this.availability = availability;
    return this;
  }

  /**
   * Get availability
   * @return availability
  **/
  @ApiModelProperty(value = "")

  @Valid

  public AvailabilityDTO getAvailability() {
    return availability;
  }

  public void setAvailability(AvailabilityDTO availability) {
    this.availability = availability;
  }

  public DiscoverExternalSqlRequestDTO subjectAreaName(String subjectAreaName) {
    this.subjectAreaName = subjectAreaName;
    return this;
  }

  /**
   * The external schema's subjectArea name
   * @return subjectAreaName
  **/
  @ApiModelProperty(value = "The external schema's subjectArea name")


  public String getSubjectAreaName() {
    return subjectAreaName;
  }

  public void setSubjectAreaName(String subjectAreaName) {
    this.subjectAreaName = subjectAreaName;
  }

  public DiscoverExternalSqlRequestDTO subjectAreaKey(String subjectAreaKey) {
    this.subjectAreaKey = subjectAreaKey;
    return this;
  }

  /**
   * The external schema's subjectArea key
   * @return subjectAreaKey
  **/
  @ApiModelProperty(value = "The external schema's subjectArea key")


  public String getSubjectAreaKey() {
    return subjectAreaKey;
  }

  public void setSubjectAreaKey(String subjectAreaKey) {
    this.subjectAreaKey = subjectAreaKey;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DiscoverExternalSqlRequestDTO discoverExternalSqlRequest = (DiscoverExternalSqlRequestDTO) o;
    return Objects.equals(this.schemaName, discoverExternalSqlRequest.schemaName) &&
        Objects.equals(this.connectionString, discoverExternalSqlRequest.connectionString) &&
        Objects.equals(this.dbUser, discoverExternalSqlRequest.dbUser) &&
        Objects.equals(this.dbPassword, discoverExternalSqlRequest.dbPassword) &&
        Objects.equals(this.dbType, discoverExternalSqlRequest.dbType) &&
        Objects.equals(this.availability, discoverExternalSqlRequest.availability) &&
        Objects.equals(this.subjectAreaName, discoverExternalSqlRequest.subjectAreaName) &&
        Objects.equals(this.subjectAreaKey, discoverExternalSqlRequest.subjectAreaKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemaName, connectionString, dbUser, dbPassword, dbType, availability, subjectAreaName, subjectAreaKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DiscoverExternalSqlRequestDTO {\n");
    
    sb.append("    schemaName: ").append(toIndentedString(schemaName)).append("\n");
    sb.append("    connectionString: ").append(toIndentedString(connectionString)).append("\n");
    sb.append("    dbUser: ").append(toIndentedString(dbUser)).append("\n");
    sb.append("    dbPassword: ").append(toIndentedString(dbPassword)).append("\n");
    sb.append("    dbType: ").append(toIndentedString(dbType)).append("\n");
    sb.append("    availability: ").append(toIndentedString(availability)).append("\n");
    sb.append("    subjectAreaName: ").append(toIndentedString(subjectAreaName)).append("\n");
    sb.append("    subjectAreaKey: ").append(toIndentedString(subjectAreaKey)).append("\n");
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

