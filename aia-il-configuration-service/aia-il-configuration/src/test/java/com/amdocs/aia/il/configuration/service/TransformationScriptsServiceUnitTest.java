package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.il.configuration.dto.TransformationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransformationScriptsServiceUnitTest {
    private static final String PROJECT_KEY = "aia";
    private static final String TRANSFORMATION_FOR_SHARED_ENTITY_TRANSFORMATION = "src/test/resources/json/transformation_for_sharedEntityTransformation.json";

    @Mock
    private ILOperations ilOperations;

    @InjectMocks
    private TransformationsScriptsServiceImpl transformationsScriptsService;

    @Test
    void whenExportTransformationsSqlQuery_ShouldReturnInputStreamResource() {
        List<TransformationDTO> transformationDTOList = new ArrayList<>();
        final File file = FileUtils.getFile(TRANSFORMATION_FOR_SHARED_ENTITY_TRANSFORMATION);
        TransformationDTO transformationDTO = readValue(file, TransformationDTO.class);
        transformationDTOList.add(transformationDTO);
        doReturn(transformationDTOList).when(ilOperations).listTransformations(PROJECT_KEY);

        byte[] exportTransformationsScriptsByteArray = transformationsScriptsService.exportTransformationsScripts(PROJECT_KEY);
        byte[] sqlQueryByteArray = createZipAsByteArray(transformationDTO.getId(), transformationDTO.getCustomScript());

        assertEquals(new String(sqlQueryByteArray), new String(exportTransformationsScriptsByteArray));
    }

    private static <T> T readValue(final File file, final Class<T> valueType) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(file, valueType);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] createZipAsByteArray (String id, String sqlQuery) {
        byte[] bytes = new byte[0];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                try {
                    String fileName = id + ".sql";
                    ZipEntry entry = new ZipEntry(fileName);
                    zos.putNextEntry(entry);
                    zos.write(sqlQuery.getBytes(StandardCharsets.UTF_8));
                    zos.closeEntry();
                } catch (IOException ie) { // NOSONAR
                }
            }
            bytes = baos.toByteArray();
        } catch (IOException ie) { // NOSONAR
        }

        return bytes;
    }
}
