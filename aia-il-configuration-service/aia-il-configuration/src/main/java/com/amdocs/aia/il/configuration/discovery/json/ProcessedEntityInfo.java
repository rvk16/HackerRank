package com.amdocs.aia.il.configuration.discovery.json;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

class ProcessedEntityInfo {
    private String entityKey;
    private String title;
    private String description;
    private String finalEntityKey; // can include a prefix that is preprended to the entity key

    private Set<String> relativePaths = new LinkedHashSet<>();
    private Set<String> mergedNodes = new LinkedHashSet<>();
    private Map<String, ProcessedAttributeInfo> processedAttributes = new LinkedHashMap<>();

    public ProcessedEntityInfo(String entityKey) {
        this.entityKey = entityKey;
        this.finalEntityKey = entityKey;
    }

    public String getFinalEntityKey() {
        return finalEntityKey;
    }

    public void setFinalEntityKey(String finalEntityKey) {
        this.finalEntityKey = finalEntityKey;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getRelativePaths() {
        return relativePaths;
    }

    public Set<String> getMergedNodes() {
        return mergedNodes;
    }

    public boolean addRelativePathIfNotExists(String relativePath) {
        return relativePaths.add(relativePath);
    }

    public boolean addMergedNodeIfNotExists(String mergedNode) {
        return mergedNodes.add(mergedNode);
    }

    public ProcessedAttributeInfo getOrCreateProcessedAttribute(String attributeKey) {
        return processedAttributes.computeIfAbsent(attributeKey, this::createAttributeInfo);
    }

    private ProcessedAttributeInfo createAttributeInfo(String attributeKey) {
        ProcessedAttributeInfo attributeInfo = new ProcessedAttributeInfo(attributeKey);
        return attributeInfo;
    }

    public ProcessedAttributeInfo getProcessedAttribute(String attributeKey) {
        return processedAttributes.get(attributeKey);
    }

    public Map<String, ProcessedAttributeInfo> getProcessedAttributes() {
        return processedAttributes;
    }
}
