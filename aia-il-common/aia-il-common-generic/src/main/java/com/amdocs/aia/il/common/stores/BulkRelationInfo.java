package com.amdocs.aia.il.common.stores;


import com.amdocs.aia.il.common.stores.RelationKeyInfo;

public class BulkRelationInfo {
    private RelationKeyInfo relationKeyInfo;
    private String randomAccessTableName;
    private String mainTable;
    private String contextName;
    private String relType;
    private String relationTable;
    private Long batchProcessingTimestamp;

    public BulkRelationInfo(RelationKeyInfo relationKeyInfo, String randomAccessTableName, String mainTable, String contextName, String relType, String relationTable, Long batchProcessingTimestamp) {
        this.relationKeyInfo = relationKeyInfo;
        this.randomAccessTableName = randomAccessTableName;
        this.mainTable = mainTable;
        this.contextName = contextName;
        this.relType = relType;
        this.relationTable = relationTable;
        this.batchProcessingTimestamp = batchProcessingTimestamp;
    }

    public RelationKeyInfo getRelationKeyInfo() {
        return relationKeyInfo;
    }

    public String getRandomAccessTableName() {
        return randomAccessTableName;
    }

    public String getMainTable() {
        return mainTable;
    }

    public String getContextName() {
        return contextName;
    }

    public String getRelType() {
        return relType;
    }

    public String getRelationTable() {
        return relationTable;
    }

    public Long getBatchProcessingTimestamp() {
        return batchProcessingTimestamp;
    }
}
