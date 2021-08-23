package com.amdocs.aia.il.configuration.discovery.json;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaEntityStoreInfo;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.configuration.discovery.AbstractExternalModelDiscoveryParameters.AVAILABILITY;
import static com.amdocs.aia.il.configuration.discovery.DiscoveryTestConstants.PROJECT_KEY;
import static com.amdocs.aia.il.configuration.discovery.DiscoveryTestConstants.TEST_USER;
import static com.amdocs.aia.il.configuration.discovery.DiscoveryTestUtils.csv;
import static com.amdocs.aia.il.configuration.discovery.DiscoveryTestUtils.testFile;
import static com.amdocs.aia.il.configuration.discovery.json.ExternalJsonDiscoveryParameters.FILE_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DiscoveryTestConfiguration.class, ExternalJsonDiscovererTest.class}, initializers = YamlFileApplicationContextInitializer.class)
@TestPropertySource(properties = {
        "aia.il.discovery.json.swagger-processing-rules.JSON_OVER_KAFKA.additional-root-entities[0].entity-key=Event",
        "aia.il.discovery.json.swagger-processing-rules.JSON_OVER_KAFKA.additional-root-entities[0].path=",
        "aia.il.discovery.json.swagger-processing-rules.JSON_OVER_KAFKA.additional-root-entities[1].entity-key=Payload",
        "aia.il.discovery.json.swagger-processing-rules.JSON_OVER_KAFKA.additional-root-entities[1].path=payload",
        "aia.il.discovery.json.swagger-processing-rules.JSON_OVER_KAFKA.forced-one-to-one-entity-paths-regex[0]=^payload$",
        "aia.il.discovery.json.swagger-processing-rules.JSON_OVER_KAFKA.forced-one-to-one-entity-paths-regex[1]=^payload/[^/]+$"
})
public class ExternalJsonDiscovererTest {
    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private AiaRepositoryOperations aiaRepositoryOperations;

    @MockBean
    private DiscoveryFilesRepository discoveryFilesRepository;

    @Autowired
    private ExternalJsonDiscoverer discoverer;

    private SimpleExternalModelDiscoveryConsumer consumer;

    @BeforeEach
    void prepareDiscoverer() {
        consumer = new SimpleExternalModelDiscoveryConsumer();
        discoverer.setConsumer(consumer);
        discoverer.setProjectKey(PROJECT_KEY);
        discoverer.setSchemaType(ExternalSchemaType.JSON_OVER_KAFKA);
        when(aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy()).thenReturn(TEST_USER);
    }

    @Test
    public void testMissingSchemaName() {
        assertThrows(IllegalStateException.class, () -> discoverer.discover());
    }

    @Test
    public void testMissingFilename() {
        discoverer.setSchemaName("Test Schema");
        assertThrows(ApiException.class, () -> setDiscoveryParams(ImmutableMap.of()));
    }

    @Test
    public void testInvalidParameters() {
        discoverer.setSchemaName("Test Schema");
        assertThrows(ApiException.class, () -> setDiscoveryParams(ImmutableMap.of("InvalidParam", "DummyValue")));
    }

    @Test
    public void testInvalidFileFormat() {
        String filename = "not_swagger.yaml";
        when(discoveryFilesRepository.downloadFile(filename)).thenReturn(
                csv(',', "a1", "a2", "a3"));
        discoverer.setSchemaName("Test Schema");
        setDiscoveryParams(ImmutableMap.of(FILE_NAME, Collections.singletonList(filename)));
        assertThrows(ApiException.class, () -> discoverer.discover());
    }

    @Test
    public void testSwaggerNoEntities() {
        String filename = "swagger-no-definitions.yaml";
        mockDiscoveryFile(filename);
        discoverer.setSchemaName("Test Schema");
        setDiscoveryParams(ImmutableMap.of(FILE_NAME, Collections.singletonList(filename)));
        discoverer.discover();
        assertEquals(1, consumer.getSchemas().size());
        final ExternalSchema schema = consumer.getSchemas().get(0);
        assertEquals("Test Schema", schema.getName());
        assertEquals("TestSchema", schema.getSchemaKey());
        assertEquals(ExternalSchemaType.JSON_OVER_KAFKA, schema.getSchemaType());
        assertEquals(filename, schema.getOrigin());
    }

