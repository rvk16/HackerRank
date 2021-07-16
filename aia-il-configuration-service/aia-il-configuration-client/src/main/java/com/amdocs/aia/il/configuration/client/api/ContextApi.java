package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.ContextDTO;
import com.amdocs.aia.il.configuration.client.dto.SaveElementsResponseDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface ContextApi extends ApiClient.Api {


  /**
   * Add new context
   * 
    * @param projectKey The project key (required)
    * @param context context to be created (required)
   * @return ContextDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/transformation-contexts")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  ContextDTO addContext(@Param("projectKey") String projectKey, ContextDTO context);

  /**
   * Add new contexts
   * 
    * @param projectKey The project key (required)
    * @param context contexts to be created (required)
   * @return SaveElementsResponseDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/transformation-contexts/contexts-bulk")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  SaveElementsResponseDTO addContexts(@Param("projectKey") String projectKey, List<ContextDTO> context);

  /**
   * Delete a context
   * 
    * @param projectKey The project key (required)
    * @param id context to be fetched (required)
   */
  @RequestLine("DELETE /projects/{projectKey}/configuration/transformation-contexts/{id}")
  @Headers({
    "Accept: application/json",
  })
  void deleteContext(@Param("projectKey") String projectKey, @Param("id") String id);

  /**
   * Get a Context
   * 
    * @param projectKey The project key (required)
    * @param id context to be fetched (required)
   * @return ContextDTO
   */
  @RequestLine("GET /projects/{projectKey}/configuration/transformation-contexts/{id}")
  @Headers({
    "Accept: application/json",
  })
  ContextDTO getContext(@Param("projectKey") String projectKey, @Param("id") String id);

  /**
   * List of relation types
   * 
    * @param projectKey The project key (required)
   * @return Object
   */
  @RequestLine("GET /projects/{projectKey}/configuration/context-relation-types")
  @Headers({
    "Accept: application/json",
  })
  Object getRelationTypesList(@Param("projectKey") String projectKey);

  /**
   * List all contexts
   * 
    * @param projectKey The project key (required)
   * @return Object
   */
  @RequestLine("GET /projects/{projectKey}/configuration/transformation-contexts")
  @Headers({
    "Accept: application/json",
  })
  Object listContexts(@Param("projectKey") String projectKey);

  /**
   * Update an existing context
   * 
    * @param projectKey The project key (required)
    * @param id context to be fetched (required)
    * @param context Context to be updated (required)
   * @return ContextDTO
   */
  @RequestLine("PUT /projects/{projectKey}/configuration/transformation-contexts/{id}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  ContextDTO updateContext(@Param("projectKey") String projectKey, @Param("id") String id, ContextDTO context);
}
