package com.amdocs.aia.il.configuration.discovery;

import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SimpleExternalModelDiscoveryConsumer implements ExternalModelDiscoveryConsumer {
    private List<ExternalSchema> schemas = new ArrayList<>();
    private List<ExternalEntity> entities = new ArrayList<>();

    private boolean started;
    private boolean completed;

    public void clear() {
        schemas.clear();
        entities.clear();
        started = false;
        completed = false;
    }

    @Override
    public void discoveryStarted() {
        this.started = true;
    }

    @Override
    public void discoveryCompleted() {
        this.completed = true;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void acceptSchema(ExternalSchema externalSchema) {
        schemas.add(externalSchema);
    }

    @Override
    public void acceptEntity(ExternalEntity externalEntity) {
        entities.add(externalEntity);
    }

    public List<ExternalSchema> getSchemas() {
        return schemas;
    }

    public List<ExternalEntity> getEntities() {
        return entities;
    }

    public void flushToFileSystem(String targetDirectory) {
        schemas.forEach(schema -> flushFile(
                Paths.get(targetDirectory, ConfigurationConstants.PRODUCT_KEY, ExternalSchema.ELEMENT_TYPE, schema.getId()),
                schema));
        entities.forEach(entity -> flushFile(
                Paths.get(targetDirectory, ConfigurationConstants.PRODUCT_KEY, ExternalEntity.ELEMENT_TYPE, entity.getId()),
                entity));
    }

    private void flushFile(Path path, Object data) {
        try {
            FileUtils.write(path.toFile(), new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
