/**
 * NOTE: This class is auto generated by the swagger code generator program (2.4.20).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.EntityTransformationDTO;
import com.amdocs.aia.il.configuration.dto.SharedEntityTransformationGridElementDTO;
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
@Api(value = "SharedEntityTransformation", description = "the SharedEntityTransformation API")
@RequestMapping(value = "/aia/api/v1/integration-layer")
public interface SharedEntityTransformationApi {

    Logger log = LoggerFactory.getLogger(SharedEntityTransformationApi.class);

    default Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    @ApiOperation(value = "Add a Shared Entity Transformation", nickname = "addSharedEntityTransformation", notes = "", response = EntityTransformationDTO.class, tags={ "Shared Entity Transformation", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Created", response = EntityTransformationDTO.class),
        @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/shared-entity-transformation",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<EntityTransformationDTO> addSharedEntityTransformation(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Entity Transformation" ,required=true )  @Valid @RequestBody EntityTransformationDTO entityTransformation) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"entityName\" : \"entityName\",  \"transformations\" : [ \"\", \"\" ],  \"logicalEntityKey\" : \"logicalEntityKey\",  \"description\" : \"description\",  \"logicalSchemaKey\" : \"logicalSchemaKey\",  \"attributes\" : [ {    \"isRequired\" : true,    \"parentSchemaKey\" : \"parentSchemaKey\",    \"parentEntityKey\" : \"parentEntityKey\",    \"origin\" : \"origin\",    \"isUpdateTime\" : true,    \"description\" : \"description\",    \"attributeKey\" : \"attributeKey\",    \"type\" : \"type\",    \"doReferencialIntegrity\" : true,    \"designSource\" : \"designSource\",    \"sortOrder\" : 0,    \"isLogicalTime\" : true,    \"parentAttributeKey\" : \"parentAttributeKey\",    \"attributeName\" : \"attributeName\",    \"sourceMapping\" : \"sourceMapping\",    \"keyPosition\" : true  }, {    \"isRequired\" : true,    \"parentSchemaKey\" : \"parentSchemaKey\",    \"parentEntityKey\" : \"parentEntityKey\",    \"origin\" : \"origin\",    \"isUpdateTime\" : true,    \"description\" : \"description\",    \"attributeKey\" : \"attributeKey\",    \"type\" : \"type\",    \"doReferencialIntegrity\" : true,    \"designSource\" : \"designSource\",    \"sortOrder\" : 0,    \"isLogicalTime\" : true,    \"parentAttributeKey\" : \"parentAttributeKey\",    \"attributeName\" : \"attributeName\",    \"sourceMapping\" : \"sourceMapping\",    \"keyPosition\" : true  } ],  \"contexts\" : [ \"\", \"\" ],  \"usedBy\" : [ \"usedBy\", \"usedBy\" ]}", EntityTransformationDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default SharedEntityTransformationApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Delete a Shared Entity Transformation", nickname = "deleteSharedEntityTransformation", notes = "", tags={ "Shared Entity Transformation", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 400, message = "Invalid Shared Entity Transformation value"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "The specified resource was not found"),
        @ApiResponse(code = 500, message = "Internal server error"),
        @ApiResponse(code = 200, message = "Unexpected error") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/shared-entity-transformation/{logicalEntityKey}",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    default ResponseEntity<Void> deleteSharedEntityTransformation(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Logical Entity Key",required=true) @PathVariable("logicalEntityKey") String logicalEntityKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default SharedEntityTransformationApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Get shared entity transformation", nickname = "getSharedEntityTransformation", notes = "", response = EntityTransformationDTO.class, tags={ "Shared Entity Transformation", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Shared Entity Transformation loaded", response = EntityTransformationDTO.class),
        @ApiResponse(code = 400, message = "Invalid keys supplied"),
        @ApiResponse(code = 404, message = "Transformation not found") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/shared-entity-transformation/{logicalEntityKey}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<EntityTransformationDTO> getSharedEntityTransformation(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Logical Entity Key",required=true) @PathVariable("logicalEntityKey") String logicalEntityKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"entityName\" : \"entityName\",  \"transformations\" : [ \"\", \"\" ],  \"logicalEntityKey\" : \"logicalEntityKey\",  \"description\" : \"description\",  \"logicalSchemaKey\" : \"logicalSchemaKey\",  \"attributes\" : [ {    \"isRequired\" : true,    \"parentSchemaKey\" : \"parentSchemaKey\",    \"parentEntityKey\" : \"parentEntityKey\",    \"origin\" : \"origin\",    \"isUpdateTime\" : true,    \"description\" : \"description\",    \"attributeKey\" : \"attributeKey\",    \"type\" : \"type\",    \"doReferencialIntegrity\" : true,    \"designSource\" : \"designSource\",    \"sortOrder\" : 0,    \"isLogicalTime\" : true,    \"parentAttributeKey\" : \"parentAttributeKey\",    \"attributeName\" : \"attributeName\",    \"sourceMapping\" : \"sourceMapping\",    \"keyPosition\" : true  }, {    \"isRequired\" : true,    \"parentSchemaKey\" : \"parentSchemaKey\",    \"parentEntityKey\" : \"parentEntityKey\",    \"origin\" : \"origin\",    \"isUpdateTime\" : true,    \"description\" : \"description\",    \"attributeKey\" : \"attributeKey\",    \"type\" : \"type\",    \"doReferencialIntegrity\" : true,    \"designSource\" : \"designSource\",    \"sortOrder\" : 0,    \"isLogicalTime\" : true,    \"parentAttributeKey\" : \"parentAttributeKey\",    \"attributeName\" : \"attributeName\",    \"sourceMapping\" : \"sourceMapping\",    \"keyPosition\" : true  } ],  \"contexts\" : [ \"\", \"\" ],  \"usedBy\" : [ \"usedBy\", \"usedBy\" ]}", EntityTransformationDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default SharedEntityTransformationApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Get a Shared Entity Transformation List", nickname = "getSharedEntityTransformationList", notes = "", response = SharedEntityTransformationGridElementDTO.class, responseContainer = "List", tags={ "Shared Entity Transformation", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Shared entity transformation list response", response = SharedEntityTransformationGridElementDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Invalid project key supplied"),
        @ApiResponse(code = 404, message = "Project not found") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/shared-entity-transformation",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<SharedEntityTransformationGridElementDTO>> getSharedEntityTransformationList(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("[ \"\", \"\" ]", List.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default SharedEntityTransformationApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Update a Shared Entity Transformation", nickname = "updateSharedEntityTransformation", notes = "", response = EntityTransformationDTO.class, tags={ "Shared Entity Transformation", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Updated", response = EntityTransformationDTO.class),
        @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/shared-entity-transformation",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    default ResponseEntity<EntityTransformationDTO> updateSharedEntityTransformation(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Entity Transformation" ,required=true )  @Valid @RequestBody EntityTransformationDTO entityTransformation) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"entityName\" : \"entityName\",  \"transformations\" : [ \"\", \"\" ],  \"logicalEntityKey\" : \"logicalEntityKey\",  \"description\" : \"description\",  \"logicalSchemaKey\" : \"logicalSchemaKey\",  \"attributes\" : [ {    \"isRequired\" : true,    \"parentSchemaKey\" : \"parentSchemaKey\",    \"parentEntityKey\" : \"parentEntityKey\",    \"origin\" : \"origin\",    \"isUpdateTime\" : true,    \"description\" : \"description\",    \"attributeKey\" : \"attributeKey\",    \"type\" : \"type\",    \"doReferencialIntegrity\" : true,    \"designSource\" : \"designSource\",    \"sortOrder\" : 0,    \"isLogicalTime\" : true,    \"parentAttributeKey\" : \"parentAttributeKey\",    \"attributeName\" : \"attributeName\",    \"sourceMapping\" : \"sourceMapping\",    \"keyPosition\" : true  }, {    \"isRequired\" : true,    \"parentSchemaKey\" : \"parentSchemaKey\",    \"parentEntityKey\" : \"parentEntityKey\",    \"origin\" : \"origin\",    \"isUpdateTime\" : true,    \"description\" : \"description\",    \"attributeKey\" : \"attributeKey\",    \"type\" : \"type\",    \"doReferencialIntegrity\" : true,    \"designSource\" : \"designSource\",    \"sortOrder\" : 0,    \"isLogicalTime\" : true,    \"parentAttributeKey\" : \"parentAttributeKey\",    \"attributeName\" : \"attributeName\",    \"sourceMapping\" : \"sourceMapping\",    \"keyPosition\" : true  } ],  \"contexts\" : [ \"\", \"\" ],  \"usedBy\" : [ \"usedBy\", \"usedBy\" ]}", EntityTransformationDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default SharedEntityTransformationApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
