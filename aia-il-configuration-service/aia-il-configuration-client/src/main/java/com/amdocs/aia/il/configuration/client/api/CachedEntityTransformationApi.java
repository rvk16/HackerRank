package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.EntityTransformationDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface CachedEntityTransformationApi extends ApiClient.Api {


  /**
   * Add a Cached Entity Transformation
   * 
    * @param projectKey The project key (required)
    * @param entityTransformation Entity Transformation (required)
   * @return EntityTransformationDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/cached-entity-transformation")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  EntityTransformationDTO addCachedEntityTransformation(@Param("projectKey") String projectKey, EntityTransformationDTO entityTransformation);

  /**
   * Delete a Cache Entity Transformation
   * 
    * @param projectKey The project key (required)
    * @param cacheReferenceEntityKey Cache Reference Entity Key (required)
   */
  @RequestLine("DELETE /projects/{projectKey}/configuration/cached-entity-transformation/{cacheReferenceEntityKey}")
  @Headers({
    "Accept: application/json",
  })
  void deleteCacheEntityTransformation(@Param("projectKey") String projectKey, @Param("cacheReferenceEntityKey") String cacheReferenceEntityKey);

  /**
   * Get cache entity transformation
   * 
    * @param projectKey The project key (required)
    * @param cacheReferenceEntityKey Cache Reference Entity Key (required)
   * @return EntityTransformationDTO
   */
  @RequestLine("GET /projects/{projectKey}/configuration/cached-entity-transformation/{cacheReferenceEntityKey}")
  @Headers({
    "Accept: application/json",
  })
  EntityTransformationDTO getCacheEntityTransformation(@Param("projectKey") String projectKey, @Param("cacheReferenceEntityKey") String cacheReferenceEntityKey);

  /**
   * Update a Cached Entity Transformation
   * 
    * @param projectKey The project key (required)
    * @param entityTransformation Entity Transformation (required)
   * @return EntityTransformationDTO
   */
  @RequestLine("PUT /projects/{projectKey}/configuration/cached-entity-transformation")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  EntityTransformationDTO updateCachedEntityTransformation(@Param("projectKey") String projectKey, EntityTransformationDTO entityTransformation);
}
