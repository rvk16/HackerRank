package com.amdocs.aia.il.configuration.mapper.external.sql;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlAttributeStoreInfo;
import com.amdocs.aia.il.configuration.dto.ExternalAttributeStoreInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSqlAttributeStoreInfoDTO;
import com.amdocs.aia.il.configuration.mapper.external.ExternalAttributeStoreInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalSqlAttributeStoreInfoMapper implements ExternalAttributeStoreInfoMapper<ExternalSqlAttributeStoreInfo, ExternalSqlAttributeStoreInfoDTO> {
    @Override
    public ExternalSqlAttributeStoreInfo toModel(ExternalSqlAttributeStoreInfoDTO dto) {
        return new ExternalSqlAttributeStoreInfo();
    }

    @Override
    public ExternalSqlAttributeStoreInfoDTO toDTO(ExternalSqlAttributeStoreInfo model) {
        return (ExternalSqlAttributeStoreInfoDTO) new ExternalSqlAttributeStoreInfoDTO().storeType(ExternalAttributeStoreInfoDTO.StoreTypeEnum.SQL);
    }

    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.SQL;
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalSqlAttributeStoreInfoDTO.StoreTypeEnum.SQL;
    }
}
