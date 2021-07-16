package com.amdocs.aia.il.integration.configuration;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.common.model.repo.ElementData;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.il.configuration.client.dto.*;
import com.amdocs.aia.il.configuration.client.dto.CommonModelDTO.ModelTypeEnum;
import com.amdocs.aia.il.integration.BaseIntegrationTest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.*;
import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

public class ITSharedEntityTransformationServiceTest extends BaseIntegrationTest {
	private static final String ERI_ELEMENT_TYPE = "EntityReferentialIntegrity";
	private static final String TRANSFORMATION_ELEMENT_TYPE = "Transformation";
	private static final String CONTEXT_ELEMENT_TYPE = "Context";
	private static final String PRODUCT_KEY = "INTEGRATIONLAYER";
	private static final String NOT_EXISTING_KEY = "notExistingKey";
	private static final String ALDM_CUSTOMER = "aLDMCustomer";
	private static final String CUSTOMER = "Customer";
	private static final String ADDRESS = "Address";
	public static final String QUEUE = "Queue";
	public static final String ALDM_CUSTOMER_SERVICE = "aLDMCustomerService";
	private static final String AIA_SHARED_SERVICES_URL = "%s://%s/aia/api/v1/shrd";
	private static final String SHARED_SERVICES_APP_LABEL = "SHARED_SERVICES_APP_LABEL";

	private EntityTransformationDTO masterDTO;
	private EntityTransformationDTO referenceDTO;

	@BeforeClass
	public void setUpConf() {
		createChangeRequest("IL Shared Entity Transformation - Test");
		masterDTO = generateEntityTransformationForMasterEntity();
		referenceDTO = generateEntityTransformationForReferenceEntity();
		sharedConfigurationURL = getUrl(SHARED_SERVICES_APP_LABEL, AIA_SHARED_SERVICES_URL, getPortByName(prop.getProperty(SHARED_SERVICES_APP_LABEL), "home"));
	}

    @Override
    protected File getConfigurationZip(){
		return new File("src/test/resources/data/TransformationConfig.zip");
    }

