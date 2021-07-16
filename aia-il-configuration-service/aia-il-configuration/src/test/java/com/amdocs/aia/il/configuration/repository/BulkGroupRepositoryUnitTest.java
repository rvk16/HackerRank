package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.il.common.model.bulk.BulkGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BulkGroupRepositoryUnitTest {
    private static final String PROJECT_KEY = "aia";

    @Spy
    @InjectMocks
    private BulkGroupRepository bulkGroupRepository;

    @Test
    void when_findBulkGroupByKey_shouldReturn() {
        BulkGroup bulkGroup = getBulkGroup();
        doReturn(bulkGroup).when(bulkGroupRepository).getByKey(Mockito.any(), Mockito.any(), Mockito.any());
        BulkGroup actual = bulkGroupRepository.getByKey(PROJECT_KEY,
                bulkGroup.getSchemaKey(), bulkGroup.getKey());
        assertEquals(bulkGroup.getSchemaKey(), actual.getSchemaKey());
        assertEquals(bulkGroup.getProjectKey(), actual.getProjectKey());
    }

    private static BulkGroup getBulkGroup() {
        BulkGroup bulkGroup = new BulkGroup();
        bulkGroup.setProjectKey(PROJECT_KEY);
        bulkGroup.setSchemaKey("BCM");
        bulkGroup.setDescription(bulkGroup.getSchemaKey() + "description");
        bulkGroup.setName("Full");
        bulkGroup.setKey("full");
        bulkGroup.setOriginProcess(OriginProcess.MAPPING_SHEETS_MIGRATION);

        return bulkGroup;
    }
}
