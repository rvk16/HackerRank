package com.amdocs.aia.il.configuration.mapper.external;

import com.amdocs.aia.il.common.model.external.ExternalEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.configuration.dto.ExternalEntityCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaDTO;

public interface ExternalEntityCollectionRulesMapper<M extends ExternalEntityCollectionRules, D extends ExternalEntityCollectionRulesDTO> extends ExternalModelTypeSpecificMapper {
    M toModel(D dto, ExternalSchema externalSchema);
    D toDTO(M model);
}
