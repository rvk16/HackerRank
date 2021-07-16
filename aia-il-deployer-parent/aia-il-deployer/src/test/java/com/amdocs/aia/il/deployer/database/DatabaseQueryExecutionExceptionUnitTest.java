package com.amdocs.aia.il.deployer.database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DatabaseQueryExecutionExceptionUnitTest {

    @Test
    void construct_databaseQueryExecutionException() {
        DatabaseQueryExecutionException ex = null;
        ex = new DatabaseQueryExecutionException("message", "param1", "param2");
        assertNotNull(ex);

        ex = new DatabaseQueryExecutionException("message", new RuntimeException("test Exception"), "param1", "param2");
        assertNotNull(ex);

        ex = new DatabaseQueryExecutionException(new RuntimeException("test Exception"));
        assertNotNull(ex);
    }

}
