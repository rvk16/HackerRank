package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.service.TransformationsScriptsService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Api(tags = {"Export"})
public class ExportApiController implements ExportApi {

    private final TransformationsScriptsService transformationsScriptsService;

    @Inject
    public ExportApiController(final TransformationsScriptsService transformationsScriptsService) {
        this.transformationsScriptsService = transformationsScriptsService;
    }
    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleExportTransformationsScriptsMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Resource> exportTransformationsScripts(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String timestamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        String filename = String.format("transformations_sql_files_%s.zip", timestamp);
        headers.setContentDispositionFormData("attachment", filename);
        byte[] bytes = transformationsScriptsService.exportTransformationsScripts(projectKey);
        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(bytes));
        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }
}
