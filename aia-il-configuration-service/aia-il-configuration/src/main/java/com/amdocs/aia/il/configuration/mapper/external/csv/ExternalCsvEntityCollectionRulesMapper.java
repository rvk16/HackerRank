package com.amdocs.aia.il.configuration.mapper.external.csv;

import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaCollectionRules;
import com.amdocs.aia.il.common.model.physical.csv.CsvEntityStore;
import com.amdocs.aia.il.configuration.dto.ExternalCsvEntityCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalEntityCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.InvalidFilenameActionTypeDTO;
import com.amdocs.aia.il.configuration.mapper.MapperUtils;
import com.amdocs.aia.il.configuration.mapper.external.AbstractExternalEntityCollectionRulesInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalCsvEntityCollectionRulesMapper extends AbstractExternalEntityCollectionRulesInfoMapper<ExternalCsvEntityCollectionRules, ExternalCsvEntityCollectionRulesDTO> {
    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.CSV;
    }

    @Override
    public ExternalCsvEntityCollectionRulesDTO toDTO(ExternalCsvEntityCollectionRules model) {
        return (ExternalCsvEntityCollectionRulesDTO) super.toDTO(model)
                .invalidFilenameAction(MapperUtils.toDTO(model.getFileInvalidNameAction()))
                .storeType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.CSV);
    }

    @Override
    public ExternalCsvEntityCollectionRules toModel(ExternalCsvEntityCollectionRulesDTO dto, ExternalSchema schema) {
        final ExternalCsvEntityCollectionRules model = super.toModel(dto, schema);
        if (dto.getInvalidFilenameAction() != null) {
            model.setFileInvalidNameAction(MapperUtils.toModel(dto.getInvalidFilenameAction()));
        } else {
            ExternalCsvSchemaCollectionRules schemaCollectionRules = (ExternalCsvSchemaCollectionRules) schema.getCollectionRules();
            model.setFileInvalidNameAction(schemaCollectionRules.getDefaultInvalidFilenameAction());
        }
        return model;
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalCsvEntityCollectionRulesDTO.StoreTypeEnum.CSV;
    }

}
