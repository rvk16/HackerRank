package com.amdocs.aia.il.common.model.external.csv;

import com.amdocs.aia.il.common.model.external.AbstractExternalEntityStoreInfo;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import com.amdocs.aia.il.common.model.physical.csv.CsvEntityStore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"fileInvalidNameAction"}) // moved to ExternalCsvEntityCollectionRules
public class ExternalCsvEntityStoreInfo extends AbstractExternalEntityStoreInfo {

    private static final long serialVersionUID = -4445703636686768171L;

    private String fileNameFormat;
    private boolean isHeader;
    private String dateFormat;
    private char columnDelimiter;

    public ExternalCsvEntityStoreInfo() {
        super(ExternalSchemaStoreTypes.CSV);
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

    public void setHeader(boolean header) {
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

    public void setColumnDelimiter(char columnDelimiter) {
        this.columnDelimiter = columnDelimiter;
    }
}
