package com.amdocs.aia.il.common.model.configuration.tables;

import com.amdocs.aia.common.model.repo.ChangeStatus;
import com.amdocs.aia.il.common.model.configuration.ConfigurationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class ConfigurationUtilsTest {

    // Test static methods

    @Test
    void test_getElementType() {
        String modelClass = ConfigurationUtils.getElementType(AbstractPublisherConfigurationModel.class);
        Assertions.assertEquals("AbstractPublisherConfigurationModel", modelClass);
    }

    @Test
    void test_getElementId() {
        String modelClass = ConfigurationUtils.getElementId(getMockedAbstractPublisherConfigurationModel());
        Assertions.assertEquals("INTEGRATIONLAYER_project_key__key", modelClass);
    }

    private AbstractPublisherConfigurationModel getMockedAbstractPublisherConfigurationModel() {
        AbstractPublisherConfigurationModel model = new AbstractPublisherConfigurationModel() {
        };
        model.setPublisherName("publisherName");
        model.setProjectKey("project_key");
        model.setKey("key");
        model.setStatus(ChangeStatus.DRAFT);
        model.setDependencies(Collections.emptyList());
        model.setPublicFeatures(Collections.emptyList());
        return model;
    }

}
