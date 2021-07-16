package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.utils.WebUtils;
import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.core.web.AiaApiMessage;
import com.amdocs.aia.common.model.repo.AsyncProcessRequestDTO;
import com.amdocs.aia.common.model.repo.AsyncProcessResponseDTO;
import com.amdocs.aia.il.common.model.external.Availability;
import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import com.amdocs.aia.il.configuration.discovery.AbstractExternalModelDiscoveryParameters;
import com.amdocs.aia.il.configuration.discovery.DiscoveryExecutor;
import com.amdocs.aia.il.configuration.discovery.DiscoveryFilesRepository;
import com.amdocs.aia.il.configuration.discovery.ExternalModelDiscoveryConsumer;
import com.amdocs.aia.il.configuration.discovery.csv.ExternalCsvDiscoveryParameters;
import com.amdocs.aia.il.configuration.discovery.json.ExternalJsonDiscoveryParameters;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.amdocs.aia.il.configuration.discovery.DiscoveryUtils.convertDiscoveryParametersToMap;
import static com.amdocs.aia.il.configuration.discovery.DiscoveryUtils.discoveryRequestToJson;

@Service
public class DiscoveryServiceImpl implements DiscoveryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryServiceImpl.class);

    public static final String DISCOVER_EXTERNAL_CSV_DISPLAY_NAME = "Discover External Csv";
    public static final String DISCOVER_EXTERNAL_JSON_DISPLAY_NAME = "Discover External Json";

    private final DiscoveryFilesRepository discoveryFilesRepository;
    private final AiaRepositoryOperations repositoryOperations;
    private final DiscoveryExecutor discoveryExecutor;
    private final ExternalModelDiscoveryConsumer discoveryConsumer;

    public DiscoveryServiceImpl(DiscoveryFilesRepository discoveryFilesRepository,
                                AiaRepositoryOperations repositoryOperations, DiscoveryExecutor discoveryExecutor,
                                ExternalModelDiscoveryConsumer discoveryConsumer) {
        this.discoveryFilesRepository = discoveryFilesRepository;
        this.repositoryOperations = repositoryOperations;
        this.discoveryExecutor = discoveryExecutor;
        this.discoveryConsumer = discoveryConsumer;
    }

    public AsyncResponseDTO discoverExternalCsvAsync(String projectKey, DiscoverExternalCsvRequestDTO discoverExternalCsvRequestDTO) {
        ExternalCsvDiscoveryParameters discoveryParams = new ExternalCsvDiscoveryParameters();
        if (!StringUtils.isEmpty(discoverExternalCsvRequestDTO.getColumnDelimiter())) {
            discoveryParams.setColumnDelimiter(discoverExternalCsvRequestDTO.getColumnDelimiter().charAt(0));
        }
        discoveryParams.setAvailability(toAvailabilityModel(discoverExternalCsvRequestDTO.getAvailability()));
        discoveryParams.setSubjectAreaName(discoverExternalCsvRequestDTO.getSubjectAreaName());
        discoveryParams.setSubjectAreaKey(discoverExternalCsvRequestDTO.getSubjectAreaKey());
        discoveryParams.setFilenames(discoverExternalCsvRequestDTO.getFilenames());
        return generalDiscovery(projectKey, discoveryParams, DISCOVER_EXTERNAL_CSV_DISPLAY_NAME, discoverExternalCsvRequestDTO.getSchemaName(), ExternalSchemaType.CSV_FILES.name());
    }

    public AsyncResponseDTO discoverExternalJsonAsync(String projectKey, DiscoverExternalJsonRequestDTO discoverExternalJsonRequestDTO) {
        ExternalJsonDiscoveryParameters discoveryParams = new ExternalJsonDiscoveryParameters();
        discoveryParams.setFilename(discoverExternalJsonRequestDTO.getFilename());
        discoveryParams.setAvailability(toAvailabilityModel(discoverExternalJsonRequestDTO.getAvailability()));
        discoveryParams.setSubjectAreaName(discoverExternalJsonRequestDTO.getSubjectAreaName());
        discoveryParams.setSubjectAreaKey(discoverExternalJsonRequestDTO.getSubjectAreaKey());
        return generalDiscovery(projectKey, discoveryParams, DISCOVER_EXTERNAL_JSON_DISPLAY_NAME, discoverExternalJsonRequestDTO.getSchemaName(), discoverExternalJsonRequestDTO.getExternalSchemaType());
    }

    public AsyncResponseDTO generalDiscovery(String projectKey, AbstractExternalModelDiscoveryParameters discoveryParams, String operationDisplayName, String schemaName, String externalSchemaType) {
        try {
            int aiaCr = Integer.parseInt(WebUtils.extractHeaderParameterFromRequest(WebUtils.AIA_CR_HEADER_PARAM));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("The CR Id used is: {}", aiaCr);
            }
            repositoryOperations.updateChangeRequestWriteMode(aiaCr, false);
        } catch (Exception e) { // NOSONAR
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST)
                    .message(new AiaApiMessage(AiaApiMessages.GENERAL.ERROR_WHILE_UPDATING_CR_WRITE_MODE, "false"));
        }

        SchemaDiscoveryRequestDTO request = new SchemaDiscoveryRequestDTO()
                .schemaName(schemaName)
                .externalSchemaType(externalSchemaType)
                .parameters(convertDiscoveryParametersToMap(discoveryParams));
        final String discoveryRequestJson = discoveryRequestToJson(request);

        AsyncProcessRequestDTO asyncProcessRequestDTO = new AsyncProcessRequestDTO();
        asyncProcessRequestDTO.setOperationName("discover-external-model");
        asyncProcessRequestDTO.setOperationDisplayName(operationDisplayName);
        asyncProcessRequestDTO.setParam("project_key", projectKey);
        asyncProcessRequestDTO.setParam("discovery_request", discoveryRequestJson);
        asyncProcessRequestDTO.setParam("addHeaderParams", true);
        asyncProcessRequestDTO.setShouldLockChangeRequestForWriting(true);
        AsyncProcessResponseDTO asyncProcessResponseDTO = repositoryOperations.launchAsyncProcess(asyncProcessRequestDTO);
        AsyncResponseDTO asyncResponseDTO = new AsyncResponseDTO();
        asyncResponseDTO.setProcessId(asyncProcessResponseDTO.getProcessId());
        return asyncResponseDTO;
    }

    public UploadDiscoveryFileResponseDTO uploadDiscoveryFile(MultipartFile file) {
        try {
            String fileID = discoveryFilesRepository.uploadFile(file.getOriginalFilename(), file.getInputStream());
            return (new UploadDiscoveryFileResponseDTO().fileId(fileID));

        } catch (IOException e) {
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST)
                    .message(new AiaApiMessage(AiaApiMessages.GENERAL.DISCOVERY_FAILED_IO_ERROR));
        }
    }

    @Override
    public void discoverExternalSchema(String projectKey, SchemaDiscoveryRequestDTO discoveryRequest) {
        try {
            discoveryExecutor.execute(
                    projectKey,
                    ExternalSchemaType.valueOf(discoveryRequest.getExternalSchemaType()),
                    discoveryRequest.getSchemaName(),
                    discoveryRequest.getParameters(),
                    discoveryConsumer);
        } finally {
            discoveryFilesRepository.cleanAllFiles();
        }
    }

    private static Availability toAvailabilityModel(AvailabilityDTO dto) {
        return dto == AvailabilityDTO.SHARED ? Availability.SHARED : Availability.EXTERNAL;
    }
}