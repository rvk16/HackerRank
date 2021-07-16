package com.amdocs.aia.il.configuration.message;

import com.amdocs.aia.common.core.utils.ValidityStatus;
import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.configuration.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MessageHelperUnitTest {

    @InjectMocks
    private MessageHelper messageHelper;

    @Mock
    private MessageSource messageSource;

    @Test
    void when_format_shouldReturn() {
        when(messageSource.getMessage(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn("message");
        assertEquals("message", messageHelper.format("key", new Object()));
    }

    @Test
    void when_createElementNotFoundException_shouldReturn() {
        ApiException ex = messageHelper.createElementNotFoundException("elementType", "elementKey");
        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void when_createElementsNotFoundException_shouldReturn() {
        ApiException ex = messageHelper.createElementsNotFoundException("elementType", "elementKey");
        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void when_createElementAlreadyExistsException_shouldReturn() {
        ApiException ex = messageHelper.createElementAlreadyExistsException("elementType", "elementKey");
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }

    @Test
    void when_createObjectAlreadyExistException_shouldReturn() {
        ApiException ex = messageHelper.createObjectAlreadyExistException("elementType", "elementKey");
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }

    @Test
    void when_createElementsAlreadyExistsException_shouldReturn() {
        ApiException ex = messageHelper.createElementsAlreadyExistsException("elementType", "elementKey");
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }

    @Test
    void when_invalidPrimaryKeyException_shouldReturn() {
        ApiException ex = messageHelper.invalidPrimaryKeyException("elementType", "elementKey");
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }

    @Test
    void when_invalidIndexColumnException_shouldReturn() {
        ApiException ex = messageHelper.invalidIndexColumnException("elementType", "elementKey", "table");
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }

    @Test
    void when_invalidIndexMissingNameException_shouldReturn() {
        ApiException ex = messageHelper.invalidIndexMissingNameException("elementType", "elementKey");
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }

    @Test
    void when_invalidIndexDuplicateNameException_shouldReturn() {
        ApiException ex = messageHelper.invalidIndexDuplicateNameException("elementType", "elementKey");
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }

    @Test
    void when_createIDNotSetException_shouldReturn() {
        ApiException ex = messageHelper.createIDNotSetException("elementType", "elementKey","elementMsg");
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }

    @Test
    void when_createObjectDoesNotExistException_shouldReturn() {
        ApiException ex = messageHelper.createObjectDoesNotExistException("elementType", "elementKey", "id");
        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void when_createUnexpectedOperationErrorException_shouldReturn() {
        ApiException ex = messageHelper.createUnexpectedOperationErrorException("elementType", new RuntimeException());
        assertEquals(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR, ex.getStatusCode());
    }

    @Test
    void when_createValidationException_shouldReturn() {
        ApiException ex = messageHelper.createValidationException(new ValidityStatus(), "elementKey");
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }

    @Test
    void when_atLeastOneColumnException_shouldReturn() {
        ApiException ex = messageHelper.atLeastOneColumnException("elementType", "elementKey");
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }


}
