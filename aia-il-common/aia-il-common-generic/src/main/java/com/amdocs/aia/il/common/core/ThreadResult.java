package com.amdocs.aia.il.common.core;

public class ThreadResult {
    private String errorMessage;
    private Class<? extends Exception> exceptionType;
    private Throwable throwable;

    public ThreadResult() {
        this.errorMessage = null;
    }

    public ThreadResult(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ThreadResult(String errorMessage, Class<? extends Exception> exceptionType) {
        this.errorMessage = errorMessage;
        this.exceptionType = exceptionType;
    }

    public boolean completedWithError() {
        return this.errorMessage != null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Class<? extends Exception> getExceptionType() { return exceptionType; }

    public void setExceptionType(Class<? extends Exception> exceptionType) { this.exceptionType = exceptionType; }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
