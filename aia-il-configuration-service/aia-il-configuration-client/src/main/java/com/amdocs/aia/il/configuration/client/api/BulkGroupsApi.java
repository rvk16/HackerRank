package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.BulkGroupDTO;
import com.amdocs.aia.il.configuration.client.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.client.dto.SetFiltersRequestDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface BulkGroupsApi extends ApiClient.Api {


  /**
   * Add a new Bulk Group
   * 
    * @param projectKey The project key (required)
    * @param schemaStoreKey Schema Store Key (required)
    * @param bulkGroup The new Bulk group (required)
   * @return BulkGroupDTO
   */
  @RequestLine("POST /projects/{projectKey}/schemas/{schemaStoreKey}/bulk-groups")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  BulkGroupDTO addBulkGroup(@Param("projectKey") String projectKey, @Param("schemaStoreKey") String schemaStoreKey, BulkGroupDTO bulkGroup);

  /**
   * Add  new Bulk Groups
   * 
    * @param projectKey The project key (required)
    * @param bulkGroups The new Bulk groups (required)
   * @return SaveElementsResponseDTO
   */
  @RequestLine("POST /projects/{projectKey}/bulk-groups")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  SaveElementsResponseDTO addBulkGroups(@Param("projectKey") String projectKey, List<BulkGroupDTO> bulkGroups);

  /**
   * Delete a BulkGroup
   * 
    * @param projectKey The project key (required)
    * @param schemaStoreKey Schema Store Key (required)
    * @param bulkGroupKey Bulk Group Key (required)
   */
  @RequestLine("DELETE /projects/{projectKey}/schemas/{schemaStoreKey}/bulk-groups/{bulkGroupKey}")
  @Headers({
    "Accept: application/json",
  })
  void deleteBulkGroup(@Param("projectKey") String projectKey, @Param("schemaStoreKey") String schemaStoreKey, @Param("bulkGroupKey") String bulkGroupKey);

  /**
   * Get a bulk group
   * 
    * @param projectKey The project key (required)
    * @param schemaStoreKey Schema Store Key (required)
    * @param bulkGroupKey Bulk Group Key (required)
   * @return BulkGroupDTO
   */
  @RequestLine("GET /projects/{projectKey}/schemas/{schemaStoreKey}/bulk-groups/{bulkGroupKey}")
  @Headers({
    "Accept: application/json",
  })
  BulkGroupDTO getBulkGroupByKey(@Param("projectKey") String projectKey, @Param("schemaStoreKey") String schemaStoreKey, @Param("bulkGroupKey") String bulkGroupKey);

  /**
   * Get all bulk groups
   * 
    * @param projectKey The project key (required)
    * @param schemaStoreKey Schema Store Key (required)
   * @return Object
   */
  @RequestLine("GET /projects/{projectKey}/schemas/{schemaStoreKey}/bulk-groups")
  @Headers({
    "Accept: application/json",
  })
  Object getBulkGroups(@Param("projectKey") String projectKey, @Param("schemaStoreKey") String schemaStoreKey);

  /**
   * Update an existing bulk group
   * 
    * @param projectKey The project key (required)
    * @param schemaStoreKey Schema Store Key (required)
    * @param bulkGroupKey Bulk Group Key (required)
    * @param bulkGroup Bulk group to be updated (required)
   * @return BulkGroupDTO
   */
  @RequestLine("PUT /projects/{projectKey}/schemas/{schemaStoreKey}/bulk-groups/{bulkGroupKey}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  BulkGroupDTO updateBulkGroup(@Param("projectKey") String projectKey, @Param("schemaStoreKey") String schemaStoreKey, @Param("bulkGroupKey") String bulkGroupKey, BulkGroupDTO bulkGroup);

  /**
   * Update an existing bulk group with entity filters
   * 
    * @param projectKey The project key (required)
    * @param schemaStoreKey Schema Store Key (required)
    * @param bulkGroupKey Bulk Group Key (required)
    * @param setFiltersRequest Request containing all entities with filters and the bulk group with list of filter references (required)
   * @return SetFiltersRequestDTO
   */
  @RequestLine("PUT /projects/{projectKey}/schemas/{schemaStoreKey}/bulk-groups/{bulkGroupKey}/set-entity-filters")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  SetFiltersRequestDTO updateBulkGroupEntityFilters(@Param("projectKey") String projectKey, @Param("schemaStoreKey") String schemaStoreKey, @Param("bulkGroupKey") String bulkGroupKey, SetFiltersRequestDTO setFiltersRequest);
}
