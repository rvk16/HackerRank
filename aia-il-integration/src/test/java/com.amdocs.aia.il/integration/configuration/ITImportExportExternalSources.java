package com.amdocs.aia.il.integration.configuration;

import com.amdocs.aia.il.configuration.client.dto.*;
import com.amdocs.aia.il.integration.BaseIntegrationTest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.*;
import static org.testng.Assert.*;
import static org.testng.Assert.assertTrue;

public class ITImportExportExternalSources extends BaseIntegrationTest {


	@BeforeClass
	public void setUpConf() {
		createChangeRequest("IL import - export external Schema - Test");
	}


	@Test
	public void T001_whenImportZipFile_initial() {

		File zipFileTOImport = new File("src/test/resources/data/importexport/external_schemas_to_import.zip");
		String url = configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/import";
		Response response = assertGetResponsePostWithFileAndContentType(url, HttpStatus.SC_OK, zipFileTOImport, "application/zip" );
		BulkImportResponseDTO bulkImportResponseDTO = response.as(BulkImportResponseDTO.class);
		checkBulkImportResponse(bulkImportResponseDTO, 4,15,0,0,0,0);
		checkSchemasA();
		checkEntitiesA();
	}

	@Test
	public void T002_whenImportZipFile_missing_Entities_and_attribute_on_file() {

		File zipFileTOImport = new File("src/test/resources/data/importexport/external_schemas_to_import_missing.zip");
		String url = configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/import";
		Response response = assertGetResponsePostWithFileAndContentType(url, HttpStatus.SC_OK, zipFileTOImport, "application/zip" );
		BulkImportResponseDTO bulkImportResponseDTO = response.as(BulkImportResponseDTO.class);
		checkBulkImportResponse(bulkImportResponseDTO, 0,0,2,15,0,0);
		checkSchemasA();
		checkEntitiesA();
	}

	@Test
	public void T003_whenImportZipFile_deleteAndUpdate() {
		File zipFileTOImport = new File("src/test/resources/data/importexport/external_schemas_to_import_delUp.zip");
		String url = configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/import";
		Response response = assertGetResponsePostWithFileAndContentType(url, HttpStatus.SC_OK, zipFileTOImport, "application/zip" );
		BulkImportResponseDTO bulkImportResponseDTO = response.as(BulkImportResponseDTO.class);
		checkBulkImportResponse(bulkImportResponseDTO, 1,1,3,10,1,2);
		checkSchemasB();
		checkEntitiesB();
	}

	@Test
	public void T004_whenImportZipFile_SchemaSheetOnly() {
		File zipFileTOImport = new File("src/test/resources/data/importexport/external_schemas_to_import_schemasOnly.zip");
		String url = configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/import";
		Response response = assertGetResponsePostWithFileAndContentType(url, HttpStatus.SC_OK, zipFileTOImport, "application/zip" );

		BulkImportResponseDTO bulkImportResponseDTO = response.as(BulkImportResponseDTO.class);
		checkBulkImportResponse(bulkImportResponseDTO, 1,0,3,0,1,0);
		Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas", HttpStatus.SC_OK);
		List<ExternalSchemaDTO> externalSchemaDTOS = Arrays.asList(responseGet.getBody().as(ExternalSchemaDTO[].class));
		Map <String,  ExternalSchemaDTO>  externalSchemaDTOMap = externalSchemaDTOS.stream().collect(Collectors.toMap(ExternalSchemaDTO::getSchemaKey, Function.identity()));
		assertTrue(externalSchemaDTOMap.containsKey("TCEVENT"));
		assertTrue(externalSchemaDTOMap.containsKey("REFDATA"));
		assertFalse(externalSchemaDTOMap.containsKey("SOM"));
		assertFalse(externalSchemaDTOMap.containsKey("OMS_FAKE"));
		assertTrue(externalSchemaDTOMap.containsKey("OMS_FAKE2"));
	}

