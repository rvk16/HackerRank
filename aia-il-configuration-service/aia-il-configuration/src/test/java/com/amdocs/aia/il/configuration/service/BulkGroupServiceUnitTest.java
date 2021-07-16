package com.amdocs.aia.il.configuration.service;


import com.amdocs.aia.common.core.web.AIAAPIMessageTemplate;
import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.common.model.bulk.BulkGroup;
import com.amdocs.aia.il.configuration.dependencies.BulkGroupDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.BulkGroupDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.mapper.BulkGroupMapper;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.BulkGroupRepository;
import com.amdocs.aia.il.configuration.service.physical.sql.BulkGroupServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BulkGroupServiceUnitTest {
    private static final String PROJECT_KEY = "aia";

    @InjectMocks
    private BulkGroupServiceImpl bulkGroupService;
    @Mock
    private BulkGroupRepository bulkGroupRepository;
    @Mock
    private BulkGroupDependencyAnalyzer bulkGroupDependencyAnalyzer;
    @Mock
    private MessageHelper messageHelper;


    @Test
    void whenGetBulkGroup_ShouldReturnDTO() {
        String schemaStoreKey = "BCMAPP";
        String bulkGroupKey = "Full";

        bulkGroupService.setMapper(new BulkGroupMapper());
        final BulkGroup bulkGroup = createMockBulkGroup(schemaStoreKey,bulkGroupKey);
        when(bulkGroupRepository.getByKey("projectKey", bulkGroup.getSchemaKey(), bulkGroup.getKey())).thenReturn(bulkGroup);
        final BulkGroupDTO bulkGroupDTO = bulkGroupService.get("projectKey", bulkGroup.getSchemaKey(), bulkGroup.getKey());
        assertEquals(schemaStoreKey, bulkGroupDTO.getSchemaKey());
        assertEquals(bulkGroupKey, bulkGroupDTO.getBulkGroupKey());
    }

    @Test
    void whenGetBulkGroup_ShouldThrowException() {
        String schemaStoreKey = "BCMAPP";
        String bulkGroupKey = "Full";

        bulkGroupService.setMapper(new BulkGroupMapper());
        final BulkGroup bulkGroup = createMockBulkGroup(schemaStoreKey,bulkGroupKey);
        doReturn(getApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST))
                .when(messageHelper).createObjectDoesNotExistException(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        final ApiException ex = assertThrows(ApiException.class, () -> bulkGroupService.get("projectKey", bulkGroup.getSchemaKey(), bulkGroup.getKey()));
        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void whenDoingSaveForBulkGroup_existingModelIdIsNotNull() {
        String schemaStoreKey = "BCMAPP";
        String bulkGroupKey = "Full";

        final BulkGroupDTO bulkGroupDTO = new BulkGroupDTO();
        final BulkGroup bulkGroup = createMockBulkGroup(schemaStoreKey,bulkGroupKey);
        doReturn(getApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST))
                .when(messageHelper).createObjectAlreadyExistException(Mockito.anyString(), Mockito.anyString());
        ApiException ex = assertThrows(ApiException.class, () -> bulkGroupService.doSave( bulkGroupDTO, bulkGroup));

        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
        assertEquals(AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST, ex.getMessageKey());
    }

    @Test
    void whenIsBulkGroupExist_shouldReturnTrue() {
        String schemaStoreKey = "BCMAPP";
        String bulkGroupKey = "Full";

        final BulkGroup bulkGroup = createMockBulkGroup(schemaStoreKey,bulkGroupKey);
        doReturn(bulkGroup).when(bulkGroupRepository).getByKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        boolean isBulkGroupKeyExistInSchema = bulkGroupService.isBulkGroupKeyExistInSchema(bulkGroup);
        assertTrue(isBulkGroupKeyExistInSchema);
    }

    public static BulkGroup createMockBulkGroup(String schemaKey, String bulkGroupKey) {
        BulkGroup bulkGroup = new BulkGroup();

        bulkGroup.setSchemaKey(schemaKey);
        bulkGroup.setProjectKey(PROJECT_KEY);
        bulkGroup.setDescription(schemaKey + "description");
        bulkGroup.setKey(bulkGroupKey);

        return bulkGroup;
    }

    private static ApiException getApiException(AiaApiException.AiaApiHttpCodes status, AIAAPIMessageTemplate message) {
        return new ApiException(status, message, "display_name", "element_key");
    }
}
