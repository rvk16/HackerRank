package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.client.dto.TransformationDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface TransformationApi extends ApiClient.Api {


  /**
   * Add new Transformation
   * 
    * @param projectKey The project key (required)
    * @param transformation Transformation to be created (required)
   * @return TransformationDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/transformations")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  TransformationDTO addTransformation(@Param("projectKey") String projectKey, TransformationDTO transformation);

  /**
   * Add new Transformations
   * 
    * @param projectKey The project key (required)
    * @param transformations Transformations to be created (required)
   * @return SaveElementsResponseDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/transformations-bulk")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  SaveElementsResponseDTO addTransformations(@Param("projectKey") String projectKey, List<TransformationDTO> transformations);

  /**
   * Delete a Transformation
   * 
    * @param id context to be fetched (required)
    * @param projectKey The project key (required)
   */
  @RequestLine("DELETE /projects/{projectKey}/configuration/transformations/{id}")
  @Headers({
    "Accept: application/json",
  })
  void deleteTransformation(@Param("id") String id, @Param("projectKey") String projectKey);

  /**
   * Get a Transformation
   * 
    * @param projectKey The project key (required)
    * @param id context to be fetched (required)
   * @return TransformationDTO
   */
  @RequestLine("GET /projects/{projectKey}/configuration/transformations/{id}")
  @Headers({
    "Accept: application/json",
  })
  TransformationDTO getTransformation(@Param("projectKey") String projectKey, @Param("id") String id);

  /**
   * List all entitiesTransformations
   * 
    * @param projectKey The project key (required)
   * @return Object
   */
  @RequestLine("GET /projects/{projectKey}/configuration/transformations")
  @Headers({
    "Accept: application/json",
  })
  Object listTransformations(@Param("projectKey") String projectKey);

  /**
   * Update an existing transformation
   * 
    * @param projectKey The project key (required)
    * @param id context to be fetched (required)
    * @param transformation Transformation to be updated (required)
   * @return TransformationDTO
   */
  @RequestLine("PUT /projects/{projectKey}/configuration/transformations/{id}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  TransformationDTO updateTransformation(@Param("projectKey") String projectKey, @Param("id") String id, TransformationDTO transformation);
}