	@Test
	public void T005_whenImportZipFile_AttributeSheetOnly() {
		Response responseGet1 = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/OMS/entities/TBORDER_ACTION", HttpStatus.SC_OK);
		ExternalEntityDTO OMS_TBORDER_ACTIONEntity = responseGet1.as(ExternalEntityDTO.class);
		Map <String,  ExternalAttributeDTO>  OMS_BORDER_ACTIONEntityAttMap = OMS_TBORDER_ACTIONEntity.getAttributes().stream().collect(Collectors.toMap(ExternalAttributeDTO::getAttributeKey, Function.identity()));
		assertEquals(OMS_BORDER_ACTIONEntityAttMap.size(),106);
		assertFalse(OMS_BORDER_ACTIONEntityAttMap.containsKey("CREATION_METHOD_1"));
		assertFalse(OMS_BORDER_ACTIONEntityAttMap.containsKey("CREATION_METHOD_2"));

		Response responseGet2 = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/OMS/entities/TBNAME", HttpStatus.SC_OK);
		ExternalEntityDTO OMS_TBNAME_Entity = responseGet2.as(ExternalEntityDTO.class);
		Map <String,  ExternalAttributeDTO>  OMS_TBNAME_EntityAttMap = OMS_TBNAME_Entity.getAttributes().stream().collect(Collectors.toMap(ExternalAttributeDTO::getAttributeKey, Function.identity()));
		assertEquals(OMS_TBNAME_EntityAttMap.size(),11);
		assertTrue(OMS_TBNAME_EntityAttMap.containsKey("LANGUAGE"));


		File zipFileTOImport = new File("src/test/resources/data/importexport/external_schemas_to_import_attributesOnly.zip");
		String url = configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/import";
		Response response = assertGetResponsePostWithFileAndContentType(url, HttpStatus.SC_OK, zipFileTOImport, "application/zip" );
		BulkImportResponseDTO bulkImportResponseDTO = response.as(BulkImportResponseDTO.class);

		checkBulkImportResponse(bulkImportResponseDTO, 0,0,0,10,0,0);


		Response responseGet3 = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/OMS/entities/TBORDER_ACTION", HttpStatus.SC_OK);
		ExternalEntityDTO OMS_TBORDER_ACTIONEntity2 = responseGet3.as(ExternalEntityDTO.class);
		Map <String,  ExternalAttributeDTO>  OMS_TBORDER_ACTION_EntityAttMap2 = OMS_TBORDER_ACTIONEntity2.getAttributes().stream().collect(Collectors.toMap(ExternalAttributeDTO::getAttributeKey, Function.identity()));
		assertEquals(OMS_TBORDER_ACTION_EntityAttMap2.size(),108);
		assertTrue(OMS_TBORDER_ACTION_EntityAttMap2.containsKey("CREATION_METHOD_1"));
		assertTrue(OMS_TBORDER_ACTION_EntityAttMap2.containsKey("CREATION_METHOD_2"));


		Response responseGet4 = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/OMS/entities/TBNAME", HttpStatus.SC_OK);
		ExternalEntityDTO OMS_TBNAME_Entity2 = responseGet4.as(ExternalEntityDTO.class);
		Map <String,  ExternalAttributeDTO>  OMS_TBNAME_EntityAttMap2 = OMS_TBNAME_Entity2.getAttributes().stream().collect(Collectors.toMap(ExternalAttributeDTO::getAttributeKey, Function.identity()));
		assertEquals(OMS_TBNAME_EntityAttMap2.size(),10);
		assertFalse(OMS_TBNAME_EntityAttMap2.containsKey("LANGUAGE"));

	}



