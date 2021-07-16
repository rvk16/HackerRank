package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.TransformationAttributeDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface TransformationAttributeApi extends ApiClient.Api {


  /**
   * Get all available transformation Attributes
   * 
    * @param projectKey The project key (required)
    * @param logicalSchemaKey Logical Schema Key (required)
    * @param logicalEntityKey Logical Entity Key (required)
   * @return List&lt;TransformationAttributeDTO&gt;
   */
  @RequestLine("GET /projects/{projectKey}/configuration/transformation-attributes/{logicalSchemaKey}/{logicalEntityKey}")
  @Headers({
    "Accept: application/json",
  })
  List<TransformationAttributeDTO> getAvailableAttributes(@Param("projectKey") String projectKey, @Param("logicalSchemaKey") String logicalSchemaKey, @Param("logicalEntityKey") String logicalEntityKey);

  /**
   * Get all transformation Attributes
   * 
    * @param projectKey The project key (required)
    * @param logicalSchemaKey Logical Schema Key (required)
    * @param logicalEntityKey Logical Entity Key (required)
   * @return List&lt;TransformationAttributeDTO&gt;
   */
  @RequestLine("GET /projects/{projectKey}/configuration/transformation-attributes/{logicalSchemaKey}/entity-stores/{logicalEntityKey}")
  @Headers({
    "Accept: application/json",
  })
  List<TransformationAttributeDTO> getTransformationAttributes(@Param("projectKey") String projectKey, @Param("logicalSchemaKey") String logicalSchemaKey, @Param("logicalEntityKey") String logicalEntityKey);
}
