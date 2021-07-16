package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.CacheReferenceEntityDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.service.cache.CacheReferenceService;
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
@Api(tags = {"CacheReference"})
public class CacheReferenceController implements CacheReferenceApi {

    private final CacheReferenceService cacheReferenceService;

    @Autowired
    public CacheReferenceController(CacheReferenceService cacheReferenceService){
        this.cacheReferenceService = cacheReferenceService;
    }


    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddCacheReferenceEntityMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<CacheReferenceEntityDTO> addCacheReferenceEntity(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                           @ApiParam(value = "The new Cache Reference Entity" ,required=true )  @Valid @RequestBody CacheReferenceEntityDTO cacheReferenceEntity) {
        CacheReferenceEntityDTO cacheReferenceEntityDTO = cacheReferenceService.save(projectKey, cacheReferenceEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(cacheReferenceEntityDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetCacheReferenceEntitiesMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<List<CacheReferenceEntityDTO>> getCacheReferenceEntities(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey) {
        List<CacheReferenceEntityDTO> cacheReferenceEntityDTOList = cacheReferenceService.list(projectKey);
        return ResponseEntity.ok(cacheReferenceEntityDTOList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetCacheReferenceEntityByKeyMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<CacheReferenceEntityDTO> getCacheReferenceEntityByKey(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                                @ApiParam(value = "Cache Reference Entity Key",required=true) @PathVariable("cacheReferenceEntityKey") String cacheReferenceEntityKey) {
        CacheReferenceEntityDTO cacheReferenceEntityDTO = cacheReferenceService.get(projectKey, cacheReferenceEntityKey);
        return ResponseEntity.ok(cacheReferenceEntityDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleUpdateCacheReferenceEntityMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<CacheReferenceEntityDTO> updateCacheReferenceEntity(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                                              @ApiParam(value = "Cache Reference Entity Key",required=true) @PathVariable("cacheReferenceEntityKey") String cacheReferenceEntityKey,
                                                                              @ApiParam(value = "Cache Reference Entity" ,required=true )  @Valid @RequestBody CacheReferenceEntityDTO cacheReferenceEntity) {
        CacheReferenceEntityDTO cacheReferenceEntityDTO = cacheReferenceService.update(projectKey, cacheReferenceEntityKey, cacheReferenceEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(cacheReferenceEntityDTO);
    }

    @Override
    public ResponseEntity<Void> deleteCacheReferenceEntity(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,
                                                           @ApiParam(value = "Cache Reference Entity Key",required=true) @PathVariable("cacheReferenceEntityKey") String cacheReferenceEntityKey) {
        cacheReferenceService.delete(projectKey, cacheReferenceEntityKey);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddCacheReferenceEntitiesMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<SaveElementsResponseDTO> addCacheReferenceEntities(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The new Cache Reference Entity" ,required=true )  @Valid @RequestBody List<CacheReferenceEntityDTO> cacheReferenceEntities) {
        SaveElementsResponseDTO ret = cacheReferenceService.bulkSave(projectKey, cacheReferenceEntities);
        return ResponseEntity.status(HttpStatus.CREATED).body(ret);
    }
}
