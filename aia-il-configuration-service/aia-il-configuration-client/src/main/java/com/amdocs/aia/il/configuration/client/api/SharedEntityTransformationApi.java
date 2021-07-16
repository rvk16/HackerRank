package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.EntityTransformationDTO;
import com.amdocs.aia.il.configuration.client.dto.SharedEntityTransformationGridElementDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface SharedEntityTransformationApi extends ApiClient.Api {


  /**
   * Add a Shared Entity Transformation
   * 
    * @param projectKey The project key (required)
    * @param entityTransformation Entity Transformation (required)
   * @return EntityTransformationDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/shared-entity-transformation")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  EntityTransformationDTO addSharedEntityTransformation(@Param("projectKey") String projectKey, EntityTransformationDTO entityTransformation);

  /**
   * Delete a Shared Entity Transformation
   * 
    * @param projectKey The project key (required)
    * @param logicalEntityKey Logical Entity Key (required)
   */
  @RequestLine("DELETE /projects/{projectKey}/configuration/shared-entity-transformation/{logicalEntityKey}")
  @Headers({
    "Accept: application/json",
  })
  void deleteSharedEntityTransformation(@Param("projectKey") String projectKey, @Param("logicalEntityKey") String logicalEntityKey);

  /**
   * Get shared entity transformation
   * 
    * @param projectKey The project key (required)
    * @param logicalEntityKey Logical Entity Key (required)
   * @return EntityTransformationDTO
   */
  @RequestLine("GET /projects/{projectKey}/configuration/shared-entity-transformation/{logicalEntityKey}")
  @Headers({
    "Accept: application/json",
  })
  EntityTransformationDTO getSharedEntityTransformation(@Param("projectKey") String projectKey, @Param("logicalEntityKey") String logicalEntityKey);

  /**
   * Get a Shared Entity Transformation List
   * 
    * @param projectKey The project key (required)
   * @return List&lt;SharedEntityTransformationGridElementDTO&gt;
   */
  @RequestLine("GET /projects/{projectKey}/configuration/shared-entity-transformation")
  @Headers({
    "Accept: application/json",
  })
  List<SharedEntityTransformationGridElementDTO> getSharedEntityTransformationList(@Param("projectKey") String projectKey);

  /**
   * Update a Shared Entity Transformation
   * 
    * @param projectKey The project key (required)
    * @param entityTransformation Entity Transformation (required)
   * @return EntityTransformationDTO
   */
  @RequestLine("PUT /projects/{projectKey}/configuration/shared-entity-transformation")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  EntityTransformationDTO updateSharedEntityTransformation(@Param("projectKey") String projectKey, EntityTransformationDTO entityTransformation);
}
