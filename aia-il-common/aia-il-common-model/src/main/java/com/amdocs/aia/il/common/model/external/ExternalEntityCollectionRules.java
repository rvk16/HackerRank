package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.il.common.model.external.csv.ExternalCsvEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlEntityCollectionRules;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExternalCsvEntityCollectionRules.class, name = ExternalSchemaStoreTypes.CSV),
        @JsonSubTypes.Type(value = ExternalSqlEntityCollectionRules.class, name = ExternalSchemaStoreTypes.SQL),
        @JsonSubTypes.Type(value = ExternalKafkaEntityCollectionRules.class, name = ExternalSchemaStoreTypes.KAFKA)
})
public interface ExternalEntityCollectionRules extends Serializable {
    String getType();

    List<ExternalEntityFilter> getFilters();

    void setFilters(List<ExternalEntityFilter> filters);

    ExternalEntityFilter getDefaultFilter();

    void setDefaultFilter(ExternalEntityFilter defaultFilter);

    ExternalAttributeIncrementalAttribute getIncrementalAttribute();

    void setIncrementalAttribute(ExternalAttributeIncrementalAttribute incrementalAttribute);
}
