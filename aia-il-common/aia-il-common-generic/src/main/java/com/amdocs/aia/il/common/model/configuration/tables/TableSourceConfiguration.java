package com.amdocs.aia.il.common.model.configuration.tables;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * Created by SWARNIMJ
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReplicaStoreSourceConfiguration.class, name = ReplicaStoreSourceConfiguration.TYPE),
})
public interface TableSourceConfiguration extends Serializable {
    String getType();

    void setType(String type);
}