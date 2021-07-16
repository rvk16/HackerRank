package com.amdocs.aia.il.configuration.mapper.external.sql;

import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlEntityStoreInfo;
import com.amdocs.aia.il.configuration.dto.ExternalEntityStoreInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSqlEntityStoreInfoDTO;
import com.amdocs.aia.il.configuration.mapper.external.ExternalEntityStoreInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalSqlEntityStoreInfoMapper implements ExternalEntityStoreInfoMapper<ExternalSqlEntityStoreInfo, ExternalSqlEntityStoreInfoDTO> {
    @Override
    public ExternalSqlEntityStoreInfo toModel(ExternalSqlEntityStoreInfoDTO dto, ExternalSchema schema) {
        return new ExternalSqlEntityStoreInfo();
    }

    @Override
    public ExternalSqlEntityStoreInfoDTO toDTO(ExternalSqlEntityStoreInfo model) {
        return (ExternalSqlEntityStoreInfoDTO)new ExternalSqlEntityStoreInfoDTO()
                .storeType(ExternalEntityStoreInfoDTO.StoreTypeEnum.SQL);
    }

    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.SQL;
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalSqlEntityStoreInfoDTO.StoreTypeEnum.SQL;
    }
}
