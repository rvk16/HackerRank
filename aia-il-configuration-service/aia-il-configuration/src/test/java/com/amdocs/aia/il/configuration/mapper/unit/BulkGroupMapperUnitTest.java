package com.amdocs.aia.il.configuration.mapper.unit;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.il.common.model.bulk.BulkGroup;
import com.amdocs.aia.il.common.model.bulk.EntityFilterRef;
import com.amdocs.aia.il.configuration.dto.*;
import com.amdocs.aia.il.configuration.mapper.BulkGroupMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BulkGroupMapperUnitTest {
    private static final String PROJECT_KEY = "aia";

    private BulkGroupMapper bulkGroupMapper;

    @TestConfiguration
    static class BulkGroupMapperConfiguration {
    }

    @BeforeEach
    void before() {
        bulkGroupMapper = new BulkGroupMapper();
    }


    @Test
    void whenPhysicalEntityStoreToPhysicalEntityStoreDTO_ShouldBeEqual() {
        final BulkGroup bulkGroup = getBulkGroupModel("SOM");
        final BulkGroupDTO bulkGroupDTO = bulkGroupMapper.toDTO(bulkGroup);

        assertEquals(bulkGroup.getName(), bulkGroupDTO.getBulkGroupName());
        assertEquals(bulkGroup.getKey(), bulkGroupDTO.getBulkGroupKey());
        assertEquals(bulkGroup.getSchemaKey(), bulkGroupDTO.getSchemaKey());
        assertEquals(bulkGroup.getEntityFilters().size(),bulkGroupDTO.getEntityFilters().size());
    }

    private static BulkGroup getBulkGroupModel(final String schemaStoreKey) {
        final BulkGroup bulkGroup = new BulkGroup();
        bulkGroup.setProjectKey(PROJECT_KEY);
        bulkGroup.setDescription(schemaStoreKey + "description");
        bulkGroup.setName("BulkGroup Full");
        bulkGroup.setKey("full");
        bulkGroup.setOriginProcess(OriginProcess.MAPPING_SHEETS_MIGRATION);
        EntityFilterRef entityFilter = new EntityFilterRef();
        entityFilter.setEntityKey("Customer");
        entityFilter.setEntityFilterKey("Filter");
        Set<EntityFilterRef> entityFilters = new HashSet<>();
        entityFilters.add(entityFilter);
        bulkGroup.setEntityFilters(entityFilters);
        return bulkGroup;
    }

    @Test
    void whenBulkGroupDTOToBulkGroup_ShouldBeEqual() {
        final BulkGroupDTO bulkGroupDTO = getBulkGroupDTO("BCMAPPDataChannel");
        final BulkGroup bulkGroup = bulkGroupMapper.toModel(PROJECT_KEY, bulkGroupDTO);

        assertEquals(bulkGroupDTO.getBulkGroupKey(), bulkGroup.getKey());
        assertEquals(bulkGroupDTO.getSchemaKey(), bulkGroup.getSchemaKey());
        assertEquals(bulkGroupDTO.getBulkGroupName(), bulkGroup.getName());
        assertEquals(bulkGroupDTO.getEntityFilters().size(),bulkGroup.getEntityFilters().size());
    }

    private static BulkGroupDTO getBulkGroupDTO(String schemaStoreKey) {
        final BulkGroupDTO bulkGroupDTO = new BulkGroupDTO();

        EntityFilterRefDTO entityFilterRefDTO = new EntityFilterRefDTO();
        entityFilterRefDTO.entityFilterKey("FilterKey").entityKey("Customer");
        List<EntityFilterRefDTO> entityFilters = new ArrayList<>();
        entityFilters.add(entityFilterRefDTO);
        bulkGroupDTO.bulkGroupName("BulkGroup Full").schemaKey(schemaStoreKey)
                .bulkGroupKey("full").entityFilters(entityFilters);

        return bulkGroupDTO;
    }
}
