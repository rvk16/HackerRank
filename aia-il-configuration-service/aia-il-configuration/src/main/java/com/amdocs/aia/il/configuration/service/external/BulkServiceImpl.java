package com.amdocs.aia.il.configuration.service.external;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.core.web.AiaApiMessage;
import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.external.csv.*;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaStoreInfo;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaStoreInfo;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.exportimport.*;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.external.ExternalEntityRepository;
import com.amdocs.aia.il.configuration.repository.external.ExternalSchemaRepository;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.stream.Collectors.*;

@Service
public class BulkServiceImpl implements BulkService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkServiceImpl.class);

    private static final String EXTERNAL_SCHEMAS_EXPORT_FILE_NAME = "external_schemas_export.csv";
    private static final String EXTERNAL_ENTITIES_EXPORT_FILE_NAME = "external_entities_export.csv";
    private static final String EXTERNAL_ATTRIBUTES_EXPORT_FILE_NAME = "external_attributes_export.csv";

    private final ExternalSchemaRepository externalSchemaRepository;
    private final ExternalEntityRepository externalEntityRepository;

    private final ExternalSchemaService externalSchemaService;
    private final ExternalEntityService externalEntityService;

    private final MessageHelper messageHelper;

    private Map<Boolean,Map<String ,Map< String, List<ExternalAttributeExportCSV>>>> attributesInFileMapByKeys;


    private final CsvInZipImportReader csvInZipImportReader;


    @Autowired
    public BulkServiceImpl(ExternalSchemaRepository externalSchemaRepository, ExternalEntityRepository externalEntityRepository,
                           ExternalSchemaService externalSchemaService, ExternalEntityService externalEntityService, MessageHelper messageHelper,
                           CsvInZipImportReader csvInZipImportReader) {
        this.externalSchemaRepository = externalSchemaRepository;
        this.externalEntityRepository = externalEntityRepository;
        this.externalSchemaService = externalSchemaService;
        this.externalEntityService = externalEntityService;
        this.messageHelper = messageHelper;
        this.csvInZipImportReader = csvInZipImportReader;
    }


    @Override
    public InputStreamResource exportExternalSchemasToZIP(String projectKey) {
        byte[] bytes;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (ZipOutputStream out = new ZipOutputStream(byteArrayOutputStream, StandardCharsets.UTF_8)) {
                out.putNextEntry(new ZipEntry(EXTERNAL_SCHEMAS_EXPORT_FILE_NAME));
                createExternalSchemasCSV(projectKey, out);
                out.putNextEntry(new ZipEntry(EXTERNAL_ENTITIES_EXPORT_FILE_NAME));
                createExternalEntitiesCSV(projectKey, out);
                out.putNextEntry(new ZipEntry(EXTERNAL_ATTRIBUTES_EXPORT_FILE_NAME));
                createExternalAttributesCSV(projectKey, out);
            }
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.EXPORT_TO_ZIP_ERROR);
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(aiaApiMessage)
                    .originalException(e);
        }

        return new InputStreamResource(new ByteArrayInputStream(bytes));
    }

    @Override
    public BulkImportResponseDTO importExternalSchemasFromZIP(String projectKey, MultipartFile file) {
        final BulkImportResponseDTO response = new BulkImportResponseDTO();
        if (file.getSize() == 0) {
            return response;
        }
        importExternalSchemas(projectKey, csvInZipImportReader.readExternalSchemaFromZipFile(file),response);
        importExternalEntities(projectKey, csvInZipImportReader.readExternalEntityFromZipFile(file),csvInZipImportReader.readExternalAttributesFromZipFile(file),response);
        return response;
    }

    private void importExternalEntities(String projectKey, List<ExternalEntityExportCSV> externalEntitiesKeysInFile , List<ExternalAttributeExportCSV> externalAttributesKeysInFile,
                                        BulkImportResponseDTO response)  {
        //delete external entities
        List<ExternalEntityExportCSV> entitiesToDelete =  externalEntitiesKeysInFile.stream()
                .filter( externalSchemaExportCSVDTO -> externalSchemaExportCSVDTO.getToDelete()==Boolean.TRUE).collect(Collectors.toList());

        int notExitEntity = 0;
        for (ExternalEntityExportCSV externalEntityExportCSV : entitiesToDelete) {
            try {
                externalEntityService.delete(projectKey, externalEntityExportCSV.getSchemaKey(), externalEntityExportCSV.getEntityKey());
            }catch (ApiException e) {
                notExitEntity++;
            }
       }
       response.setDeletedEntitiesCount(entitiesToDelete.size()>notExitEntity?entitiesToDelete.size()-notExitEntity:0);

       final List<ExternalEntityExportCSV> externalEntitiesInFileNotToDelete = externalEntitiesKeysInFile.stream().filter(externalEntityExportCSV -> externalEntityExportCSV.getToDelete()!=Boolean.TRUE).collect(Collectors.toList());
       final Map<String,Map<String,ExternalEntityDTO>> existingExternalEntitiesBySchemaKey = externalEntityService.listAll(projectKey).stream().collect(Collectors.groupingBy( ExternalEntityDTO::getSchemaKey,toMap(ExternalEntityDTO::getEntityKey, externalEntityDTO -> externalEntityDTO)));
       final Map<String, ExternalSchemaType> existingSchemaTypesByKeys = externalSchemaRepository.findByProjectKey(projectKey).stream().collect(Collectors.toMap(ExternalSchema::getSchemaKey,ExternalSchema::getSchemaType));

       attributesInFileMapByKeys =
               externalAttributesKeysInFile.stream()//.filter(externalAttributeExportCSVDTO -> externalAttributeExportCSVDTO.getToDelete()!=Boolean.TRUE)
                       .collect(partitioningBy(ExternalAttributeExportCSV::getToDelete,Collectors.groupingBy( ExternalAttributeExportCSV::getSchemaKey,Collectors.groupingBy( ExternalAttributeExportCSV::getEntityKey, Collectors.toList()))));


        final List<ExternalEntityDTO> externalEntityDTOsToAdd = externalEntitiesInFileNotToDelete.stream()
                .filter(externalEntityExportCSV ->  existingSchemaTypesByKeys.containsKey(externalEntityExportCSV.getSchemaKey()))
                .filter(externalEntityExportCSV ->  existingExternalEntitiesBySchemaKey.get(externalEntityExportCSV.getSchemaKey()) == null ||   !(existingExternalEntitiesBySchemaKey.get(externalEntityExportCSV.getSchemaKey()).containsKey(externalEntityExportCSV.getEntityKey())))
                .map(externalEntityExportCSV -> toExternalEntityDTO(externalEntityExportCSV,existingSchemaTypesByKeys.get(externalEntityExportCSV.getSchemaKey()).getStoreType(),true, null)).collect(Collectors.toList());
        SaveElementsResponseDTO saveElementsResponseDTO = null;
        if (!externalEntityDTOsToAdd.isEmpty()) {
            saveElementsResponseDTO = externalEntityService.bulkSave(projectKey,externalEntityDTOsToAdd);
        }
        response.setNewEntitiesCount(saveElementsResponseDTO != null ? saveElementsResponseDTO.getSavedElementsCount().intValue() : 0);

        final List<ExternalEntityDTO> externalEntityDTOsToUpdate = externalEntitiesInFileNotToDelete.stream()
                .filter(externalEntityExportCSV -> existingExternalEntitiesBySchemaKey.containsKey(externalEntityExportCSV.getSchemaKey()))
                .filter(externalEntityExportCSV -> (existingExternalEntitiesBySchemaKey.get(externalEntityExportCSV.getSchemaKey()).containsKey(externalEntityExportCSV.getEntityKey())))
                .map(externalEntityExportCSV -> toExternalEntityDTO(externalEntityExportCSV,existingSchemaTypesByKeys.get(externalEntityExportCSV.getSchemaKey()).getStoreType(),false,existingExternalEntitiesBySchemaKey.get(externalEntityExportCSV.getSchemaKey()).get(externalEntityExportCSV.getEntityKey())))
                .collect(Collectors.toList());
     if (!externalEntityDTOsToUpdate.isEmpty()) {
            externalEntityDTOsToUpdate.forEach( externalEntityDTO -> externalEntityService.update(projectKey,externalEntityDTO.getSchemaKey(),externalEntityDTO));
        }
        response.setModifiedEntitiesCount(externalEntityDTOsToUpdate.size());
    }


    private void importExternalSchemas(String projectKey, List<ExternalSchemaExportCSV> externalSchemasKeysInFile, BulkImportResponseDTO response) {

        //delete external schemas

        final List<String> externalSchemasKeysInFileToDelete = externalSchemasKeysInFile.stream().filter(externalSchemaExportCSV -> externalSchemaExportCSV.getToDelete()==Boolean.TRUE).map(ExternalSchemaExportCSV::getSchemaKey).collect(Collectors.toList());
        int notExitSchema = 0;
        for (String schemaKey : externalSchemasKeysInFileToDelete) {
            try {
                externalSchemaService.delete(projectKey, schemaKey);
            } catch (ApiException e) {
                notExitSchema++;
            }
        }
        response.setDeletedSchemasCount(externalSchemasKeysInFileToDelete.size()> notExitSchema?externalSchemasKeysInFileToDelete.size()- notExitSchema:0);

        final List<ExternalSchemaExportCSV> externalSchemasInFileNotToDelete = externalSchemasKeysInFile.stream().filter(externalSchemaExportCSV -> externalSchemaExportCSV.getToDelete()!=Boolean.TRUE).collect(Collectors.toList());

        final List<ExternalSchema> existingSchemas = externalSchemaRepository.findByProjectKey(projectKey);
        final Set<String> existingExternalSchemaKeys = existingSchemas.stream().map(ExternalSchema::getSchemaKey).collect(Collectors.toSet());


        final List<ExternalSchemaDTO> externalSchemaDTOsToAdd = externalSchemasInFileNotToDelete.stream().filter(externalSchemaExportCSV -> !existingExternalSchemaKeys.contains(externalSchemaExportCSV.getSchemaKey()))
                .map(this::toExternalSchemaDTO).collect(Collectors.toList());
        SaveElementsResponseDTO saveElementsResponseDTO = null;
        if (!externalSchemaDTOsToAdd.isEmpty()) {
            saveElementsResponseDTO = externalSchemaService.bulkSave(projectKey,externalSchemaDTOsToAdd);
        }
        response.setNewSchemasCount(saveElementsResponseDTO != null ? saveElementsResponseDTO.getSavedElementsCount().intValue() : 0);

        final List<ExternalSchemaDTO> ExternalSchemaDTOsToUpdate = externalSchemasInFileNotToDelete.stream().filter(externalSchemaExportCSV -> existingExternalSchemaKeys.contains(externalSchemaExportCSV.getSchemaKey()))
                .map(this::toExternalSchemaDTO).collect(Collectors.toList());
        if (!ExternalSchemaDTOsToUpdate.isEmpty()) {
            ExternalSchemaDTOsToUpdate.forEach( externalSchemaDTO -> externalSchemaService.update(projectKey,externalSchemaDTO.getSchemaKey(),externalSchemaDTO) );
        }
        response.setModifiedSchemasCount(ExternalSchemaDTOsToUpdate.size());
    }


    private void createExternalSchemasCSV(final String projectKey,final ZipOutputStream out ) {
        final List<ExternalSchema> externalSchema = externalSchemaRepository.findByProjectKey(projectKey);
        final CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING);
        final CsvSchema csvSchema = mapper.schemaFor(ExternalSchemaExportCSV.class).withHeader()/*.withoutQuoteChar()*/;
        final List<AbstractCsvExternalModel> externalSchemaExportCSVS = externalSchema.stream().map(this::createExternalSchemaExportCSVDTO).collect(Collectors.toList());
        writeCSvFile(out, mapper, csvSchema, externalSchemaExportCSVS);
    }

    private void writeCSvFile(ZipOutputStream out, CsvMapper mapper, CsvSchema csvSchema, List<AbstractCsvExternalModel> externalSchemaExportCSVS) {
        try{
           mapper.writer(csvSchema).without(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET).writeValue(out, externalSchemaExportCSVS);
        } catch (IOException e) {
            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.EXPORT_JACKSON_ERROR);
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(aiaApiMessage)
                    .originalException(e);

        }
    }

    private ExternalSchemaExportCSV createExternalSchemaExportCSVDTO(ExternalSchema externalSchema) {
        ExternalSchemaExportCSV externalSchemaExportCSV = new ExternalSchemaExportCSV();
        externalSchemaExportCSV.setSchemaKey(externalSchema.getSchemaKey());
        externalSchemaExportCSV.setSchemaName(externalSchema.getName());
        externalSchemaExportCSV.setSchemaType(externalSchema.getSchemaType().name());
        externalSchemaExportCSV.setActive(externalSchema.getIsActive());
        externalSchemaExportCSV.setDescription(externalSchema.getDescription() != null? externalSchema.getDescription().replace("\n", "").replace("\r", "") : "");
        externalSchemaExportCSV.setTypeSystem(externalSchema.getTypeSystem());
        externalSchemaExportCSV.setReference(externalSchema.getIsReference());
        externalSchemaExportCSV.setStoreType(externalSchema.getSchemaType() != null ? externalSchema.getSchemaType().getStoreType() : null);
        externalSchemaExportCSV.setSerializationMethod(externalSchema.getDataChannelInfo().getSerializationMethod());
        externalSchemaExportCSV.setAvailability(externalSchema.getAvailability().toString());
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
                externalSchemaExportCSV.setDefaultInvalidFilenameAction(((ExternalCsvSchemaCollectionRules)externalSchema.getCollectionRules()).getDefaultInvalidFilenameAction().toString());
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

        }
        return externalSchemaExportCSV;
    }


    private void createExternalEntitiesCSV(final String projectKey,final ZipOutputStream out ) {

        final List<ExternalEntity> externalEntities = externalEntityRepository.findByProjectKey(projectKey);
        final  CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING);

        final CsvSchema csvSchema = mapper.schemaFor(ExternalEntityExportCSV.class).withHeader()/*.withoutQuoteChar()*/;

        final List<AbstractCsvExternalModel> externalEntityExportCSVS = externalEntities.stream().map(this::createExternalEntityExportCSVDTO).collect(Collectors.toList());
        writeCSvFile(out, mapper, csvSchema, externalEntityExportCSVS);
