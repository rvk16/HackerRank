package com.amdocs.aia.il.configuration.client.api;

import com.amdocs.aia.il.configuration.client.ApiClient;
import com.amdocs.aia.il.configuration.client.EncodingUtils;

import com.amdocs.aia.il.configuration.client.dto.AsyncResponseDTO;
import com.amdocs.aia.il.configuration.client.dto.DiscoverExternalCsvRequestDTO;
import com.amdocs.aia.il.configuration.client.dto.DiscoverExternalJsonRequestDTO;
import java.io.File;
import com.amdocs.aia.il.configuration.client.dto.SchemaDiscoveryRequestDTO;
import com.amdocs.aia.il.configuration.client.dto.UploadDiscoveryFileResponseDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface DiscoveryApi extends ApiClient.Api {


  /**
   * Discover External Csv Async
   * 
    * @param projectKey The project key (required)
    * @param discoverExternalCsvRequest The discovery external Csv request details (required)
   * @return AsyncResponseDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/discovery/discover-external-csv")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  AsyncResponseDTO discoverExternalCsvAsync(@Param("projectKey") String projectKey, DiscoverExternalCsvRequestDTO discoverExternalCsvRequest);

  /**
   * Discover External Json Async
   * 
    * @param projectKey The project key (required)
    * @param discoverExternalJsonRequest The discovery external Json request details (required)
   * @return AsyncResponseDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/discovery/discover-external-json")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  AsyncResponseDTO discoverExternalJsonAsync(@Param("projectKey") String projectKey, DiscoverExternalJsonRequestDTO discoverExternalJsonRequest);

  /**
   * Discover External Schema
   * A generic synchorinious API for discovering entities of ANY type of external schema
    * @param projectKey The project key (required)
    * @param discoveryRequest The discovery request details (required)
   */
  @RequestLine("POST /projects/{projectKey}/configuration/discovery/discover-external-schema")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  void discoverExternalSchema(@Param("projectKey") String projectKey, SchemaDiscoveryRequestDTO discoveryRequest);

  /**
   * Upload Discovery File
   * 
    * @param projectKey The project key (required)
    * @param file The fileName uploaded (required)
   * @return UploadDiscoveryFileResponseDTO
   */
  @RequestLine("POST /projects/{projectKey}/configuration/discovery/upload-file")
  @Headers({
    "Content-Type: multipart/form-data",
    "Accept: application/json",
  })
  UploadDiscoveryFileResponseDTO uploadDiscoveryFile(@Param("projectKey") String projectKey, @Param("file") File file);
}
