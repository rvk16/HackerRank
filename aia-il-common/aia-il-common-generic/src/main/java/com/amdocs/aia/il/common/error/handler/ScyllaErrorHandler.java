package com.amdocs.aia.il.common.error.handler;

import com.amdocs.aia.il.common.utils.ClassLoaderUtils;
import com.amdocs.aia.il.common.utils.SneakyThrowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Component
public class ScyllaErrorHandler implements Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScyllaErrorHandler.class);

    private static Set<String> allowedScyllaExceptionTypes;
    private ErrorHandler errorHandler;

    static {
        try {
            allowedScyllaExceptionTypes = ClassLoaderUtils.getScyllaExceptionType();
        } catch (Exception e) {
            SneakyThrowUtil.sneakyThrow(e);
        }
    }

    public boolean isScyllaException(Class<?> exceptionType) {
        return allowedScyllaExceptionTypes.contains(exceptionType.getName());
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Autowired
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public <T> T handle(Checkpoint<T> checkpoint) {
        setErrorMetricFlag(checkpoint);
        setExceptionMetricCounter(checkpoint);
        clearFailedTasks(checkpoint);
        if (checkpoint.getFunction() != null) {
            return runWithRetriesAndDelay(checkpoint);
        }
        runWithRetriesAndDelay(checkpoint, checkpoint.getOperation());
        return null;

    }

    @Override
    public <T> T runWithRetriesAndDelay(Checkpoint<T> checkpoint) {
        while (true) {
            try {
                LOGGER.warn("Retry mechanism is running, retryCount[{}], retryDelay[{}], topicName[{}], methodName[{}]", checkpoint.getRetryCounter() + 1, checkpoint.getRetryDelay(), checkpoint.getThreadName(), checkpoint.getMethodName());
                checkpoint.setRetryCounter(checkpoint.getRetryCounter() + 1);
                Thread.sleep(Duration.ofSeconds(checkpoint.getRetryDelay()).toMillis());
                T output = checkpoint.getFunction().execute();
                setErrorMetricFlag(checkpoint);
                return output;
            } catch (Exception e) {
                SneakyThrowUtil.sneakyThrow(e);
            }
        }
    }

    @Override
    public <T> T runWithRetriesAndDelay(Checkpoint<T> checkpoint, Set<Class<? extends Exception>> allowedExceptionTypes) {
        return null;
    }

    @Override
    public <T> void runWithRetriesAndDelay(Checkpoint<T> checkpoint, ErrorHandler.ThrowingOperation operation) {
        while (true) {
            try {
                LOGGER.warn("Retry mechanism is running, retryCount[{}], retryDelay[{}], topicName[{}], methodName[{}]", checkpoint.getRetryCounter() + 1, checkpoint.getRetryDelay(), checkpoint.getThreadName(), checkpoint.getMethodName());
                checkpoint.setRetryCounter(checkpoint.getRetryCounter() + 1);
                Thread.sleep(Duration.ofSeconds(checkpoint.getRetryDelay()).toMillis());
                operation.execute();
                setErrorMetricFlag(checkpoint);
                return;
            } catch (Exception e) {
                SneakyThrowUtil.sneakyThrow(e);
            }
        }
    }

    private <T> void setErrorMetricFlag(Checkpoint<T> checkpoint) {
        String threadName;
        if(checkpoint.getThreadName().substring(checkpoint.getThreadName().length() - 1).matches(".*\\d.*")) {
            threadName = checkpoint.getThreadName().substring(0, checkpoint.getThreadName().length() - 1);
        }
        else {
            threadName = checkpoint.getThreadName();
        }
        if (checkpoint.isErrorFlag()) {
            if (checkpoint.getRetryCounter() == 0) {
                errorHandler.getMetrics().setScyllaErrorFlagPerDataChannel(1, threadName);
            }
        } else {
            errorHandler.getMetrics().setScyllaErrorFlagPerDataChannel(0, threadName);
        }
    }

    private <T> void setExceptionMetricCounter(Checkpoint<T> checkpoint) {
        Optional<ExecutionState> executionState = checkpoint.getExecutionStates().entrySet().stream().map(Map.Entry::getValue).findFirst();
        if (executionState.isPresent()) {
            for (Map.Entry<Object, ErrorType> failedTaskMap : executionState.get().getFailedTasks().entrySet()) {
                errorHandler.getMetrics().incrementCounterPerScyllaExceptionType(1, failedTaskMap.getValue().getExceptionType().getSimpleName());
                break; //NOSONAR
            }
        }
    }

    private static <T> void clearFailedTasks(Checkpoint<T> checkpoint) {
        for (Map.Entry<String, ExecutionState> executionStatesMap : checkpoint.getExecutionStates().entrySet()) {
            checkpoint.setErrorFlag(false);
            checkpoint.getExecutionStates().get(executionStatesMap.getKey()).setFailedTasks(Collections.emptyMap());
        }
    }

}
