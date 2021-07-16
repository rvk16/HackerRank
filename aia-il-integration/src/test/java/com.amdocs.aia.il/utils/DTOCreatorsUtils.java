package com.amdocs.aia.il.utils;


import com.amdocs.aia.il.configuration.client.dto.*;

import java.util.ArrayList;
import java.util.List;

public class DTOCreatorsUtils {

    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    public static ExternalSchemaDTO createExternalSchemaDTO(String schemaKey) {
        ExternalSchemaDTO dto = new ExternalSchemaDTO();
        dto.setSchemaKey(schemaKey);
        dto.setSchemaName(schemaKey);
        dto.setTypeSystem("Oracle");
        dto.setIsReference(false);
        dto.setDescription("Description");

        ExternalCsvSchemaStoreInfoDTO externalCsvSchemaStoreInfo = new ExternalCsvSchemaStoreInfoDTO();
        externalCsvSchemaStoreInfo.setStoreType(ExternalSchemaStoreInfoDTO.StoreTypeEnum.fromValue("CSV"));
        externalCsvSchemaStoreInfo.setDefaultColumnDelimiter(",");
        externalCsvSchemaStoreInfo.setDefaultDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dto.setStoreInfo(externalCsvSchemaStoreInfo);

        ExternalSchemaDataChannelInfoDTO externalSchemaDataChannelInfoDTO = new ExternalSchemaDataChannelInfoDTO();
        externalSchemaDataChannelInfoDTO.setDataChannelName("DataChannelName");
        externalSchemaDataChannelInfoDTO.setSerializationMethod(ExternalSchemaDataChannelInfoDTO.SerializationMethodEnum.SHAREDJSON);
        dto.dataChannelInfo(externalSchemaDataChannelInfoDTO);

        ExternalCsvSchemaCollectionRulesDTO collectionRules = new ExternalCsvSchemaCollectionRulesDTO();
        collectionRules.setInitialLoadChannel("NONE");
        collectionRules.setOngoingChannel("NONE");
        collectionRules.setReplayChannel("NONE");
        collectionRules.setStoreType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.CSV);
        collectionRules.setDefaultInvalidFilenameAction(InvalidFilenameActionTypeDTO.KEEP);

