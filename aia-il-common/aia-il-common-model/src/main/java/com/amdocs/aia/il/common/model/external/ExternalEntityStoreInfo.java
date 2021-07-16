package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.il.common.model.external.csv.ExternalCsvEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlEntityStoreInfo;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExternalCsvEntityStoreInfo.class, name = ExternalSchemaStoreTypes.CSV),
        @JsonSubTypes.Type(value = ExternalSqlEntityStoreInfo.class, name = ExternalSchemaStoreTypes.SQL),
        @JsonSubTypes.Type(value = ExternalKafkaEntityStoreInfo.class, name = ExternalSchemaStoreTypes.KAFKA)
})
public interface ExternalEntityStoreInfo extends Serializable {
    String getType();
    PhysicalStoreType getPhysicalStoreType();
}
