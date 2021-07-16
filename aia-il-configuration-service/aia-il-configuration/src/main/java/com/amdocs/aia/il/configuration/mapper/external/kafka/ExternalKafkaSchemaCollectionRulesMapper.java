package com.amdocs.aia.il.configuration.mapper.external.kafka;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaCollectionRules;
import com.amdocs.aia.il.configuration.dto.ExternalCsvSchemaCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalKafkaSchemaCollectionRulesDTO;
import com.amdocs.aia.il.configuration.mapper.MapperUtils;
import com.amdocs.aia.il.configuration.mapper.external.AbstractExternalSchemaCollectionRulesInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalKafkaSchemaCollectionRulesMapper extends AbstractExternalSchemaCollectionRulesInfoMapper<ExternalKafkaSchemaCollectionRules, ExternalKafkaSchemaCollectionRulesDTO> {
    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.KAFKA;
    }

    @Override
    public ExternalKafkaSchemaCollectionRulesDTO toDTO(ExternalKafkaSchemaCollectionRules model) {
        return (ExternalKafkaSchemaCollectionRulesDTO)super.toDTO(model)
                .inputDataChannel(model.getInputDataChannel())
                .skipNodeFromParsing(model.getSkipNodeFromParsing())
                .deleteEventJsonPath(model.getDeleteEventJsonPath())
                .deleteEventOperation(model.getDeleteEventOperation())
                .implicitHandlerPreviousNode(model.getImplicitHandlerPreviousNode())
                .implicitHandlerCurrentNode(model.getImplicitHandlerCurrentNode())
                .storeType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.KAFKA);
    }

    @Override
    public ExternalKafkaSchemaCollectionRules toModel(ExternalKafkaSchemaCollectionRulesDTO dto) {
        ExternalKafkaSchemaCollectionRules model = super.toModel(dto);

        model.setInputDataChannel(dto.getInputDataChannel());
        model.setSkipNodeFromParsing(dto.getSkipNodeFromParsing());
        model.setDeleteEventJsonPath(dto.getDeleteEventJsonPath());
        model.setDeleteEventOperation(dto.getDeleteEventOperation());
        model.setImplicitHandlerPreviousNode(dto.getImplicitHandlerPreviousNode());
        model.setImplicitHandlerCurrentNode(dto.getImplicitHandlerCurrentNode());

        return model;
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalKafkaSchemaCollectionRulesDTO.StoreTypeEnum.KAFKA;
    }

}
