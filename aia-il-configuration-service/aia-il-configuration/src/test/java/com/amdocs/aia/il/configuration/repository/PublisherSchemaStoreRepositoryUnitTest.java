package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.publisher.PublisherSchemaStore;
import com.amdocs.aia.il.common.model.stores.NumericKeyAssignmentPolicy;
import com.amdocs.aia.il.common.model.stores.SchemaStoreCategory;
import com.amdocs.aia.il.common.model.stores.SourceTargetType;
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
public class PublisherSchemaStoreRepositoryUnitTest {
    private static final String PROJECT_KEY = "aia";

    @Spy
    @InjectMocks
    private PublisherSchemaStoreRepository publisherSchemaStoreRepository;

    @Test
    void when_findAllPublisherSchemaStoreByProjectKey_shouldReturn() {
        doReturn(Collections.singletonList(getPublisherSchemaStore())).when(publisherSchemaStoreRepository).findByProjectKey(Mockito.any());
        List<PublisherSchemaStore> publisherSchemaStoreList = publisherSchemaStoreRepository.findByProjectKey(PROJECT_KEY);
        assertEquals(1, publisherSchemaStoreList.size());
    }

    @Test
    void when_findPublisherSchemaStoreByKeys_shouldReturn() {
        PublisherSchemaStore publisherSchemaStore = getPublisherSchemaStore();
        doReturn(publisherSchemaStore).when(publisherSchemaStoreRepository).getByKey(Mockito.any(), Mockito.any());
        PublisherSchemaStore actual = publisherSchemaStoreRepository.getByKey(PROJECT_KEY, publisherSchemaStore.getSchemaStoreKey());
        assertEquals(publisherSchemaStore.getSchemaStoreKey(), actual.getSchemaStoreKey());
        assertEquals(publisherSchemaStore.getProjectKey(), actual.getProjectKey());
        assertEquals(publisherSchemaStore.getCategory(), actual.getCategory());
    }

    private PublisherSchemaStore getPublisherSchemaStore() {
        PublisherSchemaStore publisherSchemaStore = new PublisherSchemaStore();
        publisherSchemaStore.setSchemaStoreKey("BCMAPP");
        publisherSchemaStore.setAssignedEntityNumericKey(Collections.emptyMap());
        publisherSchemaStore.setSchemaName("bcmapp");
        publisherSchemaStore.setProjectKey(PROJECT_KEY);
        publisherSchemaStore.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        publisherSchemaStore.setDescription(publisherSchemaStore.getSchemaStoreKey() + "description");
        publisherSchemaStore.setCategory(SchemaStoreCategory.PRIVATE);
        publisherSchemaStore.setSourceTarget(SourceTargetType.SOURCE);
        publisherSchemaStore.setStoreType(StoreTypeCategory.PUBLISHER_STORE);
        publisherSchemaStore.setNumericKeyAssignmentPolicy(NumericKeyAssignmentPolicy.AUTOMATIC);
        publisherSchemaStore.setReferenceIds(Collections.emptySet());

        return publisherSchemaStore;
    }
}