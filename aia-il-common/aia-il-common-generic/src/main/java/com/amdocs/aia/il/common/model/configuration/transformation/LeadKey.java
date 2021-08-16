package com.amdocs.aia.il.common.model.configuration.transformation;

import java.io.Serializable;

public class LeadKey implements Serializable {
    private static final long serialVersionUID = 8744431369689431982L;
    String sourceAttribute;
    String targetAttribute;


    public String getSourceAttribute() {
        return sourceAttribute;
    }

    public void setSourceAttribute(String sourceAttribute) {
        this.sourceAttribute = sourceAttribute;
    }

    public String getTargetAttribute() {
        return targetAttribute;
    }

    public void setTargetAttribute(String targetAttribute) {
        this.targetAttribute = targetAttribute;
    }

}
