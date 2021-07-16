package com.amdocs.aia.il.configuration.discovery;

import com.amdocs.aia.il.common.model.external.Availability;
import com.amdocs.aia.il.configuration.discovery.annotations.DiscoveryParameter;

public class AbstractExternalModelDiscoveryParameters implements ExternalModelDiscoveryParameters {
    public static final String AVAILABILITY = "availability";

    @DiscoveryParameter(required = false)
    private Boolean referenceSchema;

    @DiscoveryParameter(required = false)
    private Availability availability;

    @DiscoveryParameter(required = false)
    private String subjectAreaName;

    @DiscoveryParameter(required = false)
    private String subjectAreaKey;

    public Boolean getReferenceSchema() {
        return referenceSchema;
    }

    public void setReferenceSchema(Boolean referenceSchema) {
        this.referenceSchema = referenceSchema;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public String getSubjectAreaName() {
        return subjectAreaName;
    }

    public void setSubjectAreaName(String subjectAreaName) {
        this.subjectAreaName = subjectAreaName;
    }

    public String getSubjectAreaKey() {
        return subjectAreaKey;
    }

    public void setSubjectAreaKey(String subjectAreaKey) {
        this.subjectAreaKey = subjectAreaKey;
    }
}
