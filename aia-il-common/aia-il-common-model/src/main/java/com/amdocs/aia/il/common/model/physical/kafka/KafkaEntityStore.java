package com.amdocs.aia.il.common.model.physical.kafka;

import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.physical.AbstractPhysicalEntityStore;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;

public class KafkaEntityStore extends AbstractPhysicalEntityStore {
    private static final long serialVersionUID = 1123763165147820933L;

    public static final String ELEMENT_TYPE = getElementTypeFor(KafkaEntityStore.class);
    public static final String VALIDATE_REGEX = "validate-regex";
    public static final String DEFAULT_VALUE = "default-value";
    public static final String NAME = "name";
    public static final String DATE_FORMAT = ConfigurationConstants.ATTRIBUTE_DATE_FORMAT;
    public static final String JSON_PATH = "json-path";
    public static final String IS_MANDATORY = "mandatory";

    private String jsonTypeValue;
    private String jsonTypePath;
    private String relativePaths;
    private String mergedNodes;

    public KafkaEntityStore() {
        super(ELEMENT_TYPE, PhysicalStoreType.KAFKA);
    }

    public static String getValidateRegex() {
        return VALIDATE_REGEX;
    }

    public static String getDefaultValue() {
        return DEFAULT_VALUE;
    }

    public static String getNAME() {
        return NAME;
    }

    public static String getDateFormat() {
        return DATE_FORMAT;
    }

    public static String getJsonPath() {
        return JSON_PATH;
    }

    public static String getIsMandatory() {
        return IS_MANDATORY;
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
