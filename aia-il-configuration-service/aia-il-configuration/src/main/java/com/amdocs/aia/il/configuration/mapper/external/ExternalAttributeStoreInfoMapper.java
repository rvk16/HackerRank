package com.amdocs.aia.il.configuration.mapper.external;

import com.amdocs.aia.il.common.model.external.ExternalAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalEntityStoreInfo;
import com.amdocs.aia.il.configuration.dto.ExternalAttributeStoreInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityStoreInfoDTO;

public interface ExternalAttributeStoreInfoMapper<M extends ExternalAttributeStoreInfo, D extends ExternalAttributeStoreInfoDTO> extends ExternalModelTypeSpecificMapper {
    M toModel(D dto);
    D toDTO(M model);
}
