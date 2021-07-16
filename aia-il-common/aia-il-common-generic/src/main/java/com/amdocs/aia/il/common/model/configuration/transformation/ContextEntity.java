package com.amdocs.aia.il.common.model.configuration.transformation;

import com.amdocs.aia.common.model.transformation.TransformationContextEntity;

import java.io.Serializable;

/**
 * Created by SWARNIMJ
 */
public class ContextEntity implements Serializable {

    private static final long serialVersionUID =-5635539736074134064L;

    private String aliasedSourceEntityKey;
    private String schemaStoreKey;
    private String entityStoreKey;
    private String sourceAlias;
    private ContextEntityRelationType relationType;
    private boolean doPropagation;
    private String parentContextEntityKey;
    private String foreignKeys;
    private NoReferentAction noReferentAction;

    public String getAliasedSourceEntityKey() {
        return aliasedSourceEntityKey;
    }

    public void setAliasedSourceEntityKey(String aliasedSourceEntityKey) {
        this.aliasedSourceEntityKey = aliasedSourceEntityKey;
    }

    public String getSchemaStoreKey() {
        return schemaStoreKey;
    }

    public void setSchemaStoreKey(String schemaStoreKey) {
        this.schemaStoreKey = schemaStoreKey;
    }

    public String getEntityStoreKey() {
        return entityStoreKey;
    }

    public void setEntityStoreKey(String entityStoreKey) {
        this.entityStoreKey = entityStoreKey;
    }

    public String getSourceAlias() {
        return sourceAlias;
    }

    public void setSourceAlias(String sourceAlias) {
        this.sourceAlias = sourceAlias;
    }

    public ContextEntityRelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(ContextEntityRelationType relationType) {
        this.relationType = relationType;
    }

    public boolean isDoPropagation() {
        return doPropagation;
    }

    public void setDoPropagation(boolean doPropagation) {
        this.doPropagation = doPropagation;
    }

    public String getParentContextEntityKey() {
        return parentContextEntityKey;
    }

    public void setParentContextEntityKey(String parentContextEntityKey) {
        this.parentContextEntityKey = parentContextEntityKey;
    }

    public String getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(String foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public NoReferentAction getNoReferentAction() {
        return noReferentAction;
    }

    public void setNoReferentAction(NoReferentAction noReferentAction) {
        this.noReferentAction = noReferentAction;
    }

    public TransformationContextEntity toSharedElement() {
        TransformationContextEntity contextEntity = new TransformationContextEntity();
        contextEntity.setSchemaStoreKey(this.schemaStoreKey);
        contextEntity.setEntityStoreKey(this.entityStoreKey);
        return contextEntity;
    }

    @Override
    public String toString() {
        return "ContextEntity{" +
                "aliasedSourceEntityKey='" + aliasedSourceEntityKey + '\'' +
                ", schemaStoreKey='" + schemaStoreKey + '\'' +
                ", entityStoreKey='" + entityStoreKey + '\'' +
                ", sourceAlias='" + sourceAlias + '\'' +
                ", relationType=" + relationType +
                ", doPropagation=" + doPropagation +
                ", parentContextEntityKey='" + parentContextEntityKey + '\'' +
                ", foreignKeys='" + foreignKeys + '\'' +
                ", noReferentAction=" + noReferentAction +
                '}';
    }
}
