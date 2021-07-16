package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.logical.LogicalEntity;
import com.amdocs.aia.common.model.logical.LogicalEntityType;
import com.amdocs.aia.common.model.logical.LogicalSchema;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.TransformationRepository;
import com.amdocs.aia.shared.client.AiaSharedOperations;
import org.junit.jupiter.api.BeforeEach;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SharedEntityTransformationServiceUnitTest {
    private static final String PROJECT_KEY = "aia";
    private static final String CONTEXTDTO_FOR_SHARED_ENTITY_TRANSFORMATION = "src/test/resources/json/contextDTO_for_sharedEntityTransformation.json";
    private static final String TRANSFORMATION_ATTRIBUTE_FOR_SHARED_ENTITY_TRANSFORMATION = "src/test/resources/json/TransformationAttribute_for_sharedEntityTransformation.json";
    private static final String TRANSFORMATION_FOR_SHARED_ENTITY_TRANSFORMATION = "src/test/resources/json/transformation_for_sharedEntityTransformation.json";

    @Mock
    private ILOperations ilOperations;

    @Mock
    private DataChannelStoreType dataChannelStoreType;

    @Mock
    private AiaSharedOperations aiaSharedOperations;

    @Mock
    private MessageHelper messageHelper;

    @InjectMocks
    private SharedEntityTransformationServiceImpl sharedEntityTransformationService;

    @Mock
    private TransformationRepository publisherTransformationRepository;

    @BeforeEach
    void initMessageHelper() {
        when(messageHelper.createElementNotFoundException(anyString(), anyString()))
                .then(ctx -> new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.ENTITY_DOES_NOT_EXIST,
                        ctx.getArgument(0, String.class), ctx.getArgument(1, String.class)));
    }

    @Test
    void whenGetSharedEntityTransformationList_ShouldReturnDTO() {
        final List<TransformationDTO> transformationDTOList = createSharedEntityTransformation();
        final LogicalEntity logicalEntities = createLogicalEntity(PROJECT_KEY);
        final List<LogicalEntity> logicalEntitiesList = Collections.singletonList(logicalEntities);
        final LogicalSchema logicalSchema = createLogicalSchema(PROJECT_KEY);
        doReturn(transformationDTOList).when(ilOperations).listTransformations(eq(PROJECT_KEY));
        doReturn(Optional.of(logicalEntities)).when(aiaSharedOperations).searchLogicalEntityByEntityKeyAndSchemaKey(eq(PROJECT_KEY), eq("Customer"), eq("aLDMCustomer"));
        doReturn(Optional.of(logicalSchema)).when(aiaSharedOperations).searchLogicalSchemaBySchemaKey(eq(PROJECT_KEY), eq("aLDMCustomer"));
        doReturn(logicalEntitiesList).when(aiaSharedOperations).searchLogicalEntities(anyString(), anyString());

        List<SharedEntityTransformationGridElementDTO> sharedEntityTransformationGridElementDTOList = sharedEntityTransformationService.list(PROJECT_KEY);
        assertNotNull(sharedEntityTransformationGridElementDTOList);
        assertEquals(1, sharedEntityTransformationGridElementDTOList.size());
        assertEquals("Customer", sharedEntityTransformationGridElementDTOList.get(0).getEntityKey());
    }


    @Test
    void whenGetSharedEntityTransformationDTO_logicalModelDoesNotExist_ShouldThrowException() {
        final String logicalEntityKey = "LGE";
        doReturn(Collections.emptyList()).when(aiaSharedOperations).searchLogicalEntities(anyString(), anyString());
        ApiException e = assertThrows(ApiException.class, () -> sharedEntityTransformationService.get(PROJECT_KEY, logicalEntityKey));
        assertEquals(2, e.getMessageParams().length);
        assertEquals(logicalEntityKey, e.getMessageParams()[1]);
    }

    @Test
    void whenUpdate_entityStoreDoesNotExist_ShouldThrowException() {
        final String logicalEntityKey = "LGE";
        final String entityStoreKey = "ENS";
        final String logicalSchemaKey = "LS";
        final String schemaStoreKey = "SCS";
        final EntityTransformationDTO dto = new EntityTransformationDTO().logicalEntityKey(logicalEntityKey).logicalSchemaKey(logicalSchemaKey);
        LogicalEntity logicalEntity = mock(LogicalEntity.class);
        LogicalSchema logicalSchema = mock(LogicalSchema.class);
        doReturn(logicalSchemaKey).when(logicalSchema).getSchemaKey();
        doNothing().when(logicalEntity).setAttributes(anyList());
        doReturn(logicalSchemaKey).when(logicalEntity).getSchemaKey();
        doReturn(logicalEntityKey).when(logicalEntity).getEntityKey();
        doReturn(Optional.of(logicalEntity)).when(aiaSharedOperations).searchLogicalEntityByEntityKeyAndSchemaKey(anyString(), anyString(), anyString());
        doReturn(Optional.of(new SchemaStore())).when(aiaSharedOperations).searchSchemaStoreBySchemaKey(anyString(), anyString());
        doReturn(Optional.of(logicalSchema)).when(aiaSharedOperations).searchLogicalSchemaBySchemaKey(anyString(), anyString());
        doReturn(Optional.empty()).when(aiaSharedOperations).searchEntityStoreByEntityKeyAndSchemaStore(anyString(), anyString(), anyString());
        doReturn(schemaStoreKey).when(dataChannelStoreType).generateSchemaStoreKeyForLogical(eq(logicalSchemaKey));
        doReturn(entityStoreKey).when(dataChannelStoreType).generateEntityStoreKeyForLogical(eq(logicalEntityKey));
        ApiException e = assertThrows(ApiException.class, () -> sharedEntityTransformationService.update(PROJECT_KEY, dto));
        assertEquals(2, e.getMessageParams().length);
        assertEquals(entityStoreKey, e.getMessageParams()[1]);
    }

    @Test
    void whenCreateOrUpdate_logicalModelDoesNotExist_ShouldThrowException() {
        final String logicalEntityKey = "LGE";
        final EntityTransformationDTO dto = new EntityTransformationDTO().logicalEntityKey(logicalEntityKey);
        doReturn(Optional.empty()).when(aiaSharedOperations).searchLogicalEntityByEntityKeyAndSchemaKey(anyString(), anyString(), anyString());
        ApiException e = assertThrows(ApiException.class, () -> sharedEntityTransformationService.create(PROJECT_KEY, dto));
        assertEquals(2, e.getMessageParams().length);
        assertEquals(logicalEntityKey, e.getMessageParams()[1]);
        e = assertThrows(ApiException.class, () -> sharedEntityTransformationService.update(PROJECT_KEY, dto));
        assertEquals(2, e.getMessageParams().length);
        assertEquals(logicalEntityKey, e.getMessageParams()[1]);
    }

    @Test
    void whenCreateOrUpdate_schemaStoreDoesNotExist_ShouldThrowException() {
        final String logicalEntityKey = "LGE";
        final String schemaStoreKey = "SCS";
        final EntityTransformationDTO dto = new EntityTransformationDTO().logicalEntityKey(logicalEntityKey).logicalSchemaKey(schemaStoreKey);
        LogicalEntity logicalEntity = mock(LogicalEntity.class);
        doNothing().when(logicalEntity).setAttributes(anyList());
        doReturn(schemaStoreKey).when(logicalEntity).getSchemaKey();
        doReturn(Optional.of(logicalEntity)).when(aiaSharedOperations).searchLogicalEntityByEntityKeyAndSchemaKey(anyString(), anyString(), anyString());
        doReturn(Optional.empty()).when(aiaSharedOperations).searchSchemaStoreBySchemaKey(anyString(), anyString());
        ApiException e = assertThrows(ApiException.class, () -> sharedEntityTransformationService.create(PROJECT_KEY, dto));
        assertEquals(2, e.getMessageParams().length);
        assertEquals(schemaStoreKey, e.getMessageParams()[1]);
        e = assertThrows(ApiException.class, () -> sharedEntityTransformationService.update(PROJECT_KEY, dto));
        assertEquals(2, e.getMessageParams().length);
        assertEquals(schemaStoreKey, e.getMessageParams()[1]);
    }

    @Test
    void whenDeleteSharedEntityTransformationDTO_ShouldReturn200OK() {
        List<ContextDTO> contextDTOList = createContextDTO();
        List<LogicalEntity> logicalEntities  = Collections.singletonList(createLogicalEntity(PROJECT_KEY));
        List<TransformationAttributeDTO> transformationAttributeDTOList = createTransformationAttributeDTO();
        List<TransformationDTO> transformationDTOList = createSharedEntityTransformation();
        List<EntityStore> entityStores = Collections.singletonList(createEntityStore());

        doReturn(transformationDTOList).when(ilOperations).listTransformations(eq(PROJECT_KEY));
        doReturn(contextDTOList).when(ilOperations).listContexts(eq(PROJECT_KEY));
        doReturn(logicalEntities).when(aiaSharedOperations).searchLogicalEntities(eq(PROJECT_KEY), eq(String.format("entityKey:%s", "Customer")));
        doReturn(transformationAttributeDTOList).when(ilOperations).listTransformationAttributes(eq(PROJECT_KEY),eq("aLDMCustomer"),eq("Customer"));
        doReturn(entityStores).when(aiaSharedOperations).searchEntityStores(eq(PROJECT_KEY), eq(String.format("logicalEntityKey:%s AND storeType:" + "DATA_CHANNEL", "Customer")));

        sharedEntityTransformationService.delete(PROJECT_KEY, "Customer");

        verify(ilOperations, times(1)).deleteEntityTransformations(PROJECT_KEY, "aLDMCustomerDataChannel", "Customer");
    }

    @Test
    void whenDeleteSharedEntityTransformationDTO_entityStoreDoesNotExist_ShouldThrowException() {
        final String logicalEntityKey = "LGE";
        final EntityTransformationDTO dto = new EntityTransformationDTO().logicalEntityKey(logicalEntityKey);
        doReturn(Collections.emptyList()).when(aiaSharedOperations).searchEntityStores(anyString(), anyString());
        ApiException e = assertThrows(ApiException.class, () -> sharedEntityTransformationService.delete(PROJECT_KEY, dto.getLogicalEntityKey()));
        assertEquals(2, e.getMessageParams().length);
        assertEquals(logicalEntityKey, e.getMessageParams()[1]);
    }

    private List<TransformationDTO> createSharedEntityTransformation() {
        List<TransformationDTO> transformationDTOList = new ArrayList<>();
        final File file = FileUtils.getFile(TRANSFORMATION_FOR_SHARED_ENTITY_TRANSFORMATION);
        TransformationDTO transformationDTO = readValue(file, TransformationDTO.class);
        transformationDTOList.add(transformationDTO);
        return transformationDTOList;
    }

    private LogicalEntity createLogicalEntity(String projectKey) {
        LogicalEntity logicalEntity = new LogicalEntity();
        logicalEntity.setProjectKey(projectKey);
        logicalEntity.setEntityKey("Customer");
        logicalEntity.setEntityType(LogicalEntityType.MASTER.name());
        logicalEntity.setName("Customer");
        logicalEntity.setSchemaKey("aLDMCustomer");
        return logicalEntity;
    }

    private LogicalSchema createLogicalSchema(String projectKey) {
        LogicalSchema logicalSchema = new LogicalSchema();
        logicalSchema.setProjectKey(projectKey);
        logicalSchema.setName("Customer");
        logicalSchema.setSchemaKey("aLDMCustomer");
        logicalSchema.setEntities(Collections.singletonList(createLogicalEntity(projectKey)));
        return logicalSchema;
    }

    private EntityStore createEntityStore() {
        EntityStore entityStoreMock1 =  new EntityStore();
        entityStoreMock1.setEntityStoreKey("Customer");
        entityStoreMock1.setSchemaStoreKey("aLDMCustomerDataChannel");
        entityStoreMock1.setLogicalEntityKey("Customer");
        return entityStoreMock1;
    }

    private List<ContextDTO> createContextDTO() {
        List<ContextDTO> contextDTOList = new ArrayList<>();
        final File file = FileUtils.getFile(CONTEXTDTO_FOR_SHARED_ENTITY_TRANSFORMATION);
        ContextDTO contextDTO = readValue(file, ContextDTO.class);
        contextDTOList.add(contextDTO);
        return contextDTOList;
    }

    private List<TransformationAttributeDTO> createTransformationAttributeDTO() {
        List<TransformationAttributeDTO> transformationAttributeDTOList = new ArrayList<>();
        final File file = FileUtils.getFile(TRANSFORMATION_ATTRIBUTE_FOR_SHARED_ENTITY_TRANSFORMATION);
        TransformationAttributeDTO transformationAttributeDTO = readValue(file, TransformationAttributeDTO.class);
        transformationAttributeDTOList.add(transformationAttributeDTO);
        return transformationAttributeDTOList;
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
