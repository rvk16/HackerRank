package com.amdocs.aia.il.configuration.mapper.unit;

import com.amdocs.aia.il.common.model.cache.CacheAttribute;
import com.amdocs.aia.il.configuration.dto.CacheReferenceAttributeDTO;
import com.amdocs.aia.il.configuration.mapper.cache.CacheAttributeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CacheAttributeMapperUnitTest {

    @MockBean
    private CacheAttributeMapper cacheAttributeMapper;

    @TestConfiguration
    static class CacheAttributeMapperConfiguration {
    }

    @BeforeEach
    void before() {
        cacheAttributeMapper = new CacheAttributeMapper();
    }

    @Test
    void whenCacheAttributeModelToCacheReferenceAttributeDTO_ShouldBeEqual() {
        final CacheAttribute cacheAttribute = getCacheAttributeModel("attributeKey");
        final CacheReferenceAttributeDTO cacheReferenceAttributeDTO = cacheAttributeMapper.toDTO(cacheAttribute);

        assertEquals(cacheAttribute.getAttributeKey(), cacheReferenceAttributeDTO.getAttributeKey());
        assertEquals(cacheAttribute.getName(), cacheReferenceAttributeDTO.getAttributeName());
        assertEquals(cacheAttribute.getDescription(), cacheReferenceAttributeDTO.getDescription());
        assertEquals(cacheAttribute.getKeyPosition(), cacheReferenceAttributeDTO.getKeyPosition());
        assertEquals(cacheAttribute.getDatatype(), cacheReferenceAttributeDTO.getType());
    }

    @Test
    void whenCacheReferenceAttributeDTOToCacheAttributeModel_ShouldBeEqual() {
        final CacheReferenceAttributeDTO cacheReferenceAttributeDTO = getCacheReferenceAttributeDTO("attributeKey");
        final CacheAttribute cacheAttribute = cacheAttributeMapper.toModel(cacheReferenceAttributeDTO);

        assertEquals(cacheReferenceAttributeDTO.getAttributeKey(), cacheAttribute.getAttributeKey());
        assertEquals(cacheReferenceAttributeDTO.getAttributeName(), cacheAttribute.getName());
        assertEquals(cacheReferenceAttributeDTO.getDescription(), cacheAttribute.getDescription());
        assertEquals(cacheReferenceAttributeDTO.getKeyPosition(), cacheAttribute.getKeyPosition());
        assertEquals(cacheReferenceAttributeDTO.getType(), cacheAttribute.getDatatype());
    }

    private static CacheAttribute getCacheAttributeModel(final String attributeKey) {
        final CacheAttribute cacheAttribute = new CacheAttribute();
        cacheAttribute.setAttributeKey(attributeKey);
        cacheAttribute.setName(attributeKey);
        cacheAttribute.setDescription("description");
        cacheAttribute.setKeyPosition(1);
        cacheAttribute.setDatatype("type");
        return cacheAttribute;
    }

    private static CacheReferenceAttributeDTO getCacheReferenceAttributeDTO(final String attributeKey ) {
        CacheReferenceAttributeDTO dto = new CacheReferenceAttributeDTO();
        dto.setAttributeKey(attributeKey);
        dto.setAttributeName(attributeKey);
        dto.setDescription("description");
        dto.setKeyPosition(1);
        dto.setType("type");
        return dto;
    }
}
