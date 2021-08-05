package com.amdocs.aia.il.configuration.service.external;

import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.service.ConfigurationService;
import org.springframework.core.io.InputStreamResource;

import java.util.List;

public interface BulkService  {
     InputStreamResource exportExternalSchemasToZIP(String projectKey);
}
