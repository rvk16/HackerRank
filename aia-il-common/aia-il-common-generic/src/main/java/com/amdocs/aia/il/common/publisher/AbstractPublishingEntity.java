package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.il.common.model.configuration.RTPEntityType;

import java.io.Serializable;

/**
 * Abstract class for all entities that should be published
 */
public abstract class AbstractPublishingEntity implements Serializable {


    protected final String name;

    public AbstractPublishingEntity(String name) {
        this.name = name;
    }

    public abstract RTPEntityType getPublishingEntityType();

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "AbstractPublishingEntity{" +
                "name='" + name +
                '}';
    }
}
