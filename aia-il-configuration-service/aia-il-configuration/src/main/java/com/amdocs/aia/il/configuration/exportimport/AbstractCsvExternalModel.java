package com.amdocs.aia.il.configuration.exportimport;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public abstract class AbstractCsvExternalModel implements Serializable {

    @JsonProperty(value = "schemaKey",required = true)
    private String schemaKey = null;


    @JsonProperty("description")
    private String description = null;

    @JsonProperty("toDelete")
    private Boolean toDelete = Boolean.FALSE;


    public String getSchemaKey() {
        return schemaKey;
    }

    public void setSchemaKey(String schemaKey) {
        this.schemaKey = schemaKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Boolean getToDelete() {
        return toDelete;
    }

    public void setToDelete(Boolean toDelete) {
        if (toDelete != null) {
            this.toDelete = toDelete;
        }

    }

}
