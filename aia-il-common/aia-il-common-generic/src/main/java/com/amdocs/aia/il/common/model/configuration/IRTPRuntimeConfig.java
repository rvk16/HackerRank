package com.amdocs.aia.il.common.model.configuration;

/**
 * Interface for all config objects
 */
public interface IRTPRuntimeConfig {

    /**
     * Validate the model
     * Throw Runtime exception in case not valid
     */
    public void validate();

}
