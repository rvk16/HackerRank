package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.il.common.model.configuration.transformation.Context;
import com.amdocs.aia.il.common.model.configuration.transformation.ContextEntity;
import com.amdocs.aia.il.common.model.configuration.transformation.ContextEntityRelationType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;


@ExtendWith(SpringExtension.class)
public class ContextDependencyAnalyzerUnitTest {

    @InjectMocks
    private ContextDependencyAnalyzer contextDependencyAnalyzer;

    @Mock
    private DataChannelStoreType dataChannelStoreType;

    @Test
    public void whenGetContextDependency_shouldReturnDependency(){
        Context context = new Context();
        context.setProjectKey("aia");
        context.setContextKey("contextKey");
        List<ContextEntity> contextEntityList = new ArrayList<>();
        contextEntityList.add(createContextEntity(1));
        contextEntityList.add(createContextEntity(2));
        context.setContextEntities(contextEntityList);
        List<ElementDependency> dependencies = contextDependencyAnalyzer.getDependencies(context);
        assertEquals(2,dependencies.size());

        assertEquals(context.getContextEntities().get(0).getEntityStoreKey(), dependencies.get(0).getElementName());

        assertEquals(context.getContextEntities().get(1).getEntityStoreKey(), dependencies.get(1).getElementName());
        assertEquals("ExternalEntity",dependencies.get(0).getElementType());
        assertEquals("ExternalEntity",dependencies.get(1).getElementType());
    }

    @Test

    public void whenGetContextDependencyForRef_shouldReturnDependency(){
        Context context = new Context();
        context.setProjectKey("aia");
        context.setContextKey("contextKey");
        List<ContextEntity> contextEntityList = new ArrayList<>();
        contextEntityList.add(createContextEntityREF(1));
        contextEntityList.add(createContextEntityREF(2));
        context.setContextEntities(contextEntityList);
        doReturn("SchemaStoreKey1DataChannel").when(dataChannelStoreType).generateSchemaStoreKeyForLogical(eq("SchemaStoreKey1"));
        doReturn("SchemaStoreKey2DataChannel").when(dataChannelStoreType).generateSchemaStoreKeyForLogical(eq("SchemaStoreKey2"));
        List<ElementDependency> dependencies = contextDependencyAnalyzer.getDependencies(context);
        assertEquals(2,dependencies.size());
        assertEquals("SHARED_aia_ENS_DATA_CHANNEL_SchemaStoreKey1DataChannel_refEntityKey1", dependencies.get(0).getElementId());
        assertEquals("SHARED_aia_ENS_DATA_CHANNEL_SchemaStoreKey2DataChannel_refEntityKey2", dependencies.get(1).getElementId());

        assertEquals(context.getContextEntities().get(0).getEntityStoreKey(), dependencies.get(0).getElementName());


        //SHARED_aia_ENS_DATA_CHANNEL_aLDMCustomerDataChannel_GEONAME_REF
        assertEquals(context.getContextEntities().get(1).getEntityStoreKey(), dependencies.get(1).getElementName());
        assertEquals("ENS",dependencies.get(0).getElementType());
        assertEquals("ENS",dependencies.get(1).getElementType());
    }

    @Test

    public void whenGetContextDependencyForCache_shouldReturnDependency(){
        Context context = new Context();
        context.setProjectKey("aia");
        context.setContextKey("contextKey");
        List<ContextEntity> contextEntityList = new ArrayList<>();
        contextEntityList.add(createContextEntityCache(1));
        contextEntityList.add(createContextEntityCache(2));
        context.setContextEntities(contextEntityList);
        List<ElementDependency> dependencies = contextDependencyAnalyzer.getDependencies(context);
        assertEquals(2,dependencies.size());
        assertEquals(context.getContextEntities().get(0).getEntityStoreKey(), dependencies.get(0).getElementName());
        assertEquals(context.getContextEntities().get(1).getEntityStoreKey(), dependencies.get(1).getElementName());
        assertEquals("CacheEntity",dependencies.get(0).getElementType());
        assertEquals("CacheEntity",dependencies.get(1).getElementType());
}

