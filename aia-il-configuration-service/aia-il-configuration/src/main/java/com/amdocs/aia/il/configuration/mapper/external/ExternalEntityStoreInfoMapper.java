package com.amdocs.aia.il.configuration.mapper.external;

import com.amdocs.aia.il.common.model.external.ExternalEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.configuration.dto.ExternalEntityStoreInfoDTO;

public interface ExternalEntityStoreInfoMapper<M extends ExternalEntityStoreInfo, D extends ExternalEntityStoreInfoDTO> extends ExternalModelTypeSpecificMapper {
    M toModel(D dto, ExternalSchema schema);

    D toDTO(M model);
}
