package com.amdocs.aia.il.configuration.exception;

import com.amdocs.aia.common.core.utils.ValidityStatus;
import com.amdocs.aia.common.core.web.AIAAPIMessageTemplate;
import com.amdocs.aia.common.core.web.AiaApiException;

public class ApiException extends RuntimeException {

    private static final long serialVersionUID = -2030046172400849455L;
    private final AiaApiException.AiaApiHttpCodes statusCode;
    private final AIAAPIMessageTemplate messageKey;
    private final String[] messageParams;

    @SuppressWarnings("unused")
    private final ValidityStatus validityStatus;

    public ApiException(AiaApiException.AiaApiHttpCodes statusCode, AIAAPIMessageTemplate messageKey, String... messageParams) {
        this.statusCode = statusCode;
        this.messageKey = messageKey;
        this.messageParams = messageParams;
        validityStatus = null;
    }

    public ApiException(AiaApiException.AiaApiHttpCodes statusCode, AIAAPIMessageTemplate messageKey, Throwable cause, String... messageParams) {
        super(cause);
        this.statusCode = statusCode;
        this.messageKey = messageKey;
        this.messageParams = messageParams;
        validityStatus = null;
    }

    public ApiException(AiaApiException.AiaApiHttpCodes statusCode, AIAAPIMessageTemplate messageKey, ValidityStatus validityStatus, String... messageParams) {
        this.statusCode = statusCode;
        this.messageKey = messageKey;
        this.messageParams = messageParams;
        this.validityStatus  = validityStatus;
    }

    public AiaApiException.AiaApiHttpCodes getStatusCode() {
        return statusCode;
    }

    public AIAAPIMessageTemplate getMessageKey() {
        return messageKey;
    }

    public String[] getMessageParams() {
        return messageParams;
    }
}
