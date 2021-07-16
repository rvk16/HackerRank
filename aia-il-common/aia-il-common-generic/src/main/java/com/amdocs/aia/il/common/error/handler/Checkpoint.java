package com.amdocs.aia.il.common.error.handler;

import org.apache.kafka.clients.producer.Producer;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Checkpoint<T> {

    //This executable function for which checkpoint is created
    private ErrorHandler.ThrowingFunction<T> function;
    //This executable operation for which checkpoint is created
    private ErrorHandler.ThrowingOperation operation;
    //e.g. "CONTEXT" -> ExecutionState
    private Map<String, ExecutionState> executionStates = new ConcurrentHashMap<>();
    //Set error flag if at least one failedTask is added in any executionState
    private boolean errorFlag;
    private int retryCounter;
    private String threadName;
    private T result;
    private String methodName;
    private static final int INITIAL_DELAY = 10; // In Seconds
    Producer<String, byte[]> producer;

    public Checkpoint() {
    }

    public Checkpoint(String threadName, String methodName, final ErrorHandler.ThrowingFunction<T> function) {
        this.threadName = threadName;
        this.methodName = methodName;
        this.function = function;
    }

    public Checkpoint(String threadName, String methodName, final ErrorHandler.ThrowingOperation operation) {
        this.threadName = threadName;
        this.methodName = methodName;
        this.operation = operation;
    }
    public Checkpoint(String threadName, Producer<String, byte[]> producer) {
        this.threadName = threadName;
        this.producer = producer;
    }

    public Map<String, ExecutionState> getExecutionStates() {
        return executionStates;
    }


    public Map<String, ExecutionState> getCurrentExecutionStates() {
        synchronized (executionStates){
            Map<String, ExecutionState> currentExecutionStates = new ConcurrentHashMap<>();
            currentExecutionStates.putAll(executionStates);
            executionStates.clear();
            return currentExecutionStates;
        }

    }

    public void setExecutionStates(Map<String, ExecutionState> executionStates) {
        this.executionStates = executionStates;
    }

    public boolean isErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(boolean errorFlag) {
        this.errorFlag = errorFlag;
    }

    public int getRetryCounter() {
        return retryCounter;
    }

    public void setRetryCounter(int retryCounter) {
        this.retryCounter = retryCounter;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Producer<String, byte[]> getProducer() {
        return producer;
    }

    public void setProducer(Producer<String, byte[]> producer) {
        this.producer = producer;
    }

    public int getRetryDelay() {
        if (retryCounter == 0) {
            return INITIAL_DELAY;
        } else {
            int counter = retryCounter;
            int delay = 1;
            while (counter != 0) {
                delay *= 2;
                --counter;
            }
            return INITIAL_DELAY + delay;
        }
    }

    //Accept filterType and filterName as inputs and will return true if filterName is present in executionStates-> completedTasks. Otherwise return as false.
    public boolean filter(String filterType, String filterName) {
        return executionStates.get(filterType) != null && executionStates.get(filterType).isCompleted(filterName);
    }

    //Update the completed tasks in executionStates
    public void addCompletedTask(String filterType, String filterName) {
        if (executionStates.get(filterType) != null) {
            executionStates.get(filterType).getCompletedTasks().add(filterName);
        } else {
            ExecutionState executionState = new ExecutionState();
            List<String> completedTasks = executionState.getCompletedTasks();
            completedTasks.add(filterName);
            executionState.setCompletedTasks(completedTasks);
            executionStates.put(filterType, executionState);
        }

    }

    //Update the failed tasks in executionStates
    public void addFailedTask(String filterType, Object filterName, String errorMessage, Class<?> exceptionType) {
        synchronized (executionStates){
            if (executionStates.get(filterType) != null) {
                if (!CollectionUtils.isEmpty(executionStates.get(filterType).getFailedTasks())) {
                    executionStates.get(filterType).getFailedTasks().put(filterName, new ErrorType(errorMessage, exceptionType));
                } else {
                    Map<Object, ErrorType> failedTaskMap = new ConcurrentHashMap<>();
                    failedTaskMap.put(filterName, new ErrorType(errorMessage, exceptionType));
                    executionStates.get(filterType).setFailedTasks(failedTaskMap);
                }
            } else {
                ExecutionState executionState = new ExecutionState();
                Map<Object, ErrorType> failedTaskMap = new ConcurrentHashMap<>();
                failedTaskMap.put(filterName, new ErrorType(errorMessage, exceptionType));
                executionState.setFailedTasks(failedTaskMap);
                executionStates.put(filterType, executionState);
            }
        }
    }

    public void addResult(Mergeable<T> object) {
        result = (T) object.merge(object, result);
    }

    public <K> void addResult(List<K> object) {
        if (result != null) {
            object.addAll((List<K>) result);
        }
        result = (T) object;

    }

    public <K> void addResult(Set<K> object) {
        if (result != null) {
            object.addAll((Set<K>) result);
        }
        result = (T) object;

    }

    public <K, V> void addResult(Map<K, V> object) {
        if (result != null) {
            object.putAll((Map<K, V>) result);
        }
        result = (T) object;
    }

    public ErrorHandler.ThrowingFunction<T> getFunction() {
        return function;
    }

    public ErrorHandler.ThrowingOperation getOperation() {
        return operation;
    }
}