	@Test
	public void T006_whenImportZipFile_AddEntity() {
		Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/OMS/entities", HttpStatus.SC_OK);
		List<ExternalEntityDTO> externalEntitiesDTOs = Arrays.asList(responseGet.getBody().as(ExternalEntityDTO[].class));
		Map <String,  ExternalEntityDTO>  OMS_EntityAttMap = externalEntitiesDTOs.stream().collect(Collectors.toMap(ExternalEntityDTO::getEntityKey, Function.identity()));
		assertEquals(OMS_EntityAttMap.size(),8);
		assertFalse(OMS_EntityAttMap.containsKey("TEST"));
		File zipFileTOImport = new File("src/test/resources/data/importexport/external_schemas_to_import-addEntity.zip");
		String url = configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/import";
		Response response = assertGetResponsePostWithFileAndContentType(url, HttpStatus.SC_OK, zipFileTOImport, "application/zip" );
		BulkImportResponseDTO bulkImportResponseDTO = response.as(BulkImportResponseDTO.class);
		checkBulkImportResponse(bulkImportResponseDTO,0,1,0,0,0,0);
		Response responseGet2 = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/OMS/entities", HttpStatus.SC_OK);
		List<ExternalEntityDTO> externalEntitiesDTOs2 = Arrays.asList(responseGet2.getBody().as(ExternalEntityDTO[].class));
		Map <String,  ExternalEntityDTO>  OMS_EntityAttMap2 = externalEntitiesDTOs2.stream().collect(Collectors.toMap(ExternalEntityDTO::getEntityKey, Function.identity()));
		assertEquals(OMS_EntityAttMap2.size(),9);
		assertTrue(OMS_EntityAttMap2.containsKey("TEST"));
		ExternalEntityDTO testEntity = OMS_EntityAttMap2.get("TEST");
		assertEquals(testEntity.getAttributes().size(),1);
		assertEquals(testEntity.getAttributes().get(0).getAttributeKey(),"ATT1");
		assertEquals(testEntity.getAttributes().get(0).getKeyPosition().intValue(),2);
	}


	@Test
	public void T007_whenImportZipFile_SchemasOnlyWithErrors() {
		Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas", HttpStatus.SC_OK);
		List<ExternalSchemaDTO> externalSchemaDTOS = Arrays.asList(responseGet.getBody().as(ExternalSchemaDTO[].class));
		assertEquals(externalSchemaDTOS.size(),4);
		File zipFileTOImport = new File("src/test/resources/data/importexport/external_schemas_to_import_schemasOnly_withChannelError.zip");
		String url = configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/import";
		Response response = assertGetResponsePostWithFileAndContentType(url, HttpStatus.SC_BAD_REQUEST, zipFileTOImport, "application/zip" );
	}
	@Test
	public void T008_whenImportZipFile_SchemasOnlyWithErrors() {
		Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas", HttpStatus.SC_OK);
		List<ExternalSchemaDTO> externalSchemaDTOS = Arrays.asList(responseGet.getBody().as(ExternalSchemaDTO[].class));
		assertEquals(externalSchemaDTOS.size(),4);
		File zipFileTOImport = new File("src/test/resources/data/importexport/external_schemas_to_import_schemasOnly_withChannelError.zip");
		String url = configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/import";
		Response response = assertGetResponsePostWithFileAndContentType(url, HttpStatus.SC_BAD_REQUEST, zipFileTOImport, "application/zip" );
	}
	@Test
	public void T009_whenImportZipFile_addKafkaSchema() {
		File zipFileTOImport = new File("src/test/resources/data/importexport/external_schemas_export_addKafkaSchema.zip");
		String url = configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/import";
		Response response = assertGetResponsePostWithFileAndContentType(url, HttpStatus.SC_OK, zipFileTOImport, "application/zip" );
		BulkImportResponseDTO bulkImportResponseDTO = response.as(BulkImportResponseDTO.class);
		checkBulkImportResponse(bulkImportResponseDTO,1,2,0,0,0,0);

		Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/PartyInteractionAbe", HttpStatus.SC_OK);
		ExternalSchemaDTO externalSchemaDTO = responseGet.as(ExternalSchemaDTO.class);
		assertEquals(externalSchemaDTO.getSchemaType(),"DIGITAL1");
		assertEquals(externalSchemaDTO.getStoreInfo().getStoreType().name(),"KAFKA");


		Response responseGet2 = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/PartyInteractionAbe/entities", HttpStatus.SC_OK);
		List<ExternalEntityDTO> externalEntitiesDTOs2 = Arrays.asList(responseGet2.getBody().as(ExternalEntityDTO[].class));
		Map <String,  ExternalEntityDTO>  entities = externalEntitiesDTOs2.stream().collect(Collectors.toMap(ExternalEntityDTO::getEntityKey, Function.identity()));
		assertEquals(entities.size(),2);
		assertTrue(entities.containsKey("D1PartyInteraction"));
		ExternalEntityDTO testEntity = entities.get("D1PartyInteraction");
		assertEquals(((ExternalKafkaEntityStoreInfoDTO) testEntity.getStoreInfo()).getRelativePaths(),"event/partyInteraction(STR)");
	}

