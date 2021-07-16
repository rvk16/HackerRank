package com.amdocs.aia.il.common.publisher;

import java.io.Serializable;

public class TrxPublishingData implements Serializable {

    private static final long serialVersionUID = 2049626302226637108L;

    protected final TrxPublishingInfo trxPublishingInfo;

    public TrxPublishingData (String txnId, long currentBatchTime) {
        this.trxPublishingInfo = new TrxPublishingInfo(txnId, 0, 0, currentBatchTime);
    }

    public TrxPublishingData (String txnId, long updateTime, long queryTime, long currentBatchTime) {
        this.trxPublishingInfo = new TrxPublishingInfo(txnId, updateTime, queryTime, currentBatchTime);
    }

    public TrxPublishingInfo getTrxPublishingInfo () { return trxPublishingInfo; }
}
