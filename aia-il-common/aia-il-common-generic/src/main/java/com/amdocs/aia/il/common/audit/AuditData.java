package com.amdocs.aia.il.common.audit;

import java.util.concurrent.atomic.AtomicLong;

public class AuditData {
    private String serviceName;
    private String correlationID;
    private String entity;
    private AtomicLong recordsLoaded;
    private AtomicLong recordsMerged;
    private AtomicLong recordsStored;
    private AtomicLong recordsDeleted;
    private AtomicLong recordsNoChange;
    private AtomicLong recordsError;
    private AtomicLong recordsDropped;
    private String correlatingEntity;
    private boolean isLeadingTable;
    private long auditTime;
    private long auditGenerated;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public AtomicLong getRecordsLoaded() {
        if(recordsLoaded==null)
            return new AtomicLong(0);
        return recordsLoaded;
    }

    public void setRecordsLoaded(AtomicLong recordsLoaded) {
        this.recordsLoaded = recordsLoaded;
    }

    public AtomicLong getRecordsMerged() {
        if(recordsMerged==null){
            return new AtomicLong(0);
        }
        return recordsMerged;
    }

    public void setRecordsMerged(AtomicLong recordsMerged) {
        this.recordsMerged = recordsMerged;
    }

    public AtomicLong getRecordsDropped() {
        if(recordsDropped==null){
            return new AtomicLong(0);
        }
        return recordsDropped;
    }

    public void setRecordsDropped(AtomicLong recordsDropped) {
        this.recordsDropped = recordsDropped;
    }

    public AtomicLong getRecordsStored() {
        if(recordsStored==null){
            return new AtomicLong(0);
        }
        return recordsStored;
    }

    public void setRecordsStored(AtomicLong recordsStored) {
        this.recordsStored = recordsStored;
    }

    public AtomicLong getRecordsDeleted() {
        if(recordsDeleted==null){
            return new AtomicLong(0);
        }
        return recordsDeleted;
    }

    public void setRecordsDeleted(AtomicLong recordsDeleted) {
        this.recordsDeleted = recordsDeleted;
    }

    public AtomicLong getRecordsNoChange() {
        if(recordsNoChange==null){
            return new AtomicLong(0);
        }
        return  recordsNoChange;
    }

    public void setRecordsNoChange(AtomicLong recordsNoChange) {
        this.recordsNoChange = recordsNoChange;
    }

    public AtomicLong getRecordsError() {
        if(recordsError==null){
            return new AtomicLong(0);
        }
        return recordsError;
    }

    public void setRecordsError(AtomicLong recordsError) {
        this.recordsError = recordsError;
    }

    public String getCorrelatingEntity() {
        return correlatingEntity;
    }

    public void setCorrelatingEntity(String correlatingEntity) {
        this.correlatingEntity = correlatingEntity;
    }

    public boolean getIsLeadingTable() {
        return isLeadingTable;
    }

    public void setIsLeadingTable(boolean isLeadingTable) {
        this.isLeadingTable = isLeadingTable;
    }

    public long getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(long auditTime) {
        this.auditTime = auditTime;
    }

    public long getAuditGenerated() {
        return auditGenerated;
    }

    public void setAuditGenerated(long auditGenerated) {
        this.auditGenerated = auditGenerated;
    }

    @Override
    public String toString() {
        return "AuditData{" +
                "\nserviceName='" + serviceName + '\'' +
                "\ncorrelationID='" + correlationID + '\'' +
                "\nentity='" + entity + '\'' +
                "\nrecordLoaded=" + recordsLoaded +
                "\nrecordMerged=" + recordsMerged +
                "\nrecordStored=" + recordsStored +
                "\nrecordDeleted=" + recordsDeleted +
                "\nrecordNoChange=" + recordsNoChange +
                "\nrecordError=" + recordsError +
                "\nrecordDropped=" + recordsDropped +
                "\ncorrelatingEntity=" + correlatingEntity +
                "\nisLeadingTable=" + isLeadingTable +
                "\nauditTime=" + auditTime +
                "\nauditGenerated=" + auditGenerated +
                '}';
    }
}
