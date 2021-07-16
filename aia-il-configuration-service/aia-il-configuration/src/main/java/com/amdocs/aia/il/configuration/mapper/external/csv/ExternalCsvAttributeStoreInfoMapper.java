package com.amdocs.aia.il.configuration.mapper.external.csv;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvAttributeStoreInfo;
import com.amdocs.aia.il.configuration.dto.ExternalAttributeStoreInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalCsvAttributeStoreInfoDTO;
import com.amdocs.aia.il.configuration.mapper.external.ExternalAttributeStoreInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalCsvAttributeStoreInfoMapper implements ExternalAttributeStoreInfoMapper<ExternalCsvAttributeStoreInfo, ExternalCsvAttributeStoreInfoDTO> {
    @Override
    public ExternalCsvAttributeStoreInfo toModel(ExternalCsvAttributeStoreInfoDTO dto) {
        final ExternalCsvAttributeStoreInfo storeInfo = new ExternalCsvAttributeStoreInfo();
        storeInfo.setDateFormat(dto.getDateFormat());
        return storeInfo;
    }

    @Override
    public ExternalCsvAttributeStoreInfoDTO toDTO(ExternalCsvAttributeStoreInfo model) {
        return (ExternalCsvAttributeStoreInfoDTO) new ExternalCsvAttributeStoreInfoDTO()
                .dateFormat(model.getDateFormat())
                .storeType(ExternalAttributeStoreInfoDTO.StoreTypeEnum.CSV);
    }

    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.CSV;
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalAttributeStoreInfoDTO.StoreTypeEnum.CSV;
    }
}
