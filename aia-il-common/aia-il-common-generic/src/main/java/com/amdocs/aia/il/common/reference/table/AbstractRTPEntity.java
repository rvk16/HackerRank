package com.amdocs.aia.il.common.reference.table;

import com.amdocs.aia.il.common.model.configuration.RTPEntityType;

import java.io.Serializable;

/**
 * Abstract class for all entities that should be published
 * Created by ORENKAF on 11/15/2016.
 */
public abstract class AbstractRTPEntity implements Serializable {

    private static final long serialVersionUID = -6703163470563640072L;

    protected final String name;

    public AbstractRTPEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract RTPEntityType getRTPEntityType();

    @Override
    public String toString() {
        return "AbstractPublishingEntity{" +
                "name='" + name +
                '}';
    }
}
