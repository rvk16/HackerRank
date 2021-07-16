package com.amdocs.aia.il.configuration.service;

import java.util.List;

public interface TransformationAttributeConfigurationService<L> {
    List<L> list(final String projectKey, final String logicalSchemaKey, final String entitystoreKey);
    List<L> getAvailableAttributes(final String projectKey, final String logicalSchemaKey, final String logicalEntityKey);
}
