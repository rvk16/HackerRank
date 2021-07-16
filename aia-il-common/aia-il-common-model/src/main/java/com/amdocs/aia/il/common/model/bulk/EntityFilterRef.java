package com.amdocs.aia.il.common.model.bulk;

import java.io.Serializable;
import java.util.Objects;

public class EntityFilterRef implements Serializable {
    private static final long serialVersionUID = 1234573699905838671L;

    private String entityFilterKey;
    private String entityKey;

    public EntityFilterRef() {
        // default constructor
    }
    public EntityFilterRef(final String entityFilterKey, final String entityKey) {
        this.entityFilterKey = entityFilterKey;
        this.entityKey = entityKey;
    }

    public String getEntityFilterKey() {
        return entityFilterKey;
    }

    public void setEntityFilterKey(final String entityFilterKey) {
        this.entityFilterKey = entityFilterKey;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(final String entityKey) {
        this.entityKey = entityKey;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EntityFilterRef that = (EntityFilterRef) o;
        return Objects.equals(entityFilterKey, that.entityFilterKey) && Objects.equals(entityKey, that.entityKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityFilterKey, entityKey);
    }
}