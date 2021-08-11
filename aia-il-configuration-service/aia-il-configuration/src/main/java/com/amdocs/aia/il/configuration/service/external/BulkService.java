package com.amdocs.aia.il.configuration.service.external;

import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.service.ConfigurationService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BulkService  {
     InputStreamResource exportExternalSchemasToZIP(String projectKey);
     BulkImportResponseDTO importExternalSchemasFromZIP(final String projectKey, final MultipartFile file);

}
