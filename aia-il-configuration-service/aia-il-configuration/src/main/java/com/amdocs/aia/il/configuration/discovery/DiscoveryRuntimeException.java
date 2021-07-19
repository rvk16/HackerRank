package com.amdocs.aia.il.configuration.discovery;

public class DiscoveryRuntimeException extends RuntimeException {
    public DiscoveryRuntimeException(String message, String... params) {
        super(String.format(message, params));
    }

    public DiscoveryRuntimeException(String message, Throwable cause, String... params) {
        super(String.format(message, params), cause);
    }

    public DiscoveryRuntimeException(Throwable cause) {
        super(cause);
    }
}
