package com.amdocs.aia.il.configuration.service;


import com.amdocs.aia.common.model.repo.SaveElementsResponse;
import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;
import com.amdocs.aia.il.configuration.dto.EntityReferentialIntegrityDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;

import java.util.List;
import java.util.Optional;

public interface EntityReferentialIntegrityService {

    Optional<EntityReferentialIntegrity> get(final String projectKey, final String schemaStoreKey, final String entityStoreKey);

    EntityReferentialIntegrity createOrUpdate(final EntityReferentialIntegrity model);

    EntityReferentialIntegrityDTO save(String projectKey, EntityReferentialIntegrityDTO entityReferentialIntegrityDTO);

    SaveElementsResponseDTO bulkSave(String projectKey, List<EntityReferentialIntegrityDTO> entitiesReferentialIntegrity);


}