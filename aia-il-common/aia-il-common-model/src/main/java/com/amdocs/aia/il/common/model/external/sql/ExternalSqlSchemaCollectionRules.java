package com.amdocs.aia.il.common.model.external.sql;

import com.amdocs.aia.il.common.model.external.AbstractExternalSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;

public class ExternalSqlSchemaCollectionRules extends AbstractExternalSchemaCollectionRules {
    private static final long serialVersionUID = -6538402121921442515L;

    public ExternalSqlSchemaCollectionRules() {
        super(ExternalSchemaStoreTypes.SQL);
    }
}