    @Test
    public void testD1ProductInventory() {
        String filename = "pi-management-notifications.yaml";
        mockDiscoveryFile(filename);
        discoverer.setSchemaType(ExternalSchemaType.DIGITAL1);
        discoverer.setSchemaName("D1 Product Inventory");
        setDiscoveryParams(ImmutableMap.of(FILE_NAME, Collections.singletonList(filename)));
        discoverer.discover();
        assertEquals(1, consumer.getSchemas().size());
        final ExternalSchema schema = consumer.getSchemas().get(0);
        assertEquals("D1 Product Inventory", schema.getName());
        assertEquals("D1ProductInventory", schema.getSchemaKey());
        assertEquals(ExternalSchemaType.DIGITAL1, schema.getSchemaType());
        assertEquals(filename, schema.getOrigin());

        assertEquals(39, consumer.getEntities().size());
        assertEquals(discoverer.getSerializationIDAssigner().getStartingKey(), consumer.getEntities().get(0).getSerializationId());
        assertEquals(discoverer.getSerializationIDAssigner().getStartingKey() + 1, consumer.getEntities().get(1).getSerializationId());

        final ExternalEntity productTerm = findCreatedEntity("ProductTerm");
        assertNotNull(productTerm);
        assertEquals(19, productTerm.getAttributes().size());
        AtomicInteger expectedSerializationId = new AtomicInteger(0);
        productTerm.getAttributes().stream().forEach(att -> assertEquals(expectedSerializationId.incrementAndGet(), att.getSerializationId()));
        assertEquals(1, productTerm.getAttributes().get(0).getSerializationId());
        assertEquals(2, productTerm.getAttributes().get(1).getSerializationId());
        assertEquals(3, productTerm.getAttributes().get(2).getSerializationId());
        final ExternalAttribute nameAtt = findAttribute(productTerm, "name");
        assertNotNull(nameAtt);
        assertNull(((ExternalKafkaAttributeStoreInfo) nameAtt.getStoreInfo()).getJsonPath());

        final ExternalAttribute durationUnitsAtt = findAttribute(productTerm, "duration_units");
        assertNotNull(durationUnitsAtt);
        assertEquals("productTerm/duration/units", ((ExternalKafkaAttributeStoreInfo) durationUnitsAtt.getStoreInfo()).getJsonPath());

        final ExternalKafkaEntityStoreInfo entityStoreInfo = (ExternalKafkaEntityStoreInfo) productTerm.getStoreInfo();

        final List<String> relativePaths = splitPaths(entityStoreInfo.getRelativePaths());
        assertEquals(Arrays.asList(
                "compositeProduct/productTerm",
                "product(STR)/productTerm"
        ), relativePaths);

        final List<String> mergedNodes = splitPaths(entityStoreInfo.getMergedNodes());
        assertEquals(Arrays.asList(
                "changeTermPolicy/overrideValidFor",
                "productTerm/changeTermPolicy",
                "productTerm/duration",
                "productTerm/elapsedDuration",
                "productTerm/remainingDuration",
                "productTerm/validFor"
        ), mergedNodes);

        final ExternalEntity relatedPartyRef = findCreatedEntity("RelatedPartyRef");
        assertEquals(10, relatedPartyRef.getAttributes().size());
        final ExternalAttribute referredType = findAttribute(relatedPartyRef, "ref_referredType");
        assertNotNull(referredType);

    }

    @Test
    public void testD1PartyInteraction_specialCharacter() {
        String filename = "partyinteraction-mng-notif.yaml";
        mockDiscoveryFile(filename);
        discoverer.setSchemaType(ExternalSchemaType.DIGITAL1);
        discoverer.setSchemaName("D1 Product Inventory");
        setDiscoveryParams(ImmutableMap.of(FILE_NAME, Collections.singletonList(filename)));
        discoverer.discover();
        assertEquals(1, consumer.getSchemas().size());
        final ExternalSchema schema = consumer.getSchemas().get(0);

        final ExternalEntity partyInteraction = findCreatedEntity("PartyInteraction");
        assertEquals(14, partyInteraction.getAttributes().size());
        final ExternalAttribute refType = findAttribute(partyInteraction, "ref_type");
        assertNotNull(refType);

        assertEquals("event/partyInteraction(STR)/@type", ((ExternalKafkaAttributeStoreInfo) refType.getStoreInfo()).getJsonPath());


        final ExternalEntity relatedParty = findCreatedEntity("RelatedParty");
        assertEquals(8, relatedParty.getAttributes().size());
        final ExternalAttribute referredType = findAttribute(relatedParty, "ref_referredType");
        assertNotNull(referredType);

        assertEquals("interactionItem/relatedParty/@referredType,partyInteraction(STR)/relatedParty/@referredType", ((ExternalKafkaAttributeStoreInfo) referredType.getStoreInfo()).getJsonPath());


        final ExternalEntity relatedChannel = findCreatedEntity("RelatedChannel");
        assertEquals(6, relatedChannel.getAttributes().size());
        final ExternalAttribute refType1 = findAttribute(relatedChannel, "ref_type");
        assertNotNull(refType1);

        assertEquals("partyInteraction(STR)/channel/@type,interactionItem/channel/@type", ((ExternalKafkaAttributeStoreInfo) refType1.getStoreInfo()).getJsonPath());


    }

