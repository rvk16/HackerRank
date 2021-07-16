package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.configuration.discovery.ExternalModelDiscoveryConsumer;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.service.external.ExternalEntityService;
import com.amdocs.aia.il.configuration.service.external.ExternalSchemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The {@link ExternalModelDiscoveryConsumer} implementation that persists the discovered schemas and entities inside
 * the repository
 */
@Component
public class ExternalModelDiscoveryConsumerImpl implements ExternalModelDiscoveryConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalModelDiscoveryConsumerImpl.class);

    private final MessageHelper messageHelper;
    private final ExternalSchemaService externalSchemaService;
    private final ExternalEntityService externalEntityService;

    public ExternalModelDiscoveryConsumerImpl(MessageHelper messageHelper, ExternalSchemaService externalSchemaService, ExternalEntityService externalEntityService) {
        this.messageHelper = messageHelper;
        this.externalSchemaService = externalSchemaService;
        this.externalEntityService = externalEntityService;
    }

    @Override
    public void acceptSchema(ExternalSchema externalSchema) {
        externalSchemaService.getByKey(externalSchema.getProjectKey(), externalSchema.getSchemaKey());
        externalSchemaService.saveModel(externalSchema, false);
    }

    @Override
    public void acceptEntity(ExternalEntity externalEntity) {
        externalEntityService.getByKey(externalEntity.getProjectKey(), externalEntity.getSchemaKey(), externalEntity.getEntityKey());
        LOGGER.info("Persisting discovered entity {}->{}", externalEntity.getSchemaKey(), externalEntity.getEntityKey());
        externalEntityService.saveModel(externalEntity, false);
    }
}
