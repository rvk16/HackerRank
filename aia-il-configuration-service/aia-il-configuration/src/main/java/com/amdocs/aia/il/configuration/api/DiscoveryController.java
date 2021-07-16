package com.amdocs.aia.il.configuration.api;


import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.service.DiscoveryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = {"Discovery"})
public class DiscoveryController implements DiscoveryApi {

    private final DiscoveryService discoveryService;

    public DiscoveryController(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleDiscoverExternalJson'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<AsyncResponseDTO> discoverExternalJsonAsync(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The discovery external Json request details" ,required=true )  @Valid @RequestBody DiscoverExternalJsonRequestDTO discoverExternalJsonRequest) {
        AsyncResponseDTO asyncResponseDTO=discoveryService.discoverExternalJsonAsync(projectKey,discoverExternalJsonRequest);
        return new ResponseEntity<>(asyncResponseDTO, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleDiscoverExternalCsv'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<AsyncResponseDTO> discoverExternalCsvAsync(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The discovery external Csv request details" ,required=true )  @Valid @RequestBody DiscoverExternalCsvRequestDTO discoverExternalCsvRequest) {
        AsyncResponseDTO asyncResponseDTO=discoveryService.discoverExternalCsvAsync(projectKey,discoverExternalCsvRequest);
        return new ResponseEntity<>(asyncResponseDTO, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleUploadDiscoveryFile'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<UploadDiscoveryFileResponseDTO> uploadDiscoveryFile(String projectKey, @Valid MultipartFile file) {
        UploadDiscoveryFileResponseDTO uploadDiscoveryFileResponseDTO=discoveryService.uploadDiscoveryFile(file);
        return new ResponseEntity<>(uploadDiscoveryFileResponseDTO, HttpStatus.CREATED);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleDiscoverExternalSchema'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Void> discoverExternalSchema(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey,@ApiParam(value = "The discovery request details" ,required=true )  @Valid @RequestBody SchemaDiscoveryRequestDTO discoveryRequest) {
        discoveryService.discoverExternalSchema(projectKey, discoveryRequest);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }
}
