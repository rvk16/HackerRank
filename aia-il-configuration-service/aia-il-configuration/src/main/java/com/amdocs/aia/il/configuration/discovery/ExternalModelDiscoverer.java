package com.amdocs.aia.il.configuration.discovery;

import com.amdocs.aia.il.common.model.external.ExternalSchemaType;

public interface ExternalModelDiscoverer<P extends ExternalModelDiscoveryParameters> {
    Class<P> getParametersClass();
    void setSchemaType(ExternalSchemaType schemaType);
    void setProjectKey(String projectKey);
    void setSchemaName(String schemaName);
    void setConsumer(ExternalModelDiscoveryConsumer consumer);
    void setParameters(P parameters);
    void discover();
}
