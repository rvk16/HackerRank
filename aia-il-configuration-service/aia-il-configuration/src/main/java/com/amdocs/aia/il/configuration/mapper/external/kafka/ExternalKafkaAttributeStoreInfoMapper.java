package com.amdocs.aia.il.configuration.mapper.external.kafka;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaAttributeStoreInfo;
import com.amdocs.aia.il.configuration.dto.ExternalAttributeStoreInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalKafkaAttributeStoreInfoDTO;
import com.amdocs.aia.il.configuration.mapper.external.ExternalAttributeStoreInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalKafkaAttributeStoreInfoMapper implements ExternalAttributeStoreInfoMapper<ExternalKafkaAttributeStoreInfo, ExternalKafkaAttributeStoreInfoDTO> {
    @Override
    public ExternalKafkaAttributeStoreInfo toModel(ExternalKafkaAttributeStoreInfoDTO dto) {
        ExternalKafkaAttributeStoreInfo kafkaInfo = new ExternalKafkaAttributeStoreInfo();
        kafkaInfo.setDateFormat(dto.getDateFormat());
        kafkaInfo.setJsonPath(dto.getJsonPath());
        return kafkaInfo;
    }

    @Override
    public ExternalKafkaAttributeStoreInfoDTO toDTO(ExternalKafkaAttributeStoreInfo model) {
        return (ExternalKafkaAttributeStoreInfoDTO) new ExternalKafkaAttributeStoreInfoDTO()
                .jsonPath(model.getJsonPath())
                .dateFormat(model.getDateFormat())
                .storeType(ExternalAttributeStoreInfoDTO.StoreTypeEnum.KAFKA);
    }

    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.KAFKA;
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalKafkaAttributeStoreInfoDTO.StoreTypeEnum.KAFKA;
    }
}
