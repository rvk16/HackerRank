package com.amdocs.aia.il.configuration.mapper.external.kafka;

import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaEntityStoreInfo;
import com.amdocs.aia.il.configuration.dto.ExternalEntityStoreInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalKafkaEntityStoreInfoDTO;
import com.amdocs.aia.il.configuration.mapper.external.ExternalEntityStoreInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalKafkaEntityStoreInfoMapper implements ExternalEntityStoreInfoMapper<ExternalKafkaEntityStoreInfo, ExternalKafkaEntityStoreInfoDTO> {
    @Override
    public ExternalKafkaEntityStoreInfo toModel(ExternalKafkaEntityStoreInfoDTO dto, ExternalSchema schema) {
        ExternalKafkaEntityStoreInfo kafkaInfo = new ExternalKafkaEntityStoreInfo();
        kafkaInfo.setJsonTypePath(dto.getJsonTypePath());
        kafkaInfo.setJsonTypeValue(dto.getJsonTypeValue());
        kafkaInfo.setMergedNodes(dto.getMergedNodes());
        kafkaInfo.setRelativePaths(dto.getRelativePaths());
        return kafkaInfo;
    }

    @Override
    public ExternalKafkaEntityStoreInfoDTO toDTO(ExternalKafkaEntityStoreInfo model) {
        return (ExternalKafkaEntityStoreInfoDTO) new ExternalKafkaEntityStoreInfoDTO()
                .jsonTypePath(model.getJsonTypePath())
                .jsonTypeValue(model.getJsonTypeValue())
                .mergedNodes(model.getMergedNodes())
                .relativePaths(model.getRelativePaths())
                .storeType(ExternalEntityStoreInfoDTO.StoreTypeEnum.KAFKA);
    }

    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.KAFKA;
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalKafkaEntityStoreInfoDTO.StoreTypeEnum.KAFKA;
    }
}
