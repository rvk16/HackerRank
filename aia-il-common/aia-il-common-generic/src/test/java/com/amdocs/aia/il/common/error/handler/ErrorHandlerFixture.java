package com.amdocs.aia.il.common.error.handler;

import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ErrorHandlerFixture {

    private static Map<String, Object> checkpoints;

    public static Map<String, Object> getMockDataForTestInitCheckpointWithExistingCheckpointsForFunction() {
        Checkpoint checkpoint = new Checkpoint("Thread-1", "getSuccess", ErrorHandlerFixture::getSuccess);
        checkpoints = new ConcurrentHashMap<>();
        checkpoints.put("Thread-1", checkpoint);
        return checkpoints;
    }

    public static Map<String, Object> getMockDataForTestInitCheckpointWithExistingCheckpointsForOperation() {
        Checkpoint checkpoint = new Checkpoint("Thread-1", "printSuccess", () -> ErrorHandlerFixture.printSuccess());
        checkpoints = new ConcurrentHashMap<>();
        checkpoints.put("Thread-1", checkpoint);
        return checkpoints;
    }

    public static Checkpoint getMockDataForTestCommitCheckpointWithErrorFlagTrue() {
        Checkpoint checkpoint = new Checkpoint("Thread-1", "printSuccess", () -> printSuccess());
        checkpoint.setErrorFlag(true);
        checkpoint.setRetryCounter(1);
        checkpoint.setExecutionStates(getExecutionStates(false));
        return checkpoint;
    }

    public static Checkpoint getMockDataForTestCommitCheckpointWithErrorFlagTrueWithFunction() {
        Checkpoint checkpoint = new Checkpoint("Thread-1", "getSuccess", () -> getSuccess());
        checkpoint.setErrorFlag(true);
        checkpoint.setRetryCounter(1);
        checkpoint.setExecutionStates(getExecutionStates(false));
        return checkpoint;
    }

    public static Checkpoint getMockDataForTestCommitCheckpointWithErrorFlagFalseAndFunction() {
        Checkpoint checkpoint = new Checkpoint("Thread-1", "getSuccess", () -> getSuccess());
        checkpoint.setExecutionStates(getExecutionStates(true));
        return checkpoint;
    }

    public static Checkpoint getMockDataForTestCommitCheckpointWithErrorFlagFalseAndOperation() {
        Checkpoint checkpoint = new Checkpoint("Thread-1", "printSuccess", () -> printSuccess());
        checkpoint.setExecutionStates(getExecutionStates(true));
        return checkpoint;
    }

    public static void printSuccess() {

    }

    public static Map<String, ExecutionState> getExecutionStates(boolean isCompleted) {
        Map<String, ExecutionState> executionStatesMap = new ConcurrentHashMap<>();
        executionStatesMap.put("Table", getExecutionState(isCompleted));
        return executionStatesMap;
    }

    private static ExecutionState getExecutionState(boolean isCompleted) {
        ExecutionState executionStates = new ExecutionState();
        if (isCompleted) {
            executionStates.setCompletedTasks(getCompletedTasks());
        } else {
            executionStates.setFailedTasks(getFailedTasks());
        }

        return executionStates;
    }

    private static List<String> getCompletedTasks() {
        List<String> completedTasks = new ArrayList<>();
        completedTasks.add("BCMAPP");
        return completedTasks;
    }

    private static Map<Object, ErrorType> getFailedTasks() {
        Map<Object, ErrorType> failedTaskMap = new ConcurrentHashMap<>();
        failedTaskMap.put("BCMAPP", getErrorType());
        return failedTaskMap;
    }

    private static ErrorType getErrorType() {
        return new ErrorType("Exception Occurred", InvalidQueryException.class);
    }

    public static String getSuccess() {
        return "Success";
    }
}
