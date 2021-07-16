package com.amdocs.aia.il.common.model.configuration.transformation;

/**
 * Created by SWARNIMJ
 */
public enum ContextEntityRelationType {
    LEAD("LEAD"),

    OTO("OTO"),

    OTM("OTM"),

    MTO("MTO"),

    MTM("MTM"),

    REF("REF");

    private String value;

    ContextEntityRelationType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static ContextEntityRelationType fromValue(String text) {
        for (ContextEntityRelationType b : ContextEntityRelationType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}