	@Test
	void T00_whenCreateCachedEntityTransformation_shouldCreateCachedEntityTransformation() {
		EntityTransformationDTO dto = generateEntityTransformationForCacheEntity("cachedEntity");
		Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/cached-entity-transformation",
				HttpStatus.SC_CREATED, dto);
		EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);

		assertEquals(responseEntityTransformationDTO.getLogicalEntityKey(), dto.getLogicalEntityKey());
		assertEquals(responseEntityTransformationDTO.getLogicalSchemaKey(), dto.getLogicalSchemaKey());
		assertEquals(responseEntityTransformationDTO.getEntityName(), dto.getEntityName());
		assertEquals(responseEntityTransformationDTO.getDescription(), dto.getDescription());
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getTargetSchemaName(), dto.getTransformations().get(0).getTargetSchemaName());
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getStoreName(), "CACHE-cachedEntity-REFContext");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getId(), "CACHE-cachedEntity-REFContext");

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
		Assert.assertFalse(repoTransformations.isEmpty());
		Optional<ElementData> transformation = repoTransformations.stream()
				.filter(elementData -> elementData.getElementContent().contains(String.format("key\" : \"%s", responseEntityTransformationDTO.getTransformations().get(0).getStoreName()))).findAny();
		assertTrue(transformation.isPresent());
	}

	@Test
	void T01A_whenGetSharedEntityTransformation_FromMigration_Return200() {
		final Response response = assertGetResponseGet(
			configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation" + "/" + ADDRESS, HttpStatus.SC_OK);
		EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);

		assertNotNull(responseEntityTransformationDTO);
		assertNotNull(responseEntityTransformationDTO.getContexts());
		assertNotNull(responseEntityTransformationDTO.getTransformations());

		assertEquals(responseEntityTransformationDTO.getLogicalEntityKey(), ADDRESS);
		assertEquals(responseEntityTransformationDTO.getLogicalSchemaKey(), ALDM_CUSTOMER);
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getTargetSchemaName(), "aLDMCustomer");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getStoreName(), "aLDMCustomerDataChannel-Address-AddressContext");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getContextKey(), "AddressContext");
		assertEquals(responseEntityTransformationDTO.getTransformations().size(), 2);

		assertEquals(responseEntityTransformationDTO.getContexts().get(0).getContextKey(), ADDRESS);
		assertEquals(responseEntityTransformationDTO.getContexts().get(0).getStoreName(), "AddressContext");
		Optional<ContextEntityDTO> contextEntityDTO = responseEntityTransformationDTO.getContexts().get(0).getContextEntities().stream().filter(contextEntityDTO1 -> contextEntityDTO1.getEntityStoreKey().equals("TABLE_ADDRESS")).findFirst();
		ContextEntityDTO contextEntityDTO1 =  contextEntityDTO.get();
		assertEquals(contextEntityDTO1.getAliasedSourceEntityKey(), "TA-TABLE_ADDRESS");
		assertTrue(contextEntityDTO1.isDoPropagation());
		assertNull(contextEntityDTO1.getForeignKeys());
		assertNull(contextEntityDTO1.getNoReferentAction());
		assertNull(contextEntityDTO1.getParentContextEntityKey());
		assertEquals(contextEntityDTO1.getRelationType().name(), ContextEntityDTO.RelationTypeEnum.LEAD.name());
		assertEquals(contextEntityDTO1.getSchemaStoreKey(), "CRMAPP");
		assertEquals(contextEntityDTO1.getSourceAlias(), "TA");

		assertEquals(responseEntityTransformationDTO.getAttributes().size(), 24);
		Optional<TransformationAttributeDTO> transformationAttributeDTO1 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("addressLine1")).findFirst();
		TransformationAttributeDTO transformationAttribute1 =  transformationAttributeDTO1.get();
		assertEquals(transformationAttribute1.getAttributeKey(), "addressLine1");
		assertEquals(transformationAttribute1.getAttributeName(), "Address Line 1");
		assertEquals(transformationAttribute1.getType(),"STRING");

		//check context in repository
		checkContextForAddressT01A();

		//check transformation in repository
		checkTransformationForAddressT01A();
	}

	@Test
	void T01B_whenGetSharedEntityTransformationList_FromMigration_CheckStatus_Return200() {
		final Response response = assertGetResponseGet(
				configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
				HttpStatus.SC_OK);
		SharedEntityTransformationGridElementDTO[] sharedEntityTransformationGridElementDTOS = response
				.as(SharedEntityTransformationGridElementDTO[].class);

		List<SharedEntityTransformationGridElementDTO> transformationEntitiesList = Arrays.asList(sharedEntityTransformationGridElementDTOS);

		SharedEntityTransformationGridElementDTO sharedEntityTransformationGridElementDTO = Arrays
				.stream(sharedEntityTransformationGridElementDTOS)
				.filter(t -> t.getEntityKey().equals(ADDRESS)).findFirst().orElse(null);
		assertNotNull(sharedEntityTransformationGridElementDTOS);
		assertEquals(transformationEntitiesList.size(), 7);
		assertEquals(sharedEntityTransformationGridElementDTO.getEntityName(), ADDRESS);
		assertEquals(sharedEntityTransformationGridElementDTO.getStatus(), ChangeStatusDTO.PUBLISHED);

		//check if there duplicate transformation entities
		Map<String, List<SharedEntityTransformationGridElementDTO>> entityByKey = transformationEntitiesList.stream().collect(Collectors.groupingBy(SharedEntityTransformationGridElementDTO::getEntityKey));
		String duplicateAttKeys = entityByKey.entrySet().stream().filter(stringEntry -> stringEntry.getValue().size() > 1).map(Map.Entry::getKey).collect(Collectors.joining(","));
		assertEquals(duplicateAttKeys.length(), 0);
	}

	@Test
	void T02_whenUpdateSharedEntityTransformation_FromMigration_Return200() {
		Response response = assertGetResponseGet(
				configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation" + "/" + ADDRESS, HttpStatus.SC_OK);
		EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);

		ContextDTO contextDTO = (ContextDTO) new ContextDTO()
				.contextKey("AddressCtx2")
				.contextEntities(generateContextEntities())
				.originProcess(OriginProcess.UI.name())
				.storeName("AddressCtx2")
				.modelType(ModelTypeEnum.CONTEXT);

		TransformationDTO transformationDTO = ((TransformationDTO) new TransformationDTO()
				.productKey("INTEGRATIONLAYER")
				.isPublished(true)
				.contextKey("AddressCtx2")
				.customScript("select T1.CID as CID,\nMAX(T1.PCVERSION_ID)as PCVERSION_ID \nfrom TBCATALOG_ITEM T1 \ngroup by T1.CID;")
				.implementationType(TransformationDTO.ImplementationTypeEnum.SQL)
				.sourceType(TransformationDTO.SourceTypeEnum.CONTEXT)
				.targetEntityStoreKey(ADDRESS)
				.targetSchemaStoreKey(ALDM_CUSTOMER + "DataChannel")
				.targetSchemaName(ALDM_CUSTOMER)
				.status(ChangeStatusDTO.DRAFT)
				.originProcess(OriginProcess.UI.name())
				.modelType(ModelTypeEnum.TRANSFORMATION)
				.projectKey(PROJECT_KEY));

		responseEntityTransformationDTO.getContexts().add(contextDTO);

		responseEntityTransformationDTO.getAttributes().stream()
				.filter(transformationAttributeDTO -> "stateKey".equals(transformationAttributeDTO.getAttributeKey()))
				.forEach(transformationAttributeDTO -> {
					transformationAttributeDTO.doReferencialIntegrity(true);
					transformationAttributeDTO.setParentEntityKey("Country");
					transformationAttributeDTO.setParentSchemaKey(ALDM_CUSTOMER);
					transformationAttributeDTO.setParentAttributeKey("countryKey");
				});

		TransformationAttributeDTO  attribute = (new TransformationAttributeDTO()
				.attributeKey("electronicAccessNum")
				.attributeName("Electronic Access Num")
				.type("STRING")
				.keyPosition(false)
				.sortOrder(25000)
				.isLogicalTime(false)
				.isUpdateTime(false)
				.isRequired(true)
				.doReferencialIntegrity(true)
				.parentEntityKey("Country")
				.parentSchemaKey(ALDM_CUSTOMER)
				.parentAttributeKey("countryKey"));

		responseEntityTransformationDTO.getTransformations().add(transformationDTO);
		responseEntityTransformationDTO.getAttributes().add(attribute);

		response = assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
				HttpStatus.SC_OK, responseEntityTransformationDTO);
		EntityTransformationDTO responseEntityTransformationDTOModified = response.as(EntityTransformationDTO.class);

		//check entityReferentialIntegrity in repository
		Optional<ElementData> entityReferentialIntegrity = getEntityReferentialIntegrityFromRepo(responseEntityTransformationDTOModified.getLogicalEntityKey(), responseEntityTransformationDTOModified.getLogicalSchemaKey());
		assertTrue(entityReferentialIntegrity.isPresent());

		List<ElementData> repoContexts = getElementsForLogicalEntity(CONTEXT_ELEMENT_TYPE);
		assertFalse(repoContexts.isEmpty());
		Optional<ElementData> ctx1 = repoContexts.stream()
				.filter(elementData -> elementData.getElementContent().contains(String.format("contextKey\" : \"%s", responseEntityTransformationDTOModified.getContexts().get(0).getContextKey()))).findAny();
		assertTrue(ctx1.isPresent());

		//check Context in repository
		checkContextForAddressT02();

		List<ElementData> repoTransformations = getElementsForLogicalEntity(TRANSFORMATION_ELEMENT_TYPE);
		assertFalse(repoTransformations.isEmpty());
		Optional<ElementData> transformation = repoTransformations.stream()
				.filter(elementData -> elementData.getElementContent().contains(String.format("key\" : \"%s", responseEntityTransformationDTOModified.getTransformations().get(0).getStoreName()))).findAny();
		assertTrue(transformation.isPresent());

		//check Transformation in repository
		checkTransformationForAddressT02();

		checkAddressEntityT02(responseEntityTransformationDTO, responseEntityTransformationDTOModified);
	}

	@Test
	void T03_whenGetSharedEntityTransformation_FromMigration_WithNotExistingEntities_Return404() {
		assertGetResponseGet(
			configurationServiceUrl + configServiceAdditionalUrl
				+ "/shared-entity-transformation" + "/" + NOT_EXISTING_KEY, HttpStatus.SC_NOT_FOUND);
	}

	@Test
	void T04_whenCreateSharedEntityTransformation_FromMigration_WithNotExistingEntities_Return404() {
		EntityTransformationDTO dto = new EntityTransformationDTO()
			.logicalEntityKey(NOT_EXISTING_KEY)
			.logicalSchemaKey(NOT_EXISTING_KEY);
		assertGetResponsePost(
			configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
			HttpStatus.SC_NOT_FOUND, dto);
	}

	@Test
	void T05_whenGetSharedEntityTransformationList_FromMigration_Return200() {
		final Response response = assertGetResponseGet(
				configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
				HttpStatus.SC_OK);
		SharedEntityTransformationGridElementDTO[] sharedEntityTransformationGridElementDTOS = response
				.as(SharedEntityTransformationGridElementDTO[].class);

		List<SharedEntityTransformationGridElementDTO> transformationEntitiesList = Arrays.asList(sharedEntityTransformationGridElementDTOS);

		SharedEntityTransformationGridElementDTO sharedEntityTransformationGridElementDTO = Arrays
				.stream(sharedEntityTransformationGridElementDTOS)
				.filter(t -> t.getEntityKey().equals(ADDRESS)).findFirst().orElse(null);
		assertNotNull(sharedEntityTransformationGridElementDTOS);
		assertEquals(transformationEntitiesList.size(), 7);
		assertEquals(sharedEntityTransformationGridElementDTO.getEntityName(), ADDRESS);
		assertEquals(sharedEntityTransformationGridElementDTO.getStatus(), ChangeStatusDTO.DRAFT);

		//check if there duplicate transformation entities
		Map<String, List<SharedEntityTransformationGridElementDTO>> entityByKey = transformationEntitiesList.stream().collect(Collectors.groupingBy(SharedEntityTransformationGridElementDTO::getEntityKey));
		String duplicateAttKeys = entityByKey.entrySet().stream().filter(stringEntry -> stringEntry.getValue().size() > 1).map(Map.Entry::getKey).collect(Collectors.joining(","));
		assertEquals(duplicateAttKeys.length(), 0);
	}

	@Test
	void T06_whenDeleteSharedEntityTransformation_FromMigration_Return200() {
		assertGetResponseDelete(configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation" + "/" +  ADDRESS,
				HttpStatus.SC_OK);

		Response response  = assertGetResponseGet(
				configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation" + "/" + ADDRESS, HttpStatus.SC_OK);
		EntityTransformationDTO entityTransformationDTO = response
				.as(EntityTransformationDTO.class);
		assertNotNull(entityTransformationDTO);

		EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);

		assertEquals(responseEntityTransformationDTO.getTransformations(),Collections.emptyList());
		assertEquals(responseEntityTransformationDTO.getContexts(), Collections.emptyList());
		assertEquals(responseEntityTransformationDTO.getLogicalEntityKey(), ADDRESS);
		assertEquals(responseEntityTransformationDTO.getLogicalSchemaKey(), ALDM_CUSTOMER);

		Optional<TransformationAttributeDTO> transformationAttributeDTO1 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("zipCode")).findFirst();
		TransformationAttributeDTO transformationAttribute1 =  transformationAttributeDTO1.get();
		assertEquals(transformationAttribute1.getAttributeKey(), "zipCode");
		assertEquals(transformationAttribute1.getAttributeName(), "Zip Code");
		assertEquals(transformationAttribute1.getType(),"STRING");
	}

	@Test
	void T07_whenGetSharedEntityTransformationList_FromMigration_Return200() {
		final Response response = assertGetResponseGet(
				configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
				HttpStatus.SC_OK);
		SharedEntityTransformationGridElementDTO[] sharedEntityTransformationGridElementDTOS = response
				.as(SharedEntityTransformationGridElementDTO[].class);

		List<SharedEntityTransformationGridElementDTO> transformationEntitiesList = Arrays.asList(sharedEntityTransformationGridElementDTOS);

		SharedEntityTransformationGridElementDTO sharedEntityTransformationGridElementDTO = Arrays
				.stream(sharedEntityTransformationGridElementDTOS)
				.filter(t -> t.getEntityKey().equals(ADDRESS)).findFirst().orElse(null);
		assertNotNull(sharedEntityTransformationGridElementDTOS);
		assertEquals(transformationEntitiesList.size(), 6);
		assertNull(sharedEntityTransformationGridElementDTO);

		//check if there duplicate transformation entities
		Map<String, List<SharedEntityTransformationGridElementDTO>> entityByKey = transformationEntitiesList.stream().collect(Collectors.groupingBy(SharedEntityTransformationGridElementDTO::getEntityKey));
		String duplicateAttKeys = entityByKey.entrySet().stream().filter(stringEntry -> stringEntry.getValue().size() > 1).map(Map.Entry::getKey).collect(Collectors.joining(","));
		assertEquals(duplicateAttKeys.length(), 0);
	}

	@Test
	void T08_whenCreateSharedEntityTransformation_MasterEntityFromScratch_Return200() {
		final Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
			HttpStatus.SC_CREATED, masterDTO);

		EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);

		//check entityReferentialIntegrity in repository
		Optional<ElementData> entityReferentialIntegrity = getEntityReferentialIntegrityFromRepo(responseEntityTransformationDTO.getLogicalEntityKey(), responseEntityTransformationDTO.getLogicalSchemaKey());
		assertTrue(entityReferentialIntegrity.isPresent());

		List<ElementData> repoContexts = getElementsForLogicalEntity(CONTEXT_ELEMENT_TYPE);
		Assert.assertFalse(repoContexts.isEmpty());
		Optional<ElementData> ctx1 = repoContexts.stream()
				.filter(elementData -> elementData.getElementContent().contains(String.format("contextKey\" : \"%s", responseEntityTransformationDTO.getContexts().get(0).getContextKey()))).findAny();
		assertTrue(ctx1.isPresent());

		//check context in repository
		checkContextForCustomerT08();

		List<ElementData> repoTransformations = getElementsForLogicalEntity(TRANSFORMATION_ELEMENT_TYPE);
		Assert.assertFalse(repoTransformations.isEmpty());
		Optional<ElementData> transformation = repoTransformations.stream()
			.filter(elementData -> elementData.getElementContent().contains(String.format("key\" : \"%s", responseEntityTransformationDTO.getTransformations().get(0).getStoreName()))).findAny();
		assertTrue(transformation.isPresent());

		//check transformation in repository
		checkTransformationForCustomerT08();

		checkCustomerEntityT08(responseEntityTransformationDTO);

		//check if entity store for customer created
		final Response responseES = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemastores/aLDMCustomerDataChannel/entitystores/SHARED_aia_ENS_DATA_CHANNEL_aLDMCustomerDataChannel_Customer", HttpStatus.SC_OK);
		EntityStore entityStore = responseES.as(EntityStore.class);
		assertEquals(entityStore.getSchemaStoreKey(),"aLDMCustomerDataChannel");
		assertEquals(entityStore.getEntityStoreKey(),"Customer");

		//check if schema store for customer created
		Response responseSchema = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemastores/SHARED_aia_SCS_DATA_CHANNEL_aLDMCustomerDataChannel", HttpStatus.SC_OK);
		SchemaStore schemaStore = responseSchema.as(SchemaStore.class);
		assertEquals(schemaStore.getSchemaStoreKey(),"aLDMCustomerDataChannel");
		assertEquals(schemaStore.getLogicalSchemaKey(),"aLDMCustomer");
	}

	@Test
	void T09_whenUpdateSharedEntityTransformation_MasterEntityFromScratch_Return200() {
		List<ContextEntityDTO> contextEntities = new ArrayList<>();
		ContextEntityDTO contextEntity1 = new ContextEntityDTO().schemaStoreKey("CRMAPP").entityStoreKey("TABLE_CUSTOMER").sourceAlias("CUST").relationType(ContextEntityDTO.RelationTypeEnum.LEAD).doPropagation(true).parentContextEntityKey(null).foreignKeys(null).noReferentAction(null).aliasedSourceEntityKey("CUST-TABLE_CUSTOMER");
		ContextEntityDTO contextEntity2 = new ContextEntityDTO().schemaStoreKey("CRMAPP").entityStoreKey("MTM_CONTACT56_CUSTOMER0").sourceAlias("MCC").relationType(ContextEntityDTO.RelationTypeEnum.OTM).doPropagation(true).parentContextEntityKey("CUST-TABLE_CUSTOMER").foreignKeys("CUSTOMER2CONTACT").noReferentAction(ContextEntityDTO.NoReferentActionEnum.OPTIONAL).aliasedSourceEntityKey("MCC-MTM_CONTACT56_CUSTOMER0");
		contextEntities.add(contextEntity1);
		contextEntities.add(contextEntity2);

		ContextDTO contextDTO = (ContextDTO) new ContextDTO()
				.contextKey("CustomerCtx2")
				.contextEntities(contextEntities)
				.originProcess(OriginProcess.UI.name())
				.storeName("CustomerCtx2")
				.modelType(ModelTypeEnum.CONTEXT);

		TransformationDTO transformationDTO1 = ((TransformationDTO) new TransformationDTO()
				.productKey("INTEGRATIONLAYER")
				.isPublished(true)
				.contextKey("CustomerCtx2")
				.customScript("select T1.CID as CID,\nMAX(T1.PCVERSION_ID)as PCVERSION_ID \nfrom TBCATALOG_ITEM T1 \ngroup by T1.CID;")
				.implementationType(TransformationDTO.ImplementationTypeEnum.SQL)
				.sourceType(TransformationDTO.SourceTypeEnum.CONTEXT)
				.targetEntityStoreKey(CUSTOMER)
				.targetSchemaStoreKey(ALDM_CUSTOMER + "DataChannel")
				.targetSchemaName(ALDM_CUSTOMER)
				.status(ChangeStatusDTO.DRAFT)
				.originProcess(OriginProcess.UI.name())
				.modelType(ModelTypeEnum.TRANSFORMATION)
				.projectKey(PROJECT_KEY));

		//transformation based on exist context source
		TransformationDTO transformationDTO2 = ((TransformationDTO) new TransformationDTO()
				.productKey("INTEGRATIONLAYER")
				.isPublished(true)
				.contextKey("EaddressContext")
				.customScript("select T1.CID as CID,\nMAX(T1.PCVERSION_ID)as PCVERSION_ID \nfrom TBCATALOG_ITEM T1 \ngroup by T1.CID;")
				.implementationType(TransformationDTO.ImplementationTypeEnum.SQL)
				.sourceType(TransformationDTO.SourceTypeEnum.CONTEXT)
				.targetEntityStoreKey(CUSTOMER)
				.targetSchemaStoreKey(ALDM_CUSTOMER + "DataChannel")
				.targetSchemaName(ALDM_CUSTOMER)
				.status(ChangeStatusDTO.DRAFT)
				.originProcess(OriginProcess.UI.name())
				.modelType(ModelTypeEnum.TRANSFORMATION)
				.projectKey(PROJECT_KEY));

		masterDTO.getContexts().add(contextDTO);

		TransformationAttributeDTO  attribute = (new TransformationAttributeDTO()
				.attributeKey("countryKey")
				.attributeName("Country Key")
				.type("STRING")
				.keyPosition(false)
				.sortOrder(17000)
				.isLogicalTime(false)
				.isUpdateTime(false)
				.isRequired(true)
				.doReferencialIntegrity(true)
				.parentEntityKey("Country")
				.parentSchemaKey(ALDM_CUSTOMER)
				.parentAttributeKey("countryKey"));

		masterDTO.getTransformations().add(transformationDTO1);
		masterDTO.getTransformations().add(transformationDTO2);
		masterDTO.getAttributes().add(attribute);

		Response response = assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
				HttpStatus.SC_OK, masterDTO);
		EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);

		//check entityReferentialIntegrity in repository
		Optional<ElementData> entityReferentialIntegrity = getEntityReferentialIntegrityFromRepo(responseEntityTransformationDTO.getLogicalEntityKey(), responseEntityTransformationDTO.getLogicalSchemaKey());
		assertTrue(entityReferentialIntegrity.isPresent());

		List<ElementData> repoContexts = getElementsForLogicalEntity(CONTEXT_ELEMENT_TYPE);
		Assert.assertFalse(repoContexts.isEmpty());
		Optional<ElementData> ctx1 = repoContexts.stream()
				.filter(elementData -> elementData.getElementContent().contains(String.format("contextKey\" : \"%s", responseEntityTransformationDTO.getContexts().get(0).getContextKey()))).findAny();
		assertTrue(ctx1.isPresent());

		//check context in repository
		checkContextForCustomerT09();

		List<ElementData> repoTransformations = getElementsForLogicalEntity(TRANSFORMATION_ELEMENT_TYPE);
		Assert.assertFalse(repoTransformations.isEmpty());
		Optional<ElementData> transformation = repoTransformations.stream()
				.filter(elementData -> elementData.getElementContent().contains(String.format("key\" : \"%s", responseEntityTransformationDTO.getTransformations().get(0).getStoreName()))).findAny();
		assertTrue(transformation.isPresent());

		//check transformation in repository
		checkTransformationForCustomerT09();

		checkCustomerEntityT09(responseEntityTransformationDTO);
	}

	@Test
	void T10_whenGetSharedEntityTransformation_MasterEntityFromScratch_Return200() {
		final Response response = assertGetResponseGet(
				configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation" + "/" + CUSTOMER, HttpStatus.SC_OK);
		EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);

		assertNotNull(responseEntityTransformationDTO);
		assertEquals(responseEntityTransformationDTO.getLogicalEntityKey(), CUSTOMER);
		assertEquals(responseEntityTransformationDTO.getLogicalSchemaKey(), ALDM_CUSTOMER);

		checkCustomerEntityT10(responseEntityTransformationDTO);
	}

	@Test
	void T11_whenCreateSharedEntityTransformation_ReferenceEntityFromScratch_Return200() {
		final Response response = assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
				HttpStatus.SC_CREATED, referenceDTO);

		EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);
		assertEquals(responseEntityTransformationDTO.getContexts(), Collections.emptyList());
		assertEquals(responseEntityTransformationDTO.getLogicalEntityKey(), QUEUE);
		assertEquals(responseEntityTransformationDTO.getLogicalSchemaKey(), ALDM_CUSTOMER_SERVICE);

		List<ElementData> repoTransformations = getElementsForLogicalEntity(TRANSFORMATION_ELEMENT_TYPE);
		Assert.assertFalse(repoTransformations.isEmpty());
		Optional<ElementData> transformation = repoTransformations.stream()
				.filter(elementData -> elementData.getElementContent().contains(String.format("key\" : \"%s", responseEntityTransformationDTO.getTransformations().get(0).getStoreName()))).findAny();
		Assert.assertTrue(transformation.isPresent());

		//check transformation in repository
		checkTransformationForQueueT11();

		checkQueueEntityT11(responseEntityTransformationDTO);

		//check if entity store for queue had been created
		final Response responseES = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemastores/aLDMCustomerServiceDataChannel/entitystores/SHARED_aia_ENS_DATA_CHANNEL_aLDMCustomerServiceDataChannel_Queue", HttpStatus.SC_OK);
		EntityStore entityStore = responseES.as(EntityStore.class);
		assertEquals(entityStore.getSchemaStoreKey(),"aLDMCustomerServiceDataChannel");
		assertEquals(entityStore.getEntityStoreKey(),"Queue");

		//check if schema store for queue had been created
		Response responseSchema = assertGetResponseGet(sharedConfigurationURL + "/projects/aia/schemastores/SHARED_aia_SCS_DATA_CHANNEL_aLDMCustomerServiceDataChannel", HttpStatus.SC_OK);
		SchemaStore schemaStore = responseSchema.as(SchemaStore.class);
		assertEquals(schemaStore.getSchemaStoreKey(),"aLDMCustomerServiceDataChannel");
		assertEquals(schemaStore.getLogicalSchemaKey(),"aLDMCustomerService");
	}

	@Test
	void T12_whenUpdateSharedEntityTransformation_ReferenceEntityFromScratch_Return200() {
		TransformationAttributeDTO  attribute = (new TransformationAttributeDTO()
				.attributeKey("queueTitle")
				.attributeName("Queue title")
				.type("STRING")
				.keyPosition(false)
				.sortOrder(3000)
				.isLogicalTime(false)
				.isUpdateTime(false)
				.isRequired(true)
				.doReferencialIntegrity(false));

		referenceDTO.getAttributes().add(attribute);

		Response response = assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
				HttpStatus.SC_OK, referenceDTO);
		EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);
		assertEquals(responseEntityTransformationDTO.getContexts(), Collections.emptyList());

		List<ElementData> repoTransformations = getElementsForLogicalEntity(TRANSFORMATION_ELEMENT_TYPE);
		Assert.assertFalse(repoTransformations.isEmpty());
		Optional<ElementData> transformation = repoTransformations.stream()
				.filter(elementData -> elementData.getElementContent().contains(String.format("key\" : \"%s", responseEntityTransformationDTO.getTransformations().get(0).getStoreName()))).findAny();
		assertTrue(transformation.isPresent());

		//check transformation in repository
		checkTransformationForQueueT11();

		checkQueueEntityT12(responseEntityTransformationDTO);
	}

	@Test
	void T13_whenGetSharedEntityTransformation_ReferenceEntityFromScratch_Return200() {
		final Response response = assertGetResponseGet(
				configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation" + "/" + QUEUE, HttpStatus.SC_OK);
		EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);

		Assert.assertNotNull(responseEntityTransformationDTO);
		assertEquals(responseEntityTransformationDTO.getContexts(), Collections.emptyList());
		assertEquals(responseEntityTransformationDTO.getLogicalEntityKey(), QUEUE);
		assertEquals(responseEntityTransformationDTO.getLogicalSchemaKey(), ALDM_CUSTOMER_SERVICE);

		checkQueueEntityT13(responseEntityTransformationDTO);
	}

	@Test
	void T14_whenGetSharedEntityTransformationList_All_Return200() {
		final Response response = assertGetResponseGet(
				configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
				HttpStatus.SC_OK);
		SharedEntityTransformationGridElementDTO[] sharedEntityTransformationGridElementDTOS = response
				.as(SharedEntityTransformationGridElementDTO[].class);

		List<SharedEntityTransformationGridElementDTO> transformationEntitiesList = Arrays.asList(sharedEntityTransformationGridElementDTOS);

		SharedEntityTransformationGridElementDTO sharedEntityTransformationGridElementDTO1 = Arrays
				.stream(sharedEntityTransformationGridElementDTOS)
				.filter(t -> t.getEntityKey().equals(CUSTOMER)).findFirst().orElse(null);
		SharedEntityTransformationGridElementDTO sharedEntityTransformationGridElementDTO2 = Arrays
				.stream(sharedEntityTransformationGridElementDTOS)
				.filter(t -> t.getEntityKey().equals(QUEUE)).findFirst().orElse(null);
		assertNotNull(sharedEntityTransformationGridElementDTOS);
		assertNotNull(sharedEntityTransformationGridElementDTO1);
		assertNotNull(sharedEntityTransformationGridElementDTO2);
		assertEquals(transformationEntitiesList.size(), 8);

		//check if there duplicate transformation entities
		Map<String, List<SharedEntityTransformationGridElementDTO>> entityByKey = transformationEntitiesList.stream().collect(Collectors.groupingBy(SharedEntityTransformationGridElementDTO::getEntityKey));
		String duplicateAttKeys = entityByKey.entrySet().stream().filter(stringEntry -> stringEntry.getValue().size() > 1).map(Map.Entry::getKey).collect(Collectors.joining(","));
		assertEquals(duplicateAttKeys.length(), 0);
	}

	@Test
	void T15_whenUpdateSharedEntityTransformation_addCacheEntitySource_MasterEntityFromScratch_Return200() {
		List<ContextEntityDTO> contextEntities = new ArrayList<>();
		ContextEntityDTO contextEntity1 = new ContextEntityDTO().schemaStoreKey("CRMAPP").entityStoreKey("TABLE_CUSTOMER").sourceAlias("CUST").relationType(ContextEntityDTO.RelationTypeEnum.LEAD).doPropagation(true).parentContextEntityKey(null).foreignKeys(null).noReferentAction(null).aliasedSourceEntityKey("CUST-TABLE_CUSTOMER");
		ContextEntityDTO contextEntity2 = new ContextEntityDTO().schemaStoreKey("CACHE").entityStoreKey("cachedEntity").sourceAlias("CACHE").relationType(ContextEntityDTO.RelationTypeEnum.REF).doPropagation(true).parentContextEntityKey(null).foreignKeys(null).noReferentAction(null).aliasedSourceEntityKey("CACHE-TABLE");
		contextEntities.add(contextEntity1);
		contextEntities.add(contextEntity2);

		ContextDTO contextDTO = (ContextDTO) new ContextDTO()
				.contextKey("CustomerCtx3")
				.contextEntities(contextEntities)
				.originProcess(OriginProcess.UI.name())
				.storeName("CustomerCtx3")
				.modelType(ModelTypeEnum.CONTEXT);

		TransformationDTO transformationDTO = ((TransformationDTO) new TransformationDTO()
				.productKey("INTEGRATIONLAYER")
				.isPublished(true)
				.contextKey("CustomerCtx3")
				.customScript("select T1.CID as CID,\nMAX(T1.PCVERSION_ID)as PCVERSION_ID \nfrom TBCATALOG_ITEM T1 \ngroup by T1.CID;")
				.implementationType(TransformationDTO.ImplementationTypeEnum.SQL)
				.sourceType(TransformationDTO.SourceTypeEnum.CONTEXT)
				.targetEntityStoreKey(CUSTOMER)
				.targetSchemaStoreKey(ALDM_CUSTOMER + "DataChannel")
				.targetSchemaName(ALDM_CUSTOMER)
				.status(ChangeStatusDTO.DRAFT)
				.originProcess(OriginProcess.UI.name())
				.modelType(ModelTypeEnum.TRANSFORMATION)
				.projectKey(PROJECT_KEY));

		masterDTO.getContexts().add(contextDTO);
		masterDTO.getTransformations().add(transformationDTO);

		Response response = assertGetResponsePut(configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
				HttpStatus.SC_OK, masterDTO);
		EntityTransformationDTO responseEntityTransformationDTO = response.as(EntityTransformationDTO.class);

		//check entityReferentialIntegrity in repository
		Optional<ElementData> entityReferentialIntegrity = getEntityReferentialIntegrityFromRepo(responseEntityTransformationDTO.getLogicalEntityKey(), responseEntityTransformationDTO.getLogicalSchemaKey());
		assertTrue(entityReferentialIntegrity.isPresent());

		List<ElementData> repoContexts = getElementsForLogicalEntity(CONTEXT_ELEMENT_TYPE);
		Assert.assertFalse(repoContexts.isEmpty());
		Optional<ElementData> ctx1 = repoContexts.stream()
				.filter(elementData -> elementData.getElementContent().contains(String.format("contextKey\" : \"%s", responseEntityTransformationDTO.getContexts().get(0).getContextKey()))).findAny();
		assertTrue(ctx1.isPresent());

		//check context in repository
		checkContextForCustomerT15();

		List<ElementData> repoTransformations = getElementsForLogicalEntity(TRANSFORMATION_ELEMENT_TYPE);
		Assert.assertFalse(repoTransformations.isEmpty());
		Optional<ElementData> transformation = repoTransformations.stream()
				.filter(elementData -> elementData.getElementContent().contains(String.format("key\" : \"%s", responseEntityTransformationDTO.getTransformations().get(0).getStoreName()))).findAny();
		assertTrue(transformation.isPresent());

		//check transformation in repository
		checkTransformationForCustomerT15();

		checkCustomerEntityT15(responseEntityTransformationDTO);

		commitChangeRequest();
	}

	@Test
	void T16_whenDeleteCachedEntityUsedOnSharedTransformation_Return409() {
		createChangeRequest("DeleteCachedEntityUsedOnSharedTransformation");

		Response response = assertGetResponseDelete(
				configurationServiceUrl + configServiceAdditionalUrl
						+ "/cached-entity-transformation" + "/" + "cachedEntity", HttpStatus.SC_CONFLICT);

		assertEquals(response.getStatusCode(), 409);
		assertEquals(response.getBody().asString(), "{\n" +
				"  \"httpStatusCode\" : 409,\n" +
				"  \"internalErrorCode\" : 23,\n" +
				"  \"type\" : \"ERROR\",\n" +
				"  \"userMessageKey\" : \"general.cannot.delete.element\",\n" +
				"  \"userMessage\" : \"'cached entity' cannot be deleted due to the following issues:\\n\\ncached entity cannot be deleted since it is being used by REFContext -> cachedEntity\\ncached entity cannot be deleted since it is being used by CustomerCtx3\",\n" +
				"  \"detailedMessage\" : \"\",\n" +
				"  \"moreInfoLink\" : \"http://someServer/help?errorCode=23\",\n" +
				"  \"additionalData\" : null\n" +
				"}");
	}

	@Test
	void T17_whenCreateSharedEntityTransformation_AddContext_WithNoLeadContextEntity_Return404() {
		EntityTransformationDTO dto = new EntityTransformationDTO()
				.logicalEntityKey(ADDRESS)
				.logicalSchemaKey(ALDM_CUSTOMER)
				.addContextsItem((ContextDTO) new ContextDTO()
						.contextKey("AddressCtx1")
						.contextEntities(Collections.singletonList(new ContextEntityDTO().schemaStoreKey("CRMAPP").entityStoreKey("MTM_BUS_ORG95_CUSTOMER1").sourceAlias("MBO").relationType(ContextEntityDTO.RelationTypeEnum.OTM).doPropagation(true).parentContextEntityKey("CUST-TABLE_CUSTOMER").foreignKeys("CUSTOMER2BUS_ORG").noReferentAction(ContextEntityDTO.NoReferentActionEnum.OPTIONAL).aliasedSourceEntityKey("MBO-MTM_BUS_ORG95_CUSTOMER1")))
						.originProcess(OriginProcess.UI.name())
						.storeName("AddressCtx1")
						.modelType(ModelTypeEnum.CONTEXT)
				).addTransformationsItem((TransformationDTO) new TransformationDTO()
						.productKey("INTEGRATIONLAYER")
						.isPublished(true)
						.contextKey("AddressCtx1")
						.customScript("select T1.CID as CID,\nMAX(T1.PCVERSION_ID)as PCVERSION_ID \nfrom TBCATALOG_ITEM T1 \ngroup by T1.CID;")
						.implementationType(TransformationDTO.ImplementationTypeEnum.SQL)
						.sourceType(TransformationDTO.SourceTypeEnum.CONTEXT)
						.targetEntityStoreKey(ADDRESS)
						.targetSchemaStoreKey(ALDM_CUSTOMER + "DataChannel")
						.targetSchemaName(ALDM_CUSTOMER)
						.originProcess(OriginProcess.UI.name())
						.modelType(ModelTypeEnum.TRANSFORMATION)
						.projectKey(PROJECT_KEY)
				).addAttributesItem(new TransformationAttributeDTO()
						.attributeKey("addressKey")
						.attributeName("Address Key")
						.type("STRING")
						.keyPosition(true)
						.sortOrder(1000)
						.isLogicalTime(false)
						.isUpdateTime(false)
						.isRequired(true)
						.doReferencialIntegrity(false));

		assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
				HttpStatus.SC_NOT_FOUND, dto);
	}

	@Test
	void T18_whenCreateSharedEntityTransformation_AddContext_InvalidLeadContextEntity_Return4004() {
		EntityTransformationDTO dto = new EntityTransformationDTO()
				.logicalEntityKey(ADDRESS)
				.logicalSchemaKey(ALDM_CUSTOMER)
				.addContextsItem((ContextDTO) new ContextDTO()
						.contextKey("AddressCtx1")
						.contextEntities(Collections.singletonList(new ContextEntityDTO().schemaStoreKey("CACHE").entityStoreKey("cachedEntity").sourceAlias("CACHE").relationType(ContextEntityDTO.RelationTypeEnum.LEAD).doPropagation(true).parentContextEntityKey(null).foreignKeys(null).noReferentAction(null).aliasedSourceEntityKey("CACHE-TABLE")))
						.originProcess(OriginProcess.UI.name())
						.storeName("AddressCtx1")
						.modelType(ModelTypeEnum.CONTEXT)
				).addTransformationsItem((TransformationDTO) new TransformationDTO()
						.productKey("INTEGRATIONLAYER")
						.isPublished(true)
						.contextKey("AddressCtx1")
						.customScript("select T1.CID as CID,\nMAX(T1.PCVERSION_ID)as PCVERSION_ID \nfrom TBCATALOG_ITEM T1 \ngroup by T1.CID;")
						.implementationType(TransformationDTO.ImplementationTypeEnum.SQL)
						.sourceType(TransformationDTO.SourceTypeEnum.CONTEXT)
						.targetEntityStoreKey(ADDRESS)
						.targetSchemaStoreKey(ALDM_CUSTOMER + "DataChannel")
						.targetSchemaName(ALDM_CUSTOMER)
						.originProcess(OriginProcess.UI.name())
						.modelType(ModelTypeEnum.TRANSFORMATION)
						.projectKey(PROJECT_KEY)
				).addAttributesItem(new TransformationAttributeDTO()
						.attributeKey("addressKey")
						.attributeName("Address Key")
						.type("STRING")
						.keyPosition(true)
						.sortOrder(1000)
						.isLogicalTime(false)
						.isUpdateTime(false)
						.isRequired(true)
						.doReferencialIntegrity(false));

		assertGetResponsePost(configurationServiceUrl + configServiceAdditionalUrl + "/shared-entity-transformation",
				HttpStatus.SC_BAD_REQUEST, dto);
	}

	private Optional<ElementData> getEntityReferentialIntegrityFromRepo(final String logicalEntityKey, final String logicalSchemaKey) {
		Response response = searchByProperties(ERI_ELEMENT_TYPE, "logicalEntityKey", logicalEntityKey, "logicalSchemaKey", logicalSchemaKey);
		List<ElementData> elements = Arrays.asList(response.as(ElementData[].class));
		return elements.stream().findAny();
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

	private static EntityTransformationDTO generateEntityTransformationForMasterEntity() {
		return new EntityTransformationDTO()
				.logicalEntityKey(CUSTOMER)
				.logicalSchemaKey(ALDM_CUSTOMER)
				.addContextsItem((ContextDTO) new ContextDTO()
						.contextKey("CustomerCtx1")
						.contextEntities(generateContextEntitiesForNewTransformation())
						.originProcess(OriginProcess.UI.name())
						.storeName("CustomerCtx1")
						.modelType(ModelTypeEnum.CONTEXT)
				).addTransformationsItem((TransformationDTO) new TransformationDTO()
						.productKey("INTEGRATIONLAYER")
						.isPublished(true)
						.contextKey("CustomerCtx1")
						.customScript("select T1.CID as CID,\nMAX(T1.PCVERSION_ID)as PCVERSION_ID \nfrom TBCATALOG_ITEM T1 \ngroup by T1.CID;")
						.implementationType(TransformationDTO.ImplementationTypeEnum.SQL)
						.sourceType(TransformationDTO.SourceTypeEnum.CONTEXT)
						.targetEntityStoreKey(CUSTOMER)
						.targetSchemaStoreKey(ALDM_CUSTOMER + "DataChannel")
						.targetSchemaName(ALDM_CUSTOMER)
						.leadkeys(createLeadKeys())
						.originProcess(OriginProcess.UI.name())
						.modelType(ModelTypeEnum.TRANSFORMATION)
						.projectKey(PROJECT_KEY)
				).addAttributesItem(new TransformationAttributeDTO()
						.attributeKey("customerKey")
						.attributeName("Customer Key")
						.type("STRING")
						.keyPosition(true)
						.sortOrder(1000)
						.isLogicalTime(false)
						.isUpdateTime(false)
						.isRequired(false)
						.doReferencialIntegrity(false)
				).addAttributesItem(new TransformationAttributeDTO()
						.attributeKey("mainAddressKey")
						.attributeName("Main Address Key")
						.type("STRING")
						.keyPosition(true)
						.sortOrder(10000)
						.isLogicalTime(false)
						.isUpdateTime(false)
						.isRequired(true)
						.doReferencialIntegrity(true)
						.parentEntityKey("Address")
						.parentSchemaKey(ALDM_CUSTOMER)
						.parentAttributeKey("addressKey"));
	}

	private static EntityTransformationDTO generateEntityTransformationForReferenceEntity() {
		List<TransformationContextEntityDTO> referenceSourceEntities = new ArrayList<>();
		TransformationContextEntityDTO transformationContextEntityDTO1 = new TransformationContextEntityDTO().schemaStoreKey("CRMREF").entityStoreKey("TABLE_COUNTRY");
		TransformationContextEntityDTO transformationContextEntityDTO2 = new TransformationContextEntityDTO().schemaStoreKey("BCMREF").entityStoreKey("COUNTRY");
		referenceSourceEntities.add(transformationContextEntityDTO1);
		referenceSourceEntities.add(transformationContextEntityDTO2);

		return new EntityTransformationDTO()
				.logicalEntityKey(QUEUE)
				.logicalSchemaKey(ALDM_CUSTOMER_SERVICE)
				.addTransformationsItem((TransformationDTO) new TransformationDTO()
						.productKey("INTEGRATIONLAYER")
						.referenceSourceEntities(referenceSourceEntities)
						.isPublished(true)
						.contextKey("REFContext")
						.customScript("select T1.CID as CID,\nMAX(T1.PCVERSION_ID)as PCVERSION_ID \nfrom TBCATALOG_ITEM T1 \ngroup by T1.CID;")
						.implementationType(TransformationDTO.ImplementationTypeEnum.SQL)
						.sourceType(TransformationDTO.SourceTypeEnum.REFERENCE)
						.targetEntityStoreKey(QUEUE)
						.targetSchemaStoreKey(ALDM_CUSTOMER_SERVICE + "DataChannel")
						.targetSchemaName(ALDM_CUSTOMER_SERVICE)
						.originProcess(OriginProcess.UI.name())
						.modelType(ModelTypeEnum.TRANSFORMATION)
						.projectKey(PROJECT_KEY)
				).addAttributesItem(new TransformationAttributeDTO()
						.attributeKey("queueKey")
						.attributeName("Queue Key")
						.type("STRING")
						.keyPosition(true)
						.sortOrder(1000)
						.isLogicalTime(false)
						.isUpdateTime(false)
						.isRequired(false)
						.doReferencialIntegrity(false)
				).addAttributesItem(new TransformationAttributeDTO()
						.attributeKey("queueDesc")
						.attributeName("Queue Desc")
						.type("STRING")
						.keyPosition(true)
						.sortOrder(2000)
						.isLogicalTime(false)
						.isUpdateTime(false)
						.isRequired(false)
						.doReferencialIntegrity(false)
				).addAttributesItem(new TransformationAttributeDTO()
						.attributeKey("queueSourceId")
						.attributeName("Queue Source Id")
						.type("STRING")
						.keyPosition(true)
						.sortOrder(4000)
						.isLogicalTime(false)
						.isUpdateTime(false)
						.isRequired(true)
						.doReferencialIntegrity(false));
	}

	private static EntityTransformationDTO generateEntityTransformationForCacheEntity(String cacheEntityKey) {
		List<TransformationContextEntityDTO> referenceSourceEntities = new ArrayList<>();
		TransformationContextEntityDTO transformationContextEntityDTO = new TransformationContextEntityDTO().schemaStoreKey("CRMAPP").entityStoreKey("TABLE_BUS_ORG");
		referenceSourceEntities.add(transformationContextEntityDTO);

		return new EntityTransformationDTO()
				.logicalEntityKey(cacheEntityKey)
				.logicalSchemaKey("CACHE")
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
						.targetSchemaName("CACHE")
						.targetEntityStoreKey(cacheEntityKey)
						.targetSchemaStoreKey("CACHE")
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

	private static List<ContextEntityDTO> generateContextEntities(){
		List<ContextEntityDTO> contextEntities = new ArrayList<>();
		ContextEntityDTO contextEntity1 = new ContextEntityDTO().schemaStoreKey("CRMAPP").entityStoreKey("TABLE_CUSTOMER").sourceAlias("CUST").relationType(ContextEntityDTO.RelationTypeEnum.LEAD).doPropagation(true).parentContextEntityKey(null).foreignKeys(null).noReferentAction(null).aliasedSourceEntityKey("CUST-TABLE_CUSTOMER");
		ContextEntityDTO contextEntity2 = new ContextEntityDTO().schemaStoreKey("CACHE").entityStoreKey("GEONAME_REF").sourceAlias("CACHE").relationType(ContextEntityDTO.RelationTypeEnum.REF).doPropagation(true).parentContextEntityKey(null).foreignKeys(null).noReferentAction(null).aliasedSourceEntityKey("AAA");
		ContextEntityDTO contextEntity3 = new ContextEntityDTO().schemaStoreKey("CRMAPP").entityStoreKey("MTM_BUS_ORG95_CUSTOMER1").sourceAlias("MBO").relationType(ContextEntityDTO.RelationTypeEnum.MTO).doPropagation(true).parentContextEntityKey("CUST-TABLE_CUSTOMER").foreignKeys("CUSTOMER2BUS_ORG").noReferentAction(ContextEntityDTO.NoReferentActionEnum.OPTIONAL).aliasedSourceEntityKey("BBB");
		ContextEntityDTO contextEntity4 = new ContextEntityDTO().schemaStoreKey(ALDM_CUSTOMER).entityStoreKey("Country").sourceAlias("TBO").relationType(ContextEntityDTO.RelationTypeEnum.REF).doPropagation(true).parentContextEntityKey(null).foreignKeys(null).noReferentAction(null).aliasedSourceEntityKey("CCC");
		contextEntities.add(contextEntity1);
		contextEntities.add(contextEntity2);
		contextEntities.add(contextEntity3);
		contextEntities.add(contextEntity4);

		return contextEntities;
	}

	private static List<ContextEntityDTO> generateContextEntitiesForNewTransformation(){
		List<ContextEntityDTO> contextEntities = new ArrayList<>();
		ContextEntityDTO contextEntity1 = new ContextEntityDTO().schemaStoreKey("CRMAPP").entityStoreKey("TABLE_CUSTOMER").sourceAlias("CUST").relationType(ContextEntityDTO.RelationTypeEnum.LEAD).doPropagation(true).parentContextEntityKey(null).foreignKeys(null).noReferentAction(null).aliasedSourceEntityKey("CUST-TABLE_CUSTOMER");
		ContextEntityDTO contextEntity2 = new ContextEntityDTO().schemaStoreKey("CRMAPP").entityStoreKey("MTM_BUS_ORG95_CUSTOMER1").sourceAlias("MBO").relationType(ContextEntityDTO.RelationTypeEnum.OTM).doPropagation(true).parentContextEntityKey("CUST-TABLE_CUSTOMER").foreignKeys("CUSTOMER2BUS_ORG").noReferentAction(ContextEntityDTO.NoReferentActionEnum.OPTIONAL).aliasedSourceEntityKey("MBO-MTM_BUS_ORG95_CUSTOMER1");
		ContextEntityDTO contextEntity3 = new ContextEntityDTO().schemaStoreKey("CRMAPP").entityStoreKey("TABLE_BUS_ORG").sourceAlias("TBO").relationType(ContextEntityDTO.RelationTypeEnum.MTM).doPropagation(true).parentContextEntityKey("MBO-MTM_BUS_ORG95_CUSTOMER1").foreignKeys("BUS_ORG2CUSTOMER").noReferentAction(ContextEntityDTO.NoReferentActionEnum.OPTIONAL).aliasedSourceEntityKey("TBO-TABLE_BUS_ORG");
		ContextEntityDTO contextEntity4 = new ContextEntityDTO().schemaStoreKey("CACHE").entityStoreKey("GEONAME_REF").sourceAlias("CACHE").relationType(ContextEntityDTO.RelationTypeEnum.REF).doPropagation(true).parentContextEntityKey(null).foreignKeys(null).noReferentAction(null).aliasedSourceEntityKey("CACHE-TABLE");

		contextEntities.add(contextEntity1);
		contextEntities.add(contextEntity2);
		contextEntities.add(contextEntity3);
		contextEntities.add(contextEntity4);

		return contextEntities;
	}

	private void checkContextForAddressT01A() {
		Response contextResponse = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformation-contexts"+ "/" + "AddressContext",
				HttpStatus.SC_OK);
		ContextDTO responseContextDTO = contextResponse.as(ContextDTO.class);
		assertEquals(responseContextDTO.getContextKey(), ADDRESS);
		assertEquals(responseContextDTO.getStoreName(), "AddressContext");
		assertEquals(responseContextDTO.getContextEntities().size(),4);
		Optional<ContextEntityDTO> contextEntityFromRepo = responseContextDTO.getContextEntities().stream().filter(ctx -> ctx.getEntityStoreKey().equals("TABLE_ADDRESS")).findFirst();
		ContextEntityDTO contextEntity =  contextEntityFromRepo.get();
		assertEquals(contextEntity.getEntityStoreKey(), "TABLE_ADDRESS");
		assertEquals(contextEntity.getSchemaStoreKey(), "CRMAPP");
		assertEquals(contextEntity.getSourceAlias(),"TA");
		assertEquals(contextEntity.getRelationType(), ContextEntityDTO.RelationTypeEnum.LEAD);
		assertTrue(contextEntity.isDoPropagation());
		assertNull(contextEntity.getParentContextEntityKey());
		assertNull(contextEntity.getForeignKeys());
		assertEquals(contextEntity.getAliasedSourceEntityKey(),"TA-TABLE_ADDRESS");
	}

	private void checkTransformationForAddressT01A() {
		Response transformationResponse = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformations"+ "/" + "aLDMCustomerDataChannel-Address-AddressContext",
				HttpStatus.SC_OK);
		TransformationDTO responseTransformationDTO = transformationResponse.as(TransformationDTO.class);
		assertEquals(responseTransformationDTO.getContextKey(), "AddressContext");
		assertEquals(responseTransformationDTO.getStoreName(), "aLDMCustomerDataChannel-Address-AddressContext");
		assertEquals(responseTransformationDTO.getTargetEntityStoreKey(), ADDRESS);
		assertEquals(responseTransformationDTO.getTargetSchemaName(), ALDM_CUSTOMER);
		assertEquals(responseTransformationDTO.getTargetSchemaStoreKey(), "aLDMCustomerDataChannel");
	}

	private void checkContextForAddressT02() {
		Response contextResponse = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformation-contexts"+ "/" + "AddressCtx2",
				HttpStatus.SC_OK);
		ContextDTO responseContextDTO = contextResponse.as(ContextDTO.class);
		assertEquals(responseContextDTO.getContextKey(), "AddressCtx2");
		assertEquals(responseContextDTO.getStoreName(), "AddressCtx2");
		assertEquals(responseContextDTO.getContextEntities().size(),4);
		Optional<ContextEntityDTO> contextEntityFromRepo = responseContextDTO.getContextEntities().stream().filter(ctx -> ctx.getEntityStoreKey().equals("MTM_BUS_ORG95_CUSTOMER1")).findFirst();
		ContextEntityDTO contextEntity =  contextEntityFromRepo.get();
		assertEquals(contextEntity.getEntityStoreKey(), "MTM_BUS_ORG95_CUSTOMER1");
		assertEquals(contextEntity.getSchemaStoreKey(), "CRMAPP");
		assertEquals(contextEntity.getSourceAlias(),"MBO");
		assertEquals(contextEntity.getRelationType(), ContextEntityDTO.RelationTypeEnum.MTO);
		assertTrue(contextEntity.isDoPropagation());
		assertEquals(contextEntity.getParentContextEntityKey(), "CUST-TABLE_CUSTOMER");
		assertEquals(contextEntity.getForeignKeys(), "CUSTOMER2BUS_ORG");
		assertEquals(contextEntity.getAliasedSourceEntityKey(),"BBB");
	}

	private void checkTransformationForAddressT02() {
		Response transformationResponse = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformations"+ "/" + "aLDMCustomerDataChannel-Address-AddressCtx2",
				HttpStatus.SC_OK);
		TransformationDTO responseTransformationDTO = transformationResponse.as(TransformationDTO.class);
		assertEquals(responseTransformationDTO.getContextKey(), "AddressCtx2");
		assertEquals(responseTransformationDTO.getStoreName(), "aLDMCustomerDataChannel-Address-AddressCtx2");
		assertEquals(responseTransformationDTO.getTargetEntityStoreKey(), ADDRESS);
		assertEquals(responseTransformationDTO.getTargetSchemaName(), "aLDMCustomer");
		assertEquals(responseTransformationDTO.getTargetSchemaStoreKey(), "aLDMCustomerDataChannel");
	}

	private void checkContextForCustomerT08() {
		Response contextResponse = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformation-contexts"+ "/" + "CustomerCtx1",
				HttpStatus.SC_OK);
		ContextDTO responseContextDTO = contextResponse.as(ContextDTO.class);
		assertEquals(responseContextDTO.getContextKey(), "CustomerCtx1");
		assertEquals(responseContextDTO.getStoreName(), "CustomerCtx1");
		assertEquals(responseContextDTO.getContextEntities().size(),4);
		Optional<ContextEntityDTO> contextEntityFromRepo = responseContextDTO.getContextEntities().stream().filter(ctx -> ctx.getEntityStoreKey().equals("TABLE_CUSTOMER")).findFirst();
		ContextEntityDTO contextEntity =  contextEntityFromRepo.get();
		assertEquals(contextEntity.getEntityStoreKey(), "TABLE_CUSTOMER");
		assertEquals(contextEntity.getSchemaStoreKey(), "CRMAPP");
		assertEquals(contextEntity.getSourceAlias(),"CUST");
		assertEquals(contextEntity.getRelationType(), ContextEntityDTO.RelationTypeEnum.LEAD);
		assertTrue(contextEntity.isDoPropagation());
		assertNull(contextEntity.getParentContextEntityKey());
		assertNull(contextEntity.getForeignKeys());
		assertEquals(contextEntity.getAliasedSourceEntityKey(),"CUST-TABLE_CUSTOMER");
	}

	private void checkTransformationForCustomerT08() {
		Response transformationResponse = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformations"+ "/" + "aLDMCustomerDataChannel-Customer-CustomerCtx1",
				HttpStatus.SC_OK);
		TransformationDTO responseTransformationDTO = transformationResponse.as(TransformationDTO.class);
		assertEquals(responseTransformationDTO.getContextKey(), "CustomerCtx1");
		assertEquals(responseTransformationDTO.getStoreName(), "aLDMCustomerDataChannel-Customer-CustomerCtx1");
		assertEquals(responseTransformationDTO.getTargetEntityStoreKey(), CUSTOMER);
		assertEquals(responseTransformationDTO.getTargetSchemaName(), ALDM_CUSTOMER);
		assertEquals(responseTransformationDTO.getTargetSchemaStoreKey(), "aLDMCustomerDataChannel");
	}

	private void checkContextForCustomerT09() {
		Response contextResponse = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformation-contexts"+ "/" + "CustomerCtx2",
				HttpStatus.SC_OK);
		ContextDTO responseContextDTO = contextResponse.as(ContextDTO.class);
		assertEquals(responseContextDTO.getContextKey(), "CustomerCtx2");
		assertEquals(responseContextDTO.getStoreName(), "CustomerCtx2");
		assertEquals(responseContextDTO.getContextEntities().size(),2);
		Optional<ContextEntityDTO> contextEntityFromRepo = responseContextDTO.getContextEntities().stream().filter(ctx -> ctx.getEntityStoreKey().equals("TABLE_CUSTOMER")).findFirst();
		ContextEntityDTO contextEntity =  contextEntityFromRepo.get();
		assertEquals(contextEntity.getEntityStoreKey(), "TABLE_CUSTOMER");
		assertEquals(contextEntity.getSchemaStoreKey(), "CRMAPP");
		assertEquals(contextEntity.getSourceAlias(),"CUST");
		assertEquals(contextEntity.getRelationType(), ContextEntityDTO.RelationTypeEnum.LEAD);
		assertTrue(contextEntity.isDoPropagation());
		assertNull(contextEntity.getParentContextEntityKey());
		assertNull(contextEntity.getForeignKeys());
		assertEquals(contextEntity.getAliasedSourceEntityKey(),"CUST-TABLE_CUSTOMER");
	}

	private void checkTransformationForCustomerT09() {
		Response transformationResponse = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformations"+ "/" + "aLDMCustomerDataChannel-Customer-CustomerCtx2",
				HttpStatus.SC_OK);
		TransformationDTO responseTransformationDTO = transformationResponse.as(TransformationDTO.class);
		assertEquals(responseTransformationDTO.getContextKey(), "CustomerCtx2");
		assertEquals(responseTransformationDTO.getStoreName(), "aLDMCustomerDataChannel-Customer-CustomerCtx2");
		assertEquals(responseTransformationDTO.getTargetEntityStoreKey(), CUSTOMER);
		assertEquals(responseTransformationDTO.getTargetSchemaName(), ALDM_CUSTOMER);
		assertEquals(responseTransformationDTO.getTargetSchemaStoreKey(), "aLDMCustomerDataChannel");
	}

	private void checkTransformationForQueueT11() {
		Response transformationResponse = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformations"+ "/" + "aLDMCustomerServiceDataChannel-Queue-REFContext",
				HttpStatus.SC_OK);
		TransformationDTO responseTransformationDTO = transformationResponse.as(TransformationDTO.class);
		assertEquals(responseTransformationDTO.getContextKey(), "REFContext");
		assertEquals(responseTransformationDTO.getStoreName(), "aLDMCustomerServiceDataChannel-Queue-REFContext");
		assertEquals(responseTransformationDTO.getTargetEntityStoreKey(), QUEUE);
		assertEquals(responseTransformationDTO.getTargetSchemaName(), ALDM_CUSTOMER_SERVICE);
		assertEquals(responseTransformationDTO.getTargetSchemaStoreKey(), "aLDMCustomerServiceDataChannel");
	}

	private void checkContextForCustomerT15() {
		Response contextResponse = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformation-contexts"+ "/" + "CustomerCtx3",
				HttpStatus.SC_OK);
		ContextDTO responseContextDTO = contextResponse.as(ContextDTO.class);
		assertEquals(responseContextDTO.getContextKey(), "CustomerCtx3");
		assertEquals(responseContextDTO.getStoreName(), "CustomerCtx3");
		assertEquals(responseContextDTO.getContextEntities().size(),2);

		Optional<ContextEntityDTO> contextEntityDTO1 = responseContextDTO.getContextEntities().stream().filter(ctx -> ctx.getEntityStoreKey().equals("TABLE_CUSTOMER")).findFirst();
		ContextEntityDTO contextEntity1 =  contextEntityDTO1.get();
		assertEquals(contextEntity1.getEntityStoreKey(), "TABLE_CUSTOMER");
		assertEquals(contextEntity1.getSchemaStoreKey(), "CRMAPP");
		assertEquals(contextEntity1.getSourceAlias(),"CUST");
		assertEquals(contextEntity1.getRelationType(), ContextEntityDTO.RelationTypeEnum.LEAD);
		assertTrue(contextEntity1.isDoPropagation());
		assertNull(contextEntity1.getParentContextEntityKey());
		assertNull(contextEntity1.getForeignKeys());
		assertEquals(contextEntity1.getAliasedSourceEntityKey(),"CUST-TABLE_CUSTOMER");

		Optional<ContextEntityDTO> contextEntityDTO2 = responseContextDTO.getContextEntities().stream().filter(ctx -> ctx.getEntityStoreKey().equals("cachedEntity")).findFirst();
		ContextEntityDTO contextEntity2 =  contextEntityDTO2.get();
		assertEquals(contextEntity2.getEntityStoreKey(), "cachedEntity");
		assertEquals(contextEntity2.getSchemaStoreKey(), "CACHE");
		assertEquals(contextEntity2.getSourceAlias(),"CACHE");
		assertEquals(contextEntity2.getRelationType(), ContextEntityDTO.RelationTypeEnum.REF);
		assertTrue(contextEntity2.isDoPropagation());
		assertNull(contextEntity2.getParentContextEntityKey());
		assertNull(contextEntity2.getForeignKeys());
		assertEquals(contextEntity2.getAliasedSourceEntityKey(),"CACHE-TABLE");
	}

	private void checkTransformationForCustomerT15() {
		Response transformationResponse = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformations"+ "/" + "aLDMCustomerDataChannel-Customer-CustomerCtx3",
				HttpStatus.SC_OK);
		TransformationDTO responseTransformationDTO = transformationResponse.as(TransformationDTO.class);
		assertEquals(responseTransformationDTO.getContextKey(), "CustomerCtx3");
		assertEquals(responseTransformationDTO.getStoreName(), "aLDMCustomerDataChannel-Customer-CustomerCtx3");
		assertEquals(responseTransformationDTO.getTargetEntityStoreKey(), CUSTOMER);
		assertEquals(responseTransformationDTO.getTargetSchemaName(), ALDM_CUSTOMER);
		assertEquals(responseTransformationDTO.getTargetSchemaStoreKey(), "aLDMCustomerDataChannel");
	}

	//check data
	private void checkAddressEntityT02(EntityTransformationDTO responseEntityTransformationDTO, EntityTransformationDTO responseEntityTransformationDTOModified) {
		assertEquals(responseEntityTransformationDTOModified.getLogicalEntityKey(), ADDRESS);
		assertEquals(responseEntityTransformationDTOModified.getLogicalSchemaKey(), ALDM_CUSTOMER);
		assertEquals(responseEntityTransformationDTOModified.getTransformations().get(2).getTargetSchemaName(), ALDM_CUSTOMER);
		assertEquals(responseEntityTransformationDTOModified.getTransformations().get(2).getStoreName(), "aLDMCustomerDataChannel-Address-AddressCtx2");

		assertEquals(responseEntityTransformationDTO.getAttributes().size(), 25);
		Optional<TransformationAttributeDTO> transformationAttributeDTO1 = responseEntityTransformationDTOModified.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("stateKey")).findFirst();
		TransformationAttributeDTO transformationAttribute1 =  transformationAttributeDTO1.get();
		assertEquals(transformationAttribute1.getAttributeKey(), "stateKey");
		assertEquals(transformationAttribute1.getAttributeName(), "State Key");
		assertEquals(transformationAttribute1.getType(), "STRING");
		assertEquals(transformationAttribute1.getParentSchemaKey(), ALDM_CUSTOMER);
		assertEquals(transformationAttribute1.getParentEntityKey(), "Country");
		assertEquals(transformationAttribute1.getParentAttributeKey(), "countryKey");

		Optional<TransformationAttributeDTO> transformationAttributeDTO2 = responseEntityTransformationDTOModified.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("electronicAccessNum")).findFirst();
		TransformationAttributeDTO transformationAttribute2 =  transformationAttributeDTO2.get();
		assertEquals(transformationAttribute2.getAttributeKey(), "electronicAccessNum");
		assertEquals(transformationAttribute2.getAttributeName(), "Electronic Access Num");
		assertEquals(transformationAttribute2.getType(), "STRING");
	}

	private void checkCustomerEntityT08(EntityTransformationDTO responseEntityTransformationDTO) {
		assertEquals(responseEntityTransformationDTO.getLogicalEntityKey(), CUSTOMER);
		assertEquals(responseEntityTransformationDTO.getLogicalSchemaKey(), ALDM_CUSTOMER);
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getTargetSchemaName(), ALDM_CUSTOMER);
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getStoreName(), "aLDMCustomerDataChannel-Customer-CustomerCtx1");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getId(), "aLDMCustomerDataChannel-Customer-CustomerCtx1");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getContextKey(), "CustomerCtx1");
		assertEquals(responseEntityTransformationDTO.getTransformations().size(), 1);

		assertEquals(responseEntityTransformationDTO.getContexts().get(0).getContextKey(), "CustomerCtx1");
		assertEquals(responseEntityTransformationDTO.getContexts().get(0).getStoreName(), "CustomerCtx1");
		Optional<ContextEntityDTO> contextEntityDTO = responseEntityTransformationDTO.getContexts().get(0).getContextEntities().stream().filter(contextEntityDTO1 -> contextEntityDTO1.getEntityStoreKey().equals("TABLE_CUSTOMER")).findFirst();
		ContextEntityDTO contextEntityDTO1 =  contextEntityDTO.get();
		assertEquals(contextEntityDTO1.getAliasedSourceEntityKey(), "CUST-TABLE_CUSTOMER");
		assertTrue(contextEntityDTO1.isDoPropagation());
		assertNull(contextEntityDTO1.getForeignKeys());
		assertNull(contextEntityDTO1.getNoReferentAction());
		assertNull(contextEntityDTO1.getParentContextEntityKey());
		assertEquals(contextEntityDTO1.getRelationType().name(), ContextEntityDTO.RelationTypeEnum.LEAD.name());
		assertEquals(contextEntityDTO1.getSchemaStoreKey(), "CRMAPP");
		assertEquals(contextEntityDTO1.getSourceAlias(), "CUST");

		assertEquals(responseEntityTransformationDTO.getAttributes().size(), 2);
		Optional<TransformationAttributeDTO> transformationAttributeDTO1 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("mainAddressKey")).findFirst();
		TransformationAttributeDTO transformationAttribute1 =  transformationAttributeDTO1.get();
		assertEquals(transformationAttribute1.getAttributeKey(), "mainAddressKey");
		assertEquals(transformationAttribute1.getAttributeName(), "Main Address Key");
		assertEquals(transformationAttribute1.getType(),"STRING");
		assertTrue(transformationAttribute1.isDoReferencialIntegrity());
		assertEquals(transformationAttribute1.getParentSchemaKey(), ALDM_CUSTOMER);
		assertEquals(transformationAttribute1.getParentEntityKey(), "Address");
		assertEquals(transformationAttribute1.getParentAttributeKey(), "addressKey");
	}

	private void checkCustomerEntityT09(EntityTransformationDTO responseEntityTransformationDTO) {
		assertEquals(responseEntityTransformationDTO.getLogicalEntityKey(), masterDTO.getLogicalEntityKey());
		assertEquals(responseEntityTransformationDTO.getLogicalSchemaKey(), masterDTO.getLogicalSchemaKey());
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getTargetSchemaName(), masterDTO.getTransformations().get(0).getTargetSchemaName());
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getStoreName(), "aLDMCustomerDataChannel-Customer-CustomerCtx1");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getContextKey(), masterDTO.getTransformations().get(0).getContextKey());
		assertEquals(responseEntityTransformationDTO.getContexts().get(0).getContextKey(), masterDTO.getContexts().get(0).getContextKey());
		assertEquals(responseEntityTransformationDTO.getContexts().get(0).getStoreName(), masterDTO.getContexts().get(0).getStoreName());
		assertEquals(responseEntityTransformationDTO.getTransformations().get(1).getTargetSchemaName(), ALDM_CUSTOMER);
		assertEquals(responseEntityTransformationDTO.getTransformations().get(1).getStoreName(), "aLDMCustomerDataChannel-Customer-CustomerCtx2");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(1).getContextKey(), "CustomerCtx2");
		assertEquals(responseEntityTransformationDTO.getContexts().get(1).getContextKey(), "CustomerCtx2");
		assertEquals(responseEntityTransformationDTO.getContexts().get(1).getStoreName(), "CustomerCtx2");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(2).getTargetSchemaName(), ALDM_CUSTOMER);
		assertEquals(responseEntityTransformationDTO.getTransformations().get(2).getStoreName(), "aLDMCustomerDataChannel-Customer-EaddressContext");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(2).getContextKey(), "EaddressContext");

		assertEquals(responseEntityTransformationDTO.getAttributes().size(), 3);
		Optional<TransformationAttributeDTO> transformationAttributeDTO1 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("countryKey")).findFirst();
		TransformationAttributeDTO transformationAttribute1 =  transformationAttributeDTO1.get();
		assertEquals(transformationAttribute1.getAttributeKey(), "countryKey");
		assertEquals(transformationAttribute1.getAttributeName(), "Country Key");
		assertEquals(transformationAttribute1.getType(), "STRING");
		assertTrue(transformationAttribute1.isDoReferencialIntegrity());
		assertEquals(transformationAttribute1.getParentSchemaKey(), ALDM_CUSTOMER);
		assertEquals(transformationAttribute1.getParentEntityKey(), "Country");
		assertEquals(transformationAttribute1.getParentAttributeKey(), "countryKey");
	}

	private void checkCustomerEntityT10(EntityTransformationDTO responseEntityTransformationDTO) {
		assertEquals(responseEntityTransformationDTO.getTransformations().size(), 3);

		Optional<TransformationDTO> transformationDTO1 = responseEntityTransformationDTO.getTransformations().stream().filter(tr1 -> tr1.getId().equals("aLDMCustomerDataChannel-Customer-CustomerCtx1")).findFirst();
		TransformationDTO transformation1 =  transformationDTO1.get();
		assertEquals(transformation1.getTargetSchemaName(), ALDM_CUSTOMER);
		assertEquals(transformation1.getStoreName(), "aLDMCustomerDataChannel-Customer-CustomerCtx1");
		assertEquals(transformation1.getContextKey(), "CustomerCtx1");

		Optional<TransformationDTO> transformationDTO2 = responseEntityTransformationDTO.getTransformations().stream().filter(tr1 -> tr1.getId().equals("aLDMCustomerDataChannel-Customer-CustomerCtx2")).findFirst();
		TransformationDTO transformation2 =  transformationDTO2.get();
		assertEquals(transformation2.getTargetSchemaName(), "aLDMCustomer");
		assertEquals(transformation2.getStoreName(), "aLDMCustomerDataChannel-Customer-CustomerCtx2");
		assertEquals(transformation2.getContextKey(), "CustomerCtx2");

		Optional<TransformationDTO> transformationDTO3 = responseEntityTransformationDTO.getTransformations().stream().filter(tr1 -> tr1.getId().equals("aLDMCustomerDataChannel-Customer-EaddressContext")).findFirst();
		TransformationDTO transformation3 =  transformationDTO3.get();
		assertEquals(transformation3.getTargetSchemaName(), "aLDMCustomer");
		assertEquals(transformation3.getStoreName(), "aLDMCustomerDataChannel-Customer-EaddressContext");
		assertEquals(transformation3.getContextKey(), "EaddressContext");

		assertEquals(responseEntityTransformationDTO.getContexts().size(), 3);
		Optional<ContextDTO> contextDTO1 = responseEntityTransformationDTO.getContexts().stream().filter(ctx1 -> ctx1.getContextKey().equals("CustomerCtx1")).findFirst();
		ContextDTO context1 =  contextDTO1.get();
		assertEquals(context1.getContextKey(), "CustomerCtx1");
		assertEquals(context1.getStoreName(), "CustomerCtx1");

		Optional<ContextDTO> contextDTO2 = responseEntityTransformationDTO.getContexts().stream().filter(ctx1 -> ctx1.getContextKey().equals("CustomerCtx2")).findFirst();
		ContextDTO context2 =  contextDTO2.get();
		assertEquals(context2.getContextKey(), "CustomerCtx2");
		assertEquals(context2.getStoreName(), "CustomerCtx2");

		Optional<ContextDTO> contextDTO3 = responseEntityTransformationDTO.getContexts().stream().filter(ctx1 -> ctx1.getContextKey().equals("Eaddress")).findFirst();
		ContextDTO context3 =  contextDTO3.get();
		assertEquals(context3.getContextKey(), "Eaddress");
		assertEquals(context3.getStoreName(), "EaddressContext");

		Optional<ContextEntityDTO> contextEntityDTO1 = context1.getContextEntities().stream().filter(contextEntity -> contextEntity.getEntityStoreKey().equals("TABLE_BUS_ORG")).findFirst();
		ContextEntityDTO contextEntity1 =  contextEntityDTO1.get();
		assertEquals(contextEntity1.getAliasedSourceEntityKey(), "TBO-TABLE_BUS_ORG");
		assertTrue(contextEntity1.isDoPropagation());
		assertEquals(contextEntity1.getForeignKeys(), "BUS_ORG2CUSTOMER");
		assertEquals(contextEntity1.getNoReferentAction(), ContextEntityDTO.NoReferentActionEnum.OPTIONAL);
		assertEquals(contextEntity1.getParentContextEntityKey(), "MBO-MTM_BUS_ORG95_CUSTOMER1");
		assertEquals(contextEntity1.getRelationType().name(), ContextEntityDTO.RelationTypeEnum.MTM.name());
		assertEquals(contextEntity1.getSchemaStoreKey(), "CRMAPP");
		assertEquals(contextEntity1.getSourceAlias(), "TBO");

		Optional<ContextEntityDTO> contextEntityDTO2 = context2.getContextEntities().stream().filter(contextEntity -> contextEntity.getEntityStoreKey().equals("MTM_CONTACT56_CUSTOMER0")).findFirst();
		ContextEntityDTO contextEntity2 =  contextEntityDTO2.get();
		assertEquals(contextEntity2.getEntityStoreKey(), "MTM_CONTACT56_CUSTOMER0");
		assertEquals(contextEntity2.getSchemaStoreKey(), "CRMAPP");
		assertEquals(contextEntity2.getSourceAlias(),"MCC");
		assertEquals(contextEntity2.getRelationType(), ContextEntityDTO.RelationTypeEnum.OTM);
		assertTrue(contextEntity2.isDoPropagation());
		assertEquals(contextEntity2.getParentContextEntityKey(), "CUST-TABLE_CUSTOMER");
		assertEquals(contextEntity2.getForeignKeys(), "CUSTOMER2CONTACT");
		assertEquals(contextEntity2.getAliasedSourceEntityKey(),"MCC-MTM_CONTACT56_CUSTOMER0");

		Optional<ContextEntityDTO> contextEntityDTO3 = context3.getContextEntities().stream().filter(contextEntity -> contextEntity.getEntityStoreKey().equals("TABLE_E_ADDR")).findFirst();
		ContextEntityDTO contextEntity3 =  contextEntityDTO3.get();
		assertEquals(contextEntity3.getAliasedSourceEntityKey(), "TE-TABLE_E_ADDR");
		assertTrue(contextEntity3.isDoPropagation());
		assertNull(contextEntity3.getForeignKeys());
		assertNull(contextEntity3.getNoReferentAction());
		assertNull(contextEntity3.getParentContextEntityKey());
		assertEquals(contextEntity3.getRelationType().name(), ContextEntityDTO.RelationTypeEnum.LEAD.name());
		assertEquals(contextEntity3.getSchemaStoreKey(), "CRMAPP");
		assertEquals(contextEntity3.getSourceAlias(), "TE");

		assertEquals(responseEntityTransformationDTO.getAttributes().size(), 3);
		Optional<TransformationAttributeDTO> transformationAttributeDTO1 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("customerKey")).findFirst();
		TransformationAttributeDTO transformationAttribute1 =  transformationAttributeDTO1.get();
		assertEquals(transformationAttribute1.getAttributeKey(), "customerKey");
		assertEquals(transformationAttribute1.getAttributeName(), "Customer Key");
		assertEquals(transformationAttribute1.getType(), "STRING");
		assertFalse(transformationAttribute1.isDoReferencialIntegrity());

		Optional<TransformationAttributeDTO> transformationAttributeDTO2 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("mainAddressKey")).findFirst();
		TransformationAttributeDTO transformationAttribute2 =  transformationAttributeDTO2.get();
		assertEquals(transformationAttribute2.getAttributeKey(), "mainAddressKey");
		assertEquals(transformationAttribute2.getAttributeName(), "Main Address Key");
		assertEquals(transformationAttribute2.getType(), "STRING");
		assertTrue(transformationAttribute2.isDoReferencialIntegrity());
		assertEquals(transformationAttribute2.getParentSchemaKey(), ALDM_CUSTOMER);
		assertEquals(transformationAttribute2.getParentEntityKey(), "Address");
		assertEquals(transformationAttribute2.getParentAttributeKey(), "addressKey");

		Optional<TransformationAttributeDTO> transformationAttributeDTO3 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("countryKey")).findFirst();
		TransformationAttributeDTO transformationAttribute3 =  transformationAttributeDTO3.get();
		assertEquals(transformationAttribute3.getAttributeKey(), "countryKey");
		assertEquals(transformationAttribute3.getAttributeName(), "Country Key");
		assertEquals(transformationAttribute3.getType(), "STRING");
		assertTrue(transformationAttribute3.isDoReferencialIntegrity());
		assertEquals(transformationAttribute3.getParentSchemaKey(), ALDM_CUSTOMER);
		assertEquals(transformationAttribute3.getParentEntityKey(), "Country");
		assertEquals(transformationAttribute3.getParentAttributeKey(), "countryKey");
	}

	private void checkQueueEntityT11(EntityTransformationDTO responseEntityTransformationDTO) {
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getTargetSchemaName(), ALDM_CUSTOMER_SERVICE);
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getStoreName(), "aLDMCustomerServiceDataChannel-Queue-REFContext");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getId(), "aLDMCustomerServiceDataChannel-Queue-REFContext");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getContextKey(), "REFContext");
		assertEquals(responseEntityTransformationDTO.getTransformations().size(), 1 );

		//check referenceSourceEntities
		Optional<TransformationContextEntityDTO> transformationContextEntityDTO1 = responseEntityTransformationDTO.getTransformations().get(0).getReferenceSourceEntities().stream().filter(transformationContextEntityDTO -> transformationContextEntityDTO.getEntityStoreKey().equals("TABLE_COUNTRY")).findFirst();
		TransformationContextEntityDTO transformationContextEntity1 =  transformationContextEntityDTO1.get();
		assertEquals(transformationContextEntity1.getEntityStoreKey(), "TABLE_COUNTRY");
		assertEquals(transformationContextEntity1.getSchemaStoreKey(), "CRMREF");

		Optional<TransformationContextEntityDTO> transformationContextEntityDTO2 = responseEntityTransformationDTO.getTransformations().get(0).getReferenceSourceEntities().stream().filter(transformationContextEntityDTO -> transformationContextEntityDTO.getEntityStoreKey().equals("COUNTRY")).findFirst();
		TransformationContextEntityDTO transformationContextEntity2 =  transformationContextEntityDTO2.get();
		assertEquals(transformationContextEntity2.getEntityStoreKey(), "COUNTRY");
		assertEquals(transformationContextEntity2.getSchemaStoreKey(), "BCMREF");

		assertEquals(responseEntityTransformationDTO.getAttributes().size(), 3);
		Optional<TransformationAttributeDTO> transformationAttributeDTO1 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("queueKey")).findFirst();
		TransformationAttributeDTO transformationAttribute1 =  transformationAttributeDTO1.get();
		assertEquals(transformationAttribute1.getAttributeKey(), "queueKey");
		assertEquals(transformationAttribute1.getAttributeName(), "Queue Key");
		assertEquals(transformationAttribute1.getType(),"STRING");
		assertTrue(transformationAttribute1.isKeyPosition());
		assertFalse(transformationAttribute1.isDoReferencialIntegrity());
	}

	private void checkQueueEntityT12(EntityTransformationDTO responseEntityTransformationDTO) {
		assertEquals(responseEntityTransformationDTO.getLogicalEntityKey(), referenceDTO.getLogicalEntityKey());
		assertEquals(responseEntityTransformationDTO.getLogicalSchemaKey(), referenceDTO.getLogicalSchemaKey());
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getTargetSchemaName(), referenceDTO.getTransformations().get(0).getTargetSchemaName());
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getStoreName(), "aLDMCustomerServiceDataChannel-Queue-REFContext");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getContextKey(), referenceDTO.getTransformations().get(0).getContextKey());

		assertEquals(responseEntityTransformationDTO.getAttributes().size(), 4);
		Optional<TransformationAttributeDTO> transformationAttributeDTO1 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("queueTitle")).findFirst();
		TransformationAttributeDTO transformationAttribute1 =  transformationAttributeDTO1.get();
		assertEquals(transformationAttribute1.getAttributeKey(), "queueTitle");
		assertEquals(transformationAttribute1.getAttributeName(), "Queue title");
		assertEquals(transformationAttribute1.getType(), "STRING");
		assertFalse(transformationAttribute1.isDoReferencialIntegrity());
	}

	private void checkQueueEntityT13(EntityTransformationDTO responseEntityTransformationDTO) {
		assertEquals(responseEntityTransformationDTO.getTransformations().size(), 1);
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getTargetSchemaName(), ALDM_CUSTOMER_SERVICE);
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getStoreName(), "aLDMCustomerServiceDataChannel-Queue-REFContext");
		assertEquals(responseEntityTransformationDTO.getTransformations().get(0).getContextKey(), "REFContext");

		//check referenceSourceEntities
		Optional<TransformationContextEntityDTO> transformationContextEntityDTO1 = responseEntityTransformationDTO.getTransformations().get(0).getReferenceSourceEntities().stream().filter(transformationContextEntityDTO -> transformationContextEntityDTO.getEntityStoreKey().equals("TABLE_COUNTRY")).findFirst();
		TransformationContextEntityDTO transformationContextEntity1 =  transformationContextEntityDTO1.get();
		assertEquals(transformationContextEntity1.getEntityStoreKey(), "TABLE_COUNTRY");
		assertEquals(transformationContextEntity1.getSchemaStoreKey(), "CRMREF");

		Optional<TransformationContextEntityDTO> transformationContextEntityDTO2 = responseEntityTransformationDTO.getTransformations().get(0).getReferenceSourceEntities().stream().filter(transformationContextEntityDTO -> transformationContextEntityDTO.getEntityStoreKey().equals("COUNTRY")).findFirst();
		TransformationContextEntityDTO transformationContextEntity2 =  transformationContextEntityDTO2.get();
		assertEquals(transformationContextEntity2.getEntityStoreKey(), "COUNTRY");
		assertEquals(transformationContextEntity2.getSchemaStoreKey(), "BCMREF");

		assertEquals(responseEntityTransformationDTO.getAttributes().size(), 4);
		Optional<TransformationAttributeDTO> transformationAttributeDTO1 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("queueKey")).findFirst();
		TransformationAttributeDTO transformationAttribute1 =  transformationAttributeDTO1.get();
		assertEquals(transformationAttribute1.getAttributeKey(), "queueKey");
		assertEquals(transformationAttribute1.getAttributeName(), "Queue Key");
		assertEquals(transformationAttribute1.getType(),"STRING");
		assertFalse(transformationAttribute1.isDoReferencialIntegrity());

		Optional<TransformationAttributeDTO> transformationAttributeDTO2 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("queueDesc")).findFirst();
		TransformationAttributeDTO transformationAttribute2 =  transformationAttributeDTO2.get();
		assertEquals(transformationAttribute2.getAttributeKey(), "queueDesc");
		assertEquals(transformationAttribute2.getAttributeName(), "Queue Desc");
		assertEquals(transformationAttribute2.getType(), "STRING(50)");
		assertFalse(transformationAttribute2.isDoReferencialIntegrity());

		Optional<TransformationAttributeDTO> transformationAttributeDTO3 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("queueSourceId")).findFirst();
		TransformationAttributeDTO transformationAttribute3 =  transformationAttributeDTO3.get();
		assertEquals(transformationAttribute3.getAttributeKey(), "queueSourceId");
		assertEquals(transformationAttribute3.getAttributeName(), "Queue Source Id");
		assertEquals(transformationAttribute3.getType(), "STRING(20)");
		assertFalse(transformationAttribute3.isDoReferencialIntegrity());

		Optional<TransformationAttributeDTO> transformationAttributeDTO4 = responseEntityTransformationDTO.getAttributes().stream().filter(tr1 -> tr1.getAttributeKey().equals("queueTitle")).findFirst();
		TransformationAttributeDTO transformationAttribute4 =  transformationAttributeDTO4.get();
		assertEquals(transformationAttribute4.getAttributeKey(), "queueTitle");
		assertEquals(transformationAttribute4.getAttributeName(), "Queue title");
		assertEquals(transformationAttribute4.getType(), "STRING(50)");
		assertFalse(transformationAttribute4.isDoReferencialIntegrity());
	}

	private void checkCustomerEntityT15(EntityTransformationDTO responseEntityTransformationDTO) {
		assertEquals(responseEntityTransformationDTO.getTransformations().size(), 4);
		Optional<TransformationDTO> newTransformationDTO = responseEntityTransformationDTO.getTransformations().stream().filter(tr1 -> tr1.getId().equals("aLDMCustomerDataChannel-Customer-CustomerCtx3")).findFirst();
		TransformationDTO newTransformation =  newTransformationDTO.get();
		assertEquals(newTransformation.getTargetSchemaName(), ALDM_CUSTOMER);
		assertEquals(newTransformation.getStoreName(), "aLDMCustomerDataChannel-Customer-CustomerCtx3");
		assertEquals(newTransformation.getContextKey(), "CustomerCtx3");

		assertEquals(responseEntityTransformationDTO.getContexts().size(), 3);
		Optional<ContextDTO> contextDTO1 = responseEntityTransformationDTO.getContexts().stream().filter(ctx2 -> ctx2.getContextKey().equals("CustomerCtx3")).findFirst();
		ContextDTO context1 =  contextDTO1.get();
		assertEquals(context1.getContextKey(), "CustomerCtx3");
		assertEquals(context1.getStoreName(), "CustomerCtx3");
	}

	private static List<LeadKeyDTO> createLeadKeys() {
		LeadKeyDTO leadKeyDTO1=new LeadKeyDTO();
		leadKeyDTO1.setSourceAttribute("sourceAttribute1");
		leadKeyDTO1.setTargetAttribute("targetAttribute2");

		LeadKeyDTO leadKeyDTO2=new LeadKeyDTO();
		leadKeyDTO2.setSourceAttribute("sourceAttribute2");
		leadKeyDTO2.setTargetAttribute("targetAttribute2");

		List<LeadKeyDTO> leadKeyDTOList=new ArrayList<>();
		leadKeyDTOList.add(leadKeyDTO1);
		leadKeyDTOList.add(leadKeyDTO2);
		return  leadKeyDTOList;

	}

}
