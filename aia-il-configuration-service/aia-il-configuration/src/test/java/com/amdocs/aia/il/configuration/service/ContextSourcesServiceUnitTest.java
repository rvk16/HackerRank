package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AIAAPIMessageTemplate;
import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.service.context.ContextSourcesService;
import com.amdocs.aia.il.configuration.service.context.ContextSourcesServiceImpl;
import com.amdocs.aia.il.configuration.shared.AiaSharedProxy;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ContextSourcesServiceImpl.class})
public class ContextSourcesServiceUnitTest {
    private static final String PROJECT_KEY = "aia";

    @Autowired
    private ContextSourcesService contextSourcesService;

    @MockBean
    private AiaRepositoryOperations aiaRepositoryOperations;
    @MockBean
    private ILOperations ilOperations;
    @MockBean
    private AiaSharedProxy aiaSharedProxy;
    @MockBean
    private MessageHelper messageHelper;

    @Test
    void whenGetContextSourcesShouldReturnDTOList() {
        List<ContextSourceDTO> mockForContextSourceDTOList = new ArrayList<>();
        List<Map<String, Object>> mockForEntities = createMockForPropertiesByQuery();
        mockForContextSourceDTOList.add(createContextSourceDTOForCache(mockForEntities));
        List<Map<String, Object>> mockForEntitiesExternal = createMockForExternal();
        List<Map<String, Object>> mockForEntitiesRef = createMockForReference();

        List<ContextSourceDTO> responseForExternalSchema = Collections.singletonList(createContextSourceDTOForExternal(mockForEntitiesExternal.get(0), mockForEntitiesExternal));
        List<ContextSourceDTO> responseForReference = Collections.singletonList(createContextSourceDTOForReference(mockForEntitiesRef.get(0), mockForEntitiesRef));
        mockForContextSourceDTOList.addAll(responseForExternalSchema);
        mockForContextSourceDTOList.addAll(responseForReference);

        when(aiaRepositoryOperations.findElementsPropertiesByQuery(any())).thenReturn(mockForEntities);

        final List<ContextSourceDTO> contextSources = contextSourcesService.getContextSources(PROJECT_KEY, null);
        //cache
        assertEquals(mockForContextSourceDTOList.get(0).getSchemaKey(), contextSources.get(0).getSchemaKey());
        assertEquals(mockForContextSourceDTOList.get(0).getSchemaName(), contextSources.get(0).getSchemaName());
        assertEquals(mockForContextSourceDTOList.get(0).getSchemaType(), contextSources.get(0).getSchemaType());
        assertEquals(mockForContextSourceDTOList.get(0).getContextSourceEntities().get(0).getEntityKey(), contextSources.get(0).getContextSourceEntities().get(0).getEntityKey());
        assertEquals(mockForContextSourceDTOList.get(0).getContextSourceEntities().get(0).getEntityName(), contextSources.get(0).getContextSourceEntities().get(0).getEntityName());
        //external
        assertEquals(mockForContextSourceDTOList.get(1).getSchemaKey(), contextSources.get(1).getSchemaKey());
        assertEquals(mockForContextSourceDTOList.get(1).getSchemaName(), contextSources.get(1).getSchemaName());
        assertEquals(mockForContextSourceDTOList.get(1).getSchemaType(), contextSources.get(1).getSchemaType());
        assertEquals(mockForContextSourceDTOList.get(1).getContextSourceEntities().get(0).getEntityKey(), contextSources.get(1).getContextSourceEntities().get(0).getEntityKey());
        assertEquals(mockForContextSourceDTOList.get(1).getContextSourceEntities().get(0).getEntityName(), contextSources.get(1).getContextSourceEntities().get(0).getEntityName());
        //reference
        assertEquals(mockForContextSourceDTOList.get(2).getSchemaKey(), contextSources.get(2).getSchemaKey());
        assertEquals(mockForContextSourceDTOList.get(2).getSchemaName(), contextSources.get(2).getSchemaName());
        assertEquals(mockForContextSourceDTOList.get(2).getSchemaType(), contextSources.get(2).getSchemaType());
        assertEquals(mockForContextSourceDTOList.get(2).getContextSourceEntities().get(0).getEntityKey(), contextSources.get(2).getContextSourceEntities().get(0).getEntityKey());
        assertEquals(mockForContextSourceDTOList.get(2).getContextSourceEntities().get(0).getEntityName(), contextSources.get(2).getContextSourceEntities().get(0).getEntityName());
    }

