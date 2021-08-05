package com.amdocs.aia.il.configuration.service.external;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.core.web.AiaApiMessage;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.external.csv.*;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaStoreInfo;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaStoreInfo;
import com.amdocs.aia.il.configuration.export.ExternalAttributeExportCSVDTO;
import com.amdocs.aia.il.configuration.export.ExternalEntityExportCSVDTO;
import com.amdocs.aia.il.configuration.export.ExternalSchemaExportCSVDTO;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.repository.external.ExternalEntityRepository;
import com.amdocs.aia.il.configuration.repository.external.ExternalSchemaRepository;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class BulkServiceImpl implements BulkService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkServiceImpl.class);

    private static final String EXTERNAL_SCHEMAS_EXPORT_FILE_NAME = "external_schemas_export.csv";
    private static final String EXTERNAL_ENTITIES_EXPORT_FILE_NAME = "external_entities_export.csv";
    private static final String EXTERNAL_ATTRIBUTES_EXPORT_FILE_NAME = "external_attributes_export.csv";

    private final ExternalSchemaRepository externalSchemaRepository;
    private final ExternalEntityRepository externalEntityRepository;




    @Autowired
    public BulkServiceImpl(ExternalSchemaRepository externalSchemaRepository, ExternalEntityRepository externalEntityRepository) {
        this.externalSchemaRepository = externalSchemaRepository;
        this.externalEntityRepository = externalEntityRepository;
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



    private void createExternalSchemasCSV(final String projectKey,final ZipOutputStream out ) {
        final List<ExternalSchema> externalSchema = externalSchemaRepository.findByProjectKey(projectKey);
        final CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING);
        final CsvSchema csvSchema = mapper.schemaFor(ExternalSchemaExportCSVDTO.class).withHeader()/*.withoutQuoteChar()*/;
        final List<ExternalSchemaExportCSVDTO> externalSchemaExportCSVDTOS = externalSchema.stream().map(externalSchemaDTO ->
            createExternalSchemaExportCSVDTO(externalSchemaDTO)).collect(Collectors.toList());
        try {
            final ObjectWriter writer = mapper.writer(csvSchema);
            writer.without(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET).writeValue(out,externalSchemaExportCSVDTOS);
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
        externalSchemaExportCSVDTO.setSchemaType(externalSchema.getStoreInfo().getType());
        externalSchemaExportCSVDTO.setActive(externalSchema.getIsActive());
        externalSchemaExportCSVDTO.setDescription(externalSchema.getDescription() != null? externalSchema.getDescription().replace("\n", "").replace("\r", "") : "");
        externalSchemaExportCSVDTO.setTypeSystem(externalSchema.getTypeSystem());
        externalSchemaExportCSVDTO.setReference(externalSchema.getIsReference());
        externalSchemaExportCSVDTO.setStoreType(externalSchema.getSchemaType() != null ? externalSchema.getSchemaType().name() : null);
        externalSchemaExportCSVDTO.setSerializationMethod(externalSchema.getDataChannelInfo().getSerializationMethod().toString());
        externalSchemaExportCSVDTO.setAvailability(externalSchema.getAvailability().toString());
        externalSchemaExportCSVDTO.setSubjectAreaName(externalSchema.getSubjectAreaName());
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

        final List<ExternalEntityExportCSVDTO> externalEntityExportCSVDTOS = externalEntities.stream().map(externalEntity ->
                createExternalEntityExportCSVDTO(externalEntity)).collect(Collectors.toList());
        try {
            final ObjectWriter writer = mapper.writer(schema);
            writer.without(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET).writeValue(out,externalEntityExportCSVDTOS);

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
                        .map(att ->createExternalAttributeExportCSVDTO(att,externalEntity.getEntityKey(),externalEntity.getStoreInfo().getType()))).collect(Collectors.toList());
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

    private ExternalAttributeExportCSVDTO createExternalAttributeExportCSVDTO(ExternalAttribute externalAttribute,String entityKey, String entityType) {
        ExternalAttributeExportCSVDTO externalAttributeExportCSVDTO = new ExternalAttributeExportCSVDTO();
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

}
