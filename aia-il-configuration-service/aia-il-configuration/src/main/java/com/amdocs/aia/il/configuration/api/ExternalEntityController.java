package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.configuration.dto.ExternalEntityDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.service.external.ExternalEntityService;
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
@Api(tags = {"ExternalEntity"})
public class ExternalEntityController implements ExternalEntityApi {

    private final ExternalEntityService externalEntityService;
    private static final String EXTERNAL_ENTITY = "ExternalEntity";
    private static final String EXTERNAL_SCHEMA = "ExternalSchema";

    @Autowired
    public ExternalEntityController(ExternalEntityService externalEntityService) {
        this.externalEntityService = externalEntityService;
    }


    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleExternalEntityMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<ExternalEntityDTO> addExternalEntity(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey, @ApiParam(value = "External Schema Key", required = true) @PathVariable("externalSchemaKey") String externalSchemaKey, @ApiParam(value = "The new External Entity", required = true) @Valid @RequestBody ExternalEntityDTO externalEntity) {
        if (!externalEntity.getSchemaKey().equals(externalSchemaKey)){
            throw new ApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_KEY_NOT_COMPATIBLE,
                    externalSchemaKey,
                    EXTERNAL_SCHEMA,
                    externalEntity.getSchemaKey());
        }
        ExternalEntityDTO externalEntityDTO = externalEntityService.save(projectKey, externalSchemaKey, externalEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(externalEntityDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleExternalEntityMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<List<ExternalEntityDTO>> getExternalEntities(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey, @ApiParam(value = "External Schema Key", required = true) @PathVariable("externalSchemaKey") String externalSchemaKey) {
        List<ExternalEntityDTO> ExternalEntitiesDTO = externalEntityService.list(projectKey, externalSchemaKey);
        return ResponseEntity.ok(ExternalEntitiesDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleExternalEntityMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<ExternalEntityDTO> getExternalEntityByKey(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey, @ApiParam(value = "External Schema Key", required = true) @PathVariable("externalSchemaKey") String externalSchemaKey, @ApiParam(value = "External Entity Key", required = true) @PathVariable("externalEntityKey") String externalEntityKey) {
        ExternalEntityDTO ExternalEntityDTO = externalEntityService.get(projectKey, externalSchemaKey, externalEntityKey);
        return ResponseEntity.ok(ExternalEntityDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleUpdateExternalEntityMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<ExternalEntityDTO> updateExternalEntity(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "External Schema Key",required=true) @PathVariable("externalSchemaKey") String externalSchemaKey,@ApiParam(value = "External Entity Key",required=true) @PathVariable("externalEntityKey") String externalEntityKey,@ApiParam(value = "The updated External Entity" ,required=true )  @Valid @RequestBody ExternalEntityDTO externalEntity) {
        if (!externalEntity.getEntityKey().equals(externalEntityKey)){
            throw new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.OBJECT_KEY_NOT_COMPATIBLE,
                    externalEntityKey,
                    EXTERNAL_ENTITY,
                    externalEntity.getEntityKey());
        }

        if (!externalEntity.getSchemaKey().equals(externalSchemaKey)){
            throw new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.OBJECT_KEY_NOT_COMPATIBLE,
                    externalSchemaKey,
                    EXTERNAL_SCHEMA,
                    externalEntity.getSchemaKey());
        }

        ExternalEntityDTO externalEntityDTO = externalEntityService.update(projectKey, externalSchemaKey, externalEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(externalEntityDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleDeleteExternalEntityMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Void> deleteExternalEntityByKey(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "External Schema Key",required=true) @PathVariable("externalSchemaKey") String externalSchemaKey,@ApiParam(value = "External Entity Key",required=true) @PathVariable("externalEntityKey") String externalEntityKey) {
        externalEntityService.delete(projectKey,externalSchemaKey,externalEntityKey);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddExternalEntitiesMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<SaveElementsResponseDTO>  addExternalEntities(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey, @ApiParam(value = "New External Entities" ,required=true )  @Valid @RequestBody List<ExternalEntityDTO> externalEntities) {
        SaveElementsResponseDTO ret = externalEntityService.bulkSave(projectKey, externalEntities);
        return ResponseEntity.status(HttpStatus.CREATED).body(ret);
    }

}