    @Test
    public void whenGetContextDependency_shouldReturnDependency_NoContextEntities() {
        Context context = new Context();
        context.setProjectKey("aia");
        context.setContextKey("contextKey");
        List<ContextEntity> contextEntityList = new ArrayList<>();
        context.setContextEntities(contextEntityList);
        List<ElementDependency> dependencies = contextDependencyAnalyzer.getDependencies(context);
        assertEquals(0,dependencies.size());
    }

    /**
     * Test the flow of method getDependencies(PublisherContext element)
     * When element.getContextEntities() is not empty
     * doesn't throw any exception
     * returns not empty list
     */

    @Test

    void when_getDependencies_withContextEntitiesNotEmpty() {
        final Context context = getPublisherContext("customer");
        context.setContextEntities(Collections.singletonList(createContextEntity(1)));
        List<ElementDependency> deps = contextDependencyAnalyzer.getDependencies(context);
        assertEquals(1, deps.size());
    }

    /**
     * Test the flow of method getDependencies(PublisherContext element)
     * When element.getContextEntities() is not empty
     * doesn't throw any exception
     * returns null
     */
    @Test
    void when_getPublicFeatures_shouldReturnNull() {
        final Context context = getPublisherContext("customer");
        assertEquals(Collections.emptyList(),contextDependencyAnalyzer.getPublicFeatures(context));
    }

    @Test
    void when_getPublicFeatures_shouldReturnNotNull_NOFK() {
        Context context = new Context();
        context.setProjectKey("aia");
        context.setContextKey("contextKey");
        List<ContextEntity> contextEntityList = new ArrayList<>();
        contextEntityList.add(createContextEntity(1));
        contextEntityList.add(createContextEntity(2));
        context.setContextEntities(contextEntityList);
        assertEquals(2,contextDependencyAnalyzer.getPublicFeatures(context).size());
    }

    @Test
    void when_getPublicFeatures_shouldReturnNotNull_FK() {
        Context context = new Context();
        context.setProjectKey("aia");
        context.setContextKey("contextKey");
        List<ContextEntity> contextEntityList = new ArrayList<>();
        contextEntityList.add(createContextEntityWithFK(1));
        contextEntityList.add(createContextEntityWithFK(2));
        context.setContextEntities(contextEntityList);
        assertEquals(2,contextDependencyAnalyzer.getPublicFeatures(context).size());
    }


    private Context getPublisherContext(final String name) {
        Context context = new Context();
        context.setProjectKey("aia");
        context.setContextEntities(Collections.emptyList());
        context.setContextEntities(Collections.emptyList());
        return context;
    }

    public ContextEntity createContextEntity(int i){
        ContextEntity contextEntity =  new ContextEntity();
        contextEntity.setSchemaStoreKey("SchemaStoreKey"+i);
        contextEntity.setEntityStoreKey("EntityStoreKey"+i);
        contextEntity.setRelationType(ContextEntityRelationType.LEAD);
        return contextEntity;
    }

    public ContextEntity createContextEntityWithFK(int i){
        ContextEntity contextEntity =  new ContextEntity();
        contextEntity.setSchemaStoreKey("SchemaStoreKey"+i);
        contextEntity.setEntityStoreKey("EntityStoreKey"+i);
        contextEntity.setParentContextEntityKey("EntityStoreKey"+i);
        contextEntity.setForeignKeys("FK"+i);
        return contextEntity;
    }

    public ContextEntity createContextEntityREF(int i){
        ContextEntity contextEntity =  new ContextEntity();
        contextEntity.setSchemaStoreKey("SchemaStoreKey"+i);
        contextEntity.setEntityStoreKey("refEntityKey"+i);
        contextEntity.setRelationType(ContextEntityRelationType.REF);
        return contextEntity;
    }

    public ContextEntity createContextEntityCache(int i){
        ContextEntity contextEntity =  new ContextEntity();
        contextEntity.setSchemaStoreKey("CACHE");
        contextEntity.setEntityStoreKey("CacheEntity"+i);
        contextEntity.setRelationType(ContextEntityRelationType.REF);
        return contextEntity;
    }
}
