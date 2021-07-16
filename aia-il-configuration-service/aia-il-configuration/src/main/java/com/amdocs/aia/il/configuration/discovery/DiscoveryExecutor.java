package com.amdocs.aia.il.configuration.discovery;

import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DiscoveryExecutor {
    private final List<DiscovererProvider> providers;

    public DiscoveryExecutor(List<DiscovererProvider> providers) {
        this.providers = providers;
    }

    public void execute(String projectKey,
                        ExternalSchemaType externalSchemaType,
                        String schemaName,
                        Map<String, Object> parameters,
                        ExternalModelDiscoveryConsumer discoveryConsumer) {
        final ExternalModelDiscoverer discoverer = providers.stream()
                .filter(d -> d.getSupportedExternalSchemaTypes().contains(externalSchemaType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not find registered discoverer provider for external schema type " + externalSchemaType))
                .get();
        discoverer.setProjectKey(projectKey);
        discoverer.setSchemaType(externalSchemaType);
        discoverer.setSchemaName(schemaName);
        discoverer.setParameters(DiscoveryUtils.buildTypedDiscoveryParameters(parameters, discoverer.getParametersClass()));
        discoverer.setConsumer(discoveryConsumer);
        discoverer.discover();
    }
}
