package com.amdocs.aia.il.common.error.handler;

import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class})
public class ErrorHandlerFactoryTest {

    @InjectMocks
    private ErrorHandlerFactory errorHandlerFactory;

    @Mock
    private ScyllaErrorHandler scyllaErrorHandler;
    @Mock
    private KafkaErrorHandler kafkaErrorHandler;

    @Test
    public void testScyllaErrorHandlerSuccess(){
        Mockito.when(scyllaErrorHandler.isScyllaException(InvalidQueryException.class)).thenReturn(true);
        Handler handler = errorHandlerFactory.getHandler(InvalidQueryException.class);
        assertNotNull(handler);
    }

    @Test
    public void testScyllaErrorHandlerFailed(){
        Mockito.when(scyllaErrorHandler.isScyllaException(NullPointerException.class)).thenReturn(false);
        Handler handler = errorHandlerFactory.getHandler(NullPointerException.class);
        assertNull(handler);
    }

    @Test
    public void testHandlerWillNull(){
        Handler handler = errorHandlerFactory.getHandler(null);
        assertNull(handler);
    }
}
