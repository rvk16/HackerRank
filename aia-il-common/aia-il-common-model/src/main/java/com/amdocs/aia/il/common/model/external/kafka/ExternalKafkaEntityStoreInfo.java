package com.amdocs.aia.il.common.model.external.kafka;

import com.amdocs.aia.il.common.model.external.AbstractExternalEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExternalKafkaEntityStoreInfo extends AbstractExternalEntityStoreInfo {

    private static final long serialVersionUID = 5141361101916066078L;

    private String jsonTypeValue;
    private String jsonTypePath;
    private String relativePaths;
    private String mergedNodes;

    public ExternalKafkaEntityStoreInfo() {
        super(ExternalSchemaStoreTypes.KAFKA);
    }

    public String getJsonTypeValue() {
        return jsonTypeValue;
    }

    public void setJsonTypeValue(String jsonTypeValue) {
        this.jsonTypeValue = jsonTypeValue;
    }

    public String getJsonTypePath() {
        return jsonTypePath;
    }

    public void setJsonTypePath(String jsonTypePath) {
        this.jsonTypePath = jsonTypePath;
    }

    public String getRelativePaths() {
        return relativePaths;
    }

    public void setRelativePaths(String relativePaths) {
        this.relativePaths = relativePaths;
    }

    public String getMergedNodes() {
        return mergedNodes;
    }

    public void setMergedNodes(String mergedNodes) {
        this.mergedNodes = mergedNodes;
    }
}
