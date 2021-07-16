package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.TransformationAttributeDTO;
import com.amdocs.aia.il.configuration.service.TransformationAttributeConfigurationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@Api(tags = {"TransformationAttribute"})
public class TransformationAttributeApiController implements TransformationAttributeApi {

    private final TransformationAttributeConfigurationService transformationAttributeConfigurationService;

    @Inject
    public TransformationAttributeApiController(TransformationAttributeConfigurationService transformationAttributeConfigurationService) {
        this.transformationAttributeConfigurationService = transformationAttributeConfigurationService;
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetTransformationAttributesMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<List<TransformationAttributeDTO>> getTransformationAttributes(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey, @ApiParam(value = "Logical Schema Key", required = true) @PathVariable("logicalSchemaKey") String logicalSchemaKey, @ApiParam(value = "Logical Entity Key", required = true) @PathVariable("logicalEntityKey") String logicalEntityKey) {
        List<TransformationAttributeDTO> result = transformationAttributeConfigurationService.list(projectKey, logicalSchemaKey, logicalEntityKey);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<TransformationAttributeDTO>> getAvailableAttributes(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                                   @ApiParam(value = "Logical Schema Key",required=true) @PathVariable("logicalSchemaKey") String logicalSchemaKey,
                                                                                   @ApiParam(value = "Logical Entity Key",required=true) @PathVariable("logicalEntityKey") String logicalEntityKey) {
        List<TransformationAttributeDTO> result = transformationAttributeConfigurationService.getAvailableAttributes(projectKey, logicalSchemaKey, logicalEntityKey);
        return ResponseEntity.ok(result);
    }
}