	@Test
	public void T010_whenImportZipFile_addKafkaEntityMissingRelativePaths() {
		File zipFileTOImport = new File("src/test/resources/data/importexport/external_schemas_export_addKafkaEntityMissingRelativePaths.zip");
		String url = configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/import";
		Response response = assertGetResponsePostWithFileAndContentType(url, HttpStatus.SC_BAD_REQUEST, zipFileTOImport, "application/zip" );
	}

	@Test
	public void T011_whenImportZipFile_DeleteAll() {
		Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas", HttpStatus.SC_OK);
		List<ExternalSchemaDTO> externalSchemaDTOS = Arrays.asList(responseGet.getBody().as(ExternalSchemaDTO[].class));
		assertEquals(externalSchemaDTOS.size(),5);
		File zipFileTOImport = new File("src/test/resources/data/importexport/external_schemas_to_import-deleteAll.zip");
		String url = configurationServiceUrl + configServiceAdditionalUrl + "/external-schemas/import";
		Response response = assertGetResponsePostWithFileAndContentType(url, HttpStatus.SC_OK, zipFileTOImport, "application/zip" );
		BulkImportResponseDTO bulkImportResponseDTO = response.as(BulkImportResponseDTO.class);
		checkBulkImportResponse(bulkImportResponseDTO, 0,0,0,0,5,0);
		Response responseGet2 = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas", HttpStatus.SC_OK);
		List<ExternalSchemaDTO> externalSchemaDTOS2 = Arrays.asList(responseGet2.getBody().as(ExternalSchemaDTO[].class));
		assertEquals(externalSchemaDTOS2.size(),0);
	}


	private void checkSchemasA() {
		Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas", HttpStatus.SC_OK);
		List<ExternalSchemaDTO> externalSchemaDTOS = Arrays.asList(responseGet.getBody().as(ExternalSchemaDTO[].class));
		Map <String,  ExternalSchemaDTO>  externalSchemaDTOMap = externalSchemaDTOS.stream().collect(Collectors.toMap(ExternalSchemaDTO::getSchemaKey, Function.identity()));
		assertEquals(externalSchemaDTOMap.size(),4);
		assertTrue(externalSchemaDTOMap.containsKey("TCEVENT"));
		assertTrue(externalSchemaDTOMap.containsKey("REFDATA"));
		assertTrue(externalSchemaDTOMap.containsKey("SOM"));
		assertTrue(externalSchemaDTOMap.containsKey("OMS"));
		checkTcEventSchemaA(externalSchemaDTOMap.get("TCEVENT"));
		checkOmsSchemaA(externalSchemaDTOMap.get("OMS"));
		checkRefDataA(externalSchemaDTOMap.get("REFDATA"));
	}

	private void checkSchemasB() {
		Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas", HttpStatus.SC_OK);
		List<ExternalSchemaDTO> externalSchemaDTOS = Arrays.asList(responseGet.getBody().as(ExternalSchemaDTO[].class));
		Map <String,  ExternalSchemaDTO>  externalSchemaDTOMap = externalSchemaDTOS.stream().collect(Collectors.toMap(ExternalSchemaDTO::getSchemaKey, Function.identity()));
		assertEquals(externalSchemaDTOMap.size(),4);
		assertTrue(externalSchemaDTOMap.containsKey("TCEVENT"));
		assertTrue(externalSchemaDTOMap.containsKey("REFDATA"));
		assertFalse(externalSchemaDTOMap.containsKey("SOM"));
		assertTrue(externalSchemaDTOMap.containsKey("OMS"));
		assertTrue(externalSchemaDTOMap.containsKey("OMS_FAKE"));
		checkTcEventSchemaB(externalSchemaDTOMap.get("TCEVENT"));
		checkRefDataB(externalSchemaDTOMap.get("REFDATA"));
		checkOmsSchemaA(externalSchemaDTOMap.get("OMS"));
	}


