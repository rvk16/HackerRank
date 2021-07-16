package com.amdocs.aia.il.configuration.mapper.external.sql;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlEntityCollectionRules;
import com.amdocs.aia.il.configuration.dto.ExternalEntityCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSqlEntityCollectionRulesDTO;
import com.amdocs.aia.il.configuration.mapper.external.AbstractExternalEntityCollectionRulesInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalSqlEntityCollectionRulesMapper extends AbstractExternalEntityCollectionRulesInfoMapper<ExternalSqlEntityCollectionRules, ExternalSqlEntityCollectionRulesDTO> {
    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.SQL;
    }

    @Override
    public ExternalSqlEntityCollectionRulesDTO toDTO(ExternalSqlEntityCollectionRules model) {
        return (ExternalSqlEntityCollectionRulesDTO)super.toDTO(model).storeType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.SQL);
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalSqlEntityCollectionRulesDTO.StoreTypeEnum.SQL;
    }
}
