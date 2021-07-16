package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.ExternalEntityDTO;
import com.amdocs.aia.il.configuration.client.dto.SaveElementsResponseDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface ExternalEntityApi extends ApiClient.Api {


  /**
   * Add new External Entities
   * 
    * @param projectKey The project key (required)
    * @param externalEntities New External Entities (required)
   * @return SaveElementsResponseDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/external-schemas/entities-bulk")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  SaveElementsResponseDTO addExternalEntities(@Param("projectKey") String projectKey, List<ExternalEntityDTO> externalEntities);

  /**
   * Add a new External Entity
   * 
    * @param projectKey The project key (required)
    * @param externalSchemaKey External Schema Key (required)
    * @param externalEntity The new External Entity (required)
   * @return ExternalEntityDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/external-schemas/{externalSchemaKey}/entities")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  ExternalEntityDTO addExternalEntity(@Param("projectKey") String projectKey, @Param("externalSchemaKey") String externalSchemaKey, ExternalEntityDTO externalEntity);

  /**
   * delete an External Entity
   * 
    * @param projectKey The project key (required)
    * @param externalSchemaKey External Schema Key (required)
    * @param externalEntityKey External Entity Key (required)
   */
  @RequestLine("DELETE /projects/{projectKey}/configuration/external-schemas/{externalSchemaKey}/entities/{externalEntityKey}")
  @Headers({
    "Accept: application/json",
  })
  void deleteExternalEntityByKey(@Param("projectKey") String projectKey, @Param("externalSchemaKey") String externalSchemaKey, @Param("externalEntityKey") String externalEntityKey);

  /**
   * Get all external entities
   * 
    * @param projectKey The project key (required)
    * @param externalSchemaKey External Schema Key (required)
   * @return List&lt;ExternalEntityDTO&gt;
   */
  @RequestLine("GET /projects/{projectKey}/configuration/external-schemas/{externalSchemaKey}/entities")
  @Headers({
    "Accept: application/json",
  })
  List<ExternalEntityDTO> getExternalEntities(@Param("projectKey") String projectKey, @Param("externalSchemaKey") String externalSchemaKey);

  /**
   * Get an External Entity
   * 
    * @param projectKey The project key (required)
    * @param externalSchemaKey External Schema Key (required)
    * @param externalEntityKey External Entity Key (required)
   * @return ExternalEntityDTO
   */
  @RequestLine("GET /projects/{projectKey}/configuration/external-schemas/{externalSchemaKey}/entities/{externalEntityKey}")
  @Headers({
    "Accept: application/json",
  })
  ExternalEntityDTO getExternalEntityByKey(@Param("projectKey") String projectKey, @Param("externalSchemaKey") String externalSchemaKey, @Param("externalEntityKey") String externalEntityKey);

  /**
   * Update External Entity
   * 
    * @param projectKey The project key (required)
    * @param externalSchemaKey External Schema Key (required)
    * @param externalEntityKey External Entity Key (required)
    * @param externalEntity The updated External Entity (required)
   * @return ExternalEntityDTO
   */
  @RequestLine("PUT /projects/{projectKey}/configuration/external-schemas/{externalSchemaKey}/entities/{externalEntityKey}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  ExternalEntityDTO updateExternalEntity(@Param("projectKey") String projectKey, @Param("externalSchemaKey") String externalSchemaKey, @Param("externalEntityKey") String externalEntityKey, ExternalEntityDTO externalEntity);
}
