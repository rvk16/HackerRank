package com.amdocs.aia.il.sqlite.api;

import com.amdocs.aia.il.sqlite.dto.QueryDTO;
import com.amdocs.aia.il.sqlite.dto.ResultSetDTO;
import com.amdocs.aia.il.sqlite.service.SqliteRestService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Api(tags = {"SQLite"})
public class SqLiteApiController implements SqLiteApi {

    private final SqliteRestService sqliteRestService;

    @Autowired
    public SqLiteApiController(final SqliteRestService sqliteRestService) {
        this.sqliteRestService = sqliteRestService;
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleQueryMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<ResultSetDTO> query(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey,
                                              @ApiParam(value = "sql select query", required = true) @Valid @RequestBody QueryDTO query) {
        return new ResponseEntity<>(sqliteRestService.executeQuery(projectKey, query.getQuery()), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleExecuteMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Void> execute(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey,
                                        @ApiParam(value = "sql update query", required = true) @Valid @RequestBody QueryDTO query) {
        sqliteRestService.executeUpdate(projectKey, query.getQuery());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleExportDatabaseMethod'),@environment.getProperty('com.amdocs.msnext.securitya3s.service.roles.roleGateway'))")
    public ResponseEntity<Resource> exportDatabase(@ApiParam(value = "The project key", required = true) @PathVariable("projectKey") String projectKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String timestamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        String filename = String.format("sqlite_backup_%s.db", timestamp);
        headers.setContentDispositionFormData("attachment", filename);
        InputStreamResource inputStreamResource = sqliteRestService.exportDatabase(projectKey);
        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }

}
