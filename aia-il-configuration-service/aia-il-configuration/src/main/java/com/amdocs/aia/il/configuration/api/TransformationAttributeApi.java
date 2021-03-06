/**
 * NOTE: This class is auto generated by the swagger code generator program (2.4.20).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.TransformationAttributeDTO;
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
@Api(value = "TransformationAttribute", description = "the TransformationAttribute API")
@RequestMapping(value = "/aia/api/v1/integration-layer")
public interface TransformationAttributeApi {

    Logger log = LoggerFactory.getLogger(TransformationAttributeApi.class);

    default Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    @ApiOperation(value = "Get all available transformation Attributes", nickname = "getAvailableAttributes", notes = "", response = TransformationAttributeDTO.class, responseContainer = "List", tags={ "Transformation Attribute", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "TransformationAttribute loaded", response = TransformationAttributeDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Invalid keys supplied"),
        @ApiResponse(code = 404, message = "Transformation Attribute not found") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/transformation-attributes/{logicalSchemaKey}/{logicalEntityKey}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<TransformationAttributeDTO>> getAvailableAttributes(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Logical Schema Key",required=true) @PathVariable("logicalSchemaKey") String logicalSchemaKey,@ApiParam(value = "Logical Entity Key",required=true) @PathVariable("logicalEntityKey") String logicalEntityKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("[ {  \"isRequired\" : true,  \"parentSchemaKey\" : \"parentSchemaKey\",  \"parentEntityKey\" : \"parentEntityKey\",  \"origin\" : \"origin\",  \"isUpdateTime\" : true,  \"description\" : \"description\",  \"attributeKey\" : \"attributeKey\",  \"type\" : \"type\",  \"doReferencialIntegrity\" : true,  \"designSource\" : \"designSource\",  \"sortOrder\" : 0,  \"isLogicalTime\" : true,  \"parentAttributeKey\" : \"parentAttributeKey\",  \"attributeName\" : \"attributeName\",  \"sourceMapping\" : \"sourceMapping\",  \"keyPosition\" : true}, {  \"isRequired\" : true,  \"parentSchemaKey\" : \"parentSchemaKey\",  \"parentEntityKey\" : \"parentEntityKey\",  \"origin\" : \"origin\",  \"isUpdateTime\" : true,  \"description\" : \"description\",  \"attributeKey\" : \"attributeKey\",  \"type\" : \"type\",  \"doReferencialIntegrity\" : true,  \"designSource\" : \"designSource\",  \"sortOrder\" : 0,  \"isLogicalTime\" : true,  \"parentAttributeKey\" : \"parentAttributeKey\",  \"attributeName\" : \"attributeName\",  \"sourceMapping\" : \"sourceMapping\",  \"keyPosition\" : true} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default TransformationAttributeApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Get all transformation Attributes", nickname = "getTransformationAttributes", notes = "", response = TransformationAttributeDTO.class, responseContainer = "List", tags={ "Transformation Attribute", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "TransformationAttribute loaded", response = TransformationAttributeDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Invalid keys supplied"),
        @ApiResponse(code = 404, message = "Transformation Attribute not found") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/transformation-attributes/{logicalSchemaKey}/entity-stores/{logicalEntityKey}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<TransformationAttributeDTO>> getTransformationAttributes(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Logical Schema Key",required=true) @PathVariable("logicalSchemaKey") String logicalSchemaKey,@ApiParam(value = "Logical Entity Key",required=true) @PathVariable("logicalEntityKey") String logicalEntityKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("[ {  \"isRequired\" : true,  \"parentSchemaKey\" : \"parentSchemaKey\",  \"parentEntityKey\" : \"parentEntityKey\",  \"origin\" : \"origin\",  \"isUpdateTime\" : true,  \"description\" : \"description\",  \"attributeKey\" : \"attributeKey\",  \"type\" : \"type\",  \"doReferencialIntegrity\" : true,  \"designSource\" : \"designSource\",  \"sortOrder\" : 0,  \"isLogicalTime\" : true,  \"parentAttributeKey\" : \"parentAttributeKey\",  \"attributeName\" : \"attributeName\",  \"sourceMapping\" : \"sourceMapping\",  \"keyPosition\" : true}, {  \"isRequired\" : true,  \"parentSchemaKey\" : \"parentSchemaKey\",  \"parentEntityKey\" : \"parentEntityKey\",  \"origin\" : \"origin\",  \"isUpdateTime\" : true,  \"description\" : \"description\",  \"attributeKey\" : \"attributeKey\",  \"type\" : \"type\",  \"doReferencialIntegrity\" : true,  \"designSource\" : \"designSource\",  \"sortOrder\" : 0,  \"isLogicalTime\" : true,  \"parentAttributeKey\" : \"parentAttributeKey\",  \"attributeName\" : \"attributeName\",  \"sourceMapping\" : \"sourceMapping\",  \"keyPosition\" : true} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default TransformationAttributeApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
