package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.extensions.storetypes.DataChannelStoreType;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.il.common.model.external.Availability;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.external.ExternalSchemaDataChannelInfo;
import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaStoreInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
public class ExternalSchemaDependencyAnalyzerUnitTest {
    protected static final String PROJECT_KEY = "projectKey";

    @InjectMocks
    private ExternalSchemaDependencyAnalyzer externalSchemaDependencyAnalyzer;

    @Mock
    private DataChannelStoreType dataChannelStoreType;


    @Test
    void whenGetExternalEntityDependency_shouldReturnDependency() {

        ExternalSchema externalSchema = createExternalSchema(Availability.EXTERNAL);
        List<ElementDependency> dependencies = externalSchemaDependencyAnalyzer.getDependencies(externalSchema);
        assertEquals(0, dependencies.size());

    }

    @Test
    void whenGetExternalEntityTypeSharedDependency_shouldReturnDependency() {

        ExternalSchema externalSchema = createExternalSchema(Availability.SHARED);
        doReturn("schemaKeyDataChannel").when(dataChannelStoreType).generateSchemaStoreKeyForLogical(eq("aLDMschemaKey"));

        List<ElementDependency> dependencies = externalSchemaDependencyAnalyzer.getDependencies(externalSchema);
        assertEquals(1, dependencies.size());
        assertEquals("SHARED_aia_SCS_DATA_CHANNEL_schemaKeyDataChannel",dependencies.get(0).getElementId());
        assertEquals(externalSchema.getSchemaKey(), dependencies.get(0).getElementName());
        assertEquals(SchemaStore.ELEMENT_TYPE_CODE, dependencies.get(0).getElementType());
    }

    @Test
    void whenGetExternalEntityPublicFeatures_shouldReturnPublicFeatures() {
        ExternalSchema externalSchema = createExternalSchema(Availability.EXTERNAL);
        List<ElementPublicFeature> publicFeatures = externalSchemaDependencyAnalyzer.getPublicFeatures(externalSchema);
        assertEquals(0, publicFeatures.size());

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