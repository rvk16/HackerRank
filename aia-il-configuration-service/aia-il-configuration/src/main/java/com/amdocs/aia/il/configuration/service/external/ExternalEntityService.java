package com.amdocs.aia.il.configuration.service.external;

import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.configuration.dto.ExternalEntityDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.service.ConfigurationService;

import java.util.List;

public interface ExternalEntityService extends ConfigurationService<ExternalEntityDTO> {

    ExternalEntityDTO save(String projectKey, String externalSchemaKey, ExternalEntityDTO externalEntityDTO);

    List<ExternalEntityDTO> list(String projectKey, String externalSchemaKey);

    List<ExternalEntityDTO> listAll(String projectKey);

    ExternalEntityDTO get(String projectKey, String schemaKey, String entityKey);

    ExternalEntityDTO update(String projectKey, String externalSchemaKey, ExternalEntityDTO externalEntityDTO);

    void delete(String projectKey, String externalSchemaKey,String externalEntityKey);


    SaveElementsResponseDTO bulkSave(final String projectKey, final List<ExternalEntityDTO> externalEntitiesDTOs);

    ExternalEntity saveModel(com.amdocs.aia.il.common.model.external.ExternalEntity model, boolean validate);

    void getByKey(String projectKey, String externalSchemaKey, String entityKey);

}
