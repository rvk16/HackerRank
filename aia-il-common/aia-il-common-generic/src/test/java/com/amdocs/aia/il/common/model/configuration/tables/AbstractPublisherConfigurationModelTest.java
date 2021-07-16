package com.amdocs.aia.il.common.model.configuration.tables;

import com.amdocs.aia.common.model.repo.ChangeStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@ExtendWith({MockitoExtension.class})
public class AbstractPublisherConfigurationModelTest {

    @Test
    void test_pojo_structure() {
        final AbstractPublisherConfigurationModel model = getMockedAbstractPublisherConfigurationModel();
        Assertions.assertEquals("publisherName", model.getPublisherName());
        Assertions.assertEquals("key", model.getKey());
        Assertions.assertEquals(ChangeStatus.DRAFT, model.getStatus());
        Assertions.assertEquals(0, model.getDependencies().size());
        Assertions.assertEquals(0, model.getPublicFeatures().size());
    }


    private AbstractPublisherConfigurationModel getMockedAbstractPublisherConfigurationModel() {
        AbstractPublisherConfigurationModel model = new AbstractPublisherConfigurationModel() {
        };
        model.setPublisherName("publisherName");
        model.setKey("key");
        model.setStatus(ChangeStatus.DRAFT);
        model.setDependencies(Collections.emptyList());
        model.setPublicFeatures(Collections.emptyList());
        return model;
    }

}
