package com.amdocs.aia.il.configuration.discovery.csv;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.logical.PrimitiveDatatype;
import com.amdocs.aia.common.model.store.SharedSerializations;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.EntityConfigurationUtils;
import com.amdocs.aia.il.common.model.SchemaConfigurationUtils;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvEntityStoreInfo;
import com.amdocs.aia.il.configuration.discovery.*;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.amdocs.aia.il.configuration.discovery.DiscoveryTestConstants.PROJECT_KEY;
import static com.amdocs.aia.il.configuration.discovery.DiscoveryTestConstants.TEST_USER;
import static com.amdocs.aia.il.configuration.discovery.DiscoveryTestUtils.csv;
import static com.amdocs.aia.il.configuration.discovery.csv.ExternalCsvDiscoveryParameters.COLUMN_DELIMITER;
import static com.amdocs.aia.il.configuration.discovery.csv.ExternalCsvDiscoveryParameters.FILE_NAMES;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DiscoveryTestConfiguration.class, ExternalCsvDiscovererTest.class}, initializers = YamlFileApplicationContextInitializer.class)
public class ExternalCsvDiscovererTest {
    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private AiaRepositoryOperations aiaRepositoryOperations;

    @MockBean
    private DiscoveryFilesRepository discoveryFilesRepository;

    @Autowired
    private ExternalCsvDiscoverer discoverer;

    private SimpleExternalModelDiscoveryConsumer consumer;

    @BeforeEach
    void prepareDiscoverer() {
        consumer = new SimpleExternalModelDiscoveryConsumer();
        discoverer.setConsumer(consumer);
        discoverer.setProjectKey(PROJECT_KEY);
        discoverer.setSchemaType(ExternalSchemaType.CSV_FILES);
        when(aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy()).thenReturn(TEST_USER);
    }

    @Test
    public void discoverSimpleCsv() {
        long startTime = System.currentTimeMillis();
        final String schemaName = "Test Schema";
        final String filename = "EntityTest1.csv";
        when(discoveryFilesRepository.downloadFile(filename)).thenReturn(
                csv(',', "firstName", "lastName", "birthDate"));

        discoverer.setSchemaName(schemaName);
        setDiscoveryParams(ImmutableMap.of(COLUMN_DELIMITER, ",", FILE_NAMES, Collections.singletonList(filename)));
        discoverer.discover();
        long endTime = System.currentTimeMillis();
        assertTrue(consumer.isStarted());
        assertTrue(consumer.isCompleted());

        // schema
        assertEquals(1, consumer.getSchemas().size());
        final ExternalSchema schema = consumer.getSchemas().get(0);
        assertEquals("TestSchema", schema.getSchemaKey());
        assertEquals(schemaName, schema.getName());
        assertEquals(LogicalTypeSystem.NAME, schema.getTypeSystem());
        assertEquals("TestSchema", schema.getDataChannelInfo().getDataChannelName());
        assertEquals(ExternalSchemaType.CSV_FILES, schema.getSchemaType());
        assertEquals(SharedSerializations.JSON, schema.getDataChannelInfo().getSerializationMethod());
        assertEquals(ConfigurationConstants.DISCOVERY_ORIGIN, schema.getOrigin());
        assertEquals(TEST_USER, schema.getCreatedBy());
        assertTrue(schema.getCreatedAt() >= startTime && schema.getCreatedAt() <= endTime);
        assertEquals(SchemaConfigurationUtils.getElementId(schema), schema.getId());
        assertEquals(Availability.EXTERNAL, schema.getAvailability());
        // entity
        assertEquals(1, consumer.getEntities().size());
        final ExternalEntity entity = consumer.getEntities().get(0);
        assertEquals("EntityTest1", entity.getEntityKey());
        assertEquals("Entity Test 1", entity.getName());
        final ExternalCsvEntityStoreInfo storeInfo = (ExternalCsvEntityStoreInfo) entity.getStoreInfo();
        assertEquals(".*\\.csv", storeInfo.getFileNameFormat()); // taken from aia-il-configuration-service.yaml
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", storeInfo.getDateFormat()); // taken from aia-il-configuration-service.yaml
        assertEquals(',', storeInfo.getColumnDelimiter());
        assertEquals(LogicalTypeSystem.NAME, entity.getTypeSystem());
        assertEquals(ExternalEntityReplicationPolicy.REPLICATE, entity.getReplicationPolicy());
        assertNotNull(entity.getCollectionRules());
        assertEquals(ExternalCsvEntityCollectionRules.class, entity.getCollectionRules().getClass());
        assertEquals(discoverer.getSerializationIDAssigner().getStartingKey(), entity.getSerializationId());
        assertEquals(ConfigurationConstants.DISCOVERY_ORIGIN, entity.getOrigin());
        assertEquals(TEST_USER, entity.getCreatedBy());
        assertTrue(entity.getCreatedAt() >= startTime && entity.getCreatedAt() <= endTime);
        assertEquals(EntityConfigurationUtils.getElementId(entity), entity.getId());
        assertEquals(Availability.EXTERNAL, entity.getAvailability());
        // attributes
        assertEquals(3, entity.getAttributes().size());

        final ExternalAttribute firstName = findAttribute(entity, "firstName");
        assertNotNull(firstName);
        assertEquals("firstName", firstName.getAttributeKey());
        assertEquals("First Name", firstName.getName());
        assertEquals(PrimitiveDatatype.STRING.name(), firstName.getDatatype());
        assertEquals(PrimitiveDatatype.STRING.name(), firstName.getLogicalDatatype());
        assertNotNull(firstName.getStoreInfo());
        assertEquals(ExternalCsvAttributeStoreInfo.class, firstName.getStoreInfo().getClass());
        assertEquals(1, firstName.getSerializationId());
        assertEquals(ConfigurationConstants.DISCOVERY_ORIGIN, firstName.getOrigin());

        final ExternalAttribute birthDate = findAttribute(entity, "birthDate");
        assertNotNull(birthDate);
        assertEquals("birthDate", birthDate.getAttributeKey());
        assertEquals("Birth Date", birthDate.getName());
        assertEquals(PrimitiveDatatype.TIMESTAMP.name(), birthDate.getDatatype());
        assertEquals(PrimitiveDatatype.TIMESTAMP.name(), birthDate.getLogicalDatatype());
        assertNotNull(birthDate.getStoreInfo());
        assertEquals(ExternalCsvAttributeStoreInfo.class, birthDate.getStoreInfo().getClass());
        assertEquals(3, birthDate.getSerializationId());
        assertEquals(ConfigurationConstants.DISCOVERY_ORIGIN, birthDate.getOrigin());
    }

