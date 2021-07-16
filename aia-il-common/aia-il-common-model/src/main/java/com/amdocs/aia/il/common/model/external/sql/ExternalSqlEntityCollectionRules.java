package com.amdocs.aia.il.common.model.external.sql;

import com.amdocs.aia.il.common.model.external.AbstractExternalEntityCollectionRules;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;

public class ExternalSqlEntityCollectionRules extends AbstractExternalEntityCollectionRules {
    private static final long serialVersionUID = -4533819294069924465L;

    public ExternalSqlEntityCollectionRules() {
        super(ExternalSchemaStoreTypes.SQL);
    }
}
