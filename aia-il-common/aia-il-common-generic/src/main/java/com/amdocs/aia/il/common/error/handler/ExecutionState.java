package com.amdocs.aia.il.common.error.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExecutionState {

    List<String> completedTasks = new ArrayList<>();
    Map<Object, ErrorType> failedTasks;

    public List<String> getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(List<String> completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Map<Object, ErrorType> getFailedTasks() {
        return failedTasks;
    }

    public void setFailedTasks(Map<Object, ErrorType> failedTasks) {
        this.failedTasks = failedTasks;
    }

    //returns true if filterName is present in completedTasks else false.
    boolean isCompleted(String filterName) {
        return completedTasks.contains(filterName);
    }
}
