/**
 * NOTE: This class is auto generated by the swagger code generator program (2.4.20).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.CollectionChannelTypeInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaTypeInfoDTO;
import java.util.List;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.dto.TypeSystemInfoDTO;
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
@Api(value = "ExternalSchema", description = "the ExternalSchema API")
@RequestMapping(value = "/aia/api/v1/integration-layer")
public interface ExternalSchemaApi {

    Logger log = LoggerFactory.getLogger(ExternalSchemaApi.class);

    default Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    @ApiOperation(value = "Add a new External Schema", nickname = "addExternalSchema", notes = "", response = ExternalSchemaDTO.class, tags={ "External Schema", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK External Schema created", response = ExternalSchemaDTO.class),
        @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/external-schemas",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<ExternalSchemaDTO> addExternalSchema(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The new External Schema" ,required=true )  @Valid @RequestBody ExternalSchemaDTO externalSchema) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"initialCollector\" : \"initialCollector\",  \"ongoingCollector\" : \"ongoingCollector\",  \"collectionRules\" : {    \"storeType\" : \"CSV\",    \"replayChannel\" : \"replayChannel\",    \"ongoingChannel\" : \"ongoingChannel\",    \"partialLoadRelativeURL\" : \"partialLoadRelativeURL\",    \"initialLoadChannel\" : \"initialLoadChannel\",    \"initialLoadRelativeURL\" : \"initialLoadRelativeURL\"  },  \"description\" : \"description\",  \"availability\" : { },  \"schemaName\" : \"schemaName\",  \"isActive\" : true,  \"dataChannelInfo\" : {    \"serializationMethod\" : \"SharedProtobuf\",    \"dataChannelName\" : \"dataChannelName\"  },  \"subjectAreaKey\" : \"subjectAreaKey\",  \"createdAt\" : 0,  \"displayType\" : \"displayType\",  \"typeSystem\" : \"typeSystem\",  \"selectiveCollector\" : \"selectiveCollector\",  \"subjectAreaName\" : \"subjectAreaName\",  \"originProcess\" : \"originProcess\",  \"createdBy\" : \"createdBy\",  \"schemaType\" : \"schemaType\",  \"isReference\" : true,  \"storeInfo\" : {    \"storeType\" : \"CSV\"  },  \"schemaKey\" : \"schemaKey\",  \"status\" : { }}", ExternalSchemaDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ExternalSchemaApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Add new External Schemas", nickname = "addExternalSchemas", notes = "", response = SaveElementsResponseDTO.class, tags={ "External Schema", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK External Schemas created", response = SaveElementsResponseDTO.class),
        @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/external-schemas/schemas-bulk",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<SaveElementsResponseDTO> addExternalSchemas(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The new External Schemas" ,required=true )  @Valid @RequestBody List<ExternalSchemaDTO> externalSchemas) {
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
            log.warn("ObjectMapper or HttpServletRequest not configured in default ExternalSchemaApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "delete an External Schema", nickname = "deleteExternalSchemaByKey", notes = "", tags={ "External Schema", })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "The External Schema was removed from the configuration. Note that 204 is returned even if the External Schema was not found and there was no need to delete anything."),
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Project or target not found") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/external-schemas/{externalSchemaKey}",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    default ResponseEntity<Void> deleteExternalSchemaByKey(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "External Schema Key",required=true) @PathVariable("externalSchemaKey") String externalSchemaKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ExternalSchemaApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Get list of supported collection channel types", nickname = "getCollectionChannelTypes", notes = "", response = CollectionChannelTypeInfoDTO.class, responseContainer = "List", tags={ "External Schema", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Collection Channel Types", response = CollectionChannelTypeInfoDTO.class, responseContainer = "List"),
        @ApiResponse(code = 404, message = "Project or target not found") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/collection-channel-types",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<CollectionChannelTypeInfoDTO>> getCollectionChannelTypes(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("[ {  \"displayName\" : \"displayName\",  \"channelType\" : \"channelType\"}, {  \"displayName\" : \"displayName\",  \"channelType\" : \"channelType\"} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ExternalSchemaApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Get an External Schema", nickname = "getExternalSchemaByKey", notes = "", response = ExternalSchemaDTO.class, tags={ "External Schema", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "External Schema found", response = ExternalSchemaDTO.class),
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Project or target not found") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/external-schemas/{externalSchemaKey}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<ExternalSchemaDTO> getExternalSchemaByKey(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "External Schema Key",required=true) @PathVariable("externalSchemaKey") String externalSchemaKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"initialCollector\" : \"initialCollector\",  \"ongoingCollector\" : \"ongoingCollector\",  \"collectionRules\" : {    \"storeType\" : \"CSV\",    \"replayChannel\" : \"replayChannel\",    \"ongoingChannel\" : \"ongoingChannel\",    \"partialLoadRelativeURL\" : \"partialLoadRelativeURL\",    \"initialLoadChannel\" : \"initialLoadChannel\",    \"initialLoadRelativeURL\" : \"initialLoadRelativeURL\"  },  \"description\" : \"description\",  \"availability\" : { },  \"schemaName\" : \"schemaName\",  \"isActive\" : true,  \"dataChannelInfo\" : {    \"serializationMethod\" : \"SharedProtobuf\",    \"dataChannelName\" : \"dataChannelName\"  },  \"subjectAreaKey\" : \"subjectAreaKey\",  \"createdAt\" : 0,  \"displayType\" : \"displayType\",  \"typeSystem\" : \"typeSystem\",  \"selectiveCollector\" : \"selectiveCollector\",  \"subjectAreaName\" : \"subjectAreaName\",  \"originProcess\" : \"originProcess\",  \"createdBy\" : \"createdBy\",  \"schemaType\" : \"schemaType\",  \"isReference\" : true,  \"storeInfo\" : {    \"storeType\" : \"CSV\"  },  \"schemaKey\" : \"schemaKey\",  \"status\" : { }}", ExternalSchemaDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ExternalSchemaApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Get information about a specific external schema type", nickname = "getExternalSchemaType", notes = "", response = ExternalSchemaTypeInfoDTO.class, tags={ "External Schema", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "External Schema type information", response = ExternalSchemaTypeInfoDTO.class),
        @ApiResponse(code = 400, message = "Invalid schema type supplied"),
        @ApiResponse(code = 404, message = "Project or target not found") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/external-schema-types/{externalSchemaType}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<ExternalSchemaTypeInfoDTO> getExternalSchemaType(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "External Schema Type",required=true) @PathVariable("externalSchemaType") String externalSchemaType) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"storeType\" : \"CSV\",  \"displayName\" : \"displayName\",  \"supportedOngoingChannels\" : [ {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  }, {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  } ],  \"type\" : \"type\",  \"supportedTypeSystems\" : [ {    \"typeSystem\" : \"typeSystem\",    \"displayName\" : \"displayName\"  }, {    \"typeSystem\" : \"typeSystem\",    \"displayName\" : \"displayName\"  } ],  \"supportedInitialLoadChannels\" : [ {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  }, {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  } ],  \"supportedReplayChannels\" : [ {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  }, {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  } ]}", ExternalSchemaTypeInfoDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ExternalSchemaApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Get list of supported external schema types", nickname = "getExternalSchemaTypes", notes = "", response = ExternalSchemaTypeInfoDTO.class, responseContainer = "List", tags={ "External Schema", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "External Schema types", response = ExternalSchemaTypeInfoDTO.class, responseContainer = "List"),
        @ApiResponse(code = 404, message = "Project or target not found") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/external-schema-types",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<ExternalSchemaTypeInfoDTO>> getExternalSchemaTypes(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("[ {  \"storeType\" : \"CSV\",  \"displayName\" : \"displayName\",  \"supportedOngoingChannels\" : [ {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  }, {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  } ],  \"type\" : \"type\",  \"supportedTypeSystems\" : [ {    \"typeSystem\" : \"typeSystem\",    \"displayName\" : \"displayName\"  }, {    \"typeSystem\" : \"typeSystem\",    \"displayName\" : \"displayName\"  } ],  \"supportedInitialLoadChannels\" : [ {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  }, {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  } ],  \"supportedReplayChannels\" : [ {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  }, {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  } ]}, {  \"storeType\" : \"CSV\",  \"displayName\" : \"displayName\",  \"supportedOngoingChannels\" : [ {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  }, {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  } ],  \"type\" : \"type\",  \"supportedTypeSystems\" : [ {    \"typeSystem\" : \"typeSystem\",    \"displayName\" : \"displayName\"  }, {    \"typeSystem\" : \"typeSystem\",    \"displayName\" : \"displayName\"  } ],  \"supportedInitialLoadChannels\" : [ {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  }, {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  } ],  \"supportedReplayChannels\" : [ {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  }, {    \"displayName\" : \"displayName\",    \"channelType\" : \"channelType\"  } ]} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ExternalSchemaApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Get all external schemas", nickname = "getExternalSchemas", notes = "", response = ExternalSchemaDTO.class, responseContainer = "List", tags={ "External Schema", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "External Schemas retrieved", response = ExternalSchemaDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Invalid project key supplied"),
        @ApiResponse(code = 404, message = "Project not found") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/external-schemas",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<ExternalSchemaDTO>> getExternalSchemas(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("[ {  \"initialCollector\" : \"initialCollector\",  \"ongoingCollector\" : \"ongoingCollector\",  \"collectionRules\" : {    \"storeType\" : \"CSV\",    \"replayChannel\" : \"replayChannel\",    \"ongoingChannel\" : \"ongoingChannel\",    \"partialLoadRelativeURL\" : \"partialLoadRelativeURL\",    \"initialLoadChannel\" : \"initialLoadChannel\",    \"initialLoadRelativeURL\" : \"initialLoadRelativeURL\"  },  \"description\" : \"description\",  \"availability\" : { },  \"schemaName\" : \"schemaName\",  \"isActive\" : true,  \"dataChannelInfo\" : {    \"serializationMethod\" : \"SharedProtobuf\",    \"dataChannelName\" : \"dataChannelName\"  },  \"subjectAreaKey\" : \"subjectAreaKey\",  \"createdAt\" : 0,  \"displayType\" : \"displayType\",  \"typeSystem\" : \"typeSystem\",  \"selectiveCollector\" : \"selectiveCollector\",  \"subjectAreaName\" : \"subjectAreaName\",  \"originProcess\" : \"originProcess\",  \"createdBy\" : \"createdBy\",  \"schemaType\" : \"schemaType\",  \"isReference\" : true,  \"storeInfo\" : {    \"storeType\" : \"CSV\"  },  \"schemaKey\" : \"schemaKey\",  \"status\" : { }}, {  \"initialCollector\" : \"initialCollector\",  \"ongoingCollector\" : \"ongoingCollector\",  \"collectionRules\" : {    \"storeType\" : \"CSV\",    \"replayChannel\" : \"replayChannel\",    \"ongoingChannel\" : \"ongoingChannel\",    \"partialLoadRelativeURL\" : \"partialLoadRelativeURL\",    \"initialLoadChannel\" : \"initialLoadChannel\",    \"initialLoadRelativeURL\" : \"initialLoadRelativeURL\"  },  \"description\" : \"description\",  \"availability\" : { },  \"schemaName\" : \"schemaName\",  \"isActive\" : true,  \"dataChannelInfo\" : {    \"serializationMethod\" : \"SharedProtobuf\",    \"dataChannelName\" : \"dataChannelName\"  },  \"subjectAreaKey\" : \"subjectAreaKey\",  \"createdAt\" : 0,  \"displayType\" : \"displayType\",  \"typeSystem\" : \"typeSystem\",  \"selectiveCollector\" : \"selectiveCollector\",  \"subjectAreaName\" : \"subjectAreaName\",  \"originProcess\" : \"originProcess\",  \"createdBy\" : \"createdBy\",  \"schemaType\" : \"schemaType\",  \"isReference\" : true,  \"storeInfo\" : {    \"storeType\" : \"CSV\"  },  \"schemaKey\" : \"schemaKey\",  \"status\" : { }} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ExternalSchemaApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Get list of supported type systems", nickname = "getTypeSystems", notes = "", response = TypeSystemInfoDTO.class, responseContainer = "List", tags={ "External Schema", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Type Systems information", response = TypeSystemInfoDTO.class, responseContainer = "List"),
        @ApiResponse(code = 404, message = "Project or target not found") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/type-systems",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<TypeSystemInfoDTO>> getTypeSystems(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("[ {  \"typeSystem\" : \"typeSystem\",  \"displayName\" : \"displayName\"}, {  \"typeSystem\" : \"typeSystem\",  \"displayName\" : \"displayName\"} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ExternalSchemaApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Update External Schema", nickname = "updateExternalSchema", notes = "", response = ExternalSchemaDTO.class, tags={ "External Schema", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK External Schema Updated", response = ExternalSchemaDTO.class),
        @ApiResponse(code = 404, message = "The specified resource was not found"),
        @ApiResponse(code = 405, message = "Invalid input") })
    @RequestMapping(value = "/projects/{projectKey}/configuration/external-schemas/{externalSchemaKey}",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    default ResponseEntity<ExternalSchemaDTO> updateExternalSchema(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "External Schema Key",required=true) @PathVariable("externalSchemaKey") String externalSchemaKey,@ApiParam(value = "The Updated External Schema" ,required=true )  @Valid @RequestBody ExternalSchemaDTO externalSchema) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"initialCollector\" : \"initialCollector\",  \"ongoingCollector\" : \"ongoingCollector\",  \"collectionRules\" : {    \"storeType\" : \"CSV\",    \"replayChannel\" : \"replayChannel\",    \"ongoingChannel\" : \"ongoingChannel\",    \"partialLoadRelativeURL\" : \"partialLoadRelativeURL\",    \"initialLoadChannel\" : \"initialLoadChannel\",    \"initialLoadRelativeURL\" : \"initialLoadRelativeURL\"  },  \"description\" : \"description\",  \"availability\" : { },  \"schemaName\" : \"schemaName\",  \"isActive\" : true,  \"dataChannelInfo\" : {    \"serializationMethod\" : \"SharedProtobuf\",    \"dataChannelName\" : \"dataChannelName\"  },  \"subjectAreaKey\" : \"subjectAreaKey\",  \"createdAt\" : 0,  \"displayType\" : \"displayType\",  \"typeSystem\" : \"typeSystem\",  \"selectiveCollector\" : \"selectiveCollector\",  \"subjectAreaName\" : \"subjectAreaName\",  \"originProcess\" : \"originProcess\",  \"createdBy\" : \"createdBy\",  \"schemaType\" : \"schemaType\",  \"isReference\" : true,  \"storeInfo\" : {    \"storeType\" : \"CSV\"  },  \"schemaKey\" : \"schemaKey\",  \"status\" : { }}", ExternalSchemaDTO.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ExternalSchemaApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
