/**
 * NOTE: This class is auto generated by the swagger code generator program (2.4.20).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.BulkGroupDTO;
import java.util.List;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.dto.SetFiltersRequestDTO;
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
@Api(value = "BulkGroups", description = "the BulkGroups API")
@RequestMapping(value = "/aia/api/v1/integration-layer")
public interface BulkGroupsApi {

    Logger log = LoggerFactory.getLogger(BulkGroupsApi.class);

    default Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    @ApiOperation(value = "Add a new Bulk Group", nickname = "addBulkGroup", notes = "", response = BulkGroupDTO.class, tags={ "Bulk Groups", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK Bulk Group created", response = BulkGroupDTO.class),
        @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/projects/{projectKey}/schemas/{schemaStoreKey}/bulk-groups",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<BulkGroupDTO> addBulkGroup(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Schema Store Key",required=true) @PathVariable("schemaStoreKey") String schemaStoreKey,@ApiParam(value = "The new Bulk group" ,required=true )  @Valid @RequestBody BulkGroupDTO bulkGroup) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"groupFilter\" : {    \"filter\" : \"filter\"  },  \"originProcess\" : \"originProcess\",  \"bulkGroupKey\" : \"bulkGroupKey\",  \"entityFilters\" : [ {    \"entityKey\" : \"entityKey\",    \"entityFilterKey\" : \"entityFilterKey\"  }, {    \"entityKey\" : \"entityKey\",    \"entityFilterKey\" : \"entityFilterKey\"  } ],  \"bulkGroupName\" : \"bulkGroupName\",  \"schemaKey\" : \"schemaKey\"}", BulkGroupDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default BulkGroupsApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Add  new Bulk Groups", nickname = "addBulkGroups", notes = "", response = SaveElementsResponseDTO.class, tags={ "Bulk Groups", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK Bulk Groups created", response = SaveElementsResponseDTO.class),
        @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/projects/{projectKey}/bulk-groups",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<SaveElementsResponseDTO> addBulkGroups(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The new Bulk groups" ,required=true )  @Valid @RequestBody List<BulkGroupDTO> bulkGroups) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"savedElementsCount\" : 0}", SaveElementsResponseDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default BulkGroupsApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Delete a BulkGroup", nickname = "deleteBulkGroup", notes = "", tags={ "Bulk Groups", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 400, message = "Invalid Context value"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "The specified resource was not found"),
        @ApiResponse(code = 500, message = "Internal server error"),
        @ApiResponse(code = 200, message = "Unexpected error") })
    @RequestMapping(value = "/projects/{projectKey}/schemas/{schemaStoreKey}/bulk-groups/{bulkGroupKey}",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    default ResponseEntity<Void> deleteBulkGroup(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Schema Store Key",required=true) @PathVariable("schemaStoreKey") String schemaStoreKey,@ApiParam(value = "Bulk Group Key",required=true) @PathVariable("bulkGroupKey") String bulkGroupKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default BulkGroupsApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Get a bulk group", nickname = "getBulkGroupByKey", notes = "", response = BulkGroupDTO.class, tags={ "Bulk Groups", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Bulk group found", response = BulkGroupDTO.class),
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Bulk group not found") })
    @RequestMapping(value = "/projects/{projectKey}/schemas/{schemaStoreKey}/bulk-groups/{bulkGroupKey}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<BulkGroupDTO> getBulkGroupByKey(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Schema Store Key",required=true) @PathVariable("schemaStoreKey") String schemaStoreKey,@ApiParam(value = "Bulk Group Key",required=true) @PathVariable("bulkGroupKey") String bulkGroupKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"groupFilter\" : {    \"filter\" : \"filter\"  },  \"originProcess\" : \"originProcess\",  \"bulkGroupKey\" : \"bulkGroupKey\",  \"entityFilters\" : [ {    \"entityKey\" : \"entityKey\",    \"entityFilterKey\" : \"entityFilterKey\"  }, {    \"entityKey\" : \"entityKey\",    \"entityFilterKey\" : \"entityFilterKey\"  } ],  \"bulkGroupName\" : \"bulkGroupName\",  \"schemaKey\" : \"schemaKey\"}", BulkGroupDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default BulkGroupsApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Get all bulk groups", nickname = "getBulkGroups", notes = "", response = Object.class, tags={ "Bulk Groups", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Bulk groups loaded", response = Object.class),
        @ApiResponse(code = 400, message = "Invalid project key supplied"),
        @ApiResponse(code = 404, message = "Project not found") })
    @RequestMapping(value = "/projects/{projectKey}/schemas/{schemaStoreKey}/bulk-groups",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<Object> getBulkGroups(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Schema Store Key",required=true) @PathVariable("schemaStoreKey") String schemaStoreKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("\"{}\"", Object.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default BulkGroupsApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Update an existing bulk group", nickname = "updateBulkGroup", notes = "", response = BulkGroupDTO.class, tags={ "Bulk Groups", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK BulkGroup updated", response = BulkGroupDTO.class),
        @ApiResponse(code = 201, message = "Created"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "The specified resource was not found"),
        @ApiResponse(code = 500, message = "Internal server error"),
        @ApiResponse(code = 200, message = "Unexpected error") })
    @RequestMapping(value = "/projects/{projectKey}/schemas/{schemaStoreKey}/bulk-groups/{bulkGroupKey}",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    default ResponseEntity<BulkGroupDTO> updateBulkGroup(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Schema Store Key",required=true) @PathVariable("schemaStoreKey") String schemaStoreKey,@ApiParam(value = "Bulk Group Key",required=true) @PathVariable("bulkGroupKey") String bulkGroupKey,@ApiParam(value = "Bulk group to be updated" ,required=true )  @Valid @RequestBody BulkGroupDTO bulkGroup) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"groupFilter\" : {    \"filter\" : \"filter\"  },  \"originProcess\" : \"originProcess\",  \"bulkGroupKey\" : \"bulkGroupKey\",  \"entityFilters\" : [ {    \"entityKey\" : \"entityKey\",    \"entityFilterKey\" : \"entityFilterKey\"  }, {    \"entityKey\" : \"entityKey\",    \"entityFilterKey\" : \"entityFilterKey\"  } ],  \"bulkGroupName\" : \"bulkGroupName\",  \"schemaKey\" : \"schemaKey\"}", BulkGroupDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default BulkGroupsApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Update an existing bulk group with entity filters", nickname = "updateBulkGroupEntityFilters", notes = "", response = SetFiltersRequestDTO.class, tags={ "Bulk Groups", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK filters updated", response = SetFiltersRequestDTO.class),
        @ApiResponse(code = 201, message = "Created"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "The specified resource was not found"),
        @ApiResponse(code = 500, message = "Internal server error"),
        @ApiResponse(code = 200, message = "Unexpected error") })
    @RequestMapping(value = "/projects/{projectKey}/schemas/{schemaStoreKey}/bulk-groups/{bulkGroupKey}/set-entity-filters",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    default ResponseEntity<SetFiltersRequestDTO> updateBulkGroupEntityFilters(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Schema Store Key",required=true) @PathVariable("schemaStoreKey") String schemaStoreKey,@ApiParam(value = "Bulk Group Key",required=true) @PathVariable("bulkGroupKey") String bulkGroupKey,@ApiParam(value = "Request containing all entities with filters and the bulk group with list of filter references" ,required=true )  @Valid @RequestBody SetFiltersRequestDTO setFiltersRequest) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"entities\" : [ {    \"isTransaction\" : true,    \"collectionRules\" : {      \"storeType\" : \"CSV\",      \"defaultFilter\" : {        \"filterKey\" : \"filterKey\",        \"filterLogic\" : \"filterLogic\"      },      \"incrementalAttribute\" : {        \"type\" : \"SEQUENCE\",        \"key\" : \"key\"      },      \"filters\" : [ {        \"filterKey\" : \"filterKey\",        \"filterLogic\" : \"filterLogic\"      }, {        \"filterKey\" : \"filterKey\",        \"filterLogic\" : \"filterLogic\"      } ]    },    \"entityKey\" : \"entityKey\",    \"description\" : \"description\",    \"isActive\" : true,    \"createdAt\" : 5,    \"originProcess\" : \"originProcess\",    \"createdBy\" : \"createdBy\",    \"entityName\" : \"entityName\",    \"attributes\" : [ {      \"isRequired\" : true,      \"defaultValue\" : \"defaultValue\",      \"isUpdateTime\" : true,      \"description\" : \"description\",      \"attributeKey\" : \"attributeKey\",      \"logicalDatatype\" : \"logicalDatatype\",      \"datatype\" : \"datatype\",      \"isLogicalTime\" : true,      \"attributeName\" : \"attributeName\",      \"storeInfo\" : {        \"storeType\" : \"CSV\"      },      \"keyPosition\" : 1,      \"validationRegex\" : \"validationRegex\",      \"serializationId\" : 6    }, {      \"isRequired\" : true,      \"defaultValue\" : \"defaultValue\",      \"isUpdateTime\" : true,      \"description\" : \"description\",      \"attributeKey\" : \"attributeKey\",      \"logicalDatatype\" : \"logicalDatatype\",      \"datatype\" : \"datatype\",      \"isLogicalTime\" : true,      \"attributeName\" : \"attributeName\",      \"storeInfo\" : {        \"storeType\" : \"CSV\"      },      \"keyPosition\" : 1,      \"validationRegex\" : \"validationRegex\",      \"serializationId\" : 6    } ],    \"storeInfo\" : {      \"storeType\" : \"CSV\"    },    \"isTransient\" : true,    \"serializationId\" : 0,    \"schemaKey\" : \"schemaKey\",    \"status\" : { }  }, {    \"isTransaction\" : true,    \"collectionRules\" : {      \"storeType\" : \"CSV\",      \"defaultFilter\" : {        \"filterKey\" : \"filterKey\",        \"filterLogic\" : \"filterLogic\"      },      \"incrementalAttribute\" : {        \"type\" : \"SEQUENCE\",        \"key\" : \"key\"      },      \"filters\" : [ {        \"filterKey\" : \"filterKey\",        \"filterLogic\" : \"filterLogic\"      }, {        \"filterKey\" : \"filterKey\",        \"filterLogic\" : \"filterLogic\"      } ]    },    \"entityKey\" : \"entityKey\",    \"description\" : \"description\",    \"isActive\" : true,    \"createdAt\" : 5,    \"originProcess\" : \"originProcess\",    \"createdBy\" : \"createdBy\",    \"entityName\" : \"entityName\",    \"attributes\" : [ {      \"isRequired\" : true,      \"defaultValue\" : \"defaultValue\",      \"isUpdateTime\" : true,      \"description\" : \"description\",      \"attributeKey\" : \"attributeKey\",      \"logicalDatatype\" : \"logicalDatatype\",      \"datatype\" : \"datatype\",      \"isLogicalTime\" : true,      \"attributeName\" : \"attributeName\",      \"storeInfo\" : {        \"storeType\" : \"CSV\"      },      \"keyPosition\" : 1,      \"validationRegex\" : \"validationRegex\",      \"serializationId\" : 6    }, {      \"isRequired\" : true,      \"defaultValue\" : \"defaultValue\",      \"isUpdateTime\" : true,      \"description\" : \"description\",      \"attributeKey\" : \"attributeKey\",      \"logicalDatatype\" : \"logicalDatatype\",      \"datatype\" : \"datatype\",      \"isLogicalTime\" : true,      \"attributeName\" : \"attributeName\",      \"storeInfo\" : {        \"storeType\" : \"CSV\"      },      \"keyPosition\" : 1,      \"validationRegex\" : \"validationRegex\",      \"serializationId\" : 6    } ],    \"storeInfo\" : {      \"storeType\" : \"CSV\"    },    \"isTransient\" : true,    \"serializationId\" : 0,    \"schemaKey\" : \"schemaKey\",    \"status\" : { }  } ],  \"bulkGroup\" : {    \"groupFilter\" : {      \"filter\" : \"filter\"    },    \"originProcess\" : \"originProcess\",    \"bulkGroupKey\" : \"bulkGroupKey\",    \"entityFilters\" : [ {      \"entityKey\" : \"entityKey\",      \"entityFilterKey\" : \"entityFilterKey\"    }, {      \"entityKey\" : \"entityKey\",      \"entityFilterKey\" : \"entityFilterKey\"    } ],    \"bulkGroupName\" : \"bulkGroupName\",    \"schemaKey\" : \"schemaKey\"  }}", SetFiltersRequestDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default BulkGroupsApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
