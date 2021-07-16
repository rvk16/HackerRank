package com.amdocs.aia.il.configuration.mapper.external.kafka;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaStoreInfo;
import com.amdocs.aia.il.configuration.dto.ExternalKafkaSchemaStoreInfoDTO;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaStoreInfoDTO;
import com.amdocs.aia.il.configuration.mapper.external.ExternalSchemaStoreInfoMapper;
import org.springframework.stereotype.Component;

@Component
public class ExternalKafkaSchemaStoreInfoMapper implements ExternalSchemaStoreInfoMapper<ExternalKafkaSchemaStoreInfo, ExternalKafkaSchemaStoreInfoDTO> {
    @Override
    public ExternalKafkaSchemaStoreInfo toModel(ExternalKafkaSchemaStoreInfoDTO dto) {
        final ExternalKafkaSchemaStoreInfo model = new ExternalKafkaSchemaStoreInfo();
        model.setDefaultDateFormat(dto.getDefaultDateFormat());
        return model;
    }

    @Override
    public ExternalKafkaSchemaStoreInfoDTO toDTO(ExternalKafkaSchemaStoreInfo model) {
        return (ExternalKafkaSchemaStoreInfoDTO)new ExternalKafkaSchemaStoreInfoDTO()
                .defaultDateFormat(model.getDefaultDateFormat())
                .storeType(ExternalSchemaStoreInfoDTO.StoreTypeEnum.KAFKA);
    }

    @Override
    public String getExternalSchemaType() {
        return ExternalSchemaStoreTypes.KAFKA;
    }

    @Override
    public Object getDtoDiscriminatorValue() {
        return ExternalSchemaStoreInfoDTO.StoreTypeEnum.KAFKA;
    }
}
