package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.il.common.model.external.csv.ExternalCsvAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaAttributeStoreInfo;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlAttributeStoreInfo;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExternalCsvAttributeStoreInfo.class, name = ExternalSchemaStoreTypes.CSV),
        @JsonSubTypes.Type(value = ExternalSqlAttributeStoreInfo.class, name = ExternalSchemaStoreTypes.SQL),
        @JsonSubTypes.Type(value = ExternalKafkaAttributeStoreInfo.class, name = ExternalSchemaStoreTypes.KAFKA)
})
public interface ExternalAttributeStoreInfo extends Serializable {
    String getType();
    PhysicalStoreType getPhysicalStoreType();
}
