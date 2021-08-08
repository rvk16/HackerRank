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
import com.amdocs.aia.il.configuration.export.ExternalAttributeExportCSVDTO;
import com.amdocs.aia.il.configuration.export.ExternalEntityExportCSVDTO;
import com.amdocs.aia.il.configuration.export.ExternalSchemaExportCSVDTO;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.external.ExternalEntityRepository;
import com.amdocs.aia.il.configuration.repository.external.ExternalSchemaRepository;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import java.util.zip.ZipInputStream;
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

    private Map<Boolean,Map<String ,Map< String, List<ExternalAttributeExportCSVDTO>>>> attributesInFileMapByKeys;


    @Autowired
    public BulkServiceImpl(ExternalSchemaRepository externalSchemaRepository, ExternalEntityRepository externalEntityRepository, ExternalSchemaService externalSchemaService, ExternalEntityService externalEntityService, MessageHelper messageHelper) {
        this.externalSchemaRepository = externalSchemaRepository;
        this.externalEntityRepository = externalEntityRepository;
        this.externalSchemaService = externalSchemaService;
        this.externalEntityService = externalEntityService;
        this.messageHelper = messageHelper;
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
        importExternalSchemas(projectKey, readExternalSchemaFromZipFile(file),response);
        importExternalEntities(projectKey, readExternalEntityFromZipFile(file),readExternalAttributesFromZipFile(file),response);
        return response;
    }

    private List<ExternalSchemaExportCSVDTO> readExternalSchemaFromZipFile(MultipartFile file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            if (!isZipFile(file.getOriginalFilename(), file.getInputStream())) {
                final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.IMPORT_FROM_ZIP_NOT_ZIP_ERROR);
                throw new AiaApiException()
                        .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                        .message(aiaApiMessage);
            }
            findFileInZip(zipInputStream, EXTERNAL_SCHEMAS_EXPORT_FILE_NAME);
            final CsvMapper mapper = new CsvMapper();
            final CsvSchema schema = mapper.schemaFor(ExternalSchemaExportCSVDTO.class).withHeader().withNullValue("");
            final MappingIterator<ExternalSchemaExportCSVDTO> externalSchemasInFileIter = mapper.readerFor(ExternalSchemaExportCSVDTO.class).with(schema).readValues(zipInputStream);
            return externalSchemasInFileIter.readAll();
        } catch (IOException e) {
            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.IMPORT_FROM_ZIP_ERROR);
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(aiaApiMessage)
                    .originalException(e);
        }
    }

    private List<ExternalEntityExportCSVDTO> readExternalEntityFromZipFile(MultipartFile file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            findFileInZip(zipInputStream, EXTERNAL_ENTITIES_EXPORT_FILE_NAME);
            final CsvMapper mapper = new CsvMapper();
            final CsvSchema schema = mapper.schemaFor(ExternalEntityExportCSVDTO.class).withHeader().withNullValue("");
            final MappingIterator<ExternalEntityExportCSVDTO> externalSchemasInFileIter = mapper.readerFor(ExternalEntityExportCSVDTO.class).with(schema).readValues(zipInputStream);
            return externalSchemasInFileIter.readAll();
        } catch (IOException e) {
            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.IMPORT_FROM_ZIP_ERROR);
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(aiaApiMessage)
                    .originalException(e);
        }
    }

    private List<ExternalAttributeExportCSVDTO> readExternalAttributesFromZipFile(MultipartFile file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            findFileInZip(zipInputStream, EXTERNAL_ATTRIBUTES_EXPORT_FILE_NAME);
            final CsvMapper mapper = new CsvMapper();
            final CsvSchema schema = mapper.schemaFor(ExternalAttributeExportCSVDTO.class).withHeader().withNullValue("");
            final MappingIterator<ExternalAttributeExportCSVDTO> externalSchemasInFileIter = mapper.readerFor(ExternalAttributeExportCSVDTO.class).with(schema).readValues(zipInputStream);
            return externalSchemasInFileIter.readAll();
        } catch (IOException e) {
            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.IMPORT_FROM_ZIP_ERROR);
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(aiaApiMessage)
                    .originalException(e);
        }
    }

    private void importExternalEntities(String projectKey,List<ExternalEntityExportCSVDTO> externalEntitiesKeysInFile , List<ExternalAttributeExportCSVDTO> externalAttributesKeysInFile,
                                        BulkImportResponseDTO response)  {
        //delete external entities
        List<ExternalEntityExportCSVDTO> entitiesToDelete =  externalEntitiesKeysInFile.stream()
                .filter( externalSchemaExportCSVDTO -> externalSchemaExportCSVDTO.getToDelete()==Boolean.TRUE).collect(Collectors.toList());

        int notExitEntity = 0;
        for (ExternalEntityExportCSVDTO externalEntityExportCSVDTO : entitiesToDelete) {
            try {
                externalEntityService.delete(projectKey,externalEntityExportCSVDTO.getSchemaKey(),externalEntityExportCSVDTO.getEntityKey());
            }catch (ApiException e) {
                notExitEntity++;
            }
       }
       response.setDeletedEntitiesCount(entitiesToDelete.size()>notExitEntity?entitiesToDelete.size()-notExitEntity:0);

       final List<ExternalEntityExportCSVDTO> externalEntitiesInFileNotToDelete = externalEntitiesKeysInFile.stream().filter( externalEntityExportCSVDTO -> externalEntityExportCSVDTO.getToDelete()!=Boolean.TRUE).collect(Collectors.toList());
       final Map<String,Map<String,ExternalEntityDTO>> existingExternalEntitiesBySchemaKey = externalEntityService.listAll(projectKey).stream().collect(Collectors.groupingBy( ExternalEntityDTO::getSchemaKey,toMap(ExternalEntityDTO::getEntityKey, externalEntityDTO -> externalEntityDTO)));
       final Map<String, ExternalSchemaType> existingSchemaTypesByKeys = externalSchemaRepository.findByProjectKey(projectKey).stream().collect(Collectors.toMap(ExternalSchema::getSchemaKey,ExternalSchema::getSchemaType));

       attributesInFileMapByKeys =
               externalAttributesKeysInFile.stream()//.filter(externalAttributeExportCSVDTO -> externalAttributeExportCSVDTO.getToDelete()!=Boolean.TRUE)
                       .collect(partitioningBy(ExternalAttributeExportCSVDTO::getToDelete,Collectors.groupingBy( ExternalAttributeExportCSVDTO::getSchemaKey,Collectors.groupingBy( ExternalAttributeExportCSVDTO::getEntityKey, Collectors.toList()))));


        final List<ExternalEntityDTO> externalEntityDTOsToAdd = externalEntitiesInFileNotToDelete.stream()
                .filter(externalEntityExportCSVDTO ->  existingSchemaTypesByKeys.containsKey(externalEntityExportCSVDTO.getSchemaKey()))
                .filter(externalEntityExportCSVDTO ->  existingExternalEntitiesBySchemaKey.get(externalEntityExportCSVDTO.getSchemaKey()) == null ||   !(existingExternalEntitiesBySchemaKey.get(externalEntityExportCSVDTO.getSchemaKey()).containsKey(externalEntityExportCSVDTO.getEntityKey())))
                .map(externalEntityExportCSVDTO -> toExternalEntityDTO(externalEntityExportCSVDTO,existingSchemaTypesByKeys.get(externalEntityExportCSVDTO.getSchemaKey()).getStoreType(),true, null)).collect(Collectors.toList());
        SaveElementsResponseDTO saveElementsResponseDTO = null;
        if (!externalEntityDTOsToAdd.isEmpty()) {
            saveElementsResponseDTO = externalEntityService.bulkSave(projectKey,externalEntityDTOsToAdd);
        }
        response.setNewEntitiesCount(saveElementsResponseDTO != null ? saveElementsResponseDTO.getSavedElementsCount().intValue() : 0);

        final List<ExternalEntityDTO> externalEntityDTOsToUpdate = externalEntitiesInFileNotToDelete.stream()
                .filter(externalEntityExportCSVDTO -> existingExternalEntitiesBySchemaKey.containsKey(externalEntityExportCSVDTO.getSchemaKey()))
                .filter(externalEntityExportCSVDTO -> (existingExternalEntitiesBySchemaKey.get(externalEntityExportCSVDTO.getSchemaKey()).containsKey(externalEntityExportCSVDTO.getEntityKey())))
                .map(externalEntityExportCSVDTO -> toExternalEntityDTO(externalEntityExportCSVDTO,existingSchemaTypesByKeys.get(externalEntityExportCSVDTO.getSchemaKey()).getStoreType(),false,existingExternalEntitiesBySchemaKey.get(externalEntityExportCSVDTO.getSchemaKey()).get(externalEntityExportCSVDTO.getEntityKey())))
                .collect(Collectors.toList());
     if (!externalEntityDTOsToUpdate.isEmpty()) {
            externalEntityDTOsToUpdate.forEach( externalEntityDTO -> externalEntityService.update(projectKey,externalEntityDTO.getSchemaKey(),externalEntityDTO));
        }
        response.setModifiedEntitiesCount(externalEntityDTOsToUpdate.size());
    }


    private void importExternalSchemas(String projectKey, List<ExternalSchemaExportCSVDTO> externalSchemasKeysInFile, BulkImportResponseDTO response) {

        //delete external schemas

        final List<String> externalSchemasKeysInFileToDelete = externalSchemasKeysInFile.stream().filter( externalSchemaExportCSVDTO -> externalSchemaExportCSVDTO.getToDelete()==Boolean.TRUE).map(ExternalSchemaExportCSVDTO::getSchemaKey).collect(Collectors.toList());
        int notExitSchema = 0;
        for (String schemaKey : externalSchemasKeysInFileToDelete) {
            try {
                externalSchemaService.delete(projectKey, schemaKey);
            } catch (ApiException e) {
                notExitSchema++;
            }
        }
        response.setDeletedSchemasCount(externalSchemasKeysInFileToDelete.size()> notExitSchema?externalSchemasKeysInFileToDelete.size()- notExitSchema:0);

        final List<ExternalSchemaExportCSVDTO> externalSchemasInFileNotToDelete = externalSchemasKeysInFile.stream().filter( externalSchemaExportCSVDTO -> externalSchemaExportCSVDTO.getToDelete()!=Boolean.TRUE).collect(Collectors.toList());

        final List<ExternalSchema> existingSchemas = externalSchemaRepository.findByProjectKey(projectKey);
        final Set<String> existingExternalSchemaKeys = existingSchemas.stream().map(ExternalSchema::getSchemaKey).collect(Collectors.toSet());


        final List<ExternalSchemaDTO> externalSchemaDTOsToAdd = externalSchemasInFileNotToDelete.stream().filter(externalSchemaExportCSVDTO -> !existingExternalSchemaKeys.contains(externalSchemaExportCSVDTO.getSchemaKey()))
                .map(this::toExternalSchemaDTO).collect(Collectors.toList());
        SaveElementsResponseDTO saveElementsResponseDTO = null;
        if (!externalSchemaDTOsToAdd.isEmpty()) {
            saveElementsResponseDTO = externalSchemaService.bulkSave(projectKey,externalSchemaDTOsToAdd);
        }
        response.setNewSchemasCount(saveElementsResponseDTO != null ? saveElementsResponseDTO.getSavedElementsCount().intValue() : 0);

        final List<ExternalSchemaDTO> ExternalSchemaDTOsToUpdate = externalSchemasInFileNotToDelete.stream().filter(externalSchemaExportCSVDTO -> existingExternalSchemaKeys.contains(externalSchemaExportCSVDTO.getSchemaKey()))
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
        final CsvSchema csvSchema = mapper.schemaFor(ExternalSchemaExportCSVDTO.class).withHeader()/*.withoutQuoteChar()*/;
        final List<ExternalSchemaExportCSVDTO> externalSchemaExportCSVDTOS = externalSchema.stream().map(this::createExternalSchemaExportCSVDTO).collect(Collectors.toList());
        try{
           mapper.writer(csvSchema).without(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET).writeValue(out, externalSchemaExportCSVDTOS);
        } catch (IOException e) {
            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.EXPORT_JACKSON_ERROR);
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(aiaApiMessage)
                    .originalException(e);

        }
    }

    private ExternalSchemaExportCSVDTO createExternalSchemaExportCSVDTO(ExternalSchema externalSchema) {
        ExternalSchemaExportCSVDTO externalSchemaExportCSVDTO = new ExternalSchemaExportCSVDTO();
        externalSchemaExportCSVDTO.setSchemaKey(externalSchema.getSchemaKey());
        externalSchemaExportCSVDTO.setSchemaName(externalSchema.getName());
        externalSchemaExportCSVDTO.setSchemaType(externalSchema.getSchemaType().name());
        externalSchemaExportCSVDTO.setActive(externalSchema.getIsActive());
        externalSchemaExportCSVDTO.setDescription(externalSchema.getDescription() != null? externalSchema.getDescription().replace("\n", "").replace("\r", "") : "");
        externalSchemaExportCSVDTO.setTypeSystem(externalSchema.getTypeSystem());
        externalSchemaExportCSVDTO.setReference(externalSchema.getIsReference());
        externalSchemaExportCSVDTO.setStoreType(externalSchema.getSchemaType() != null ? externalSchema.getSchemaType().getStoreType() : null);
        externalSchemaExportCSVDTO.setSerializationMethod(externalSchema.getDataChannelInfo().getSerializationMethod());
        externalSchemaExportCSVDTO.setAvailability(externalSchema.getAvailability().toString());
        externalSchemaExportCSVDTO.setSubjectAreaName(externalSchema.getSubjectAreaName());

        externalSchemaExportCSVDTO.setOngoingChannel(externalSchema.getCollectionRules().getOngoingChannel()!= null?externalSchema.getCollectionRules().getOngoingChannel().name():null);
        externalSchemaExportCSVDTO.setInitialLoadChannel(externalSchema.getCollectionRules().getInitialLoadChannel()!= null? externalSchema.getCollectionRules().getInitialLoadChannel().name():null);
        externalSchemaExportCSVDTO.setReplayChannel(externalSchema.getCollectionRules().getReplayChannel() != null?externalSchema.getCollectionRules().getReplayChannel().name():null);
        externalSchemaExportCSVDTO.setInitialLoadRelativeURL(externalSchema.getCollectionRules().getInitialLoadRelativeURL());
        externalSchemaExportCSVDTO.setPartialLoadRelativeURL(externalSchema.getCollectionRules().getPartialLoadRelativeURL());

        switch (externalSchema.getStoreInfo().getType()){
            case ExternalSchemaStoreTypes.SQL:
                externalSchemaExportCSVDTO.setDatabaseType(((ExternalSqlSchemaStoreInfo)externalSchema.getStoreInfo()).getDatabaseType());
                break;
            case ExternalSchemaStoreTypes.CSV:
                externalSchemaExportCSVDTO.setDefaultDateFormat(((ExternalCsvSchemaStoreInfo)externalSchema.getStoreInfo()).getDefaultDateFormat());
                externalSchemaExportCSVDTO.setDefaultColumnDelimiter(String.valueOf(((ExternalCsvSchemaStoreInfo)externalSchema.getStoreInfo()).getDefaultColumnDelimiter()));
                externalSchemaExportCSVDTO.setDefaultInvalidFilenameAction(((ExternalCsvSchemaCollectionRules)externalSchema.getCollectionRules()).getDefaultInvalidFilenameAction().toString());
                break;
            case ExternalSchemaStoreTypes.KAFKA:
                externalSchemaExportCSVDTO.setDefaultDateFormat(((ExternalKafkaSchemaStoreInfo)externalSchema.getStoreInfo()).getDefaultDateFormat());
                externalSchemaExportCSVDTO.setInputDataChannel(((ExternalKafkaSchemaCollectionRules)externalSchema.getCollectionRules()).getInputDataChannel());
                externalSchemaExportCSVDTO.setSkipNodeFromParsing(((ExternalKafkaSchemaCollectionRules)externalSchema.getCollectionRules()).getSkipNodeFromParsing());
                externalSchemaExportCSVDTO.setDeleteEventJsonPath(((ExternalKafkaSchemaCollectionRules)externalSchema.getCollectionRules()).getDeleteEventJsonPath());
                externalSchemaExportCSVDTO.setDeleteEventOperation(((ExternalKafkaSchemaCollectionRules)externalSchema.getCollectionRules()).getDeleteEventOperation());
                externalSchemaExportCSVDTO.setImplicitHandlerPreviousNode(((ExternalKafkaSchemaCollectionRules)externalSchema.getCollectionRules()).getImplicitHandlerPreviousNode());
                externalSchemaExportCSVDTO.setImplicitHandlerCurrentNode(((ExternalKafkaSchemaCollectionRules)externalSchema.getCollectionRules()).getImplicitHandlerCurrentNode());
                break;

        }
        return externalSchemaExportCSVDTO;
    }


    private void createExternalEntitiesCSV(final String projectKey,final ZipOutputStream out ) {

        final List<ExternalEntity> externalEntities = externalEntityRepository.findByProjectKey(projectKey);
        final  CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING);

        final CsvSchema schema = mapper.schemaFor(ExternalEntityExportCSVDTO.class).withHeader()/*.withoutQuoteChar()*/;

        final List<ExternalEntityExportCSVDTO> externalEntityExportCSVDTOS = externalEntities.stream().map(this::createExternalEntityExportCSVDTO).collect(Collectors.toList());
        try {
            mapper.writer(schema).without(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET).writeValue(out,externalEntityExportCSVDTOS);

        } catch (IOException e) {
            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.EXPORT_JACKSON_ERROR);
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(aiaApiMessage)
                    .originalException(e);
        }
    }

    private ExternalEntityExportCSVDTO createExternalEntityExportCSVDTO(ExternalEntity externalEntity) {
        ExternalEntityExportCSVDTO externalEntityExportCSVDTO = new ExternalEntityExportCSVDTO();
        externalEntityExportCSVDTO.setSchemaKey(externalEntity.getSchemaKey());
        externalEntityExportCSVDTO.setEntityKey(externalEntity.getEntityKey());
        externalEntityExportCSVDTO.setActive(externalEntity.getIsActive());
        externalEntityExportCSVDTO.setEntityName(externalEntity.getName());
        externalEntityExportCSVDTO.setDescription(externalEntity.getDescription() != null? externalEntity.getDescription().replace("\n", "").replace("\r", "") : "");
        externalEntityExportCSVDTO.setSerializationId(externalEntity.getSerializationId());
        externalEntityExportCSVDTO.setTransient(ExternalEntityReplicationPolicy.NO_REPLICATION.equals(externalEntity.getReplicationPolicy()));
        externalEntityExportCSVDTO.setTransaction(externalEntity.getIsTransaction());
        switch (externalEntity.getStoreInfo().getType()){
          case ExternalSchemaStoreTypes.CSV:
                externalEntityExportCSVDTO.setFileHeader(((ExternalCsvEntityStoreInfo)externalEntity.getStoreInfo()).isHeader());
                externalEntityExportCSVDTO.setFileNameFormat(((ExternalCsvEntityStoreInfo)externalEntity.getStoreInfo()).getFileNameFormat());
                externalEntityExportCSVDTO.setDateFormat(((ExternalCsvEntityStoreInfo)externalEntity.getStoreInfo()).getDateFormat());
                externalEntityExportCSVDTO.setColumnDelimiter(String.valueOf(((ExternalCsvEntityStoreInfo)externalEntity.getStoreInfo()).getColumnDelimiter()));
                externalEntityExportCSVDTO.setInvalidFilenameAction(((ExternalCsvEntityCollectionRules)externalEntity.getCollectionRules()).getFileInvalidNameAction().name());
                break;
          case ExternalSchemaStoreTypes.KAFKA:
                externalEntityExportCSVDTO.setJsonTypeValue(((ExternalKafkaEntityStoreInfo)externalEntity.getStoreInfo()).getJsonTypeValue());
                externalEntityExportCSVDTO.setJsonTypePath(((ExternalKafkaEntityStoreInfo)externalEntity.getStoreInfo()).getJsonTypePath());
                externalEntityExportCSVDTO.setRelativePaths(((ExternalKafkaEntityStoreInfo)externalEntity.getStoreInfo()).getRelativePaths());
                externalEntityExportCSVDTO.setMergedNodes(((ExternalKafkaEntityStoreInfo)externalEntity.getStoreInfo()).getMergedNodes());
                break;

        }
        return externalEntityExportCSVDTO;
    }



    private void createExternalAttributesCSV(final String projectKey,final ZipOutputStream out ) {
        final List<ExternalEntity> externalEntities = externalEntityRepository.findByProjectKey(projectKey);
        final CsvMapper mapper = new CsvMapper();
        final CsvSchema schema = mapper.schemaFor(ExternalAttributeExportCSVDTO.class).withHeader();
        mapper.enable(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING);

        final List<ExternalAttributeExportCSVDTO> externalAttributeExportCSVDTOS = externalEntities.stream()
                .flatMap(externalEntity -> externalEntity.getAttributes().stream()
                        .map(att ->createExternalAttributeExportCSVDTO(att,externalEntity.getSchemaKey(),externalEntity.getEntityKey(),externalEntity.getStoreInfo().getType()))).collect(Collectors.toList());
        try {
            final ObjectWriter writer = mapper.writer(schema);
            writer.without(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET).writeValue(out,externalAttributeExportCSVDTOS);

        } catch (IOException e) {
            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.EXPORT_JACKSON_ERROR);
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(aiaApiMessage)
                    .originalException(e);

        }

    }

    private ExternalAttributeExportCSVDTO createExternalAttributeExportCSVDTO(ExternalAttribute externalAttribute,String schemaKey,String entityKey, String entityType) {
        ExternalAttributeExportCSVDTO externalAttributeExportCSVDTO = new ExternalAttributeExportCSVDTO();
        externalAttributeExportCSVDTO.setSchemaKey(schemaKey);
        externalAttributeExportCSVDTO.setEntityKey(entityKey);
        externalAttributeExportCSVDTO.setAttributeKey(externalAttribute.getAttributeKey());
        externalAttributeExportCSVDTO.setAttributeName(externalAttribute.getName());
        externalAttributeExportCSVDTO.setDescription(externalAttribute.getDescription() != null? externalAttribute.getDescription().replace("\n", "").replace("\r", "") : "");
        externalAttributeExportCSVDTO.setDatatype(externalAttribute.getDatatype());
        externalAttributeExportCSVDTO.setLogicalDatatype(externalAttribute.getLogicalDatatype());
        externalAttributeExportCSVDTO.setKeyPosition(externalAttribute.getKeyPosition());
        externalAttributeExportCSVDTO.setUpdateTime(externalAttribute.isUpdateTime());
        externalAttributeExportCSVDTO.setLogicalTime(externalAttribute.isLogicalTime());
        externalAttributeExportCSVDTO.setRequired(externalAttribute.isRequired());
        externalAttributeExportCSVDTO.setDefaultValue(externalAttribute.getDefaultValue());
        externalAttributeExportCSVDTO.setValidationRegex(externalAttribute.getValidationRegex());
        externalAttributeExportCSVDTO.setSerializationId(externalAttribute.getSerializationId());
        switch (entityType) {
            case ExternalSchemaStoreTypes.KAFKA:
                externalAttributeExportCSVDTO.setDateFormat(((ExternalKafkaAttributeStoreInfo) externalAttribute.getStoreInfo()).getDateFormat());
                externalAttributeExportCSVDTO.setJsonPath(((ExternalKafkaAttributeStoreInfo) externalAttribute.getStoreInfo()).getJsonPath());
                break;
            case ExternalSchemaStoreTypes.CSV:
                externalAttributeExportCSVDTO.setDateFormat(((ExternalCsvAttributeStoreInfo) externalAttribute.getStoreInfo()).getDateFormat());
                break;
        }
        return externalAttributeExportCSVDTO;
    }

    private static boolean isZipFile(String filename, InputStream stream) throws IOException {

        if (filename == null || !filename.endsWith("zip")){
            return false;
        }

        DataInputStream in = new DataInputStream(new BufferedInputStream(stream));
        int test = in.readInt();
        in.close();
        return test == 0x504b0304;
    }

    private void findFileInZip(ZipInputStream zipInputStream,String fileName) throws IOException {
        ZipEntry entry;
        while (( entry = zipInputStream.getNextEntry()) != null) {
            if (entry.getName().equals(fileName)) {
                return;
            }
        }

        final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.IMPORT_FROM_ZIP_MISSING_CSV_ERROR,fileName);
        throw new AiaApiException()
                .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                .message(aiaApiMessage);
    }


    public ExternalSchemaDTO toExternalSchemaDTO(ExternalSchemaExportCSVDTO externalSchemaExportCSVDTO) {
        final ExternalSchemaDTO externalSchemaDTO = new ExternalSchemaDTO();
        externalSchemaDTO.setSchemaKey(externalSchemaExportCSVDTO.getSchemaKey());
        externalSchemaDTO.setSchemaName(externalSchemaExportCSVDTO.getSchemaName());
        externalSchemaDTO.setTypeSystem(externalSchemaExportCSVDTO.getTypeSystem());
        externalSchemaDTO.setIsReference(externalSchemaExportCSVDTO.getReference());
        externalSchemaDTO.setStoreInfo(toStoreInfo(externalSchemaExportCSVDTO));
        externalSchemaDTO.setCollectionRules(toCollectionRules(externalSchemaExportCSVDTO));
        externalSchemaDTO.setDescription(externalSchemaExportCSVDTO.getDescription());
        externalSchemaDTO.setDataChannelInfo(toExternalSchemaDataChannelInfoDTO(externalSchemaExportCSVDTO.getSerializationMethod()));
        externalSchemaDTO.setSchemaType(externalSchemaExportCSVDTO.getSchemaType() != null ? externalSchemaExportCSVDTO.getSchemaType() : null);
        externalSchemaDTO.setOriginProcess(OriginProcess.IMPLEMENTATION.name());
        externalSchemaDTO.setIsActive(externalSchemaExportCSVDTO.getActive());
        externalSchemaDTO.setDisplayType(getDisplayType(externalSchemaExportCSVDTO));
        externalSchemaDTO.setAvailability(externalSchemaExportCSVDTO.getAvailability() != null && externalSchemaExportCSVDTO.getAvailability().equals(AvailabilityDTO.SHARED.toString()) ? AvailabilityDTO.SHARED : AvailabilityDTO.EXTERNAL);
        externalSchemaDTO.setSubjectAreaName(externalSchemaDTO.getAvailability()==AvailabilityDTO.EXTERNAL?null:externalSchemaExportCSVDTO.getSubjectAreaName());
        //TODO generate key????
        externalSchemaDTO.setSubjectAreaKey(externalSchemaDTO.getAvailability()==AvailabilityDTO.EXTERNAL?null:externalSchemaExportCSVDTO.getSubjectAreaName());
        return externalSchemaDTO;
    }

    private ExternalSchemaStoreInfoDTO toStoreInfo(ExternalSchemaExportCSVDTO externalSchemaExportCSVDTO) {
        if(externalSchemaExportCSVDTO.getStoreType() != null){
            switch (externalSchemaExportCSVDTO.getStoreType()){
                case ExternalSchemaStoreTypes.CSV:
                    return  new ExternalCsvSchemaStoreInfoDTO()
                            .defaultColumnDelimiter(externalSchemaExportCSVDTO.getDefaultDateFormat())
                            .defaultDateFormat(externalSchemaExportCSVDTO.getDefaultDateFormat())
                            .storeType(ExternalSchemaStoreInfoDTO.StoreTypeEnum.CSV);

                case ExternalSchemaStoreTypes.KAFKA:
                    return new ExternalKafkaSchemaStoreInfoDTO()
                            .defaultDateFormat(externalSchemaExportCSVDTO.getDefaultDateFormat())
                            .storeType(ExternalSchemaStoreInfoDTO.StoreTypeEnum.KAFKA);
                case ExternalSchemaStoreTypes.SQL:
                    return new ExternalSqlSchemaStoreInfoDTO()
                            .databaseType(externalSchemaExportCSVDTO.getDatabaseType())
                            .storeType(ExternalSchemaStoreInfoDTO.StoreTypeEnum.SQL);
                default:
                    return null;
            }
        }else{
            return null;
        }
    }

    private ExternalSchemaCollectionRulesDTO toCollectionRules(ExternalSchemaExportCSVDTO externalSchemaExportCSVDTO) {
        if(  externalSchemaExportCSVDTO.getStoreType() != null){
            switch (externalSchemaExportCSVDTO.getStoreType()){
                case ExternalSchemaStoreTypes.CSV:
                    ExternalCsvSchemaCollectionRulesDTO externalCsvSchemaCollectionRulesDTO = new ExternalCsvSchemaCollectionRulesDTO();
                    setExternalCollectionRules(externalSchemaExportCSVDTO, externalCsvSchemaCollectionRulesDTO);
                    externalCsvSchemaCollectionRulesDTO.setDefaultInvalidFilenameAction(toInvalidFilenameActionTypeDTO(externalSchemaExportCSVDTO.getDefaultInvalidFilenameAction()));
                    externalCsvSchemaCollectionRulesDTO.setStoreType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.CSV);
                    return externalCsvSchemaCollectionRulesDTO;
                case ExternalSchemaStoreTypes.KAFKA:
                    ExternalKafkaSchemaCollectionRulesDTO externalKafkaSchemaCollectionRulesDTO = new ExternalKafkaSchemaCollectionRulesDTO();
                    setExternalCollectionRules(externalSchemaExportCSVDTO, externalKafkaSchemaCollectionRulesDTO);
                    externalKafkaSchemaCollectionRulesDTO.setInputDataChannel(externalSchemaExportCSVDTO.getInputDataChannel());
                    externalKafkaSchemaCollectionRulesDTO.setSkipNodeFromParsing(externalSchemaExportCSVDTO.getSkipNodeFromParsing());
                    externalKafkaSchemaCollectionRulesDTO.setDeleteEventJsonPath(externalSchemaExportCSVDTO.getDeleteEventJsonPath());
                    externalKafkaSchemaCollectionRulesDTO.setDeleteEventOperation(externalSchemaExportCSVDTO.getDeleteEventOperation());
                    externalKafkaSchemaCollectionRulesDTO.setImplicitHandlerPreviousNode(externalSchemaExportCSVDTO.getImplicitHandlerPreviousNode());
                    externalKafkaSchemaCollectionRulesDTO.setImplicitHandlerCurrentNode(externalSchemaExportCSVDTO.getImplicitHandlerCurrentNode());
                    externalKafkaSchemaCollectionRulesDTO.setStoreType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.KAFKA);
                    return externalKafkaSchemaCollectionRulesDTO;
                case ExternalSchemaStoreTypes.SQL:
                    ExternalSqlSchemaCollectionRulesDTO externalSqlSchemaCollectionRulesDTO = new ExternalSqlSchemaCollectionRulesDTO();
                    setExternalCollectionRules(externalSchemaExportCSVDTO, externalSqlSchemaCollectionRulesDTO);
                    externalSqlSchemaCollectionRulesDTO.setStoreType(ExternalSchemaCollectionRulesDTO.StoreTypeEnum.SQL);
                    return externalSqlSchemaCollectionRulesDTO;
                default:
                    return null;
            }
        }else{
            return null;
        }

    }

    private void setExternalCollectionRules(ExternalSchemaExportCSVDTO externalSchemaExportCSVDTO, ExternalSchemaCollectionRulesDTO externalSchemaCollectionRulesDTO) {
        externalSchemaCollectionRulesDTO.setOngoingChannel(externalSchemaExportCSVDTO.getOngoingChannel());
        externalSchemaCollectionRulesDTO.setInitialLoadChannel(externalSchemaExportCSVDTO.getInitialLoadChannel());
        externalSchemaCollectionRulesDTO.setReplayChannel(externalSchemaExportCSVDTO.getReplayChannel());
        externalSchemaCollectionRulesDTO.setPartialLoadRelativeURL(externalSchemaExportCSVDTO.getPartialLoadRelativeURL());
        externalSchemaCollectionRulesDTO.setInitialLoadRelativeURL(externalSchemaExportCSVDTO.getInitialLoadRelativeURL());
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


    private ExternalEntityDTO toExternalEntityDTO(ExternalEntityExportCSVDTO externalEntityExportCSVDTO, String schemaType,boolean isNew , ExternalEntityDTO externalEntity) {
        ExternalEntityDTO externalEntityDTO = new ExternalEntityDTO();
        externalEntityDTO.setSchemaKey(externalEntityExportCSVDTO.getSchemaKey());
        externalEntityDTO.setEntityKey(externalEntityExportCSVDTO.getEntityKey());
        externalEntityDTO.setEntityName(externalEntityExportCSVDTO.getEntityName());
        externalEntityDTO.setDescription(externalEntityExportCSVDTO.getDescription());
        externalEntityDTO.setSerializationId(externalEntityExportCSVDTO.getSerializationId());
        externalEntityDTO.setIsTransient(externalEntityExportCSVDTO.getTransient());
        externalEntityDTO.setIsTransaction(externalEntityExportCSVDTO.getTransaction());
        externalEntityDTO.setStoreInfo(toStoreInfoEntity(externalEntityExportCSVDTO,schemaType));
        externalEntityDTO.setCollectionRules(toCollectionRulesEntity(externalEntityExportCSVDTO,schemaType));
        externalEntityDTO.setAttributes(isNew? getAttributes(externalEntityExportCSVDTO, schemaType): mergeAttribute (externalEntityExportCSVDTO, schemaType,externalEntity.getAttributes()));
        externalEntityDTO.setIsActive(externalEntityExportCSVDTO.getActive());
        externalEntityDTO.setOriginProcess(isNew? OriginProcess.IMPLEMENTATION.name(): externalEntity.getOriginProcess() );
        return externalEntityDTO;
    }

    private List<ExternalAttributeDTO> getAttributes(ExternalEntityExportCSVDTO externalEntityExportCSVDTO,String schemaType) {

        List<ExternalAttributeExportCSVDTO> attributeExportCSVDTOS =  attributesInFileMapByKeys.get(false).get(externalEntityExportCSVDTO.getSchemaKey()).get(externalEntityExportCSVDTO.getEntityKey());
       if(attributeExportCSVDTOS != null){
          return attributeExportCSVDTOS.stream().map(attributeExportCSVDTO -> toExternalAttributeDTO(attributeExportCSVDTO,schemaType))
                   .sorted(Comparator.comparingInt(ExternalAttributeDTO::getSerializationId))
                   .collect(Collectors.toList());
       }
       return null;
    }

    private List<ExternalAttributeDTO> mergeAttribute(ExternalEntityExportCSVDTO externalEntityExportCSVDTO,String schemaType,List <ExternalAttributeDTO> exitingExternalAttributeDTOS ) {
        List<ExternalAttributeDTO> attributes = new ArrayList<>();

        Map<String, ExternalAttributeDTO > existingAttributeMapByKey = exitingExternalAttributeDTOS.stream().collect(Collectors.toMap(ExternalAttributeDTO::getAttributeKey,externalAttributeDTO -> externalAttributeDTO));
        List<ExternalAttributeExportCSVDTO> attributeExportInCSVList = null;
        if( attributesInFileMapByKeys.get(false)!= null && attributesInFileMapByKeys.get(false).get(externalEntityExportCSVDTO.getSchemaKey())!= null) {
            attributeExportInCSVList = attributesInFileMapByKeys.get(false).get(externalEntityExportCSVDTO.getSchemaKey()).get(externalEntityExportCSVDTO.getEntityKey());
        }
        List<ExternalAttributeExportCSVDTO> attributeToDelete =  null;
        if( attributesInFileMapByKeys.get(true)!= null && attributesInFileMapByKeys.get(true).get(externalEntityExportCSVDTO.getSchemaKey())!= null ){
            attributeToDelete = attributesInFileMapByKeys.get(true).get(externalEntityExportCSVDTO.getSchemaKey()).get(externalEntityExportCSVDTO.getEntityKey());
        }
        //delete from existing entity list
        if(attributeToDelete !=null && !exitingExternalAttributeDTOS.isEmpty()){
            attributeToDelete.forEach(att-> existingAttributeMapByKey.remove(att.getAttributeKey()) );
        }
        if(attributeExportInCSVList != null) {
            attributeExportInCSVList.forEach(attributeExportCSVDTO -> attributes.add(toExternalAttributeDTO(attributeExportCSVDTO, schemaType)));
            Set<String> attributeExportKeysInCSV = attributeExportInCSVList.stream().map(ExternalAttributeExportCSVDTO::getAttributeKey ).collect(Collectors.toSet());
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

    private ExternalAttributeDTO toExternalAttributeDTO(ExternalAttributeExportCSVDTO attributeExportCSVDTO, String schemaType) {
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

    private ExternalAttributeStoreInfoDTO toStoreInfoAttribute(ExternalAttributeExportCSVDTO attributeExportCSVDTO,String schemaType) {
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

    public ExternalEntityStoreInfoDTO toStoreInfoEntity(ExternalEntityExportCSVDTO externalEntityExportCSVDTO, String schemaType) {
        switch(schemaType){
            case ExternalSchemaStoreTypes.CSV:
                return new ExternalCsvEntityStoreInfoDTO()
                        .fileHeader(externalEntityExportCSVDTO.getFileHeader())
                        .columnDelimiter(String.valueOf(externalEntityExportCSVDTO.getColumnDelimiter()))
                        .dateFormat(externalEntityExportCSVDTO.getDateFormat())
                        .fileNameFormat(externalEntityExportCSVDTO.getFileNameFormat())
                        .storeType(ExternalEntityStoreInfoDTO.StoreTypeEnum.CSV);
            case ExternalSchemaStoreTypes.KAFKA:
                return new ExternalKafkaEntityStoreInfoDTO()
                        .jsonTypePath(externalEntityExportCSVDTO.getJsonTypePath())
                        .jsonTypeValue(externalEntityExportCSVDTO.getJsonTypeValue())
                        .mergedNodes(externalEntityExportCSVDTO.getMergedNodes())
                        .relativePaths(externalEntityExportCSVDTO.getRelativePaths())
                        .storeType(ExternalEntityStoreInfoDTO.StoreTypeEnum.KAFKA);
            case ExternalSchemaStoreTypes.SQL:
                return new ExternalSqlEntityStoreInfoDTO()
                        .storeType(ExternalEntityStoreInfoDTO.StoreTypeEnum.SQL);
            default: return null;
        }

    }

    private ExternalEntityCollectionRulesDTO toCollectionRulesEntity(ExternalEntityExportCSVDTO externalEntityExportCSVDTO, String schemaType) {
        if(schemaType != null){
            switch (schemaType){
                case ExternalSchemaStoreTypes.CSV:
                    ExternalCsvEntityCollectionRulesDTO externalCsvEntityCollectionRulesDTO = new ExternalCsvEntityCollectionRulesDTO();
                    externalCsvEntityCollectionRulesDTO.setStoreType(ExternalEntityCollectionRulesDTO.StoreTypeEnum.CSV);
                    externalCsvEntityCollectionRulesDTO.setInvalidFilenameAction(toInvalidFilenameActionTypeDTO(externalEntityExportCSVDTO.getInvalidFilenameAction()));
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

    private String getDisplayType(ExternalSchemaExportCSVDTO externalEntityExportCSVDTO) {
        return messageHelper.format("external.schema.type." + externalEntityExportCSVDTO.getSchemaType());
    }



}