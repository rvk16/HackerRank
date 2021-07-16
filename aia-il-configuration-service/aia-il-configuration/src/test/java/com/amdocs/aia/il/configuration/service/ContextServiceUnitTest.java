package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AIAAPIMessageTemplate;
import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.repo.ValidationResponseDTO;
import com.amdocs.aia.common.model.repo.ValidationStatus;
import com.amdocs.aia.il.common.model.configuration.transformation.Context;
import com.amdocs.aia.il.common.model.configuration.transformation.ContextEntity;
import com.amdocs.aia.il.common.model.configuration.transformation.ContextEntityRelationType;
import com.amdocs.aia.il.common.model.configuration.transformation.NoReferentAction;
import com.amdocs.aia.il.configuration.dependencies.ContextDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.ContextDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.mapper.ContextMapper;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.ContextRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContextServiceUnitTest {
    private static final String PROJECT_KEY = "aia";

    @InjectMocks
    private ContextConfigurationServiceImpl publisherContextConfigurationService;
    @Mock
    private ContextRepository publisherContextRepository;
    @Mock
    private ContextMapper contextMapper;
    @Mock
    private ContextDependencyAnalyzer contextDependencyAnalyzer;
    @Mock
    private MessageHelper messageHelper;

    @Test
    void whenGetContextRelationTypesList_ShouldReturnList() {
        List<String> relationTypesList = publisherContextConfigurationService.relationTypesList();
        assertEquals(6, relationTypesList.size());
    }

    @Test
    void whenGetPublisherContext_ShouldReturnDTO() {
        String contextKey = "Customer";
        publisherContextConfigurationService.setMapper(new ContextMapper());
        final Context context = createMockPublicContext(contextKey);
        when(publisherContextRepository.getByKey("projectKey", context.getKey())).thenReturn(context);
        final ContextDTO publisherContextDTO = publisherContextConfigurationService.get("projectKey", context.getKey());
        assertEquals(1, publisherContextDTO.getContextEntities().size());
        assertEquals(contextKey, publisherContextDTO.getContextKey());
    }

    @Test
    void whenGetPublisherContext_ShouldThrowException() {
        String contextKey = "Customer";
        publisherContextConfigurationService.setMapper(new ContextMapper());
        final Context context = createMockPublicContext(contextKey);
        doReturn(getPublisherApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST))
                .when(messageHelper).createObjectDoesNotExistException(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        final ApiException ex = assertThrows(ApiException.class, () -> publisherContextConfigurationService.get("projectKey", context.getKey()));
        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void whenDoingSaveForPublisherContext_existingModelIdIsNotNull() {
        final ContextDTO publisherContextDTO = new ContextDTO();
        final Context context = createMockPublicContext("customer");
        doReturn(getPublisherApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST))
                .when(messageHelper).createObjectAlreadyExistException(Mockito.anyString(), Mockito.anyString());
        ApiException ex = assertThrows(ApiException.class, () -> publisherContextConfigurationService.doSave(PROJECT_KEY, publisherContextDTO, context));

        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
        assertEquals(AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST, ex.getMessageKey());
    }

    @Test
    void whenIsPublisherContextExist_shouldReturnTrue() {
        final Context context = createMockPublicContext("customer");
        doReturn(context).when(publisherContextRepository).getByKey(Mockito.anyString(), Mockito.anyString());
        boolean isPublisherContextExist = publisherContextConfigurationService.isPublisherContextExist(PROJECT_KEY, context.getContextKey());

        assertTrue(isPublisherContextExist);
    }

    @Test
    void whenDeletePublisherContext_ShouldDeleteFromRepository() {
        String contextKey = "Customer";
        publisherContextConfigurationService.setMapper(new ContextMapper());
        final Context context = createMockPublicContext(contextKey);
        context.setProductKey("productKey");
        //     String viewId = ModelUtils.generateGlobalUniqueId(publisherContext, publisherContext.getKey());
        when(publisherContextRepository.getByKey("projectKey", context.getKey())).thenReturn(context);
        publisherContextConfigurationService.delete("projectKey", context.getKey());
        Mockito.verify(publisherContextRepository, times(1)).delete(context.getKey());
    }

    @Test
    void whenDeletePublisherContext_ShouldThrowException() {
        String contextKey = "Customer";
        publisherContextConfigurationService.setMapper(new ContextMapper());
        final Context context = createMockPublicContext(contextKey);
        context.setProductKey("productKey");
        ApiException exceptionToThrow = new ApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST,
                "element.type.publisherContext",
                context.getPublisherName());
        when(messageHelper.createObjectDoesNotExistException(anyString(), any(), any())).thenReturn(exceptionToThrow);
        try {
            publisherContextConfigurationService.delete("projectKey", context.getKey());
            fail("PublisherApiException should have been thrown");
        } catch (ApiException e) {
            assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, e.getStatusCode());
        }
    }

    @Test
    void whenUpdatePublisherContext() {
        String contextKey = "Customer";
        publisherContextConfigurationService.setMapper(new ContextMapper());
        final Context publisherContext = createMockPublicContext(contextKey);
        publisherContext.setProductKey("IL");
        when(publisherContextRepository.getByKey("projectKey", publisherContext.getKey())).thenReturn(publisherContext);
        final ContextDTO publisherContextDTO = publisherContextConfigurationService.get("projectKey", publisherContext.getKey());
        when(publisherContextRepository.getByKey(eq(publisherContext.getProjectKey()), eq(publisherContext.getPublisherName()))).thenReturn(publisherContext);
        when(publisherContextRepository.validateBeforeSave(any())).thenReturn(new ValidationResponseDTO(ValidationStatus.OK, 0, false, Collections.emptyList()));
        when(publisherContextRepository.save(any())).thenReturn(publisherContext);
        publisherContextConfigurationService.update(publisherContext.getProjectKey(), publisherContext.getPublisherName(), publisherContextDTO);
        assertEquals("aia", publisherContext.getProjectKey());
        assertNotNull(publisherContext);
    }

    @Test
    void whenUpdatePublisherContextThrowException() {
        String contextKey = "Customer";
        publisherContextConfigurationService.setMapper(new ContextMapper());
        final Context context = createMockPublicContext(contextKey);
        when(publisherContextRepository.getByKey("projectKey", context.getKey())).thenReturn(context);
        final ContextDTO publisherContextDTO = publisherContextConfigurationService.get("projectKey", context.getKey());
        ApiException exceptionToThrow = new ApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.ENTITY_DOES_NOT_EXIST,
                "element.type.publisherContext",
                context.getPublisherName());
        when(publisherContextRepository.getByKey(eq(context.getProjectKey()), eq(context.getPublisherName()))).thenReturn(null);
        when(messageHelper.createElementNotFoundException(any(), any())).thenReturn(exceptionToThrow);
        try {
            publisherContextConfigurationService.update(context.getProjectKey(), context.getPublisherName(), publisherContextDTO);
            fail("PublisherApiException should have been thrown");
        } catch (ApiException sve) {
            assertEquals(AiaApiMessages.GENERAL.ENTITY_DOES_NOT_EXIST, sve.getMessageKey());
        }
    }

    @Test
    void whenDoUpdate_withIdNull_shouldThrowException() {
        String contextKey = "Customer";
        publisherContextConfigurationService.setMapper(new ContextMapper());
        final Context context = createMockPublicContext(contextKey);
        context.setId(null);
        doReturn(getPublisherApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.ID_NOT_SET))
                .when(messageHelper).createIDNotSetException(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        ApiException ex = assertThrows(ApiException.class,
                () -> publisherContextConfigurationService.doUpdate(PROJECT_KEY, context.getKey(), new ContextDTO(), context));

        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
        assertEquals(AiaApiMessages.GENERAL.ID_NOT_SET, ex.getMessageKey());
    }

    @Test
    void whenDoUpdate_withContextKeyNull_shouldThrowException() {
        String contextKey = "Customer";
        publisherContextConfigurationService.setMapper(new ContextMapper());
        final Context context = createMockPublicContext(contextKey);
        context.setContextKey(null);
        doReturn(getPublisherApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST))
                .when(messageHelper).createObjectDoesNotExistException(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        ApiException ex = assertThrows(ApiException.class,
                () -> publisherContextConfigurationService.doUpdate(PROJECT_KEY, context.getKey(), new ContextDTO(), context));

        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
        assertEquals(AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST, ex.getMessageKey());
    }


    public static Context createMockPublicContext(String entity) {
        Context context = new Context();
        context.setId(entity + "_id");
        context.setContextKey(entity + "Context");
        context.setKey(context.getContextKey());
        context.setProjectKey(PROJECT_KEY);
        context.setContextEntities(makePublisherContextEntities());
        context.setDescription(entity + "description");
        context.setPublisherName(context.getContextKey());
        return context;
    }

    private static List<ContextEntity> makePublisherContextEntities() {
        List<ContextEntity> publisherContextEntities = new ArrayList<>();
        ContextEntity contextEntity = new ContextEntity();
        contextEntity.setRelationType(ContextEntityRelationType.LEAD);
        contextEntity.setSourceAlias("C");
        contextEntity.setNoReferentAction(NoReferentAction.MANDATORY);
        contextEntity.setForeignKeys("Customer");
        contextEntity.setEntityStoreKey("Address");
        contextEntity.setParentContextEntityKey("Customer");
        contextEntity.setSchemaStoreKey("BCMAPP");
        contextEntity.setAliasedSourceEntityKey("C-Customer");
        contextEntity.setDoPropagation(true);
        publisherContextEntities.add(contextEntity);
        return publisherContextEntities;
    }

    private static ApiException getPublisherApiException(AiaApiException.AiaApiHttpCodes status, AIAAPIMessageTemplate message) {
        return new ApiException(status, message, "display_name", "element_key");
    }
}
