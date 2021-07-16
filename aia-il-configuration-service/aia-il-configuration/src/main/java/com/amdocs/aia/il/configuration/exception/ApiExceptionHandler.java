package com.amdocs.aia.il.configuration.exception;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.core.web.AiaApiExceptionHandler;
import com.amdocs.aia.common.core.web.AiaApiMessage;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.repo.client.AiaRepositoryClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
public class ApiExceptionHandler extends AiaApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handlePublisherApiException(final ApiException e, final Locale locale)
            throws JsonProcessingException {
        LOGGER.debug("Publisher api error", e);
        final AiaApiException aiaException = new AiaApiException();
        aiaException
                .statusCode(e.getStatusCode())
                .message(new AiaApiMessage(e.getMessageKey(), e.getMessageParams()))
                .originalException(e);
        return (ResponseEntity<Object>) handleApiException(aiaException, locale);
    }

    @ExceptionHandler(AiaRepositoryClientException.class)
    public ResponseEntity<Object> handleAiaRepositoryClientException(final AiaRepositoryClientException e, final Locale locale)
            throws JsonProcessingException {
        LOGGER.debug("client error", e);
        final AiaApiException aiaException = new AiaApiException();
        aiaException
                .statusCode(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST)
                .message(new AiaApiMessage(AiaApiMessages.GENERAL.REPOSITORY_ERROR, e.getMessage()))
                .originalException(e);
        return (ResponseEntity<Object>)handleApiException(aiaException, locale);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefault(Exception e, Locale locale) throws JsonProcessingException {
        LOGGER.error("Internal error", e);
        final AiaApiException aiaException = new AiaApiException();
        aiaException
                .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                .message(new AiaApiMessage(AiaApiMessages.GENERAL.UNEXPECTED_SERVER_ERROR))
                .originalException(e);
        return (ResponseEntity<Object>) handleApiException(aiaException, locale);
    }


}
