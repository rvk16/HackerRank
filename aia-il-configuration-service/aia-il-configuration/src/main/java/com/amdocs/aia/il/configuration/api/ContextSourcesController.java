package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.BaseEntityDTO;
import com.amdocs.aia.il.configuration.dto.ContextSourceDTO;
import com.amdocs.aia.il.configuration.dto.EntitiesDTO;
import com.amdocs.aia.il.configuration.service.context.ContextSourcesService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ContextSourcesController implements ContextSourcesApi{

    private final ContextSourcesService service;

    @Autowired
    public ContextSourcesController(ContextSourcesService service) {
        this.service = service;
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetContextSourcesMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<List<ContextSourceDTO>> getContextSources(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                    @ApiParam(value = "Schema type",required=false) @Valid @RequestParam(value = "schemaType", required = false) String schemaType) {
        List<ContextSourceDTO> contextSources = service.getContextSources(projectKey, schemaType);
        return ResponseEntity.ok(contextSources);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetContextEntitiesMetadataMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<List<BaseEntityDTO>>  searchContextEntitiesMetadata(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                              @ApiParam(value = "context entities ref" ,required=true )  @Valid @RequestBody EntitiesDTO entities) {

        List<BaseEntityDTO> contextEntitiesMetadata = service.searchContextEntitiesMetadata(projectKey, entities);
        return ResponseEntity.ok(contextEntitiesMetadata);
    }
}
