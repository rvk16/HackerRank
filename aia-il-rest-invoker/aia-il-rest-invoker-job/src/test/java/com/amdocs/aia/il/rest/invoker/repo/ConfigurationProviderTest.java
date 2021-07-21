package com.amdocs.aia.il.rest.invoker.repo;

import com.amdocs.aia.il.rest.invoker.configuration.AutoConfiguration;
import com.amdocs.aia.il.rest.invoker.configuration.ConfigurationProvider;
import com.amdocs.aia.il.rest.invoker.configuration.InvokerConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Invoker configuration provider tests.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AutoConfiguration.class)
@TestPropertySource(properties = {"invoker.schemaKey=test", "invoker.bulkGroupKey=test", "invoker.baseUrl=test",
        "invoker.loadType=initial", "invoker.secured=true", "invoker.adminUser=adminUser", "invoker.user=user",
        "invoker.authP=Tg2Nn7wUZOQ6Xc+1lenkZTQ9Z", "invoker.user=user", "invoker.url=url", "invoker.repo-elements-local-path=classpath:aia_export.zip"})
class ConfigurationProviderTest {

    @Autowired
    private InvokerConfiguration configuration;
    @Autowired
    private ConfigurationProvider configurationProvider;

    @Test
    void readConfiguration() {
        assertTrue(configurationProvider.hasSharedData());
        assertEquals(10, configurationProvider.getExternalSchemas().size());
        assertEquals(2, configurationProvider.getBulkGroups().size());
    }

    @Test
    void emptyConfiguration() {
        final String repoElementsLocalPath = configuration.getRepoElementsLocalPath();
        configuration.setRepoElementsLocalPath("config-empty.zip");
        try {
            final ConfigurationProvider configurationProvider = new ConfigurationProvider(configuration);
            configurationProvider.init();
            assertFalse(configurationProvider.hasSharedData());
            assertEquals(0, configurationProvider.getBulkGroups().size());
            assertEquals(0, configurationProvider.getExternalSchemas().size());
        } finally {
            configuration.setRepoElementsLocalPath(repoElementsLocalPath);
        }
    }
}
