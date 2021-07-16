package com.amdocs.aia.il.common.model.configuration.tables;

import com.amdocs.aia.common.model.store.EntityStoreRef;

/**
 * Created by SWARNIMJ
 */
public class ReplicaStoreSourceConfiguration extends AbstractTableSourceConfiguration {
    private static final long serialVersionUID = -9218255217032910563L;

    public static final String TYPE = "PUBLISHER";

    private EntityStoreRef sourceEntityStore;

    public ReplicaStoreSourceConfiguration() {
        super(TYPE);
    }

    public ReplicaStoreSourceConfiguration(final EntityStoreRef sourceEntityStore) {
        this();
        this.sourceEntityStore = sourceEntityStore;
    }

    public EntityStoreRef getSourceEntityStore() {
        return sourceEntityStore;
    }

    public void setSourceEntityStore(EntityStoreRef sourceEntityStore) {
        this.sourceEntityStore = sourceEntityStore;
    }
}