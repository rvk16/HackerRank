package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.ContextDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.service.ContextConfigurationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
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
@Api(tags = {"Context"})
public class ContextApiController implements ContextApi {

    private final ContextConfigurationService contextConfigurationService;

    @Inject
    public ContextApiController(final ContextConfigurationService contextConfigurationService) {
        this.contextConfigurationService = contextConfigurationService;
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddContextMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<ContextDTO> addContext(
            @ApiParam(required = true) @PathVariable("projectKey") String projectKey,
            @ApiParam(required = true) @Valid @RequestBody ContextDTO context) {
        ContextDTO contextDTO = contextConfigurationService.save(projectKey, context);
        return ResponseEntity.status(HttpStatus.CREATED).body(contextDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetContextMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<ContextDTO> getContext(
            @ApiParam(required = true) @PathVariable("projectKey") String projectKey,
            @ApiParam(required = true) @PathVariable("id") String id) {
        ContextDTO publisherContextDTO = contextConfigurationService.get(projectKey, id);
        return ResponseEntity.ok(publisherContextDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleUpdateContextMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<ContextDTO> updateContext(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey,
                                                    @ApiParam(value = "context to be fetched", required = true) @PathVariable("id") String id,
                                                    @ApiParam(value = "context to be updated", required = true) @Valid @RequestBody ContextDTO context) {
        ContextDTO updatedPublisherContext = contextConfigurationService.update(projectKey, id, context);
        return ResponseEntity.ok(updatedPublisherContext);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleListContextMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Object> listContexts(
            @ApiParam(value = "The project key", required = true)
            @PathVariable("projectKey") String projectKey) {
        List<ContextDTO> publisherContextsList = contextConfigurationService.list(projectKey);
        return ResponseEntity.ok(publisherContextsList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleDeleteContextMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Void> deleteContext(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey,
                                              @ApiParam(value = "context to be fetched", required = true) @PathVariable("id") String id) {
        contextConfigurationService.delete(projectKey, id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.rolegetcontextRelationTypesListMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Object> getRelationTypesList(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey) {
        List<String> ContextRelationTypesList = contextConfigurationService.relationTypesList();
        return ResponseEntity.ok(ContextRelationTypesList);
    }


    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddContextsMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<SaveElementsResponseDTO> addContexts(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "contexts to be created" ,required=true )  @Valid @RequestBody List<ContextDTO> context) {
        SaveElementsResponseDTO ret = contextConfigurationService.bulkSave(projectKey, context);
        return ResponseEntity.status(HttpStatus.CREATED).body(ret);
    }
}