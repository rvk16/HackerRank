package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;


import com.amdocs.aia.il.common.model.cache.CacheAttribute;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class CacheEntityDependencyAnalyzerUnitTest {
    protected static final String PROJECT_KEY = "projectKey";

    @InjectMocks
    private CacheEntityDependencyAnalyzer cacheEntityDependencyAnalyzer;

    @Test
    void whenGetCacheEntityDependency_shouldNotReturnDependency() {
        CacheEntity cacheEntity = createCacheEntity();
        List<ElementDependency> dependencies = cacheEntityDependencyAnalyzer.getDependencies(cacheEntity);
        assertEquals(0, dependencies.size());
    }

    @Test
    void whenGetExternalEntityPublicFeatures_shouldReturnPublicFeatures() {
        CacheEntity cacheEntity = createCacheEntity();
        List<ElementPublicFeature> publicFeatures = cacheEntityDependencyAnalyzer.getPublicFeatures(cacheEntity);
        assertEquals(2, publicFeatures.size());

        ElementPublicFeature ElementPublicFeatureAtt1 = publicFeatures.stream().filter(elementPublicFeature -> elementPublicFeature.getKey().equals("att1")).findAny().orElse(null);
        assertNotNull(ElementPublicFeatureAtt1);
        ElementPublicFeature ElementPublicFeatureAtt2 = publicFeatures.stream().filter(elementPublicFeature -> elementPublicFeature.getKey().equals("att2")).findAny().orElse(null);
        assertNotNull(ElementPublicFeatureAtt2);
    }

    private CacheEntity createCacheEntity() {
        CacheEntity cacheEntity = new CacheEntity();
        cacheEntity.setEntityKey("entityKey");
        cacheEntity.setProjectKey("aia");

        List<CacheAttribute> cacheAttributes = new ArrayList<>();
        CacheAttribute cacheAttribute1 = new CacheAttribute();
        cacheAttribute1.setAttributeKey("att1");

        CacheAttribute cacheAttribute2 = new CacheAttribute();
        cacheAttribute2.setAttributeKey("att2");

        cacheAttributes.add(cacheAttribute1);
        cacheAttributes.add(cacheAttribute2);
        cacheEntity.setAttributes(cacheAttributes);
        return cacheEntity;
    }



}
