package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.EntityReferentialIntegrityDTO;
import com.amdocs.aia.il.configuration.client.dto.SaveElementsResponseDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface EntityReferentialIntegrityApi extends ApiClient.Api {


  /**
   * Add new Entities Referential Integrity
   * 
    * @param projectKey The project key (required)
    * @param entitiesReferentialIntegrity The new EntitiesReferentialIntegrity (required)
   * @return SaveElementsResponseDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/referential-integrity-bulk")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  SaveElementsResponseDTO addEntitiesReferentialIntegrity(@Param("projectKey") String projectKey, List<EntityReferentialIntegrityDTO> entitiesReferentialIntegrity);

  /**
   * Add a new Entity Referential Integrity
   * 
    * @param projectKey The project key (required)
    * @param entityReferentialIntegrity The new EntityReferentialIntegrity (required)
   * @return EntityReferentialIntegrityDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/referential-integrity")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  EntityReferentialIntegrityDTO addEntityReferentialIntegrity(@Param("projectKey") String projectKey, EntityReferentialIntegrityDTO entityReferentialIntegrity);
}