    @Test
    public void testMultipleEntities() {
        final String schemaName = "Test Schema";
        String filename1 = "EntityTestA";
        String filename2 = "EntityTestB";
        when(discoveryFilesRepository.downloadFile(filename1)).thenReturn(
                csv(',', "a1", "a2", "a3"));
        when(discoveryFilesRepository.downloadFile(filename2)).thenReturn(
                csv(',', "b1", "b2"));

        discoverer.setSchemaName(schemaName);
        setDiscoveryParams(ImmutableMap.of(
                COLUMN_DELIMITER, ",",
                FILE_NAMES, Arrays.asList(filename1, filename2)));
        discoverer.discover();

        assertEquals(1, consumer.getSchemas().size());
        final ExternalSchema schema = consumer.getSchemas().get(0);
        assertEquals("TestSchema", schema.getSchemaKey());

        final List<ExternalEntity> entities = consumer.getEntities();
        assertEquals(2, entities.size());
        assertEquals("Entity Test A", entities.get(0).getName());
        assertEquals(3, entities.get(0).getAttributes().size());
        assertEquals("Entity Test B", entities.get(1).getName());
        assertEquals(2, entities.get(1).getAttributes().size());
    }

    @Test
    public void testMissingSchemaName() {
        assertThrows(IllegalStateException.class, () -> discoverer.discover());
    }

    @Test
    public void testMissingFilenames() {
        discoverer.setSchemaName("Test Schema");
        assertThrows(ApiException.class, () -> setDiscoveryParams(ImmutableMap.of("columnDelimiter", ",")));
    }

    @Test
    public void testInvalidFilenames() {
        final String filename = "Test.csv";
        discoverer.setSchemaName("Test Schema");
        setDiscoveryParams(ImmutableMap.of(
                FILE_NAMES, Collections.singletonList(filename),
                COLUMN_DELIMITER, ","));
        when(discoveryFilesRepository.downloadFile(filename))
                .thenThrow(new ApiException(
                AiaApiException.AiaApiHttpCodes.BAD_RERQUEST,
                AiaApiMessages.GENERAL.DISCOVERY_FILE_NOT_FOUND));
        assertThrows(ApiException.class, () -> discoverer.discover());
    }

    @Test
    public void testInvalidDelimiter() {
        discoverer.setSchemaName("Test Schema");
        assertThrows(ApiException.class, () ->
                setDiscoveryParams(ImmutableMap.of(
                        FILE_NAMES, Collections.singletonList("Test.csv"),
                        COLUMN_DELIMITER, "NotACharacter")));
    }

    private ExternalAttribute findAttribute(ExternalEntity entity, String attributeKey) {
        return entity.getAttributes().stream().filter(a -> a.getAttributeKey().equals(attributeKey)).findFirst().orElse(null);
    }

    private void setDiscoveryParams(Map<String, Object> parameters) {
        discoverer.setParameters(DiscoveryUtils.buildTypedDiscoveryParameters(parameters, discoverer.getParametersClass()));
    }
}
