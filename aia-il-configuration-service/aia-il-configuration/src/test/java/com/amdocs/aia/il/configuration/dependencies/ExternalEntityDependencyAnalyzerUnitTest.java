package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaStoreInfo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
public class ExternalEntityDependencyAnalyzerUnitTest {
    protected static final String PROJECT_KEY = "projectKey";

    @InjectMocks
    private ExternalEntityDependencyAnalyzer externalEntityDependencyAnalyzer;

    @Mock
    private DataChannelStoreType dataChannelStoreType;


    @Test
    void whenGetExternalEntityDependency_shouldReturnDependency() {
        ExternalEntity externalEntity = createExternalEntity();
        ExternalSchema externalSchema = createExternalSchema(Availability.EXTERNAL);
        doReturn("schemaKeyDataChannel").when(dataChannelStoreType).generateSchemaStoreKeyForLogical(eq("schemaKey"));

        List<ElementDependency> dependencies = externalEntityDependencyAnalyzer.getDependencies(externalEntity);
        assertEquals(1, dependencies.size());
        assertEquals(externalEntity.getSchemaKey(), dependencies.get(0).getElementName());
    }

    @Test
    void whenGetExternalEntityTypeSharedDependency_shouldReturnDependency() {
        ExternalEntity externalEntity = createExternalEntity();
        externalEntity.setAvailability(Availability.SHARED);
        ExternalSchema externalSchema = createExternalSchema(Availability.SHARED);
        doReturn("schemaKeyDataChannel").when(dataChannelStoreType).generateSchemaStoreKeyForLogical(eq("aLDMschemaKey"));

        List<ElementDependency> dependencies = externalEntityDependencyAnalyzer.getDependencies(externalEntity);
        assertEquals(2, dependencies.size());
        assertEquals("SHARED_aia_ENS_DATA_CHANNEL_schemaKeyDataChannel_entityKey",dependencies.get(1).getElementId());
        assertEquals(externalEntity.getSchemaKey(), dependencies.get(0).getElementName());
    }

    @Test
    void whenGetExternalEntityPublicFeatures_shouldReturnPublicFeatures() {
        ExternalEntity externalEntity = createExternalEntity();
        List<ElementPublicFeature> publicFeatures = externalEntityDependencyAnalyzer.getPublicFeatures(externalEntity);
        assertEquals(4, publicFeatures.size());
        ElementPublicFeature ElementPublicFeatureQuery1 = publicFeatures.stream().filter(elementPublicFeature -> elementPublicFeature.getKey().equals("Query1")).findAny().orElse(null);
        assertNotNull(ElementPublicFeatureQuery1);
        ElementPublicFeature ElementPublicFeatureAtt1 = publicFeatures.stream().filter(elementPublicFeature -> elementPublicFeature.getKey().equals("att1")).findAny().orElse(null);
        assertNotNull(ElementPublicFeatureAtt1);
    }

    private ExternalEntity createExternalEntity() {
        ExternalEntity externalEntity = new ExternalEntity();
        externalEntity.setEntityKey("entityKey");
        externalEntity.setSchemaKey("schemaKey");
        externalEntity.setSubjectAreaKey("aLDMschemaKey");
        externalEntity.setProjectKey("aia");

        List<ExternalAttribute> externalAttributes = new ArrayList<>();
        ExternalAttribute externalAttribute1 = new ExternalAttribute();
        externalAttribute1.setAttributeKey("att1");

        ExternalAttribute externalAttribute2 = new ExternalAttribute();
        externalAttribute2.setAttributeKey("att2");

        externalAttributes.add(externalAttribute1);
        externalAttributes.add(externalAttribute2);
        externalEntity.setAttributes(externalAttributes);

        ExternalEntityCollectionRules externalEntityCollectionRules = new ExternalCsvEntityCollectionRules();
        List<ExternalEntityFilter> filters = new ArrayList<>();
        ExternalEntityFilter externalEntityFilter1 = new ExternalEntityFilter();
        externalEntityFilter1.setFilterKey("Query1");
        externalEntityFilter1.setFilterLogic("select ENTITY_ID, ENCODING, PAYLOAD, BUCKET from OSS_CONTEXT_STORE where ENTITY_ID='[ENTITYID]'");
        ExternalEntityFilter externalEntityFilter2 = new ExternalEntityFilter();
        externalEntityFilter2.setFilterKey("Query2");
        externalEntityFilter2.setFilterLogic("select ENTITY_ID, ENCODING, PAYLOAD, BUCKET from OSS_CONTEXT_STORE where BUCKET='[BUCKET]'");

        filters.add(externalEntityFilter1);
        filters.add(externalEntityFilter2);
        externalEntityCollectionRules.setFilters(filters);
        externalEntity.setCollectionRules(externalEntityCollectionRules);


        return externalEntity;
    }

    private ExternalSchema createExternalSchema(Availability availability) {
        ExternalSchema externalSchema = new ExternalSchema();
        externalSchema.setSchemaKey("schemaKey");
        externalSchema.setTypeSystem("TypeSystem");
        externalSchema.setIsReference(false);
        externalSchema.setProjectKey("aia");
        externalSchema.setAvailability(availability);
        externalSchema.setSubjectAreaName("aLDM schemaKey");
        externalSchema.setSubjectAreaKey("aLDMschemaKey");
        ExternalSchemaDataChannelInfo externalSchemaDataChannelInfo = new ExternalSchemaDataChannelInfo();

        externalSchemaDataChannelInfo.setDataChannelName("DataChannelName");
        externalSchemaDataChannelInfo.setSerializationMethod("SHAREDJSON");
        externalSchema.setDataChannelInfo(externalSchemaDataChannelInfo);

        externalSchema.setStoreInfo(new ExternalCsvSchemaStoreInfo());
        return externalSchema;

    }

}
