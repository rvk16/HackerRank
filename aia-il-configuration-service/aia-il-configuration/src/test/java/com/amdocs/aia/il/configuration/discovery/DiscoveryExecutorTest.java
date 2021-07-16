package com.amdocs.aia.il.configuration.discovery;

import com.amdocs.aia.il.common.model.external.Availability;
import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import com.amdocs.aia.il.configuration.discovery.csv.ExternalCsvDiscoveryParameters;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.amdocs.aia.il.configuration.discovery.DiscoveryTestConstants.PROJECT_KEY;
import static com.amdocs.aia.il.configuration.discovery.DiscoveryTestConstants.TEST_USER;
import static com.amdocs.aia.il.configuration.discovery.DiscoveryTestUtils.csv;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DiscoveryTestConfiguration.class, DiscoveryExecutorTest.class}, initializers = YamlFileApplicationContextInitializer.class)
public class DiscoveryExecutorTest {

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private AiaRepositoryOperations aiaRepositoryOperations;

    @MockBean
    private DiscoveryFilesRepository discoveryFilesRepository;

    @Autowired
    private DiscoveryExecutor executor;

    private SimpleExternalModelDiscoveryConsumer consumer;

    @BeforeEach
    void prepareDiscoverer() {
        consumer = new SimpleExternalModelDiscoveryConsumer();
        when(aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy()).thenReturn(TEST_USER);
    }

    @Test
    public void testCsvDiscovery() {
        final String filename = "Entity1.csv";
        when(discoveryFilesRepository.downloadFile(filename)).thenReturn(
                csv('|', "firstName", "lastName"));

        assertEquals(0, consumer.getSchemas().size());
        executor.execute(PROJECT_KEY,
                ExternalSchemaType.CSV_FILES,
                "Test CSV Schema",
                ImmutableMap.of(ExternalCsvDiscoveryParameters.FILE_NAMES, filename,
                        AbstractExternalModelDiscoveryParameters.AVAILABILITY, Availability.SHARED,
                        ExternalCsvDiscoveryParameters.COLUMN_DELIMITER, "|"),
                consumer
        );

        assertEquals(1, consumer.getSchemas().size());
        assertEquals(ExternalSchemaType.CSV_FILES, consumer.getSchemas().get(0).getSchemaType());
        assertEquals(Availability.SHARED, consumer.getSchemas().get(0).getAvailability());
        assertEquals(1, consumer.getEntities().size());
        assertEquals(ExternalSchemaType.CSV_FILES, consumer.getEntities().get(0).getSchemaType());
        assertEquals(Availability.SHARED, consumer.getEntities().get(0).getAvailability());
        assertEquals(2, consumer.getEntities().get(0).getAttributes().size());

    }
}
