package com.amdocs.aia.il.configuration.mapper.external.sql;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaCollectionRules;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSqlSchemaCollectionRulesDTO;
import com.amdocs.aia.il.configuration.mapper.external.AbstractExternalSchemaCollectionRulesInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalSqlSchemaCollectionRulesMapper extends AbstractExternalSchemaCollectionRulesInfoMapper<ExternalSqlSchemaCollectionRules, ExternalSqlSchemaCollectionRulesDTO> {
    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.SQL;
    }

    @Override
    public ExternalSqlSchemaCollectionRulesDTO toDTO(ExternalSqlSchemaCollectionRules model) {
        return (ExternalSqlSchemaCollectionRulesDTO)super.toDTO(model).storeType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.SQL);
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalSqlSchemaCollectionRulesDTO.StoreTypeEnum.SQL;
    }
}
