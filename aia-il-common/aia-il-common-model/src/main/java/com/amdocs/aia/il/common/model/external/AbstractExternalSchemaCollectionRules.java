package com.amdocs.aia.il.common.model.external;

public abstract class AbstractExternalSchemaCollectionRules implements ExternalSchemaCollectionRules {
    private static final long serialVersionUID = 6129312947460270780L;

    private final String type;
    private CollectorChannelType ongoingChannel;
    private CollectorChannelType initialLoadChannel;
    private CollectorChannelType replayChannel;
    private String initialLoadRelativeURL;
    private String partialLoadRelativeURL;

    protected AbstractExternalSchemaCollectionRules(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public CollectorChannelType getOngoingChannel() {
        return ongoingChannel;
    }

    @Override
    public void setOngoingChannel(CollectorChannelType ongoingChannelType) {
        this.ongoingChannel = ongoingChannelType;
    }

    @Override
    public CollectorChannelType getInitialLoadChannel() {
        return initialLoadChannel;
    }

    @Override
    public void setInitialLoadChannel(CollectorChannelType initialLoadChannelType) {
        this.initialLoadChannel = initialLoadChannelType;
    }

    @Override
    public CollectorChannelType getReplayChannel() {
        return replayChannel;
    }

    @Override
    public void setReplayChannel(CollectorChannelType replayChannelType) {
        this.replayChannel = replayChannelType;
    }

    @Override
    public String getInitialLoadRelativeURL() { return initialLoadRelativeURL; }

    @Override
    public void setInitialLoadRelativeURL(String initialLoadRelativeURL) { this.initialLoadRelativeURL = initialLoadRelativeURL; }

    @Override
    public String getPartialLoadRelativeURL() { return partialLoadRelativeURL; }

    @Override
    public void setPartialLoadRelativeURL(String partialLoadRelativeURL) { this.partialLoadRelativeURL = partialLoadRelativeURL; }
}