	private void checkBulkImportResponse(BulkImportResponseDTO bulkImportResponseDTO , int newSchemas, int newEntities, int  modifiedSchemas, int modifiedEntities,
										 int deletedSchemas, int deletedEntities) {
		assertEquals(bulkImportResponseDTO.getNewSchemasCount().intValue(),newSchemas);
		assertEquals(bulkImportResponseDTO.getNewEntitiesCount().intValue(),newEntities);
		assertEquals(bulkImportResponseDTO.getModifiedSchemasCount().intValue(),modifiedSchemas);
		assertEquals(bulkImportResponseDTO.getModifiedEntitiesCount().intValue(),modifiedEntities);
		assertEquals(bulkImportResponseDTO.getDeletedSchemasCount().intValue(),deletedSchemas);
		assertEquals(bulkImportResponseDTO.getDeletedEntitiesCount().intValue(),deletedEntities);
	}

	private void checkTcEventSchemaA(ExternalSchemaDTO tcEventSchema) {
		assertEquals(tcEventSchema.getSchemaType(), "CSV_FILES");
		assertEquals(tcEventSchema.getDisplayType(), "CSV Files");
		assertEquals(tcEventSchema.getDescription(), "Tourbo Charging");
		assertEquals(tcEventSchema.getTypeSystem(), "logical");
		assertTrue(tcEventSchema.isIsActive());
		assertEquals(((ExternalCsvSchemaStoreInfoDTO) tcEventSchema.getStoreInfo()).getDefaultColumnDelimiter(), ",");
		assertEquals(((ExternalCsvSchemaStoreInfoDTO) tcEventSchema.getStoreInfo()).getDefaultDateFormat(), "yyyyy.MMMMM.dd GGG hh:mm aaa");
		assertEquals(((ExternalCsvSchemaStoreInfoDTO) tcEventSchema.getStoreInfo()).getStoreType().name(), "CSV");
		assertEquals(((ExternalCsvSchemaCollectionRulesDTO) tcEventSchema.getCollectionRules()).getDefaultInvalidFilenameAction().name(), "KEEP");
		assertEquals( tcEventSchema.getCollectionRules().getStoreType().name(), "CSV");
		assertEquals( tcEventSchema.getCollectionRules().getOngoingChannel(), "FILES");
		assertEquals(((ExternalCsvSchemaCollectionRulesDTO) tcEventSchema.getCollectionRules()).getDefaultInvalidFilenameAction().name(), "KEEP");
	//	assertEquals( tcEventSchema.getDataChannelInfo().getSerializationMethod().getValue(), "SharedProtobuf");
		assertEquals(tcEventSchema.getCreatedBy(), "testUser");
		assertEquals(tcEventSchema.getOriginProcess(), "IMPLEMENTATION");
		assertEquals( tcEventSchema.getAvailability().name(), "EXTERNAL");
	}

	private void checkTcEventSchemaB(ExternalSchemaDTO tcEventSchema) {
		assertEquals(tcEventSchema.getDescription(), "Tourbo Charging Schema");
		assertEquals(((ExternalCsvSchemaCollectionRulesDTO) tcEventSchema.getCollectionRules()).getDefaultInvalidFilenameAction().name(), "MOVE");
		assertEquals(((ExternalCsvSchemaCollectionRulesDTO) tcEventSchema.getCollectionRules()).getDefaultInvalidFilenameAction().name(), "MOVE");
		assertEquals( tcEventSchema.getDataChannelInfo().getSerializationMethod().getValue(), "SharedJson");
		assertEquals(((ExternalCsvSchemaStoreInfoDTO) tcEventSchema.getStoreInfo()).getDefaultColumnDelimiter(), ";");

	}

	private void checkRefDataA(ExternalSchemaDTO tcRefData) {
		assertNull(((ExternalCsvSchemaStoreInfoDTO) tcRefData.getStoreInfo()).getDefaultDateFormat());
	}

	private void checkRefDataB(ExternalSchemaDTO tcRefData) {
		assertNotNull(((ExternalCsvSchemaStoreInfoDTO) tcRefData.getStoreInfo()).getDefaultDateFormat());
		assertEquals(((ExternalCsvSchemaStoreInfoDTO) tcRefData.getStoreInfo()).getDefaultDateFormat(), "yyyyy.MMMMM.dd GGG hh:mm aaa");
	}

