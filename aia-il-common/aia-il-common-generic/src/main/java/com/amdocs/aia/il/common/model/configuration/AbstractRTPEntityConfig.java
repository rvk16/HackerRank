package com.amdocs.aia.il.common.model.configuration;


/**
 * Abstract config class for all publishing entities
 */
public abstract class AbstractRTPEntityConfig implements IRTPRuntimeConfig {


    protected final String name;

    protected String targetEntity ;

    public AbstractRTPEntityConfig(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract RTPEntityType getRTPEntityType();

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    @Override
    public void validate() {
    }

    @Override
    public String toString() {
        return "AbstractRTPEntityConfig{" +
                "name='" + name + '\'' +
                ", type='" + getRTPEntityType().name() + '\'' +
                '}';
    }
}
