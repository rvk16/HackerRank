package com.amdocs.aia.il.common.model.external.csv;

import com.amdocs.aia.il.common.model.external.AbstractExternalSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;
import com.amdocs.aia.il.common.model.physical.csv.CsvEntityStore;

public class ExternalCsvSchemaCollectionRules extends AbstractExternalSchemaCollectionRules {
    private static final long serialVersionUID = 1682019287074633140L;
    private CsvEntityStore.FileInvalidNameAction defaultInvalidFilenameAction = CsvEntityStore.FileInvalidNameAction.KEEP;


    public CsvEntityStore.FileInvalidNameAction getDefaultInvalidFilenameAction() {
        return defaultInvalidFilenameAction;
    }

    public void setDefaultInvalidFilenameAction(CsvEntityStore.FileInvalidNameAction defaultInvalidFilenameAction) {
        this.defaultInvalidFilenameAction = defaultInvalidFilenameAction;
    }

    public ExternalCsvSchemaCollectionRules() {
        super(ExternalSchemaStoreTypes.CSV);
    }
}
