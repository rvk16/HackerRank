package com.amdocs.aia.il.configuration.service;


import com.amdocs.aia.common.core.web.AIAAPIMessageTemplate;
import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.repo.ValidationResponseDTO;
import com.amdocs.aia.common.model.repo.ValidationStatus;
import com.amdocs.aia.il.common.model.configuration.transformation.Transformation;
import com.amdocs.aia.il.configuration.dependencies.TransformationDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.TransformationDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.mapper.ContextMapper;
import com.amdocs.aia.il.configuration.mapper.TransformationMapper;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.TransformationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransformationServiceUnitTest {
    private static final String PROJECT_KEY = "aia";

    @InjectMocks
    private TransformationConfigurationServiceImpl publisherTransformationConfigurationService;
    @Mock
    private TransformationRepository publisherTransformationRepository;
    @Mock
    private MessageHelper messageHelper;
    @Mock
    private TransformationMapper mapper;
    @Mock
    private TransformationDependencyAnalyzer transformationDependencyAnalyzer;
    @Mock
    private ContextMapper contextMapper;

    @Test
    void whenGetPublisherTransformation_ShouldReturnDTO() {
        String schema = "aLDMCustomer";
        String entity = "Customer";
        publisherTransformationConfigurationService.setMapper(new TransformationMapper());
        final Transformation transformation = createMockPublisherTransformation(schema, entity);
        when(publisherTransformationRepository.getByKey("projectKey", transformation.getKey())).thenReturn(transformation);
        final TransformationDTO publisherTransformationDTO = publisherTransformationConfigurationService.get("projectKey", transformation.getKey());
        assertEquals(entity, publisherTransformationDTO.getTargetEntityStoreKey());
        assertEquals(schema, publisherTransformationDTO.getTargetSchemaStoreKey());
        assertEquals(entity + "PublisherContext", publisherTransformationDTO.getContextKey());
    }

    @Test
    void whenGetPublisherTransformation_ShouldThrowException() {
        String schema = "aLDMCustomer";
        String entity = "Customer";
        publisherTransformationConfigurationService.setMapper(new TransformationMapper());
        final Transformation transformation = createMockPublisherTransformation(schema, entity);
        ApiException exceptionToThrow = new ApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST,
                "element.type.publisherTransformation",
                transformation.getPublisherName());
        when(messageHelper.createObjectDoesNotExistException(anyString(), any(), any())).thenReturn(exceptionToThrow);

        try {
            publisherTransformationConfigurationService.get("projectKey", transformation.getKey());
            fail("PublisherApiException should have been thrown");
        } catch (ApiException e) {
            assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, e.getStatusCode());
        }
    }

    @Test
    void whenGetPublisherTransformation_ShouldThrowIDNotSet() {
        doReturn(getPublisherApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.ID_NOT_SET))
                .when(messageHelper).createIDNotSetException(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        ApiException ex = assertThrows(ApiException.class, () -> publisherTransformationConfigurationService.get(PROJECT_KEY, ""));
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }

    @Test
    void whenGetPublisherTransformation_ShouldThrowUnexpectedOperation() {
        when(publisherTransformationRepository.getByKey(Mockito.anyString(), Mockito.anyString())).thenThrow(new RuntimeException());
        doReturn(getPublisherApiException(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR, AiaApiMessages.GENERAL.UNEXPECTED_OPERATION_ERROR))
                .when(messageHelper).createUnexpectedOperationErrorException(Mockito.anyString(), Mockito.any(Exception.class));
        ApiException ex = assertThrows(ApiException.class, () -> publisherTransformationConfigurationService.get(PROJECT_KEY, "project_id"));
        assertEquals(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR, ex.getStatusCode());
    }

    @Test
    void whenGetPublisherTransformation_ShouldThrowDoesNotExist() {
        when(publisherTransformationRepository.getByKey(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        doReturn(getPublisherApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST))
                .when(messageHelper).createObjectDoesNotExistException(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        ApiException ex = assertThrows(ApiException.class, () -> publisherTransformationConfigurationService.get(PROJECT_KEY, "project_id"));
        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void canList_whenReturnListNotEmpty() {
        final Transformation transformation = createMockPublisherTransformation("schema", "entity");
        when(publisherTransformationRepository.findAllPublisherTransformation(Mockito.anyString())).thenReturn(Collections.singletonList(transformation));
        when(mapper.toDTO(Mockito.any(Transformation.class))).thenReturn(new TransformationDTO());
        List<TransformationDTO> list = publisherTransformationConfigurationService.list(PROJECT_KEY);

        assertEquals(1, list.size());
    }

    @Test
    void canList_whenReturnListEmpty() {
        final Transformation transformation = createMockPublisherTransformation("schema", "entity");
        when(publisherTransformationRepository.findAllPublisherTransformation(Mockito.anyString())).thenReturn(Collections.emptyList());
        List<TransformationDTO> list = publisherTransformationConfigurationService.list(PROJECT_KEY);

        assertTrue(list.isEmpty());
    }

    @Test
    void whenDoSave_withIdNotNull_shouldThrowAlreadyExists() {
        final Transformation transformation = createMockPublisherTransformation("schema", "entity");
        transformation.setId("project_id");
        doReturn(getPublisherApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST))
                .when(messageHelper).createElementAlreadyExistsException(Mockito.anyString(), Mockito.anyString());
        ApiException ex = assertThrows(ApiException.class,
                () -> publisherTransformationConfigurationService.doSave(PROJECT_KEY, new TransformationDTO(), transformation));

        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
        assertEquals(AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST, ex.getMessageKey());
    }

    @Test
    void when_findByTargetSchemaStoreAndTargetEntityStore_shouldReturn() {
        final Transformation transformation = createMockPublisherTransformation("schema", "entity");
        when(publisherTransformationRepository.findPublisherTransformationByKeys(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(transformation);
        when(mapper.toDTO(Mockito.any(Transformation.class))).thenReturn(new TransformationDTO());
        assertNotNull(publisherTransformationConfigurationService.findByTargetSchemaStoreAndTargetEntityStore("projectKey", "targetSchemaStoreKey", "targetEntityStoreKey", "key"));
    }

    @Test
    void when_findByTargetSchemaStoreAndTargetEntityStore_shouldThrowDoesNotExist() {
        final Transformation transformation = createMockPublisherTransformation("schema", "entity");
        when(publisherTransformationRepository.findPublisherTransformationByKeys(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new RuntimeException());
        doReturn(getPublisherApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST))
                .when(messageHelper).createObjectDoesNotExistException(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        ApiException ex = assertThrows(ApiException.class,
                () -> publisherTransformationConfigurationService.findByTargetSchemaStoreAndTargetEntityStore("projectKey", "targetSchemaStoreKey", "targetEntityStoreKey", "key"));

        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void whenDoingSaveForPublisherTransformation_existingModelIdIsNotNull() {
        final TransformationDTO publisherTransformationDTO = new TransformationDTO();
        String schema = "aLDMCustomer";
        String entity = "Customer";
        final Transformation transformation = createMockPublisherTransformation(schema, entity);
        ApiException exceptionToThrow = new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST,
                "element.type.publisherTransformation",
                transformation.getPublisherName());
        when(messageHelper.createElementAlreadyExistsException(Mockito.anyString(), Mockito.anyString())).thenReturn(exceptionToThrow);
        try {
            publisherTransformationConfigurationService.doSave(PROJECT_KEY, publisherTransformationDTO, transformation);
            fail("PublisherApiException should have been thrown");
        } catch (ApiException ex) {
            assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
            assertEquals(AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST, ex.getMessageKey());
        }
    }

    @Test
    void whenDeletePublisherTransformation_ShouldDeleteFromRepository() {
        String schema = "aLDMCustomer";
        String entity = "Customer";
        publisherTransformationConfigurationService.setMapper(new TransformationMapper());
        final Transformation transformation = createMockPublisherTransformation(schema, entity);
        transformation.setProductKey("IL");
        transformation.setId("id");
        //     String viewId = ModelUtils.generateGlobalUniqueId(publisherContext, publisherContext.getKey());
        when(publisherTransformationRepository.getByKey("projectKey", transformation.getKey())).thenReturn(transformation);
        publisherTransformationConfigurationService.delete("projectKey", transformation.getKey());
        verify(publisherTransformationRepository, times(1)).delete(transformation.getId());
    }

    @Test
    void whenDeletePublisherTransformation_ShouldThrowException() {
        String schema = "aLDMCustomer";
        String entity = "Customer";
        publisherTransformationConfigurationService.setMapper(new TransformationMapper());
        final Transformation transformation = createMockPublisherTransformation(schema, entity);
        ApiException exceptionToThrow = new ApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST,
                "element.type.publisherTransformation",
                transformation.getPublisherName());
        when(messageHelper.createObjectDoesNotExistException(anyString(), any(), any())).thenReturn(exceptionToThrow);
        try {
            publisherTransformationConfigurationService.delete("projectKey", transformation.getKey());
            fail("PublisherApiException should have been thrown");
        } catch (ApiException e) {
            assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, e.getStatusCode());
        }
    }

    @Test
    void whenUpdatePublisherTransformation() {
        String schema = "aLDMCustomer";
        String entity = "Customer";
        publisherTransformationConfigurationService.setMapper(new TransformationMapper());
        final Transformation transformation = createMockPublisherTransformation(schema, entity);
        transformation.setProductKey("IL");
        when(publisherTransformationRepository.getByKey("projectKey", transformation.getKey())).thenReturn(transformation);
        final TransformationDTO publisherTransformationDTO = publisherTransformationConfigurationService.get("projectKey", transformation.getKey());
        when(publisherTransformationRepository.getByKey(eq(transformation.getProjectKey()), eq(transformation.getPublisherName()))).thenReturn(transformation);
        when(publisherTransformationRepository.validateBeforeSave(any())).thenReturn(new ValidationResponseDTO(ValidationStatus.OK, 0, false, Collections.emptyList()));
        when(publisherTransformationRepository.save(any())).thenReturn(transformation);
        publisherTransformationConfigurationService.update(transformation.getProjectKey(), transformation.getPublisherName(), publisherTransformationDTO);
        assertEquals("aia", transformation.getProjectKey());
        assertNotNull(transformation);
    }

    @Test
    void whenUpdatePublisherTransformationThrowException() {
        String schema = "aLDMCustomer";
        String entity = "Customer";
        publisherTransformationConfigurationService.setMapper(new TransformationMapper());
        final Transformation transformation = createMockPublisherTransformation(schema, entity);
        when(publisherTransformationRepository.getByKey("projectKey", transformation.getKey())).thenReturn(transformation);
        final TransformationDTO publisherTransformationDTO = publisherTransformationConfigurationService.get("projectKey", transformation.getKey());
        ApiException exceptionToThrow = new ApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.ENTITY_DOES_NOT_EXIST,
                "element.type.publisherTransformation",
                transformation.getPublisherName());
        when(publisherTransformationRepository.getByKey(eq(transformation.getProjectKey()), eq(transformation.getPublisherName()))).thenReturn(null);
        when(messageHelper.createElementNotFoundException(any(), any())).thenReturn(exceptionToThrow);
        try {
            publisherTransformationConfigurationService.update(transformation.getProjectKey(), transformation.getPublisherName(), publisherTransformationDTO);
            fail("PublisherApiException should have been thrown");
        } catch (ApiException sve) {
            assertEquals(AiaApiMessages.GENERAL.ENTITY_DOES_NOT_EXIST, sve.getMessageKey());
        }
    }

    @Test
    void whenDoUpdate_withIdNull_shouldThrowException() {
        final Transformation transformation = createMockPublisherTransformation("schema", "entity");
        transformation.setId(null);
        doReturn(getPublisherApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.ID_NOT_SET))
                .when(messageHelper).createIDNotSetException(anyString(), anyString(), anyString());
        ApiException ex = assertThrows(ApiException.class,
                () -> publisherTransformationConfigurationService.doUpdate(PROJECT_KEY, transformation.getKey(), new TransformationDTO(), transformation));
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
        assertEquals(AiaApiMessages.GENERAL.ID_NOT_SET, ex.getMessageKey());
    }

    @Test
    void whenDoUpdate_withContextKeyNull_shouldThrowException() {
        final Transformation transformation = createMockPublisherTransformation("schema", "entity");
        transformation.setContextKey(null);
        doReturn(getPublisherApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST))
                .when(messageHelper).createObjectDoesNotExistException(anyString(), anyString(), anyString());
        ApiException ex = assertThrows(ApiException.class,
                () -> publisherTransformationConfigurationService.doUpdate(PROJECT_KEY, transformation.getKey(), new TransformationDTO(), transformation));

        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
        assertEquals(AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST, ex.getMessageKey());
    }

    private static Transformation createMockPublisherTransformation(String schema, String entity) {
        Transformation transformation = new Transformation();
        transformation.setContextKey(entity + "PublisherContext");
        transformation.setKey(transformation.getContextKey());
        transformation.setProjectKey(PROJECT_KEY);
        transformation.setTargetEntityStoreKey(entity);
        transformation.setTargetSchemaStoreKey(schema);
        transformation.setCustomScript("Select * from " + entity);
        transformation.setReferenceSourceEntities(new ArrayList<>());
        transformation.setDescription(entity + "description");
        transformation.setPublisherName(transformation.getContextKey());
        transformation.setId("id");
        return transformation;
    }

    private static ApiException getPublisherApiException(AiaApiException.AiaApiHttpCodes status, AIAAPIMessageTemplate message) {
        return new ApiException(status, message, "display_name", "element_key");
    }
}