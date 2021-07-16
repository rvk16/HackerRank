package com.amdocs.aia.il.common.publisher;

public abstract class Publisher {

    /**
     * Publish the results
     * @param trxPublishingData
     */
    protected abstract void publish (TrxPublishingData trxPublishingData);

    /**
     * Commit the results
     * @param trxPublishingInfo
     */
    protected abstract void commit (TrxPublishingInfo trxPublishingInfo);
}
