package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.il.common.model.cache.CacheAttribute;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import com.amdocs.aia.il.configuration.repository.cache.CacheReferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CacheReferenceRepositoryUnitTest {
    private static final String PROJECT_KEY = "aia";

    @Spy
    @InjectMocks
    private CacheReferenceRepository cacheReferenceRepository;

    @Test
    void when_findAllCacheEntitiesByProjectKey_shouldReturn() {
        doReturn(Collections.singletonList(getCacheEntity())).when(cacheReferenceRepository).findByProjectKey(Mockito.any());
        List<CacheEntity> cacheEntity = cacheReferenceRepository.findByProjectKey(PROJECT_KEY);
        assertEquals(1, cacheEntity.size());
    }

    @Test
    void when_findCacheEntityByKeys_shouldReturn() {
        CacheEntity cacheEntity = getCacheEntity();
        doReturn(cacheEntity).when(cacheReferenceRepository).getByKey(Mockito.any(), Mockito.any());
        CacheEntity actual = cacheReferenceRepository.getByKey(PROJECT_KEY, cacheEntity.getEntityKey());
        assertEquals(cacheEntity.getEntityKey(), actual.getEntityKey());
        assertEquals(cacheEntity.getName(), actual.getName());
        assertEquals(cacheEntity.getDescription(), actual.getDescription());
        assertEquals(cacheEntity.getProjectKey(), actual.getProjectKey());
        assertEquals(cacheEntity.getAttributes().get(0).getAttributeKey(), actual.getAttributes().get(0).getAttributeKey());
        assertEquals(cacheEntity.getAttributes().get(0).getName(), actual.getAttributes().get(0).getName());
        assertEquals(cacheEntity.getAttributes().get(0).getDescription(), actual.getAttributes().get(0).getDescription());
        assertEquals(cacheEntity.getAttributes().get(0).getDatatype(), actual.getAttributes().get(0).getDatatype());
    }


    private static CacheEntity getCacheEntity() {
        final CacheEntity cacheEntity =new CacheEntity();
        cacheEntity.setEntityKey("cacheEntityKey");
        cacheEntity.setName("cacheEntityName");
        cacheEntity.setDescription("Description");
        cacheEntity.setAttributes(getCacheReferenceAttributeList());

        return cacheEntity;
    }

    private static List<CacheAttribute> getCacheReferenceAttributeList(){
        CacheAttribute cacheAttribute1 = new CacheAttribute();
        cacheAttribute1.setAttributeKey("attributeKey1");
        cacheAttribute1.setName("Attribute Key1");
        cacheAttribute1.setDescription("Attribute Description");
        cacheAttribute1.setKeyPosition(1);
        cacheAttribute1.setDatatype("type");

        CacheAttribute cacheAttribute2 = new CacheAttribute();
        cacheAttribute2.setAttributeKey("attributeKey2");
        cacheAttribute2.setName("Attribute Key2");
        cacheAttribute2.setDescription("Attribute Description");
        cacheAttribute2.setKeyPosition(0);
        cacheAttribute2.setDatatype("type");

        List<CacheAttribute> cacheAttributes = new ArrayList<>();
        cacheAttributes.add(cacheAttribute1);
        cacheAttributes.add(cacheAttribute2);
        return cacheAttributes;
    }
}
