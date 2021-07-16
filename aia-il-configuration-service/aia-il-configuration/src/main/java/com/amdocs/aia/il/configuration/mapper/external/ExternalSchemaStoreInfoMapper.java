package com.amdocs.aia.il.configuration.mapper.external;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreInfo;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaStoreInfoDTO;

public interface ExternalSchemaStoreInfoMapper<M extends ExternalSchemaStoreInfo, D extends ExternalSchemaStoreInfoDTO> extends ExternalModelTypeSpecificMapper {
    M toModel(D dto);
    D toDTO(M model);
}
