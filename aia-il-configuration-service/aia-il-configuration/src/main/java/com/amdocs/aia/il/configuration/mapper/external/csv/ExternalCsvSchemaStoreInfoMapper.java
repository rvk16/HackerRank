package com.amdocs.aia.il.configuration.mapper.external.csv;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaStoreInfo;
import com.amdocs.aia.il.configuration.dto.ExternalCsvSchemaStoreInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaStoreInfoDTO;
import com.amdocs.aia.il.configuration.mapper.external.ExternalSchemaStoreInfoMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ExternalCsvSchemaStoreInfoMapper implements ExternalSchemaStoreInfoMapper<ExternalCsvSchemaStoreInfo, ExternalCsvSchemaStoreInfoDTO> {
    @Override
    public ExternalCsvSchemaStoreInfo toModel(ExternalCsvSchemaStoreInfoDTO dto) {
        ExternalCsvSchemaStoreInfo model = new ExternalCsvSchemaStoreInfo();
        model.setDefaultDateFormat(dto.getDefaultDateFormat());
        if (StringUtils.hasLength(dto.getDefaultColumnDelimiter())) {
            model.setDefaultColumnDelimiter(dto.getDefaultColumnDelimiter().charAt(0));
        }
        return model;
    }

    @Override
    public ExternalCsvSchemaStoreInfoDTO toDTO(ExternalCsvSchemaStoreInfo model) {

        return ((ExternalCsvSchemaStoreInfoDTO) new ExternalCsvSchemaStoreInfoDTO()
                .storeType(ExternalSchemaStoreInfoDTO.StoreTypeEnum.CSV))
                .defaultColumnDelimiter(String.valueOf(model.getDefaultColumnDelimiter()))
                .defaultDateFormat(model.getDefaultDateFormat());
    }

    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.CSV;
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalCsvSchemaStoreInfoDTO.StoreTypeEnum.CSV;
    }
}
