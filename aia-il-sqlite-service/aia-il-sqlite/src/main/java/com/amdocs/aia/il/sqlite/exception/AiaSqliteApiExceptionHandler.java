package com.amdocs.aia.il.sqlite.exception;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.core.web.AiaApiExceptionHandler;
import com.amdocs.aia.common.core.web.AiaApiMessage;
import com.amdocs.aia.il.sqlite.message.AiaApiMessages;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
public class AiaSqliteApiExceptionHandler extends AiaApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiaSqliteApiExceptionHandler.class);

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
