package com.amdocs.aia.il.common.error.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ErrorHandlerFactory {

    private ScyllaErrorHandler scyllaErrorHandler;
    private KafkaErrorHandler kafkaErrorHandler;

    public ScyllaErrorHandler getScyllaErrorHandler() {
        return scyllaErrorHandler;
    }

    @Autowired
    public void setScyllaErrorHandler(ScyllaErrorHandler scyllaErrorHandler) {
        this.scyllaErrorHandler = scyllaErrorHandler;
    }

    public KafkaErrorHandler getKafkaErrorHandler() {
        return kafkaErrorHandler;
    }

    @Autowired
    public void setKafkaErrorHandler(KafkaErrorHandler kafkaErrorHandler) {
        this.kafkaErrorHandler = kafkaErrorHandler;
    }

    public Handler getHandler(Class<?> exceptionType) {
        if (exceptionType == null) {
            return null;
        }
        if (scyllaErrorHandler.isScyllaException(exceptionType)) {
            return scyllaErrorHandler;
        }
        if (kafkaErrorHandler.isKafkaException(exceptionType)) {
            return kafkaErrorHandler;
        }

        return null;
    }

}
