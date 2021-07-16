package com.amdocs.aia.il.busdeployer.exception;

public class KafkaTopicsCreatorException extends RuntimeException {
    private static final long serialVersionUID = 1351152824219500852L;

    public KafkaTopicsCreatorException(Throwable t) {
        super(t);
    }

    public KafkaTopicsCreatorException(String s) {
        super(s);
    }
}