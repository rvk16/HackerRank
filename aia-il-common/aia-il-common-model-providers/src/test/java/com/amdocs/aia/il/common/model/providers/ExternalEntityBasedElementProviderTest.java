package com.amdocs.aia.il.common.model.providers;

import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.IntegrationLayerAttributeStore;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaStoreInfo;
import com.amdocs.aia.il.common.model.physical.csv.CsvEntityStore;
import com.amdocs.aia.il.common.model.physical.kafka.KafkaEntityStore;
import com.amdocs.aia.repo.client.local.LocalFileSystemElementsProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExternalEntityBasedElementProviderTest {
    @Test
    public void testKafkaEntityStoreEnrichment() {
        final String defaultDateFormat = "yyyy/MM/dd";
        final ExternalSchema externalSchema = new LocalFileSystemElementsProvider("classpath:design.zip")
                .getElements(ConfigurationConstants.PRODUCT_KEY,
                        ExternalSchema.ELEMENT_TYPE,
                        ExternalSchema.class)
                .stream()
                .filter(schema -> schema.getSchemaKey().equals("SrcCust"))
                .findFirst()
                .orElse(null);
        assertNotNull(externalSchema);
        assertEquals(defaultDateFormat, ((ExternalKafkaSchemaStoreInfo) externalSchema.getStoreInfo()).getDefaultDateFormat());

        final List<KafkaEntityStore> entityStores = new LocalFileSystemElementsProvider("classpath:design.zip")
                .getElements(ConfigurationConstants.PRODUCT_KEY,
                        KafkaEntityStore.ELEMENT_TYPE,
                        KafkaEntityStore.class);

        final KafkaEntityStore entityStore = entityStores.stream()
                .filter(es -> es.getEntityStoreKey().equals("Address") && es.getSchemaStoreKey().equals("SrcCust"))
                .findFirst()
                .orElse(null);
        assertNotNull(entityStore);
        assertEquals(8, entityStore.getAttributeStores().size());

        final IntegrationLayerAttributeStore testDate1 = entityStore.getAttributeStores().stream().filter(a -> a.getAttributeStoreKey().equals("testDate1")).findFirst().orElse(null);
        final IntegrationLayerAttributeStore testDate2 = entityStore.getAttributeStores().stream().filter(a -> a.getAttributeStoreKey().equals("testDate2")).findFirst().orElse(null);

        assertNotNull(testDate1);
        assertEquals(defaultDateFormat, testDate1.getPropertyValue(KafkaEntityStore.DATE_FORMAT));

        assertNotNull(testDate2);
        assertEquals("yyyy-MM-dd", testDate2.getPropertyValue(KafkaEntityStore.DATE_FORMAT));
    }

    @Test
    public void testKCsvEntityStoreAttributeDateFormatPopulation() {
        final String defaultDateFormat = "yyyy/MM/dd";
        final CsvEntityStore entityStore = new LocalFileSystemElementsProvider("classpath:design.zip")
                .getElements(ConfigurationConstants.PRODUCT_KEY,
                        CsvEntityStore.ELEMENT_TYPE,
                        CsvEntityStore.class)
                .stream()
                .filter(es -> es.getEntityStoreKey().equals("RATED_EVENT") && es.getSchemaStoreKey().equals("TCEVENT"))
                .findFirst()
                .orElse(null);


        assertNotNull(entityStore);

        final IntegrationLayerAttributeStore attributeWithAssignedFormat = entityStore.getAttributeStores().stream().filter(a -> a.getAttributeStoreKey().equals("networkStartTime")).findFirst().orElse(null);
        final IntegrationLayerAttributeStore attributeWithoutAssignedFormat = entityStore.getAttributeStores().stream().filter(a -> a.getAttributeStoreKey().equals("bSSModificationDate")).findFirst().orElse(null);

        assertNotNull(attributeWithoutAssignedFormat);
        assertEquals(defaultDateFormat, attributeWithoutAssignedFormat.getPropertyValue(KafkaEntityStore.DATE_FORMAT));

        assertNotNull(attributeWithAssignedFormat);
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", attributeWithAssignedFormat.getPropertyValue(KafkaEntityStore.DATE_FORMAT));
    }
}
