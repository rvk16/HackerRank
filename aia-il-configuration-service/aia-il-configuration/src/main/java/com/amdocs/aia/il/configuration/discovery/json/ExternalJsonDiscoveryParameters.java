package com.amdocs.aia.il.configuration.discovery.json;

import com.amdocs.aia.il.configuration.discovery.AbstractExternalModelDiscoveryParameters;
import com.amdocs.aia.il.configuration.discovery.annotations.DiscoveryParameter;

public class ExternalJsonDiscoveryParameters extends AbstractExternalModelDiscoveryParameters {
    public static final String FILE_NAME = "filename";

    @DiscoveryParameter
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
