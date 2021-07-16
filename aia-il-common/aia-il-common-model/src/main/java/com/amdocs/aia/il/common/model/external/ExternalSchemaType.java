package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.OracleTypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.PostgreSqlTypeSystem;

import java.util.Arrays;
import java.util.List;

import static com.amdocs.aia.il.common.model.external.CollectorChannelType.*;

public enum ExternalSchemaType {
    CSV_FILES (
            Arrays.asList(LogicalTypeSystem.NAME),
            Arrays.asList(FILES),
            Arrays.asList(FILES, NONE),
            Arrays.asList(FILES, NONE),
            ExternalSchemaStoreTypes.CSV,
            true),
    JSON_OVER_KAFKA (
            Arrays.asList(LogicalTypeSystem.NAME),
            Arrays.asList(KAFKA),
            Arrays.asList(NONE, KAFKA, REST_KAFKA),
            Arrays.asList(NONE, KAFKA, REST_KAFKA),
            ExternalSchemaStoreTypes.KAFKA,
            true),
    CATALOG1 (
            Arrays.asList(LogicalTypeSystem.NAME),
            Arrays.asList(KAFKA_NEXUS),
            Arrays.asList(REST_KAFKA_NEXUS),
            Arrays.asList(NONE),
            ExternalSchemaStoreTypes.KAFKA,
            true),
    DIGITAL1 (
            Arrays.asList(LogicalTypeSystem.NAME),
            Arrays.asList(KAFKA),
            Arrays.asList(REST_KAFKA, KAFKA),
            Arrays.asList(REST_KAFKA, KAFKA),
            ExternalSchemaStoreTypes.KAFKA,
            true),
    ORACLE (
            Arrays.asList(OracleTypeSystem.NAME),
            Arrays.asList(ATTUNITY, GOLDEN_GATE, EXTERNAL),
            Arrays.asList(SQL, NONE),
            Arrays.asList(SQL, NONE),
            ExternalSchemaStoreTypes.SQL,
            true),
    POSTGRESQL (
            Arrays.asList(PostgreSqlTypeSystem.NAME),
            Arrays.asList(ATTUNITY, GOLDEN_GATE, EXTERNAL),
            Arrays.asList(SQL, NONE),
            Arrays.asList(SQL, NONE),
            ExternalSchemaStoreTypes.SQL,
            false),
    COUCHBASE (
            Arrays.asList(LogicalTypeSystem.NAME),
            Arrays.asList(KAFKA),
            Arrays.asList(NIQL),
            Arrays.asList(NIQL),
            ExternalSchemaStoreTypes.KAFKA,
            false);

    private List<String> supportedTypeSystems;
    private List<CollectorChannelType> supportedOngoingChannels;
    private List<CollectorChannelType> supportedInitialLoadChannels;
    private List<CollectorChannelType> supportedReplayChannels;
    private String storeType;
    private boolean isAuthoringSupported;

    ExternalSchemaType(List<String> supportedTypeSystems,
                       List<CollectorChannelType> supportedOngoingChannels,
                       List<CollectorChannelType> supportedInitialLoadChannels,
                       List<CollectorChannelType> supportedReplayChannels,
                       String storeType,
                       boolean isAuthoringSupported) {
        this.supportedTypeSystems = supportedTypeSystems;
        this.supportedOngoingChannels = supportedOngoingChannels;
        this.supportedInitialLoadChannels = supportedInitialLoadChannels;
        this.supportedReplayChannels = supportedReplayChannels;
        this.storeType= storeType;
        this.isAuthoringSupported = isAuthoringSupported;
    }

    public List<String> getSupportedTypeSystems() {
        return supportedTypeSystems;
    }

    public List<CollectorChannelType> getSupportedOngoingChannels() {
        return supportedOngoingChannels;
    }

    public List<CollectorChannelType> getSupportedInitialLoadChannels() {
        return supportedInitialLoadChannels;
    }

    public List<CollectorChannelType> getSupportedReplayChannels() {
        return supportedReplayChannels;
    }

    public String getStoreType() { return storeType; }

    public boolean isAuthoringSupported() {
        return isAuthoringSupported;
    }
}
