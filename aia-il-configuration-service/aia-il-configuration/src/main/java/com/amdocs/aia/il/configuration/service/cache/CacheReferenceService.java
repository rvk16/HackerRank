package com.amdocs.aia.il.configuration.service.cache;

import com.amdocs.aia.il.configuration.dto.CacheReferenceEntityDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.service.ConfigurationService;

import java.util.List;

public interface CacheReferenceService extends ConfigurationService<CacheReferenceEntityDTO> {
    CacheReferenceEntityDTO get(String projectKey, String cacheReferenceEntityKey);

    List<CacheReferenceEntityDTO> list(String projectKey);

    CacheReferenceEntityDTO update(String projectKey, String cacheReferenceEntityKey, CacheReferenceEntityDTO cacheReferenceEntityDTO);

    CacheReferenceEntityDTO save(String projectKey, CacheReferenceEntityDTO cacheReferenceEntityDTO);

    void delete(String projectKey, String cacheReferenceEntityKey);

    SaveElementsResponseDTO bulkSave(String projectKey, List<CacheReferenceEntityDTO> s);
}
