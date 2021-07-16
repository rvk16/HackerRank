package com.amdocs.aia.il.deployer.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeployerExceptionUnitTest {

    /**
     * This case is to test various constructor of the DeployerException.class
     */
    @Test
    void constructors_of_deployerException() {
        DeployerException ex = null;
        ex = new DeployerException("message", "param1", "param2");
        assertNotNull(ex);

        ex = new DeployerException("message", new RuntimeException("test Exception"), "param1", "param2");
        assertNotNull(ex);

        ex = new DeployerException(new RuntimeException("test Exception"));
        assertNotNull(ex);
    }

}
