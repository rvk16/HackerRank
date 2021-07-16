package com.amdocs.aia.il.common.model.external.csv;

import com.amdocs.aia.il.common.model.external.AbstractExternalEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.physical.csv.CsvEntityStore;

public class ExternalCsvEntityCollectionRules extends AbstractExternalEntityCollectionRules {

    private static final long serialVersionUID = -7438893144442292998L;
    private CsvEntityStore.FileInvalidNameAction fileInvalidNameAction = CsvEntityStore.FileInvalidNameAction.KEEP;

    public ExternalCsvEntityCollectionRules() {
        super(ExternalSchemaStoreTypes.CSV);
    }

    public CsvEntityStore.FileInvalidNameAction getFileInvalidNameAction() {
        return fileInvalidNameAction;
    }

    public void setFileInvalidNameAction(CsvEntityStore.FileInvalidNameAction fileInvalidNameAction) {
        this.fileInvalidNameAction = fileInvalidNameAction;
    }
}
