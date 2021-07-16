package com.amdocs.aia.il.configuration.mapper.external.csv;

import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaStoreInfo;
import com.amdocs.aia.il.configuration.dto.ExternalCsvEntityStoreInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityStoreInfoDTO;
import com.amdocs.aia.il.configuration.mapper.external.ExternalEntityStoreInfoMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ExternalCsvEntityStoreInfoMapper implements ExternalEntityStoreInfoMapper<ExternalCsvEntityStoreInfo, ExternalCsvEntityStoreInfoDTO> {
    @Override
    public ExternalCsvEntityStoreInfo toModel(ExternalCsvEntityStoreInfoDTO dto, ExternalSchema schema) {
        ExternalCsvEntityStoreInfo model = new ExternalCsvEntityStoreInfo();
        ExternalCsvSchemaStoreInfo schemaStoreInfo = (ExternalCsvSchemaStoreInfo) schema.getStoreInfo();
        if (StringUtils.hasLength(dto.getColumnDelimiter())) {
            model.setColumnDelimiter(dto.getColumnDelimiter().charAt(0));
        } else {
            model.setColumnDelimiter(schemaStoreInfo.getDefaultColumnDelimiter());
        }

        model.setDateFormat(dto.getDateFormat() != null ? dto.getDateFormat() : schemaStoreInfo.getDefaultDateFormat());
        model.setFileNameFormat(dto.getFileNameFormat());
        model.setHeader(dto.isFileHeader() != null && dto.isFileHeader().booleanValue());
        return model;
    }

    @Override
    public ExternalCsvEntityStoreInfoDTO toDTO(ExternalCsvEntityStoreInfo model) {
        return (ExternalCsvEntityStoreInfoDTO) new ExternalCsvEntityStoreInfoDTO()
                .fileHeader(model.isHeader())
                .columnDelimiter(String.valueOf(model.getColumnDelimiter()))
                .dateFormat(model.getDateFormat())
                .fileNameFormat(model.getFileNameFormat())
                .storeType(ExternalEntityStoreInfoDTO.StoreTypeEnum.CSV);
    }

    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.CSV;
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalEntityStoreInfoDTO.StoreTypeEnum.CSV;
    }
}
