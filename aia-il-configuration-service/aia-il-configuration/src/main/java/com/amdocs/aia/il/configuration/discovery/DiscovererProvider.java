package com.amdocs.aia.il.configuration.discovery;

import com.amdocs.aia.il.common.model.external.ExternalSchemaType;

import java.util.Set;

public interface DiscovererProvider<T extends ExternalModelDiscoverer> {
    T get();
    Set<ExternalSchemaType> getSupportedExternalSchemaTypes();
}
