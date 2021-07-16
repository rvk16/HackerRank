package com.amdocs.aia.il.common.error.handler;

import com.amdocs.aia.il.common.stores.scylla.monitor.DbMetrics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class ScyllaErrorHandlerTest {

    @InjectMocks
    ScyllaErrorHandler scyllaErrorHandler;
    @Mock
    ErrorHandler errorHandler;
    @Mock
    DbMetrics metrics;

  @BeforeEach
    void setUp() {
        scyllaErrorHandler.setErrorHandler(errorHandler);
        errorHandler.setMetrics(metrics);
    }


    @Test
    public void testHandleWithFunction(){
        Mockito.when(errorHandler.getMetrics()).thenReturn(metrics);
        Object obj = scyllaErrorHandler.handle(ErrorHandlerFixture.getMockDataForTestCommitCheckpointWithErrorFlagTrueWithFunction());
        Assertions.assertNotNull(obj);
    }

    @Test
    public void testHandleWithOperation(){
        Mockito.when(errorHandler.getMetrics()).thenReturn(metrics);
        Object obj = scyllaErrorHandler.handle(ErrorHandlerFixture.getMockDataForTestCommitCheckpointWithErrorFlagTrue());
        Assertions.assertNull(obj);
    }
}
