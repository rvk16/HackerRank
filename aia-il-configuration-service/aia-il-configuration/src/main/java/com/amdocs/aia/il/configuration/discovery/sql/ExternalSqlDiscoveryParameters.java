package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.il.configuration.discovery.AbstractExternalModelDiscoveryParameters;
import com.amdocs.aia.il.configuration.discovery.annotations.DiscoveryParameter;

import java.util.List;

public class ExternalSqlDiscoveryParameters extends AbstractExternalModelDiscoveryParameters {

    public static final String FILE_NAMES = "filenames";
    public static final String COLUMN_DELIMITER = "columnDelimiter";
    public static final String DATE_FORMAT = "dateFormat";

    @DiscoveryParameter
    private List<String> filenames;

    @DiscoveryParameter(required = false)
    private Character columnDelimiter;

    @DiscoveryParameter(required = false)
    private String dateFormat;

    public List<String> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    public Character getColumnDelimiter() {
        return columnDelimiter;
    }

    public void setColumnDelimiter(Character columnDelimiter) {
        this.columnDelimiter = columnDelimiter;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
