package com.amdocs.aia.il.common.model.configuration.transformation;

/**
 * Created by SWARNIMJ
 */
public enum NoReferentAction {
    MANDATORY("MANDATORY"),
    OPTIONAL("OPTIONAL"),
    MANDATORY_PUBLISH("MANDATORY_PUBLISH");

    public final String value;


    NoReferentAction(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static NoReferentAction fromValue(String text) {
        for (NoReferentAction b : NoReferentAction.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
