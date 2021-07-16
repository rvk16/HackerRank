package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CachedEntityTransformationServiceUnitTest {
    private static final String PROJECT_KEY = "aia";
    private static final String CONTEXTDTO_FOR_CACHED_ENTITY_TRANSFORMATION = "src/test/resources/json/contextDTO_for_cachedEntityTransformation.json";
    private static final String TRANSFORMATION_ATTRIBUTE_FOR_CACHED_ENTITY_TRANSFORMATION = "src/test/resources/json/TransformationAttribute_for_cachedEntityTransformation.json";
    private static final String TRANSFORMATION_FOR_CACHED_ENTITY_TRANSFORMATION = "src/test/resources/json/transformation_for_cachedEntityTransformation.json";

    @Mock
    private ILOperations ilOperations;

    @Mock
    private MessageHelper messageHelper;

    @InjectMocks
    private CachedEntityTransformationServiceImpl cachedEntityTransformationServiceImpl;

    @BeforeEach
    void initMessageHelper() {
        when(messageHelper.createElementNotFoundException(anyString(), anyString()))
                .then(ctx -> new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.ENTITY_DOES_NOT_EXIST,
                        ctx.getArgument(0, String.class), ctx.getArgument(1, String.class)));
        when(messageHelper.invalidPrimaryKeyException(anyString(), anyString()))
                .then(ctx -> new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.INVALID_PK,
                        ctx.getArgument(0, String.class), ctx.getArgument(1, String.class)));
        when(messageHelper.invalidIndexException(anyString(), anyString()))
                .then(ctx -> new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.INVALID_INDEX_SIZE,
                        ctx.getArgument(0, String.class), ctx.getArgument(1, String.class)));
    }

    @Test
    void whenGetCachedEntityTransformationDTO_ShouldReturnDTO() {
        CacheReferenceEntityDTO cacheReferenceEntity = createMockForCacheReferenceEntityDTO("InteractionChannel");
        List<ContextDTO> contextDTOList = createContextDTO();
        List<TransformationAttributeDTO> transformationAttributeDTOList = createTransformationAttributeDTO();
        List<TransformationDTO> transformationDTOList = createCachedEntityTransformation();

        doReturn(transformationDTOList).when(ilOperations).listTransformations(eq(PROJECT_KEY));
        doReturn(contextDTOList).when(ilOperations).listContexts(eq(PROJECT_KEY));
        doReturn(transformationAttributeDTOList).when(ilOperations).listTransformationAttributes(eq(PROJECT_KEY),eq("CACHE"),eq("InteractionChannel"));
        doReturn(cacheReferenceEntity).when(ilOperations).getCacheEntity(eq(PROJECT_KEY), eq("InteractionChannel"));

        final EntityTransformationDTO entityTransformationDTO = cachedEntityTransformationServiceImpl.get(PROJECT_KEY, "InteractionChannel");
        assertNotNull(entityTransformationDTO);
        assertEquals("CACHE",entityTransformationDTO.getTransformations().get(0).getTargetSchemaName());
        assertEquals("attributeKey1",entityTransformationDTO.getAttributes().get(0).getAttributeKey());
        assertEquals("attributeKey2",entityTransformationDTO.getAttributes().get(1).getAttributeKey());
        assertEquals("CACHE-InteractionChannel-REFContext",entityTransformationDTO.getTransformations().get(0).getStoreName());
    }

    @Test
    void whenCreateCachedEntityTransformationDTO_ShouldReturnDTO() {
        CacheReferenceEntityDTO cacheReferenceEntity = createMockForCacheReferenceEntityDTO("InteractionChannel");
        EntityTransformationDTO entityTransformationDTOExpected = new EntityTransformationDTO().logicalEntityKey(cacheReferenceEntity.getCacheReferenceEntityKey());
        List<ContextDTO> contextDTOList = createContextDTO();
        List<TransformationAttributeDTO> transformationAttributeDTOList = createTransformationAttributeDTO();
        List<TransformationDTO> transformationDTOList = createCachedEntityTransformation();
        entityTransformationDTOExpected.setAttributes(transformationAttributeDTOList);
        entityTransformationDTOExpected.setTransformations(transformationDTOList);

        doReturn(cacheReferenceEntity).when(ilOperations).saveCacheEntity(anyString(), any());
        doReturn(transformationDTOList).when(ilOperations).listTransformations(eq(PROJECT_KEY));
        doReturn(contextDTOList).when(ilOperations).listContexts(eq(PROJECT_KEY));
        doReturn(transformationAttributeDTOList).when(ilOperations).listTransformationAttributes(eq(PROJECT_KEY),eq("CACHE"),eq("InteractionChannel"));

        final EntityTransformationDTO entityTransformationDTOActual = cachedEntityTransformationServiceImpl.create(PROJECT_KEY, entityTransformationDTOExpected);
        assertNotNull(entityTransformationDTOActual);
        assertEquals(entityTransformationDTOExpected.getLogicalEntityKey(), entityTransformationDTOActual.getLogicalEntityKey());
    }

    @Test
    void whenUpdateCachedEntityTransformationDTO_ShouldReturnDTO() {
        CacheReferenceEntityDTO cacheReferenceEntity = createMockForCacheReferenceEntityDTO("InteractionChannel");
        EntityTransformationDTO entityTransformationDTOExpected = new EntityTransformationDTO().logicalEntityKey(cacheReferenceEntity.getCacheReferenceEntityKey());
        List<ContextDTO> contextDTOList = createContextDTO();
        List<TransformationAttributeDTO> transformationAttributeDTOList = createTransformationAttributeDTO();
        List<TransformationDTO> transformationDTOList = createCachedEntityTransformation();
        entityTransformationDTOExpected.setAttributes(transformationAttributeDTOList);
        entityTransformationDTOExpected.setTransformations(transformationDTOList);

        doReturn(cacheReferenceEntity).when(ilOperations).getCacheEntity(anyString(), any());
        doReturn(transformationDTOList).when(ilOperations).listTransformations(eq(PROJECT_KEY));
        doReturn(contextDTOList).when(ilOperations).listContexts(eq(PROJECT_KEY));
        doReturn(transformationAttributeDTOList).when(ilOperations).listTransformationAttributes(eq(PROJECT_KEY),eq("CACHE"),eq("InteractionChannel"));

        final EntityTransformationDTO entityTransformationDTOActual = cachedEntityTransformationServiceImpl.update(PROJECT_KEY, entityTransformationDTOExpected);
        assertNotNull(entityTransformationDTOActual);
        assertEquals(entityTransformationDTOExpected.getLogicalEntityKey(), entityTransformationDTOActual.getLogicalEntityKey());
        assertEquals(entityTransformationDTOExpected.getEntityName(), entityTransformationDTOActual.getEntityName());
    }

    @Test
    void whenUpdate_cachedEntityDoesNotExist_ShouldThrowException() {
        final String cacheEntityKey = "cacheEntity";
        final EntityTransformationDTO dto = new EntityTransformationDTO().logicalEntityKey(cacheEntityKey);
        doReturn(null).when(ilOperations).getCacheEntity(anyString(), anyString());

        ApiException e = assertThrows(ApiException.class, () -> cachedEntityTransformationServiceImpl.update(PROJECT_KEY, dto));
        assertEquals(2, e.getMessageParams().length);
        assertEquals(cacheEntityKey, e.getMessageParams()[1]);
    }

    @Test
    void whenGet_cachedEntityDoesNotExist_ShouldThrowException() {
        final String cacheEntityKey = "cacheEntity";
        final EntityTransformationDTO dto = new EntityTransformationDTO().logicalEntityKey(cacheEntityKey);
        doReturn(null).when(ilOperations).getCacheEntity(anyString(), anyString());

        ApiException e = assertThrows(ApiException.class, () -> cachedEntityTransformationServiceImpl.get(PROJECT_KEY, dto.getLogicalEntityKey()));
        assertEquals(2, e.getMessageParams().length);
        assertEquals(cacheEntityKey, e.getMessageParams()[1]);
    }

    @Test
    void whenSave_cachedEntityInvalidKey_ShouldThrowException() {
        EntityTransformationDTO entityTransformationDTOExpected = new EntityTransformationDTO().logicalEntityKey("cache entity invalid key").entityName("cache entity name");
        List<TransformationAttributeDTO> transformationAttributeDTOList = createTransformationAttributeDTO();
        List<TransformationDTO> transformationDTOList = createCachedEntityTransformation();
        entityTransformationDTOExpected.setAttributes(transformationAttributeDTOList);
        entityTransformationDTOExpected.setTransformations(transformationDTOList);

        ApiException e = assertThrows(ApiException.class, () -> cachedEntityTransformationServiceImpl.create(PROJECT_KEY, entityTransformationDTOExpected));
        assertEquals(2, e.getMessageParams().length);
        assertEquals(entityTransformationDTOExpected.getLogicalEntityKey(), e.getMessageParams()[1]);
    }

    @Test
    void whenSave_cachedEntityMultipleTransformation_ShouldThrowException() {
        EntityTransformationDTO entityTransformationDTOExpected = new EntityTransformationDTO().logicalEntityKey("cacheEntityKey").entityName("cache entity name");
        List<TransformationAttributeDTO> transformationAttributeDTOList = createTransformationAttributeDTO();
        List<TransformationDTO> transformationDTOList = createCachedEntityTransformation();
        entityTransformationDTOExpected.setAttributes(transformationAttributeDTOList);
        entityTransformationDTOExpected.setTransformations(transformationDTOList);
        entityTransformationDTOExpected.getTransformations().addAll(transformationDTOList);

        ApiException e = assertThrows(ApiException.class, () -> cachedEntityTransformationServiceImpl.create(PROJECT_KEY, entityTransformationDTOExpected));
        assertEquals(2, e.getMessageParams().length);
        assertEquals(entityTransformationDTOExpected.getLogicalEntityKey(), e.getMessageParams()[1]);
    }

    @Test
    void whenUpdate_cachedEntityMultipleTransformations_ShouldThrowException() {
        EntityTransformationDTO entityTransformationDTOExpected = new EntityTransformationDTO().logicalEntityKey("cacheEntityKey").entityName("cache entity name");
        List<TransformationAttributeDTO> transformationAttributeDTOList = createTransformationAttributeDTO();
        List<TransformationDTO> transformationDTOList = createCachedEntityTransformation();
        entityTransformationDTOExpected.setAttributes(transformationAttributeDTOList);
        entityTransformationDTOExpected.setTransformations(transformationDTOList);
        entityTransformationDTOExpected.getTransformations().addAll(transformationDTOList);

        ApiException e = assertThrows(ApiException.class, () -> cachedEntityTransformationServiceImpl.update(PROJECT_KEY, entityTransformationDTOExpected));
        assertEquals(2, e.getMessageParams().length);
        assertEquals(entityTransformationDTOExpected.getLogicalEntityKey(), e.getMessageParams()[1]);
    }

    @Test
    void whenDeleteCachedEntityTransformationDTO_ShouldReturn200OK() {
        CacheReferenceEntityDTO cacheReferenceEntity = createMockForCacheReferenceEntityDTO("InteractionChannel");
        List<ContextDTO> contextDTOList = createContextDTO();
        List<TransformationAttributeDTO> transformationAttributeDTOList = createTransformationAttributeDTO();
        List<TransformationDTO> transformationDTOList = createCachedEntityTransformation();

        doReturn(cacheReferenceEntity).when(ilOperations).saveCacheEntity(anyString(), any());
        doReturn(transformationDTOList).when(ilOperations).listTransformations(eq(PROJECT_KEY));
        doReturn(contextDTOList).when(ilOperations).listContexts(eq(PROJECT_KEY));
        doReturn(transformationAttributeDTOList).when(ilOperations).listTransformationAttributes(eq(PROJECT_KEY),eq("CACHE"),eq("InteractionChannel"));

        cachedEntityTransformationServiceImpl.delete(PROJECT_KEY, "InteractionChannel");
        verify(ilOperations, times(1)).deleteEntityTransformations(PROJECT_KEY, "CACHE", "InteractionChannel");
    }

    @Test
    void whenCreateCachedEntityTransformationDTO_DuplicateReferenceSourceEntities_ShouldThrowException() {
        EntityTransformationDTO entityTransformationDTOExpected = new EntityTransformationDTO().logicalEntityKey("cacheEntityKey").entityName("cache entity name");
        List<TransformationAttributeDTO> transformationAttributeDTOList = createTransformationAttributeDTO();
        List<TransformationDTO> transformationDTOList = createCachedEntityTransformation();
        TransformationContextEntityDTO TransformationContextEntityDTO1 = new TransformationContextEntityDTO().schemaStoreKey("CRM").entityStoreKey("MTM_BUS_ORG95_CUSTOMER1");
        TransformationContextEntityDTO TransformationContextEntityDTO2 = new TransformationContextEntityDTO().schemaStoreKey("CRM").entityStoreKey("MTM_BUS_ORG95_CUSTOMER1");
        transformationDTOList.get(0).getReferenceSourceEntities().add(TransformationContextEntityDTO1);
        transformationDTOList.get(0).getReferenceSourceEntities().add(TransformationContextEntityDTO2);
        entityTransformationDTOExpected.setAttributes(transformationAttributeDTOList);
        entityTransformationDTOExpected.setTransformations(transformationDTOList);

        ApiException e = assertThrows(ApiException.class, () -> cachedEntityTransformationServiceImpl.create(PROJECT_KEY, entityTransformationDTOExpected));
        assertEquals(1, e.getMessageParams().length);
        assertEquals("general.duplicate.attribute.key", e.getMessageKey().getMessageKey());
    }

    private List<TransformationDTO> createCachedEntityTransformation() {
        List<TransformationDTO> transformationDTOList = new ArrayList<>();
        final File file = FileUtils.getFile(TRANSFORMATION_FOR_CACHED_ENTITY_TRANSFORMATION);
        TransformationDTO transformationDTO = readValue(file, TransformationDTO.class);
        transformationDTOList.add(transformationDTO);
        return transformationDTOList;
    }

    private List<ContextDTO> createContextDTO() {
        List<ContextDTO> contextDTOList = new ArrayList<>();
        final File file = FileUtils.getFile(CONTEXTDTO_FOR_CACHED_ENTITY_TRANSFORMATION);
        ContextDTO contextDTO = readValue(file, ContextDTO.class);
        contextDTOList.add(contextDTO);
        return contextDTOList;
    }

    private List<TransformationAttributeDTO> createTransformationAttributeDTO() {
        List<TransformationAttributeDTO> transformationAttributeDTOList = new ArrayList<>();
        final File file = FileUtils.getFile(TRANSFORMATION_ATTRIBUTE_FOR_CACHED_ENTITY_TRANSFORMATION);
        TransformationAttributeDTO transformationAttributeDTO = readValue(file, TransformationAttributeDTO.class);
        transformationAttributeDTOList.add(transformationAttributeDTO);
        return transformationAttributeDTOList;
    }

    private static CacheReferenceEntityDTO createMockForCacheReferenceEntityDTO(String cacheReferenceEntityKey) {
        CacheReferenceEntityDTO mockCacheReferenceEntityDTO = new CacheReferenceEntityDTO();
        mockCacheReferenceEntityDTO
                .cacheReferenceEntityKey(cacheReferenceEntityKey)
                .cacheReferenceEntityName("cache entity")
                .description("description")
                .cacheReferenceAttributes(getAttributes());

        return mockCacheReferenceEntityDTO;
    }

    private static List<CacheReferenceAttributeDTO> getAttributes(){
        CacheReferenceAttributeDTO cacheReferenceAttributeDTO1 = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO1.setAttributeKey("attributeKey1");
        cacheReferenceAttributeDTO1.setAttributeName("attribute Key1");
        cacheReferenceAttributeDTO1.setKeyPosition(1);
        cacheReferenceAttributeDTO1.setType("type");

        CacheReferenceAttributeDTO cacheReferenceAttributeDTO2 = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO2.setAttributeKey("attributeKey2");
        cacheReferenceAttributeDTO2.setAttributeName("attribute Key2");
        cacheReferenceAttributeDTO2.setKeyPosition(0);
        cacheReferenceAttributeDTO2.setType("type");

        List<CacheReferenceAttributeDTO> cacheReferenceAttributeDTOS = new ArrayList<>();
        cacheReferenceAttributeDTOS.add(cacheReferenceAttributeDTO1);
        cacheReferenceAttributeDTOS.add(cacheReferenceAttributeDTO2);

        return cacheReferenceAttributeDTOS;
    }

    private static <T> T readValue(final File file, final Class<T> valueType) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(file, valueType);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
