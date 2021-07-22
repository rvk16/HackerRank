package com.amdocs.aia.il.configuration.api;

import com.amdocs.aia.il.configuration.service.external.BulkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Api(tags = {"Bulk"})
public class BulkApiController implements BulkApi {

    private final BulkService bulkService;
    private static final String EXTERNAL_SCHEMA = "ExternalSchema";
    private static final String EXTERNAL_SCHEMA_EXPORT_FILE_NAME = "external_schemas_export_%s.zip";

    @Autowired
    public BulkApiController(BulkService bulkService) {
        this.bulkService = bulkService;
    }

    @Override
   @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleExportExternalSchemasMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Resource> exportExternalSchemas(@ApiParam(value = "The project key",required=true) @PathVariable("projectKey") String projectKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String timestamp =  new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        String filename = String.format(EXTERNAL_SCHEMA_EXPORT_FILE_NAME, timestamp);
        headers.setContentDispositionFormData("attachment", filename);
        InputStreamResource inputStreamResource = bulkService.exportExternalSchemasToZIP(projectKey);
        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }
}
