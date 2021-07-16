package com.amdocs.aia.il.sqlite.client.api;

import com.amdocs.aia.il.sqlite.client.ApiClient;
import com.amdocs.aia.il.sqlite.client.EncodingUtils;

import java.io.File;
import com.amdocs.aia.il.sqlite.client.dto.QueryDTO;
import com.amdocs.aia.il.sqlite.client.dto.ResultSetDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import feign.*;


public interface SqLiteApi extends ApiClient.Api {


  /**
   * execute sql update statement
   * 
    * @param projectKey The project key (required)
    * @param query sql update query (required)
   */
  @RequestLine("POST /projects/{projectKey}/execute")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  void execute(@Param("projectKey") String projectKey, QueryDTO query);

  /**
   * export sqlite database
   * export sqlite database
    * @param projectKey The project key (required)
   * @return File
   */
  @RequestLine("GET /projects/{projectKey}/export")
  @Headers({
    "Accept: application/json",
  })
  File exportDatabase(@Param("projectKey") String projectKey);

  /**
   * execute sql select statement
   * 
    * @param projectKey The project key (required)
    * @param query sql select query (required)
   * @return ResultSetDTO
   */
  @RequestLine("POST /projects/{projectKey}/query")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  ResultSetDTO query(@Param("projectKey") String projectKey, QueryDTO query);
}