//        try {
//            mapper.writer(schema).without(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET).writeValue(out, externalEntityExportCSVS);
//
//        } catch (IOException e) {
//            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.EXPORT_JACKSON_ERROR);
//            throw new AiaApiException()
//                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
//                    .message(aiaApiMessage)
//                    .originalException(e);
//        }
    }

    private ExternalEntityExportCSV createExternalEntityExportCSVDTO(ExternalEntity externalEntity) {
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
                externalEntityExportCSV.setInvalidFilenameAction(((ExternalCsvEntityCollectionRules)externalEntity.getCollectionRules()).getFileInvalidNameAction().name());
                break;
          case ExternalSchemaStoreTypes.KAFKA:
                externalEntityExportCSV.setJsonTypeValue(((ExternalKafkaEntityStoreInfo)externalEntity.getStoreInfo()).getJsonTypeValue());
                externalEntityExportCSV.setJsonTypePath(((ExternalKafkaEntityStoreInfo)externalEntity.getStoreInfo()).getJsonTypePath());
                externalEntityExportCSV.setRelativePaths(((ExternalKafkaEntityStoreInfo)externalEntity.getStoreInfo()).getRelativePaths());
                externalEntityExportCSV.setMergedNodes(((ExternalKafkaEntityStoreInfo)externalEntity.getStoreInfo()).getMergedNodes());
                break;

        }
        return externalEntityExportCSV;
    }



    private void createExternalAttributesCSV(final String projectKey,final ZipOutputStream out ) {
        final List<ExternalEntity> externalEntities = externalEntityRepository.findByProjectKey(projectKey);
        final CsvMapper mapper = new CsvMapper();
        final CsvSchema csvSchema = mapper.schemaFor(ExternalAttributeExportCSV.class).withHeader();
        mapper.enable(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING);

        final List<AbstractCsvExternalModel> externalAttributeExportCSVS = externalEntities.stream()
                .flatMap(externalEntity -> externalEntity.getAttributes().stream()
                        .map(att ->createExternalAttributeExportCSVDTO(att,externalEntity.getSchemaKey(),externalEntity.getEntityKey(),externalEntity.getStoreInfo().getType()))).collect(Collectors.toList());


        writeCSvFile(out, mapper, csvSchema, externalAttributeExportCSVS);
        //        try {
//            final ObjectWriter writer = mapper.writer(schema);
//            writer.without(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET).writeValue(out, externalAttributeExportCSVS);
//
//        } catch (IOException e) {
//            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.EXPORT_JACKSON_ERROR);
//            throw new AiaApiException()
//                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
//                    .message(aiaApiMessage)
//                    .originalException(e);
//
//        }

    }

    private ExternalAttributeExportCSV createExternalAttributeExportCSVDTO(ExternalAttribute externalAttribute, String schemaKey, String entityKey, String entityType) {
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
        switch (entityType) {
            case ExternalSchemaStoreTypes.KAFKA:
                externalAttributeExportCSV.setDateFormat(((ExternalKafkaAttributeStoreInfo) externalAttribute.getStoreInfo()).getDateFormat());
                externalAttributeExportCSV.setJsonPath(((ExternalKafkaAttributeStoreInfo) externalAttribute.getStoreInfo()).getJsonPath());
                break;
            case ExternalSchemaStoreTypes.CSV:
                externalAttributeExportCSV.setDateFormat(((ExternalCsvAttributeStoreInfo) externalAttribute.getStoreInfo()).getDateFormat());
                break;
        }
        return externalAttributeExportCSV;
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
        externalSchemaDTO.setAvailability(externalSchemaExportCSV.getAvailability() != null && externalSchemaExportCSV.getAvailability().equals(AvailabilityDTO.SHARED.toString()) ? AvailabilityDTO.SHARED : AvailabilityDTO.EXTERNAL);
        externalSchemaDTO.setSubjectAreaName(externalSchemaDTO.getAvailability()==AvailabilityDTO.EXTERNAL?null: externalSchemaExportCSV.getSubjectAreaName());
        //TODO generate key????
        externalSchemaDTO.setSubjectAreaKey(externalSchemaDTO.getAvailability()==AvailabilityDTO.EXTERNAL?null: externalSchemaExportCSV.getSubjectAreaName());
        return externalSchemaDTO;
    }

    private ExternalSchemaStoreInfoDTO toStoreInfo(ExternalSchemaExportCSV externalSchemaExportCSV) {
        if(externalSchemaExportCSV.getStoreType() != null){
            switch (externalSchemaExportCSV.getStoreType()){
                case ExternalSchemaStoreTypes.CSV:
                    return  new ExternalCsvSchemaStoreInfoDTO()
                            .defaultColumnDelimiter(externalSchemaExportCSV.getDefaultDateFormat())
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
                    return null;
            }
        }else{
            return null;
        }
    }

    private ExternalSchemaCollectionRulesDTO toCollectionRules(ExternalSchemaExportCSV externalSchemaExportCSV) {
        if(  externalSchemaExportCSV.getStoreType() != null){
            switch (externalSchemaExportCSV.getStoreType()){
                case ExternalSchemaStoreTypes.CSV:
                    ExternalCsvSchemaCollectionRulesDTO externalCsvSchemaCollectionRulesDTO = new ExternalCsvSchemaCollectionRulesDTO();
                    setExternalCollectionRules(externalSchemaExportCSV, externalCsvSchemaCollectionRulesDTO);
                    externalCsvSchemaCollectionRulesDTO.setDefaultInvalidFilenameAction(toInvalidFilenameActionTypeDTO(externalSchemaExportCSV.getDefaultInvalidFilenameAction()));
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
                    return null;
            }
        }else{
            return null;
        }

    }

    private void setExternalCollectionRules(ExternalSchemaExportCSV externalSchemaExportCSV, ExternalSchemaCollectionRulesDTO externalSchemaCollectionRulesDTO) {
        externalSchemaCollectionRulesDTO.setOngoingChannel(externalSchemaExportCSV.getOngoingChannel());
        externalSchemaCollectionRulesDTO.setInitialLoadChannel(externalSchemaExportCSV.getInitialLoadChannel());
        externalSchemaCollectionRulesDTO.setReplayChannel(externalSchemaExportCSV.getReplayChannel());
        externalSchemaCollectionRulesDTO.setPartialLoadRelativeURL(externalSchemaExportCSV.getPartialLoadRelativeURL());
        externalSchemaCollectionRulesDTO.setInitialLoadRelativeURL(externalSchemaExportCSV.getInitialLoadRelativeURL());
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

    public ExternalSchemaDataChannelInfoDTO toExternalSchemaDataChannelInfoDTO(String SerializationMethod) {
        final ExternalSchemaDataChannelInfoDTO externalSchemaDataChannelInfoDTO = new ExternalSchemaDataChannelInfoDTO();
        externalSchemaDataChannelInfoDTO.setSerializationMethod(ExternalSchemaDataChannelInfoDTO.SerializationMethodEnum.fromValue(SerializationMethod));
        return externalSchemaDataChannelInfoDTO;

    }


    private ExternalEntityDTO toExternalEntityDTO(ExternalEntityExportCSV externalEntityExportCSV, String schemaType, boolean isNew , ExternalEntityDTO externalEntity) {
        ExternalEntityDTO externalEntityDTO = new ExternalEntityDTO();
        externalEntityDTO.setSchemaKey(externalEntityExportCSV.getSchemaKey());
        externalEntityDTO.setEntityKey(externalEntityExportCSV.getEntityKey());
        externalEntityDTO.setEntityName(externalEntityExportCSV.getEntityName());
        externalEntityDTO.setDescription(externalEntityExportCSV.getDescription());
        externalEntityDTO.setSerializationId(externalEntityExportCSV.getSerializationId());
        externalEntityDTO.setIsTransient(externalEntityExportCSV.getTransient());
        externalEntityDTO.setIsTransaction(externalEntityExportCSV.getTransaction());
        externalEntityDTO.setStoreInfo(toStoreInfoEntity(externalEntityExportCSV,schemaType));
        externalEntityDTO.setCollectionRules(toCollectionRulesEntity(externalEntityExportCSV,schemaType));
        externalEntityDTO.setAttributes(isNew? getAttributes(externalEntityExportCSV, schemaType): mergeAttribute (externalEntityExportCSV, schemaType,externalEntity.getAttributes()));
        externalEntityDTO.setIsActive(externalEntityExportCSV.getActive());
        externalEntityDTO.setOriginProcess(isNew? OriginProcess.IMPLEMENTATION.name(): externalEntity.getOriginProcess() );
        return externalEntityDTO;
    }

    private List<ExternalAttributeDTO> getAttributes(ExternalEntityExportCSV externalEntityExportCSV, String schemaType) {

        List<ExternalAttributeExportCSV> attributeExportCSVDTOS =  attributesInFileMapByKeys.get(false).get(externalEntityExportCSV.getSchemaKey()).get(externalEntityExportCSV.getEntityKey());
       if(attributeExportCSVDTOS != null){
          return attributeExportCSVDTOS.stream().map(attributeExportCSVDTO -> toExternalAttributeDTO(attributeExportCSVDTO,schemaType))
                   .sorted(Comparator.comparingInt(ExternalAttributeDTO::getSerializationId))
                   .collect(Collectors.toList());
       }
       return null;
    }

    private List<ExternalAttributeDTO> mergeAttribute(ExternalEntityExportCSV externalEntityExportCSV, String schemaType, List <ExternalAttributeDTO> exitingExternalAttributeDTOS ) {
        List<ExternalAttributeDTO> attributes = new ArrayList<>();

        Map<String, ExternalAttributeDTO > existingAttributeMapByKey = exitingExternalAttributeDTOS.stream().collect(Collectors.toMap(ExternalAttributeDTO::getAttributeKey,externalAttributeDTO -> externalAttributeDTO));
        List<ExternalAttributeExportCSV> attributeExportInCSVList = null;
        if( attributesInFileMapByKeys.get(false)!= null && attributesInFileMapByKeys.get(false).get(externalEntityExportCSV.getSchemaKey())!= null) {
            attributeExportInCSVList = attributesInFileMapByKeys.get(false).get(externalEntityExportCSV.getSchemaKey()).get(externalEntityExportCSV.getEntityKey());
        }
        List<ExternalAttributeExportCSV> attributeToDelete =  null;
        if( attributesInFileMapByKeys.get(true)!= null && attributesInFileMapByKeys.get(true).get(externalEntityExportCSV.getSchemaKey())!= null ){
            attributeToDelete = attributesInFileMapByKeys.get(true).get(externalEntityExportCSV.getSchemaKey()).get(externalEntityExportCSV.getEntityKey());
        }
        //delete from existing entity list
        if(attributeToDelete !=null && !exitingExternalAttributeDTOS.isEmpty()){
            attributeToDelete.forEach(att-> existingAttributeMapByKey.remove(att.getAttributeKey()) );
        }
        if(attributeExportInCSVList != null) {
            attributeExportInCSVList.forEach(attributeExportCSVDTO -> attributes.add(toExternalAttributeDTO(attributeExportCSVDTO, schemaType)));
            Set<String> attributeExportKeysInCSV = attributeExportInCSVList.stream().map(ExternalAttributeExportCSV::getAttributeKey ).collect(Collectors.toSet());
            existingAttributeMapByKey.forEach((attKey, att) -> {
                if (!attributeExportKeysInCSV.contains(attKey)) {
                    attributes.add(att);
                }
            });
        }else{
            attributes.addAll(new ArrayList<>(existingAttributeMapByKey.values()));
        }


       return  attributes.stream().sorted(Comparator.comparingInt(ExternalAttributeDTO::getSerializationId)).collect(Collectors.toList());
    }

    private ExternalAttributeDTO toExternalAttributeDTO(ExternalAttributeExportCSV attributeExportCSVDTO, String schemaType) {
        ExternalAttributeDTO attributeDTO = new ExternalAttributeDTO();
        attributeDTO.setAttributeKey(attributeExportCSVDTO.getAttributeKey());
        attributeDTO.setAttributeName(attributeExportCSVDTO.getAttributeName());
        attributeDTO.setDescription(attributeExportCSVDTO.getDescription());
        attributeDTO.setDatatype(attributeExportCSVDTO.getDatatype());
        attributeDTO.setLogicalDatatype(attributeExportCSVDTO.getLogicalDatatype());
        attributeDTO.setSerializationId(attributeExportCSVDTO.getSerializationId());
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
            default: return null;
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
            default: return null;
        }

    }

    private ExternalEntityCollectionRulesDTO toCollectionRulesEntity(ExternalEntityExportCSV externalEntityExportCSV, String schemaType) {
        if(schemaType != null){
            switch (schemaType){
                case ExternalSchemaStoreTypes.CSV:
                    ExternalCsvEntityCollectionRulesDTO externalCsvEntityCollectionRulesDTO = new ExternalCsvEntityCollectionRulesDTO();
                    externalCsvEntityCollectionRulesDTO.setStoreType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.CSV);
                    externalCsvEntityCollectionRulesDTO.setInvalidFilenameAction(toInvalidFilenameActionTypeDTO(externalEntityExportCSV.getInvalidFilenameAction()));
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
                    return null;
            }
        }else{
            return null;
        }
    }

    private String getDisplayType(ExternalSchemaExportCSV externalEntityExportCSVDTO) {
        return messageHelper.format("external.schema.type." + externalEntityExportCSVDTO.getSchemaType());
    }



}