package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.BulkImportResponseDTO;
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface BulkApi extends ApiClient.Api {


  /**
   * Export External Schemas
   * 
    * @param projectKey The project key (required)
   * @return File
   */
  @RequestLine("GET /projects/{projectKey}/configuration/external-schemas/export")
  @Headers({
    "Accept: application/octet-stream",
  })
  File exportExternalSchemas(@Param("projectKey") String projectKey);

  /**
   * Import External Schemas 
   * 
    * @param file  (required)
    * @param projectKey The project key (required)
   * @return BulkImportResponseDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/external-schemas/import")
  @Headers({
    "Content-Type: multipart/form-data",
    "Accept: application/json",
  })
  BulkImportResponseDTO importExternalSchemas(@Param("file") File file, @Param("projectKey") String projectKey);
}
