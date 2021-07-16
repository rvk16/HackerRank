package com.amdocs.aia.il.configuration.mapper.external;

import com.amdocs.aia.il.common.model.configuration.ConfigurationUtils;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaStoreInfo;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.mapper.MapperUtils;
import com.amdocs.aia.il.configuration.mapper.ModelDtoMapper;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.amdocs.aia.il.common.model.configuration.ConfigurationUtils.nullSafeBoolean;

import java.util.stream.Collectors;
@Component
public class ExternalSchemaMapper implements ModelDtoMapper<ExternalSchema, ExternalSchemaDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalSchemaMapper.class);
    private final MessageHelper messageHelper;
    private final ExternalModelTypeSpecificMapperLookup<ExternalSchemaStoreInfoMapper> storeInfoMapperLookup;
    private final ExternalModelTypeSpecificMapperLookup<ExternalSchemaCollectionRulesMapper> collectionRulesMapperLookup;

    private String defaultSerializationMethod;

    public ExternalSchemaMapper(MessageHelper messageHelper, ExternalModelTypeSpecificMapperLookup<ExternalSchemaStoreInfoMapper> storeInfoMapperLookup, ExternalModelTypeSpecificMapperLookup<ExternalSchemaCollectionRulesMapper> collectionRulesMapperLookup) {
        this.messageHelper = messageHelper;
        this.storeInfoMapperLookup = storeInfoMapperLookup;
        this.collectionRulesMapperLookup = collectionRulesMapperLookup;
    }

    @Value("${aia.il.default-serialization-method:SharedJson}")
    public void setDefaultSerializationMethod(String defaultSerializationMethod) {
        this.defaultSerializationMethod = defaultSerializationMethod;
    }

    @Override
    public ExternalSchema toModel(String projectKey, ExternalSchemaDTO dto) {
        final ExternalSchema model = new ExternalSchema();
        model.setSchemaKey(dto.getSchemaKey());
        model.setName(dto.getSchemaName());
        model.setSchemaType(getSchemaTypeFromDTO(dto));
        model.setTypeSystem(dto.getTypeSystem());
        model.setIsReference(nullSafeBoolean(dto.isIsReference()));
        model.setStoreInfo(toModel(dto.getStoreInfo()));
        model.setCollectionRules(toModel(dto.getCollectionRules()));
        model.setDataChannelInfo(toModel(dto.getDataChannelInfo(), dto.getSchemaKey()));
        model.setProjectKey(projectKey);
        model.setDescription(dto.getDescription());
        model.setOriginProcess(ConfigurationUtils.getOriginProcess(dto.getOriginProcess()));
        model.setIsActive(nullSafeBoolean(dto.isIsActive()));
        model.setAvailability(toModel(dto.getAvailability()));
        model.setSubjectAreaName(dto.getSubjectAreaName());
        model.setSubjectAreaKey(dto.getSubjectAreaKey());

        return model;
    }

    @Override
    public List<ExternalSchema> toModel(String projectKey, List<ExternalSchemaDTO> dtos) {
        return dtos.stream()
                .peek(dto->  LOGGER.info("ExternalSchema toModel: {}", dto.getSchemaKey() ))
                .map(dto -> toModel(projectKey,dto))
                .collect(Collectors.toList());
    }


    @Override
    public ExternalSchemaDTO toDTO(ExternalSchema model) {
        final ExternalSchemaDTO dto = new ExternalSchemaDTO();
        dto.setSchemaKey(model.getSchemaKey());
        dto.setSchemaName(model.getName());
        dto.setTypeSystem(model.getTypeSystem());
        dto.setIsReference(model.getIsReference());
        dto.setStoreInfo(toDTO(model.getStoreInfo()));
        dto.setCollectionRules(toDTO(model.getCollectionRules()));
        dto.setDescription(model.getDescription());
        dto.setDataChannelInfo(toDTO(model.getDataChannelInfo()));
        dto.setSchemaType(getSchemaTypeFromModel(model));
        dto.setOriginProcess(model.getOriginProcess().name());
        dto.setIsActive(model.getIsActive());
        dto.setDisplayType(getDisplayType(model));
        dto.setInitialCollector(getCollectorDisplayName(model.getCollectionRules() != null ? model.getCollectionRules().getInitialLoadChannel() : null));
        dto.setOngoingCollector(getCollectorDisplayName(model.getCollectionRules() != null ? model.getCollectionRules().getOngoingChannel() : null));
        dto.setSelectiveCollector(getCollectorDisplayName(model.getCollectionRules() != null ? model.getCollectionRules().getReplayChannel() : null));
        dto.setStatus(MapperUtils.toDTO(model.getChangeStatus()));
        dto.setCreatedBy(model.getCreatedBy());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setAvailability(toDTO(model.getAvailability()));
        dto.setSubjectAreaName(model.getSubjectAreaName());
        dto.setSubjectAreaKey(model.getSubjectAreaKey());

        return dto;
    }

    private ExternalSchemaType getSchemaTypeFromDTO(ExternalSchemaDTO dto) {
        if (StringUtils.hasText(dto.getSchemaType())) {
            final Optional<ExternalSchemaType> externalSchemaType = Arrays.stream(ExternalSchemaType.values()).filter(enumValue -> enumValue.name().equals(dto.getSchemaType())).findAny();
            if (externalSchemaType.isPresent()) {
                return externalSchemaType.get();
            } else {
                throw new IllegalArgumentException("Invalid external schema type: " + dto.getSchemaType()); // for now we throw an exception. in the future when we support custom schema types we will probably need to store it separately
            }
        } else {
            // NOTE that this is only for for backward compatibility to support existing code! for now we will not throw an exception but we'll keep this field as null
            // TODO do not allow null values
            return null;
        }
    }

    private String getSchemaTypeFromModel(ExternalSchema model) {
        // NOTE that for backward compatibility (to support existing configurations) for now we will not throw an exception if this field is null
        // TODO do not allow null values
        return model.getSchemaType() != null ? model.getSchemaType().name() : null;
    }

    private ExternalSchemaCollectionRules toModel(ExternalSchemaCollectionRulesDTO dto) {
        return dto != null ? collectionRulesMapperLookup.getByDtoDiscriminator(dto.getStoreType()).toModel(dto) : null;
    }

    private ExternalSchemaCollectionRulesDTO toDTO(ExternalSchemaCollectionRules model) {
        return model != null ? collectionRulesMapperLookup.getBySchemaType(model.getType()).toDTO(model) : null;
    }

    private String getCollectorDisplayName(CollectorChannelType channelType) {
        return messageHelper.getCollectorChannelTypeDisplayName(channelType != null ? channelType : CollectorChannelType.NONE);
    }

    private String getDisplayType(ExternalSchema model) {
        // TODO do not allow null schema type (we keep this null check for backward compatibility)
        if (model.getSchemaType() != null) {
            return messageHelper.getExternalSchemaTypeDisplayName(model.getSchemaType());
        } else if (model.getStoreInfo() != null) {
            String storeType = model.getStoreInfo().getPhysicalStoreType().name();
            String storeTypeDisplayName = messageHelper.format("store.type." + storeType);
            String subType = null;
            if (model.getStoreInfo().getPhysicalStoreType().equals(PhysicalStoreType.SQL)) {
                subType = ((ExternalSqlSchemaStoreInfo) model.getStoreInfo()).getDatabaseType();
            }
            if (subType != null) {
                return String.format("%s (%s)", storeTypeDisplayName, subType);
            } else {
                return storeTypeDisplayName;
            }
        } else {
            return messageHelper.format("unknown.external.schema.type");
        }
    }

    private ExternalSchemaStoreInfoDTO toDTO(ExternalSchemaStoreInfo model) {
        return model != null ? storeInfoMapperLookup.getBySchemaType(model.getType()).toDTO(model) : null;
    }

    private ExternalSchemaStoreInfo toModel(ExternalSchemaStoreInfoDTO dto) {
        return dto != null ? storeInfoMapperLookup.getByDtoDiscriminator(dto.getStoreType()).toModel(dto) : null;
    }

    public ExternalSchemaDataChannelInfoDTO toDTO(ExternalSchemaDataChannelInfo model) {
        ExternalSchemaDataChannelInfoDTO dto = new ExternalSchemaDataChannelInfoDTO();
        dto.setDataChannelName(model.getDataChannelName());
        dto.setSerializationMethod(ExternalSchemaDataChannelInfoDTO.SerializationMethodEnum.fromValue(model.getSerializationMethod()));
        return dto;
    }


    public ExternalSchemaDataChannelInfo toModel(ExternalSchemaDataChannelInfoDTO dto, String defaultDataChannelName) {
        final ExternalSchemaDataChannelInfo model = new ExternalSchemaDataChannelInfo();
        String dataChannelName = dto != null && StringUtils.hasText(dto.getDataChannelName()) ?
                dto.getDataChannelName() : defaultDataChannelName;
        String serializationMethod = dto != null && dto.getSerializationMethod() != null ?
                dto.getSerializationMethod().toString() : defaultSerializationMethod;

        model.setDataChannelName(dataChannelName);
        model.setSerializationMethod(serializationMethod);
        return model;

    }

    private Availability toModel(AvailabilityDTO dto) {
        return dto != null && dto.equals(AvailabilityDTO.SHARED) ? Availability.SHARED : Availability.EXTERNAL;
    }

    private AvailabilityDTO toDTO(Availability model) {
        return model != null && model.equals(Availability.SHARED) ? AvailabilityDTO.SHARED : AvailabilityDTO.EXTERNAL;
    }


}
