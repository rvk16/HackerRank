package com.amdocs.aia.il.common.audit;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PartitionHandler {

    private final Integer partitionId;
    private boolean isMessageEmpty = true;


    private final List<JSONObject> auditDataList = new ArrayList<>();

    public PartitionHandler(Integer partitionId) {
        this.partitionId = partitionId;
    }



    public Integer getPartitionId() {
        return partitionId;
    }

    public boolean isMessageEmpty() {
        return isMessageEmpty;
    }

    public List<JSONObject> getAuditDataList() {
        return auditDataList;
    }

    public void addMessages(JSONObject auditData) {
        this.auditDataList.add(auditData);
        isMessageEmpty = false;
    }


    public void clear() {
        this.isMessageEmpty = true;

    }
}
