package com.amdocs.aia.il.configuration.discovery;

import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalSchema;

public interface ExternalModelDiscoveryConsumer {
    default void discoveryStarted() {}
    default void acceptSchema(ExternalSchema externalSchema) {}
    default void acceptEntity(ExternalEntity externalEntity) {}
    default void discoveryCompleted() {}
}
