package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.BaseEntityDTO;
import com.amdocs.aia.il.configuration.client.dto.ContextSourceDTO;
import com.amdocs.aia.il.configuration.client.dto.EntitiesDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface ContextSourcesApi extends ApiClient.Api {


  /**
   * Get all context sources
   * 
    * @param projectKey The project key (required)
    * @param schemaType Schema type (optional)
   * @return List&lt;ContextSourceDTO&gt;
   */
  @RequestLine("GET /projects/{projectKey}/configuration/context-sources/shared?schemaType={schemaType}")
  @Headers({
    "Accept: application/json",
  })
  List<ContextSourceDTO> getContextSources(@Param("projectKey") String projectKey, @Param("schemaType") String schemaType);

  /**
   * Get all context sources
   * 
   * Note, this is equivalent to the other <code>getContextSources</code> method,
   * but with the query parameters collected into a single Map parameter. This
   * is convenient for services with optional query parameters, especially when
   * used with the {@link GetContextSourcesQueryParams} class that allows for
   * building up this map in a fluent style.
   * @param projectKey The project key (required)
   * @param queryParams Map of query parameters as name-value pairs
   *   <p>The following elements may be specified in the query map:</p>
   *   <ul>
   *   <li>schemaType - Schema type (optional)</li>
   *   </ul>
   * @return List&lt;ContextSourceDTO&gt;
   */
  @RequestLine("GET /projects/{projectKey}/configuration/context-sources/shared?schemaType={schemaType}")
  @Headers({
  "Accept: application/json",
  })
  List<ContextSourceDTO> getContextSources(@Param("projectKey") String projectKey, @QueryMap(encoded=true) Map<String, Object> queryParams);

  /**
   * A convenience class for generating query parameters for the
   * <code>getContextSources</code> method in a fluent style.
   */
  public static class GetContextSourcesQueryParams extends HashMap<String, Object> {
    public GetContextSourcesQueryParams schemaType(final String value) {
      put("schemaType", EncodingUtils.encode(value));
      return this;
    }
  }

  /**
   * Find context entities metadata
   * 
    * @param projectKey The project key (required)
    * @param entities context entities ref (required)
   * @return List&lt;BaseEntityDTO&gt;
   */
  @RequestLine("POST /projects/{projectKey}/configuration/context-sources/context-entities-metadata")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  List<BaseEntityDTO> searchContextEntitiesMetadata(@Param("projectKey") String projectKey, EntitiesDTO entities);
}
