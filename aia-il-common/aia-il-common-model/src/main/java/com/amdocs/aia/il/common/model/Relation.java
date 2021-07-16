package com.amdocs.aia.il.common.model;

import javax.validation.constraints.NotNull;

public class Relation {

    @NotNull
    private String attributeKey;

    @NotNull
    private String parentSchemaKey;

    @NotNull
    private String parentEntityKey;

    @NotNull
    private String parentAttributeKey;

    public String getAttributeKey() {
        return attributeKey;
    }

    public Relation setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
        return this;
    }

    public String getParentSchemaKey() {
        return parentSchemaKey;
    }

    public Relation setParentSchemaKey(String parentSchemaKey) {
        this.parentSchemaKey = parentSchemaKey;
        return this;
    }

    public String getParentEntityKey() {
        return parentEntityKey;
    }

    public Relation setParentEntityKey(String parentEntityKey) {
        this.parentEntityKey = parentEntityKey;
        return this;
    }

    public String getParentAttributeKey() {
        return parentAttributeKey;
    }

    public Relation setParentAttributeKey(String parentAttributeKey) {
        this.parentAttributeKey = parentAttributeKey;
        return this;
    }
}
