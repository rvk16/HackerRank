package com.amdocs.aia.il.configuration.exception;

import com.amdocs.aia.common.core.utils.ValidityStatus;
import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.repo.client.AiaRepositoryClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ApiExceptionHandlerUnitTest {

    @InjectMocks
    private ApiExceptionHandler apiExceptionHandler;

    @Mock
    private MessageSource messageSource;

    @Test
    void when_handlePublisherApiException_shouldReturn() throws Exception {
        doReturn("custom message").when(messageSource).getMessage(Mockito.anyString(), Mockito.any(), Mockito.any());
        ResponseEntity<Object> response = apiExceptionHandler.handlePublisherApiException(getPublisherApiException(), Locale.ENGLISH);
        assertNotNull(response.getBody());
    }

    @Test
    void when_handlePublisherApiException_shouldReturn_withAlternativeCauseException() throws Exception {
        doReturn("custom message").when(messageSource).getMessage(Mockito.anyString(), Mockito.any(), Mockito.any());
        ResponseEntity<Object> response = apiExceptionHandler.handlePublisherApiException(getPublisherApiExceptionAlternative(), Locale.ENGLISH);
        assertNotNull(response.getBody());
    }

    @Test
    void when_handleAiaRepositoryClientException_shouldReturn() throws Exception {
        doReturn("custom message").when(messageSource).getMessage(Mockito.anyString(), Mockito.any(), Mockito.any());
        ResponseEntity<Object> response = apiExceptionHandler.handleAiaRepositoryClientException(getAiaRepositoryClientException(), Locale.ENGLISH);
        assertNotNull(response.getBody());
    }

    @Test
    void when_handleDefault_shouldReturn() throws Exception {
        doReturn("custom message").when(messageSource).getMessage(Mockito.anyString(), Mockito.any(), Mockito.any());
        ResponseEntity<Object> response = apiExceptionHandler.handleDefault(new Exception(), Locale.ENGLISH);
        assertNotNull(response.getBody());
    }

    private ApiException getPublisherApiException() {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST,
                new RuntimeException("test exception"), "elementType", "elementKey");
    }

    private ApiException getPublisherApiExceptionAlternative() {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST,
                new ValidityStatus(), "elementType", "elementKey");
    }

    private AiaRepositoryClientException getAiaRepositoryClientException() {
        return new AiaRepositoryClientException("custom message");
    }

}
