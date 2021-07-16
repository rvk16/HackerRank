package com.amdocs.aia.il.configuration.discovery.json;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

class ProcessedAttributeInfo {
    private String attributeKey;
    private String datatype;
    private String title;
    private String description;
    private Set<String> jsonPaths = new LinkedHashSet<>();

    public ProcessedAttributeInfo(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getAttributeKey() {
        return attributeKey;
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

    public Set<String> getJsonPaths() {
        return jsonPaths;
    }

    public boolean addJsonPathIfNotExists(String relativePath) {
        return jsonPaths.add(relativePath);
    }

    public String getFormattedJsonPath() {
        return jsonPaths.isEmpty() ?
                null :
                jsonPaths.stream().collect(Collectors.joining(","));
    }
}
