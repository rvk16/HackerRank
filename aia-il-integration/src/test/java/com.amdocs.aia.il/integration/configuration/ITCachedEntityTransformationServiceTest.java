package com.amdocs.aia.il.integration.configuration;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.common.model.repo.ElementData;
import com.amdocs.aia.il.configuration.client.dto.*;
import com.amdocs.aia.il.integration.BaseIntegrationTest;
import io.restassured.response.Response;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.*;
import static org.testng.Assert.*;

public class ITCachedEntityTransformationServiceTest extends BaseIntegrationTest {
    private static final String CACHE = "CACHE";
    private static final String TRANSFORMATION_ELEMENT_TYPE = "Transformation";
    private static final String PRODUCT_KEY = "INTEGRATIONLAYER";
    private static final String NOT_EXISTING_KEY = "notExistingKey";
    private static final String CACHED_ENTITY_TRANSFORMATION1 = "cachedEntity1";
    private static final String CACHED_ENTITY_TRANSFORMATION2 = "cachedEntity2";

    @BeforeClass
    public void setUpConf() {
        createChangeRequest("IL Cached Entity Transformation - Test");
    }

    @Override
    protected File getConfigurationZip(){
        return new File("src/test/resources/data/data.zip");
    }

    @Test
    void T01_whenCreateCachedEntityTransformation_shouldCreateCachedEntityTransformation() {
        EntityTransformationDTO dto = generateEntityTransformation(CACHED_ENTITY_TRANSFORMATION1);
        Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/cached-entity-transformation",
                HttpStatus.SC_CREATED, dto);
        EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);

        assertEquals(responseEntityTransformationDTO.getLogicalEntityKey(), dto.getLogicalEntityKey());
        assertEquals(responseEntityTransformationDTO.getLogicalSchemaKey(), dto.getLogicalSchemaKey());
        assertEquals(responseEntityTransformationDTO.getEntityName(), dto.getEntityName());
        assertEquals(responseEntityTransformationDTO.getDescription(), dto.getDescription());
        assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getTargetSchemaName(), dto.getTransformations().get(0).getTargetSchemaName());
        assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getStoreName(), "CACHE-cachedEntity1-REFContext");
        assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getId(), "CACHE-cachedEntity1-REFContext");

        assertEquals(responseEntityTransformationDTO.getAttributes().size(), 2 );
        Optional<TransformationAttributeDTO> transformationAttributeDTO1 = responseEntityTransformationDTO.getAttributes().stream().filter(transformationAttributeDTO -> transformationAttributeDTO.getAttributeKey().equals("attr1")).findFirst();
        TransformationAttributeDTO transformationAttribute1 =  transformationAttributeDTO1.get();
        assertEquals(transformationAttribute1.getAttributeKey(), dto.getAttributes().get(0).getAttributeKey());
        assertEquals(transformationAttribute1.getAttributeName(), dto.getAttributes().get(0).getAttributeName());
        assertEquals(transformationAttribute1.getType(), dto.getAttributes().get(0).getType());

        Optional<TransformationAttributeDTO> transformationAttributeDTO2 = responseEntityTransformationDTO.getAttributes().stream().filter(transformationAttributeDTO -> transformationAttributeDTO.getAttributeKey().equals("attr2")).findFirst();
        TransformationAttributeDTO transformationAttribute2 =  transformationAttributeDTO2.get();
        assertEquals(transformationAttribute2.getAttributeKey(), dto.getAttributes().get(1).getAttributeKey());
        assertEquals(transformationAttribute2.getAttributeName(), dto.getAttributes().get(1).getAttributeName());
        assertEquals(transformationAttribute2.getType(), dto.getAttributes().get(1).getType());

        List<ElementData> repoTransformations = getElementsForLogicalEntity(TRANSFORMATION_ELEMENT_TYPE);
        assertFalse(repoTransformations.isEmpty());
        Optional<ElementData> transformation = repoTransformations.stream()
                .filter(elementData -> elementData.getElementContent().contains(String.format("key\" : \"%s", responseEntityTransformationDTO.getTransformations().get(0).getStoreName()))).findAny();
        Assert.assertTrue(transformation.isPresent());

        //check referenceSourceEntities
        Optional<TransformationContextEntityDTO> transformationContextEntityDTO1 = responseEntityTransformationDTO.getTransformations().get(0).getReferenceSourceEntities().stream().filter(transformationContextEntityDTO -> transformationContextEntityDTO.getEntityStoreKey().equals("MTM_BUS_ORG95_CUSTOMER1")).findFirst();
        TransformationContextEntityDTO transformationContextEntity1 =  transformationContextEntityDTO1.get();
        assertEquals(transformationContextEntity1.getEntityStoreKey(), "MTM_BUS_ORG95_CUSTOMER1");
        assertEquals(transformationContextEntity1.getSchemaStoreKey(), "CRM");
    }

    @Test
    void T02_whenUpdateCachedEntityTransformation_shouldUpdateCachedEntityTransformation() {
        EntityTransformationDTO dto = generateEntityTransformation(CACHED_ENTITY_TRANSFORMATION2);
        Response response1 = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/cached-entity-transformation",
                HttpStatus.SC_CREATED, dto);
        EntityTransformationDTO responseEntityTransformationBeforeUpdate = response1.as(EntityTransformationDTO.class);

        TransformationAttributeDTO transformationAttribute = new TransformationAttributeDTO()
                .attributeKey("attr3")
                .attributeName("Attribute 3")
                .type("INTEGER")
                .keyPosition(false)
                .sortOrder(1000)
                .isLogicalTime(false)
                .isUpdateTime(false)
                .isRequired(false)
                .doReferencialIntegrity(false);

        responseEntityTransformationBeforeUpdate.getAttributes().get(0).setAttributeName("Updated Attribute");
        responseEntityTransformationBeforeUpdate.getAttributes().add(transformationAttribute);
        assertEquals(responseEntityTransformationBeforeUpdate.getAttributes().size(), 3);
        responseEntityTransformationBeforeUpdate.getAttributes().remove(1);

        Response response2 = assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/cached-entity-transformation",
                HttpStatus.SC_OK, responseEntityTransformationBeforeUpdate);
        EntityTransformationDTO responseEntityTransformationAfterUpdate = response2.as(EntityTransformationDTO.class);

        assertEquals(responseEntityTransformationAfterUpdate.getLogicalEntityKey(), responseEntityTransformationBeforeUpdate.getLogicalEntityKey());
        assertEquals(responseEntityTransformationAfterUpdate.getLogicalSchemaKey(), responseEntityTransformationBeforeUpdate.getLogicalSchemaKey());
        assertEquals(responseEntityTransformationAfterUpdate.getEntityName(), responseEntityTransformationBeforeUpdate.getEntityName());
        assertEquals(responseEntityTransformationAfterUpdate.getDescription(), responseEntityTransformationBeforeUpdate.getDescription());
        assertEquals(responseEntityTransformationAfterUpdate.getTransformations().get(0).getTargetSchemaName(), responseEntityTransformationBeforeUpdate.getTransformations().get(0).getTargetSchemaName());
        assertEquals(responseEntityTransformationAfterUpdate.getTransformations().get(0).getStoreName(), responseEntityTransformationBeforeUpdate.getTransformations().get(0).getStoreName());
        assertEquals(responseEntityTransformationAfterUpdate.getTransformations().get(0).getId(), responseEntityTransformationBeforeUpdate.getTransformations().get(0).getId());

        assertEquals(responseEntityTransformationAfterUpdate.getAttributes().size(), 2);
        Optional<TransformationAttributeDTO> transformationAttributeDTO1 = responseEntityTransformationAfterUpdate.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("attr1")).findFirst();
        TransformationAttributeDTO transformationAttribute1 =  transformationAttributeDTO1.get();
        assertEquals(transformationAttribute1.getAttributeKey(), responseEntityTransformationBeforeUpdate.getAttributes().get(0).getAttributeKey());
        assertEquals(transformationAttribute1.getAttributeName(), responseEntityTransformationBeforeUpdate.getAttributes().get(0).getAttributeName());
        assertEquals(transformationAttribute1.getType(), responseEntityTransformationBeforeUpdate.getAttributes().get(0).getType());

        Optional<TransformationAttributeDTO> transformationAttributeDTO2 = responseEntityTransformationAfterUpdate.getAttributes().stream().filter(tr2 -> tr2.getAttributeKey().equals("attr3")).findFirst();
        TransformationAttributeDTO transformationAttribute2 =  transformationAttributeDTO2.get();
        assertEquals(transformationAttribute2.getAttributeKey(), responseEntityTransformationBeforeUpdate.getAttributes().get(1).getAttributeKey());
        assertEquals(transformationAttribute2.getAttributeName(), responseEntityTransformationBeforeUpdate.getAttributes().get(1).getAttributeName());
        assertEquals(transformationAttribute2.getType(), responseEntityTransformationBeforeUpdate.getAttributes().get(1).getType());

        List<ElementData> repoTransformations = getElementsForLogicalEntity(TRANSFORMATION_ELEMENT_TYPE);
        Assert.assertFalse(repoTransformations.isEmpty());
        Optional<ElementData> transformation = repoTransformations.stream()
                .filter(elementData -> elementData.getElementContent().contains(String.format("key\" : \"%s", responseEntityTransformationAfterUpdate.getTransformations().get(0).getStoreName()))).findAny();
        assertTrue(transformation.isPresent());
    }

    @Test
    void T03_whenUpdateCachedEntityTransformation_WithNotExistingCacheEntity_Return404() {
        EntityTransformationDTO dto = generateEntityTransformation(NOT_EXISTING_KEY);
        assertGetResponsePut(
                configurationServiceUrl + configServiceAdditionalUrl + "/cached-entity-transformation",
                HttpStatus.SC_NOT_FOUND, dto);
    }

    @Test
    void T04_whenGetCachedEntityTransformation_Return200() {
        String cacheEntityKey = CACHED_ENTITY_TRANSFORMATION1;
        final Response response = assertGetResponseGet(
                configurationServiceUrl + configServiceAdditionalUrl + "/cached-entity-transformation" + "/" + cacheEntityKey, HttpStatus.SC_OK);
        EntityTransformationDTO entityTransformationDTO = response
                .as(EntityTransformationDTO.class);

        assertNotNull(entityTransformationDTO);
        assertNotNull(entityTransformationDTO.getAttributes());
        assertNotNull(entityTransformationDTO.getTransformations());

        EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);

        assertEquals(responseEntityTransformationDTO.getLogicalEntityKey(), cacheEntityKey);
        assertEquals(responseEntityTransformationDTO.getEntityName(), "cached entity");
        assertEquals(responseEntityTransformationDTO.getDescription(), "description");
        assertEquals(responseEntityTransformationDTO.getLogicalSchemaKey(), CACHE);
        assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getTargetSchemaName(), CACHE);
        assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getStoreName(), "CACHE-cachedEntity1-REFContext");
        assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getId(), "CACHE-cachedEntity1-REFContext");
        assertEquals(responseEntityTransformationDTO.getTransformations().size(), 1 );

        assertEquals(responseEntityTransformationDTO.getAttributes().size(), 2 );
        Optional<TransformationAttributeDTO> transformationAttributeDTO1 = responseEntityTransformationDTO.getAttributes().stream().filter(transformationAttributeDTO -> transformationAttributeDTO.getAttributeKey().equals("attr1")).findFirst();
        TransformationAttributeDTO transformationAttribute1 =  transformationAttributeDTO1.get();
        assertEquals(transformationAttribute1.getAttributeKey(), "attr1");
        assertEquals(transformationAttribute1.getAttributeName(), "Attribute 1");
        assertEquals(transformationAttribute1.getType(), "STRING");

        Optional<TransformationAttributeDTO> transformationAttributeDTO2 = responseEntityTransformationDTO.getAttributes().stream().filter(transformationAttributeDTO -> transformationAttributeDTO.getAttributeKey().equals("attr2")).findFirst();
        TransformationAttributeDTO transformationAttribute2 =  transformationAttributeDTO2.get();
        assertEquals(transformationAttribute2.getAttributeKey(), "attr2");
        assertEquals(transformationAttribute2.getAttributeName(),"Attribute 2");
        assertEquals(transformationAttribute2.getType(), "STRING");

        //check referenceSourceEntities
        Optional<TransformationContextEntityDTO> transformationContextEntityDTO1 = responseEntityTransformationDTO.getTransformations().get(0).getReferenceSourceEntities().stream().filter(transformationContextEntityDTO -> transformationContextEntityDTO.getEntityStoreKey().equals("MTM_BUS_ORG95_CUSTOMER1")).findFirst();
        TransformationContextEntityDTO transformationContextEntity1 =  transformationContextEntityDTO1.get();
        assertEquals(transformationContextEntity1.getEntityStoreKey(), "MTM_BUS_ORG95_CUSTOMER1");
        assertEquals(transformationContextEntity1.getSchemaStoreKey(), "CRM");
    }

    @Test
    void T05_whenGetCachedEntityTransformation_WithNotExistingCacheEntity_Return404() {
        assertGetResponseGet(
                configurationServiceUrl + configServiceAdditionalUrl
                        + "/cached-entity-transformation" + "/" + NOT_EXISTING_KEY, HttpStatus.SC_NOT_FOUND);
    }

   @Ignore
   @Test
    void T06_whenDeleteCachedEntityTransformation_Return200() {
        //delete the transformation
        assertGetResponseDelete(configurationServiceUrl + configServiceAdditionalUrl + "/transformations"+ "/" + "CACHE-cachedEntity1-REFContext",
                HttpStatus.SC_OK);

        //delete the cache entity
        assertGetResponseDelete(configurationServiceUrl + configServiceAdditionalUrl + "/cached-entity-transformation"+ "/" + CACHED_ENTITY_TRANSFORMATION1,
                HttpStatus.SC_OK);

        //Check that the transformation does not exist
        assertGetResponseGet(
                configurationServiceUrl + configServiceAdditionalUrl
                        + "transformations" + "/" + "CACHE-cachedEntity1-REFContext", HttpStatus.SC_NOT_FOUND);

        //Check that the cached entity does not exist
        assertGetResponseGet(
                configurationServiceUrl + configServiceAdditionalUrl
                        + "/cached-entity-transformation" + "/" + CACHED_ENTITY_TRANSFORMATION1, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void T07_whenDeleteCachedEntityTransformation_WithNotExistingCacheEntity_Return404() {
        assertGetResponseDelete(
                configurationServiceUrl + configServiceAdditionalUrl
                        + "/cached-entity-transformation" + "/" + NOT_EXISTING_KEY, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void T08_whenCreateCachedEntityTransformation_InvalidCachedEntityKey_Return400() {
        EntityTransformationDTO dto = generateEntityTransformation(CACHED_ENTITY_TRANSFORMATION1);
        dto.setLogicalEntityKey("cached entity invalid key");
        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/cached-entity-transformation",
                HttpStatus.SC_BAD_REQUEST, dto);
    }

    @Test
    void T09_whenCreateCachedEntityTransformation_MultipleTransformation_Return400() {
        EntityTransformationDTO dto = generateEntityTransformation(CACHED_ENTITY_TRANSFORMATION1);
        TransformationDTO transformation =  (TransformationDTO)new TransformationDTO()
                .productKey("INTEGRATIONLAYER")
                .referenceSourceEntities(Collections.singletonList(new TransformationContextEntityDTO().schemaStoreKey("CRM").entityStoreKey("MTM_BUS_ORG95_CUSTOMER1")))
                .isPublished(false)
                .contextKey("REFContext")
                .customScript("select T1.CID as CID,\nMAX(T1.PCVERSION_ID)as PCVERSION_ID \nfrom TBCATALOG_ITEM T1 \ngroup by T1.CID;")
                .implementationType(TransformationDTO.ImplementationTypeEnum.SQL)
                .sourceType(TransformationDTO.SourceTypeEnum.REFERENCE)
                .targetSchemaName(CACHE)
                .targetEntityStoreKey(CACHED_ENTITY_TRANSFORMATION1)
                .targetSchemaStoreKey(CACHE)
                .originProcess(OriginProcess.UI.name())
                .storeName(null)
                .modelType(CommonModelDTO.ModelTypeEnum.TRANSFORMATION)
                .projectKey(PROJECT_KEY);
        dto.addTransformationsItem(transformation);

        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/cached-entity-transformation",
                HttpStatus.SC_BAD_REQUEST, dto);
    }

    @Test
    void T10_whenUpdateCachedEntityTransformation_MultipleTransformation_Return400() {
        EntityTransformationDTO dto = generateEntityTransformation(CACHED_ENTITY_TRANSFORMATION1);
        TransformationDTO transformation =  (TransformationDTO)new TransformationDTO()
                .productKey("INTEGRATIONLAYER")
                .referenceSourceEntities(Collections.singletonList(new TransformationContextEntityDTO().schemaStoreKey("CRM").entityStoreKey("MTM_BUS_ORG95_CUSTOMER1")))
                .isPublished(false)
                .contextKey("REFContext")
                .customScript("select T1.CID as CID,\nMAX(T1.PCVERSION_ID)as PCVERSION_ID \nfrom TBCATALOG_ITEM T1 \ngroup by T1.CID;")
                .implementationType(TransformationDTO.ImplementationTypeEnum.SQL)
                .sourceType(TransformationDTO.SourceTypeEnum.REFERENCE)
                .targetSchemaName(CACHE)
                .targetEntityStoreKey(CACHED_ENTITY_TRANSFORMATION1)
                .targetSchemaStoreKey(CACHE)
                .originProcess(OriginProcess.UI.name())
                .storeName(null)
                .modelType(CommonModelDTO.ModelTypeEnum.TRANSFORMATION)
                .projectKey(PROJECT_KEY);
        dto.addTransformationsItem(transformation);

        assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/cached-entity-transformation",
                HttpStatus.SC_BAD_REQUEST, dto);
    }

    @Test
    void T11_whenCreateCachedEntityTransformation_DuplicateAttributeKeys_Return400() {
        EntityTransformationDTO dto = generateEntityTransformation(CACHED_ENTITY_TRANSFORMATION1);

        TransformationAttributeDTO transformationAttribute = new TransformationAttributeDTO()
                        .attributeKey("attr1")
                        .attributeName("Attribute 1111")
                        .type("STRING")
                        .keyPosition(false)
                        .sortOrder(1000)
                        .isLogicalTime(false)
                        .isUpdateTime(false)
                        .isRequired(false)
                        .doReferencialIntegrity(false);

        dto.getAttributes().add(transformationAttribute);

       assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/cached-entity-transformation",
               HttpStatus.SC_BAD_REQUEST, dto);
    }

    @Test
    void T12_whenCreateCachedEntityTransformation_DuplicateReferenceSourceEntities_Return400() {
        EntityTransformationDTO dto = generateEntityTransformation(CACHED_ENTITY_TRANSFORMATION1);

        TransformationContextEntityDTO transformationContextEntityDTO = new TransformationContextEntityDTO().schemaStoreKey("CRM").entityStoreKey("MTM_BUS_ORG95_CUSTOMER1");
        dto.getTransformations().get(0).getReferenceSourceEntities().add(transformationContextEntityDTO);

        assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/cached-entity-transformation",
               HttpStatus.SC_BAD_REQUEST, dto);

        commitChangeRequest();

    }

    private List<ElementData> getElementsForLogicalEntity(final String elementType, String ... additionalProperties) {
        Response response = searchByProperties(elementType, additionalProperties);
        return Arrays.asList(response.as(ElementData[].class));
    }

    private Response searchByProperties(final String elementType, String ... additionalProperties) {
        return assertGetResponsePost(aiaRepoURL + "elements/searchByProperties",
                HttpStatus.SC_OK, generatePropertiesMap(elementType, additionalProperties));
    }

    private static Map<String, String> generatePropertiesMap(final String elementType, String ... additionalProperties) {
        Map<String, String> properties = new HashMap<>();
        properties.put("projectKey", PROJECT_KEY);
        properties.put("productKey", PRODUCT_KEY);
        properties.put("elementType", elementType);
        for(int i = 0; i < additionalProperties.length; ++i) {
            properties.put(additionalProperties[i], additionalProperties[++i]);
        }
        return properties;
    }

    private static EntityTransformationDTO generateEntityTransformation(String cacheEntityKey) {
        List<TransformationContextEntityDTO> referenceSourceEntities = new ArrayList<>();
        TransformationContextEntityDTO transformationContextEntityDTO = new TransformationContextEntityDTO().schemaStoreKey("CRM").entityStoreKey("MTM_BUS_ORG95_CUSTOMER1");
        referenceSourceEntities.add(transformationContextEntityDTO);

        return new EntityTransformationDTO()
                .logicalEntityKey(cacheEntityKey)
                .logicalSchemaKey(CACHE)
                .entityName("cached entity")
                .description("description")

                .addTransformationsItem((TransformationDTO) new TransformationDTO()
                        .productKey("INTEGRATIONLAYER")
                        .referenceSourceEntities(referenceSourceEntities)
                        .isPublished(false)
                        .contextKey("REFContext")
                        .customScript("select T1.CID as CID,\nMAX(T1.PCVERSION_ID)as PCVERSION_ID \nfrom TBCATALOG_ITEM T1 \ngroup by T1.CID;")
                        .implementationType(TransformationDTO.ImplementationTypeEnum.SQL)
                        .sourceType(TransformationDTO.SourceTypeEnum.REFERENCE)
                        .targetSchemaName(CACHE)
                        .targetEntityStoreKey(cacheEntityKey)
                        .targetSchemaStoreKey(CACHE)
                        .originProcess(OriginProcess.UI.name())
                        .storeName(null)
                        .modelType(CommonModelDTO.ModelTypeEnum.TRANSFORMATION)
                        .projectKey(PROJECT_KEY)
                ).addAttributesItem(new TransformationAttributeDTO()
                        .attributeKey("attr1")
                        .attributeName("Attribute 1")
                        .type("STRING")
                        .keyPosition(false)
                        .sortOrder(1000)
                        .isLogicalTime(false)
                        .isUpdateTime(false)
                        .isRequired(false)
                        .doReferencialIntegrity(false)
                ).addAttributesItem(new TransformationAttributeDTO()
                        .attributeKey("attr2")
                        .attributeName("Attribute 2")
                        .type("STRING")
                        .keyPosition(true)
                        .sortOrder(2000)
                        .isLogicalTime(false)
                        .isUpdateTime(false)
                        .isRequired(true)
                        .doReferencialIntegrity(false));
    }
}
