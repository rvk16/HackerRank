package com.amdocs.aia.il.common.error.handler;


import com.amdocs.aia.il.common.publisher.ComputeLeadingProducerRecord;
import com.amdocs.aia.il.common.stores.scylla.monitor.DbMetrics;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    Map<String, Object> checkpoints = new ConcurrentHashMap<>();
    private boolean errorHandlerEnabled;
    private DbMetrics metrics;
    private ErrorHandlerFactory handlerFactory;

    public Map<String, Object> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(Map<String, Object> checkpoints) {
        this.checkpoints = checkpoints;
    }

    public boolean isErrorHandlerEnabled() {
        return errorHandlerEnabled;
    }

    public void setErrorHandlerEnabled(boolean errorHandlerEnabled) {
        this.errorHandlerEnabled = errorHandlerEnabled;
    }

    public ErrorHandlerFactory getHandlerFactory() {
        return handlerFactory;
    }

    @Autowired
    public void setHandlerFactory(ErrorHandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    public DbMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(DbMetrics metrics) {
        this.metrics = metrics;
    }

    public <T> Checkpoint<T> getCheckpoint(String threadName) {
        return (Checkpoint<T>) checkpoints.get(threadName);
    }

    //This will create a new checkpoint
    public <T> Checkpoint<T> initCheckpoint(String threadName, String methodName, final ThrowingFunction<T> function) {
        Checkpoint<T> checkpoint = new Checkpoint(threadName, methodName, function);
        checkpoints.put(threadName, checkpoint);
        return checkpoint;
    }

    //This will create a new checkpoint
    public Checkpoint<Void> initCheckpoint(String threadName, String methodName, final ThrowingOperation operation) {
        Checkpoint<Void> checkpoint = new Checkpoint(threadName, methodName, operation);
        checkpoints.put(threadName, checkpoint);
        return checkpoint;
    }

    //This will create a new checkpoint for kafka producer
    public Checkpoint<Void> initCheckpoint(String threadName, Producer<String, byte[]> producer) {
        if (getCheckpoint(threadName) != null) {
            return getCheckpoint(threadName);
        }
        Checkpoint<Void> checkpoint = new Checkpoint(threadName, producer);
        checkpoints.put(threadName, checkpoint);
        return checkpoint;
    }

    /**
     * This will check if error flag is set in Checkpoint.
     * If its set, it will check for exception type.
     * On basis of exceptionType, it will trigger handleMethod of appropriate ErrorHandler.e.g. ScyllaErrorHandler
     *
     * @param checkpoint
     * @return
     */
    public <T> T commitCheckpoint(Checkpoint<T> checkpoint) {
        LOGGER.debug("Checkpoint error flag : {}, errorHandlerEnabled : {}", checkpoint.isErrorFlag(), errorHandlerEnabled);
        while (this.errorHandlerEnabled && checkpoint.isErrorFlag()) {
            performRetry(checkpoint);
        }
        finishRetry(checkpoint);
        return checkpoint.getResult();
    }

    private <T> T performRetry(Checkpoint<T> checkpoint) {
        for (Map.Entry<String, ExecutionState> executionStatesMap : checkpoint.getExecutionStates().entrySet()) {
            for (Map.Entry<Object, ErrorType> failedTaskMap : executionStatesMap.getValue().getFailedTasks().entrySet()) {
                Handler handler = handlerFactory.getHandler(failedTaskMap.getValue().getExceptionType());
                LOGGER.debug("Handler {} ", handler);
                if (handler != null) {
                    LOGGER.warn("Retry mechanism is started for {}, exceptionType[{}]", failedTaskMap.getKey(), failedTaskMap.getValue().getExceptionType().getName());
                    return handler.handle(checkpoint);
                }
            }
        }
        checkpoint.setErrorFlag(false);
        return checkpoint.getResult();
    }

    public <T> void finishRetry(Checkpoint<T> checkpoint) {
        if (checkpoint.getProducer() == null) {
            checkpoints.remove(checkpoint.getThreadName());
        } else{
            checkpoint.setRetryCounter(0);
        }
    }

    public interface ThrowingFunction<T> {
        T execute() throws Exception; //NOSONAR
    }

    public interface ThrowingOperation {
        void execute() throws Exception; //NOSONAR
    }

}