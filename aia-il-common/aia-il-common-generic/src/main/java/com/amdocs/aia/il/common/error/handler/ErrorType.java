package com.amdocs.aia.il.common.error.handler;

public class ErrorType {
    private String errorMessage;
    private Class<?> exceptionType;

    public ErrorType(String errorMessage, Class<?> exceptionType) {
        this.errorMessage = errorMessage;
        this.exceptionType = exceptionType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Class<?> getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(Class<?> exceptionType) {
        this.exceptionType = exceptionType;
    }
}
