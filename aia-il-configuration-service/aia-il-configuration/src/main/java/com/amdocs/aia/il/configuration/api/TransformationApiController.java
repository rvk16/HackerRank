package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.dto.TransformationDTO;
import com.amdocs.aia.il.configuration.service.TransformationConfigurationService;
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
@Api(tags = {"Transformation"})
public class TransformationApiController implements TransformationApi {

    private final TransformationConfigurationService transformationService;

    @Inject
    public TransformationApiController(final TransformationConfigurationService transformationService) {
        this.transformationService = transformationService;
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<TransformationDTO> addTransformation(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey,
                                                               @ApiParam(value = "Transformation to be created", required = true) @Valid @RequestBody TransformationDTO transformation) {
        TransformationDTO returnedPublisherTransformation = transformationService.save(projectKey, transformation);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnedPublisherTransformation);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGetTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<TransformationDTO> getTransformation(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey,
                                                               @ApiParam(value = "Context to be fetched", required = true) @PathVariable("id") String id) {
        TransformationDTO publisherTransformation = transformationService.get(projectKey, id);
        return ResponseEntity.ok(publisherTransformation);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleUpdateTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<TransformationDTO> updateTransformation(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey,
                                                                  @ApiParam(value = "Context to be fetched", required = true) @PathVariable("id") String id,
                                                                  @ApiParam(value = "Transformation to be updated", required = true) @Valid @RequestBody TransformationDTO transformation) {
        TransformationDTO updatedPublisherTransformation = transformationService.update(projectKey, id, transformation);
        return ResponseEntity.ok(updatedPublisherTransformation);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleListTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Object> listTransformations(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey) {
        List<TransformationDTO> publisherTransformationList = transformationService.list(projectKey);
        return ResponseEntity.ok(publisherTransformationList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleDeleteTransformationMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Void> deleteTransformation(@ApiParam(value = "Context to be fetched", required = true) @PathVariable("id") String id,
                                                     @ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey) {
        transformationService.delete(projectKey, id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleAddTransformationsMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<SaveElementsResponseDTO> addTransformations(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey, @ApiParam(value = "Transformations to be created" ,required=true )  @Valid @RequestBody List<TransformationDTO> transformations) {
        SaveElementsResponseDTO ret = transformationService.bulkSave(projectKey, transformations);
        return ResponseEntity.status(HttpStatus.CREATED).body(ret);
    }
}