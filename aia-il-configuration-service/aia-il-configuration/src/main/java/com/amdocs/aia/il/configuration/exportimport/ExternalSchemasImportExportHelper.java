package com.amdocs.aia.il.configuration.exportimport;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.external.csv.*;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaStoreInfo;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaStoreInfo;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExternalSchemasImportExportHelper {

    private final MessageHelper messageHelper;
    public static final String UNKNOWN_EXTERNAL_SCHEMA_STORE_TYPE = "Unknown External Schema Store Type: ";
    @Autowired
    public ExternalSchemasImportExportHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    private ExternalSchemaCollectionRulesDTO toCollectionRules(ExternalSchemaExportCSV externalSchemaExportCSV) {
        String storeType = ExternalSchemaType.valueOf(externalSchemaExportCSV.getSchemaType()).getStoreType();
        switch (storeType){
            case ExternalSchemaStoreTypes.CSV:
                ExternalCsvSchemaCollectionRulesDTO externalCsvSchemaCollectionRulesDTO = new ExternalCsvSchemaCollectionRulesDTO();
                setExternalCollectionRules(externalSchemaExportCSV, externalCsvSchemaCollectionRulesDTO);
                externalCsvSchemaCollectionRulesDTO.setDefaultInvalidFilenameAction(toInvalidFilenameActionTypeDTO(externalSchemaExportCSV.getDefaultInvalidFilenameAction().name()));
                externalCsvSchemaCollectionRulesDTO.setStoreType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.CSV);
                return externalCsvSchemaCollectionRulesDTO;
            case ExternalSchemaStoreTypes.KAFKA:
                ExternalKafkaSchemaCollectionRulesDTO externalKafkaSchemaCollectionRulesDTO = new ExternalKafkaSchemaCollectionRulesDTO();
                setExternalCollectionRules(externalSchemaExportCSV, externalKafkaSchemaCollectionRulesDTO);
                externalKafkaSchemaCollectionRulesDTO.setInputDataChannel(externalSchemaExportCSV.getInputDataChannel());
                externalKafkaSchemaCollectionRulesDTO.setSkipNodeFromParsing(externalSchemaExportCSV.getSkipNodeFromParsing());
                externalKafkaSchemaCollectionRulesDTO.setDeleteEventJsonPath(externalSchemaExportCSV.getDeleteEventJsonPath());
                externalKafkaSchemaCollectionRulesDTO.setDeleteEventOperation(externalSchemaExportCSV.getDeleteEventOperation());
                externalKafkaSchemaCollectionRulesDTO.setImplicitHandlerPreviousNode(externalSchemaExportCSV.getImplicitHandlerPreviousNode());
                externalKafkaSchemaCollectionRulesDTO.setImplicitHandlerCurrentNode(externalSchemaExportCSV.getImplicitHandlerCurrentNode());
                externalKafkaSchemaCollectionRulesDTO.setStoreType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.KAFKA);
                return externalKafkaSchemaCollectionRulesDTO;
            case ExternalSchemaStoreTypes.SQL:
                ExternalSqlSchemaCollectionRulesDTO externalSqlSchemaCollectionRulesDTO = new ExternalSqlSchemaCollectionRulesDTO();
                setExternalCollectionRules(externalSchemaExportCSV, externalSqlSchemaCollectionRulesDTO);
                externalSqlSchemaCollectionRulesDTO.setStoreType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.SQL);
                return externalSqlSchemaCollectionRulesDTO;
            default:
                throw new IllegalArgumentException(UNKNOWN_EXTERNAL_SCHEMA_STORE_TYPE + storeType);
        }
    }

    private void setExternalCollectionRules(ExternalSchemaExportCSV externalSchemaExportCSV, ExternalSchemaCollectionRulesDTO externalSchemaCollectionRulesDTO) {
        if( ExternalSchemaType.valueOf(externalSchemaExportCSV.getSchemaType()).getSupportedInitialLoadChannels().contains( CollectorChannelType.valueOf(externalSchemaExportCSV.getInitialLoadChannel()))){
            externalSchemaCollectionRulesDTO.setInitialLoadChannel(externalSchemaExportCSV.getInitialLoadChannel());
        }else{
            throw messageHelper.invalidExternalCollectionRulesException(ExternalSchema.ELEMENT_TYPE, externalSchemaExportCSV.getSchemaKey(),externalSchemaExportCSV.getSchemaType(), externalSchemaExportCSV.getInitialLoadChannel());
        }
        if( ExternalSchemaType.valueOf(externalSchemaExportCSV.getSchemaType()).getSupportedOngoingChannels().contains(CollectorChannelType.valueOf(externalSchemaExportCSV.getOngoingChannel()))){
            externalSchemaCollectionRulesDTO.setOngoingChannel(externalSchemaExportCSV.getOngoingChannel());
        }else{
            throw messageHelper.invalidExternalCollectionRulesException(ExternalSchema.ELEMENT_TYPE, externalSchemaExportCSV.getSchemaKey(),externalSchemaExportCSV.getSchemaType(), externalSchemaExportCSV.getOngoingChannel());
        }
        if( ExternalSchemaType.valueOf(externalSchemaExportCSV.getSchemaType()).getSupportedReplayChannels().contains(CollectorChannelType.valueOf(externalSchemaExportCSV.getReplayChannel()))){
            externalSchemaCollectionRulesDTO.setReplayChannel(externalSchemaExportCSV.getReplayChannel());
        }else{
            throw messageHelper.invalidExternalCollectionRulesException(ExternalSchema.ELEMENT_TYPE, externalSchemaExportCSV.getSchemaKey(),externalSchemaExportCSV.getSchemaType(), externalSchemaExportCSV.getReplayChannel());
        }
        externalSchemaCollectionRulesDTO.setPartialLoadRelativeURL(externalSchemaExportCSV.getPartialLoadRelativeURL());
        externalSchemaCollectionRulesDTO.setInitialLoadRelativeURL(externalSchemaExportCSV.getInitialLoadRelativeURL());
    }

    public ExternalSchemaDataChannelInfoDTO toExternalSchemaDataChannelInfoDTO(String serializationMethod) {
        final ExternalSchemaDataChannelInfoDTO externalSchemaDataChannelInfoDTO = new ExternalSchemaDataChannelInfoDTO();
        externalSchemaDataChannelInfoDTO.setSerializationMethod(ExternalSchemaDataChannelInfoDTO.SerializationMethodEnum.fromValue(serializationMethod));
        return externalSchemaDataChannelInfoDTO;

    }




    public ExternalAttributeDTO toExternalAttributeDTO(ExternalAttributeExportCSV attributeExportCSVDTO, String schemaType) {
        ExternalAttributeDTO attributeDTO = new ExternalAttributeDTO();
        attributeDTO.setAttributeKey(attributeExportCSVDTO.getAttributeKey());
        attributeDTO.setAttributeName(attributeExportCSVDTO.getAttributeName());
        attributeDTO.setDescription(attributeExportCSVDTO.getDescription());
        attributeDTO.setDatatype(attributeExportCSVDTO.getDatatype());
        attributeDTO.setKeyPosition(attributeExportCSVDTO.getKeyPosition());
        attributeDTO.setIsLogicalTime(attributeExportCSVDTO.getLogicalTime());
        attributeDTO.setIsUpdateTime(attributeExportCSVDTO.getUpdateTime());
        attributeDTO.setIsRequired(attributeExportCSVDTO.getRequired());
        attributeDTO.setDefaultValue(attributeExportCSVDTO.getDefaultValue());
        attributeDTO.setValidationRegex(attributeExportCSVDTO.getValidationRegex());
        attributeDTO.setStoreInfo(toStoreInfoAttribute(attributeExportCSVDTO,schemaType));
        return attributeDTO;
    }

    private ExternalAttributeStoreInfoDTO toStoreInfoAttribute(ExternalAttributeExportCSV attributeExportCSVDTO, String schemaType) {
        switch(schemaType){
            case ExternalSchemaStoreTypes.CSV:
                return new ExternalCsvAttributeStoreInfoDTO()
                        .dateFormat(attributeExportCSVDTO.getDateFormat())
                        .storeType(ExternalAttributeStoreInfoDTO.StoreTypeEnum.CSV);
            case ExternalSchemaStoreTypes.KAFKA:
                return new ExternalKafkaAttributeStoreInfoDTO()
                        .jsonPath(attributeExportCSVDTO.getJsonPath())
                        .dateFormat(attributeExportCSVDTO.getDateFormat())
                        .storeType(ExternalAttributeStoreInfoDTO.StoreTypeEnum.KAFKA);
            case ExternalSchemaStoreTypes.SQL:
                return new ExternalSqlAttributeStoreInfoDTO().storeType(ExternalAttributeStoreInfoDTO.StoreTypeEnum.SQL);
            default: throw new IllegalArgumentException(UNKNOWN_EXTERNAL_SCHEMA_STORE_TYPE + schemaType);
        }
    }

    public ExternalEntityStoreInfoDTO toStoreInfoEntity(ExternalEntityExportCSV externalEntityExportCSV, String schemaType) {
        switch(schemaType){
            case ExternalSchemaStoreTypes.CSV:
                return new ExternalCsvEntityStoreInfoDTO()
                        .fileHeader(externalEntityExportCSV.getFileHeader())
                        .columnDelimiter(String.valueOf(externalEntityExportCSV.getColumnDelimiter()))
                        .dateFormat(externalEntityExportCSV.getDateFormat())
                        .fileNameFormat(externalEntityExportCSV.getFileNameFormat())
                        .storeType(ExternalEntityStoreInfoDTO.StoreTypeEnum.CSV);
            case ExternalSchemaStoreTypes.KAFKA:
                return new ExternalKafkaEntityStoreInfoDTO()
                        .jsonTypePath(externalEntityExportCSV.getJsonTypePath())
                        .jsonTypeValue(externalEntityExportCSV.getJsonTypeValue())
                        .mergedNodes(externalEntityExportCSV.getMergedNodes())
                        .relativePaths(externalEntityExportCSV.getRelativePaths())
                        .storeType(ExternalEntityStoreInfoDTO.StoreTypeEnum.KAFKA);
            case ExternalSchemaStoreTypes.SQL:
                return new ExternalSqlEntityStoreInfoDTO()
                        .storeType(ExternalEntityStoreInfoDTO.StoreTypeEnum.SQL);
            default: throw new IllegalArgumentException(UNKNOWN_EXTERNAL_SCHEMA_STORE_TYPE + schemaType);
        }

    }

    public ExternalEntityCollectionRulesDTO toCollectionRulesEntity(ExternalEntityExportCSV externalEntityExportCSV, String schemaType) {
        if(schemaType != null){
            switch (schemaType){
                case ExternalSchemaStoreTypes.CSV:
                    ExternalCsvEntityCollectionRulesDTO externalCsvEntityCollectionRulesDTO = new ExternalCsvEntityCollectionRulesDTO();
                    externalCsvEntityCollectionRulesDTO.setStoreType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.CSV);
                    externalCsvEntityCollectionRulesDTO.setInvalidFilenameAction(toInvalidFilenameActionTypeDTO(externalEntityExportCSV.getInvalidFilenameAction().name()));
                    return externalCsvEntityCollectionRulesDTO;
                case ExternalSchemaStoreTypes.KAFKA:
                    ExternalKafkaEntityCollectionRulesDTO externalKafkaEntityCollectionRulesDTO = new ExternalKafkaEntityCollectionRulesDTO();
                    externalKafkaEntityCollectionRulesDTO.setStoreType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.KAFKA);
                    return externalKafkaEntityCollectionRulesDTO;
                case ExternalSchemaStoreTypes.SQL:
                    ExternalSqlEntityCollectionRulesDTO externalSqlEntityCollectionRulesDTO = new ExternalSqlEntityCollectionRulesDTO();
                    externalSqlEntityCollectionRulesDTO.setStoreType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.SQL);
                    return externalSqlEntityCollectionRulesDTO;

                default:
                    throw new IllegalArgumentException(UNKNOWN_EXTERNAL_SCHEMA_STORE_TYPE + schemaType);
            }
        }else{
            return null;
        }
    }

    private String getDisplayType(ExternalSchemaExportCSV externalEntityExportCSVDTO) {
        return messageHelper.format("external.schema.type." + externalEntityExportCSVDTO.getSchemaType());
    }

    public ExternalSchemaDTO toExternalSchemaDTO(ExternalSchemaExportCSV externalSchemaExportCSV) {
        final ExternalSchemaDTO externalSchemaDTO = new ExternalSchemaDTO();
        externalSchemaDTO.setSchemaKey(externalSchemaExportCSV.getSchemaKey());
        externalSchemaDTO.setSchemaName(externalSchemaExportCSV.getSchemaName());
        externalSchemaDTO.setTypeSystem(externalSchemaExportCSV.getTypeSystem());
        externalSchemaDTO.setIsReference(externalSchemaExportCSV.getReference());
        externalSchemaDTO.setStoreInfo(toStoreInfo(externalSchemaExportCSV));
        externalSchemaDTO.setCollectionRules(toCollectionRules(externalSchemaExportCSV));
        externalSchemaDTO.setDescription(externalSchemaExportCSV.getDescription());
        externalSchemaDTO.setDataChannelInfo(toExternalSchemaDataChannelInfoDTO(externalSchemaExportCSV.getSerializationMethod()));
        externalSchemaDTO.setSchemaType(externalSchemaExportCSV.getSchemaType() != null ? externalSchemaExportCSV.getSchemaType() : null);
        externalSchemaDTO.setOriginProcess(OriginProcess.IMPLEMENTATION.name());
        externalSchemaDTO.setIsActive(externalSchemaExportCSV.getActive());
        externalSchemaDTO.setDisplayType(getDisplayType(externalSchemaExportCSV));
        externalSchemaDTO.setAvailability(externalSchemaExportCSV.getAvailability());
        externalSchemaDTO.setSubjectAreaName(externalSchemaDTO.getAvailability()==AvailabilityDTO.EXTERNAL?null: externalSchemaExportCSV.getSubjectAreaName());
        externalSchemaDTO.setSubjectAreaKey(externalSchemaDTO.getAvailability()==AvailabilityDTO.EXTERNAL?null: externalSchemaExportCSV.getSubjectAreaName());
        return externalSchemaDTO;
    }

    private ExternalSchemaStoreInfoDTO toStoreInfo(ExternalSchemaExportCSV externalSchemaExportCSV) {
        String storeType = ExternalSchemaType.valueOf(externalSchemaExportCSV.getSchemaType()).getStoreType();
        switch (storeType){
            case ExternalSchemaStoreTypes.CSV:
                return  new ExternalCsvSchemaStoreInfoDTO()
                        .defaultColumnDelimiter(externalSchemaExportCSV.getDefaultColumnDelimiter())
                        .defaultDateFormat(externalSchemaExportCSV.getDefaultDateFormat())
                        .storeType(ExternalSchemaStoreInfoDTO.StoreTypeEnum.CSV);
            case ExternalSchemaStoreTypes.KAFKA:
                return new ExternalKafkaSchemaStoreInfoDTO()
                        .defaultDateFormat(externalSchemaExportCSV.getDefaultDateFormat())
                        .storeType(ExternalSchemaStoreInfoDTO.StoreTypeEnum.KAFKA);
            case ExternalSchemaStoreTypes.SQL:
                return new ExternalSqlSchemaStoreInfoDTO()
                        .databaseType(externalSchemaExportCSV.getDatabaseType())
                        .storeType(ExternalSchemaStoreInfoDTO.StoreTypeEnum.SQL);
            default:
                throw new IllegalArgumentException(UNKNOWN_EXTERNAL_SCHEMA_STORE_TYPE + storeType);

        }
    }


    public static InvalidFilenameActionTypeDTO toInvalidFilenameActionTypeDTO(String fileInvalidNameAction) {
        if (fileInvalidNameAction == null) {
            return InvalidFilenameActionTypeDTO.KEEP; // default value
        }
        switch (fileInvalidNameAction) {
            case "KEEP":
                return InvalidFilenameActionTypeDTO.KEEP;
            case "MOVE":
                return InvalidFilenameActionTypeDTO.MOVE;
            default:
                throw new IllegalStateException("Unexpected value: " + fileInvalidNameAction);
        }
    }

    public ExternalEntityExportCSV createExternalEntityExportCSVDTO(ExternalEntity externalEntity) {
        ExternalEntityExportCSV externalEntityExportCSV = new ExternalEntityExportCSV();
        externalEntityExportCSV.setSchemaKey(externalEntity.getSchemaKey());
        externalEntityExportCSV.setEntityKey(externalEntity.getEntityKey());
        externalEntityExportCSV.setActive(externalEntity.getIsActive());
        externalEntityExportCSV.setEntityName(externalEntity.getName());
        externalEntityExportCSV.setDescription(externalEntity.getDescription() != null? externalEntity.getDescription().replace("\n", "").replace("\r", "") : "");
        externalEntityExportCSV.setSerializationId(externalEntity.getSerializationId());
        externalEntityExportCSV.setTransient(ExternalEntityReplicationPolicy.NO_REPLICATION.equals(externalEntity.getReplicationPolicy()));
        externalEntityExportCSV.setTransaction(externalEntity.getIsTransaction());
        switch (externalEntity.getStoreInfo().getType()){
            case ExternalSchemaStoreTypes.CSV:
                externalEntityExportCSV.setFileHeader(((ExternalCsvEntityStoreInfo)externalEntity.getStoreInfo()).isHeader());
                externalEntityExportCSV.setFileNameFormat(((ExternalCsvEntityStoreInfo)externalEntity.getStoreInfo()).getFileNameFormat());
                externalEntityExportCSV.setDateFormat(((ExternalCsvEntityStoreInfo)externalEntity.getStoreInfo()).getDateFormat());
                externalEntityExportCSV.setColumnDelimiter(String.valueOf(((ExternalCsvEntityStoreInfo)externalEntity.getStoreInfo()).getColumnDelimiter()));
                externalEntityExportCSV.setInvalidFilenameAction(toInvalidFilenameActionTypeDTO(((ExternalCsvEntityCollectionRules)externalEntity.getCollectionRules()).getFileInvalidNameAction().name()));
                break;
            case ExternalSchemaStoreTypes.KAFKA:
                externalEntityExportCSV.setJsonTypeValue(((ExternalKafkaEntityStoreInfo)externalEntity.getStoreInfo()).getJsonTypeValue());
                externalEntityExportCSV.setJsonTypePath(((ExternalKafkaEntityStoreInfo)externalEntity.getStoreInfo()).getJsonTypePath());
                externalEntityExportCSV.setRelativePaths(((ExternalKafkaEntityStoreInfo)externalEntity.getStoreInfo()).getRelativePaths());
                externalEntityExportCSV.setMergedNodes(((ExternalKafkaEntityStoreInfo)externalEntity.getStoreInfo()).getMergedNodes());
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_EXTERNAL_SCHEMA_STORE_TYPE + externalEntity.getStoreInfo().getType());
        }
        return externalEntityExportCSV;
    }

    public ExternalAttributeExportCSV createExternalAttributeExportCSVDTO(ExternalAttribute externalAttribute, String schemaKey, String entityKey, String entityType) {
        ExternalAttributeExportCSV externalAttributeExportCSV = new ExternalAttributeExportCSV();
        externalAttributeExportCSV.setSchemaKey(schemaKey);
        externalAttributeExportCSV.setEntityKey(entityKey);
        externalAttributeExportCSV.setAttributeKey(externalAttribute.getAttributeKey());
        externalAttributeExportCSV.setAttributeName(externalAttribute.getName());
        externalAttributeExportCSV.setDescription(externalAttribute.getDescription() != null? externalAttribute.getDescription().replace("\n", "").replace("\r", "") : "");
        externalAttributeExportCSV.setDatatype(externalAttribute.getDatatype());
        externalAttributeExportCSV.setLogicalDatatype(externalAttribute.getLogicalDatatype());
        externalAttributeExportCSV.setKeyPosition(externalAttribute.getKeyPosition());
        externalAttributeExportCSV.setUpdateTime(externalAttribute.isUpdateTime());
        externalAttributeExportCSV.setLogicalTime(externalAttribute.isLogicalTime());
        externalAttributeExportCSV.setRequired(externalAttribute.isRequired());
        externalAttributeExportCSV.setDefaultValue(externalAttribute.getDefaultValue());
        externalAttributeExportCSV.setValidationRegex(externalAttribute.getValidationRegex());
        externalAttributeExportCSV.setSerializationId(externalAttribute.getSerializationId());
        if (entityType.equals(ExternalSchemaStoreTypes.KAFKA)) {
            externalAttributeExportCSV.setDateFormat(((ExternalKafkaAttributeStoreInfo) externalAttribute.getStoreInfo()).getDateFormat());
            externalAttributeExportCSV.setJsonPath(((ExternalKafkaAttributeStoreInfo) externalAttribute.getStoreInfo()).getJsonPath());
        }
        else if (entityType.equals(ExternalSchemaStoreTypes.CSV)){
            externalAttributeExportCSV.setDateFormat(((ExternalCsvAttributeStoreInfo) externalAttribute.getStoreInfo()).getDateFormat());
        }
        return externalAttributeExportCSV;
    }

    public ExternalSchemaExportCSV createExternalSchemaExportCSVDTO(ExternalSchema externalSchema) {
        ExternalSchemaExportCSV externalSchemaExportCSV = new ExternalSchemaExportCSV();
        externalSchemaExportCSV.setSchemaKey(externalSchema.getSchemaKey());
        externalSchemaExportCSV.setSchemaName(externalSchema.getName());
        externalSchemaExportCSV.setSchemaType(externalSchema.getSchemaType().name());
        externalSchemaExportCSV.setActive(externalSchema.getIsActive());
        externalSchemaExportCSV.setDescription(externalSchema.getDescription() != null? externalSchema.getDescription().replace("\n", "").replace("\r", "") : "");
        externalSchemaExportCSV.setTypeSystem(externalSchema.getTypeSystem());
        externalSchemaExportCSV.setReference(externalSchema.getIsReference());
        externalSchemaExportCSV.setSerializationMethod(externalSchema.getDataChannelInfo().getSerializationMethod());
        externalSchemaExportCSV.setAvailability(externalSchema.getAvailability().equals(Availability.SHARED) ? AvailabilityDTO.SHARED : AvailabilityDTO.EXTERNAL);
        externalSchemaExportCSV.setSubjectAreaName(externalSchema.getSubjectAreaName());

        externalSchemaExportCSV.setOngoingChannel(externalSchema.getCollectionRules().getOngoingChannel()!= null?externalSchema.getCollectionRules().getOngoingChannel().name():null);
        externalSchemaExportCSV.setInitialLoadChannel(externalSchema.getCollectionRules().getInitialLoadChannel()!= null? externalSchema.getCollectionRules().getInitialLoadChannel().name():null);
        externalSchemaExportCSV.setReplayChannel(externalSchema.getCollectionRules().getReplayChannel() != null?externalSchema.getCollectionRules().getReplayChannel().name():null);
        externalSchemaExportCSV.setInitialLoadRelativeURL(externalSchema.getCollectionRules().getInitialLoadRelativeURL());
        externalSchemaExportCSV.setPartialLoadRelativeURL(externalSchema.getCollectionRules().getPartialLoadRelativeURL());

        switch (externalSchema.getStoreInfo().getType()){
            case ExternalSchemaStoreTypes.SQL:
                externalSchemaExportCSV.setDatabaseType(((ExternalSqlSchemaStoreInfo)externalSchema.getStoreInfo()).getDatabaseType());
                break;
            case ExternalSchemaStoreTypes.CSV:
                externalSchemaExportCSV.setDefaultDateFormat(((ExternalCsvSchemaStoreInfo)externalSchema.getStoreInfo()).getDefaultDateFormat());
                externalSchemaExportCSV.setDefaultColumnDelimiter(String.valueOf(((ExternalCsvSchemaStoreInfo)externalSchema.getStoreInfo()).getDefaultColumnDelimiter()));
                externalSchemaExportCSV.setDefaultInvalidFilenameAction(toInvalidFilenameActionTypeDTO(((ExternalCsvSchemaCollectionRules)externalSchema.getCollectionRules()).getDefaultInvalidFilenameAction().name()));
                break;
            case ExternalSchemaStoreTypes.KAFKA:
                externalSchemaExportCSV.setDefaultDateFormat(((ExternalKafkaSchemaStoreInfo)externalSchema.getStoreInfo()).getDefaultDateFormat());
                externalSchemaExportCSV.setInputDataChannel(((ExternalKafkaSchemaCollectionRules)externalSchema.getCollectionRules()).getInputDataChannel());
                externalSchemaExportCSV.setSkipNodeFromParsing(((ExternalKafkaSchemaCollectionRules)externalSchema.getCollectionRules()).getSkipNodeFromParsing());
                externalSchemaExportCSV.setDeleteEventJsonPath(((ExternalKafkaSchemaCollectionRules)externalSchema.getCollectionRules()).getDeleteEventJsonPath());
                externalSchemaExportCSV.setDeleteEventOperation(((ExternalKafkaSchemaCollectionRules)externalSchema.getCollectionRules()).getDeleteEventOperation());
                externalSchemaExportCSV.setImplicitHandlerPreviousNode(((ExternalKafkaSchemaCollectionRules)externalSchema.getCollectionRules()).getImplicitHandlerPreviousNode());
                externalSchemaExportCSV.setImplicitHandlerCurrentNode(((ExternalKafkaSchemaCollectionRules)externalSchema.getCollectionRules()).getImplicitHandlerCurrentNode());
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_EXTERNAL_SCHEMA_STORE_TYPE + externalSchema.getStoreInfo().getType());
        }
        return externalSchemaExportCSV;
    }

}
