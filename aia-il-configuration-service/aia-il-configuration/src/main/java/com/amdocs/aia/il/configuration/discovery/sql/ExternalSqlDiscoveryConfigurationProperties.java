package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.logical.Datatype;
import com.amdocs.aia.common.model.logical.PrimitiveDatatype;
import com.amdocs.aia.il.common.model.physical.csv.CsvEntityStore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "aia.il.discovery.sql")
@EnableConfigurationProperties
public class ExternalSqlDiscoveryConfigurationProperties {

    private static final String DEFAULT_FILENAME_FORMAT = ".*\\.csv";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final char DEFAULT_COLUMN_DELIMITER = ',';
    private static final String DEFAULT_DATATYPE = LogicalTypeSystem.format(Datatype.forPrimitive(PrimitiveDatatype.STRING));
    private static final CsvEntityStore.FileInvalidNameAction DEFAULT_INVALID_FILENAME_ACTION= CsvEntityStore.FileInvalidNameAction.KEEP;

    private char defaultColumnDelimiter = DEFAULT_COLUMN_DELIMITER;
    private String defaultDateFormat = DEFAULT_DATE_FORMAT;
    private String defaultFilenameFormat = DEFAULT_FILENAME_FORMAT;
    private String defaultDatatype = DEFAULT_DATATYPE;
    private CsvEntityStore.FileInvalidNameAction defaultInvalidFilenameAction = DEFAULT_INVALID_FILENAME_ACTION;

    public CsvEntityStore.FileInvalidNameAction getDefaultInvalidFilenameAction() {
        return defaultInvalidFilenameAction;
    }

    public void setDefaultInvalidFilenameAction(CsvEntityStore.FileInvalidNameAction defaultInvalidFilenameAction) {
        this.defaultInvalidFilenameAction = defaultInvalidFilenameAction;
    }

    private List<DatatypePattern> attributeDatatypePatterns = new ArrayList<>();

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

    public List<DatatypePattern> getAttributeDatatypePatterns() {
        return attributeDatatypePatterns;
    }

    public void setAttributeDatatypePatterns(List<DatatypePattern> attributeDatatypePatterns) {
        this.attributeDatatypePatterns = attributeDatatypePatterns;
    }

    public String getDefaultFilenameFormat() {
        return defaultFilenameFormat;
    }

    public void setDefaultFilenameFormat(String defaultFilenameFormat) {
        this.defaultFilenameFormat = defaultFilenameFormat;
    }

    public String getDefaultDatatype() {
        return defaultDatatype;
    }

    public void setDefaultDatatype(String defaultDatatype) {
        this.defaultDatatype = defaultDatatype;
    }

    public static class DatatypePattern {
        private String pattern;
        private String datatype;

        public DatatypePattern() {
        }

        public DatatypePattern(String pattern, String datatype) {
            this.pattern = pattern;
            this.datatype = datatype;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getDatatype() {
            return datatype;
        }

        public void setDatatype(String datatype) {
            this.datatype = datatype;
        }
    }
}
