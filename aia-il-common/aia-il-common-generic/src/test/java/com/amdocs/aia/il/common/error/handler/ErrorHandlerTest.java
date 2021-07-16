package com.amdocs.aia.il.common.error.handler;

import com.amdocs.aia.il.common.stores.scylla.monitor.DbMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class})
public class ErrorHandlerTest {

    @Mock
    private ErrorHandlerFactory handlerFactory;
    @InjectMocks
    private ErrorHandler errorHandler;
    @Mock
    private DbMetrics metrics;

 //   private Map<String, Checkpoint> checkpoints = new ConcurrentHashMap<>();
    private Checkpoint checkpoint;
   // private ScyllaErrorHandler scyllaErrorHandler = new ScyllaErrorHandler();

    @BeforeEach
    void setUp() {
        errorHandler.setHandlerFactory(handlerFactory);
        errorHandler.setMetrics(metrics);
    }

    @Test
    void testInitCheckpointWithEmptyCheckpointsForFunction() {
        checkpoint = errorHandler.initCheckpoint("Thread-1", "getSuccess", () -> ErrorHandlerFixture.getSuccess());
        assertNotNull(checkpoint);
        assertTrue(errorHandler.getCheckpoints().containsKey("Thread-1"));
    }

    @Test
    void testInitCheckpointWithExistingCheckpointsForFunction() {
        errorHandler.setCheckpoints(ErrorHandlerFixture.getMockDataForTestInitCheckpointWithExistingCheckpointsForFunction());
        checkpoint = errorHandler.initCheckpoint("Thread-1", "getSuccess", () -> ErrorHandlerFixture.getSuccess());
        assertNotNull(checkpoint);
        assertTrue(errorHandler.getCheckpoints().containsKey("Thread-1"));
    }

    @Test
    void testInitCheckpointWithEmptyCheckpointsForOperation() {
        checkpoint = errorHandler.initCheckpoint("Thread-1", "printSuccess", () -> ErrorHandlerFixture.printSuccess());
        assertNotNull(checkpoint);
        assertTrue(errorHandler.getCheckpoints().containsKey("Thread-1"));
    }

    @Test
    void testInitCheckpointWithExistingCheckpointsForOperation() {
        errorHandler.setCheckpoints(ErrorHandlerFixture.getMockDataForTestInitCheckpointWithExistingCheckpointsForOperation());
        checkpoint = errorHandler.initCheckpoint("Thread-1", "printSuccess", () -> ErrorHandlerFixture.printSuccess());
        assertNotNull(checkpoint);
        assertTrue(errorHandler.getCheckpoints().containsKey("Thread-1"));
    }

    @Test
    void testCommitCheckpointWithErrorFlagFalse(){
        checkpoint = new Checkpoint("Thread-1", "printSuccess", () -> ErrorHandlerFixture.printSuccess());
        Object obj = errorHandler.commitCheckpoint(checkpoint);
        assertNull(obj);
    }

    @Test
    void testCommitCheckpointWithErrorFlagTrueWithOperation(){
        errorHandler.setErrorHandlerEnabled(true);
        Object obj = errorHandler.commitCheckpoint(ErrorHandlerFixture.getMockDataForTestCommitCheckpointWithErrorFlagTrue());
        assertNull(obj);
    }
}
