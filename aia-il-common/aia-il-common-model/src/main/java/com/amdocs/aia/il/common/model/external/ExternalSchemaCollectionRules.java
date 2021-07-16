package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.il.common.model.external.csv.ExternalCsvSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.sql.ExternalSqlSchemaCollectionRules;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExternalCsvSchemaCollectionRules.class, name = ExternalSchemaStoreTypes.CSV),
        @JsonSubTypes.Type(value = ExternalSqlSchemaCollectionRules.class, name = ExternalSchemaStoreTypes.SQL),
        @JsonSubTypes.Type(value = ExternalKafkaSchemaCollectionRules.class, name = ExternalSchemaStoreTypes.KAFKA)
})
public interface ExternalSchemaCollectionRules extends Serializable {
    String getType();
    CollectorChannelType getOngoingChannel();
    void setOngoingChannel(CollectorChannelType channelType);
    CollectorChannelType getInitialLoadChannel();
    void setInitialLoadChannel(CollectorChannelType channelType);
    CollectorChannelType getReplayChannel();
    void setReplayChannel(CollectorChannelType channelType);
    String getInitialLoadRelativeURL();
    void setInitialLoadRelativeURL(String initialLoadRelativeURL);
    String getPartialLoadRelativeURL();
    void setPartialLoadRelativeURL(String partialLoadRelativeURL);
}
