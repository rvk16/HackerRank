package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.EntityTransformationDTO;
import com.amdocs.aia.il.configuration.service.CachedEntityTransformationService;
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

@RestController
@Api(tags = {"CachedEntityTransformation"})
public class CachedEntityTransformationApiController implements CachedEntityTransformationApi {

    CachedEntityTransformationService service;

    @Autowired
    CachedEntityTransformationApiController(final CachedEntityTransformationService cachedEntityTransformationService){
        this.service = cachedEntityTransformationService;
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddCachedEntityTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<EntityTransformationDTO> addCachedEntityTransformation(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                                 @ApiParam(value = "Entity Transformation" ,required=true )  @Valid @RequestBody EntityTransformationDTO entityTransformation) {

        return new ResponseEntity<>(service.create(projectKey, entityTransformation), HttpStatus.CREATED);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleDeleteCacheEntityTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Void> deleteCacheEntityTransformation(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                @ApiParam(value = "Cache Reference Entity Key",required=true) @PathVariable("cacheReferenceEntityKey") String cacheReferenceEntityKey) {
        service.delete(projectKey, cacheReferenceEntityKey);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleUpdateCachedEntityTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<EntityTransformationDTO> updateCachedEntityTransformation(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                                    @ApiParam(value = "Entity Transformation" ,required=true )  @Valid @RequestBody EntityTransformationDTO entityTransformation) {
        return new ResponseEntity<>(service.update(projectKey, entityTransformation), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetCacheEntityTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<EntityTransformationDTO> getCacheEntityTransformation(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                                @ApiParam(value = "Cache Reference Entity Key",required=true) @PathVariable("cacheReferenceEntityKey") String cacheReferenceEntityKey) {
        return new ResponseEntity<>(service.get(projectKey, cacheReferenceEntityKey), HttpStatus.OK);
    }
}
