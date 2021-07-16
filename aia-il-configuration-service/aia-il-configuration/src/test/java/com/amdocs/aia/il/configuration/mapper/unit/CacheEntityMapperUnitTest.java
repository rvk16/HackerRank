package com.amdocs.aia.il.configuration.mapper.unit;

import com.amdocs.aia.il.common.model.cache.CacheAttribute;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import com.amdocs.aia.il.configuration.dto.CacheReferenceAttributeDTO;
import com.amdocs.aia.il.configuration.dto.CacheReferenceEntityDTO;
import com.amdocs.aia.il.configuration.dto.ChangeStatusDTO;
import com.amdocs.aia.il.configuration.mapper.cache.CacheAttributeMapper;
import com.amdocs.aia.il.configuration.mapper.cache.CacheEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CacheEntityMapperUnitTest {
    private static final String PROJECT_KEY = "aia";

    @MockBean
    private CacheEntityMapper cacheEntityMapper;

    @MockBean
    private CacheAttributeMapper cacheAttributeMapper;

    @TestConfiguration
    static class CacheEntityMapperConfiguration {
    }

    @BeforeEach
    void before() {
        cacheAttributeMapper = new CacheAttributeMapper();
        cacheEntityMapper = new CacheEntityMapper(cacheAttributeMapper);
    }

    @Test
    void whenCacheEntityModelToCacheReferenceEntityDTO_ShouldBeEqual() {
        final CacheEntity cacheEntity = getCacheEntityModel("cacheReferenceEntityKey");
        final CacheReferenceEntityDTO cacheReferenceEntityDTO = cacheEntityMapper.toDTO(cacheEntity);

        assertEquals(cacheEntity.getEntityKey(), cacheReferenceEntityDTO.getCacheReferenceEntityKey());
        assertEquals(cacheEntity.getName(), cacheReferenceEntityDTO.getCacheReferenceEntityName());
        assertEquals(cacheEntity.getDescription(), cacheReferenceEntityDTO.getDescription());
        assertEquals(cacheEntity.getAttributes().size(), cacheReferenceEntityDTO.getCacheReferenceAttributes().size());
    }

    @Test
    void whenCacheReferenceEntityDTOToCacheEntityModel_ShouldBeEqual() {
        final CacheReferenceEntityDTO cacheReferenceEntityDTO = getCacheReferenceEntityDTO("cacheReferenceEntityKey");
        final CacheEntity cacheEntity = cacheEntityMapper.toModel(PROJECT_KEY, cacheReferenceEntityDTO);

        assertEquals(cacheReferenceEntityDTO.getCacheReferenceEntityKey(), cacheEntity.getEntityKey());
        assertEquals(cacheReferenceEntityDTO.getCacheReferenceEntityName(), cacheEntity.getName());
        assertEquals(cacheReferenceEntityDTO.getDescription(), cacheEntity.getDescription());
        assertEquals(cacheReferenceEntityDTO.getCacheReferenceAttributes().size(), cacheEntity.getAttributes().size());
    }

    private static CacheEntity getCacheEntityModel(final String cacheEntityKey) {
        final CacheEntity cacheEntity =new CacheEntity();
        cacheEntity.setEntityKey(cacheEntityKey);
        cacheEntity.setName("cacheReferenceEntityName");
        cacheEntity.setDescription("Description");
        cacheEntity.setAttributes(getCacheReferenceAttributeList());

        return cacheEntity;
    }

    private static CacheReferenceEntityDTO getCacheReferenceEntityDTO(String cacheReferenceEntityKey ) {
        CacheReferenceEntityDTO dto = new CacheReferenceEntityDTO();
        dto.setCacheReferenceEntityKey(cacheReferenceEntityKey);
        dto.setCacheReferenceEntityName("cacheReferenceEntityName");
        dto.setDescription("Description");
        dto.setCacheReferenceAttributes(getCacheReferenceAttributeDTOList());
        dto.setStatus(ChangeStatusDTO.DRAFT);

        return dto;
    }

    private static List<CacheReferenceAttributeDTO> getCacheReferenceAttributeDTOList(){
        CacheReferenceAttributeDTO cacheReferenceAttributeDTO1 = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO1.setAttributeKey("attributeKey1");
        cacheReferenceAttributeDTO1.setAttributeName("attribute Key1");
        cacheReferenceAttributeDTO1.setDescription("attribute description");
        cacheReferenceAttributeDTO1.setKeyPosition(1);
        cacheReferenceAttributeDTO1.setType("type");

        CacheReferenceAttributeDTO cacheReferenceAttributeDTO2 = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO2.setAttributeKey("attributeKey2");
        cacheReferenceAttributeDTO2.setAttributeName("attribute Key2");
        cacheReferenceAttributeDTO2.setDescription("attribute description");
        cacheReferenceAttributeDTO2.setKeyPosition(0);
        cacheReferenceAttributeDTO2.setType("type");

        List<CacheReferenceAttributeDTO> cacheReferenceAttributeDTOS = new ArrayList<>();
        cacheReferenceAttributeDTOS.add(cacheReferenceAttributeDTO1);
        cacheReferenceAttributeDTOS.add(cacheReferenceAttributeDTO2);
        return cacheReferenceAttributeDTOS;
    }

    private static List<CacheAttribute> getCacheReferenceAttributeList(){
        CacheAttribute cacheAttribute1 = new CacheAttribute();
        cacheAttribute1.setAttributeKey("attributeKey1");
        cacheAttribute1.setName("attribute Key1");
        cacheAttribute1.setDescription("attribute description");
        cacheAttribute1.setKeyPosition(1);
        cacheAttribute1.setDatatype("type");

        CacheAttribute cacheAttribute2 = new CacheAttribute();
        cacheAttribute2.setAttributeKey("attributeKey2");
        cacheAttribute2.setName("attribute Key2");
        cacheAttribute2.setDescription("attribute description");
        cacheAttribute2.setKeyPosition(0);
        cacheAttribute2.setDatatype("type");

        List<CacheAttribute> cacheAttributes = new ArrayList<>();
        cacheAttributes.add(cacheAttribute1);
        cacheAttributes.add(cacheAttribute2);
        return cacheAttributes;
    }
}
