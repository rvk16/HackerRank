package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.il.common.model.cache.CacheAttribute;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CacheEntityDependencyAnalyzer  {

    public List<ElementDependency> getDependencies(CacheEntity element) {
        return Collections.emptyList();
    }

    public List<ElementPublicFeature> getPublicFeatures(CacheEntity element) {
        List<ElementPublicFeature> features = new ArrayList<>();
        features.addAll(element.getAttributes().stream().map(this::toPublicFeature).collect(Collectors.toList()));
        return features;
    }

    protected ElementPublicFeature toPublicFeature(CacheAttribute attribute) {
        return new ElementPublicFeature(attribute.getAttributeKey(), attribute.getName());
    }
}





