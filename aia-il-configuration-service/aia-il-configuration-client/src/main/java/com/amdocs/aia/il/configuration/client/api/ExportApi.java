package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface ExportApi extends ApiClient.Api {


  /**
   * Export all transformations scripts
   * 
    * @param projectKey The project key (required)
   * @return File
   */
  @RequestLine("GET /projects/{projectKey}/configuration/export-transformations-scripts")
  @Headers({
    "Accept: application/octet-stream",
  })
  File exportTransformationsScripts(@Param("projectKey") String projectKey);
}
