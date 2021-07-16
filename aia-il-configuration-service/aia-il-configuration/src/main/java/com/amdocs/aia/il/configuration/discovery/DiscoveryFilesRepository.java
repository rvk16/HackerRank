package com.amdocs.aia.il.configuration.discovery;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DiscoveryFilesRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryFilesRepository.class);
    private static final String FILE_ID_PREFIX = ConfigurationConstants.PRODUCT_KEY + "_discovery_";

    private final AiaRepositoryOperations aiaRepositoryOperations;

    public DiscoveryFilesRepository(AiaRepositoryOperations repositoryOperations) {
        this.aiaRepositoryOperations = repositoryOperations;
    }

    public String uploadFile(String filename, InputStream inputStream) {
        String fileId = getFileId(filename);
        aiaRepositoryOperations.uploadExternalFile(inputStream, fileId);
        return fileId;
    }

    public InputStream downloadFile(String filename) {
        String fileId = getFileId(filename);
        try {
            final Resource resource = aiaRepositoryOperations.downloadExternalFile(fileId);
            if (resource == null) {
                throw new ApiException(
                        AiaApiException.AiaApiHttpCodes.BAD_RERQUEST,
                        AiaApiMessages.GENERAL.DISCOVERY_FILE_NOT_FOUND,
                        filename,
                        fileId);
            }
            return resource.getInputStream();
        } catch (Exception e) {
            LOGGER.error("Failed opening input stream for file {}", fileId, e);
            throw new ApiException(
                    AiaApiException.AiaApiHttpCodes.BAD_RERQUEST,
                    AiaApiMessages.GENERAL.DISCOVERY_FAILED_DOWNLOADING_FILE,
                    filename,
                    fileId);
        }
    }

    public void cleanAllFiles() {
        aiaRepositoryOperations.listExternalFiles().stream()
                .map(FilenameUtils::getName)
                .filter(this::isDiscoveryFile)
                .forEach(aiaRepositoryOperations::deleteExternalFileById);
    }

    private boolean isDiscoveryFile(String fileId) {
        return fileId.startsWith(FILE_ID_PREFIX);
    }

    private String getFileId(String filename) {
        return FILE_ID_PREFIX + filename;
    }
}
