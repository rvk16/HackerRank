package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.CollectionChannelTypeInfoDTO;
import com.amdocs.aia.il.configuration.client.dto.ExternalSchemaDTO;
import com.amdocs.aia.il.configuration.client.dto.ExternalSchemaTypeInfoDTO;
import com.amdocs.aia.il.configuration.client.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.client.dto.TypeSystemInfoDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface ExternalSchemaApi extends ApiClient.Api {


  /**
   * Add a new External Schema
   * 
    * @param projectKey The project key (required)
    * @param externalSchema The new External Schema (required)
   * @return ExternalSchemaDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/external-schemas")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  ExternalSchemaDTO addExternalSchema(@Param("projectKey") String projectKey, ExternalSchemaDTO externalSchema);

  /**
   * Add new External Schemas
   * 
    * @param projectKey The project key (required)
    * @param externalSchemas The new External Schemas (required)
   * @return SaveElementsResponseDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/external-schemas/schemas-bulk")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  SaveElementsResponseDTO addExternalSchemas(@Param("projectKey") String projectKey, List<ExternalSchemaDTO> externalSchemas);

  /**
   * delete an External Schema
   * 
    * @param projectKey The project key (required)
    * @param externalSchemaKey External Schema Key (required)
   */
  @RequestLine("DELETE /projects/{projectKey}/configuration/external-schemas/{externalSchemaKey}")
  @Headers({
    "Accept: application/json",
  })
  void deleteExternalSchemaByKey(@Param("projectKey") String projectKey, @Param("externalSchemaKey") String externalSchemaKey);

  /**
   * Get list of supported collection channel types
   * 
    * @param projectKey The project key (required)
   * @return List&lt;CollectionChannelTypeInfoDTO&gt;
   */
  @RequestLine("GET /projects/{projectKey}/configuration/collection-channel-types")
  @Headers({
    "Accept: application/json",
  })
  List<CollectionChannelTypeInfoDTO> getCollectionChannelTypes(@Param("projectKey") String projectKey);

  /**
   * Get an External Schema
   * 
    * @param projectKey The project key (required)
    * @param externalSchemaKey External Schema Key (required)
   * @return ExternalSchemaDTO
   */
  @RequestLine("GET /projects/{projectKey}/configuration/external-schemas/{externalSchemaKey}")
  @Headers({
    "Accept: application/json",
  })
  ExternalSchemaDTO getExternalSchemaByKey(@Param("projectKey") String projectKey, @Param("externalSchemaKey") String externalSchemaKey);

  /**
   * Get information about a specific external schema type
   * 
    * @param projectKey The project key (required)
    * @param externalSchemaType External Schema Type (required)
   * @return ExternalSchemaTypeInfoDTO
   */
  @RequestLine("GET /projects/{projectKey}/configuration/external-schema-types/{externalSchemaType}")
  @Headers({
    "Accept: application/json",
  })
  ExternalSchemaTypeInfoDTO getExternalSchemaType(@Param("projectKey") String projectKey, @Param("externalSchemaType") String externalSchemaType);

  /**
   * Get list of supported external schema types
   * 
    * @param projectKey The project key (required)
   * @return List&lt;ExternalSchemaTypeInfoDTO&gt;
   */
  @RequestLine("GET /projects/{projectKey}/configuration/external-schema-types")
  @Headers({
    "Accept: application/json",
  })
  List<ExternalSchemaTypeInfoDTO> getExternalSchemaTypes(@Param("projectKey") String projectKey);

  /**
   * Get all external schemas
   * 
    * @param projectKey The project key (required)
   * @return List&lt;ExternalSchemaDTO&gt;
   */
  @RequestLine("GET /projects/{projectKey}/configuration/external-schemas")
  @Headers({
    "Accept: application/json",
  })
  List<ExternalSchemaDTO> getExternalSchemas(@Param("projectKey") String projectKey);

  /**
   * Get list of supported type systems
   * 
    * @param projectKey The project key (required)
   * @return List&lt;TypeSystemInfoDTO&gt;
   */
  @RequestLine("GET /projects/{projectKey}/configuration/type-systems")
  @Headers({
    "Accept: application/json",
  })
  List<TypeSystemInfoDTO> getTypeSystems(@Param("projectKey") String projectKey);

  /**
   * Update External Schema
   * 
    * @param projectKey The project key (required)
    * @param externalSchemaKey External Schema Key (required)
    * @param externalSchema The Updated External Schema (required)
   * @return ExternalSchemaDTO
   */
  @RequestLine("PUT /projects/{projectKey}/configuration/external-schemas/{externalSchemaKey}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  ExternalSchemaDTO updateExternalSchema(@Param("projectKey") String projectKey, @Param("externalSchemaKey") String externalSchemaKey, ExternalSchemaDTO externalSchema);
}
