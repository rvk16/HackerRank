package com.amdocs.aia.il.configuration.service.external;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.core.web.AiaApiMessage;
import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.exportimport.*;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
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
import java.util.concurrent.atomic.AtomicInteger;
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



    private Map<Boolean,Map<String ,Map< String, List<ExternalAttributeExportCSV>>>> attributesInFileMapByKeys;

    private final CsvInZipImportReader csvInZipImportReader;
    private final ExternalSchemasImportExportHelper externalSchemasImportExportHelper;


    @Autowired
    public BulkServiceImpl(ExternalSchemaRepository externalSchemaRepository, ExternalEntityRepository externalEntityRepository,
                           ExternalSchemaService externalSchemaService, ExternalEntityService externalEntityService,
                           CsvInZipImportReader csvInZipImportReader, ExternalSchemasImportExportHelper externalSchemasImportExportHelper) {
        this.externalSchemaRepository = externalSchemaRepository;
        this.externalEntityRepository = externalEntityRepository;
        this.externalSchemaService = externalSchemaService;
        this.externalEntityService = externalEntityService;
        this.csvInZipImportReader = csvInZipImportReader;
        this.externalSchemasImportExportHelper = externalSchemasImportExportHelper;
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
        LOGGER.debug("import external schemas");
        final BulkImportResponseDTO response = new BulkImportResponseDTO();
        if (file.getSize() == 0) {
            return response;
        }
        importExternalSchemas(projectKey,csvInZipImportReader.readExternalSchemaFromZipFile(file),response);
        importExternalEntities(projectKey,csvInZipImportReader.readExternalEntityFromZipFile(file),csvInZipImportReader.readExternalAttributesFromZipFile(file),response);
        return response;
    }

    private void importExternalEntities(String projectKey, List<ExternalEntityExportCSV> externalEntitiesKeysInFile ,
                                        List<ExternalAttributeExportCSV> externalAttributesKeysInFile,
                                        BulkImportResponseDTO response)  {
       deleteExternalEntities(projectKey, externalEntitiesKeysInFile, response);

       final List<ExternalEntityExportCSV> externalEntitiesInFileNotToDelete = externalEntitiesKeysInFile.stream().filter(externalEntityExportCSV -> externalEntityExportCSV.getToDelete()!=Boolean.TRUE).collect(Collectors.toList());
       final Map<String,Map<String,ExternalEntityDTO>> existingExternalEntitiesBySchemaKey = externalEntityService.listAll(projectKey).stream().collect(Collectors.groupingBy( ExternalEntityDTO::getSchemaKey,toMap(ExternalEntityDTO::getEntityKey, externalEntityDTO -> externalEntityDTO)));
       final Map<String, ExternalSchemaType> existingSchemaTypesByKeys = externalSchemaRepository.findByProjectKey(projectKey).stream().collect(Collectors.toMap(ExternalSchema::getSchemaKey,ExternalSchema::getSchemaType));

       attributesInFileMapByKeys =
               externalAttributesKeysInFile.stream()
                       .collect(partitioningBy(ExternalAttributeExportCSV::getToDelete,Collectors.groupingBy( ExternalAttributeExportCSV::getSchemaKey,Collectors.groupingBy( ExternalAttributeExportCSV::getEntityKey, Collectors.toList()))));


        addExternalEntities(projectKey, response, externalEntitiesInFileNotToDelete, existingExternalEntitiesBySchemaKey, existingSchemaTypesByKeys);

        updateExternalEntities(projectKey, response, externalEntitiesInFileNotToDelete, existingExternalEntitiesBySchemaKey, existingSchemaTypesByKeys);

        updateExternalEntitiesWithoutCSVFile(projectKey, externalAttributesKeysInFile, response, externalEntitiesInFileNotToDelete, existingExternalEntitiesBySchemaKey);
    }

    private void updateExternalEntitiesWithoutCSVFile(String projectKey, List<ExternalAttributeExportCSV> externalAttributesKeysInFile, BulkImportResponseDTO response,
                                                      List<ExternalEntityExportCSV> externalEntitiesInFileNotToDelete, Map<String, Map<String, ExternalEntityDTO>> existingExternalEntitiesBySchemaKey) {
        Map<String, Map<String, List<ExternalAttributeExportCSV>>> attributesInFileWithoutParentMap = calculateAttributesInFileWithoutParent(externalAttributesKeysInFile, externalEntitiesInFileNotToDelete);
        AtomicInteger additionalUpdateEntity = new AtomicInteger();
        attributesInFileWithoutParentMap.forEach( (schemaKey, entitiesMap) -> entitiesMap.forEach((entityKey, attributes) -> {
            if(existingExternalEntitiesBySchemaKey.get(schemaKey)!= null && existingExternalEntitiesBySchemaKey.get(schemaKey).get(entityKey)!= null) {
                ExternalEntityDTO existingEntityDTO = existingExternalEntitiesBySchemaKey.get(schemaKey).get(entityKey);
                Map<String, ExternalAttributeDTO > existingAttributeMapByKey = existingEntityDTO.getAttributes().stream().collect(Collectors.toMap(ExternalAttributeDTO::getAttributeKey,externalAttributeDTO -> externalAttributeDTO));
                for (ExternalAttributeExportCSV csvAttribute : attributes) {
                    if (Boolean.TRUE.equals(csvAttribute.getToDelete())){
                        existingAttributeMapByKey.remove(csvAttribute.getAttributeKey());
                    }else{
                        ExternalAttributeDTO attributeDTO =  externalSchemasImportExportHelper.toExternalAttributeDTO(csvAttribute,existingEntityDTO.getStoreInfo().getStoreType().name());
                        existingAttributeMapByKey.put(attributeDTO.getAttributeKey(),attributeDTO);
                    }
                }
               existingEntityDTO.setAttributes(new ArrayList<>(existingAttributeMapByKey.values()));
               externalEntityService.update(projectKey,schemaKey,existingEntityDTO);
               additionalUpdateEntity.getAndIncrement();
            }
        }));
        response.setModifiedEntitiesCount(response.getModifiedEntitiesCount()+additionalUpdateEntity.get());
    }

    private Map<String, Map<String, List<ExternalAttributeExportCSV>>> calculateAttributesInFileWithoutParent(List<ExternalAttributeExportCSV> externalAttributesKeysInFile, List<ExternalEntityExportCSV> externalEntitiesInFileNotToDelete) {
        Set<String> externalEntitiesKeysInFileNotToDelete = externalEntitiesInFileNotToDelete.stream().map(h ->h.getSchemaKey()+"_"+h.getEntityKey()).collect(Collectors.toSet());
        return externalAttributesKeysInFile.stream().filter(externalAttributeExportCSV -> !externalEntitiesKeysInFileNotToDelete.contains(externalAttributeExportCSV.getSchemaKey()+"_"+externalAttributeExportCSV.getEntityKey()))
                        .collect(Collectors.groupingBy( ExternalAttributeExportCSV::getSchemaKey,Collectors.groupingBy( ExternalAttributeExportCSV::getEntityKey, Collectors.toList())));

    }

    private void updateExternalEntities(String projectKey, BulkImportResponseDTO response, List<ExternalEntityExportCSV> externalEntitiesInFileNotToDelete, Map<String, Map<String, ExternalEntityDTO>> existingExternalEntitiesBySchemaKey, Map<String, ExternalSchemaType> existingSchemaTypesByKeys) {
        final List<ExternalEntityDTO> externalEntityDTOsToUpdate = externalEntitiesInFileNotToDelete.stream()
                .filter(externalEntityExportCSV -> existingExternalEntitiesBySchemaKey.containsKey(externalEntityExportCSV.getSchemaKey()))
                .filter(externalEntityExportCSV -> (existingExternalEntitiesBySchemaKey.get(externalEntityExportCSV.getSchemaKey()).containsKey(externalEntityExportCSV.getEntityKey())))
                .map(externalEntityExportCSV -> toExternalEntityDTO(externalEntityExportCSV, existingSchemaTypesByKeys.get(externalEntityExportCSV.getSchemaKey()).getStoreType(),false, existingExternalEntitiesBySchemaKey.get(externalEntityExportCSV.getSchemaKey()).get(externalEntityExportCSV.getEntityKey())))
                .collect(Collectors.toList());
        if (!externalEntityDTOsToUpdate.isEmpty()) {
            externalEntityDTOsToUpdate.forEach( externalEntityDTO -> externalEntityService.update(projectKey,externalEntityDTO.getSchemaKey(),externalEntityDTO));
        }
        response.setModifiedEntitiesCount(externalEntityDTOsToUpdate.size());
    }

    private void addExternalEntities(String projectKey, BulkImportResponseDTO response, List<ExternalEntityExportCSV> externalEntitiesInFileNotToDelete, Map<String, Map<String, ExternalEntityDTO>> existingExternalEntitiesBySchemaKey, Map<String, ExternalSchemaType> existingSchemaTypesByKeys) {
        final List<ExternalEntityDTO> externalEntityDTOsToAdd = externalEntitiesInFileNotToDelete.stream()
                .filter(externalEntityExportCSV ->  existingSchemaTypesByKeys.containsKey(externalEntityExportCSV.getSchemaKey()))
                .filter(externalEntityExportCSV ->  existingExternalEntitiesBySchemaKey.get(externalEntityExportCSV.getSchemaKey()) == null ||   !(existingExternalEntitiesBySchemaKey.get(externalEntityExportCSV.getSchemaKey()).containsKey(externalEntityExportCSV.getEntityKey())))
                .map(externalEntityExportCSV -> toExternalEntityDTO(externalEntityExportCSV, existingSchemaTypesByKeys.get(externalEntityExportCSV.getSchemaKey()).getStoreType(),true, null)).collect(Collectors.toList());
        SaveElementsResponseDTO saveElementsResponseDTO = null;
        if (!externalEntityDTOsToAdd.isEmpty()) {
            saveElementsResponseDTO = externalEntityService.bulkSave(projectKey,externalEntityDTOsToAdd);
        }
        response.setNewEntitiesCount(saveElementsResponseDTO != null ? saveElementsResponseDTO.getSavedElementsCount().intValue() : 0);
    }

    private void deleteExternalEntities(String projectKey, List<ExternalEntityExportCSV> externalEntitiesKeysInFile, BulkImportResponseDTO response) {
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
    }


    private void importExternalSchemas(String projectKey, List<ExternalSchemaExportCSV> externalSchemasKeysInFile, BulkImportResponseDTO response) {
        //delete external schemas
        final List<String> externalSchemasKeysInFileToDelete = externalSchemasKeysInFile.stream().filter(externalSchemaExportCSV -> externalSchemaExportCSV.getToDelete()==Boolean.TRUE).map(ExternalSchemaExportCSV::getSchemaKey).collect(Collectors.toList());
        int notExitSchema = 0;
        for (String schemaKey : externalSchemasKeysInFileToDelete) {
            try {
              externalSchemaService.delete(projectKey, schemaKey);
              LOGGER.debug("schema {} was deleted",schemaKey);
            } catch (ApiException e) {
                notExitSchema++;
            }
        }
        response.setDeletedSchemasCount(externalSchemasKeysInFileToDelete.size()> notExitSchema?externalSchemasKeysInFileToDelete.size()- notExitSchema:0);

        final List<ExternalSchemaExportCSV> externalSchemasInFileNotToDelete = externalSchemasKeysInFile.stream().filter(externalSchemaExportCSV -> externalSchemaExportCSV.getToDelete()!=Boolean.TRUE).collect(Collectors.toList());

        final List<ExternalSchema> existingSchemas = externalSchemaRepository.findByProjectKey(projectKey);
        final Set<String> existingExternalSchemaKeys = existingSchemas.stream().map(ExternalSchema::getSchemaKey).collect(Collectors.toSet());


        final List<ExternalSchemaDTO> externalSchemaDTOsToAdd = externalSchemasInFileNotToDelete.stream().filter(externalSchemaExportCSV -> !existingExternalSchemaKeys.contains(externalSchemaExportCSV.getSchemaKey()))
                .map(externalSchemasImportExportHelper::toExternalSchemaDTO).collect(Collectors.toList());
        SaveElementsResponseDTO saveElementsResponseDTO = null;
        if (!externalSchemaDTOsToAdd.isEmpty()) {
            saveElementsResponseDTO = externalSchemaService.bulkSave(projectKey,externalSchemaDTOsToAdd);
            LOGGER.debug("bulk operation for saving {} new schemas was performed",externalSchemaDTOsToAdd.size());
        }
        response.setNewSchemasCount(saveElementsResponseDTO != null ? saveElementsResponseDTO.getSavedElementsCount().intValue() : 0);

        final List<ExternalSchemaDTO> externalSchemaDTOsToUpdate = externalSchemasInFileNotToDelete.stream().filter(externalSchemaExportCSV -> existingExternalSchemaKeys.contains(externalSchemaExportCSV.getSchemaKey()))
                .map(externalSchemasImportExportHelper::toExternalSchemaDTO).collect(Collectors.toList());
        if (!externalSchemaDTOsToUpdate.isEmpty()) {
            externalSchemaDTOsToUpdate.forEach( externalSchemaDTO -> {
                externalSchemaService.update(projectKey,externalSchemaDTO.getSchemaKey(),externalSchemaDTO);
                LOGGER.debug("schema {} was updated",externalSchemaDTO.getSchemaKey());
            } );
        }

        response.setModifiedSchemasCount(externalSchemaDTOsToUpdate.size());
    }


    private void createExternalSchemasCSV(final String projectKey,final ZipOutputStream out ) {
        LOGGER.debug("exporting external schemas");
        final List<ExternalSchema> externalSchema = externalSchemaRepository.findByProjectKey(projectKey);
        final CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING);
        final CsvSchema csvSchema = mapper.schemaFor(ExternalSchemaExportCSV.class).withHeader()/*.withoutQuoteChar()*/;
        final List<AbstractCsvExternalModel> externalSchemaExportCSVS = externalSchema.stream().map(externalSchemasImportExportHelper::createExternalSchemaExportCSVDTO).collect(Collectors.toList());
        writeCSvFile(out, mapper, csvSchema, externalSchemaExportCSVS);
    }

    private void writeCSvFile(ZipOutputStream out, CsvMapper mapper, CsvSchema csvSchema, List<AbstractCsvExternalModel> externalSchemaExportCSVS) {
        try{
            LOGGER.debug("write scv file");
            mapper.writer(csvSchema).without(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET).writeValue(out, externalSchemaExportCSVS);
        } catch (IOException e) {
            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.EXPORT_JACKSON_ERROR);
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(aiaApiMessage)
                    .originalException(e);

        }
    }

    public ExternalEntityDTO toExternalEntityDTO(ExternalEntityExportCSV externalEntityExportCSV, String schemaType, boolean isNew , ExternalEntityDTO externalEntity) {
        ExternalEntityDTO externalEntityDTO = new ExternalEntityDTO();
        externalEntityDTO.setSchemaKey(externalEntityExportCSV.getSchemaKey());
        externalEntityDTO.setEntityKey(externalEntityExportCSV.getEntityKey());
        externalEntityDTO.setEntityName(externalEntityExportCSV.getEntityName());
        externalEntityDTO.setDescription(externalEntityExportCSV.getDescription());
        externalEntityDTO.setSerializationId(!isNew ? externalEntity.getSerializationId():null);
        externalEntityDTO.setIsTransient(externalEntityExportCSV.getTransient());
        externalEntityDTO.setIsTransaction(externalEntityExportCSV.getTransaction());
        externalEntityDTO.setStoreInfo(externalSchemasImportExportHelper.toStoreInfoEntity(externalEntityExportCSV,schemaType));
        externalEntityDTO.setCollectionRules(externalSchemasImportExportHelper.toCollectionRulesEntity(externalEntityExportCSV,schemaType));
        externalEntityDTO.setAttributes(isNew? getAttributes(externalEntityExportCSV, schemaType): mergeAttribute (externalEntityExportCSV.getSchemaKey(),externalEntityExportCSV.getEntityKey(), schemaType,externalEntity.getAttributes()));
        externalEntityDTO.setIsActive(externalEntityExportCSV.getActive());
        externalEntityDTO.setOriginProcess(isNew? OriginProcess.IMPLEMENTATION.name(): externalEntity.getOriginProcess() );
        return externalEntityDTO;
    }


    private void createExternalEntitiesCSV(final String projectKey,final ZipOutputStream out ) {
        LOGGER.debug("exporting external entities");
        final List<ExternalEntity> externalEntities = externalEntityRepository.findByProjectKey(projectKey);
        final  CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING);

        final CsvSchema csvSchema = mapper.schemaFor(ExternalEntityExportCSV.class).withHeader()/*.withoutQuoteChar()*/;

        final List<AbstractCsvExternalModel> externalEntityExportCSVS = externalEntities.stream().map(externalSchemasImportExportHelper::createExternalEntityExportCSVDTO).collect(Collectors.toList());
        writeCSvFile(out, mapper, csvSchema, externalEntityExportCSVS);
    }


    private void createExternalAttributesCSV(final String projectKey,final ZipOutputStream out ) {
        LOGGER.debug("exporting external attributes");
        final List<ExternalEntity> externalEntities = externalEntityRepository.findByProjectKey(projectKey);
        final CsvMapper mapper = new CsvMapper();
        final CsvSchema csvSchema = mapper.schemaFor(ExternalAttributeExportCSV.class).withHeader();
        mapper.enable(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING);

        final List<AbstractCsvExternalModel> externalAttributeExportCSVS = externalEntities.stream()
                .flatMap(externalEntity -> externalEntity.getAttributes().stream()
                        .map(att ->externalSchemasImportExportHelper.createExternalAttributeExportCSVDTO(att,externalEntity.getSchemaKey(),externalEntity.getEntityKey(),externalEntity.getStoreInfo().getType()))).collect(Collectors.toList());

        writeCSvFile(out, mapper, csvSchema, externalAttributeExportCSVS);
    }




    private List<ExternalAttributeDTO> getAttributes(ExternalEntityExportCSV externalEntityExportCSV, String schemaType) {

        List<ExternalAttributeExportCSV> attributeExportCSVDTOS =  attributesInFileMapByKeys.get(false).get(externalEntityExportCSV.getSchemaKey()).get(externalEntityExportCSV.getEntityKey());
       if(attributeExportCSVDTOS != null){
          return attributeExportCSVDTOS.stream().map(attributeExportCSVDTO -> externalSchemasImportExportHelper.toExternalAttributeDTO(attributeExportCSVDTO,schemaType))
                  /* .sorted(Comparator.comparingInt(ExternalAttributeDTO::getSerializationId))*/
                   .collect(Collectors.toList());
       }
       return Collections.emptyList();
    }

    private List<ExternalAttributeDTO> mergeAttribute(String schemaKey,String  entityKey ,String schemaType, List <ExternalAttributeDTO> exitingExternalAttributeDTOS ) {
        List<ExternalAttributeDTO> attributes = new ArrayList<>();

        Map<String, ExternalAttributeDTO > existingAttributeMapByKey = exitingExternalAttributeDTOS.stream().collect(Collectors.toMap(ExternalAttributeDTO::getAttributeKey,externalAttributeDTO -> externalAttributeDTO));
        List<ExternalAttributeExportCSV> attributeExportInCSVList = null;
        if( attributesInFileMapByKeys.get(false)!= null && attributesInFileMapByKeys.get(false).get(schemaKey)!= null) {
            attributeExportInCSVList = attributesInFileMapByKeys.get(false).get(schemaKey).get(entityKey);
        }
        List<ExternalAttributeExportCSV> attributeToDelete =  null;
        if( attributesInFileMapByKeys.get(true)!= null && attributesInFileMapByKeys.get(true).get(schemaKey)!= null ){
            attributeToDelete = attributesInFileMapByKeys.get(true).get(schemaKey).get(entityKey);
        }
        //delete from existing entity list
        if(attributeToDelete !=null && !exitingExternalAttributeDTOS.isEmpty()){
            attributeToDelete.forEach(att-> existingAttributeMapByKey.remove(att.getAttributeKey()) );
        }
        if(attributeExportInCSVList != null) {
            attributeExportInCSVList.forEach(attributeExportCSVDTO -> attributes.add(externalSchemasImportExportHelper.toExternalAttributeDTO(attributeExportCSVDTO, schemaType)));
            Set<String> attributeExportKeysInCSV = attributeExportInCSVList.stream().map(ExternalAttributeExportCSV::getAttributeKey ).collect(Collectors.toSet());
            existingAttributeMapByKey.forEach((attKey, att) -> {
                if (!attributeExportKeysInCSV.contains(attKey)) {
                    attributes.add(att);
                }
            });
        }else{
            attributes.addAll(new ArrayList<>(existingAttributeMapByKey.values()));
        }

       return  attributes;
    }





}