package com.amdocs.aia.il.configuration.mapper.external.sql;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaStoreInfo;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaStoreInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSqlSchemaStoreInfoDTO;
import com.amdocs.aia.il.configuration.mapper.external.ExternalSchemaStoreInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalSqlSchemaStoreInfoMapper implements ExternalSchemaStoreInfoMapper<ExternalSqlSchemaStoreInfo, ExternalSqlSchemaStoreInfoDTO> {
    @Override
    public ExternalSqlSchemaStoreInfo toModel(ExternalSqlSchemaStoreInfoDTO dto) {
        final ExternalSqlSchemaStoreInfo sqlInfo = new ExternalSqlSchemaStoreInfo();
        sqlInfo.setDatabaseType(dto.getDatabaseType());
        return sqlInfo;

    }

    @Override
    public ExternalSqlSchemaStoreInfoDTO toDTO(ExternalSqlSchemaStoreInfo model) {
        return (ExternalSqlSchemaStoreInfoDTO) new ExternalSqlSchemaStoreInfoDTO()
                .databaseType(model.getDatabaseType())
                .storeType(ExternalSchemaStoreInfoDTO.StoreTypeEnum.SQL);
    }

    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.SQL;
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalSqlSchemaStoreInfoDTO.StoreTypeEnum.SQL;
    }

}
