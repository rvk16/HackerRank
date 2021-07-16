package com.amdocs.aia.il.configuration.service;


import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.dto.TransformationDTO;

import java.util.List;


public interface TransformationConfigurationService extends ConfigurationService<TransformationDTO> {
    TransformationDTO findByTargetSchemaStoreAndTargetEntityStore(String projectKey, String targetSchemaStoreKey, String targetEntityStoreKey, String key);

    void deleteEntityTransformations(String projectKey, String targetSchemaStoreKey, String targetEntityStoreKey);

    SaveElementsResponseDTO bulkSave(String projectKey, List<TransformationDTO> s);

    List<TransformationDTO> findByLogicalEntity(String projectKey, String logicalSchema, String logicalEntity);
}
