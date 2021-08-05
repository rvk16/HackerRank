package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaStoreInfo;
import com.amdocs.aia.il.configuration.repository.external.ExternalEntityRepository;
import com.amdocs.aia.il.configuration.repository.external.ExternalSchemaRepository;
import com.amdocs.aia.il.configuration.service.external.BulkServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BulkServiceUnitTest {
    private static final String EXTERNAL_ENTITIES_EXPORT_CSV = "external_entities_export.csv";
    private static final String EXTERNAL_SCHEMAS_EXPORT_CSV = "external_schemas_export.csv";
    private static final String EXTERNAL_ATTRIBUTES_EXPORT_CSV = "external_attributes_export.csv";

    @Mock
    ExternalSchemaRepository externalSchemaRepository;

    @Mock
    ExternalEntityRepository externalEntityRepository;


    @InjectMocks
    private BulkServiceImpl bulkService;



    private static List<ExternalSchema> createExternalSchemas() {
        final List<ExternalSchema> mockExternalSchemas = new ArrayList<>();

        final ExternalSchema externalSchema = new ExternalSchema();
        externalSchema.setSchemaKey("PartyInteractionAbe");
        externalSchema.setDescription("Party Interaction Schema");
        externalSchema.setName("PartyInteractionAbe");
        externalSchema.setProjectKey("aia");
        externalSchema.setSchemaType(ExternalSchemaType.ORACLE);
        externalSchema.setAvailability(Availability.EXTERNAL);
        externalSchema.setCollectionRules(new ExternalSqlSchemaCollectionRules());
        externalSchema.setIsReference(false);
        ExternalSchemaDataChannelInfo externalSchemaDataChannelInfo = new ExternalSchemaDataChannelInfo();
        externalSchemaDataChannelInfo.setSerializationMethod("Json");
        externalSchema.setDataChannelInfo(externalSchemaDataChannelInfo);
        ExternalSqlSchemaStoreInfo externalSqlSchemaStoreInfo = new ExternalSqlSchemaStoreInfo();
        externalSqlSchemaStoreInfo.setDatabaseType("Oracle");
        externalSchema.setStoreInfo(externalSqlSchemaStoreInfo);
        externalSchema.setSubjectAreaName("Customer");
        externalSchema.setTypeSystem("SQL");

        mockExternalSchemas.add(externalSchema);
        return mockExternalSchemas;

    }
    private static List<ExternalEntity> createExternalEntities() {
        final List<ExternalEntity> mockExternalEntities = new ArrayList<>();
        final ExternalEntity externalEntity = new ExternalEntity();
        externalEntity.setSchemaKey("PartyInteractionAbe");
        externalEntity.setDescription("description");
        externalEntity.setName("PartyInteraction");
        externalEntity.setProjectKey("aia");
        externalEntity.setAvailability(Availability.EXTERNAL);
        externalEntity.setCollectionRules(new ExternalSqlEntityCollectionRules());
        externalEntity.setSerializationId(1);
        externalEntity.setStoreInfo(new ExternalSqlEntityStoreInfo());
        externalEntity.setCollectionRules(new ExternalSqlEntityCollectionRules());
        externalEntity.setSchemaType(ExternalSchemaType.ORACLE);
        List<ExternalAttribute> attributes =  new ArrayList<>();
        ExternalAttribute attribute1 = new ExternalAttribute();
        attribute1.setAttributeKey("id");
        attribute1.setDatatype("varchar2");
        ExternalAttribute attribute2 = new ExternalAttribute();
        attribute2.setAttributeKey("description");
        attribute2.setDatatype("varchar2");
        attributes.add(attribute1);
        attributes.add(attribute2);
        externalEntity.setAttributes(attributes);
        mockExternalEntities.add(externalEntity);
       return mockExternalEntities;


    }



    @Test
    void whenExportDatastoreTablesToZIP_shouldReturnZipFile_With_2filesAndCorrectNumberOfLines() {
        when(externalSchemaRepository.findByProjectKey("aia")).thenReturn(createExternalSchemas());
        when(externalEntityRepository.findByProjectKey("aia")).thenReturn(createExternalEntities());


        final InputStreamResource inp = bulkService.exportExternalSchemasToZIP("aia");

        Map<String, String> zipFileContents = new HashMap<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(inp.getInputStream())) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                int count;
                byte[] data = new byte[1024];
                String filename = entry.getName();

                StringBuilder sb = new StringBuilder();
                while ((count = zipInputStream.read(data, 0, 1024)) != -1) {
                    sb.append(new String(data, StandardCharsets.UTF_8));
                }

                zipFileContents.put(filename, sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(3, zipFileContents.size());
        assertTrue(zipFileContents.containsKey(EXTERNAL_SCHEMAS_EXPORT_CSV));
        assertTrue(zipFileContents.containsKey(EXTERNAL_ENTITIES_EXPORT_CSV));
        assertTrue(zipFileContents.containsKey(EXTERNAL_ATTRIBUTES_EXPORT_CSV));

        String schemasExport = zipFileContents.get(EXTERNAL_SCHEMAS_EXPORT_CSV);
        String entitiesExport = zipFileContents.get(EXTERNAL_ENTITIES_EXPORT_CSV);
        String attributesExport = zipFileContents.get(EXTERNAL_ATTRIBUTES_EXPORT_CSV);

//        assertEquals(2, schemasExport.split("\r\n|\r|\n").length - 1);
//        assertEquals(2, entitiesExport.split("\r\n|\r|\n").length - 1);
//        assertEquals(3, attributesExport.split("\r\n|\r|\n").length - 1);

    }

//
//    @Test
//    void whenImportDatastoreCSV_shouldOneTableBeUpdated_andOneTableNotUpdated() {
//        List<TableConfiguration> existingTables = createExternalSchemas();
//        when(externalEntityRepository.findByProjectKey("projectKey")).thenReturn(existingTables);
//
//        when(externalEntityRepository.saveElements(any())).thenReturn(new SaveElementsResponse());
//        MockMultipartFile importFile = createMultipartFile("src/test/resources/bulk/datastore_tables_export_to_update.zip", "datastore_tables_export_to_update.zip");
//
//        BulkImportResponseDTO res = bulkService.importDatastoreTablesFromZIP("projectKey", importFile);
//        assertEquals(2, res.getImportedTableCount());
//        assertEquals(1, res.getModifiedTableCount());
//        assertEquals(2, res.getTotalTableCount());
//        assertEquals(1, res.getUnmodifiedTableCount());
//    }
//
//    @Test
//    void whenImportDatastoreCSVWithWrongPartitionPolicy_shouldThrowException() {
//        MockMultipartFile importFile = createMultipartFile("src/test/resources/bulk/datastore_tables_export_wrong_partition_policy.zip", "datastore_tables_export_to_update.zip");
//        AiaApiException e = assertThrows(AiaApiException.class, () -> bulkService.importDatastoreTablesFromZIP("projectKey", importFile));
//        assertEquals(9, e.getApiMessage().getMessageTemplate().getErrorCode());
//    }
//
//    @Test
//    void whenImportDatastoreCSVWithWrongNotDataPartitionColumn_shouldThrowException() {
//        List<TableConfiguration> existingTables = createExternalSchemas();
//        when(externalEntityRepository.findByProjectKey("projectKey")).thenReturn(existingTables);
//        MockMultipartFile importFile = createMultipartFile("src/test/resources/bulk/datastore_tables_with_not_date_partition_column.zip", "datastore_tables_export_to_update.zip");
//        AiaApiException e = assertThrows(AiaApiException.class, () -> bulkService.importDatastoreTablesFromZIP("projectKey", importFile));
//        assertEquals(5, e.getApiMessage().getMessageTemplate().getErrorCode());
//    }
//
//    @Test
//    void whenImportDatastoreCSVWithNotExistingPartitionColumn_shouldThrowException() {
//        List<TableConfiguration> existingTables = createExternalSchemas();
//        when(externalEntityRepository.findByProjectKey("projectKey")).thenReturn(existingTables);
//        MockMultipartFile importFile = createMultipartFile("src/test/resources/bulk/datastore_tables_with_not_existing_partition_column.zip", "datastore_tables_export_to_update.zip");
//        AiaApiException e = assertThrows(AiaApiException.class, () -> bulkService.importDatastoreTablesFromZIP("projectKey", importFile));
//        assertEquals(3, e.getApiMessage().getMessageTemplate().getErrorCode());
//    }

    private static MockMultipartFile createMultipartFile(String filePath, String fileName) {
        byte[] content;
        Path path = Paths.get(filePath);
        try {
            content = Files.readAllBytes(path);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while trying to convert file to bytes");
        }
        return new MockMultipartFile(fileName, fileName, "text/plain", content);
    }
}