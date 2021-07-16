package com.amdocs.aia.il.common.model.external;

import java.io.Serializable;

public class ExternalAttributeIncrementalAttribute implements Serializable {
    private static final long serialVersionUID = -8331913171798362610L;

    public enum Type {SEQUENCE, TIMESTAMP}

    private String key;
    private Type type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
