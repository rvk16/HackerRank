package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;

import java.util.List;

public interface AbstractEntityReferentialIntegrityDependencyAnalyzer {

    List<ElementDependency> getDependencies(EntityReferentialIntegrity element) ;
}
