package com.amdocs.aia.il.configuration.mapper.unit;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.configuration.transformation.Context;
import com.amdocs.aia.il.common.model.configuration.transformation.ContextEntity;
import com.amdocs.aia.il.configuration.dto.ContextDTO;
import com.amdocs.aia.il.configuration.dto.ContextEntityDTO;
import com.amdocs.aia.il.configuration.mapper.ContextMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContextMapperUnitTest {
    private static final String PROJECT_KEY = "aia";

    private ContextMapper contextMapper;

    @TestConfiguration
    static class PublisherContextMapperConfiguration {
    }

    @BeforeEach
    void before() {
        contextMapper = new ContextMapper();
    }

    @Test
    void whenPublisherContextToPublisherContextDTO_ShouldBeEqual() {
        final Context context = getPublisherContextModel("Customer");
        final ContextDTO contextDTO = contextMapper.toDTO(context);

        assertEquals(context.getPublisherName(), contextDTO.getStoreName());
        assertEquals(context.getDescription(), contextDTO.getDescription());
        assertEquals(context.getName(), contextDTO.getDisplayName());
        assertEquals(context.getProjectKey(), contextDTO.getProjectKey());
        assertEquals(context.getContextKey(), contextDTO.getContextKey());
    }

    private static Context getPublisherContextModel(final String entity) {
        final Context context = new Context();
        context.setDescription(entity + "description");
        context.setName(entity);
        context.setProjectKey(PROJECT_KEY);
        context.setContextKey(entity + "PublisherContext");
        context.setPublisherName(context.getContextKey());
        context.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        context.setContextEntities(new ArrayList<>());
        return context;
    }

    @Test
    void whenPublisherContextDTOToPublisherContext_ShouldBeEqual() {
        final ContextDTO contextDTO = getContextDTO("Customer");
        final Context context = contextMapper.toModel(PROJECT_KEY, contextDTO);

        assertEquals(contextDTO.getStoreName(), context.getPublisherName());
        assertEquals(contextDTO.getDescription(), context.getDescription());
        assertEquals(contextDTO.getDisplayName(), context.getName());
        assertEquals(contextDTO.getStoreName(), context.getKey());
        assertEquals(contextDTO.getProjectKey(), context.getProjectKey());
    }

    @Test
    void toModel_withContextEntites_withOrigins() {
        final ContextDTO contextDTO = getContextDTO("Customer");
        final ContextEntityDTO entityDTO = getPublisherContextEntityDTOWithRefAction("customer");
        final List<ContextEntityDTO> entityDTOList = Collections.singletonList(entityDTO);
        contextDTO.contextEntities(entityDTOList);
        contextDTO.origin("CUSTOM");
        final Context context = contextMapper.toModel(PROJECT_KEY, contextDTO);

        assertEquals(contextDTO.getContextEntities().size(), entityDTOList.size());
        assertEquals(OriginProcess.CUSTOM, context.getOriginProcess());
    }

    @Test
    void toModel_withContextEntites_withRefAction() {
        final ContextDTO contextDTO = getContextDTO("Customer");
        final ContextEntityDTO entityDTO = getPublisherContextEntityDTOWithRefAction("customer");
        final List<ContextEntityDTO> entityDTOList = Collections.singletonList(entityDTO);
        contextDTO.contextEntities(entityDTOList);
        final Context context = contextMapper.toModel(PROJECT_KEY, contextDTO);

        assertEquals(contextDTO.getContextEntities().size(), entityDTOList.size());
        ContextEntity entity = context.getContextEntities().get(0);
        assertEquals(entity.getEntityStoreKey(), entityDTO.getEntityStoreKey());
        assertEquals(entity.getAliasedSourceEntityKey(), entityDTO.getAliasedSourceEntityKey());
        assertEquals(entity.getForeignKeys(), entityDTO.getForeignKeys());
        assertEquals(entity.getNoReferentAction().toString(), entityDTO.getNoReferentAction().toString());
    }

    @Test
    void toModel_withContextEntites_withNoRefAction() {
        final ContextDTO contextDTO = getContextDTO("Customer");
        final ContextEntityDTO entityDTO = getPublisherContextEntityDTO("customer");
        final List<ContextEntityDTO> entityDTOList = Collections.singletonList(entityDTO);
        contextDTO.contextEntities(entityDTOList);
        final Context context = contextMapper.toModel(PROJECT_KEY, contextDTO);

        assertEquals(contextDTO.getContextEntities().size(), entityDTOList.size());
        ContextEntity entity = context.getContextEntities().get(0);
        assertEquals(entity.getEntityStoreKey(), entityDTO.getEntityStoreKey());
        assertEquals(entity.getAliasedSourceEntityKey(), entityDTO.getAliasedSourceEntityKey());
        assertEquals(entity.getForeignKeys(), entityDTO.getForeignKeys());
        assertNull(entity.getNoReferentAction());
    }

    private static ContextDTO getContextDTO(String entity) {
        final ContextDTO contextDTO = new ContextDTO();
        contextDTO.projectKey(PROJECT_KEY);
        contextDTO.storeName(entity + "PublisherContext");
        contextDTO.displayName(entity);
        contextDTO.description(entity + "description");
        return contextDTO;
    }

    private static ContextEntityDTO getPublisherContextEntityDTO(String entity) {
        final ContextEntityDTO entityDTO = new ContextEntityDTO();
        entityDTO.entityStoreKey(entity + "_store_key");
        entityDTO.schemaStoreKey(entity + "_schema_store_key");
        entityDTO.parentContextEntityKey(entity + "_context_entity_key");
        entityDTO.aliasedSourceEntityKey(entity + "_aliased_store_entity_key");
        entityDTO.foreignKeys(entity + "_foreign_keys");
        entityDTO.sourceAlias(entity + "_source_alias");
        entityDTO.relationType(ContextEntityDTO.RelationTypeEnum.OTM);
        entityDTO.setDoPropagation(true);
        return entityDTO;
    }

    private static ContextEntityDTO getPublisherContextEntityDTOWithRefAction(String entity) {
        final ContextEntityDTO entityDTO = getPublisherContextEntityDTO(entity);
        entityDTO.noReferentAction(ContextEntityDTO.NoReferentActionEnum.MANDATORY);
        return entityDTO;
    }
}