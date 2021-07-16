package com.amdocs.aia.il.common.error.handler;

import com.datastax.oss.driver.api.core.servererrors.AlreadyExistsException;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.session.MockitoSessionBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockitoExtension.class})
public class CheckpointTest {

    @InjectMocks
    private Checkpoint checkpoint;


    @Test
    public void testAddCompletedTaskWithExecutionStates() {
        checkpoint.setExecutionStates(ErrorHandlerFixture.getExecutionStates(true));
        checkpoint.addCompletedTask("Table", "CRMAPP");
        assertTrue(getExecutionState(checkpoint).getCompletedTasks().size() == 2);
        assertTrue(getExecutionState(checkpoint).getCompletedTasks().contains("CRMAPP"));
    }

    @Test
    public void testAddCompletedTaskWithExecutionStatesAndEmptyCompletedTask() {
        checkpoint.setExecutionStates(ErrorHandlerFixture.getExecutionStates(false));
        checkpoint.addCompletedTask("Table", "CRMAPP");
        assertTrue(getExecutionState(checkpoint).getCompletedTasks().size() == 1);
        assertTrue(getExecutionState(checkpoint).getCompletedTasks().contains("CRMAPP"));
    }

    @Test
    public void testAddCompletedTaskWithoutExecutionStates() {
        checkpoint.setExecutionStates(new ConcurrentHashMap<>());
        checkpoint.addCompletedTask("Table", "CRMAPP");
        assertTrue(getExecutionState(checkpoint).getCompletedTasks().size() == 1);
        assertTrue(getExecutionState(checkpoint).getCompletedTasks().contains("CRMAPP"));
    }

    @Test
    public void testAddFailedTaskWithoutExecutionStates() {
        checkpoint.setExecutionStates(new ConcurrentHashMap<>());
        checkpoint.addFailedTask("Table", "CRMAPP", "Exception Occurred", InvalidQueryException.class);
        assertTrue(getExecutionState(checkpoint).getFailedTasks().size() == 1);
        assertTrue(getExecutionState(checkpoint).getFailedTasks().containsKey("CRMAPP"));
    }

    @Test
    public void testAddFailedTaskWithExecutionStatesAndEmptyFailedTasks() {
        checkpoint.setExecutionStates(ErrorHandlerFixture.getExecutionStates(true));
        checkpoint.addFailedTask("Table", "CRMAPP", "Exception Occurred", InvalidQueryException.class);
        assertTrue(getExecutionState(checkpoint).getFailedTasks().size() == 1);
        assertTrue(getExecutionState(checkpoint).getFailedTasks().containsKey("CRMAPP"));
    }

    @Test
    public void testAddFailedTaskWithExecutionStatesAndExistingFailedTasks() {
        checkpoint.setExecutionStates(ErrorHandlerFixture.getExecutionStates(false));
        checkpoint.addFailedTask("Table", "CRMAPP", "Exception Occurred", AlreadyExistsException.class);
        assertTrue(getExecutionState(checkpoint).getFailedTasks().size() == 2);
        assertTrue(getExecutionState(checkpoint).getFailedTasks().containsKey("CRMAPP"));
    }

    private ExecutionState getExecutionState(Checkpoint checkpoint) {
        Map<String, ExecutionState> map = checkpoint.getExecutionStates();
        return map.get("Table");
    }

    @Test
    public void testFilterSuccess() {
        checkpoint.setExecutionStates(ErrorHandlerFixture.getExecutionStates(true));
        assertTrue(checkpoint.filter("Table", "BCMAPP"));
    }

    @Test
    public void testFilterFailed() {
        checkpoint.setExecutionStates(ErrorHandlerFixture.getExecutionStates(false));
        assertFalse(checkpoint.filter("Table", "BCMAPP"));
    }


}