        ExternalKafkaSchemaCollectionRulesDTO externalKafkaSchemaCollectionRules = new ExternalKafkaSchemaCollectionRulesDTO();
        externalKafkaSchemaCollectionRules.setInitialLoadChannel("NONE");
        externalKafkaSchemaCollectionRules.setOngoingChannel("NONE");
        externalKafkaSchemaCollectionRules.setReplayChannel("NONE");
        externalKafkaSchemaCollectionRules.setStoreType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.KAFKA);
        externalKafkaSchemaCollectionRules.setInputDataChannel("InputDataChannel");
        externalKafkaSchemaCollectionRules.setSkipNodeFromParsing("SkipNodeFromParsing");
        externalKafkaSchemaCollectionRules.setDeleteEventJsonPath("DeleteEventJsonPath");
        externalKafkaSchemaCollectionRules.setDeleteEventOperation("DeleteEventOperation");
        externalKafkaSchemaCollectionRules.setImplicitHandlerPreviousNode("ImplicitHandlerPreviousNode");
        externalKafkaSchemaCollectionRules.setImplicitHandlerCurrentNode("ImplicitHandlerCurrentNode");

        dto.setCollectionRules(collectionRules);
        dto.setOriginProcess("MAPPING_SHEETS_MIGRATION");
        dto.setInitialCollector("CSV");
        dto.setOngoingCollector("CSV");
        dto.setSelectiveCollector("CSV");
        dto.setIsActive(true);
        dto.setIsReference(false);
        dto.displayType("SQL");

        return dto;
    }

    public static ExternalEntityDTO createExternalEntityDTO(String schemaKey, String entityKey, String filterKey) {
        ExternalEntityDTO externalEntityDTO = new ExternalEntityDTO();
        externalEntityDTO.setEntityKey(entityKey);
        externalEntityDTO.setEntityName(entityKey);
        externalEntityDTO.setSchemaKey(schemaKey);
        ExternalCsvEntityStoreInfoDTO externalCsvEntityStoreInfoDTO = new ExternalCsvEntityStoreInfoDTO();
        externalCsvEntityStoreInfoDTO.setStoreType(ExternalSqlEntityStoreInfoDTO.StoreTypeEnum.CSV);
        externalCsvEntityStoreInfoDTO.setFileHeader(false);
        externalEntityDTO.setStoreInfo(externalCsvEntityStoreInfoDTO);
        externalEntityDTO.setOriginProcess("MAPPING_SHEETS_MIGRATION");
        externalEntityDTO.setIsActive(true);
        externalEntityDTO.setSerializationId(-1);

        ExternalCsvEntityCollectionRulesDTO externalCsvEntityCollectionRulesDTO = new ExternalCsvEntityCollectionRulesDTO();
        externalCsvEntityCollectionRulesDTO.setStoreType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.CSV);
        List<ExternalEntityFilterDTO> externalEntityFilterList = new ArrayList<>();
        ExternalEntityFilterDTO externalEntityFilterDTO = new ExternalEntityFilterDTO();
        externalEntityFilterDTO.setFilterKey(filterKey);
        externalEntityFilterDTO.setFilterLogic("select ENTITY_ID, ENCODING, PAYLOAD, BUCKET from OSS_CONTEXT_STORE where BUCKET='[BUCKET]'");
        externalEntityFilterList.add(externalEntityFilterDTO);
        externalCsvEntityCollectionRulesDTO.setFilters(externalEntityFilterList);
        externalEntityDTO.setCollectionRules(externalCsvEntityCollectionRulesDTO);
        externalEntityDTO.setIsTransient(false);

        ExternalAttributeDTO externalAttributeDTOone = new ExternalAttributeDTO();
        externalAttributeDTOone.setAttributeKey("att1");
        externalAttributeDTOone.setAttributeName("att1");
        externalAttributeDTOone.setDatatype("STRING");
        externalAttributeDTOone.setSerializationId(-1);
        externalAttributeDTOone.setIsLogicalTime(false);
        externalAttributeDTOone.setIsRequired(false);
        externalAttributeDTOone.setIsUpdateTime(false);
        externalEntityDTO.addAttributesItem(externalAttributeDTOone);

        ExternalAttributeDTO externalAttributeDTOtwo = new ExternalAttributeDTO();
        externalAttributeDTOtwo.setAttributeKey("att2");
        externalAttributeDTOtwo.setAttributeName("att2");
        externalAttributeDTOtwo.setDatatype("STRING");
        externalAttributeDTOtwo.setSerializationId(-1);
        externalAttributeDTOtwo.setIsLogicalTime(false);
        externalAttributeDTOtwo.setIsRequired(false);
        externalAttributeDTOtwo.setIsUpdateTime(false);
        externalEntityDTO.addAttributesItem(externalAttributeDTOtwo);

        return externalEntityDTO;
    }

    public static ExternalSchemaDTO createExternalSchemaDTOQueue() {
        ExternalSchemaDTO dto = new ExternalSchemaDTO();
        dto.setSchemaKey("aLDMCustomerService");
        dto.setSchemaName("aLDMCustomerService");
        dto.setTypeSystem("Oracle");
        dto.setIsReference(false);
        dto.setDescription("Description");
        dto.setAvailability(AvailabilityDTO.SHARED);
        dto.setSchemaType("CSV_FILES");
        ExternalCsvSchemaStoreInfoDTO externalCsvSchemaStoreInfo = new ExternalCsvSchemaStoreInfoDTO();
        externalCsvSchemaStoreInfo.setStoreType(ExternalSchemaStoreInfoDTO.StoreTypeEnum.fromValue("CSV"));
        externalCsvSchemaStoreInfo.setDefaultColumnDelimiter(",");
        externalCsvSchemaStoreInfo.setDefaultDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dto.setStoreInfo(externalCsvSchemaStoreInfo);

        ExternalSchemaDataChannelInfoDTO externalSchemaDataChannelInfoDTO = new ExternalSchemaDataChannelInfoDTO();
        externalSchemaDataChannelInfoDTO.setDataChannelName("ALDMCUSTSVC");
        externalSchemaDataChannelInfoDTO.setSerializationMethod(ExternalSchemaDataChannelInfoDTO.SerializationMethodEnum.SHAREDJSON);
        dto.dataChannelInfo(externalSchemaDataChannelInfoDTO);

        ExternalCsvSchemaCollectionRulesDTO collectionRules = new ExternalCsvSchemaCollectionRulesDTO();
        collectionRules.setInitialLoadChannel("NONE");
        collectionRules.setOngoingChannel("NONE");
        collectionRules.setReplayChannel("NONE");
        collectionRules.setStoreType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.CSV);
        collectionRules.setDefaultInvalidFilenameAction(InvalidFilenameActionTypeDTO.KEEP);

        dto.setCollectionRules(collectionRules);
        dto.setOriginProcess("MAPPING_SHEETS_MIGRATION");
        dto.setInitialCollector("CSV");
        dto.setOngoingCollector("CSV");
        dto.setSelectiveCollector("CSV");
        dto.setIsActive(true);
        dto.setIsReference(false);
        dto.displayType("SQL");

        return dto;
    }

    public static ExternalEntityDTO createExternalEntityDTOQueue() {
        ExternalEntityDTO externalEntityDTO = new ExternalEntityDTO();
        externalEntityDTO.setEntityKey("Queue");
        externalEntityDTO.setEntityName("Queue");
        externalEntityDTO.setSchemaKey("aLDMCustomerService");
        ExternalCsvEntityStoreInfoDTO externalCsvEntityStoreInfoDTO = new ExternalCsvEntityStoreInfoDTO();
        externalCsvEntityStoreInfoDTO.setStoreType(ExternalSqlEntityStoreInfoDTO.StoreTypeEnum.CSV);
        externalCsvEntityStoreInfoDTO.setFileHeader(false);
        externalEntityDTO.setStoreInfo(externalCsvEntityStoreInfoDTO);
        externalEntityDTO.setOriginProcess("MAPPING_SHEETS_MIGRATION");
        externalEntityDTO.setIsActive(true);
        externalEntityDTO.setSerializationId(-1);

        ExternalCsvEntityCollectionRulesDTO externalCsvEntityCollectionRulesDTO = new ExternalCsvEntityCollectionRulesDTO();
        externalCsvEntityCollectionRulesDTO.setStoreType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.CSV);
        List<ExternalEntityFilterDTO> externalEntityFilterList = new ArrayList<>();
        ExternalEntityFilterDTO externalEntityFilterDTO = new ExternalEntityFilterDTO();
        externalEntityFilterDTO.setFilterKey("filterKey");
        externalEntityFilterDTO.setFilterLogic("select ENTITY_ID, ENCODING, PAYLOAD, BUCKET from OSS_CONTEXT_STORE where BUCKET='[BUCKET]'");
        externalEntityFilterList.add(externalEntityFilterDTO);
        externalCsvEntityCollectionRulesDTO.setFilters(externalEntityFilterList);
        externalEntityDTO.setCollectionRules(externalCsvEntityCollectionRulesDTO);
        externalEntityDTO.setIsTransient(false);

        externalEntityDTO.addAttributesItem(createExternalAttributeDTO("queueKey", "Queue Key","STRING",1,false));
        externalEntityDTO.addAttributesItem(createExternalAttributeDTO("queueDesc", "Queue Desc","STRING",2,false));
        externalEntityDTO.addAttributesItem(createExternalAttributeDTO("queueTitle", "Queue title","STRING",3,false));
        externalEntityDTO.addAttributesItem(createExternalAttributeDTO("queueSourceId", "Queue Source Id","STRING",4,false));

        return externalEntityDTO;
    }

    public static ExternalSchemaDTO createExternalSchemaDTOForJson(String schemaKey) {
        ExternalSchemaDTO dto = new ExternalSchemaDTO();
        dto.setSchemaKey(schemaKey);
        dto.setSchemaName(schemaKey);
        dto.setTypeSystem("Oracle");
        dto.setIsReference(false);
        dto.setDescription("Description");

        ExternalKafkaSchemaStoreInfoDTO externalKafkaSchemaStoreInfo = new ExternalKafkaSchemaStoreInfoDTO();
        externalKafkaSchemaStoreInfo.setStoreType(ExternalSchemaStoreInfoDTO.StoreTypeEnum.fromValue("KAFKA"));
        externalKafkaSchemaStoreInfo.setDefaultDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dto.setStoreInfo(externalKafkaSchemaStoreInfo);

        ExternalSchemaDataChannelInfoDTO externalSchemaDataChannelInfoDTO = new ExternalSchemaDataChannelInfoDTO();
        externalSchemaDataChannelInfoDTO.setDataChannelName("DataChannelName");
        externalSchemaDataChannelInfoDTO.setSerializationMethod(ExternalSchemaDataChannelInfoDTO.SerializationMethodEnum.SHAREDJSON);
        dto.dataChannelInfo(externalSchemaDataChannelInfoDTO);

        ExternalKafkaSchemaCollectionRulesDTO collectionRules = new ExternalKafkaSchemaCollectionRulesDTO();
        collectionRules.setInitialLoadChannel("NONE");
        collectionRules.setOngoingChannel("NONE");
        collectionRules.setReplayChannel("NONE");
        collectionRules.setStoreType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.KAFKA);
        collectionRules.setInputDataChannel("inputDataChannel");
        collectionRules.setSkipNodeFromParsing("skipNodeFromParsing");
        collectionRules.setDeleteEventJsonPath("deleteEventJsonPath");
        collectionRules.setDeleteEventOperation("deleteEventOperation");
        collectionRules.setImplicitHandlerPreviousNode("implicitHandlerPreviousNode");
        collectionRules.setImplicitHandlerCurrentNode("implicitHandlerCurrentNode");

        dto.setCollectionRules(collectionRules);
        dto.setOriginProcess("MAPPING_SHEETS_MIGRATION");
        dto.setInitialCollector("KAFKA");
        dto.setOngoingCollector("KAFKA");
        dto.setSelectiveCollector("KAFKA");
        dto.setIsActive(true);
        dto.setIsReference(false);
        dto.displayType("JSON");

        return dto;
    }

    private static ExternalAttributeDTO createExternalAttributeDTO(String key, String attName, String datatype, int serializationId, boolean isLogicalTime) {
        ExternalAttributeDTO queueDTO = new ExternalAttributeDTO();
        queueDTO.setAttributeKey(key);
        queueDTO.setAttributeName(attName);
        queueDTO.setDatatype(datatype);

        queueDTO.setSerializationId(serializationId);
        queueDTO.setIsLogicalTime(isLogicalTime);
        queueDTO.setIsRequired(false);
        queueDTO.setIsUpdateTime(false);
        return queueDTO;
    }


    public static BulkGroupDTO createBulkGroup(String bulkKey, String schemaKey, String entityKey, String filterKey) {
        BulkGroupDTO bulkGroupDTO = new BulkGroupDTO();
        bulkGroupDTO.setBulkGroupKey(bulkKey);
        bulkGroupDTO.setSchemaKey(schemaKey);

        EntityFilterRefDTO entityFilterRefDTO = new EntityFilterRefDTO();
        entityFilterRefDTO.setEntityFilterKey(filterKey);
        entityFilterRefDTO.setEntityKey(entityKey);
        List<EntityFilterRefDTO> entityFilterRefList = new ArrayList<>();
        entityFilterRefList.add(entityFilterRefDTO);
        bulkGroupDTO.setEntityFilters(entityFilterRefList);

        return bulkGroupDTO;
    }

    public static ExternalEntityDTO UpdateQueryEntityDTO(ExternalEntityDTO externalEntityDTOUpdate) {
        ExternalEntityCollectionRulesDTO externalEntityCollectionRulesDTO = new ExternalEntityCollectionRulesDTO();
        externalEntityCollectionRulesDTO.setStoreType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.fromValue("SQL"));
        List<ExternalEntityFilterDTO> externalEntityFilterList = new ArrayList<>();
        ExternalEntityFilterDTO externalEntityFilterDTO = new ExternalEntityFilterDTO();
        List<ExternalEntityFilterDTO> list = new ArrayList<>();
        externalEntityCollectionRulesDTO.setFilters(list);
        externalEntityDTOUpdate.setCollectionRules(externalEntityCollectionRulesDTO);
        return externalEntityDTOUpdate;
    }
}
