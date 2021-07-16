package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.il.common.model.publisher.PublisherEntityStore;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PublisherEntityStoreRepositoryUnitTest {
    private static final String PROJECT_KEY = "aia";

    @Spy
    @InjectMocks
    private PublisherEntityStoreRepository publisherEntityStoreRepository;

    @Test
    void when_findAllPublisherEntityStoreByProjectKey_shouldReturn() {
        doReturn(Collections.singletonList(getPublisherEntityStore())).when(publisherEntityStoreRepository).findByProjectKey(Mockito.any());
        List<PublisherEntityStore> publisherEntityStores = publisherEntityStoreRepository.findByProjectKey(PROJECT_KEY);
        assertEquals(1, publisherEntityStores.size());
    }

    @Test
    void when_findPublisherEntityStoreByKeys_shouldReturn() {
        PublisherEntityStore publisherEntityStore = getPublisherEntityStore();
        doReturn(publisherEntityStore).when(publisherEntityStoreRepository).getByKey(Mockito.any(), Mockito.any(), Mockito.any());
        PublisherEntityStore actual = publisherEntityStoreRepository.getByKey(PROJECT_KEY, publisherEntityStore.getSchemaStoreKey(), publisherEntityStore.getEntityStoreKey());
        assertEquals(publisherEntityStore.getSchemaStoreKey(), actual.getSchemaStoreKey());
        assertEquals(publisherEntityStore.getProjectKey(), actual.getProjectKey());
    }

    private static PublisherEntityStore getPublisherEntityStore() {
        PublisherEntityStore publisherEntityStore = new PublisherEntityStore();
        publisherEntityStore.setAttributeStores(Collections.emptyList());
        publisherEntityStore.setAssignedAttributeNumericKey(Collections.emptyMap());
        publisherEntityStore.setProjectKey(PROJECT_KEY);
        publisherEntityStore.setSchemaStoreKey("BCMAPP");
        publisherEntityStore.setDescription(publisherEntityStore.getSchemaStoreKey() + "description");
        publisherEntityStore.setEntityName("Customer");
        publisherEntityStore.setEntityStoreKey("Customer");
        publisherEntityStore.setLogicalEntityKey("Customer");
        publisherEntityStore.setLogicalSchemaKey("aLDMCustomer");
        publisherEntityStore.setOriginProcess(OriginProcess.MAPPING_SHEETS_MIGRATION);
        publisherEntityStore.setSerializationId(1);
        publisherEntityStore.setStoreType(StoreTypeCategory.PUBLISHER_STORE);

        return publisherEntityStore;
    }
}