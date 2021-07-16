package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.core.web.AiaApiMessage;
import com.amdocs.aia.il.configuration.dto.TransformationDTO;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class TransformationsScriptsServiceImpl implements TransformationsScriptsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransformationsScriptsServiceImpl.class);
    private final ILOperations ilOperations;

    public TransformationsScriptsServiceImpl(ILOperations ilOperations) {
        this.ilOperations = ilOperations;
    }

    @Override
    public byte[] exportTransformationsScripts(String projectKey) {
        byte[] bytes = new byte[0];
        List<TransformationDTO> transformationList = ilOperations.listTransformations(projectKey);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (TransformationDTO transformation : transformationList) {
                    String id = transformation.getId();
                    String sqlQuery = transformation.getCustomScript();
                    if (StringUtils.hasText(sqlQuery)) {
                        try {
                            String fileName = id + ".sql";
                            ZipEntry entry = new ZipEntry(fileName);
                            zos.putNextEntry(entry);
                            zos.write(sqlQuery.getBytes(StandardCharsets.UTF_8));
                            zos.closeEntry();
                            LOGGER.debug("exportTransformationsSqlQuery - successfully created entry for transformation id-: {}", id);
                        } catch (IOException ie) { // NOSONAR
                            LOGGER.error("error in exportTransformationsSqlQuery for transformation id-{}. exception: {}", id, ie);
                            throw ie;
                        }
                    }
                }
            }
            bytes = baos.toByteArray();
        } catch (IOException ioe) { // NOSONAR
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(new AiaApiMessage(AiaApiMessages.GENERAL.UNEXPECTED_SERVER_ERROR, "false"));
        }
        return bytes;
    }
}
