/**
 * NOTE: This class is auto generated by the swagger code generator program (2.4.20).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.AsyncResponseDTO;
import com.amdocs.aia.il.configuration.dto.DiscoverExternalCsvRequestDTO;
import com.amdocs.aia.il.configuration.dto.DiscoverExternalJsonRequestDTO;
import com.amdocs.aia.il.configuration.dto.DiscoverExternalSqlRequestDTO;
import com.amdocs.aia.il.configuration.dto.DiscoveryTestSqlConnectionRequestDTO;
import org.springframework.core.io.Resource;
import com.amdocs.aia.il.configuration.dto.SchemaDiscoveryRequestDTO;
import com.amdocs.aia.il.configuration.dto.UploadDiscoveryFileResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Validated
@Api(value = "Discovery", description = "the Discovery API")
@RequestMapping(value = "/aia/api/v1/integration-layer")
public interface DiscoveryApi {

    Logger log = LoggerFactory.getLogger(DiscoveryApi.class);

    default Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    @ApiOperation(value = "Discover External Csv Async", nickname = "discoverExternalCsvAsync", notes = "", response = AsyncResponseDTO.class, tags={ "Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK discover External Csv Schema", response = AsyncResponseDTO.class),
        @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/discovery/discover-external-csv",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<AsyncResponseDTO> discoverExternalCsvAsync(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The discovery external Csv request details" ,required=true )  @Valid @RequestBody DiscoverExternalCsvRequestDTO discoverExternalCsvRequest) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"processId\" : 0}", AsyncResponseDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default DiscoveryApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Discover External Json Async", nickname = "discoverExternalJsonAsync", notes = "", response = AsyncResponseDTO.class, tags={ "Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK discover External Json Schema", response = AsyncResponseDTO.class),
        @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/discovery/discover-external-json",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<AsyncResponseDTO> discoverExternalJsonAsync(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The discovery external Json request details" ,required=true )  @Valid @RequestBody DiscoverExternalJsonRequestDTO discoverExternalJsonRequest) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"processId\" : 0}", AsyncResponseDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default DiscoveryApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Discover External Schema", nickname = "discoverExternalSchema", notes = "A generic synchorinious API for discovering entities of ANY type of external schema", tags={ "Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK External Schema created with discovered entities"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "The specified resource was not found"),
        @ApiResponse(code = 500, message = "Internal server error"),
        @ApiResponse(code = 200, message = "Unexpected error") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/discovery/discover-external-schema",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<Void> discoverExternalSchema(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The discovery request details" ,required=true )  @Valid @RequestBody SchemaDiscoveryRequestDTO discoveryRequest) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default DiscoveryApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Discover External Sql Async", nickname = "discoverExternalSqlAsync", notes = "", response = AsyncResponseDTO.class, tags={ "Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK discover External Sql Schema", response = AsyncResponseDTO.class),
        @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/discovery/discover-external-sql",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<AsyncResponseDTO> discoverExternalSqlAsync(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The discovery external Sql request details" ,required=true )  @Valid @RequestBody DiscoverExternalSqlRequestDTO discoverExternalSqlRequest) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"processId\" : 0}", AsyncResponseDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default DiscoveryApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Test database connection for external sql discovery", nickname = "discoveryTestSqlConnection", notes = "", tags={ "Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK test discovery sql connection"),
        @ApiResponse(code = 400, message = "Invalid input"),
        @ApiResponse(code = 500, message = "Error during discovery sql connection creation") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/discovery/test-sql-connection",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<Void> discoveryTestSqlConnection(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The discovery external Sql database connection details" ,required=true )  @Valid @RequestBody DiscoveryTestSqlConnectionRequestDTO testSqlConnectionRequest) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default DiscoveryApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Upload Discovery File", nickname = "uploadDiscoveryFile", notes = "", response = UploadDiscoveryFileResponseDTO.class, tags={ "Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK discover External Csv Schema", response = UploadDiscoveryFileResponseDTO.class),
        @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/discovery/upload-file",
        produces = { "application/json" }, 
        consumes = { "multipart/form-data" },
        method = RequestMethod.POST)
    default ResponseEntity<UploadDiscoveryFileResponseDTO> uploadDiscoveryFile(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The fileName uploaded") @Valid @RequestPart(value="file", required=true) MultipartFile file) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"fileId\" : \"fileId\"}", UploadDiscoveryFileResponseDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default DiscoveryApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
