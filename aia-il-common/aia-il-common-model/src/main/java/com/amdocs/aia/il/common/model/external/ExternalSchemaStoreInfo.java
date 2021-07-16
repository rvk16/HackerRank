package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaStoreInfo;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaStoreInfo;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaStoreInfo;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExternalCsvSchemaStoreInfo.class, name = ExternalSchemaStoreTypes.CSV),
        @JsonSubTypes.Type(value = ExternalSqlSchemaStoreInfo.class, name = ExternalSchemaStoreTypes.SQL),
        @JsonSubTypes.Type(value = ExternalKafkaSchemaStoreInfo.class, name = ExternalSchemaStoreTypes.KAFKA)
})
public interface ExternalSchemaStoreInfo extends Serializable {
    String getType();
    PhysicalStoreType getPhysicalStoreType();
}
