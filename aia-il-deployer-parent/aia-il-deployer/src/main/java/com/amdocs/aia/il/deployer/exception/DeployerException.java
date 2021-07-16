package com.amdocs.aia.il.deployer.exception;

public class DeployerException extends RuntimeException {

    public DeployerException(String message, String... params) {
        super(String.format(message, params));
    }

    public DeployerException(String message, Throwable cause, String... params) {
        super(String.format(message, params), cause);
    }

    public DeployerException(Throwable cause) {
        super(cause);
    }
}
