package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.OracleTypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.PostgreSqlTypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.SqlTypeSystem;

import java.util.Arrays;
import java.util.List;

import static com.amdocs.aia.il.common.model.external.CollectorChannelType.*;

public enum ExternalSchemaType {
    CSV_FILES (
            LogicalTypeSystem.NAME,
            Arrays.asList(LogicalTypeSystem.NAME),
            Arrays.asList(FILES),
            Arrays.asList(FILES, NONE),
            Arrays.asList(FILES, NONE),
            ExternalSchemaStoreTypes.CSV,
            true),
    JSON_OVER_KAFKA (
            LogicalTypeSystem.NAME,
            Arrays.asList(LogicalTypeSystem.NAME),
            Arrays.asList(KAFKA),
            Arrays.asList(NONE, KAFKA, REST_KAFKA),
            Arrays.asList(NONE, KAFKA, REST_KAFKA),
            ExternalSchemaStoreTypes.KAFKA,
            true),
    CATALOG1 (
            LogicalTypeSystem.NAME,
            Arrays.asList(LogicalTypeSystem.NAME),
            Arrays.asList(KAFKA_NEXUS),
            Arrays.asList(REST_KAFKA_NEXUS),
            Arrays.asList(NONE),
            ExternalSchemaStoreTypes.KAFKA,
            true),
    DIGITAL1 (
            LogicalTypeSystem.NAME,
            Arrays.asList(LogicalTypeSystem.NAME),
            Arrays.asList(KAFKA),
            Arrays.asList(REST_KAFKA, KAFKA),
            Arrays.asList(REST_KAFKA, KAFKA),
            ExternalSchemaStoreTypes.KAFKA,
            true),
    ORACLE (
            OracleTypeSystem.NAME,
            Arrays.asList(SqlTypeSystem.NAME),
            Arrays.asList(ATTUNITY, GOLDEN_GATE, EXTERNAL),
            Arrays.asList(SQL, NONE),
            Arrays.asList(SQL, NONE),
            ExternalSchemaStoreTypes.SQL,
            true),
    POSTGRESQL (
            PostgreSqlTypeSystem.NAME,
            Arrays.asList(SqlTypeSystem.NAME),
            Arrays.asList(ATTUNITY, GOLDEN_GATE, EXTERNAL),
            Arrays.asList(SQL, NONE),
            Arrays.asList(SQL, NONE),
            ExternalSchemaStoreTypes.SQL,
            false),
    COUCHBASE (
            LogicalTypeSystem.NAME,
            Arrays.asList(LogicalTypeSystem.NAME),
            Arrays.asList(KAFKA),
            Arrays.asList(NIQL),
            Arrays.asList(NIQL),
            ExternalSchemaStoreTypes.KAFKA,
            false);

    private String nativeDatatype;
    private List<String> supportedTypeSystems;
    private List<CollectorChannelType> supportedOngoingChannels;
    private List<CollectorChannelType> supportedInitialLoadChannels;
    private List<CollectorChannelType> supportedReplayChannels;
    private String storeType;
    private boolean isAuthoringSupported;

    ExternalSchemaType(String nativeDatatype,
                       List<String> supportedTypeSystems,
                       List<CollectorChannelType> supportedOngoingChannels,
                       List<CollectorChannelType> supportedInitialLoadChannels,
                       List<CollectorChannelType> supportedReplayChannels,
                       String storeType,
                       boolean isAuthoringSupported) {
        this.nativeDatatype = nativeDatatype;
        this.supportedTypeSystems = supportedTypeSystems;
        this.supportedOngoingChannels = supportedOngoingChannels;
        this.supportedInitialLoadChannels = supportedInitialLoadChannels;
        this.supportedReplayChannels = supportedReplayChannels;
        this.storeType= storeType;
        this.isAuthoringSupported = isAuthoringSupported;
    }

    public String getNativeDatatype() { return nativeDatatype; }

    public void setNativeDatatype(String nativeDatatype) { this.nativeDatatype = nativeDatatype; }

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
