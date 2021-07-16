package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.il.common.model.DataChannelSchemaStore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PublishTopic implements Serializable {
    private static final long serialVersionUID = 2415348333982502938L;

    private final String namespace;

    private final Map<String, DataChannelSchemaRow> dataChannelConfigurations = new HashMap<>();

    public PublishTopic(String namespace) {
        this.namespace = namespace;
    }

    public void put(DataChannelSchemaStore dataChannelSchemaStore) {
        dataChannelConfigurations.computeIfAbsent(dataChannelSchemaStore.getSchemaStoreKey(), t -> new DataChannelSchemaRow(dataChannelSchemaStore));
    }

    public Map<String, DataChannelSchemaRow> getDataChannelConfigurations() {
        return dataChannelConfigurations;
    }

    public static final class DataChannelSchemaRow implements Serializable {
        private static final long serialVersionUID = 7923395801253579172L;

        private final DataChannelSchemaStore dataChannelSchemaStore;

        public DataChannelSchemaRow(DataChannelSchemaStore dataChannelSchemaStore) {
            this.dataChannelSchemaStore = dataChannelSchemaStore;
        }

        public DataChannelSchemaStore getDataChannelSchemaStore() {
            return dataChannelSchemaStore;
        }
    }
}