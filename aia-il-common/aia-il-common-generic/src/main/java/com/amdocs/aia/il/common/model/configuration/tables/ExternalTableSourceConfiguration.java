package com.amdocs.aia.il.common.model.configuration.tables;

public class ExternalTableSourceConfiguration extends AbstractTableSourceConfiguration {
    private static final long serialVersionUID = -7052352069413486563L;

    public static final String TYPE = "EXTERNAL";

    public ExternalTableSourceConfiguration() {
        super(TYPE);
    }
}