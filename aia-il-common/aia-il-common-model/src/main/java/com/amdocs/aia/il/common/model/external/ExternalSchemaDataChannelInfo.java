package com.amdocs.aia.il.common.model.external;

public class ExternalSchemaDataChannelInfo {
    private String dataChannelName;
    private String serializationMethod; // valid values taken from com.amdocs.aia.common.model.store.SharedSerializations

    public String getDataChannelName() {
        return dataChannelName;
    }

    public void setDataChannelName(String dataChannelName) {
        this.dataChannelName = dataChannelName;
    }

    public String getSerializationMethod() {
        return serializationMethod;
    }

    public void setSerializationMethod(String serializationMethod) {
        this.serializationMethod = serializationMethod;
    }
}
