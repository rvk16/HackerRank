package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.service.external.ExternalSchemaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = {"ExternalSchema"})
public class ExternalSchemaController implements ExternalSchemaApi {

    private final ExternalSchemaService externalSchemasService;
    private static final String EXTERNAL_SCHEMA = "ExternalSchema";

    @Autowired
    public ExternalSchemaController(ExternalSchemaService externalSchemasService) {
        this.externalSchemasService = externalSchemasService;
    }


    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleExternalSchemaMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<List<ExternalSchemaDTO>> getExternalSchemas(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey) {
        List<ExternalSchemaDTO> externalSchemaDTOList = externalSchemasService.list(projectKey);
        return ResponseEntity.ok(externalSchemaDTOList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetExternalSchemaTypes'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<List<ExternalSchemaTypeInfoDTO>> getExternalSchemaTypes(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey) {
        List<ExternalSchemaTypeInfoDTO> externalSchemaTypes = externalSchemasService.listExternalSchemaTypes(projectKey);
        return ResponseEntity.ok(externalSchemaTypes);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetCollectionChannelTypes'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<List<CollectionChannelTypeInfoDTO>> getCollectionChannelTypes(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey) {
        List<CollectionChannelTypeInfoDTO> collectionChannels = externalSchemasService.listCollectionChannelTypes(projectKey);
        return ResponseEntity.ok(collectionChannels);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetTypeSystems'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<List<TypeSystemInfoDTO>> getTypeSystems(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey) {
        List<TypeSystemInfoDTO> typeSystems = externalSchemasService.listTypeSystems(projectKey);
        return ResponseEntity.ok(typeSystems);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetExternalSchemaType'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<ExternalSchemaTypeInfoDTO> getExternalSchemaType(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "External Schema Type",required=true) @PathVariable("externalSchemaType") String externalSchemaType) {
        ExternalSchemaTypeInfoDTO externalSchemaTypeInfo = externalSchemasService.getExternalSchemaType(projectKey, externalSchemaType);
        return ResponseEntity.ok(externalSchemaTypeInfo);
    }


        @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleExternalSchemaMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<ExternalSchemaDTO> addExternalSchema(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey, @ApiParam(value = "The new External Schema", required = true) @Valid @RequestBody ExternalSchemaDTO externalSchema) {
        ExternalSchemaDTO externalSchemaDTO = externalSchemasService.save(projectKey, externalSchema);
        return ResponseEntity.status(HttpStatus.CREATED).body(externalSchemaDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleExternalSchemaMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<ExternalSchemaDTO> getExternalSchemaByKey(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey, @ApiParam(value = "External Schema Key", required = true) @PathVariable("externalSchemaKey") String externalSchemaKey) {
        ExternalSchemaDTO externalSchemaDTO = externalSchemasService.get(projectKey, externalSchemaKey);
        return ResponseEntity.ok(externalSchemaDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleUpdateExternalSchemaMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<ExternalSchemaDTO> updateExternalSchema(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "External Schema Key",required=true) @PathVariable("externalSchemaKey") String externalSchemaKey,@ApiParam(value = "The Updated External Schema" ,required=true )  @Valid @RequestBody ExternalSchemaDTO externalSchema) {
        if (!externalSchema.getSchemaKey().equals(externalSchemaKey)){
            throw new ApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_KEY_NOT_COMPATIBLE,
                    externalSchemaKey,
                    EXTERNAL_SCHEMA,
                    externalSchema.getSchemaKey());
        }

        ExternalSchemaDTO externalSchemaDTO = externalSchemasService.update(projectKey, externalSchemaKey, externalSchema);
        return ResponseEntity.status(HttpStatus.CREATED).body(externalSchemaDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleDeleteExternalSchemaMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Void> deleteExternalSchemaByKey(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "External Schema Key",required=true) @PathVariable("externalSchemaKey") String externalSchemaKey) {
        externalSchemasService.delete(projectKey,externalSchemaKey);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddExternalSchemasMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<SaveElementsResponseDTO> addExternalSchemas(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The new External Schemas" ,required=true )  @Valid @RequestBody List<ExternalSchemaDTO> externalSchemas) {
        SaveElementsResponseDTO saveElementsResponseDTO = externalSchemasService.bulkSave(projectKey, externalSchemas);
        //return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveElementsResponseDTO);
    }
}
