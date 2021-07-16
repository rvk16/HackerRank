package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.EntityTransformationDTO;
import com.amdocs.aia.il.configuration.dto.SharedEntityTransformationGridElementDTO;
import com.amdocs.aia.il.configuration.service.SharedEntityTransformationService;
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
@Api(tags = {"SharedEntityTransformation"})
public class SharedEntityTransformationApiController implements SharedEntityTransformationApi {

    SharedEntityTransformationService service;

    @Autowired
    SharedEntityTransformationApiController(final SharedEntityTransformationService service) {
        this.service = service;
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddSharedEntityTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<EntityTransformationDTO> addSharedEntityTransformation(@ApiParam(value = "The project key", required=true)
                                                                                             @PathVariable("projectKey") String projectKey,
                                                                                 @ApiParam(value = "Entity Transformation", required=true)
                                                                                         @Valid @RequestBody EntityTransformationDTO entityTransformation) {
        return new ResponseEntity<>(service.create(projectKey, entityTransformation), HttpStatus.CREATED);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleDeleteSharedEntityTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Void> deleteSharedEntityTransformation(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                 @ApiParam(value = "Logical Entity Key",required=true) @PathVariable("logicalEntityKey") String logicalEntityKey) {
        service.delete(projectKey, logicalEntityKey);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleUpdateSharedEntityTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<EntityTransformationDTO> updateSharedEntityTransformation(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "Entity Transformation" ,required=true )  @Valid @RequestBody EntityTransformationDTO entityTransformation) {
        return new ResponseEntity<>(service.update(projectKey, entityTransformation), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetSharedEntityTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<EntityTransformationDTO> getSharedEntityTransformation(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                                 @ApiParam(value = "Logical Entity Key",required=true) @PathVariable("logicalEntityKey") String logicalEntityKey) {
        return new ResponseEntity<>(service.get(projectKey, logicalEntityKey), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.rolegetSharedEntityTransformationListMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<List<SharedEntityTransformationGridElementDTO>> getSharedEntityTransformationList(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey) {
        List<SharedEntityTransformationGridElementDTO> entityTransformationDTOList = service.list(projectKey);
        return ResponseEntity.ok(entityTransformationDTOList);
    }
}