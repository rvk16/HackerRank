package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.BulkGroupDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.dto.SetFiltersRequestDTO;
import com.amdocs.aia.il.configuration.service.physical.sql.BulkGroupService;
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
@Api(tags = {"BulkGroups"})
public class BulkGroupsApiController implements BulkGroupsApi {

    private final BulkGroupService bulkGroupService;

    @Inject
    public BulkGroupsApiController(final BulkGroupService bulkGroupService) {
        this.bulkGroupService = bulkGroupService;
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddBulkGroupMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<BulkGroupDTO> addBulkGroup(@ApiParam(required = true) @PathVariable("projectKey") String projectKey,
                                                     @ApiParam(required = true) @PathVariable("schemaStoreKey") String schemaStoreKey,
                                                     @ApiParam(required = true) @Valid @RequestBody BulkGroupDTO bulkGroup) {
        log.info("addBulkGroup project {} schema {} addBulkGroup {}",projectKey,schemaStoreKey,bulkGroup);
        BulkGroupDTO BulkGroupDTO = bulkGroupService.save(projectKey, bulkGroup);
        return ResponseEntity.status(HttpStatus.CREATED).body(BulkGroupDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleDeleteBulkGroupMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Void> deleteBulkGroup(@ApiParam(required = true) @PathVariable("projectKey") String projectKey,
                                                @ApiParam(required = true) @PathVariable("schemaStoreKey")  String schemaStoreKey,
                                                @ApiParam(required = true) @PathVariable("bulkGroupKey") String bulkGroupKey) {
        bulkGroupService.delete(projectKey, schemaStoreKey, bulkGroupKey);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetBulkGroupByKeyMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<BulkGroupDTO> getBulkGroupByKey(@ApiParam(required = true) @PathVariable("projectKey") String projectKey,
                                                          @ApiParam(required = true) @PathVariable("schemaStoreKey") String schemaStoreKey,
                                                          @ApiParam(required = true) @PathVariable("bulkGroupKey") String bulkGroupKey) {
        BulkGroupDTO physicalEntityStoreDTO = bulkGroupService.get(projectKey, schemaStoreKey, bulkGroupKey);
        return ResponseEntity.ok(physicalEntityStoreDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetBulkGroupsMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Object> getBulkGroups(@ApiParam(required = true) @PathVariable("projectKey") String projectKey,
                                                @ApiParam(required = true) @PathVariable("schemaStoreKey") String schemaStoreKey) {
        List<BulkGroupDTO> physicalEntityStoreDTOList = bulkGroupService.list(projectKey, schemaStoreKey);
        return ResponseEntity.ok(physicalEntityStoreDTOList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleUpdateBulkGroupMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<BulkGroupDTO> updateBulkGroup(@ApiParam(required = true) @PathVariable("projectKey") String projectKey,
                                                        @ApiParam(required = true) @PathVariable("schemaStoreKey") String schemaStoreKey,
                                                        @ApiParam(required = true) @PathVariable("bulkGroupKey") String bulkGroupKey,
                                                        @ApiParam(required = true) @Valid @RequestBody BulkGroupDTO bulkGroup) {
        bulkGroup.setBulkGroupKey(bulkGroupKey);
        BulkGroupDTO bulkGroupDTO = bulkGroupService.update(projectKey, schemaStoreKey, bulkGroup);
        return ResponseEntity.status(HttpStatus.CREATED).body(bulkGroupDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddBulkGroupsMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<SaveElementsResponseDTO> addBulkGroups(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey, @ApiParam(value = "The new Bulk groups" ,required=true )  @Valid @RequestBody List<BulkGroupDTO> bulkGroups) {
        SaveElementsResponseDTO saveElementsResponseDTO = bulkGroupService.bulkSave(projectKey, bulkGroups);

        return ResponseEntity.status(HttpStatus.CREATED).body(saveElementsResponseDTO);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleUpdateBulkGroupMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<SetFiltersRequestDTO> updateBulkGroupEntityFilters(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey, @ApiParam(value = "Schema Store Key",required=true) @PathVariable("schemaStoreKey") String schemaStoreKey, @ApiParam(value = "Bulk Group Key",required=true) @PathVariable("bulkGroupKey") String bulkGroupKey, @ApiParam(value = "Request containing all entities with filters and the bulk group with list of filter references" ,required=true )  @Valid @RequestBody SetFiltersRequestDTO setFiltersRequest){
        setFiltersRequest.getBulkGroup().setBulkGroupKey(bulkGroupKey);
        SetFiltersRequestDTO rspDTO =  bulkGroupService.updateFilters(projectKey, schemaStoreKey, bulkGroupKey, setFiltersRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(rspDTO);
    }
}