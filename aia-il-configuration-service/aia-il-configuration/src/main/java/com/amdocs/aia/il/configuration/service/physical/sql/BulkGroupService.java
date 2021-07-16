package com.amdocs.aia.il.configuration.service.physical.sql;

import com.amdocs.aia.il.configuration.dto.BulkGroupDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.dto.SetFiltersRequestDTO;
import com.amdocs.aia.il.configuration.service.ConfigurationService;

import java.util.List;

/**
 * Bulk group service.
 *
 * @author LIATD
 */
public interface BulkGroupService extends ConfigurationService<BulkGroupDTO> {
    BulkGroupDTO get(String projectKey, String schemaStoreKey, String bulkGroupKey);

    void delete(String projectKey, String schemaStoreKey, String bulkGroupKey);

    List<BulkGroupDTO> list(String projectKey, String schemaStoreKey);

    SaveElementsResponseDTO bulkSave(String projectKey, List<BulkGroupDTO> s);

    SetFiltersRequestDTO updateFilters(String projectKey, String schemaStoreKey, String bulkGroupKey, SetFiltersRequestDTO setFiltersRequest);
}
