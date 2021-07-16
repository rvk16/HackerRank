package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.CacheReferenceEntityDTO;
import com.amdocs.aia.il.configuration.client.dto.SaveElementsResponseDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface CacheReferenceApi extends ApiClient.Api {


  /**
   * Add  new Cache Reference Entities
   * 
    * @param projectKey The project key (required)
    * @param cacheReferenceEntities The new Cache Reference Entity (required)
   * @return SaveElementsResponseDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/cache-reference-bulk")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  SaveElementsResponseDTO addCacheReferenceEntities(@Param("projectKey") String projectKey, List<CacheReferenceEntityDTO> cacheReferenceEntities);

  /**
   * Add a new Cache Reference Entity
   * 
    * @param projectKey The project key (required)
    * @param cacheReferenceEntity The new Cache Reference Entity (required)
   * @return CacheReferenceEntityDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/cache-reference")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  CacheReferenceEntityDTO addCacheReferenceEntity(@Param("projectKey") String projectKey, CacheReferenceEntityDTO cacheReferenceEntity);

  /**
   * Delete a Cache Reference Entity
   * 
    * @param projectKey The project key (required)
    * @param cacheReferenceEntityKey Cache Reference Entity Key (required)
   */
  @RequestLine("DELETE /projects/{projectKey}/configuration/cache-reference/{cacheReferenceEntityKey}")
  @Headers({
    "Accept: application/json",
  })
  void deleteCacheReferenceEntity(@Param("projectKey") String projectKey, @Param("cacheReferenceEntityKey") String cacheReferenceEntityKey);

  /**
   * Get all cache reference entities
   * 
    * @param projectKey The project key (required)
   * @return List&lt;CacheReferenceEntityDTO&gt;
   */
  @RequestLine("GET /projects/{projectKey}/configuration/cache-reference")
  @Headers({
    "Accept: application/json",
  })
  List<CacheReferenceEntityDTO> getCacheReferenceEntities(@Param("projectKey") String projectKey);

  /**
   * Get an Cache Reference Entity
   * 
    * @param projectKey The project key (required)
    * @param cacheReferenceEntityKey Cache Reference Entity Key (required)
   * @return CacheReferenceEntityDTO
   */
  @RequestLine("GET /projects/{projectKey}/configuration/cache-reference/{cacheReferenceEntityKey}")
  @Headers({
    "Accept: application/json",
  })
  CacheReferenceEntityDTO getCacheReferenceEntityByKey(@Param("projectKey") String projectKey, @Param("cacheReferenceEntityKey") String cacheReferenceEntityKey);

  /**
   * Update a Cache Reference Entity
   * 
    * @param projectKey The project key (required)
    * @param cacheReferenceEntityKey Cache Reference Entity Key (required)
    * @param cacheReferenceEntity Cache Reference Entity (required)
   * @return CacheReferenceEntityDTO
   */
  @RequestLine("PUT /projects/{projectKey}/configuration/cache-reference/{cacheReferenceEntityKey}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  CacheReferenceEntityDTO updateCacheReferenceEntity(@Param("projectKey") String projectKey, @Param("cacheReferenceEntityKey") String cacheReferenceEntityKey, CacheReferenceEntityDTO cacheReferenceEntity);
}
