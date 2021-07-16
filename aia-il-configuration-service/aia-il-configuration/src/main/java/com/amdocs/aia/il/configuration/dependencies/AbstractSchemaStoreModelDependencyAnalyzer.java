package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel;

import java.util.List;

public interface AbstractSchemaStoreModelDependencyAnalyzer<M extends AbstractIntegrationLayerSchemaStoreModel> {
    List<ElementDependency> getDependencies(M element);

    List<ElementPublicFeature> getPublicFeatures(M m);
}