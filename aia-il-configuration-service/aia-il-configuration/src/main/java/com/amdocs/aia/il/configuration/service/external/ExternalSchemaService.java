package com.amdocs.aia.il.configuration.service.external;

import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.service.ConfigurationService;
import com.amdocs.aia.common.model.repo.SaveElementsResponse;
import java.util.List;

public interface ExternalSchemaService extends ConfigurationService<ExternalSchemaDTO> {
    ExternalSchemaDTO get(String projectKey, String externalSchemaKey);

    List<ExternalSchemaDTO> list(String projectKey);

    ExternalSchemaDTO update(String projectKey, String externalSchemaKey, ExternalSchemaDTO externalSchemaDTO);

    ExternalSchemaDTO save(String projectKey, ExternalSchemaDTO externalSchemaDTO);

    List<ExternalSchemaTypeInfoDTO> listExternalSchemaTypes(String projectKey);

    ExternalSchemaTypeInfoDTO getExternalSchemaType(String projectKey, String externalSchemaType);

    List<CollectionChannelTypeInfoDTO> listCollectionChannelTypes(String projectKey);

    List<TypeSystemInfoDTO> listTypeSystems(String projectKey);

    void delete(String projectKey, String externalSchemaKeykey);

    SaveElementsResponseDTO bulkSave(final String projectKey, final List<ExternalSchemaDTO> externalSchemaDTOs);

    ExternalSchema saveModel(com.amdocs.aia.il.common.model.external.ExternalSchema model, boolean validate);

    void getByKey(String projectKey, String externalSchemaKey);

}