    @Test
    void whenGetContextSourcesShouldReturnDTOListForSpecificSchema() {
        List<ContextSourceDTO> mockForContextSourceDTOList = new ArrayList<>();
        List<Map<String, Object>> mockForEntities = createMockForPropertiesByQuery();
        mockForContextSourceDTOList.add(createContextSourceDTOForCache(mockForEntities));
        List<Map<String, Object>> mockForEntitiesExternal = createMockForExternal();
        List<Map<String, Object>> mockForEntitiesRef = createMockForReference();

        List<ContextSourceDTO> responseForExternalSchema = Collections.singletonList(createContextSourceDTOForExternal(mockForEntitiesExternal.get(0), mockForEntitiesExternal));
        List<ContextSourceDTO> responseForReference = Collections.singletonList(createContextSourceDTOForReference(mockForEntitiesRef.get(0), mockForEntitiesRef));
        mockForContextSourceDTOList.addAll(responseForExternalSchema);
        mockForContextSourceDTOList.addAll(responseForReference);

        when(aiaRepositoryOperations.findElementsPropertiesByQuery(any())).thenReturn(mockForEntities);

        final List<ContextSourceDTO> contextSources = contextSourcesService.getContextSources(PROJECT_KEY, "CACHE");
        assertEquals(mockForContextSourceDTOList.get(0).getSchemaKey(), contextSources.get(0).getSchemaKey());
        assertEquals(mockForContextSourceDTOList.get(0).getSchemaName(), contextSources.get(0).getSchemaName());
        assertEquals(mockForContextSourceDTOList.get(0).getSchemaType(), contextSources.get(0).getSchemaType());
        assertEquals(mockForContextSourceDTOList.get(0).getContextSourceEntities().get(0).getEntityKey(), contextSources.get(0).getContextSourceEntities().get(0).getEntityKey());
        assertEquals(mockForContextSourceDTOList.get(0).getContextSourceEntities().get(0).getEntityName(), contextSources.get(0).getContextSourceEntities().get(0).getEntityName());
    }

    @Test
    void whenGetContextEntitiesMetadataShouldReturnDTOList() {
        ExternalEntityDTO mockForExternalEntity = createMockExternalEntity();
        List<EntityStore> mockForEntityStores = Collections.singletonList(mock(EntityStore.class));
        CacheReferenceEntityDTO mockForCacheEntity = mock(CacheReferenceEntityDTO.class);
        EntitiesDTO mockForEntities = getMockContextEntitiesRefDTO();
        List<BaseEntityDTO> mockForBaseEntityDTOs = Collections.singletonList(createMockBaseEntityDTOs(mockForExternalEntity.getEntityKey()));

        when(ilOperations.getCacheEntity(anyString(), anyString())).thenReturn(mockForCacheEntity);
        when(ilOperations.getExternalEntity(anyString(), anyString(), anyString())).thenReturn(mockForExternalEntity);
        when(aiaSharedProxy.searchEntityStores(anyString(), anyString())).thenReturn(mockForEntityStores);

        final List<BaseEntityDTO> baseEntitiesDTOs = contextSourcesService.searchContextEntitiesMetadata(PROJECT_KEY, mockForEntities);

        final BaseEntityDTO expected = mockForBaseEntityDTOs.get(0);
        final BaseEntityDTO actual = baseEntitiesDTOs.get(0);
        assertEquals(expected.getEntityKey(), actual.getEntityKey());
        assertEquals(expected.getEntityName(), actual.getEntityName());
        assertEquals(expected.getSchemaKey(), actual.getSchemaKey());
        assertEquals(expected.getAttributes().size(), 2);

        Optional<BaseAttributeDTO> attributeDTO1 = actual.getAttributes().stream().filter(baseAttributeDTO -> baseAttributeDTO.getAttributeKey().equals("attributeKey1")).findFirst();
        Optional<BaseAttributeDTO> attributeDTO2 = actual.getAttributes().stream().filter(baseAttributeDTO -> baseAttributeDTO.getAttributeKey().equals("attributeKey2")).findFirst();

        assertEquals(expected.getAttributes().get(0).getAttributeKey(), attributeDTO1.get().getAttributeKey());
        assertEquals(expected.getAttributes().get(0).getAttributeName(), attributeDTO1.get().getAttributeName());
        assertEquals(expected.getAttributes().get(0).getDatatype(), attributeDTO1.get().getDatatype());
        assertEquals(expected.getAttributes().get(0).getKeyPosition(),  attributeDTO1.get().getKeyPosition());
        assertEquals(expected.getAttributes().get(1).getAttributeKey(), attributeDTO2.get().getAttributeKey());
        assertEquals(expected.getAttributes().get(1).getAttributeName(), attributeDTO2.get().getAttributeName());
        assertEquals(expected.getAttributes().get(1).getDatatype(), attributeDTO2.get().getDatatype());
        assertEquals(expected.getAttributes().get(1).getKeyPosition(),  attributeDTO2.get().getKeyPosition());
    }

