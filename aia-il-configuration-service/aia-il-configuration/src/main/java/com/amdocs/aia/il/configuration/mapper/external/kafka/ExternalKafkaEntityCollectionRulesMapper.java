package com.amdocs.aia.il.configuration.mapper.external.kafka;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaEntityCollectionRules;
import com.amdocs.aia.il.configuration.dto.ExternalEntityCollectionRulesDTO;
import com.amdocs.aia.il.configuration.dto.ExternalKafkaEntityCollectionRulesDTO;
import com.amdocs.aia.il.configuration.mapper.external.AbstractExternalEntityCollectionRulesInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalKafkaEntityCollectionRulesMapper extends AbstractExternalEntityCollectionRulesInfoMapper<ExternalKafkaEntityCollectionRules, ExternalKafkaEntityCollectionRulesDTO> {
    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.KAFKA;
    }

    @Override
    public ExternalKafkaEntityCollectionRulesDTO toDTO(ExternalKafkaEntityCollectionRules model) {
        return (ExternalKafkaEntityCollectionRulesDTO)super.toDTO(model).storeType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.KAFKA);
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalKafkaEntityCollectionRulesDTO.StoreTypeEnum.KAFKA;
    }

}
