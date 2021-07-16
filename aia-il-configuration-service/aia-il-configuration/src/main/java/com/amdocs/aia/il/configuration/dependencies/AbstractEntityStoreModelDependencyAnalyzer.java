package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerEntityStoreModel;

import java.util.List;

public interface AbstractEntityStoreModelDependencyAnalyzer<M extends AbstractIntegrationLayerEntityStoreModel> {
    List<ElementDependency> getDependencies(M element);

    List<ElementPublicFeature> getPublicFeatures(M m);
}