    @Test
    void whenGetContextEntitiesMetadataShouldThrowException() {
        EntitiesDTO mockForEntities = getMockContextEntitiesRefDTO();

        when(ilOperations.getExternalEntity(anyString(), anyString(), anyString())).thenThrow(new ApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST));
        doReturn(getEntityApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST))
                .when(messageHelper).createObjectDoesNotExistException(anyString(), anyString(), anyString());

        final ApiException ex = assertThrows(ApiException.class, () -> contextSourcesService.searchContextEntitiesMetadata(PROJECT_KEY, mockForEntities));
        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
    }

    private static ApiException getEntityApiException(AiaApiException.AiaApiHttpCodes status, AIAAPIMessageTemplate message) {
        return new ApiException(status, message, "display_name", "element_key");
    }

    private static ExternalEntityDTO createMockExternalEntity() {
        ExternalEntityDTO externalEntityDTO = new ExternalEntityDTO();
        externalEntityDTO.setEntityKey("entityKey1");
        externalEntityDTO.setEntityName("entityName");
        externalEntityDTO.setSchemaKey("schemaKey1");
        externalEntityDTO.setAttributes(getAttributes());

        return externalEntityDTO;
    }

    private static List<ExternalAttributeDTO> getAttributes() {
        ExternalAttributeDTO attributeDTO1 = new ExternalAttributeDTO();
        attributeDTO1.setAttributeKey("attributeKey1");
        attributeDTO1.setAttributeName("attribute Key1");
        attributeDTO1.setKeyPosition(1);
        attributeDTO1.setLogicalDatatype("STRING");

        ExternalAttributeDTO attributeDTO2 = new ExternalAttributeDTO();
        attributeDTO2.setAttributeKey("attributeKey2");
        attributeDTO2.setAttributeName("attribute Key2");
        attributeDTO2.setKeyPosition(null);
        attributeDTO2.setLogicalDatatype("LONG");

        List<ExternalAttributeDTO> attributeDTOList = new ArrayList<>();
        attributeDTOList.add(attributeDTO1);
        attributeDTOList.add(attributeDTO2);

        return attributeDTOList;
    }

    private static EntitiesDTO getMockContextEntitiesRefDTO() {
        EntitiesDTO entitiesDTO = new EntitiesDTO();

        ContextEntityRefDTO contextEntityRefDTO1 = new ContextEntityRefDTO();
        contextEntityRefDTO1
                .entityKey("entityKey1")
                .schemaKey("schemaKey1")
                .type(ContextEntityRefDTO.TypeEnum.EXTERNAL);

        entitiesDTO.setContextEntities(Collections.singletonList(contextEntityRefDTO1));
        return entitiesDTO;
    }

    private static BaseEntityDTO createMockBaseEntityDTOs(String entityKey) {
        BaseEntityDTO mockBaseEntityDTO = new BaseEntityDTO();
        mockBaseEntityDTO
                .entityKey(entityKey)
                .entityName("entityName")
                .schemaKey("schemaKey1")
                .attributes(getMockBaseAttributeDTO());

        return mockBaseEntityDTO;
    }

    private static List<BaseAttributeDTO> getMockBaseAttributeDTO() {
        BaseAttributeDTO baseAttributeDTO1 = new BaseAttributeDTO();
        baseAttributeDTO1
                .attributeKey("attributeKey1")
                .attributeName("attribute Key1")
                .datatype("STRING")
                .keyPosition(1);

        BaseAttributeDTO baseAttributeDTO2 = new BaseAttributeDTO();
        baseAttributeDTO2
                .attributeKey("attributeKey2")
                .attributeName("attribute Key2")
                .datatype("LONG")
                .keyPosition(null);

        List<BaseAttributeDTO> attributeDTOList = new ArrayList<>();
        attributeDTOList.add(baseAttributeDTO1);
        attributeDTOList.add(baseAttributeDTO2);

        return attributeDTOList;
    }

    private static List<Map<String, Object>> createMockForPropertiesByQuery() {
        List<Map<String, Object>> entities = new ArrayList<>();
        Map<String, Object> mockObj = new HashMap<>();
        mockObj.put("entityKey", "entityKey");
        mockObj.put("name", "entityName");
        mockObj.put("schemaKey", "schemaKey");
        mockObj.put("schemaStoreKey", "schemaStoreKey");
        mockObj.put("entityStoreKey", "entityStoreKey");
        mockObj.put("logicalSchemaKey", "logicalSchemaKey");

        entities.add(0, mockObj);
        return entities;
    }

    private static List<Map<String, Object>> createMockForExternal() {
        List<Map<String, Object>> entities = new ArrayList<>();
        Map<String, Object> mockObj = new HashMap<>();
        mockObj.put("schemaKey", "schemaKey");
        mockObj.put("entityKey", "entityKey");
        mockObj.put("name", "entityName");

        entities.add(0, mockObj);
        return entities;
    }

    private static List<Map<String, Object>> createMockForReference() {
        List<Map<String, Object>> entities = new ArrayList<>();
        Map<String, Object> mockObj = new HashMap<>();
        mockObj.put("logicalSchemaKey", "logicalSchemaKey");
        mockObj.put("entityStoreKey", "entityStoreKey");
        mockObj.put("name", "entityName");

        entities.add(0, mockObj);
        return entities;
    }

    private static ContextSourceDTO createContextSourceDTOForCache(List<Map<String, Object>> entityProperties) {
        return new ContextSourceDTO()
                .schemaKey("CACHE")
                .schemaName("Cached Entity")
                .schemaType(ContextSourceDTO.SchemaTypeEnum.CACHE)
                .contextSourceEntities(entityProperties.stream().map(ContextSourcesServiceUnitTest::createContextSourceEntityDTO).collect(Collectors.toList()));
    }

    private static ContextSourceEntityDTO createContextSourceEntityDTO(Map<String, Object> entityProperties) {
        return new ContextSourceEntityDTO()
                .entityKey(entityProperties.get("entityKey").toString())
                .entityName(entityProperties.get("name").toString());
    }

    private static ContextSourceDTO createContextSourceDTOForExternal(Map<String, Object> schemaProperties, List<Map<String, Object>> entityProperties) {
        return new ContextSourceDTO()
                .schemaKey(schemaProperties.get("schemaKey").toString())
                .schemaName(schemaProperties.getOrDefault("name", schemaProperties.get("schemaKey")).toString())
                .schemaType(ContextSourceDTO.SchemaTypeEnum.EXTERNAL)
                .contextSourceEntities(entityProperties != null ? entityProperties.stream().map(ContextSourcesServiceUnitTest::createContextSourceEntityDTO).collect(Collectors.toList()) : null);
    }

    private static ContextSourceDTO createContextSourceDTOForReference(Map<String, Object> schemaProperties, List<Map<String, Object>> entityProperties) {
        return new ContextSourceDTO()
                .schemaKey(schemaProperties.get("logicalSchemaKey").toString())
                .schemaName(schemaProperties.getOrDefault("name", schemaProperties.get("schemaStoreKey")).toString())
                .schemaType(ContextSourceDTO.SchemaTypeEnum.REFERENCE)
                .contextSourceEntities(entityProperties != null ? entityProperties.stream().map(ContextSourcesServiceUnitTest::createContextSourceEntityDTOForReference).collect(Collectors.toList()) : null);
    }

    private static ContextSourceEntityDTO createContextSourceEntityDTOForReference(Map<String, Object> entityProperties) {
        return new ContextSourceEntityDTO()
                .entityKey(entityProperties.get("entityStoreKey").toString())
                .entityName(entityProperties.get("name").toString());
    }
}