    @Test
    public void testPetstore() {
        String filename = "Petstore.yaml";
        mockDiscoveryFile(filename);
        discoverer.setSchemaType(ExternalSchemaType.JSON_OVER_KAFKA);
        discoverer.setSchemaName("Swagger Petstore");
        setDiscoveryParams(ImmutableMap.of(FILE_NAME, Collections.singletonList(filename)));
        discoverer.discover();
        assertEquals(1, consumer.getSchemas().size());
        final ExternalSchema schema = consumer.getSchemas().get(0);
        assertEquals("Swagger Petstore", schema.getName());
        assertEquals("SwaggerPetstore", schema.getSchemaKey());
        assertEquals(ExternalSchemaType.JSON_OVER_KAFKA, schema.getSchemaType());
        assertEquals(filename, schema.getOrigin());

        assertEquals(4, consumer.getEntities().size());
        final ExternalEntity user = findCreatedEntity("User");
        assertNotNull(user);
        assertEquals("/User", ((ExternalKafkaEntityStoreInfo) user.getStoreInfo()).getRelativePaths());
        assertEquals(8, user.getAttributes().size());
    }

    @Test
    public void testDeepIo() {
        String filename = "deepio.yaml";
        mockDiscoveryFile(filename);
        discoverer.setSchemaType(ExternalSchemaType.JSON_OVER_KAFKA);
        discoverer.setSchemaName("Deep IO");
        setDiscoveryParams(ImmutableMap.of(FILE_NAME, Collections.singletonList(filename)));
        discoverer.discover();
        assertEquals(1, consumer.getSchemas().size());
        final ExternalSchema schema = consumer.getSchemas().get(0);
        assertEquals("Deep IO", schema.getName());
        assertEquals("DeepIO", schema.getSchemaKey());
        assertEquals(ExternalSchemaType.JSON_OVER_KAFKA, schema.getSchemaType());
        assertEquals(filename, schema.getOrigin());

        assertEquals(8, consumer.getEntities().size());
    }

    private List<String> splitPaths(String pathsAsString) {
        return Arrays.stream(pathsAsString.split(",")).sorted().collect(Collectors.toList());
    }

    private ExternalEntity findCreatedEntity(String entityKey) {
        return consumer.getEntities().stream()
                .filter(entity -> entity.getEntityKey().equals(entityKey))
                .findFirst()
                .orElse(null);
    }

    private ExternalAttribute findAttribute(ExternalEntity entity, String attributeKey) {
        return entity.getAttributes().stream().filter(a -> a.getAttributeKey().equals(attributeKey)).findFirst().orElse(null);
    }

    @Test
    public void testD1ProductOrder() {
        String filename = "product-ordering-management-notifications.yaml";
        mockDiscoveryFile(filename);
        discoverer.setSchemaType(ExternalSchemaType.DIGITAL1);
        discoverer.setSchemaName("D1 Order");
        setDiscoveryParams(ImmutableMap.of(
                FILE_NAME, Collections.singletonList(filename),
                AVAILABILITY, Availability.SHARED));
        discoverer.discover();
        assertEquals(1, consumer.getSchemas().size());
        final ExternalSchema schema = consumer.getSchemas().get(0);
        assertEquals("D1 Order", schema.getName());
        assertEquals("D1Order", schema.getSchemaKey());
        assertEquals(ExternalSchemaType.DIGITAL1, schema.getSchemaType());
        assertEquals(filename, schema.getOrigin());

        assertEquals(69, consumer.getEntities().size());
        assertTrue(consumer.getEntities().stream()
                .anyMatch(entity -> entity.getEntityKey().equals("D1ProductOrder")));
    }

    @Test
    public void testInvalidFilename() {
        final String filename = "Test.csv";
        discoverer.setSchemaName("Test Schema");
        setDiscoveryParams(ImmutableMap.of(FILE_NAME, Collections.singletonList(filename)));
        when(discoveryFilesRepository.downloadFile(filename))
                .thenThrow(new ApiException(
                        AiaApiException.AiaApiHttpCodes.BAD_RERQUEST,
                        AiaApiMessages.GENERAL.DISCOVERY_FILE_NOT_FOUND));
        assertThrows(ApiException.class, () -> discoverer.discover());
    }

    private void setDiscoveryParams(Map<String, Object> parameters) {
        discoverer.setParameters(DiscoveryUtils.buildTypedDiscoveryParameters(parameters, discoverer.getParametersClass()));
    }

    private void mockDiscoveryFile(String testFilename) {
        when(discoveryFilesRepository.downloadFile(testFilename)).thenReturn(
                testFile("discovery/swagger/" + testFilename));
    }
}
