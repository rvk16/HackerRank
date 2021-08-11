package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.il.configuration.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface DiscoveryService {
    AsyncResponseDTO discoverExternalCsvAsync(String projectKey, DiscoverExternalCsvRequestDTO discoverExternalCsvRequestDTO);

    AsyncResponseDTO discoverExternalJsonAsync(String projectKey, DiscoverExternalJsonRequestDTO discoverExternalJsonRequestDTO);

    UploadDiscoveryFileResponseDTO uploadDiscoveryFile(MultipartFile file);

    void discoverExternalSchema(String projectKey, SchemaDiscoveryRequestDTO discoveryRequest);

    AsyncResponseDTO discoverExternalSqlAsync(String projectKey, DiscoverExternalSqlRequestDTO discoverExternalSqlRequest);
}