	private void checkOmsSchemaA(ExternalSchemaDTO omsSchema) {
		assertEquals(omsSchema.getSchemaType(), "ORACLE");
		assertEquals(omsSchema.getDisplayType(), "Oracle");
		assertTrue(omsSchema.isIsActive());
		assertFalse(omsSchema.isIsReference());
		assertEquals(omsSchema.getDescription(), "Amdocs Ordering");
		assertEquals(omsSchema.getTypeSystem(), "Oracle");
		assertEquals(((ExternalSqlSchemaStoreInfoDTO) omsSchema.getStoreInfo()).getDatabaseType(), "Oracle");
		assertEquals(omsSchema.getStoreInfo().getStoreType().name(), "SQL");

		assertEquals( omsSchema.getCollectionRules().getStoreType().name(), "SQL");
		assertEquals( omsSchema.getCollectionRules().getOngoingChannel(), "ATTUNITY");
		assertEquals( omsSchema.getCollectionRules().getReplayChannel(), "SQL");
		assertEquals( omsSchema.getCollectionRules().getInitialLoadChannel(), "SQL");
		assertEquals( omsSchema.getDataChannelInfo().getSerializationMethod().getValue(), "SharedJson");

		assertEquals( omsSchema.getOngoingCollector(), "Attunity");
		assertEquals( omsSchema.getInitialCollector(), "SQL");
		assertEquals( omsSchema.getSelectiveCollector(), "SQL");

		assertEquals( omsSchema.getAvailability().name(), "EXTERNAL");


		assertEquals(omsSchema.getCreatedBy(), "testUser");
		assertEquals(omsSchema.getOriginProcess(), "IMPLEMENTATION");
	}

	private void checkEntitiesA() {
		Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/OMS/entities", HttpStatus.SC_OK);
		List<ExternalEntityDTO> externalSchemaDTOS = Arrays.asList(responseGet.getBody().as(ExternalEntityDTO[].class));
		Map <String,  ExternalEntityDTO>  externalEntitiesDTOMap = externalSchemaDTOS.stream().collect(Collectors.toMap(ExternalEntityDTO::getEntityKey, Function.identity()));
	    assertEquals(externalEntitiesDTOMap.size(),10);

	    ExternalEntityDTO TBPRICE_PLANEntity = externalEntitiesDTOMap.get("TBPRICE_PLAN");
		assertEquals(TBPRICE_PLANEntity.getSchemaKey(), "OMS");
		assertEquals(TBPRICE_PLANEntity.getEntityKey(), "TBPRICE_PLAN");
		assertTrue(TBPRICE_PLANEntity.isIsActive());
		assertEquals(TBPRICE_PLANEntity.getStoreInfo().getStoreType().name(), "SQL");
		assertEquals(TBPRICE_PLANEntity.getCollectionRules().getStoreType().name(), "SQL");
		assertNotNull(TBPRICE_PLANEntity.getSerializationId());
		assertEquals(TBPRICE_PLANEntity.getOriginProcess(), "IMPLEMENTATION");
		Map <String,  ExternalAttributeDTO>  TBPRICE_PLANAttributes = TBPRICE_PLANEntity.getAttributes().stream().collect(Collectors.toMap(ExternalAttributeDTO::getAttributeKey, Function.identity()));
		checkTBPRICE_PLANAttributesA(TBPRICE_PLANAttributes);
		ExternalEntityDTO TBCATALOG_ITEM_CTGEntity = externalEntitiesDTOMap.get("TBCATALOG_ITEM_CTG");
		assertEquals(TBCATALOG_ITEM_CTGEntity.getDescription(),"");
		checkAmountOfEntities("REFDATA",1);
		checkAmountOfEntities("SOM",3);
		checkAmountOfEntities("TCEVENT",1);
	}

	private void checkAmountOfEntities(String schemaKey, int amount ) {
		Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/"+schemaKey+"/entities", HttpStatus.SC_OK);
		List<ExternalEntityDTO> externalSchemaDTOS = Arrays.asList(responseGet.getBody().as(ExternalEntityDTO[].class));
		assertEquals(externalSchemaDTOS.size(),amount);
	}

