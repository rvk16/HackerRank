package com.amdocs.aia.il.common.model.external.csv;

import com.amdocs.aia.il.common.model.external.AbstractExternalSchemaStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;

public class ExternalCsvSchemaStoreInfo extends AbstractExternalSchemaStoreInfo {

    private static final long serialVersionUID = -3994040437258008885L;
    private String defaultDateFormat;
    private char defaultColumnDelimiter;


    public char getDefaultColumnDelimiter() {
        return defaultColumnDelimiter;
    }

    public void setDefaultColumnDelimiter(char defaultColumnDelimiter) {
        this.defaultColumnDelimiter = defaultColumnDelimiter;
    }


    public String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    public void setDefaultDateFormat(String defaultDateFormat) {
        this.defaultDateFormat = defaultDateFormat;
    }


    public ExternalCsvSchemaStoreInfo() {
        super(ExternalSchemaStoreTypes.CSV);
    }
}
