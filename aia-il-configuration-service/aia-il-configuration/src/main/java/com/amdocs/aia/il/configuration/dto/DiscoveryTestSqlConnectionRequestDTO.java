package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DiscoveryTestSqlConnectionRequestDTO
 */
@Validated


public class DiscoveryTestSqlConnectionRequestDTO   {
  @JsonProperty("connectionString")
  private String connectionString = null;

  @JsonProperty("dbUser")
  private String dbUser = null;

  @JsonProperty("dbPassword")
  private String dbPassword = null;

  @JsonProperty("dbType")
  private String dbType = null;

  public DiscoveryTestSqlConnectionRequestDTO connectionString(String connectionString) {
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

  public DiscoveryTestSqlConnectionRequestDTO dbUser(String dbUser) {
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

  public DiscoveryTestSqlConnectionRequestDTO dbPassword(String dbPassword) {
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

  public DiscoveryTestSqlConnectionRequestDTO dbType(String dbType) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DiscoveryTestSqlConnectionRequestDTO discoveryTestSqlConnectionRequest = (DiscoveryTestSqlConnectionRequestDTO) o;
    return Objects.equals(this.connectionString, discoveryTestSqlConnectionRequest.connectionString) &&
        Objects.equals(this.dbUser, discoveryTestSqlConnectionRequest.dbUser) &&
        Objects.equals(this.dbPassword, discoveryTestSqlConnectionRequest.dbPassword) &&
        Objects.equals(this.dbType, discoveryTestSqlConnectionRequest.dbType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(connectionString, dbUser, dbPassword, dbType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DiscoveryTestSqlConnectionRequestDTO {\n");
    
    sb.append("    connectionString: ").append(toIndentedString(connectionString)).append("\n");
    sb.append("    dbUser: ").append(toIndentedString(dbUser)).append("\n");
    sb.append("    dbPassword: ").append(toIndentedString(dbPassword)).append("\n");
    sb.append("    dbType: ").append(toIndentedString(dbType)).append("\n");
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

