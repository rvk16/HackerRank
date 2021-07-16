package com.amdocs.aia.il.common.model.configuration.entity;

import java.util.Objects;

/**
 * Immutable unique entity key.
 *
 * @author SWARNIMJ
 */
public class EntityKey {
    private final String schemaStoreKey;
    private final String entityStoreKey;

    private EntityKey(final String schemaStoreKey, final String entityStoreKey) {
        this.schemaStoreKey = schemaStoreKey;
        this.entityStoreKey = entityStoreKey;
    }

    public static EntityKey of(final String schemaStoreKey, final String entityStoreKey) {
        return new EntityKey(schemaStoreKey, entityStoreKey);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EntityKey entityKey = (EntityKey) o;
        return schemaStoreKey.equals(entityKey.schemaStoreKey) &&
                entityStoreKey.equals(entityKey.entityStoreKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemaStoreKey, entityStoreKey);
    }
}