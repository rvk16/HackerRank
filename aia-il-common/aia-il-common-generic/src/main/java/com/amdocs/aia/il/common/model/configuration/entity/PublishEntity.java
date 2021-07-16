package com.amdocs.aia.il.common.model.configuration.entity;

/*
 * @author SWARNIMJ on 5/12/20
 */

import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.model.publisher.PublisherEntityStore;
import com.amdocs.aia.il.common.model.publisher.PublisherSchemaStore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishEntity implements Serializable{

    private final String namespace;
    private final Map<String, SchemaRow> publishTableConfigurations = new HashMap<>();

    public PublishEntity(String namespace) {
        this.namespace = namespace;
    }

    public void put(final PublisherEntityStore publisherEntity, final PublisherSchemaStore publisherSchema) {
        publishTableConfigurations.computeIfAbsent(publisherSchema.getSchemaStoreKey(), k -> new SchemaRow(publisherSchema))
                .add(new ConfigurationRow(publisherEntity, publisherSchema.getTypeSystem()));
    }

    // getting replica db details
    public Map<String, SchemaRow> getTableConfigurations() {
        return publishTableConfigurations;
    }

    public String getNamespace() {
        return namespace;
    }

    /**
     * Immutable schema row.
     *
     * @author SWARNIMJ
     */
    public static final class SchemaRow implements Serializable {

        private static final long serialVersionUID = 5214624591835744578L;

        private final PublisherSchemaStore schemaStore;
        private final List<ConfigurationRow> rows = new ArrayList<>();

        private SchemaRow(final PublisherSchemaStore schemaStore) {
            this.schemaStore = schemaStore;
        }

        private void add(final ConfigurationRow row) {
            rows.add(row);
        }

        public PublisherSchemaStore getSchemaStore() {
            return schemaStore;
        }

        public List<ConfigurationRow> getRows() {
            return rows;
        }
    }
}