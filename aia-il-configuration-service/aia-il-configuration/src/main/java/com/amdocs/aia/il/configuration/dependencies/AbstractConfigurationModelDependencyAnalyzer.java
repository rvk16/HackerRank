package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.il.common.model.configuration.tables.AbstractPublisherConfigurationModel;

import java.util.List;

public interface AbstractConfigurationModelDependencyAnalyzer<M extends AbstractPublisherConfigurationModel> {
    List<ElementDependency> getDependencies(M element);
    List<ElementPublicFeature> getPublicFeatures(M m);
}