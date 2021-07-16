package com.amdocs.aia.il.configuration.mapper.external;

import com.amdocs.aia.il.common.model.external.ExternalSchemaCollectionRules;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaCollectionRulesDTO;

public interface ExternalSchemaCollectionRulesMapper<M extends ExternalSchemaCollectionRules, D extends ExternalSchemaCollectionRulesDTO> extends ExternalModelTypeSpecificMapper {
    M toModel(D dto);
    D toDTO(M model);
}
