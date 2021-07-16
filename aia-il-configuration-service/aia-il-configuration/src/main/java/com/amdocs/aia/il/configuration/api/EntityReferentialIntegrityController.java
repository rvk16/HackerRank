package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.EntityReferentialIntegrityDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.service.EntityReferentialIntegrityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = {"EntityReferentialIntegrity"})
public class EntityReferentialIntegrityController implements EntityReferentialIntegrityApi {

    private final EntityReferentialIntegrityService entityReferentialIntegrityService;

    @Autowired
    public EntityReferentialIntegrityController(EntityReferentialIntegrityService entityReferentialIntegrityService){
        this.entityReferentialIntegrityService = entityReferentialIntegrityService;
    }


    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddEntityReferentialIntegrityMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<EntityReferentialIntegrityDTO> addEntityReferentialIntegrity(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                                 @ApiParam(value = "The new Entity Referential Integrity" ,required=true )  @Valid @RequestBody EntityReferentialIntegrityDTO referentialIntegrity) {
        EntityReferentialIntegrityDTO entityReferentialIntegrityDTO = entityReferentialIntegrityService.save(projectKey, referentialIntegrity);
        return ResponseEntity.status(HttpStatus.CREATED).body(entityReferentialIntegrityDTO);
    }


    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddEntitiesReferentialIntegrityMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<SaveElementsResponseDTO> addEntitiesReferentialIntegrity(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey, @ApiParam(value = "The new EntitiesReferentialIntegrity" ,required=true )  @Valid @RequestBody List<EntityReferentialIntegrityDTO> entitiesReferentialIntegrity) {
        SaveElementsResponseDTO ret = entityReferentialIntegrityService.bulkSave(projectKey, entitiesReferentialIntegrity);
        return ResponseEntity.status(HttpStatus.CREATED).body(ret);
    }
}
