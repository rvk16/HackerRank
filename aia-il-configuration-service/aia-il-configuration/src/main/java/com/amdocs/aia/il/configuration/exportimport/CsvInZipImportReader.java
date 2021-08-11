package com.amdocs.aia.il.configuration.exportimport;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.core.web.AiaApiMessage;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class CsvInZipImportReader {
    private static final String EXTERNAL_SCHEMAS_EXPORT_FILE_NAME = "external_schemas_export.csv";
    private static final String EXTERNAL_ENTITIES_EXPORT_FILE_NAME = "external_entities_export.csv";
    private static final String EXTERNAL_ATTRIBUTES_EXPORT_FILE_NAME = "external_attributes_export.csv";

    public List<ExternalSchemaExportCSV> readExternalSchemaFromZipFile(MultipartFile file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            if (!isZipFile(file.getOriginalFilename(), file.getInputStream())) {
                final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.IMPORT_FROM_ZIP_NOT_ZIP_ERROR);
                throw new AiaApiException()
                        .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                        .message(aiaApiMessage);
            }
            findFileInZip(zipInputStream, EXTERNAL_SCHEMAS_EXPORT_FILE_NAME);
            final CsvMapper mapper = new CsvMapper();
            final CsvSchema schema = mapper.schemaFor(ExternalSchemaExportCSV.class).withHeader().withNullValue("");
            final MappingIterator<ExternalSchemaExportCSV> externalSchemasInFileIter = mapper.readerFor(ExternalSchemaExportCSV.class).with(schema).readValues(zipInputStream);
            return externalSchemasInFileIter.readAll();
        } catch (IOException e) {
            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.IMPORT_FROM_ZIP_ERROR);
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(aiaApiMessage)
                    .originalException(e);
        }
    }

    public List<ExternalEntityExportCSV> readExternalEntityFromZipFile(MultipartFile file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            findFileInZip(zipInputStream, EXTERNAL_ENTITIES_EXPORT_FILE_NAME);
            final CsvMapper mapper = new CsvMapper();
            final CsvSchema schema = mapper.schemaFor(ExternalEntityExportCSV.class).withHeader().withNullValue("");
            final MappingIterator<ExternalEntityExportCSV> externalSchemasInFileIter = mapper.readerFor(ExternalEntityExportCSV.class).with(schema).readValues(zipInputStream);
            return externalSchemasInFileIter.readAll();
        } catch (IOException e) {
            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.IMPORT_FROM_ZIP_ERROR);
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(aiaApiMessage)
                    .originalException(e);
        }
    }

    public List<ExternalAttributeExportCSV> readExternalAttributesFromZipFile(MultipartFile file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            findFileInZip(zipInputStream, EXTERNAL_ATTRIBUTES_EXPORT_FILE_NAME);
            final CsvMapper mapper = new CsvMapper();
            final CsvSchema schema = mapper.schemaFor(ExternalAttributeExportCSV.class).withHeader().withNullValue("");
            final MappingIterator<ExternalAttributeExportCSV> externalSchemasInFileIter = mapper.readerFor(ExternalAttributeExportCSV.class).with(schema).readValues(zipInputStream);
            return externalSchemasInFileIter.readAll();
        } catch (IOException e) {
            final AiaApiMessage aiaApiMessage = new AiaApiMessage(AiaApiMessages.GENERAL.IMPORT_FROM_ZIP_ERROR);
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(aiaApiMessage)
                    .originalException(e);
        }
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
}