	private void checkEntitiesB() {
		Response responseGet = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/OMS/entities", HttpStatus.SC_OK);
		List<ExternalEntityDTO> externalEntities_OMS = Arrays.asList(responseGet.getBody().as(ExternalEntityDTO[].class));
		Map <String,  ExternalEntityDTO>  externalEntitiesDTOMap = externalEntities_OMS.stream().collect(Collectors.toMap(ExternalEntityDTO::getEntityKey, Function.identity()));
		assertEquals(externalEntitiesDTOMap.size(),8);
		ExternalEntityDTO TBCATALOG_ITEM_CTGEntity = externalEntitiesDTOMap.get("TBCATALOG_ITEM_CTG");
		assertEquals(TBCATALOG_ITEM_CTGEntity.getDescription(), "desc!!!");
		Response responseGet2 = assertGetResponseGet(configurationServiceUrl + "/projects/" + PROJECT_KEY + "/configuration/external-schemas/OMS_FAKE/entities", HttpStatus.SC_OK);
		List<ExternalEntityDTO> externalEntities_OMSFake = Arrays.asList(responseGet2.getBody().as(ExternalEntityDTO[].class));
		assertEquals(externalEntities_OMSFake.size(),1);
		assertEquals(externalEntities_OMSFake.get(0).getEntityKey(),"TBOFFER");
		assertEquals(externalEntities_OMSFake.get(0).getAttributes().size(),2);
		ExternalEntityDTO TBPRICE_PLANEntity = externalEntitiesDTOMap.get("TBPRICE_PLAN");
		Map <String,  ExternalAttributeDTO>  TBPRICE_PLANAttributes = TBPRICE_PLANEntity.getAttributes().stream().collect(Collectors.toMap(ExternalAttributeDTO::getAttributeKey, Function.identity()));
		checkTBPRICE_PLANAttributesB(TBPRICE_PLANAttributes);

	}

	private void checkTBPRICE_PLANAttributesA(Map<String, ExternalAttributeDTO> TBPRICE_PLANAttributes) {
		assertEquals(TBPRICE_PLANAttributes.size(),18);
		assertTrue(TBPRICE_PLANAttributes.containsKey("IS_PAYABLE"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("PCVERSION_ID"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("PRICE_PLAN_TYPE"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("CTDB_LAST_UPDATOR"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("IS_CHARGE_FOR_SUSPEND"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("CTDB_CRE_DATETIME"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("IS_VISIBLE"));
		ExternalAttributeDTO cidAtt = TBPRICE_PLANAttributes.get("CID");
		assertEquals(cidAtt.getAttributeKey(),"CID");
		assertEquals(cidAtt.getAttributeName(),"CID");
		assertEquals(cidAtt.getDatatype(),"VARCHAR2");
		assertEquals(cidAtt.getLogicalDatatype(),"STRING");
		assertNotNull(cidAtt.getSerializationId().intValue());
		assertEquals(cidAtt.getKeyPosition().intValue(),1);
		assertTrue(cidAtt.isIsRequired());
		assertFalse(cidAtt.isIsUpdateTime());
		assertFalse(cidAtt.isIsLogicalTime());
		assertEquals(cidAtt.getStoreInfo().getStoreType().name(),"SQL");
	}

	private void checkTBPRICE_PLANAttributesB(Map<String, ExternalAttributeDTO> TBPRICE_PLANAttributes) {
		assertEquals(TBPRICE_PLANAttributes.size(),20);
		assertTrue(TBPRICE_PLANAttributes.containsKey("IS_PAYABLE"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("PCVERSION_ID"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("PRICE_PLAN_TYPE"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("CTDB_LAST_UPDATOR"));
		assertFalse(TBPRICE_PLANAttributes.containsKey("IS_CHARGE_FOR_SUSPEND"));
		assertFalse(TBPRICE_PLANAttributes.containsKey("CTDB_CRE_DATETIME"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("IS_VISIBLE"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("IS_VISIBLE1"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("IS_VISIBLE2"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("IS_VISIBLE3"));
		assertTrue(TBPRICE_PLANAttributes.containsKey("IS_VISIBLE4"));

	}



}
