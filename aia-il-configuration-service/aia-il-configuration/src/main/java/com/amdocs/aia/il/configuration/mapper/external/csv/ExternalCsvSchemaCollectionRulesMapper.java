package com.amdocs.aia.il.configuration.mapper.external.csv;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaCollectionRules;
import com.amdocs.aia.il.common.model.physical.csv.CsvEntityStore;
import com.amdocs.aia.il.configuration.dto.ExternalCsvSchemaCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaCollectionRulesDTO;
import com.amdocs.aia.il.configuration.mapper.MapperUtils;
import com.amdocs.aia.il.configuration.mapper.external.AbstractExternalSchemaCollectionRulesInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalCsvSchemaCollectionRulesMapper extends AbstractExternalSchemaCollectionRulesInfoMapper<ExternalCsvSchemaCollectionRules, ExternalCsvSchemaCollectionRulesDTO> {

    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.CSV;
    }

    @Override
    public ExternalCsvSchemaCollectionRulesDTO toDTO(ExternalCsvSchemaCollectionRules model) {
        ExternalCsvSchemaCollectionRulesDTO dto = super.toDTO(model);
        dto.setDefaultInvalidFilenameAction(MapperUtils.toDTO(model.getDefaultInvalidFilenameAction()));
        dto.storeType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.CSV);
        return dto;
    }

    @Override
    public ExternalCsvSchemaCollectionRules toModel(ExternalCsvSchemaCollectionRulesDTO dto) {
        ExternalCsvSchemaCollectionRules model = super.toModel(dto);
        model.setDefaultInvalidFilenameAction(MapperUtils.toModel(dto.getDefaultInvalidFilenameAction()));
        return model;
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalCsvSchemaCollectionRulesDTO.StoreTypeEnum.CSV;
    }

}
