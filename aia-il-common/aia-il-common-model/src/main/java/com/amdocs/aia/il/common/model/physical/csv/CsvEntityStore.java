package com.amdocs.aia.il.common.model.physical.csv;

import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.physical.AbstractPhysicalEntityStore;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;

/**
 * CSV entity store.
 *
 * @author ALEXKRA
 */
public class CsvEntityStore extends AbstractPhysicalEntityStore {
    private static final long serialVersionUID = 4197031073913227465L;

    public enum FileInvalidNameAction {KEEP, MOVE}

    public static final String ELEMENT_TYPE = getElementTypeFor(CsvEntityStore.class);
    public static final String VALIDATE_REGEX = "validate-regex";
    public static final String DEFAULT_VALUE = "default-value";
    public static final String DATE_FORMAT = ConfigurationConstants.ATTRIBUTE_DATE_FORMAT;

    private String fileNameFormat;
    private boolean isHeader;
    private String dateFormat;
    private char columnDelimiter;
    private FileInvalidNameAction fileInvalidNameAction = FileInvalidNameAction.KEEP;

    public CsvEntityStore() {
        super(ELEMENT_TYPE, PhysicalStoreType.CSV);
    }

    public String getFileNameFormat() {
        return fileNameFormat;
    }

    public void setFileNameFormat(String fileNameFormat) {
        this.fileNameFormat = fileNameFormat;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(final boolean header) {
        isHeader = header;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public char getColumnDelimiter() {
        return columnDelimiter;
    }

    public void setColumnDelimiter(final char columnDelimiter) {
        this.columnDelimiter = columnDelimiter;
    }

    public FileInvalidNameAction getFileInvalidNameAction() {
        return fileInvalidNameAction;
    }

    public void setFileInvalidNameAction(final FileInvalidNameAction fileInvalidNameAction) {
        this.fileInvalidNameAction = fileInvalidNameAction;
    }
}